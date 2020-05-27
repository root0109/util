/**
 * 
 */
package com.zaprit.profiling;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author vaibhav.singh
 *
 */
@Getter
@Setter
public class MethodCallStack
{
	private long				startTime	= 0;
	private long				endTime		= 0;
	private List<MethodCall>	methodCalls	= new ArrayList<MethodCall>();
}
