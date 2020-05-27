/**
 * 
 */
package com.zaprit.cache;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Optional;

/**
 * @author vaibhav.singh
 */
public final class OnionCache<K, V>
{
	private ArrayList<CacheLayer<? extends AbstractCacheService<K, V>, K, V>> layers = new ArrayList<>();

	public OnionCache<K, V> addLayer(CacheLayer<? extends AbstractCacheService<K, V>, K, V> layer)
	{
		layers.add(layer);
		return this;
	}

	public OnionCache<K, V> addLayerAtLevel(CacheLayer<? extends AbstractCacheService<K, V>, K, V> layer, int level)
	{
		layers.add(level, layer);
		return this;
	}

	public Optional<V> get(K key)
	{
		ArrayList<CacheLayer<? extends AbstractCacheService<K, V>, K, V>> cacheMissLayers = new ArrayList<>();
		ListIterator<CacheLayer<? extends AbstractCacheService<K, V>, K, V>> iterator = layers.listIterator();
		Optional<V> result = Optional.empty();
		while (iterator.hasNext())
		{
			CacheLayer<? extends AbstractCacheService<K, V>, K, V> layer = iterator.next();
			result = layer.get(key);
			if (result.isPresent())
			{
				break;
			}
			else
			{
				cacheMissLayers.add(layer);
			}
		}
		if (!cacheMissLayers.isEmpty() && result.isPresent())
		{
			V value = result.get();
			cacheMissLayers.forEach(o -> o.put(key, value));
		}
		return result;
	}

	public boolean put(K key, V value)
	{
		for (CacheLayer<? extends AbstractCacheService<K, V>, K, V> layer : layers)
		{
			if (!layer.put(key, value))
			{
				return false;
			}
		}
		return true;
	}

	public boolean put(K key, V value, int expireTimeSeconds)
	{
		for (CacheLayer<? extends AbstractCacheService<K, V>, K, V> layer : layers)
		{
			if (!layer.put(key, value, expireTimeSeconds))
			{
				return false;
			}
		}
		return true;
	}

	public boolean remove(K key)
	{
		for (CacheLayer<? extends AbstractCacheService<K, V>, K, V> layer : layers)
		{
			if (!layer.remove(key))
			{
				return false;
			}
		}
		return true;
	}
}
