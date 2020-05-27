/**
 * 
 */
package com.zaprit.profiling;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author vaibhav.singh
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class MethodCall
{
	private String	methodName	= null;
	private long	startTime	= 0;
	private long	endTime		= 0;

	/**
	 * 
	 * @param methodName
	 * @param startTime
	 * @param endTime
	 */
	public MethodCall(String methodName, long startTime, long endTime)
	{
		setMethodName(methodName);
		setStartTime(startTime);
		setEndTime(endTime);
	}
}
