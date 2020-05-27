/**
 * 
 */
package com.zaprit.cache;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;

import com.zaprit.validation.FileUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
public class FileSystemCacheService<K extends CharSequence, V extends Serializable> implements AbstractCacheService<K, V>
{
	private String										basePath			= null;
	private boolean										withGzip			= false;
	private boolean										increaseExpiryOnGet	= false;
	private CacheCallback<K, V>							cacheCallback		= null;
	private ConcurrentMap<K, ScheduledFuture<Boolean>>	futures				= new ConcurrentHashMap<>();
	private ScheduledThreadPoolExecutor					executor			= new ScheduledThreadPoolExecutor(1);
	private static final TimeUnit						UNITS				= TimeUnit.SECONDS;

	public FileSystemCacheService(String basePath, boolean withGzip, boolean increaseExpiryOnGet, CacheCallback<K, V> callback) throws IOException
	{
		this(basePath, withGzip, increaseExpiryOnGet);
		this.cacheCallback = callback;
	}

	public FileSystemCacheService(String basePath, boolean withGzip, boolean increaseExpiryOnGet) throws IOException
	{
		if (!Files.isWritable(Paths.get(basePath)))
		{
			throw new IOException("The path " + basePath + " is not writable");
		}
		else
		{
			this.basePath = basePath;
			this.withGzip = withGzip;
			this.increaseExpiryOnGet = increaseExpiryOnGet;
		}
		executor.setRemoveOnCancelPolicy(true);
		executor.setMaximumPoolSize(2);
	}

	@Override
	public Optional<V> get(K key)
	{
		try
		{
			Path path = getPathFromKey(key);
			if (!path.toFile().exists())
			{
				return Optional.empty();
			}
			byte[] bytes = Files.readAllBytes(path);

			V result = (this.withGzip) ? FileUtil.ungzip(bytes) : SerializationUtils.deserialize(bytes);
			if (increaseExpiryOnGet)
				onFileAccess(key);
			return Optional.ofNullable(result);
		}
		catch (IOException | ClassNotFoundException e)
		{
			log.error("[INVESTIGATE] : ", e);
			return Optional.empty();
		}
	}

	@Override
	public boolean put(K key, V value)
	{
		try
		{
			Optional<V> result = Optional.ofNullable(value);
			if (!result.isPresent())
			{
				return false;
			}
			byte[] bytes = (this.withGzip) ? FileUtil.gzip(result.get()) : SerializationUtils.serialize(result.get());
			Files.write(getPathFromKey(key), bytes);
			if (cacheCallback != null)
				cacheCallback.onEntryAddition(key, value);
		}
		catch (IOException e)
		{
			log.error("Error in put :: ", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean put(K key, V value, int expireTimeSeconds)
	{
		put(key, value);
		scheduleForDeletion(key, expireTimeSeconds);
		return true;
	}

	@Override
	public boolean remove(K key)
	{
		try
		{
			Path path = getPathFromKey(key);
			if (!path.toFile().exists())
			{
				return false;
			}
			if (cacheCallback != null)
			{
				byte[] bytes = Files.readAllBytes(path);
				V value = (this.withGzip) ? FileUtil.ungzip(bytes) : SerializationUtils.deserialize(bytes);
				cacheCallback.onEntryRemoval(key, value);
			}
			Files.delete(path);
			futures.remove(key);
			return true;
		}
		catch (IOException | ClassNotFoundException e)
		{
			log.error("[INVESTIGATE]: ", e);
			return false;
		}
	}

	@Override
	public void clearCache()
	{
		try
		{
			FileUtils.deleteDirectory(new File(basePath));
		}
		catch (IOException e)
		{
			log.error("[INVESTIGATE]: ", e);
		}
	}

	private Path getPathFromKey(K key)
	{
		return Paths.get(basePath + File.separator + key + ((withGzip) ? ".gz" : ""));
	}

	public void scheduleForDeletion(K key, long delay)
	{
		ScheduledFuture<Boolean> future = executor.schedule(() -> {
			boolean result = false;
			try
			{
				if (futures.get(key) != null)
				{
					Files.delete(getPathFromKey(key));
					futures.remove(key);
				}
				result = true;
			}
			catch (IOException e)
			{
				log.error("[INVESTIGATE] :", e);
			}
			return result;
		}, delay, UNITS);

		futures.put(key, future);
	}

	public void onFileAccess(K key)
	{
		log.debug("onFileAccess : " + key);
		ScheduledFuture<Boolean> future = futures.get(key);
		if (future != null)
		{
			if (future.isDone())
			{
				futures.remove(key);
			}
			else if (future.cancel(false))
			{
				// reschedule the task
				futures.remove(key);
				scheduleForDeletion(key, 2 * future.getDelay(UNITS));
			}
			else
			{
				futures.remove(key);
				// too late, task was already running
			}
		}
	}
}
