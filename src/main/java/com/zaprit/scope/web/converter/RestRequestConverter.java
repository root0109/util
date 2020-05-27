/**
 * 
 */
package com.zaprit.scope.web.converter;

import javax.servlet.http.HttpServletRequest;

import com.zaprit.scope.web.service.bo.ServiceRequest;

/**
 * @author vaibhav.singh
 *
 */
public class RestRequestConverter implements ServiceRequestConverter
{
	@Override
	public ServiceRequest getServiceRequest(HttpServletRequest request)
	{
		ServiceRequest serviceRequest = new ServiceRequest();
		serviceRequest.setMethod(request.getParameter("method"));
		serviceRequest.setParams(request.getParameterMap());
		return serviceRequest;
	}
}
