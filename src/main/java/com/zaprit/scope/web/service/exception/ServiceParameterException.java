/**
 * 
 */
package com.zaprit.scope.web.service.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author vaibhav.singh
 */
@Getter
@Setter
@NoArgsConstructor
public class ServiceParameterException extends ScopeException implements ParameterizedException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7883389896121398838L;

	public static final int	ERR_CODE_PARAMETER_MISSING	= 1987;
	public static final int	ERR_CODE_INVALID_PARAMETER	= 1988;

	private int			errorCode		= ERR_CODE_PARAMETER_MISSING;
	private Object[]	errorParameters	= null;

	/**
	 * @param message
	 * @param throwable
	 */
	public ServiceParameterException(String message, Throwable throwable)
	{
		super(message, throwable);
	}

	/**
	 * @param message
	 */
	public ServiceParameterException(String message)
	{
		super(message);
	}

	/**
	 * @param throwable
	 */
	public ServiceParameterException(Throwable throwable)
	{
		super(throwable);
	}

	/**
	 * @param errorCode
	 * @param message
	 * @param errorParameters
	 */
	public ServiceParameterException(int errorCode, String message, Object[] errorParameters)
	{
		super(errorCode, message, errorParameters);
	}

	/**
	 * @param errorCode
	 * @param message
	 * @param throwable
	 */
	public ServiceParameterException(int errorCode, String message, Throwable throwable)
	{
		super(errorCode, message, throwable);
	}

	/**
	 * @param errorCode
	 * @param message
	 * @param errorParameters
	 * @param throwable
	 */
	public ServiceParameterException(int errorCode, String message, Object[] errorParameters, Throwable throwable)
	{
		super(errorCode, message, errorParameters, throwable);
	}
}
