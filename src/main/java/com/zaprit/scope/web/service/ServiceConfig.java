/**
 * 
 */
package com.zaprit.scope.web.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zaprit.scope.web.converter.ServiceRequestConverter;
import com.zaprit.scope.web.converter.ServiceResponseConverter;

/**
 * @author vaibhav.singh
 */
public class ServiceConfig
{
	private Map<String, ServiceRequestConverter>	requestConverterMap		= new HashMap<>();
	private Map<String, ServiceResponseConverter>	resoponseConverterMap	= new HashMap<>();
	private Map<String, ServiceController>			controllerMap			= new HashMap<>();
	private Set<String>								getKeywords				= new HashSet<>();
	private Set<String>								postKeywords			= new HashSet<>();

	/**
	 * @return Map<String, ServiceRequestConverter>
	 */
	public Map<String, ServiceRequestConverter> getRequestConverterMap()
	{
		return requestConverterMap;
	}

	/**
	 * @param requestConverterMap
	 */
	public void setRequestConverterMap(Map<String, ServiceRequestConverter> requestConverterMap)
	{
		this.requestConverterMap = requestConverterMap;
	}

	/**
	 * @return Map<String, ServiceResponseConverter>
	 */
	public Map<String, ServiceResponseConverter> getResoponseConverterMap()
	{
		return resoponseConverterMap;
	}

	/**
	 * @param resoponseConverterMap
	 */
	public void setResoponseConverterMap(Map<String, ServiceResponseConverter> resoponseConverterMap)
	{
		this.resoponseConverterMap = resoponseConverterMap;
	}

	/**
	 * @return Map<String, ServiceController>
	 */
	public Map<String, ServiceController> getControllerMap()
	{
		return controllerMap;
	}

	/**
	 * @param controllerMap
	 */
	public void setControllerMap(Map<String, ServiceController> controllerMap)
	{
		this.controllerMap = controllerMap;
	}

	/**
	 * @return Set<String>
	 */
	public Set<String> getGetKeywords()
	{
		return getKeywords;
	}

	/**
	 * @param getKeywords
	 */
	public void setGetKeywords(Set<String> getKeywords)
	{
		this.getKeywords = getKeywords;
	}

	/**
	 * @return Set<String>
	 */
	public Set<String> getPostKeywords()
	{
		return postKeywords;
	}

	/**
	 * @param postKeywords
	 */
	public void setPostKeywords(Set<String> postKeywords)
	{
		this.postKeywords = postKeywords;
	}

}
