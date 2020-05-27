/**
 * 
 */
package com.zaprit.profiling;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 *
 */
@Slf4j
public class MethodTraceProfiler implements MethodInterceptor
{
	private static final ThreadLocal<MethodCallStack>	methodThreadLocal	= new ThreadLocal<MethodCallStack>();
	private long										minTime				= 0;

	/**
	 * 
	 * @param minTime
	 */
	public MethodTraceProfiler(long minTime)
	{
		this.minTime = minTime;
	}

	public Object invoke(MethodInvocation methodInvocation) throws Throwable
	{
		MethodCallStack methodCallStack = methodThreadLocal.get();
		boolean start = false;
		long startTime = System.currentTimeMillis();
		if (methodCallStack == null)
		{
			methodCallStack = new MethodCallStack();
			methodCallStack.setStartTime(startTime);
			start = true;
			methodThreadLocal.set(methodCallStack);
		}
		MethodCall methodCall = new MethodCall();
		methodCall.setMethodName(methodInvocation.getMethod().getDeclaringClass().getName() + "." + methodInvocation.getMethod().getName());
		methodCall.setStartTime(startTime);
		methodCallStack.getMethodCalls().add(methodCall);
		Object result = methodInvocation.proceed();
		long endTime = System.currentTimeMillis();
		methodCall.setEndTime(endTime);
		if (start)
		{
			if ((endTime - startTime > minTime))
			{
				if (log.isInfoEnabled())
				{
					SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("-----------------------------------------------------------------------------------\n");
					stringBuilder.append("Start Time :" + new Date(startTime) + "\tEnd Time:" + new Date(endTime) + "\n");
					for (MethodCall methodCall2 : methodCallStack.getMethodCalls())
					{
						stringBuilder.append(dateFormat.format(methodCall2.getStartTime()) + "\t" + dateFormat.format(methodCall2.getEndTime()) + "\t"
						                     + (methodCall2.getEndTime() - methodCall2.getStartTime()) + "\t" + methodCall2.getMethodName() + "\n");
					}
					stringBuilder.append("-----------------------------------------------------------------------------------\n");
					log.info(stringBuilder.toString());
				}
			}
			methodThreadLocal.set(null);
		}
		return result;
	}
}
