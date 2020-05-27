/**
 * 
 */
package com.zaprit.control;

/**
 * @author vaibhav.singh
 */
public interface ReConnector
{
	public static final int RECONNECTION_PRIORITY_0 = 0;

	public static final int RECONNECTION_PRIORITY_1 = 1;

	public static final int RECONNECTION_PRIORITY_2 = 2;

	public enum RECONNECTABLE_CLIENT_ID
	{
		MESSAGE_QUEUE,
		DBACCESSOR,
		CONFIG
	}

	public void reconnect();

	public void registerReConnectable();

	public void unRegisterReConnectable();

	public String getIdentifier();

	/** higher values indicate late connection of re-connectable */
	public int getReConnectionPriority();

	public String getStatus();
}
