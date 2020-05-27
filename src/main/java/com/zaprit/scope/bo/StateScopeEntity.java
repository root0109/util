/**
 * 
 */
package com.zaprit.scope.bo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

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
public abstract class StateScopeEntity extends UserScopeEntity
{
	private int					status			= -1;
	private String				statusComments	= null;
	private boolean				archive			= false;
	private boolean				error			= false;
	private Map<String, String>	errorReasons	= new HashMap<>();

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
