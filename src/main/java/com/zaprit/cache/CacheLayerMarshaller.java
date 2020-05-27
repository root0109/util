/**
 * 
 */
package com.zaprit.cache;

/**
 * @author vaibhav.singh
 */
public final class CacheLayerMarshaller<I, O>
{
	private Class<I>	inputClazz;
	private Class<O>	outputClazz;

	public CacheLayerMarshaller(Class<I> iClazz, Class<O> oClazz)
	{
		this.inputClazz = iClazz;
		this.outputClazz = oClazz;
	}

	public Class<I> getInputClazz()
	{
		return inputClazz;
	}

	public Class<O> getOutputClazz()
	{
		return outputClazz;
	}
}
