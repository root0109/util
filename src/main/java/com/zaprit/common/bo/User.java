/**
 * 
 */
package com.zaprit.common.bo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;

import com.zaprit.scope.bo.StateScopeEntity;
import com.zaprit.validation.SerializationUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vaibhav.singh
 *
 */
@Getter
@Setter
@ToString
public class User extends StateScopeEntity
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8764140438582038367L;
	private UUID				userId;
	private String				firstName;
	private String				lastName;
	private String				username;
	private String				displayName;
	private String				email;
	private boolean				enabled;

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
