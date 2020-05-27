/**
 * 
 */
package com.zaprit.scope.bo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;

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
public abstract class UserScopeEntity extends ScopeEntity
{
	private String			createdBy	= null;
	private LocalDateTime	createdOn	= null;
	private String			modifiedBy	= null;
	private LocalDateTime	modifiedOn	= null;

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
