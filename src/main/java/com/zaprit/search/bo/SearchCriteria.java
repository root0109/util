package com.zaprit.search.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author vaibhav.singh
 * @param <K>
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchCriteria<K> implements Serializable
{
	/**
	 * 
	 */
	private static final long			serialVersionUID			= 1673763077097865294L;
	/**
	 * MODE_RESULT
	 */
	public static final int				MODE_RESULT					= 0;
	/**
	 * MODE_TOTAL_COUNT
	 */
	public static final int				MODE_TOTAL_COUNT			= 1;
	/**
	 * MODE_RESULT_AND_TOTAL_COUNT
	 */
	public static final int				MODE_RESULT_AND_TOTAL_COUNT	= 2;
	private int							mode						= MODE_RESULT;
	private List<SearchCondition<K>>	conditions					= new ArrayList<>();
	private Map<K, SearchCondition<K>>	mapConditions				= new HashMap<>();
	private int							startIndex					= -1;
	private int							noOfRecords					= -1;
	private K							sortColumn					= null;
	private boolean						ascending					= false;

	/**
	 * @param mode
	 */
	public SearchCriteria(int mode)
	{
		this.mode = mode;
	}

	/**
	 * @param column
	 * @param value
	 */
	public void addCondition(K column, Object value)
	{
		SearchCondition<K> searchCondition = new SearchCondition<>();
		searchCondition.setColumn(column);
		searchCondition.setValue(value);
		conditions.add(searchCondition);
		mapConditions.put(column, searchCondition);
	}

	/**
	 * @param searchCondition
	 */
	public void addCondition(SearchCondition<K> searchCondition)
	{
		conditions.add(searchCondition);
		mapConditions.put(searchCondition.getColumn(), searchCondition);
	}

	/**
	 * @param searchConditions
	 */
	public void addConditions(List<SearchCondition<K>> searchConditions)
	{
		conditions.addAll(searchConditions);
		for (SearchCondition<K> searchCondition : searchConditions)
		{
			mapConditions.put(searchCondition.getColumn(), searchCondition);
		}
	}

	/**
	 * @param column
	 * @return searchConditions
	 */
	public SearchCondition<K> getCondition(K column)
	{
		return mapConditions.get(column);
	}

	/**
	 * @return List<SearchConditios
	 */
	public List<SearchCondition<K>> getConditions()
	{
		return conditions;
	}

	/**
	 * @return mode
	 */
	public boolean reqTotalCount()
	{
		return (mode == SearchCriteria.MODE_TOTAL_COUNT) || (mode == SearchCriteria.MODE_RESULT_AND_TOTAL_COUNT);
	}

	/**
	 * @return mode
	 */
	public boolean reqResult()
	{
		return (mode == SearchCriteria.MODE_RESULT) || (mode == SearchCriteria.MODE_RESULT_AND_TOTAL_COUNT);
	}
}
