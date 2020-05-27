/**
 * 
 */
package com.zaprit.validation;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Dictionary;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zaprit.collections.ArrayDictionary;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 *
 */
@Slf4j
public final class SerializationUtil
{
	private SerializationUtil()
	{
		throw new IllegalArgumentException("This is a util and should be used as one.");
	}

	/**
	 * @param out
	 * @throws IOException
	 */
	public static void serialize(Object obj, ObjectOutput out) throws IOException
	{
		Dictionary<String, Object> serializedMap = new ArrayDictionary<>();
		for (Field field : obj.getClass().getDeclaredFields())
		{
			String name = null;
			try
			{
				name = field.getName();
				serializedMap.put(name, callGetter(obj, name));
			}
			catch (Exception e)
			{
				log.error("Unable to serialize this field :: " + name, e);
			}
		}
		out.writeObject(serializedMap);
	}

	@SuppressWarnings("unchecked")
	public static void deserialize(Object obj, ObjectInput in) throws IOException, ClassNotFoundException
	{
		Dictionary<String, Object> deserializedMap = (ArrayDictionary<String, Object>) in.readObject();
		for (Field field : obj.getClass().getDeclaredFields())
		{
			String name = null;
			Object value = null;
			try
			{
				name = field.getName();
				value = deserializedMap.get(name);
				callSetter(obj, name, value);
			}
			catch (Exception e)
			{
				log.error("Unable to serialize this field :: " + name + " value :: " + value);
			}
		}
	}

	/**
	 * 
	 * @param values
	 * @return JsonObject
	 */
	public static JSONObject serializeMap(Map<String, String> values)
	{
		JSONObject jsonObject = new JSONObject();
		for (Entry<String, String> entry : values.entrySet())
		{
			jsonObject.put(entry.getKey(), entry.getValue());
		}
		return jsonObject;
	}

	/**
	 * 
	 * @param jsonObject
	 * @return LinkedHashMap<String, String>
	 */
	public static LinkedHashMap<String, String> deserializeMap(JSONObject jsonObject)
	{
		LinkedHashMap<String, String> values = new LinkedHashMap<>();
		for (String key : jsonObject.keySet())
		{
			values.put(key, jsonObject.getString(key));
		}
		return values;
	}

	/**
	 * 
	 * @param values
	 * @return JsonObject
	 */
	public static JSONArray serializeOptionsMap(Map<String, String> values)
	{
		JSONObject jsonObject = null;
		JSONArray jsonArray = new JSONArray();
		for (Entry<String, String> entry : values.entrySet())
		{
			jsonObject = new JSONObject();
			jsonObject.put("key", entry.getKey());
			jsonObject.put("value", entry.getValue());
			jsonArray.put(jsonObject);
		}
		return jsonArray;
	}

	/**
	 * 
	 * @param values
	 * @return JsonArray
	 */
	public static JSONObject serializeNestedMap(Map<String, Map<String, String>> values)
	{
		JSONObject jsonValue = null;
		JSONArray jsonArray = null;
		JSONObject jsonObject = new JSONObject();
		for (Entry<String, Map<String, String>> entry : values.entrySet())
		{
			jsonArray = new JSONArray();
			for (Entry<String, String> innerEntry : entry.getValue().entrySet())
			{
				jsonValue = new JSONObject();
				jsonValue.put(innerEntry.getKey(), innerEntry.getValue());
				jsonArray.put(jsonValue);
			}
			jsonObject.put(entry.getKey(), jsonArray);
		}
		return jsonObject;
	}

	/**
	 * 
	 * @param values
	 * @return JsonObject
	 */
	public static JSONObject serializeNestedOptionsMap(Map<String, Map<String, String>> values)
	{
		JSONObject jsonObject = new JSONObject();
		for (Entry<String, Map<String, String>> entry : values.entrySet())
		{
			jsonObject.put(entry.getKey(), serializeOptionsMap(entry.getValue()));
		}
		return jsonObject;
	}

	/**
	 * Sets a field value on a given object
	 *
	 * @param targetObject the object to set the field value on
	 * @param fieldName    exact name of the field
	 * @param fieldValue   value to set on the field
	 * @return true if the value was successfully set, false otherwise
	 * @throws ClassNotFoundException 
	 */
	public static boolean setField(Object targetObject, String fieldName, Object fieldValue)
	{
		Field field;
		try
		{
			field = targetObject.getClass().getDeclaredField(fieldName);
		}
		catch (NoSuchFieldException e)
		{
			field = null;
		}
		Class<?> superClass = targetObject.getClass().getSuperclass();
		while (field == null && superClass != null)
		{
			try
			{
				field = superClass.getDeclaredField(fieldName);
			}
			catch (NoSuchFieldException e)
			{
				superClass = superClass.getSuperclass();
			}
		}
		if (field == null)
		{
			return false;
		}
		field.setAccessible(true);
		try
		{
			field.set(targetObject, fieldValue);
			return true;
		}
		catch (IllegalAccessException e)
		{
			return false;
		}
	}

	private static void callSetter(Object obj, String fieldName, Object value)
	{
		PropertyDescriptor pd;
		try
		{
			pd = new PropertyDescriptor(fieldName, obj.getClass());
			pd.getWriteMethod().invoke(obj, value);
		}
		catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			log.error("Error in calling setter " + fieldName, e);
		}
	}

	private static Object callGetter(Object obj, String fieldName)
	{
		Object object = null;
		try
		{
			object = new PropertyDescriptor(fieldName, obj.getClass()).getReadMethod().invoke(obj);
		}
		catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			log.error("Error in calling Getter " + fieldName, e);
		}
		return object;
	}
}
