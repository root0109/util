package com.zaprit.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.zaprit.validation.FieldUtil;

/**
 * @author vaibhav.singh
 */
public class MyThreadFactory implements ThreadFactory
{
	private final ThreadGroup			group;
	private final String				namePrefix;
	private static final AtomicInteger	poolNumber		= new AtomicInteger(1);
	private final AtomicInteger			threadNumber	= new AtomicInteger(1);

	public MyThreadFactory(String name)
	{
		SecurityManager securityManager = System.getSecurityManager();
		group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
		if (FieldUtil.isBlank(name))
			namePrefix = "Processor-" + poolNumber.getAndIncrement() + "-thread-";
		else
			namePrefix = "Processor-" + poolNumber.getAndIncrement() + "-" + name + "-thread-";
	}

	public Thread newThread(Runnable runnable)
	{
		Thread thread = new Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0);
		if (thread.isDaemon())
			thread.setDaemon(false);
		if (thread.getPriority() != Thread.NORM_PRIORITY)
			thread.setPriority(Thread.NORM_PRIORITY);
		return thread;
	}
}
