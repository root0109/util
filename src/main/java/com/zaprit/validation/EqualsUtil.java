/**
 * 
 */
package com.zaprit.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author vaibhav.singh
 */
public final class EqualsUtil
{
	private EqualsUtil() throws IllegalAccessException
	{
		throw new IllegalArgumentException("Object cannot be created");
	}

	/**
	 * @param list1
	 * @param list2
	 * @return true or false
	 */
	public static <T> boolean isEqualList(List<T> list1, List<T> list2)
	{
		boolean matched = true;
		if (!FieldUtil.isSameRef(list1, list2))
		{
			if (!ObjectUtils.allNotNull(list1, list2) || list1.size() != list2.size())
			{
				matched = false;
			}
			else
			{
				List<T> tempList = new ArrayList<>(list2);
				for (Object obj : list1)
				{
					if (!tempList.remove(obj))
					{
						matched = false;
						break;
					}
				}
			}
		}
		return matched;
	}

	/**
	 * @param set1
	 * @param set2
	 * @return true or false
	 */
	public static <T> boolean isEqualSet(Set<T> set1, Set<T> set2)
	{
		boolean matched = true;
		if (!FieldUtil.isSameRef(set1, set2))
		{
			if (!ObjectUtils.allNotNull(set1, set2) || set1.size() != set2.size())
			{
				matched = false;
			}
			else
			{
				Set<T> tempSet = new HashSet<>(set2);
				for (Object obj : set1)
				{
					if (!tempSet.remove(obj))
					{
						matched = false;
						break;
					}
				}
			}
		}
		return matched;
	}

	/**
	 * @param map1
	 * @param map2
	 * @return true or false
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K, V> boolean isEqualMap(Map<K, V> map1, Map<K, V> map2)
	{
		boolean matched = true;
		if (!FieldUtil.isSameRef(map1, map2))
		{
			if (!ObjectUtils.allNotNull(map1, map2) || map1.size() != map2.size())
			{
				matched = false;
			}
			else
			{
				for (Entry<K, V> entry : (Set<Entry<K, V>>) map1.entrySet())
				{
					if (!matched)
					{
						break;
					}
					V val1 = entry.getValue();
					V val2 = map2.get(entry.getKey());
					if (val1 instanceof List)
					{
						matched = isEqualList((List) val1, (List) val2);
					}
					else if (val1 instanceof Set)
					{
						matched = isEqualSet((Set) val1, (Set) val2);
					}
					else if (val1 instanceof Map)
					{
						matched = isEqualMap((Map) val1, (Map) val2);
					}
					//val1 can be null , hence instanceOf check is important 
					else if (val1 instanceof Object)
					{
						matched = val1.equals(val2);
					}
					else
					{
						matched = false;
					}
				}
			}
		}
		return matched;
	}
}
