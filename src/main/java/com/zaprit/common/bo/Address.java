/**
 * 
 */
package com.zaprit.common.bo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.zaprit.scope.bo.StateScopeEntity;
import com.zaprit.validation.SerializationUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * @author vaibhav.singh
 *
 */
@Getter
@Setter
public class Address extends StateScopeEntity
{
	private String	id		= null;
	private String	name	= null;
	private String	line1	= null;
	private String	line2	= null;
	private String	line3	= null;
	private String	street	= null;
	private String	city	= null;
	private String	state	= null;
	private String	country	= null;
	private String	zip		= null;

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
