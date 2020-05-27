/**
 * 
 */
package com.zaprit.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
public final class ControllableManager
{
	private static final String DEFAULT_STATUS = "NOT AVAILABLE";

	private static HashMap<String, Controllable> controllableMap = new HashMap<>();

	private ControllableManager()
	{}

	public static synchronized void registerControllable(Controllable controllable)
	{
		controllableMap.put(controllable.getIdentifier(), controllable);
	}

	public static synchronized void unregisterControllable(Controllable controllable)
	{
		controllableMap.remove(controllable.getIdentifier());
	}

	private static synchronized Set<Map.Entry<String, Controllable>> getPrioritySortedEntrySet()
	{
		TreeSet<Map.Entry<String, Controllable>> treeSet = new TreeSet<>((o1, o2) -> {
			if (o1.getValue().getShutdownPriority() < o2.getValue().getShutdownPriority())
				return -1;
			if (o1.getValue().getShutdownPriority() > o2.getValue().getShutdownPriority())
				return 1;
			return o1.getKey().compareTo(o2.getKey());
		});
		treeSet.addAll(controllableMap.entrySet());
		return treeSet;
	}

	public static synchronized void stopAll()
	{
		for (Map.Entry<String, Controllable> controllableEntry : getPrioritySortedEntrySet())
		{
			log.info("[STOPPING]: " + controllableEntry.getKey());
			controllableEntry.getValue().onStop();
		}
	}

	public static synchronized void stop(String identifier)
	{
		Controllable controllable = controllableMap.get(identifier);
		if (controllable != null)
		{
			log.info("STOPPING: " + identifier);
			controllable.onStop();
		}
	}

	public static synchronized void startAll()
	{
		List<Entry<String, Controllable>> reverseControllables = new ArrayList<>();
		reverseControllables.addAll(getPrioritySortedEntrySet());
		Collections.reverse(reverseControllables);
		for (Map.Entry<String, Controllable> controllableEntry : reverseControllables)
		{
			log.info("[STARTING]: " + controllableEntry.getKey());
			controllableEntry.getValue().onStart();
		}
	}

	public static synchronized void start(String identifier)
	{
		Controllable controllable = controllableMap.get(identifier);
		if (controllable != null)
		{
			log.info("[STARTING] : " + identifier);
			controllable.onStart();
		}
	}

	public static synchronized List<Map.Entry<String, String>> statusAll()
	{
		List<Map.Entry<String, String>> returnList = new LinkedList<>();
		for (final Map.Entry<String, Controllable> controllableEntry : getPrioritySortedEntrySet())
		{
			returnList.add(new Map.Entry<String, String>() {
				public String getKey()
				{
					return controllableEntry.getKey();
				}

				public String getValue()
				{
					return controllableEntry.getValue().getStatus();
				}

				public String setValue(String value)
				{
					return null;
				}
			});
		}
		return returnList;
	}

	public static synchronized String status(String identifier)
	{
		Controllable controllable = controllableMap.get(identifier);
		if (controllable != null)
		{
			return controllable.getStatus();
		}
		return DEFAULT_STATUS;
	}
}
