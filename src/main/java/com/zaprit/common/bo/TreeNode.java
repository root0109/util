package com.zaprit.common.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vaibhav.singh
 *
 */
public class TreeNode implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -880446582712170039L;
	private String				id					= null;
	private String				text				= null;
	private String				description			= null;
	private String				type				= null;
	private Object				node				= null;
	private List<TreeNode>		children			= new ArrayList<TreeNode>();

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the text
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text)
	{
		this.text = text;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * @return the node
	 */
	public Object getNode()
	{
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(Object node)
	{
		this.node = node;
	}

	/**
	 * @return the children
	 */
	public List<TreeNode> getChildren()
	{
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<TreeNode> children)
	{
		this.children = children;
	}

	/**
	 * 
	 * @return String
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
}
