/**
 * 
 */
package com.zaprit.search.bo;

import java.util.ArrayList;
import java.util.List;

import com.zaprit.scope.db.DBProvider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Please Note DTO's are not be used for persistence, only for transfer
 * 
 * @author vaibhav.singh
 *
 */
@Getter
@Setter
public final class Page
{
	private final int			offset;
	private final int			limit;
	private final List<Sort>	sortColumns;
	private final DBProvider	dbProvider;

	public Page(int offset, int limit, List<Sort> sortColumns)
	{
		this.offset = offset;
		this.limit = limit;
		if (sortColumns == null)
			sortColumns = new ArrayList<>();
		this.sortColumns = sortColumns;
		this.dbProvider = DBProvider.MYSQL;
	}

	public Page(int offset, int limit, List<Sort> sortColumns, DBProvider database)
	{
		this.offset = offset;
		this.limit = limit;
		if (sortColumns == null)
			sortColumns = new ArrayList<>();
		this.sortColumns = sortColumns;
		this.dbProvider = database;
	}

	/**
	 * @return current page number
	 */
	public int getPageNumber()
	{
		if (offset < limit || limit == 0)
			return 1;

		return (offset / limit) + 1;
	}

	/**
	 * @return new pagination object with offset shifted by offset+limit
	 */
	public Page getNext()
	{
		return new Page(offset + limit, limit, sortColumns);
	}

	/**
	 * @return new pagination object with offset shifted by offset-limit
	 */
	public Page getPrevious()
	{
		if (limit >= offset)
		{
			return new Page(0, limit, sortColumns);
		}
		else
		{
			return new Page(offset - limit, limit, sortColumns);
		}
	}

	/**
	 * Enum declaring the sorting order of the query
	 * 
	 * @author vaibhav.singh
	 *
	 */
	public enum Order
	{
		ASC,
		DESC
	}

	/**
	 * Enum for null handling in result order
	 * 
	 * @author vaibhav.singh
	 *
	 */
	public enum NullHandling
	{
		NULLS_FIRST,
		NULLS_LAST
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class Sort
	{
		private String			property;
		private Order			order;
		private NullHandling	nullHandling;
		private String			alias;			// When using a JOIN , we give alias to the table 

		public Sort(String property, Order order, String alias)
		{
			this.property = property;
			this.order = order;
			this.alias = alias;
		}
	}
}
