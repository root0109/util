package com.zaprit.control;

/**
 * @author vaibhav.singh
 */
public interface Controllable
{
	/**
	 * Control to stop
	 */
	public void onStop();

	/**
	 * Control to start
	 */
	public void onStart();

	/**
	 * Returns current status
	 */
	public String getStatus();

	/**
	 * Returns current Identifier
	 */
	public String getIdentifier();

	/** higher values indicate later shutdown of Controllable */
	public int getShutdownPriority();

	public static final int	SHUTDOWN_PRIORITY_0	= 0;
	public static final int	SHUTDOWN_PRIORITY_1	= 1;
	public static final int	SHUTDOWN_PRIORITY_2	= 2;
	public static final int	SHUTDOWN_PRIORITY_3	= 3;

}
