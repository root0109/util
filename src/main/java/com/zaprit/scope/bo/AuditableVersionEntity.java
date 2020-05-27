/**
 * 
 */
package com.zaprit.scope.bo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.zaprit.validation.SerializationUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * @author vaibhav.singh
 */
@Getter
@Setter
public class AuditableVersionEntity extends StateScopeEntity
{
	private int version = 1;

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		super.writeExternal(out);
		SerializationUtil.serialize(this, out);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		super.readExternal(in);
		SerializationUtil.deserialize(this, in);
	}
}
