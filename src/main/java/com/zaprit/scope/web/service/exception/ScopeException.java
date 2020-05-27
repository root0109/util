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
public class ScopeException extends Exception implements ParameterizedException
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4820681723595400639L;
	private int					errorCode			= -1;
	private Object[]			errorParameters		= null;

	/**
	 * @param message
	 * @param throwable
	 */
	public ScopeException(String message, Throwable throwable)
	{
		super(message, throwable);
	}

	/**
	 * @param message
	 */
	public ScopeException(String message)
	{
		super(message);
	}

	/**
	 * @param throwable
	 */
	public ScopeException(Throwable throwable)
	{
		this(throwable.getMessage(), throwable);
	}

	/**
	 * @param errorCode
	 * @param message
	 * @param errorParameters
	 */
	public ScopeException(int errorCode, String message, Object[] errorParameters)
	{
		super(message);
		this.errorCode = errorCode;
		this.errorParameters = errorParameters;
	}

	/**
	 * @param errorCode
	 * @param message
	 * @param throwable
	 */
	public ScopeException(int errorCode, String message, Throwable throwable)
	{
		super(message, throwable);
		this.errorCode = errorCode;

	}

	/**
	 * @param errorCode
	 * @param message
	 * @param errorParameters
	 * @param throwable
	 */
	public ScopeException(int errorCode, String message, Object[] errorParameters, Throwable throwable)
	{
		super(message, throwable);
		this.errorCode = errorCode;
		this.errorParameters = errorParameters;
	}
}
