/**
 * 
 */
package com.zaprit.scope.web.service.exception;

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
public class FrameworkException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1895391527503292066L;

	private String		message	= null;
	private Object[]	args	= null;

	/**
	 * @param throwable
	 */
	public FrameworkException(String message)
	{
		super(message);
		this.message = message;
	}

	/**
	 * @param throwable
	 */
	public FrameworkException(Throwable throwable)
	{
		super(throwable);
	}

	/**
	 * @param message
	 * @param throwable
	 */
	public FrameworkException(String message, Throwable throwable)
	{
		super(message, throwable);
		this.message = message;
	}

	/**
	 * @param errorCode
	 * @param message
	 * @param errorParameters
	 */
	public FrameworkException(String message, Object[] errorParameters, Throwable throwable)
	{
		super(message, throwable);
		this.message = message;
		this.args = errorParameters;
	}
}
