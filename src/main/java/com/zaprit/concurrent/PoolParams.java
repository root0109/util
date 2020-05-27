/**
 * 
 */
package com.zaprit.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 *
 */
@Data
@Slf4j
@AllArgsConstructor
public final class PoolParams
{
	private BlockingQueue<Runnable>		queue;
	private String						processorName;
	private int							numRetries							= 1;
	private int							coreConsumerThreads					= 10;
	private int							maxConsumerThreads					= 1000;
	private long						consumerThreadKeepAliveTime			= Integer.MAX_VALUE;
	private TimeUnit					consumerThreadKeepAliveTimeUnit		= TimeUnit.SECONDS;
	private RejectedExecutionHandler	consumerTaskRejectionPolicy			= new CallerRunsPolicy();
	private int							consumerTerminationWaitTimeInSec	= 10;

	private int			numProducerThreads			= 1;
	private long		producerSleepTime			= 10;
	private TimeUnit	producerSleepTimeUnit		= TimeUnit.MILLISECONDS;
	private boolean		doAbruptShutdownForConsumer	= true;
	private boolean		isFixedDelay				= true;

	public PoolParams(BlockingQueue<Runnable> queue, int maxConsumerThreads, String processorIdentifier)
	{
		this.queue = queue;
		this.maxConsumerThreads = maxConsumerThreads;
		this.processorName = processorIdentifier;
	}

	public boolean isValid()
	{
		if (queue == null)
		{
			log.error("queue not specified to executor processor");
			return false;
		}

		if (maxConsumerThreads == -1)
		{
			log.error("max consumer thread not specified to executor processor");
			return false;
		}

		if (this.processorName == null)
		{
			log.error("Processor Identitifer not specified to executor processor.");
			return false;
		}

		return true;
	}
}