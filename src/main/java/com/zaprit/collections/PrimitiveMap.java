/**
 * 
 */
package com.zaprit.collections;

import java.util.Map;

/**
 * @author dhruv.patel
 *
 */
public interface PrimitiveMap extends Map<String, String>
{
	/**
	 * 
	 * @param key
	 * @return Long
	 */
	public Long getLong(String key);

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return long
	 */
	public long getLong(String key, long defaultValue);

	/**
	 * 
	 * @param key
	 * @return Integer
	 */
	public Integer getInteger(String key);

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return Integer
	 */
	public int getInteger(String key, int defaultValue);

	/**
	 * 
	 * @param key
	 * @return Double
	 */
	public Double getDouble(String key);

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return double
	 */
	public double getDouble(String key, double defaultValue);

	/**
	 * 
	 * @param key
	 * @return Boolean
	 */
	public Boolean getBoolean(String key);

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return boolean
	 */
	public boolean getBoolean(String key, boolean defaultValue);

	/**
	 * 
	 * @param key
	 * @return String
	 */
	public String getString(String key);

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return String
	 */
	public String getString(String key, String defaultValue);
}
