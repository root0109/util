/**
 * 
 */
package com.zaprit.scope.web.converter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.zaprit.config.ConfigConstants;
import com.zaprit.scope.web.service.bo.ServiceResponse;
import com.zaprit.validation.FieldUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
public class JsonResponseConverter implements ServiceResponseConverter
{
	private Gson				gson		= null;
	private static Properties	properties	= new Properties();

	static
	{
		// get this value from project.properties
		try (InputStream inputStream = JsonResponseConverter.class.getResourceAsStream(ConfigConstants.PROJECT_PROPERTIES))
		{
			properties.load(inputStream);
		}
		catch (IOException e)
		{
			log.error("Error in loading properties : ", e);
		}
	}

	public JsonResponseConverter()
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder = gsonBuilder.serializeNulls();
		gsonBuilder = gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
		gsonBuilder = gsonBuilder.registerTypeAdapter(Date.class, new DateSerializer());
		gsonBuilder = gsonBuilder.registerTypeAdapter(Currency.class, new CurrencySerializer());
		gsonBuilder = gsonBuilder.registerTypeAdapter(Timestamp.class, new DateSerializer());
		gson = gsonBuilder.create();
	}

	@Override
	public <K> String getResponseString(ServiceResponse<K> serviceResponse)
	{
		return gson.toJson(serviceResponse);
	}

	@Override
	public <K> String getResponseString(List<ServiceResponse<K>> serviceResponses)
	{
		return gson.toJson(serviceResponses);
	}

	@Override
	public void setResponseHeader(HttpServletResponse response)
	{
		response.setHeader("Content-type", "text/x-json;charset=UTF-8");
		response.setHeader("OnionCache-Control", "no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		Set<String> allowedOrigins = getAllowedOrigins();
		if (!FieldUtil.isCollectionEmpty(allowedOrigins))
		{
			for (String origin : allowedOrigins)
			{
				response.setHeader("Access-Control-Allow-Origin", origin);
			}
		}
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.setHeader("Access-Control-Allow-Headers",
		                "X-PINGOTHER, Origin, X-Requested-With, Content-Type, token, fkAssociateId, associateName, Accept");
		response.setHeader("Access-Control-Max-Age", "1728000");
	}

	private static class CurrencySerializer implements JsonSerializer<Currency>, JsonDeserializer<Currency>
	{
		@SuppressWarnings("unused")
		public Currency getInstance(Type type)
		{
			return Currency.getInstance("en-us");
		}

		@Override
		public JsonElement serialize(Currency currency, Type type, JsonSerializationContext context)
		{
			return new JsonPrimitive(currency.getCurrencyCode());
		}

		@Override
		public Currency deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
		{
			return Currency.getInstance(jsonElement.getAsString());
		}
	}

	private static class DateSerializer implements JsonSerializer<Date>, JsonDeserializer<Date>
	{
		@SuppressWarnings("unused")
		public Date createInstance(Type type)
		{
			return new Date();
		}

		@Override
		public JsonElement serialize(Date date, Type type, JsonSerializationContext context)
		{
			return new JsonPrimitive(date.getTime());
		}

		@Override
		public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
		{
			return new Date(jsonElement.getAsLong());
		}

	}

	private static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime>
	{
		@SuppressWarnings("unused")
		public Date createInstance(Type type)
		{
			return new Date();
		}

		@Override
		public JsonElement serialize(LocalDateTime date, Type type, JsonSerializationContext context)
		{
			return new JsonPrimitive(Timestamp.valueOf(date).getTime());
		}

		@Override
		public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
		{
			return LocalDateTime.ofInstant(Instant.ofEpochMilli(jsonElement.getAsLong()), ZoneId.systemDefault());
		}

	}

	private static Set<String> getAllowedOrigins()
	{
		Set<String> allowedOrigins = new HashSet<>();
		String value = properties.getProperty("allowed-origins");
		for (String entry : value.split(","))
		{
			allowedOrigins.add(entry);
		}
		return allowedOrigins;
	}
}