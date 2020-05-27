/**
 * 
 */
package com.zaprit.cache;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.zaprit.collections.ExpiryHashMap;
import com.zaprit.collections.ExpiryHashMap.ExpiryCallback;

/**
 * @author vaibhav.singh
 */
public class InMemoryCacheService<K extends CharSequence, V> implements AbstractCacheService<K, V>
{
	public static final long	MINUTE_IN_MILLIS	= 60 * 1000;
	public static final long	HOUR_IN_MILLIS		= 1 * 60 * MINUTE_IN_MILLIS;

	private volatile ConcurrentMap<K, V> cache;

	public InMemoryCacheService()
	{
		this.cache = new ConcurrentHashMap<>();
	}

	public InMemoryCacheService(long ttl)
	{
		this(ttl, null, true, (long) (((ttl > MINUTE_IN_MILLIS) ? ttl : HOUR_IN_MILLIS) * 0.9));
	}

	public InMemoryCacheService(long ttl, boolean increaseExpiryOnGet, long noExpiryExtensionPeriod)
	{
		this(ttl, null, increaseExpiryOnGet, noExpiryExtensionPeriod);
	}

	public InMemoryCacheService(long ttl, ExpiryCallback<K, V> expiryCallback, boolean increaseExpiryOnGet, long noExpiryExtensionPeriod)
	{
		this.cache = new ExpiryHashMap<>(((ttl > MINUTE_IN_MILLIS) ? ttl : HOUR_IN_MILLIS), expiryCallback, increaseExpiryOnGet,
		                noExpiryExtensionPeriod);
	}

	@Override
	public Optional<V> get(K key)
	{
		V obj = cache.get(key);
		return obj != null ? Optional.of(obj) : Optional.empty();
	}

	@Override
	public boolean put(K key, V value)
	{
		cache.put(key, value);
		return true;
	}

	@Override
	public boolean put(K key, V value, int expireTimeSeconds)
	{
		cache.put(key, value);
		return true;
	}

	@Override
	public boolean remove(K key)
	{
		return cache.remove(key) != null;
	}

	@Override
	public void clearCache()
	{
		try
		{
			cache.clear();
		}
		catch (Exception e)
		{}
	}
}
