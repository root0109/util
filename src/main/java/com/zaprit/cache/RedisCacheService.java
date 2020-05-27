/**
 * 
 */
package com.zaprit.cache;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author vaibhav.singh
 */
@Slf4j
public class RedisCacheService<K extends CharSequence, V extends CharSequence> implements AbstractCacheService<K, V>
{
	private JedisPool jedisPool;

	public RedisCacheService(String host, Integer port)
	{
		jedisPool = (jedisPool != null) ? jedisPool : new JedisPool(new JedisPoolConfig(), host, port);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<V> get(K key)
	{
		try (Jedis jedis = jedisPool.getResource())
		{
			String result = jedis.get(key.toString());
			return result == null ? Optional.empty() : (Optional<V>) Optional.of(result);
		}
		catch (Exception e)
		{
			return Optional.empty();
		}
	}

	@Override
	public boolean put(K key, V value)
	{
		try (Jedis jedis = jedisPool.getResource())
		{
			if (value instanceof CharSequence)
				jedis.set(key.toString(), value.toString());
			else
				log.error("[ERROR] : Non String values are not supported");
			return true;
		}
		catch (Exception e)
		{
			log.error("[INVESTIGATE]: ", e);
		}
		return false;
	}

	@Override
	public boolean put(K key, V value, int expireTimeSeconds)
	{
		try (Jedis jedis = jedisPool.getResource())
		{
			if (value instanceof CharSequence)
				jedis.setex(key.toString(), expireTimeSeconds, value.toString());
			else
				log.error("[ERROR] : Non String values are not supported");
			return true;
		}
		catch (Exception e)
		{
			log.error("[INVESTIGATE]: ", e);
		}
		return false;
	}

	@Override
	public boolean remove(K key)
	{
		try (Jedis jedis = jedisPool.getResource())
		{
			jedis.del(key.toString());
			return true;
		}
		catch (Exception e)
		{
			log.error("[INVESTIGATE]: ", e);
		}
		return false;
	}

	@Override
	public void clearCache()
	{
		try (Jedis jedis = jedisPool.getResource())
		{
			jedis.flushAll();
		}
		catch (Exception e)
		{
			log.error("[INVESTIGATE]: ", e);
		}
	}
}
