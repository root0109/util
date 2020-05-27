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
import lombok.ToString;

/**
 * @author vaibhav
 *
 */
@Getter
@Setter
@ToString
public class Attachment extends StateScopeEntity
{
	/**
	 * TYPE_FILE
	 */
	public static final int	TYPE_FILE			= 10;
	/**
	 * TYPE_WEB
	 */
	public static final int	TYPE_WEB			= 20;
	/**
	 * VISIBILITY_PRIVATE
	 */
	public static final int	VISIBILITY_PRIVATE	= 10;
	/**
	 * VISIBILITY_PUBLIC
	 */
	public static final int	VISIBILITY_PUBLIC	= 20;
	private String			attachmentId		= null;
	private String			name				= null;
	private String			encoding			= "UTF-8";
	private long			fileSize			= 0;
	private String			path				= null;
	private int				type				= TYPE_FILE;
	private int				visibility			= VISIBILITY_PUBLIC;
	private String			comments			= null;

	/**
	 * @return Exact New Copy of this Attachment
	 */
	public Attachment copy()
	{
		Attachment attachment = new Attachment();
		attachment.setScopeId(this.getScopeId());
		attachment.setAttachmentId(this.attachmentId);
		attachment.setName(this.name);
		attachment.setEncoding(this.encoding);
		attachment.setFileSize(this.fileSize);
		attachment.setPath(path);
		attachment.setType(this.getType());
		attachment.setVisibility(this.visibility);
		attachment.setComments(this.comments);
		attachment.setStatus(this.getStatus());
		attachment.setStatusComments(this.getStatusComments());
		attachment.setCreatedBy(this.getCreatedBy());
		attachment.setCreatedOn(this.getCreatedOn());
		attachment.setArchive(this.isArchive());
		attachment.setError(this.isError());
		attachment.setErrorReasons(this.getErrorReasons());
		return attachment;
	}

	/**
	 * This provides the fileExtension for the name of the file
	 * 
	 * @return
	 */
	public String getFileExtension()
	{
		if (this.name != null)
		{
			int indexOfDot = this.name.lastIndexOf('.');
			if (indexOfDot != -1)
			{
				return this.name.substring(indexOfDot + 1, name.length());
			}
		}
		return "";
	}

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
