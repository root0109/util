package com.zaprit.config;

public class ConfigConstants
{
	public static final String	CONFIG_URL		= "config.url";
	public static final String	CONFIG_USER		= "config.user";
	public static final String	CONFIG_PASSWD	= "config.passwd";

	public static final String	URL_LIST				= "url.properties";
	public static final String	CONNECTION_LIST			= "connection.properties";
	public static final String	REDIS_CACHE_PROPERTIES	= "rediscache.properties";
	public static final String	MQ_PROPERTIES			= "mq.properties";
	public static final String	FILE_PROPERTIES			= "file.properties";
	public static final String	PROJECT_PROPERTIES		= "project.properties";

	public static final String[] propertyFileNames = new String[] { URL_LIST, CONNECTION_LIST, REDIS_CACHE_PROPERTIES, MQ_PROPERTIES, FILE_PROPERTIES,
	                                                                PROJECT_PROPERTIES };

	private ConfigConstants()
	{}

	public static String[] getPropertyFileNames()
	{
		return propertyFileNames;
	}

	public static String getUrlForJmxProperties(String propertyFileName)
	{
		StringBuilder builder = new StringBuilder(25);
		builder.append("igp").append(".").append("config").append(":");
		builder.append("type").append("=").append("properties");
		return builder.toString();
	}
}
