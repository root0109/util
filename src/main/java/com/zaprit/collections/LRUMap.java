/**
 * 
 */
package com.zaprit.collections;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is not thread safe
 * 
 * @author vaibhav.singh
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V>
{
	/**
	 * used default serial version id 1L here
	 */
	private static final long	serialVersionUID	= 1L;
	private int					capacity;

	public LRUMap(int capacity)
	{
		super(10, 0.75f, true);
		this.capacity = capacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
	{
		return size() > capacity;
	}
}
