/**
 * 
 */
package com.zaprit.cache;

import java.util.Optional;

/**
 * @author vaibhav.singh
 */
public interface AbstractCacheService<K, V>
{
	/**
	 * @param key
	 * @return Value of the corresponding key
	 */
	public Optional<V> get(K key);

	/**
	 * @param key
	 * @param value
	 * @return the success/failure of the operation
	 */
	public boolean put(K key, V value);

	/**
	 * @param key
	 * @param value
	 * @param expireTimeSeconds
	 * @return the success/failure of the operation
	 */
	public boolean put(K key, V value, int expireTimeSeconds);

	/**
	 * @param key
	 * @return the success/failure of the operation
	 */
	public boolean remove(K key);

	/**
	 * 
	 */
	public void clearCache();
}
