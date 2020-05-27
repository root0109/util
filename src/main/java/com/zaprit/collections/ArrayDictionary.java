/**
 * 
 */
package com.zaprit.collections;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * This is an associative array implementation of  All Collection base class Dictionary
 * 
 * @author vaibhav.singh
 * 
 * @param <K>
 * @param <V>
 *
 */
public class ArrayDictionary<K, V> extends Dictionary<K, V> implements Cloneable
{
	/** The array of keys */
	protected K[]	keys;
	/** The array of corresponding values */
	protected V[]	values;
	/** the number of elements it contains */
	protected int	size;
	/** By how much to grow */
	protected int	incrementalSize;

	public static final int DEFAULT_CAPACITY = 10;

	/**
	 * Create an ArrayDictionary using default values for initial size and
	 * increment.
	 */
	public ArrayDictionary()
	{
		this(DEFAULT_CAPACITY, DEFAULT_CAPACITY);
	}

	/**
	 * Create an ArrayDictionary using the given initial size.
	 * (The increment is set to the same value).
	 * @param init The initial size
	 */
	public ArrayDictionary(int init)
	{
		this(init, init);
	}

	/**
	* Create an ArrayDictionary using the given initial size and
	* the given increment for growing the array.
	* 
	* @param init the initial size
	* @param incrementalSize the increment
	*/
	@SuppressWarnings("unchecked")
	public ArrayDictionary(int init, int incr)
	{
		//keys = new Object[init];
		//values = new Object[init];
		keys = (K[]) java.lang.reflect.Array.newInstance(keys.getClass().getComponentType(), init);
		values = (V[]) java.lang.reflect.Array.newInstance(values.getClass().getComponentType(), init);
		this.incrementalSize = incr;
		size = 0;
	}

	/**
	 * Create an ArrayDicitonary, <em>using</em> (not copying) the given pair
	 * of arrays as keys and values. The increment is set to the length of the
	 * arrays. 
	 * @param keys the array of keys
	 * @param values the array of values
	 */
	public ArrayDictionary(K[] keys, V[] values)
	{
		this(keys, values, values.length);
	}

	/**
	 * Create an ArrayDicitonary, <em>using</em> (not copying) the given pair
	 * of arrays as keys and values.
	 * @param keys the array of keys
	 * @param values the array of values
	 * @param incrementalSize the increment for growing the arrays
	 */
	public ArrayDictionary(K[] keys, V[] values, int incr)
	{
		this.incrementalSize = incr;
		size = keys.length;
		this.keys = keys;
		this.values = values;
	}

	protected final void grow()
	{
		grow(keys.length + incrementalSize);
	}

	@SuppressWarnings("unchecked")
	protected void grow(int newCapacity)
	{
		K[] newKeys = (K[]) java.lang.reflect.Array.newInstance(keys.getClass().getComponentType(), newCapacity);
		V[] newVals = (V[]) java.lang.reflect.Array.newInstance(values.getClass().getComponentType(), newCapacity);

		System.arraycopy(keys, 0, newKeys, 0, keys.length);
		System.arraycopy(values, 0, newVals, 0, values.length);

		keys = newKeys;
		values = newVals;
	}

	/**
	 * Clone this array dictionary.
	 * <p>As for hashtables, a shallow copy is made, the keys and elements
	 * themselves are <em>not</em> cloned.
	 * @return The clone.
	 */
	@SuppressWarnings("unchecked")
	public Object clone()
	{
		try
		{
			ArrayDictionary<K, V> clone = (ArrayDictionary<K, V>) super.clone();
			//clone.values = new Object[values.length];
			clone.values = (V[]) java.lang.reflect.Array.newInstance(values.getClass().getComponentType(), values.length);
			System.arraycopy(values, 0, clone.values, 0, values.length);
			//clone.keys = new Object[values.length];
			clone.keys = (K[]) java.lang.reflect.Array.newInstance(keys.getClass().getComponentType(), values.length);
			System.arraycopy(keys, 0, clone.keys, 0, keys.length);
			return clone;
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError();
		}
	}

	@Override
	public int size()
	{
		return size;
	}

	@Override
	public boolean isEmpty()
	{
		return size == 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Enumeration<K> keys()
	{
		return new ArrayEnumeration(keys);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Enumeration<V> elements()
	{
		return new ArrayEnumeration(values);
	}

	/**
	 * Returns the value that maps to the given key.
	 * @param key the key
	 * @return the value
	 */
	@Override
	public V get(Object key)
	{
		int n;
		int i;
		for (i = 0, n = 0; i < keys.length; i++)
		{
			if (n >= size)
				break;
			if (keys[i] == null)
				continue;
			if (keys[i].equals(key))
				return values[i];
			n++;
		}
		return null;
	}

	@Override
	public V put(K key, V value)
	{
		int empty = -1;
		int i;
		int n;
		for (i = 0, n = 0; i < keys.length; i++)
		{
			if (n >= size)
				break;
			if (keys[i] == null)
			{
				empty = i;
				continue;
			}
			if (keys[i].equals(key))
			{
				V prev = values[i];
				values[i] = value;
				return prev;
			}
			n++;
		}

		if (empty != -1)
		{
			keys[empty] = key;
			values[empty] = value;
			size++;
		}
		else
		{
			grow();
			keys[size] = key;
			values[size++] = value;
		}

		return null;
	}

	@Override
	public V remove(Object key)
	{
		int i;
		int n;
		for (i = 0, n = 0; i < keys.length; i++)
		{
			if (n >= size)
				break;
			if (keys[i] == null)
				continue;
			if (keys[i].equals(key))
			{
				size--;
				V prev = values[i];
				keys[i] = null;
				values[i] = null;
				return prev;
			}
			n++;
		}
		return null;
	}

	/** Iterates through array skipping nulls. */
	@SuppressWarnings("rawtypes")
	private class ArrayEnumeration implements Enumeration
	{
		private int			size;
		private int			elemCount;
		private int			arrayIdx;
		private Object[]	array;

		public ArrayEnumeration(Object[] array)
		{
			this(array, array.length);
		}

		public ArrayEnumeration(Object[] array, int size)
		{
			arrayIdx = elemCount = 0;
			this.size = size;
			this.array = array;
		}

		public final boolean hasMoreElements()
		{
			return elemCount < size;
		}

		public final Object nextElement()
		{
			while (array[arrayIdx] == null && arrayIdx < array.length)
				arrayIdx++;

			if (arrayIdx >= array.length)
				throw new NoSuchElementException();

			elemCount++;
			return array[arrayIdx++];
		}
	}
}
