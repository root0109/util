/**
 * 
 */
package com.zaprit.scope.db;

/**
 * This is a marker interafce for all the database connection ids to be implmented 
 * by each application 
 * 
 * For.eg, Read and Write can happen from multiple and differnt datasources
 * 
 * @author vaibhav.singh
 */
public interface ConnectionId
{
	public String getName();
}
