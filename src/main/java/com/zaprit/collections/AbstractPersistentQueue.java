/**
 * 
 */
package com.zaprit.collections;

import java.io.Externalizable;

/**
 * @author vaibhav.singh
 */
public interface AbstractPersistentQueue<V extends Externalizable>
{
	/**
	 * Adds an object to the collection
	 * 
	 * @param object
	 *            to put
	 * @throws InterruptedException
	 */
	public void put(V object) throws InterruptedException;

	/**
	 * Gets the front most element of the collection
	 * 
	 * @return the first element
	 * @throws InterruptedException
	 */
	public V take() throws InterruptedException;

	/**
	 * Adds an object to the collection
	 * 
	 * @param object
	 *            to add
	 * @return success
	 */
	public boolean add(V object);

	/**
	 * Automatically removes all the elements from the collection. The collection
	 * will be empty after this call.
	 */
	public void clear();

	/**
	 * Gets the front most element of the collection NON-BLOCKING
	 * 
	 * @return the first element
	 */
	public V poll();

	/**
	 * Gets the size of the collection
	 * 
	 * @return size of list
	 */
	public int size();

	/**
	 * Loads the persisted tuples from disk
	 */
	public void open();

	/**
	 * Persists tuples to disk
	 */
	public void close();
}
