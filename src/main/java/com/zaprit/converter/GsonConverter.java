/**
 * 
 */
package com.zaprit.converter;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * @author vaibhav.singh
 *
 */
public final class GsonConverter
{
	private GsonConverter()
	{
		throw new IllegalArgumentException("This class can not be instantiated");
	}

	private static final Gson gson = new GsonBuilder().serializeNulls().create();

	public static <K, V> Map<K, V> convertMapfromJson(String input)
	{
		return gson.fromJson(input, new TypeToken<HashMap<K, V>>() {}.getType());
	}

	public static <K, Y> String toJson(Map<K, Y> inputMap)
	{
		return gson.toJson(inputMap);
	}
}
