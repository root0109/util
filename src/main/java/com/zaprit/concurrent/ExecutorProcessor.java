/**
 * 
 */
package com.zaprit.concurrent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
public abstract class ExecutorProcessor<E>
{
	protected volatile boolean isAlive = false;

	private ThreadPoolExecutor producer;

	private ThreadPoolExecutor consumer;

	/**
	 * logging executor that prints producer and consumer queue size
	 **/
	private ScheduledThreadPoolExecutor threadPoolLogger;

	private PoolParams params;

	public ExecutorProcessor(PoolParams params)
	{
		if (params.isValid())
			this.params = params;
		else
			log.error("Executor Processor Params are not valid");
	}

	protected abstract List<E> produceObject() throws Exception;

	protected abstract boolean consumeObject(E e) throws Exception;

	protected abstract void handleFailed(E e, Exception ex) throws Exception;

	protected abstract void handleCancelled(List<E> e) throws Exception;

	/**
	 * Initializes Consumer & Producer
	 */
	public void start()
	{
		/**
		 * Its important to start consumer prior to starting the producer or else some
		 * requests may be lost.
		 */
		initConsumer();
		initProducer();
		initLogger();
		isAlive = true;
	}

	private void initConsumer()
	{
		consumer = new ThreadPoolExecutor(params.getCoreConsumerThreads(), params.getMaxConsumerThreads(), params.getConsumerThreadKeepAliveTime(),
		                params.getConsumerThreadKeepAliveTimeUnit(), params.getQueue(),
		                new MyThreadFactory(params.getProcessorName() + "-ConsumeTask"));
		consumer.setRejectedExecutionHandler(params.getConsumerTaskRejectionPolicy());

	}

	private void initProducer()
	{
		producer = new ScheduledThreadPoolExecutor(params.getNumProducerThreads(), new MyThreadFactory(params.getProcessorName() + "-ProduceTask"));
		((ScheduledThreadPoolExecutor) producer).setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		((ScheduledThreadPoolExecutor) producer).setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		if (params.isFixedDelay())
			((ScheduledThreadPoolExecutor) producer).scheduleWithFixedDelay(new ProduceTask(), 0L, params.getProducerSleepTime(),
			                params.getProducerSleepTimeUnit());
		else
			((ScheduledThreadPoolExecutor) producer).scheduleAtFixedRate(new ProduceTask(), 0L, params.getProducerSleepTime(),
			                params.getProducerSleepTimeUnit());

	}

	private void initLogger()
	{
		threadPoolLogger = new ScheduledThreadPoolExecutor(1);
		threadPoolLogger.scheduleAtFixedRate(new LogTask(), 0, 1, TimeUnit.MINUTES);
		threadPoolLogger.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		threadPoolLogger.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

	}

	private class ProduceTask implements Runnable
	{
		@Override
		public void run()
		{
			if (producer.isTerminated() || consumer.isTerminated())
				return;
			internalProduceObject();
		}

		private void internalProduceObject()
		{
			List<E> objects = null;
			try
			{
				objects = produceObject();
			}
			catch (Exception e)
			{
				log.error("Unable to produce objects list : ", e);
			}
			if (objects != null && !objects.isEmpty())
			{
				for (E e : objects)
				{
					if (consumer.isTerminated())
					{
						log.error(params.getProcessorName() + " : Consumer not started !");
					}
					ConsumeTask newTask = new ConsumeTask(e);
					consumer.submit(newTask);
				}
			}
		}
	}

	private class ConsumeTask implements Runnable
	{
		private E e;

		public ConsumeTask(E e)
		{
			this.e = e;
		}

		@Override
		public void run()
		{
			try
			{
				consumeObject(e);
			}
			catch (Exception exception)
			{
				for (int i = 0; i < params.getNumRetries(); i++)
				{
					try
					{
						consumeObject(e);
						return;
					}
					catch (Exception ex1)
					{
						log.error(params.getProcessorName() + " Consume object retry " + (i + 1) + "/" + params.getNumRetries() + " failed", ex1);
					}
				}
				try
				{
					handleFailed(e, exception);
				}
				catch (Exception e)
				{
					log.error("HandleFailed failure", e);
				}
			}
		}

		public E getObject()
		{
			return e;
		}
	}

	private class LogTask implements Runnable
	{
		@Override
		public void run()
		{
			if (getQueueSize() > 0)
				log.info(params.getProcessorName() + ": Size of consumer queue = " + getQueueSize());
		}

	}

	public void addTaskToProcessingQueue(E e)
	{
		ConsumeTask task = new ConsumeTask(e);
		if (consumer == null || consumer.isTerminated())
		{
			LinkedList<E> list = new LinkedList<>();
			list.add(e);
			try
			{
				log.info(params.getProcessorName() + " consumer is terminated. Calling handlCancelled.");
				handleCancelled(list);
			}
			catch (Exception e1)
			{
				log.error(params.getProcessorName() + " exception while  Calling handlCancelled.");
				try
				{
					handleFailed(e, e1);
				}
				catch (Exception e2)
				{
					log.error("Giving up after calling handleFailed ", e2);
				}
			}
		}
		else
			consumer.submit(task);
	}

	private int getQueueSize()
	{
		if (consumer == null)
			return 0;

		return consumer.getQueue() == null ? 0 : consumer.getQueue().size();
	}

	@SuppressWarnings("unchecked")
	public void handleUnConsumedTasks(List<Runnable> tasks)
	{
		if (tasks == null || tasks.isEmpty())
			return;
		List<E> objectsToReturn = new ArrayList<>();
		for (Runnable task : tasks)
		{
			try
			{
				if (task instanceof RunnableFuture<?>)
				{
					try
					{
						/**
						 * Found that some of the tasks which were submitted last were still in the form
						 * of RunnableFuture. Running these last set of tasks in the current thread to
						 * completion. It will break when this task is producer for some other consumer
						 * and if that consumer is shut down prior to shutting down of consumer here.
						 * These tasks which are executed here will be lost in that case.
						 */
						((RunnableFuture<?>) task).run();
					}
					catch (Exception e)
					{
						log.error(params.getProcessorName() + " : Exception while processing FutureTask", e);
					}
				}
				else
					objectsToReturn.add(((ConsumeTask) task).getObject());
			}
			catch (ClassCastException cce)
			{
				log.error(params.getProcessorName() + " : Exception while class consuming FutureTask", cce);
			}
		}
		try
		{
			handleCancelled(objectsToReturn);
		}
		catch (Exception e)
		{
			log.error(params.getProcessorName() + " : Exception while handling cancel FutureTask", e);
		}
	}

	public void stop()
	{
		stopProducer();
		stopConsumer();
		stopLogger();
	}

	private void stopProducer()
	{
		if (producer == null || producer.isTerminated())
			return;
		producer.shutdown();

		try
		{
			producer.awaitTermination(1, TimeUnit.MINUTES);
		}
		catch (InterruptedException e)
		{}

		log.info(params.getProcessorName() + ". Producer not terminated within 1 minute. trying abrupt shutdown with 30 secs wait");

		if (!producer.isTerminated())
		{
			handleUnConsumedTasks(producer.shutdownNow());
			try
			{
				producer.awaitTermination(30, TimeUnit.SECONDS);
			}
			catch (InterruptedException e)
			{}
		}

	}

	private void stopConsumer()
	{
		if (consumer == null || consumer.isTerminated())
			return;
		if (producer.isTerminated())
		{
			try
			{
				if (params.isDoAbruptShutdownForConsumer())
				{
					handleUnConsumedTasks(consumer.shutdownNow());
				}
				else
					consumer.shutdown();
				consumer.awaitTermination(params.getConsumerTerminationWaitTimeInSec(), TimeUnit.SECONDS);
				if (!consumer.isTerminated())
					log.error("Consumer not terminated within " + params.getConsumerTerminationWaitTimeInSec() + " secs.");
			}
			catch (Exception e)
			{
				log.error("Exception while shutting down processor", e);
			}
		}
		else
		{
			log.debug("Can not stop  consumer as producer is not terminated for Executor Processor " + params.getProcessorName());
		}

	}

	private void stopLogger()
	{
		if (threadPoolLogger == null || threadPoolLogger.isTerminated())
			return;
		threadPoolLogger.shutdownNow();
	}

	public boolean isAlive()
	{
		if (consumer == null || consumer.isTerminated())
			return false;
		return !producer.isTerminated();
	}
}
