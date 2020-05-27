/**
 * 
 */
package com.zaprit.scope.web.converter;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.zaprit.scope.web.service.bo.ServiceResponse;

/**
 * @author vaibhav.singh
 */
public interface ServiceResponseConverter
{
	/**
	 * @param serviceResponse
	 * @return String
	 */
	public <K> String getResponseString(ServiceResponse<K> serviceResponse);

	/**
	 * @param serviceResponses
	 * @return String
	 */
	public <K> String getResponseString(List<ServiceResponse<K>> serviceResponses);

	/**
	 * @param response
	 */
	public void setResponseHeader(HttpServletResponse response);
}
