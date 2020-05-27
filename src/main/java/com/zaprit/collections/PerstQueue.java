package com.zaprit.collections;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.zaprit.validation.FileUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
public class PerstQueue<V extends Externalizable> implements AbstractPersistentQueue<V>
{
	private static final int	DEFAULT_BUFFER_SIZE			= 10 * 1024 * 1024;
	private static final long	POLLING_TIME_FOR_ACTIVITY	= 5000L;
	private static final String	META_DATA_FILE				= ".metadata";
	private static final String	TMP_SUFFIX					= ".tmp.";

	private final String					dir;
	private final int						bufferSize;
	private final boolean					readOnly;
	private final PerstQueueSerializer<V>	serializer;
	protected final ReentrantLock			lock	= new ReentrantLock();
	private final Condition					empty	= lock.newCondition();

	private LinkedList<String>	fileList;
	private long				enQueueBufferIndex, deQueueBufferIndex;
	private Buffer				enQueueBuffer	= null, deQueueBuffer = null;

	private AtomicBoolean	objectEnqueued	= new AtomicBoolean(false);
	private AtomicBoolean	objectDequeued	= new AtomicBoolean(false);
	private AtomicInteger	numInQueue		= new AtomicInteger(0);

	private volatile Thread persistThread;

	private volatile boolean open = false;

	public PerstQueue(String dir, int bufferSize, boolean readOnly, PerstQueueSerializer<V> serializer)
	{
		if (bufferSize < 0)
			throw new IllegalArgumentException("Buffer Size cannot be negative " + bufferSize);
		this.readOnly = readOnly;
		this.bufferSize = bufferSize;
		this.dir = dir;
		this.serializer = serializer;
		open();
	}

	public PerstQueue(String dir, int bufferSize, boolean readOnly)
	{
		this(dir, bufferSize, readOnly, new DefaultPerstQueueSerializer<V>());
	}

	public PerstQueue(String dir, int bufferSize)
	{
		this(dir, bufferSize, false);
	}

	public PerstQueue(String dir, boolean readOnly)
	{
		this(dir, DEFAULT_BUFFER_SIZE, readOnly);
	}

	public PerstQueue(String dir)
	{
		this(dir, DEFAULT_BUFFER_SIZE, false);
	}

	@Override
	public void put(V object) throws InterruptedException
	{
		// same behaviour as add
		add(object);
	}

	@Override
	public V take() throws InterruptedException
	{
		V obj = null;
		lock.lockInterruptibly();
		try
		{
			obj = internalRemoveObj();

			if (obj != null)
				return obj;

			try
			{
				while (numInQueue.get() == 0)
					empty.await();
			}
			catch (InterruptedException ie)
			{
				empty.signal(); // propagate to a non-interrupted thread
				throw ie;
			}

			obj = internalRemoveObj();

			if (numInQueue.get() > 0)
				empty.signal();
		}
		catch (InterruptedException ie)
		{
			throw ie;
		}
		catch (Exception e)
		{
			log.error("Error while taking writable from persistent queue[" + dir + "]", e);
		}
		finally
		{
			lock.unlock();
		}
		return obj;
	}

	@Override
	public boolean add(V object)
	{
		if (object == null)
			return false;
		byte[] objectData = null;
		try
		{
			objectData = serializer.objectToBytes(object);
		}
		catch (Exception e)
		{
			log.error("Error while adding writable to persistent queue[" + dir + "]", e);
			return true;
		}
		lock.lock();
		try
		{
			if (!open)
				return false;
			if (readOnly)
				throw new IOException("In read only mode");

			int counter = 0;
			while (!enQueueBuffer.canWrite())
			{
				nextEnqueueBuffer();
				if (counter++ > 10)
					throw new IOException("Unable to create buffers correctly");
			}

			// add the object in buffer
			enQueueBuffer.writeData(objectData);

			// increase the size
			numInQueue.incrementAndGet();

			// signal any take's to continue
			empty.signal();

			// the persister thread can now save changes
			objectEnqueued.set(true);
		}
		catch (Exception e)
		{
			log.error("Error while adding writable to persistent queue[" + dir + "]", e);
		}
		finally
		{
			lock.unlock();
		}
		return true;
	}

	@Override
	public void clear()
	{
		lock.lock();
		try
		{
			if (!open)
				return;
			// delete all the files
			String file = null;
			while ((file = fileList.poll()) != null)
				new File(getFileName(Long.parseLong(file))).delete();

			// reset everything
			numInQueue.set(0);
			deQueueBufferIndex = 0;
			enQueueBufferIndex = deQueueBufferIndex;
			fileList.offer(Long.toString(enQueueBufferIndex));
			deQueueBuffer = loadBuffer(deQueueBufferIndex);
			enQueueBuffer = loadBuffer(enQueueBufferIndex);
			flushMetaData();
		}
		catch (Exception e)
		{
			log.error("Error while clearing persistent queue[" + dir + "]", e);
		}
		finally
		{
			lock.unlock();
		}
	}

	@Override
	public V poll()
	{
		byte[] objectData = null;
		lock.lock();
		try
		{
			if (!open)
				return null;
			objectData = internalRemove(true);
		}
		catch (Exception e)
		{
			log.error("Error while polling writable from persistent queue[" + dir + "]", e);
		}
		finally
		{
			lock.unlock();
		}
		try
		{
			return objectData == null ? null : serializer.bytesToObject(objectData);
		}
		catch (Exception e)
		{
			log.error("Error while polling writable from persistent queue[" + dir + "]", e);
		}
		return null;
	}

	@Override
	public int size()
	{
		return numInQueue.get();
	}

	@Override
	public void open()
	{
		lock.lock();
		try
		{
			if (open)
				return;
			File dirFile = new File(dir);
			if (!dirFile.exists())
			{
				boolean success = (new File(dir)).mkdirs();
				if (!success)
				{
					throw new FileNotFoundException("The directory: " + dir + " does not exist and cannot be created");
				}
			}
			fileList = new LinkedList<String>();

			String[] files = dirFile.list(new FilenameFilter() {
				public boolean accept(File dir, String name)
				{
					return name.matches("\\p{XDigit}+");
				}
			});

			if (files.length != 0)
			{
				Arrays.sort(files);

				for (String file : files)
				{
					try
					{
						fileList.offer(Long.toString(Long.parseLong(file, 16)));
					}
					catch (NumberFormatException e)
					{
						/* ignore */}
				}
			}

			boolean readMetaData = true;
			if (fileList.size() == 0)
			{
				readMetaData = false;
				deQueueBufferIndex = 0;
				enQueueBufferIndex = deQueueBufferIndex;
			}
			else
			{
				deQueueBufferIndex = Long.parseLong(fileList.getFirst());
				enQueueBufferIndex = Long.parseLong(fileList.getLast()) + 1; // always a new file for enqueue
			}
			fileList.offer(Long.toString(enQueueBufferIndex));
			deQueueBuffer = loadBuffer(deQueueBufferIndex);
			enQueueBuffer = loadBuffer(enQueueBufferIndex);

			if (readMetaData)
			{
				// read the metadata
				readMetaData();
			}
			else
			{
				flushMetaData();
			}
			log.info("PerstQueue[" + dir + "] Start Index: " + deQueueBufferIndex + " End Index: " + enQueueBufferIndex);
			open = true;
			// if in read only mode - stop here
			if (readOnly)
				return;
			// start the persister thread
			persistThread = new Thread("PersistThread-" + dir) {
				public void run()
				{
					log.info("Starting thread: " + getName());
					while (open)
					{
						if (objectEnqueued.get())
						{
							lock.lock();
							try
							{
								if (objectEnqueued.getAndSet(false)) // double check idiom
									enQueueBuffer.writeBuffer();
							}
							catch (Exception e)
							{
								/* ignore */}
							finally
							{
								lock.unlock();
							}
						}

						if (objectDequeued.get())
						{
							lock.lock();
							try
							{
								if (objectDequeued.getAndSet(false)) // double check idiom
									flushMetaData();
							}
							catch (Exception e)
							{
								/* ignore */}
							finally
							{
								lock.unlock();
							}
						}

						try
						{
							sleep(POLLING_TIME_FOR_ACTIVITY);
						}
						catch (InterruptedException e)
						{
							Thread.interrupted();
							break;
						}
					}
					log.info("Done PerstQueue persisting thread: " + getName());
				}
			};
			persistThread.start();
		}
		catch (Exception e)
		{
			log.error("Error while opening persistent queue[" + dir + "]", e);
		}
		finally
		{
			lock.unlock();
		}
	}

	@Override
	public void close()
	{
		lock.lock();
		try
		{
			if (!open)
				return;
			log.info("Closing the persistent queue[" + dir + "] enQueueBufferIndex: " + enQueueBufferIndex + " deQueueBufferIndex: "
			         + deQueueBufferIndex + " Size: " + size());

			open = false;

			// if in read only mode - stop here
			if (readOnly)
				return;

			// save buffers and metadata
			if (enQueueBufferIndex == deQueueBufferIndex && !deQueueBuffer.canRead())
			{
				// Nothing left to read, so delete all the files
				deQueueBuffer.deleteBuffer(false);
				deQueueBuffer.setReadOffset(0);
			}
			else
			{
				enQueueBuffer.writeBuffer();
			}
			flushMetaData();

			// stop the persister thread
			try
			{
				persistThread.interrupt();
				persistThread.join();
			}
			catch (InterruptedException e)
			{
				Thread.interrupted();
			}
		}
		catch (Exception e)
		{
			log.error("Error while closing persistent queue[" + dir + "]", e);
		}
		finally
		{
			lock.unlock();
		}
	}

	private void readMetaData()
	{
		File metaDataFile = new File(dir + File.separator + META_DATA_FILE);
		if (metaDataFile.exists())
		{
			try
			{
				DataInputStream in = new DataInputStream(new FileInputStream(metaDataFile));
				numInQueue.set(in.readInt());
				deQueueBuffer.setReadOffset(in.readInt());
				in.close();
			}
			catch (IOException e)
			{
				// in the rare case of an error
				// assume everything ZERO'ed
				numInQueue.set(0);
				deQueueBuffer.setReadOffset(0);
				log.error("Error while reading persistent queue metadata[" + dir + "]", e);
			}
			log.info("Read from metadata[" + dir + "]: numInQueue: " + numInQueue.get() + " readOffset: " + deQueueBuffer.getReadOffset());
		}
	}

	private void flushMetaData() throws IOException
	{
		if (readOnly)
			return;
		// the metadata to write in the file
		ByteArrayOutputStream bout = new ByteArrayOutputStream(8);
		DataOutputStream out = new DataOutputStream(bout);
		out.writeInt(numInQueue.get());
		out.writeInt(deQueueBuffer.getReadOffset());
		out.flush();
		byte[] buffer = bout.toByteArray();
		// atomically write to the filesystem
		File fileObject = new File(dir + File.separator + META_DATA_FILE);
		if (fileObject.exists())
		{
			File fileToWriteTo = new File(fileObject.getAbsolutePath() + TMP_SUFFIX + System.currentTimeMillis());
			FileUtil.writeBytesToFile(fileToWriteTo, buffer, 0, buffer.length);
			fileToWriteTo.renameTo(fileObject);
		}
		else
		{
			FileUtil.writeBytesToFile(fileObject, buffer, 0, buffer.length);
		}
		log.info("Write to metadata[" + dir + "]: numInQueue: " + numInQueue.get() + " readOffset: " + deQueueBuffer.getReadOffset());
		// no need to flush metadata
		objectDequeued.set(false);
	}

	private Buffer loadBuffer(long num) throws IOException
	{
		if (num == deQueueBufferIndex && deQueueBuffer != null)
			return deQueueBuffer;
		if (num == enQueueBufferIndex && enQueueBuffer != null)
			return enQueueBuffer;

		return new Buffer(getFileName(num), bufferSize, readOnly);
	}

	private String getFileName(long num)
	{
		return String.format("%s%s%016x", dir, File.separator, num);
	}

	private V internalRemoveObj() throws IOException
	{
		byte[] objectData = internalRemove(true);
		return objectData == null ? null : serializer.bytesToObject(objectData);
	}

	/**
	 * Point of note - this method can return null : even if the queue is full in
	 * the condition that byte[] cannot be properly converted to writable
	 * 
	 * @param updateReadPointer
	 * @return
	 * @throws IOException
	 */
	private byte[] internalRemove(boolean updateReadPointer) throws IOException
	{
		while (true)
		{
			if (deQueueBuffer.canRead())
			{
				try
				{
					byte[] objectData = deQueueBuffer.readData(updateReadPointer);

					// don't allow the numInQueue to become -ve
					if (updateReadPointer && numInQueue.get() > 0)
						numInQueue.getAndDecrement();

					// the persister thread can now save changes
					objectDequeued.set(true);

					return objectData;
				}
				catch (Exception e)
				{
					if (deQueueBufferIndex != enQueueBufferIndex)
					{
						// mark the current buffer as corrupted and move on
						nextDequeueBuffer(true);
						continue;
					}
					else
					{
						// enQueue is always a new buffer so this case
						// should not arise unless writable itself
						// is not written correctly
						throw new RuntimeException(e);
					}
				}
			}

			// Being read and written from the same buffer, and nothing left to read in the
			// current
			// deQueueBuffer. So return null
			if (deQueueBufferIndex == enQueueBufferIndex)
			{
				// force set this to zero since no more elements in queue
				numInQueue.set(0);
				return null;
			}

			// go for the next file
			nextDequeueBuffer(false);
		}
	}

	/**
	 * If the current file isCorrupted dont delete but move it to a new name
	 * 
	 * @param isCorrupted
	 * @throws IOException
	 */
	private void nextDequeueBuffer(boolean isCorrupted) throws IOException
	{
		deQueueBuffer.deleteBuffer(isCorrupted);
		fileList.poll();
		long deQueueBufferIndex = Long.parseLong(fileList.getFirst());
		deQueueBuffer = loadBuffer(deQueueBufferIndex);
		this.deQueueBufferIndex = deQueueBufferIndex;
		flushMetaData();
	}

	private void nextEnqueueBuffer() throws IOException
	{
		lock.lock();
		try
		{
			enQueueBuffer.writeBuffer();
			// no need to flush buffer
			objectEnqueued.set(false);
			// create a new buffer
			long enQueueBufferIndex = this.enQueueBufferIndex + 1;
			fileList.offer(Long.toString(enQueueBufferIndex));
			enQueueBuffer = loadBuffer(enQueueBufferIndex);
			this.enQueueBufferIndex = enQueueBufferIndex;
			flushMetaData();
		}
		finally
		{
			lock.unlock();
		}
	}

	/*----*/
	/**
	 * There are a number of buffers in a block. Each buffer, starts with int -
	 * containing the read offset int - containing the write offset
	 */
	private static class Buffer
	{
		private static class ByteUtils
		{
			public static void writeInt(final byte[] buffer, int offset, int data)
			{
				for (int i = 0; i < INT_SIZE; i++)
				{
					buffer[offset + i] = (byte) (data & (int) 0xFF);
					data = data >> 8;
				}
			}

			public static int readInt(final byte[] buffer, int offset)
			{
				int data = 0;
				for (int i = INT_SIZE - 1; i >= 0; i--)
				{
					data = data << 8;
					data = data | (buffer[offset + i] & 0xFF);
				}
				return data;
			}
		}

		private File			fileObject;
		private byte[]			buffer;
		private int				writeOffset	= 0;	// Write offset in the buffer
		private int				readOffset	= 0;	// Read offset on the buffer
		private final boolean	readOnly;

		private static final String	TMP_SUFFIX			= ".tmp.";
		private static final String	CORRUPTED_SUFFIX	= ".corrupted.";
		private static final int	INT_SIZE			= Integer.SIZE / 8;

		public Buffer(String filePath, int bufferSize, boolean readOnly) throws IOException
		{
			this.readOnly = readOnly;
			fileObject = new File(filePath);
			writeOffset = 0;
			readOffset = 0;
			if (fileObject.exists())
			{
				buffer = FileUtil.getBytesFromFile(fileObject);
				writeOffset = buffer.length;
			}
			else
			{
				buffer = new byte[bufferSize];
			}
		}

		public boolean canWrite()
		{
			return writeOffset < buffer.length;
		}

		public boolean canRead()
		{
			return readOffset + INT_SIZE < writeOffset;
		}

		public int getReadOffset()
		{
			return readOffset;
		}

		public void setReadOffset(int readOffset)
		{
			if (readOffset >= 0 && readOffset <= buffer.length)
				this.readOffset = readOffset;
		}

		public void writeBuffer() throws IOException
		{
			if (readOnly)
				return;

			if (fileObject.exists())
			{
				File fileToWriteTo = new File(fileObject.getAbsolutePath() + TMP_SUFFIX + System.currentTimeMillis());
				FileUtil.writeBytesToFile(fileToWriteTo, buffer, 0, writeOffset);
				fileToWriteTo.renameTo(fileObject);
			}
			else
			{
				FileUtil.writeBytesToFile(fileObject, buffer, 0, writeOffset);
			}
		}

		public void deleteBuffer(boolean corrupted) throws IOException
		{
			if (readOnly)
				return;

			if (corrupted)
			{
				File fileToWriteTo = new File(fileObject.getAbsolutePath() + CORRUPTED_SUFFIX + System.currentTimeMillis());
				FileUtil.writeBytesToFile(fileToWriteTo, buffer, readOffset, writeOffset - readOffset);
			}
			if (fileObject.exists())
				fileObject.delete();
		}

		public void writeData(byte[] data) throws IOException
		{
			// extend the current buffer if the object does not fit
			int numBytes = data.length;
			if ((writeOffset + INT_SIZE + numBytes) > buffer.length)
			{
				byte[] newBuffer = new byte[writeOffset + INT_SIZE + numBytes];
				System.arraycopy(buffer, 0, newBuffer, 0, writeOffset);
				buffer = newBuffer;
			}
			// write the object bytes in buffer
			ByteUtils.writeInt(buffer, writeOffset, numBytes);
			System.arraycopy(data, 0, buffer, writeOffset + INT_SIZE, numBytes);
			// move the write offset ahead
			writeOffset += numBytes + INT_SIZE;
		}

		public byte[] readData(boolean updateReadPointer) throws IOException
		{
			int numBytes = ByteUtils.readInt(buffer, readOffset);
			if (numBytes <= 0 || numBytes > buffer.length - (readOffset + INT_SIZE))
				throw new IOException(
				                "Error: numBytes: " + numBytes + " readOffset: " + (readOffset + INT_SIZE) + " buffer length: " + buffer.length);
			// read the object bytes in buffer
			byte[] data = new byte[numBytes];
			System.arraycopy(buffer, readOffset + INT_SIZE, data, 0, numBytes);
			// move the read offset ahead if updateReadPointer
			if (updateReadPointer)
				readOffset += numBytes + INT_SIZE;
			return data;
		}
	}
}
