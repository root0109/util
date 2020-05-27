/**
 * 
 */
package com.zaprit.control;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.zaprit.validation.FieldUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
public final class ReconnectorManager
{
	private static final String DEFAULT_STATUS = "NOT AVAILABLE";

	private static HashMap<String, ReConnector> reconnectorResources = new HashMap<>();

	private static String projectName = "UNKNOWN";

	public enum RECONNECTION_MANAGER_EVENTS
	{
		DNS_FLUSH,
		RECONNECT_CLIENT,
		RECONNECT_ALL
	}

	public static synchronized void registerResource(ReConnector reConnectable)
	{
		log.debug("[ReConnector] REGISTERING: " + reConnectable.getIdentifier() + ", project: " + projectName);
		reconnectorResources.put(reConnectable.getIdentifier(), reConnectable);
	}

	public static synchronized void unregisterReConnectable(ReConnector reConnectable)
	{
		log.debug("[ReConnector] UN-REGISTERING: " + reConnectable.getIdentifier() + ", project: " + projectName);
		reconnectorResources.remove(reConnectable.getIdentifier());
	}

	private static synchronized Set<Map.Entry<String, ReConnector>> getPrioritySortedEntrySet()
	{
		TreeSet<Map.Entry<String, ReConnector>> treeSet = new TreeSet<>((o1, o2) -> {
			if (o1.getValue().getReConnectionPriority() < o2.getValue().getReConnectionPriority())
				return -1;
			if (o1.getValue().getReConnectionPriority() > o2.getValue().getReConnectionPriority())
				return 1;
			return o1.getKey().compareTo(o2.getKey());
		});
		treeSet.addAll(reconnectorResources.entrySet());
		return treeSet;
	}

	public static synchronized void reconnectAll()
	{
		for (Map.Entry<String, ReConnector> controllableEntry : getPrioritySortedEntrySet())
		{
			log.debug("[ReConnector] RECONNECTING: " + controllableEntry.getKey() + ", project: " + projectName);
			controllableEntry.getValue().reconnect();
		}
	}

	public static synchronized void reconnect(String identifier)
	{
		ReConnector reConnectable = reconnectorResources.get(identifier);
		if (reConnectable != null)
		{
			log.debug("[ReConnector] RECONNECTING: " + identifier + ", project: " + projectName);
			reConnectable.reconnect();
		}
	}

	public static synchronized List<Map.Entry<String, String>> statusAll()
	{
		List<Map.Entry<String, String>> returnList = new LinkedList<>();
		for (final Map.Entry<String, ReConnector> reconnectableResource : getPrioritySortedEntrySet())
		{
			returnList.add(new Map.Entry<String, String>() {
				public String getKey()
				{
					return reconnectableResource.getKey();
				}

				public String getValue()
				{
					return reconnectableResource.getValue().getStatus();
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
		ReConnector reConnector = reconnectorResources.get(identifier);
		if (reConnector != null)
		{
			return reConnector.getStatus();
		}
		return DEFAULT_STATUS;
	}

	public static void flushDNS()
	{
		flushDNSFor(null);
	}

	@SuppressWarnings("rawtypes")
	public static void flushDNSFor(String hostName)
	{
		try
		{
			Class<InetAddress> klass = InetAddress.class;
			Field acf = klass.getDeclaredField("addressCache");
			acf.setAccessible(true);
			Object addressCache = acf.get(null);
			Class<?> cacheKlass = addressCache.getClass();
			Field cf = cacheKlass.getDeclaredField("cache");
			cf.setAccessible(true);
			LinkedHashMap map = (LinkedHashMap) cf.get(addressCache);

			if (!FieldUtil.isBlank(hostName))
			{
				map.remove(hostName);
				log.debug("[ReConnector] Flushed DNS Entry for host: " + hostName + ", project: " + projectName);
			}
			else
			{
				map.clear();
				log.debug("[ReConnector] Flushed All DNS Entries" + ", project: " + projectName);
			}
		}
		catch (Exception e)
		{
			if (!FieldUtil.isBlank(hostName))
			{
				log.error("[ReConnector] Could not flush DNS Entry for host: " + hostName + ", project: " + projectName, e);
			}
			else
			{
				log.error("[ReConnector] Could not flush All DNS Entries for project: " + projectName, e);
			}
		}
	}
}