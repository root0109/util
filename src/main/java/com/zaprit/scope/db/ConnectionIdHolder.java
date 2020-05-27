/**
 * 
 */
package com.zaprit.scope.db;

import javax.validation.constraints.NotNull;

/**
 * @author vaibhav.singh
 */
public final class ConnectionIdHolder
{
	private ConnectionIdHolder()
	{}

	private static final ThreadLocal<ConnectionId> contextHolder = new ThreadLocal<>();

	public static void setConnectionID(@NotNull ConnectionId connectionId)
	{
		contextHolder.set(connectionId);
	}

	public static ConnectionId getConnectionId()
	{
		return contextHolder.get();
	}

	public static void clearConnectionId()
	{
		contextHolder.remove();
	}
}
