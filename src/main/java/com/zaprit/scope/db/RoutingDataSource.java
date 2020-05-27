/**
 * 
 */
package com.zaprit.scope.db;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author vaibhav.singh
 */
public class RoutingDataSource extends AbstractRoutingDataSource
{
	private Map<Object, Object> targetDataSources = null;

	@Override
	protected String determineCurrentLookupKey()
	{
		return ConnectionIdHolder.getConnectionId().getName();
	}

	@Override
	protected DataSource determineTargetDataSource()
	{
		Object lookupKey = determineCurrentLookupKey();
		DataSource dataSource = (DataSource) targetDataSources.get(lookupKey);
		/*
		 * for (Entry<Object, Object> entry : this.targetDataSources.entrySet()) { if
		 * (lookupKey.equals(entry.getKey())) { dataSource = (DataSource)
		 * entry.getValue(); } }
		 */
		if (dataSource == null)
		{
			throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
		}
		return dataSource;
	}

	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources)
	{
		super.setTargetDataSources(targetDataSources);
		this.targetDataSources = targetDataSources;
	}
}
