/**
 * 
 */
package com.zaprit.scope.bo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.zaprit.validation.SerializationUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vaibhav.singh
 */
@Getter
@Setter
@ToString
public abstract class ScopeEntity implements Externalizable
{
	private String scopeId = null;

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		SerializationUtil.serialize(this, out);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		SerializationUtil.deserialize(this, in);
	}
}
