/**
 * 
 */
package com.zaprit.scope.web.service;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpStatus;

import com.zaprit.scope.web.converter.ServiceRequestConverter;
import com.zaprit.scope.web.converter.ServiceResponseConverter;
import com.zaprit.scope.web.service.bo.ServiceRequest;
import com.zaprit.scope.web.service.bo.ServiceResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
@Getter
@Setter
public abstract class ServiceFrontController
{
	private ServiceConfig serviceConfig = null;

	protected void handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String url = request.getRequestURI();
		String requestType = url.substring(url.lastIndexOf('/') + 1);
		String responseFormatType = request.getParameter("responseType");
		ServiceRequestConverter serviceRequestConverter = serviceConfig.getRequestConverterMap().get(requestType);
		ServiceResponseConverter serviceResponseConverter = serviceConfig.getResoponseConverterMap().get(responseFormatType);
		serviceResponseConverter.setResponseHeader(response);
		PrintWriter printWriter = response.getWriter();
		ServiceResponse<?> serviceResponse = new ServiceResponse<>();
		ServiceRequest serviceRequest = serviceRequestConverter.getServiceRequest(request);
		String method = serviceRequest.getMethod();

		// Mapping Diagnostic Context to collect all query params
		for (Entry<String, String[]> entry : serviceRequest.getParams().entrySet())
		{
			ThreadContext.put(entry.getKey(), entry.getValue()[0]);
		}

		int lastDotIndex = method.lastIndexOf(".");
		if (lastDotIndex == -1)
		{
			serviceResponse.setCode(HttpStatus.BAD_REQUEST);
			serviceResponse.setErrorParams(new String[] { method });
			serviceResponse.setSuccess(false);
			return;
		}
		String moduleName = method.substring(0, lastDotIndex);
		String methodName = method.substring(lastDotIndex + 1);
		ServiceController serviceController = serviceConfig.getControllerMap().get(moduleName);
		String requestMethodType = request.getMethod().toLowerCase();
		int counter = 0;
		boolean typeRequest = true;
		if (requestMethodType.equalsIgnoreCase("post"))
		{
			for (String keyword : serviceConfig.getPostKeywords())
			{
				if (methodName.startsWith(keyword))
				{
					counter++;
					break;
				}
			}
			for (String keyword : serviceConfig.getGetKeywords())
			{
				if (methodName.startsWith(keyword))
				{
					typeRequest = false;
					break;
				}
			}
		}
		else
		{
			for (String keyword : serviceConfig.getGetKeywords())
			{
				if (methodName.startsWith(keyword))
				{
					counter++;
					break;
				}
			}
			for (String keyword : serviceConfig.getPostKeywords())
			{
				if (methodName.startsWith(keyword))
				{
					typeRequest = false;
					break;
				}
			}
		}
		if (counter == 0)
		{
			if (!typeRequest)
			{
				serviceResponse.setCode(HttpStatus.BAD_REQUEST);
			}
			else
			{
				serviceResponse.setCode(HttpStatus.METHOD_NOT_ALLOWED);
			}
			serviceResponse.setErrorParams(new String[] { requestMethodType });
			serviceResponse.setSuccess(false);
		}
		else
		{
			try
			{
				Method methodImpl = serviceController.getClass().getMethod(methodName, ServiceRequest.class, ServiceResponse.class);
				methodImpl.invoke(serviceController, serviceRequest, serviceResponse);
			}
			catch (NoSuchMethodException e)
			{
				serviceResponse.setCode(HttpStatus.METHOD_NOT_ALLOWED);
				serviceResponse.setErrorParams(new String[] { methodName });
				serviceResponse.setSuccess(false);
			}
			catch (Exception e)
			{
				serviceResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
				serviceResponse.setErrorParams(new String[] { methodName });
				serviceResponse.setSuccess(false);
				serviceResponse.setErrorMessage(e.getMessage());
				log.error("Error in method " + methodName, e);
			}
		}
		printWriter.write(serviceResponseConverter.getResponseString(serviceResponse));
		printWriter.flush();
		printWriter.close();
	}
}
