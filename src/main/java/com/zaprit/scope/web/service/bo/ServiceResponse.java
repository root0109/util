/**
 * 
 */
package com.zaprit.scope.web.service.bo;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author vaibhav
 */
@Data
public class ServiceResponse<K>
{
	/**
	 * This field holds operation is success / failure
	 */
	private boolean		success				= true;
	/**
	 * This field holds the response of the current url
	 */
	private String		url					= "";
	/**
	 * This holds the httpStatus Code
	 */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private int			httpStatusCode		= HttpStatus.OK.value();
	/**
	 * This holds the httpStatus reason Phrase
	 */
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private String		httpStatusReason	= HttpStatus.OK.getReasonPhrase();
	/**
	 * this holds the success message or error message of the operation performed
	 */
	private String		errorMessage		= "";
	/**
	 * In case of error Params will hold error objects
	 */
	private Object[]	errorParams			= new Object[0];
	/**
	 * This generic variable holds the result of type K of the operation
	 */
	private K			result				= null;

	public void setCode(HttpStatus code)
	{
		this.httpStatusCode = code.value();
		this.httpStatusReason = code.getReasonPhrase();
	}

	public HttpStatus getCode()
	{
		return HttpStatus.valueOf(httpStatusCode);
	}
}
