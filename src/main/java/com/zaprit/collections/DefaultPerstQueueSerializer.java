/**
 * 
 */
package com.zaprit.collections;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author vaibhav.singh
 */
public class DefaultPerstQueueSerializer<V extends Externalizable> implements PerstQueueSerializer<V>
{
	@Override
	public byte[] objectToBytes(V obj) throws IOException
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream(256);
		ObjectOutputStream out = new ObjectOutputStream(bout);
		out.writeUTF(obj.getClass().getName());
		obj.writeExternal(out);
		out.close();
		return bout.toByteArray();
	}

	@Override
	@SuppressWarnings("unchecked")
	public V bytesToObject(byte[] bytes) throws IOException
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
		ObjectInputStream in = new ObjectInputStream(bin);
		String className = in.readUTF();
		V obj = null;
		try
		{
			obj = (V) Class.forName(className).getDeclaredConstructor().newInstance();
			obj.readExternal(in);
		}
		catch (Exception e)
		{
			throw new IOException("Unable to create an instance/read obj " + className);
		}
		return obj;
	}
}
