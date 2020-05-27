/**
 * 
 */
package com.zaprit.cache;

import java.util.Optional;

/**
 * @author vaibhav.singh
 */
public class CacheLayer<C extends AbstractCacheService<K, V>, K, V> implements AbstractCacheLayer<C, K, V>
{
	private C	cacheService;
	private int	defaultExpiration;

	@Override
	public AbstractCacheLayer<C, K, V> withCacheService(C cacheService, int defaultExpiration)
	{
		this.cacheService = cacheService;
		this.defaultExpiration = defaultExpiration;
		return this;
	}

	@Override
	public Optional<V> get(K k)
	{
		return cacheService.get(k);
	}

	@Override
	public boolean put(K k, V v)
	{
		return cacheService.put(k, v, defaultExpiration);
	}

	@Override
	public boolean put(K k, V v, int expireTimeSeconds)
	{
		return cacheService.put(k, v, expireTimeSeconds);
	}

	@Override
	public boolean remove(K k)
	{
		return cacheService.remove(k);
	}

	@Override
	public void clearCache()
	{
		cacheService.clearCache();
	}
}
