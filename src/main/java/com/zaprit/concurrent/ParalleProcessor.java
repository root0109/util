/**
 * 
 */
package com.zaprit.concurrent;

/**
 * @author vaibhav.singh
 */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.zaprit.scope.web.service.exception.FrameworkException;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * R = object on which the processing has to commence
 * V = result of the processing
 * 
 * @author vaibhav.singh
 *
 */
@Slf4j
@Data
public abstract class ParalleProcessor<R, V>
{
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private ThreadPoolExecutor	consumer;
	private PoolParams			params;
	@Setter(AccessLevel.NONE)
	protected volatile boolean	isAlive	= false;

	public ParalleProcessor(PoolParams poolParams)
	{
		this.params = poolParams;
		if (!poolParams.isValid())
			throw new FrameworkException("Invalid params defined");
	}

	private void init()
	{
		initConsumer();
		isAlive = true;
	}

	/***
	 * initializes and starts consumer
	 */
	public void start()
	{
		if (isAlive())
			return;
		init();
	}

	public void stop()
	{
		stopConsumer();
		isAlive = false;
	}

	private void initConsumer()
	{
		consumer = new ThreadPoolExecutor(params.getCoreConsumerThreads(), params.getMaxConsumerThreads(), params.getConsumerThreadKeepAliveTime(),
		                params.getConsumerThreadKeepAliveTimeUnit(), params.getQueue(),
		                new MyThreadFactory(params.getProcessorName() + "-ConsumeTask"));
		consumer.setRejectedExecutionHandler(params.getConsumerTaskRejectionPolicy());
	}

	protected abstract V consumeObject(R request) throws FrameworkException;

	public Future<V> addTaskToProcessingQueue(R request)
	{
		ConsumeTask task = new ConsumeTask(request);
		if (consumer == null || consumer.isTerminated())
		{
			log.error("ConsumerThread Pool is terminated/Not started Unable to process :: " + request);
			throw new FrameworkException("ConsumerThread Pool is terminated/Not started");
		}
		else
			return consumer.submit(task);
	}

	public List<Future<V>> addTasksToProcessingQueue(List<R> requests)
	{
		List<Future<V>> list = new ArrayList<>();
		for (R request : requests)
		{
			list.add(addTaskToProcessingQueue(request));
		}
		return list;
	}

	private void stopConsumer()
	{
		if (consumer == null || consumer.isTerminated())
			return;
		if (!consumer.isTerminated())
		{
			consumer.shutdown();
			try
			{
				consumer.awaitTermination(params.getConsumerTerminationWaitTimeInSec(), TimeUnit.SECONDS);
			}
			catch (InterruptedException e)
			{
				log.error("Exception while shutting down processor", e);
			}
			if (!consumer.isTerminated())
				log.error("Consumer not terminated within " + params.getConsumerTerminationWaitTimeInSec() + " secs.");
		}
		else
		{
			log.info("Consumer is already terminated for Processor " + params.getProcessorName());
		}
	}

	public boolean isAlive()
	{
		if (consumer == null || consumer.isTerminated())
			isAlive = false;
		return isAlive;
	}

	/*
	 * -------------------------ConsumeTask Class --------------------------------
	 */

	private class ConsumeTask implements Callable<V>
	{
		private R request;

		public ConsumeTask(R request)
		{
			this.request = request;
		}

		@SuppressWarnings("unused")
		public R getObject()
		{
			return request;
		}

		@Override
		public V call() throws Exception
		{
			V value = null;
			try
			{
				value = consumeObject(request);
			}
			catch (Exception e1)
			{
				for (int i = 0; i < params.getNumRetries(); i++)
				{
					try
					{
						value = consumeObject(request);
						break;
					}
					catch (Exception e2)
					{
						log.error(params.getProcessorName() + " Consume object retry " + (i + 1) + "/" + params.getNumRetries() + " failed", e2);
					}
				}
			}
			return value;
		}
	}
}