/**
 * 
 */
package com.zaprit.cache;

import java.util.Optional;

import redis.clients.jedis.JedisSentinelPool;

/**
 * This is yet to be implemented
 * 
 * @author vaibhav.singh
 */
public class RedisSentinelCacheService<K extends CharSequence, V> implements AbstractCacheService<K, V>
{
	public RedisSentinelCacheService(String masterName, String sentinelHosts, JedisSentinelPool jedisSentinelPool)
	{}

	@Override
	public Optional<V> get(K k)
	{
		return null;
	}

	@Override
	public boolean put(K k, V v)
	{
		return false;
	}

	@Override
	public boolean put(K k, V v, int expireTimeSeconds)
	{
		return false;
	}

	@Override
	public boolean remove(K k)
	{
		return false;
	}

	@Override
	public void clearCache()
	{

	}
}
