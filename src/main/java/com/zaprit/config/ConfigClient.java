package com.zaprit.config;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
public final class ConfigClient
{
	private static volatile JMXConnector	connector		= null;
	private static Map<String, Properties>	propertyFileMap	= new ConcurrentHashMap<>();

	private ConfigClient()
	{}

	public static Properties getPropertiesForFile(String filename)
	{

		Properties properties = propertyFileMap.get(filename);
		if (properties == null)
		{
			synchronized (ConfigClient.class)
			{
				properties = propertyFileMap.get(filename);
				if (properties == null)
				{
					try
					{
						properties = addJMXPropertiesToMap(propertyFileMap, filename, ConfigClient.connector.getMBeanServerConnection());
					}
					catch (Exception e)
					{
						StringBuilder message = new StringBuilder("[JMX]");
						message.append(" Cannot obtain properties for file [").append(filename).append("]");
						log.error(message.toString(), e);
					}
				}
			}
		}
		return properties;
	}

	private static Properties addJMXPropertiesToMap(Map<String, Properties> propertyFileMap, String filename,
	                MBeanServerConnection mBeanServerConnection)
	                throws MalformedObjectNameException, InstanceNotFoundException, IntrospectionException, ReflectionException, IOException
	{
		Properties properties = new Properties();
		propertyFileMap.put(filename, properties);

		// Fetch Mbean from JMX server
		ObjectName objectName = new ObjectName(ConfigConstants.getUrlForJmxProperties(filename));
		MBeanInfo mBeanInfo = mBeanServerConnection.getMBeanInfo(objectName);

		// fetch properties from server
		List<String> listOfAttributes = new LinkedList<>();
		for (MBeanAttributeInfo attrInfo : mBeanInfo.getAttributes())
		{
			if (attrInfo.isReadable())
			{
				listOfAttributes.add(attrInfo.getName());
			}
		}

		String[] attributes = listOfAttributes.toArray(new String[0]);
		AttributeList attributeList = mBeanServerConnection.getAttributes(objectName, attributes);
		for (int i = 0; i < attributeList.size(); ++i)
		{
			Attribute attribute = (Attribute) attributeList.get(i);
			properties.setProperty(attribute.getName(), attribute.getValue().toString());
		}
		return properties;
	}

}
