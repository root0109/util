/**
 * 
 */
package com.zaprit.collections;

import java.io.Externalizable;
import java.io.IOException;

/**
 * @author vaibhav.singh
 */
public interface PerstQueueSerializer<V extends Externalizable>
{
	public byte[] objectToBytes(V writable) throws IOException;

	public V bytesToObject(byte[] bytes) throws IOException;
}
