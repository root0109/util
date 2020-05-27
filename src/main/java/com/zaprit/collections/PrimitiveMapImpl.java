/**
 * 
 */
package com.zaprit.collections;

import java.util.HashMap;

/**
 * @author dhruv.patel
 *
 */
public class PrimitiveMapImpl extends HashMap<String, String> implements PrimitiveMap
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8791960746279895698L;

	/**
	 * 
	 * @param key
	 * @return Long
	 */
	public Long getLong(String key)
	{
		String value = get(key);
		if (value != null && value.length() != 0)
		{
			return Long.parseLong(value.trim());
		}
		return null;

	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return long
	 */
	public long getLong(String key, long defaultValue)
	{
		String value = get(key);
		if (value != null && value.length() != 0)
		{
			return Long.parseLong(value.trim());
		}
		return defaultValue;
	}

	/**
	 * 
	 * @param key
	 * @return Integer
	 */
	public Integer getInteger(String key)
	{
		String value = get(key);
		if (value != null && value.length() != 0)
		{
			return Integer.parseInt(value.trim());
		}
		return null;

	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return int
	 */
	public int getInteger(String key, int defaultValue)
	{
		String value = get(key);
		if (value != null && value.length() != 0)
		{
			return Integer.parseInt(value.trim());
		}
		return defaultValue;
	}

	/**
	 * 
	 * @param key
	 * @return Double
	 */
	public Double getDouble(String key)
	{
		String value = get(key);
		if (value != null && value.length() != 0)
		{
			return Double.parseDouble(value.trim());
		}
		return null;

	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return double
	 */
	public double getDouble(String key, double defaultValue)
	{
		String value = get(key);
		if (value != null && value.length() != 0)
		{
			return Double.parseDouble(value.trim());
		}
		return defaultValue;
	}

	/**
	 * 
	 * @param key
	 * @return Double
	 */
	public Boolean getBoolean(String key)
	{
		String value = get(key);
		if (value != null && value.length() != 0)
		{
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return null;

	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return double
	 */
	public boolean getBoolean(String key, boolean defaultValue)
	{
		String value = get(key);
		if (value != null && value.length() != 0)
		{
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on") || value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1"))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return defaultValue;
	}

	/**
	 * 
	 * @param key
	 * @return Double
	 */
	public String getString(String key)
	{
		return get(key);
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return double
	 */
	public String getString(String key, String defaultValue)
	{
		String value = get(key);
		if (value != null && value.length() != 0)
		{
			return value;
		}
		return defaultValue;
	}

}
