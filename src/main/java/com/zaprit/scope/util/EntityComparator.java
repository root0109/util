/**
 * 
 */
package com.zaprit.scope.util;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author vaibhav.singh
 */
public final class EntityComparator
{

	private EntityComparator()
	{}

	/**
	 * @param fields
	 * @param type
	 */
	public static void getAllFields(List<Field> fields, Class<?> type)
	{
		for (Field field : type.getDeclaredFields())
		{
			fields.add(field);
		}
		if (type.getSuperclass() != null)
		{
			getAllFields(fields, type.getSuperclass());
		}
	}

	/**
	 * @param obj1
	 * @param obj2
	 * @return true if same else false
	 */
	public static boolean compareObject(Object obj1, Object obj2)
	{
		boolean same = true;
		try
		{
			List<Field> fields = new ArrayList<>();
			getAllFields(fields, obj1.getClass());
			for (Field field : fields)
			{
				if (!compareField(field, obj1, obj2))
				{
					same = false;
					break;
				}
			}
		}
		catch (Exception e)
		{
			same = false;
		}
		return same;
	}

	private static boolean compareField(Field field, Object obj1, Object obj2) throws IllegalAccessException
	{
		boolean same = true;
		field.setAccessible(true);
		if (!field.isAnnotationPresent(EntityCompareIgnore.class))
		{
			same = compareValue(field.get(obj1), field.get(obj2));
		}
		return same;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean compareValue(Object val1, Object val2)
	{
		if (val1 == val2)
		{
			return true;
		}
		boolean same = true;
		if (val1 != null && val2 != null)
		{
			if (val1 instanceof String)
			{
				same = val1.equals(val2);
			}
			else if (val1 instanceof Number)
			{
				same = val1.equals(val2);
			}
			else if (val1 instanceof Boolean)
			{
				same = val1.equals(val2);
			}
			else if (val1 instanceof Character)
			{
				same = val1.equals(val2);
			}
			else if (val1 instanceof Date)
			{
				if (((Date) val1).compareTo((Date) val2) != 0)
				{
					same = false;
				}
			}
			else if (val1 instanceof LocalDate)
			{
				if (((LocalDate) val1).compareTo((LocalDate) val2) != 0)
				{
					same = false;
				}
			}
			else if (val1 instanceof LocalDateTime)
			{
				if (((LocalDateTime) val1).compareTo((LocalDateTime) val2) != 0)
				{
					same = false;
				}
			}
			else if (val1 instanceof Currency)
			{
				same = ((Currency) val1).getCurrencyCode().equals(((Currency) val2).getCurrencyCode());

			}
			else if (val1 instanceof Enum)
			{
				same = val1.equals(val2);
			}
			else if (val1 instanceof List)
			{
				List list1 = (List) val1;
				List list2 = (List) val2;
				if (list1.size() != list2.size())
				{
					same = false;
				}
				else
				{
					for (int index = 0; index < list1.size(); index++)
					{
						same = compareValue(list1.get(index), list2.get(index));
						if (!same)
						{
							break;
						}
					}
				}
			}
			else if (val1 instanceof Set)
			{
				Set set1 = (Set) val1;
				Set set2 = (Set) val2;
				if (set1.size() != set2.size())
				{
					same = false;
				}
				else
				{
					for (Object setObj : set1)
					{
						same = set2.contains(setObj);
						if (!same)
						{
							break;
						}
					}
				}
			}
			else if (val1 instanceof Map)
			{
				Map map1 = (Map) val1;
				Map map2 = (Map) val2;
				if (map1.size() != map2.size())
				{
					same = false;
				}
				else
				{
					for (Entry entry : (Set<Entry>) map1.entrySet())
					{
						same = compareValue(entry.getValue(), map2.get(entry.getKey()));
						if (!same)
						{
							break;
						}
					}
				}
			}
			else
			{
				same = compareObject(val1, val2);
				if (!same)
				{
					return same;
				}
			}
		}
		else
		{
			same = false;
		}
		return same;
	}
}
