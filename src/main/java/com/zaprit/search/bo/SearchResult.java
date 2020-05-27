/**
 * 
 */
package com.zaprit.search.bo;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author vaibhav.singh
 * @param <E>
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchResult<E> implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7508255186044874867L;
	private int					totalCount			= -1;
	private E					result				= null;
}
