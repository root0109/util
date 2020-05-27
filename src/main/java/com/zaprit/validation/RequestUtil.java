/**
 * 
 */
package com.zaprit.validation;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
public final class RequestUtil
{

	private static final String	SPACE_CONSTANT	= " ";
	private final static String	UNKNOWN			= "unknown";

	private RequestUtil()
	{}

	public static String getIpAddress(HttpServletRequest request)
	{
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip))
		{
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip))
		{
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip))
		{
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip))
		{
			ip = request.getHeader("HTTP_X_FORWARDED");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip))
		{
			ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip))
		{
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip))
		{
			ip = request.getHeader("HTTP_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip))
		{
			ip = request.getHeader("HTTP_FORWARDED");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip))
		{
			ip = request.getHeader("HTTP_VIA");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip))
		{
			ip = request.getHeader("REMOTE_ADDR");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip))
		{
			ip = request.getRemoteAddr();
		}
		return ip;

	}

	/**
	 * This returns the Calling stack Trace of the Method,
	 * Use it carefully
	 */
	public static void logCallingTrace()
	{
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		boolean logged = false;
		boolean foundMe = false;
		String space = SPACE_CONSTANT;
		for (int i = 0; i < stacktrace.length; i++)
		{
			StackTraceElement e = stacktrace[i];
			String methodName = e.getMethodName();
			int lineNo = e.getLineNumber();
			if (foundMe)
			{
				if (!methodName.startsWith("access$"))
				{
					log.info("<<CALL-TRACE>>" + space + String.format("%s.%s[%s]", e.getClassName(), methodName, lineNo));
					logged = true;
					space += SPACE_CONSTANT;
				}
			}
			else
			{
				if (methodName.equals("logCallingTrace"))
				{
					foundMe = true;
				}
			}
		}
		if (!logged)
			log.info("unlogged call");
	}
}
