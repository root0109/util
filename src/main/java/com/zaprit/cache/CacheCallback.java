/**
 * 
 */
package com.zaprit.cache;

/**
 * @author vaibhav.singh
 */
public interface CacheCallback<K, V>
{
	/**
	 * Gets called once when a new entry is added to cache
	 * 
	 * @param k
	 * @param v
	 */
	public void onEntryAddition(K key, V value);

	/**
	 * Gets called when entry is removed from cache
	 * 
	 * @param k
	 * @param v
	 */
	public void onEntryRemoval(K key, V value);
}
