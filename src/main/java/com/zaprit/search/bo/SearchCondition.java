/**
 * 
 */
package com.zaprit.search.bo;

import java.io.Serializable;
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vaibhav.singh
 * @param <K>
 */
@Getter
@Setter
@ToString
public class SearchCondition<K> implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2204655592001409369L;
	private K					column				= null;
	private Object				value				= null;

	/**
	 * @return int
	 */
	public int getIntValue()
	{
		return ((Number) value).intValue();
	}

	/**
	 * @return long
	 */
	public long getLongValue()
	{
		return ((Number) value).longValue();
	}

	/**
	 * @return double
	 */
	public double getDoubleValue()
	{
		return ((Number) value).doubleValue();
	}

	/**
	 * @return float
	 */
	public float getFloatValue()
	{
		return ((Number) value).floatValue();
	}

	/**
	 * @return String
	 */
	public String getStringValue()
	{
		return (String) value;
	}

	/**
	 * @return boolean
	 */
	public boolean getBooleanValue()
	{
		return (Boolean) value;
	}

	/**
	 * @return Date
	 */
	public Date getDateValue()
	{
		return (Date) value;
	}

	/**
	 * @return LocalDate
	 */
	public LocalDate getLocalDateValue()
	{
		return (LocalDate) value;
	}

	/**
	 * @return LocalDateTime
	 */
	public LocalDateTime getLocalDateTimeValue()
	{
		return (LocalDateTime) value;
	}

	/**
	 * @return List
	 */
	@SuppressWarnings("rawtypes")
	public List getListValue()
	{
		return (List) value;
	}

	/**
	 * @return Set
	 */
	@SuppressWarnings("rawtypes")
	public Set getSetValue()
	{
		return (Set) value;
	}

	/**
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public Map getMapValue()
	{
		return (Map) value;
	}
}
