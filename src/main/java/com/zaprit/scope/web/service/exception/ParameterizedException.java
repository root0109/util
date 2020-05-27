/**
 * 
 */
package com.zaprit.scope.web.service.exception;

/**
 * @author vaibhav.singh
 */
public interface ParameterizedException
{
	/**
	 * @return String
	 */
	public int getErrorCode();

	/**
	 * @return Object[]
	 */
	public Object[] getErrorParameters();
}
