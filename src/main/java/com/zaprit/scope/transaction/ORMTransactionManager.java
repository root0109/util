/**
 * 
 */
package com.zaprit.scope.transaction;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.zaprit.callback.CallBack;
import com.zaprit.scope.db.ConnectionId;
import com.zaprit.scope.db.ConnectionIdHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
public class ORMTransactionManager<T extends Exception>
{
	private PlatformTransactionManager platformTransactionManager = null;

	public void setTransactionManager(PlatformTransactionManager platformTransactionManager)
	{
		this.platformTransactionManager = platformTransactionManager;
	}

	/**
	 * This is an overloaded method which always creates a read only Transaction
	 * @param callBack
	 * @return E
	 * @throws GicsException
	 */
	public <E> E doInReadTransaction(CallBack<E> callBack) throws T
	{
		return doInTransaction(callBack, true);
	}

	/**
	 * This is an overloaded method which always creates a write only Transaction
	 * 
	 * @param callBack
	 * @return E
	 * @throws GicsException
	 */
	public <E> E doInWriteTransaction(CallBack<E> callBack) throws T
	{
		return doInTransaction(callBack, false);
	}

	/**
	 * This is an overloaded method which always creates a read only Transaction for a connectionId
	 * @param callBack
	 * @return E
	 * @throws GicsException
	 */
	public <E> E doInReadTransaction(CallBack<E> callBack, ConnectionId connectionId) throws T
	{
		return doInTransaction(callBack, connectionId, true);
	}

	/**
	 * This is an overloaded method which always creates a write only Transaction for a connection ID
	 * 
	 * @param callBack
	 * @return E
	 * @throws GicsException
	 */
	public <E> E doInWriteTransaction(CallBack<E> callBack, ConnectionId connectionId) throws T
	{
		return doInTransaction(callBack, connectionId, false);
	}

	@SuppressWarnings("unchecked")
	private <E> E doInTransaction(CallBack<E> callBack, boolean readonly) throws T
	{
		E object = null;
		DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus transactionStatus = null;
		try
		{
			transactionStatus = platformTransactionManager.getTransaction(defaultTransactionDefinition);
			if (readonly)
			{
				defaultTransactionDefinition.setReadOnly(true);
			}
			object = callBack.execute();
			platformTransactionManager.commit(transactionStatus);
		}
		catch (Exception e)
		{
			log.error("Exception :", e);
			platformTransactionManager.rollback(transactionStatus);
			throw (T) e;
		}
		return object;
	}

	/**
	 * This method is for creating transaction with different datasources e.g master slave configuration or another
	 * database altogether.
	 * 
	 * @param callBack
	 * @param connectionId
	 * @return
	 * @throws T
	 */
	@SuppressWarnings("unchecked")
	private <E> E doInTransaction(CallBack<E> callBack, ConnectionId connectionId, boolean readonly) throws T
	{
		E object = null;
		ConnectionIdHolder.setConnectionID(connectionId);
		DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
		TransactionStatus transactionStatus = null;
		try
		{
			transactionStatus = platformTransactionManager.getTransaction(defaultTransactionDefinition);
			object = callBack.execute();
			platformTransactionManager.commit(transactionStatus);
		}
		catch (Exception e)
		{
			log.error("Exception :", e);
			platformTransactionManager.rollback(transactionStatus);
			throw (T) e;
		}
		finally
		{
			ConnectionIdHolder.clearConnectionId();
		}
		return object;
	}

}
