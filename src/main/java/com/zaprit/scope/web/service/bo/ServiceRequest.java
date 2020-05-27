/**
 * 
 */
package com.zaprit.scope.web.service.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zaprit.scope.web.service.exception.ServiceParameterException;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vaibhav.singh
 */
@Getter
@Setter
@ToString
public class ServiceRequest implements Serializable
{
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 5530131691309972174L;
	private String					method				= null;
	private Map<String, String[]>	params				= new HashMap<>();

	/**
	 * @param key
	 * @return the Byte Value
	 */
	public Byte getByte(String key)
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return null;
		}
		return Byte.valueOf(values[0]);
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return the Byte Value
	 */
	public Byte getByte(String key, byte defaultValue)
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return defaultValue;
		}
		return Byte.valueOf(values[0]);
	}

	/**
	 * @param key
	 * @return the Byte Value
	 * @throws ServiceParameterException
	 */
	public Byte getMandatoryByte(String key) throws ServiceParameterException
	{
		try
		{
			String[] values = params.get(key);
			if (values == null)
			{
				throw new ServiceParameterException("Mandatory value missing for parameter key = " + key);
			}

			return Byte.valueOf(values[0]);
		}
		catch (NumberFormatException e)
		{
			throw new ServiceParameterException(ServiceParameterException.ERR_CODE_PARAMETER_MISSING,
			                "Error while converting " + params.get(key)[0] + "into int value for key = " + key, e);
		}
	}

	/**
	 * @param key
	 * @return the value
	 */
	// will return null if int value not found.
	public Integer getInt(String key)
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return null;
		}
		return Integer.valueOf(values[0]);
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return the value
	 */
	public Integer getInt(String key, int defaultValue)
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return defaultValue;
		}
		return Integer.valueOf(values[0]);
	}

	/**
	 * @param key
	 * @return the value
	 * @throws ServiceParameterException
	 */
	public Integer getMandatoryInt(String key) throws ServiceParameterException
	{
		try
		{
			String[] values = params.get(key);
			if (values == null)
			{
				throw new ServiceParameterException("Mandatory value missing for parameter key = " + key);
			}

			return Integer.valueOf(values[0]);
		}
		catch (NumberFormatException e)
		{
			throw new ServiceParameterException(ServiceParameterException.ERR_CODE_PARAMETER_MISSING,
			                "Error while converting " + params.get(key)[0] + "into int value for key = " + key, e);
		}
	}

	/**
	 * @param key
	 * @return the intArrayValues[]
	 * @throws ServiceParameterException
	 */
	public int[] getIntArray(String key) throws ServiceParameterException
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			return new int[0];
		}
		int intValueArray[] = new int[strValueArray.length];
		try
		{
			for (int i = 0; i < strValueArray.length; i++)
			{
				intValueArray[i] = Integer.valueOf(strValueArray[i]);
			}
		}
		catch (NumberFormatException e)
		{
			throw new ServiceParameterException("Error while converting string array to int array.", e);
		}
		return intValueArray;
	}

	/**
	 * @param key
	 * @return the intArrayValues[]
	 * @throws ServiceParameterException
	 */
	public int[] getMandatoryIntArray(String key) throws ServiceParameterException
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			throw new ServiceParameterException("Error while getting int array value for " + key);
		}

		try
		{
			int intValueArray[] = new int[strValueArray.length];
			for (int i = 0; i < strValueArray.length; i++)
			{
				intValueArray[i] = Integer.valueOf(strValueArray[i]);
			}
			return intValueArray;
		}
		catch (NumberFormatException e)
		{
			throw new ServiceParameterException("Error while converting string array to int array.", e);
		}
	}

	/**
	 * @param key
	 * @return List<String>
	 * @throws ServiceParameterException
	 */
	public List<Integer> getIntList(String key) throws ServiceParameterException
	{
		int[] values = getIntArray(key);
		if (values == null)
		{
			return new ArrayList<>();
		}
		List<Integer> valueList = new ArrayList<>();
		for (int i = 0; i < values.length; i++)
		{
			valueList.add(values[i]);
		}
		return valueList;
	}

	/**
	 * @param key
	 * @return List<String>
	 * @throws ServiceParameterException
	 */
	public List<Integer> getMandatoryIntList(String key) throws ServiceParameterException
	{
		int[] values = getMandatoryIntArray(key);
		List<Integer> valueList = new ArrayList<>();
		for (int i = 0; i < values.length; i++)
		{
			valueList.add(values[i]);
		}
		return valueList;
	}

	/**
	 * @param key
	 * @return the value
	 */
	public BigDecimal getBigDecimal(String key)
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return null;
		}
		return new BigDecimal(params.get(key)[0]).setScale(6, RoundingMode.HALF_UP);
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return the value
	 */
	public BigDecimal getBigDecimal(String key, BigDecimal defaultValue)
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return defaultValue;
		}
		return new BigDecimal(values[0]).setScale(6, RoundingMode.HALF_UP);
	}

	/**
	 * @param key
	 * @return the value
	 * @throws ServiceParameterException
	 */
	public BigDecimal getMandatoryBigDecimal(String key) throws ServiceParameterException
	{
		String[] values = params.get(key);
		if (values == null)
		{
			throw new ServiceParameterException("Mandatory value missing for parameter key = " + key);
		}
		try
		{
			return new BigDecimal(values[0]).setScale(8, RoundingMode.HALF_UP);
		}
		catch (NumberFormatException e)
		{
			throw new ServiceParameterException("Error while converting " + params.get(key)[0] + "into BigDecimal value for key = " + key, e);
		}
	}

	/**
	 * @param key
	 * @return BigDecimalValues
	 */
	public BigDecimal[] getBigDecimalArray(String key)
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			return new BigDecimal[0];
		}
		BigDecimal bigDecimalValueArray[] = new BigDecimal[strValueArray.length];
		for (int i = 0; i < strValueArray.length; i++)
		{
			bigDecimalValueArray[i] = new BigDecimal(strValueArray[i]).setScale(8, RoundingMode.HALF_UP);
		}
		return bigDecimalValueArray;
	}

	/**
	 * @param key
	 * @return the bigDecimalValues
	 * @throws ServiceParameterException
	 */
	public BigDecimal[] getMandatoryBigDecimalArray(String key) throws ServiceParameterException
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			throw new ServiceParameterException("Error while getting BigDecimal array value for " + key);
		}
		try
		{
			BigDecimal bigDecimalValueArray[] = new BigDecimal[strValueArray.length];
			for (int i = 0; i < strValueArray.length; i++)
			{
				bigDecimalValueArray[i] = new BigDecimal(strValueArray[i]).setScale(8, RoundingMode.HALF_UP);
			}
			return bigDecimalValueArray;

		}
		catch (NumberFormatException e)
		{
			throw new ServiceParameterException("Error while converting string array to BigDecimal array.", e);
		}
	}

	/**
	 * @param key
	 * @return List<String>
	 * @throws ServiceParameterException
	 */
	public List<BigDecimal> getBigDecimalList(String key)
	{
		BigDecimal[] values = getBigDecimalArray(key);
		if (values == null)
		{
			return new ArrayList<>();
		}
		List<BigDecimal> valueList = new ArrayList<>();
		for (int i = 0; i < values.length; i++)
		{
			valueList.add(values[i]);
		}
		return valueList;
	}

	/**
	 * @param key
	 * @return List<String>
	 * @throws ServiceParameterException
	 */
	public List<BigDecimal> getMandatoryBigDecimalList(String key) throws ServiceParameterException
	{
		BigDecimal[] values = getMandatoryBigDecimalArray(key);
		List<BigDecimal> valueList = new ArrayList<>();
		for (int i = 0; i < values.length; i++)
		{
			valueList.add(values[i]);
		}
		return valueList;
	}

	/**
	 * @param key
	 * @return the value
	 */
	public Double getDouble(String key)
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return null;
		}
		return Double.valueOf(params.get(key)[0]);
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return the value
	 */
	public double getDouble(String key, double defaultValue)
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return defaultValue;
		}
		return Double.valueOf(values[0]);
	}

	/**
	 * @param key
	 * @return the value
	 * @throws ServiceParameterException
	 */
	public Double getMandatoryDouble(String key) throws ServiceParameterException
	{
		String[] values = params.get(key);
		if (values == null)
		{
			throw new ServiceParameterException("Mandatory value missing for parameter key = " + key);
		}
		try
		{
			return Double.valueOf(values[0]);
		}
		catch (NumberFormatException e)
		{
			throw new ServiceParameterException("Error while converting " + params.get(key)[0] + "into double value for key = " + key, e);
		}
	}

	/**
	 * @param key
	 * @return the doubleValueArray[]
	 */
	public double[] getDoubleArray(String key)
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			return new double[0];
		}
		double doubleValueArray[] = new double[strValueArray.length];
		for (int i = 0; i < strValueArray.length; i++)
		{
			doubleValueArray[i] = Double.valueOf(strValueArray[i]);
		}
		return doubleValueArray;
	}

	/**
	 * @param key
	 * @return the doubleArrayValues[]
	 * @throws ServiceParameterException
	 */
	public double[] getMandatoryDoubleArray(String key) throws ServiceParameterException
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			throw new ServiceParameterException("Error while getting double array value for " + key);
		}
		try
		{
			double doubleValueArray[] = new double[strValueArray.length];
			for (int i = 0; i < strValueArray.length; i++)
			{
				doubleValueArray[i] = Double.valueOf(strValueArray[i]);
			}
			return doubleValueArray;

		}
		catch (NumberFormatException e)
		{
			throw new ServiceParameterException("Error while converting string array to double array.", e);
		}
	}

	/**
	 * @param key
	 * @return List<String>
	 * @throws ServiceParameterException
	 */
	public List<Double> getDoubleList(String key)
	{
		double[] values = getDoubleArray(key);
		if (values == null)
		{
			return new ArrayList<>();
		}
		List<Double> valueList = new ArrayList<>();
		for (int i = 0; i < values.length; i++)
		{
			valueList.add(values[i]);
		}
		return valueList;
	}

	/**
	 * @param key
	 * @return List<String>
	 * @throws ServiceParameterException
	 */
	public List<Double> getMandatoryDoubleList(String key) throws ServiceParameterException
	{
		double[] values = getMandatoryDoubleArray(key);
		List<Double> valueList = new ArrayList<>();
		for (int i = 0; i < values.length; i++)
		{
			valueList.add(values[i]);
		}
		return valueList;
	}

	/**
	 * @param key
	 * @return the value
	 */
	public Long getLong(String key)
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return null;
		}
		return Long.valueOf(values[0]);
	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return the value
	 */
	public Long getLong(String key, long defaultValue)
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return defaultValue;
		}
		return Long.valueOf(values[0]);
	}

	/**
	 * @param key
	 * @return the value
	 * @throws ServiceParameterException
	 */
	public Long getMandatoryLong(String key) throws ServiceParameterException
	{
		String[] values = params.get(key);
		if (values == null)
		{
			throw new ServiceParameterException("Mandatory value missing for parameter key = " + key);
		}
		try
		{
			return Long.valueOf(values[0]);
		}
		catch (NumberFormatException e)
		{
			throw new ServiceParameterException("Error while converting " + params.get(key)[0] + "into long value for key = " + key, e);
		}
	}

	/**
	 * @param key
	 * @return the longValueArray[]
	 */
	public long[] getLongArray(String key)
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			return new long[0];
		}
		long longValueArray[] = new long[strValueArray.length];
		for (int i = 0; i < strValueArray.length; i++)
		{
			longValueArray[i] = Long.valueOf(strValueArray[i]);
		}

		return longValueArray;
	}

	/**
	 * @param key
	 * @return the longArrayValues[]
	 * @throws ServiceParameterException
	 */
	public long[] getMandatoryLongArray(String key) throws ServiceParameterException
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			throw new ServiceParameterException("Parameter not found for key = " + key);
		}
		long longValueArray[] = new long[strValueArray.length];
		try
		{
			for (int i = 0; i < strValueArray.length; i++)
			{
				longValueArray[i] = Long.valueOf(strValueArray[i]);
			}
		}
		catch (NumberFormatException e)
		{
			throw new ServiceParameterException("Error while converting string array to long array.", e);
		}

		return longValueArray;
	}

	/**
	 * @param key
	 * @return List<String>
	 * @throws ServiceParameterException
	 */
	public List<Long> getLongList(String key)
	{
		long[] values = getLongArray(key);
		if (values == null)
		{
			return new ArrayList<>();
		}
		List<Long> valueList = new ArrayList<>();
		for (int i = 0; i < values.length; i++)
		{
			valueList.add(values[i]);
		}
		return valueList;
	}

	/**
	 * @param key
	 * @return List<String>
	 * @throws ServiceParameterException
	 */
	public List<Long> getMandatoryLongList(String key) throws ServiceParameterException
	{
		long[] values = getMandatoryLongArray(key);
		List<Long> valueList = new ArrayList<>();
		for (int i = 0; i < values.length; i++)
		{
			valueList.add(values[i]);
		}
		return valueList;
	}

	/**
	 * @param key
	 * @param size
	 * @return String
	 * @throws ServiceParameterException
	 */
	public String getString(String key, int size) throws ServiceParameterException
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return null;
		}
		if ((size != -1) && (values[0].trim().length() > size))
		{
			throw new ServiceParameterException("Length constraints fail for parameter= " + key);
		}
		return values[0];
	}

	/**
	 * @param key
	 * @param size
	 * @return String
	 * @throws ServiceParameterException
	 */
	public String getString(String key, String defaultValue, int size) throws ServiceParameterException
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return defaultValue;
		}
		if ((size != -1) && (values[0].trim().length() > size))
		{
			throw new ServiceParameterException("Length constraints fail for parameter= " + key);
		}
		return values[0];
	}

	/**
	 * @param key
	 * @return String[]
	 * @throws ServiceParameterException
	 */
	public List<String> getStringList(String key) throws ServiceParameterException
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			throw new ServiceParameterException("Parameter not found for key = " + key);
		}
		List<String> valueList = new ArrayList<>();
		Collections.addAll(valueList, strValueArray);
		return valueList;
	}

	/**
	 * @param key
	 * @param size
	 * @return String
	 * @throws ServiceParameterException
	 */
	public String getMandatoryString(String key, int size) throws ServiceParameterException
	{
		String[] values = params.get(key);
		if (values == null)
		{
			throw new ServiceParameterException("Parameter not found for key = " + key);
		}
		if ((size != -1) && (values[0].trim().length() > size))
		{
			throw new ServiceParameterException("Length constraints fail for parameter= " + key);
		}
		return values[0];
	}

	/**
	 * @param key
	 * @param size
	 * @return String[]
	 * @throws ServiceParameterException
	 */
	public String[] getMandatoryStringArray(String key, int size) throws ServiceParameterException
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			throw new ServiceParameterException("Parameter not found for key = " + key);
		}
		for (String value : strValueArray)
		{
			if ((size != -1) && (value.length() > size))
			{
				throw new ServiceParameterException("Length constraints fail for parameter= " + key);
			}
		}
		return strValueArray;
	}

	/**
	 * @param key
	 * @param size
	 * @return String[]
	 * @throws ServiceParameterException
	 */
	public List<String> getMandatoryStringList(String key, int size) throws ServiceParameterException
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			throw new ServiceParameterException("Parameter not found for key = " + key);
		}
		for (String value : strValueArray)
		{
			if ((size != -1) && (value.length() > size))
			{
				throw new ServiceParameterException("Length constraints fail for parameter= " + key);
			}
		}
		List<String> valueList = new ArrayList<>();
		Collections.addAll(valueList, strValueArray);
		return valueList;
	}

	/**
	 * @param key
	 * @param size
	 * @return String[]
	 * @throws ServiceParameterException
	 */
	public Set<String> getMandatoryStringSet(String key, int size) throws ServiceParameterException
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			throw new ServiceParameterException("Parameter not found for key = " + key);
		}
		if (size != -1)
		{
			for (String value : strValueArray)
			{
				if (value.length() > size)
				{
					throw new ServiceParameterException("Length constraints fail for parameter= " + key);
				}
			}
		}
		Set<String> valueList = new HashSet<>();
		Collections.addAll(valueList, strValueArray);
		return valueList;
	}

	/**
	 * @param key
	 * @return Boolean
	 */
	public Boolean getBoolean(String key)
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return false;
		}

		return (values[0].equalsIgnoreCase("true") || values[0].equalsIgnoreCase("yes") || values[0].equalsIgnoreCase("1")
		        || values[0].equalsIgnoreCase("on"));

	}

	/**
	 * @param key
	 * @param defaultValue
	 * @return Boolean
	 */
	public Boolean getBoolean(String key, Boolean defaultValue)
	{
		String[] values = params.get(key);
		if (values == null)
		{
			return defaultValue;
		}
		return (values[0].equalsIgnoreCase("true") || values[0].equalsIgnoreCase("yes") || values[0].equalsIgnoreCase("1")
		        || values[0].equalsIgnoreCase("on"));
	}

	/**
	 * @param key
	 * @return Boolean
	 * @throws ServiceParameterException
	 */
	public Boolean getMandatoryBoolean(String key) throws ServiceParameterException
	{
		String[] values = params.get(key);
		if (values == null)
		{
			throw new ServiceParameterException("Mandatory value missing for parameter key = " + key);
		}
		if (values[0].equalsIgnoreCase("true") || values[0].equalsIgnoreCase("yes") || values[0].equalsIgnoreCase("1")
		    || values[0].equalsIgnoreCase("on"))
		{
			return true;
		}
		else if (values[0].equalsIgnoreCase("false") || values[0].equalsIgnoreCase("no") || values[0].equalsIgnoreCase("0")
		         || values[0].equalsIgnoreCase("off"))
		{
			return false;
		}
		else
		{
			throw new ServiceParameterException("Error while converting " + params.get(key)[0] + "into long value for key = " + key);
		}
	}

	/**
	 * @param key
	 * @return boolean[]
	 */
	public boolean[] getBooleanArray(String key)
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			return new boolean[0];
		}
		boolean[] booleanValueArray = new boolean[strValueArray.length];
		for (int i = 0; i < strValueArray.length; i++)
		{
			booleanValueArray[i] = strValueArray[i].equalsIgnoreCase("true") || strValueArray[i].equalsIgnoreCase("yes")
			                       || strValueArray[i].equalsIgnoreCase("1") || strValueArray[i].equalsIgnoreCase("on");
		}
		return booleanValueArray;
	}

	/**
	 * @param key
	 * @return the booleanArrayValues[]
	 * @throws ServiceParameterException
	 */
	public boolean[] getMandatoryBooleanArray(String key) throws ServiceParameterException
	{
		String[] strValueArray = params.get(key);
		if (strValueArray == null)
		{
			throw new ServiceParameterException("Mandatory value missing for parameter key = " + key);
		}

		boolean[] booleanValueArray = new boolean[strValueArray.length];
		for (int i = 0; i < strValueArray.length; i++)
		{
			booleanValueArray[i] = strValueArray[i].equalsIgnoreCase("true") || strValueArray[i].equalsIgnoreCase("yes")
			                       || strValueArray[i].equalsIgnoreCase("1") || strValueArray[i].equalsIgnoreCase("on");
		}
		return booleanValueArray;
	}
}
