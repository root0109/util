/**
 * 
 */
package com.zaprit.concurrent;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.mutable.MutableInt;

import com.zaprit.scope.web.service.exception.FrameworkException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * This a single Reader and Multiple Processor thread class, wherein jobs 
 * ReaderThread      -> job is to put into object in memory queue
 * Processor Thread  -> job is to poll from objectQueue and process it
 *      
 * @author vaibhav.singh
 *
 */
@Slf4j
@Getter
@Setter
public abstract class MultiThreadProcessor<E>
{
	private static final int THREAD_SLEEP_TIME = 1000; /* Sleep time for threads */

	private static final int READ_THREAD_SLEEP_TIME = 2000;

	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private BlockingQueue<E>		objectQueue;			/* The queue for objects */
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private ReaderThread<E>			reader;					/* Reader thread */
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private ProcessorThread<E>[]	senders;				/* The sender threads */
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private ThreadGroup				processorThreadGroup;	/* Thread group for sender threads */
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private volatile boolean		stop	= false;		/* stops the running service */
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private boolean					stopped	= true;			/* when the service is stopped */

	private int		numProcessorThreads;
	private int		readThreadSleepTime;
	private boolean	sleepDuringProcessing	= true;

	private int retriesDuringProcessing = 10;

	private boolean interruptProcessingOnStop = true;

	public MultiThreadProcessor(int numProcessorThreads)
	{
		this(numProcessorThreads, 10000);
	}

	public MultiThreadProcessor(int numProcessorThreads, int queueSize)
	{
		this(numProcessorThreads, queueSize, READ_THREAD_SLEEP_TIME);
	}

	public MultiThreadProcessor(int numProcessorThreads, int queueSize, int readThreadSleepTime)
	{
		objectQueue = new LinkedBlockingQueue<E>(queueSize);
		this.numProcessorThreads = numProcessorThreads;
		this.readThreadSleepTime = readThreadSleepTime;
	}

	/**
	 * Initialize the Multi thread processor system and start this Processor
	 */
	@SuppressWarnings("unchecked")
	public synchronized void start()
	{
		if (!stopped)
		{
			return;
		}
		objectQueue.clear();
		processorThreadGroup = new ThreadGroup("Processor");
		reader = new ReaderThread<E>("Reader-" + getClass().getCanonicalName());
		reader.start();
		senders = new ProcessorThread[numProcessorThreads];
		for (int i = 0; i < senders.length; i++)
		{
			senders[i] = new ProcessorThread<E>("SenderThread-" + getClass().getSimpleName() + "-" + i, i, sleepDuringProcessing,
			                retriesDuringProcessing);
			senders[i].start();
		}
		stopped = false;
		stop = false;
	}

	/**
	 * Stops the processor
	 * @throws InterruptedException 
	 */
	public synchronized void stop() throws InterruptedException
	{
		if (stopped)
		{
			return;
		}
		stop = true;
		while (true)
		{
			try
			{
				E tuple = objectQueue.poll();
				if (tuple == null) // Queue is empty
				{
					if (!reader.isAlive() && objectQueue.size() == 0) // If the reader thread is dead and and the object queue is empty
					{
						break;
					}
					lockfreeSleep(this, 10 * THREAD_SLEEP_TIME);
					continue;
				}
				if (tuple != null)
				{
					handleUnProcessed(tuple);
				}
			}
			catch (Exception e)
			{
				log.debug("Exception in stopping the MultiThreadProcessor:", e);
			}
		}

		if (reader.isAlive())
		{
			log.debug("Reader still alive");
		}

		reader.interrupt();
		reader.join();
		reader = null;

		for (int i = 0; i < senders.length; i++)
		{
			if (interruptProcessingOnStop)
			{
				senders[i].interrupt();
			}
			senders[i].join();
			senders[i] = null;
		}
		senders = null;
		stopped = true;
	}

	/* ----------------------- Below Methods need to be implemented by the extending class -----------------------*/
	/**
	 * To read the objects from the processor
	 * @return
	 * @throws Exception
	 */
	protected abstract List<E> readObjects() throws FrameworkException;

	/** 
	 * Processes the object with a number of retries.
	 * @param object
	 * @param numReTries
	 * @return Return false to Retry and true to not to
	 * @throws Exception
	 */
	protected abstract boolean processObject(E object, MutableInt numReTries, int threadId) throws FrameworkException;

	/**
	 * How to handle an object which was unprocessed
	 * @param object
	 * @throws Exception
	 */
	protected abstract void handleUnProcessed(E object) throws FrameworkException;

	/* ----------------------- Below Methods need to be implemented by the extending class -----------------------*/
	/**
	 * How to handle an object which couldn't be processed after max no. of retries
	 * @param object
	 * @throws Exception
	 */
	protected void handleFailedRetries(E object) throws FrameworkException
	{
		log.error("INVESTIGATE: Default handling called after failed retries for: " + object);
		return;
	}

	public int getNumBatchesInQueue()
	{
		return objectQueue.size();
	}

	public synchronized boolean isAlive()
	{
		return !stopped;
	}

	public int getSizeOfQueue()
	{
		return objectQueue.size();
	}

	private class ReaderThread<F> extends Thread
	{
		/**
		 * @param name
		 */
		public ReaderThread(String name)
		{
			super(name);
			log.debug("Starting: " + name);
		}

		public void run()
		{
			while (!stop)
			{
				try
				{
					List<E> tuples = readObjects();
					if (tuples != null)
					{
						for (E tuple : tuples)
						{
							objectQueue.put(tuple);
						}
						if (tuples.size() != 0)
						{
							continue;
						}
					}
				}
				catch (InterruptedException ie)
				{
					// In case put on object queue is waiting. interrupt will cause to break the wait and continue
					log.debug("Reader thread " + this.getName() + " interrupted. ");
					continue;
				}
				catch (Exception e)
				{
					log.debug("Reader thread caught Exception.", e);
				}
				lockfreeSleep(this, readThreadSleepTime);
			}
			log.debug("Stopped: " + getName());
		}
	}

	private void lockfreeSleep(Object obj, int time)
	{
		synchronized (obj)
		{
			try
			{
				obj.wait(time);
				//Thread.sleep(readThreadSleepTime);
			}
			catch (InterruptedException e)
			{
				log.debug("Reader thread caught Exception while sleeping.", e);
			}
		}
	}

	private class ProcessorThread<F> extends Thread
	{
		private final int		threadId;
		private final boolean	enableSleep;
		private final int		retries;

		public ProcessorThread(String name, int id, boolean enableSleep, int retries)
		{
			super(processorThreadGroup, name);
			this.threadId = id;
			this.enableSleep = enableSleep;
			this.retries = retries;
			log.debug("Starting: " + name);
		}

		public void run()
		{
			E tuple = null;
			MutableInt numReTries = new MutableInt();
			while (!stop)
			{
				try
				{
					if (tuple != null)
					{
						log.error("Tuple is not null: " + tuple);
					}
					tuple = objectQueue.take();
					numReTries.setValue(0);

					if (enableSleep)
					{
						lockfreeSleep(this, THREAD_SLEEP_TIME);
					}
					while (!stop && numReTries.getValue() < retries)
					{
						numReTries.increment();
						try
						{
							if (processObject(tuple, numReTries, threadId))
							{
								tuple = null; //condition-1 this specifies that object has been processed 
								break;
							}
						}
						/*catch (InterruptedException ie)
						{
							// In case process object is waiting. interrupt will cause to break the wait and continue
							log.debug("Processor thread " + this.getName() + " interrupted. ");
							continue;
						}*/
						catch (Exception e)
						{
							log.debug("Exception in Processing:", e);
						}

						if (enableSleep)
						{
							lockfreeSleep(this, numReTries.getValue() > 0 ? numReTries.getValue() * THREAD_SLEEP_TIME : THREAD_SLEEP_TIME);
						}
					}

					if ((!stop) && (tuple != null)) //condition-1
					{
						try
						{
							handleFailedRetries(tuple);
						}
						catch (Exception e)
						{
							log.debug("", e);
						}
						finally
						{
							tuple = null;
						}
					}
				}
				catch (InterruptedException ie)
				{
					// In case take on object queue  is waiting. interrupt will cause to break the wait and continue
					log.debug("Processor thread " + this.getName() + " interrupted. ");
					continue;
				}
				catch (Exception e)
				{
					tuple = null;
					log.debug("Found exception", e);
					try
					{
						if (enableSleep)
						{
							lockfreeSleep(this, 10 * THREAD_SLEEP_TIME);
						}
					}
					catch (Exception e1)
					{
						log.debug("Caught while sleeping..", e1);
					}
				}
			}
			try
			{
				if (tuple != null)
				{
					handleUnProcessed(tuple);
				}
			}
			catch (Exception e)
			{
				log.debug("Exception in processing:", e);
			}
			log.debug("Stopped: " + getName());
		}
	}
}
