/**
 * 
 */
package com.zaprit.common.bo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vaibhav.singh
 */
@Getter
@Setter
@ToString
public class ApplicationEmail implements Externalizable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID		= 684426804359651812L;
	private String				hostName				= null;
	private String				bounceEmailAddress		= null;
	private String				fromName				= null;
	private String				fromAddress				= null;
	private String				subject					= null;
	private String				message					= null;
	private int					smtpPort				= 25;
	private Date				sentDate				= new Date();
	private Set<String>			toRecipients			= new HashSet<>();
	private Set<String>			ccRecipients			= new HashSet<>();
	private Set<String>			bccRecipients			= new HashSet<>();
	private Set<String>			replyToRecipients		= new HashSet<>();
	private List<Attachment>	attachments				= new ArrayList<>();
	private List<Attachment>	endOfMailAttachments	= new ArrayList<>();

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		Map<String, Object> serializedMap = new HashMap<>();
		serializedMap.put("1", this.getHostName());
		serializedMap.put("2", this.getBounceEmailAddress());
		serializedMap.put("3", this.getFromName());
		serializedMap.put("4", this.getFromAddress());
		serializedMap.put("5", this.getSubject());
		serializedMap.put("6", this.getMessage());
		serializedMap.put("7", this.getSmtpPort());
		serializedMap.put("8", this.getSentDate());
		serializedMap.put("9", this.toRecipients);
		serializedMap.put("10", this.getCcRecipients());
		serializedMap.put("11", this.getBccRecipients());
		serializedMap.put("12", this.getReplyToRecipients());
		serializedMap.put("13", this.getAttachments());
		serializedMap.put("14", this.getEndOfMailAttachments());
		out.writeObject(serializedMap);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		Map<String, Object> deserializedMap = (Map<String, Object>) in.readObject();
		this.hostName = ((String) deserializedMap.get("1"));
		this.bounceEmailAddress = (String) deserializedMap.get("2");
		this.fromName = (String) deserializedMap.get("3");
		this.fromAddress = (String) deserializedMap.get("4");
		this.subject = (String) deserializedMap.get("5");
		this.message = (String) deserializedMap.get("6");
		this.smtpPort = (int) deserializedMap.get("7");
		this.sentDate = (Date) deserializedMap.get("8");
		this.toRecipients = (Set<String>) deserializedMap.get("9");
		this.ccRecipients = (Set<String>) deserializedMap.get("10");
		this.bccRecipients = (Set<String>) deserializedMap.get("11");
		this.replyToRecipients = (Set<String>) deserializedMap.get("12");
		this.attachments = (List<Attachment>) deserializedMap.get("13");
		this.endOfMailAttachments = (List<Attachment>) deserializedMap.get("14");
	}
}
