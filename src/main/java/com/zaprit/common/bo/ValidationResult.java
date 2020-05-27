/**
 * 
 */
package com.zaprit.common.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 * @author vaibhav.singh
 */
@Getter
public final class ValidationResult implements Serializable
{
	/**
	 * 
	 */
	private static final long			serialVersionUID	= -237020251284612827L;
	private Map<String, List<Object>>	errors				= new LinkedHashMap<>(5);
	private Map<String, Object>			dataMap				= new LinkedHashMap<>(3);

	/**
	 * @param entityType
	 * @param entity
	 */
	public void addError(String entityType, Object entity)
	{
		List<Object> entities = errors.get(entityType);
		if (entities == null)
		{
			entities = new ArrayList<>(3);
			errors.put(entityType, entities);
		}
		entities.add(entity);
	}

	/**
	 * @param entityType
	 * @param entity
	 */
	public void addData(String entityName, Object entity)
	{
		dataMap.put(entityName, entity);
	}

	/**
	 * @return true if error else false
	 */
	public boolean isError()
	{
		return !errors.isEmpty();
	}
}
