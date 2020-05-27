/**
 * 
 */
package com.zaprit.callback;

/**
 * @author vaibhav.singh
 * @param <K>
 */
public interface CallBack<K>
{
	/**
	 * @return k
	 * @throws Exception
	 */
	public K execute() throws Exception;
}
