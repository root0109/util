package com.zaprit.scope.web.converter;

import javax.servlet.http.HttpServletRequest;

import com.zaprit.scope.web.service.bo.ServiceRequest;

/**
 * @author vaibhav.singh
 */
public interface ServiceRequestConverter
{
	/**
	 * @param request
	 * @return ServiceRequest
	 */
	public ServiceRequest getServiceRequest(HttpServletRequest request);
}
