/**
 * 
 */
package com.zaprit.cache;

import java.util.Optional;

/**
 * @author vaibhav.singh
 * @param <S>
 *            Caching service
 * @param <K>
 *            Key to be cached
 * @param <V>
 *            Value to be cached for key K
 */
public interface AbstractCacheLayer<S, K, V>
{
	public AbstractCacheLayer<S, K, V> withCacheService(S cacheService, int defaultExpiration);

	public Optional<V> get(K key);

	public boolean put(K key, V value);

	public boolean put(K key, V value, int expireTimeSeconds);

	public boolean remove(K key);

	public void clearCache();
}
