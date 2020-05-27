/**
 * 
 */
package com.zaprit.scope.db;

import java.sql.Connection;

/**
 * @author vaibhav.singh
 */
public interface CallBackConnection<K>
{
	/**
	 * @param connection
	 * @return K
	 * @throws Exception
	 */
	public K execute(Connection connection) throws Exception;
}
