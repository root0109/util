package com.zaprit.validation;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormat.Field;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.ObjectUtils;

import com.zaprit.search.bo.Page;
import com.zaprit.search.bo.Page.Sort;

/**
 * @author vaibhav.singh
 */
public final class SQLUtil
{
	private SQLUtil()
	{
		throw new IllegalArgumentException("This is a util and should be used as one.");
	}

	public static boolean isLessThanSystemDate(long longDate)
	{
		return Instant.ofEpochMilli(longDate).atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now());
	}

	private static Calendar clearTimes(Calendar calendar)
	{
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	/**
	 * @param val
	 * @return String
	 */
	public static String convertSimpleDayFormat(long val)
	{
		Calendar today = Calendar.getInstance();
		today = clearTimes(today);
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);
		yesterday = clearTimes(yesterday);
		Calendar last7days = Calendar.getInstance();
		last7days.add(Calendar.DAY_OF_YEAR, -7);
		last7days = clearTimes(last7days);
		Calendar last30days = Calendar.getInstance();
		last30days.add(Calendar.DAY_OF_YEAR, -30);
		last30days = clearTimes(last30days);
		if (val > today.getTimeInMillis())
		{
			return "Today";
		}
		else if (val > yesterday.getTimeInMillis())
		{
			return "Yesterday";
		}
		else if (val > last7days.getTimeInMillis())
		{
			return "Last 7 days";
		}
		else if (val > last30days.getTimeInMillis())
		{
			return "Last 30 days";
		}
		else
		{
			return "Morethan 30 days";
		}
	}

	public static LocalDateTime millsToLocalDateTime(long millis)
	{
		Instant instant = Instant.ofEpochMilli(millis);
		return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * @param format
	 * @param date
	 * @param timeZone
	 * @return String
	 */
	public static String formatUtilDate(String format, TimeZone timeZone, Date date)
	{
		DateFormat dateFormat = new SimpleDateFormat(format);
		if (timeZone != null)
		{
			dateFormat.setTimeZone(timeZone);
		}
		return dateFormat.format(date);
	}

	/**
	 * @param format
	 * @param locale
	 * @param date
	 * @param timeZone
	 * @return String
	 */
	public static String formatUtilDate(String format, Locale locale, TimeZone timeZone, Date date)
	{
		DateFormat dateFormat = new SimpleDateFormat(format, locale);
		if (timeZone != null)
		{
			dateFormat.setTimeZone(timeZone);
		}
		return dateFormat.format(date);
	}

	/**
	 * @param format
	 * @param date
	 * @param timeZone
	 * @return String
	 */
	public static String formatUtilDateTime(String format, TimeZone timeZone, Date date)
	{
		DateFormat dateFormat = new SimpleDateFormat(format + " HH:mm aa");
		if (timeZone != null)
		{
			dateFormat.setTimeZone(timeZone);
		}
		return dateFormat.format(date);
	}

	/**
	 * @param format
	 * @param locale
	 * @param date
	 * @param timeZone
	 * @return String
	 */
	public static String formatUtilDateTime(String format, Locale locale, TimeZone timeZone, Date date)
	{
		DateFormat dateFormat = new SimpleDateFormat(format + " HH:mm aa", locale);
		if (timeZone != null)
		{
			dateFormat.setTimeZone(timeZone);
		}
		return dateFormat.format(date);
	}

	/**
	 * 
	 * @param format
	 * @param date
	 * @param userTimezone
	 * @return converts a given Date which is in GMT to user TimeZone
	 */
	public static String convertUtilDateFromGMT(String format, TimeZone userTimezone, Date date)
	{
		DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(userTimezone);
		return dateFormat.format(date);
	}

	/**
	 * @param format
	 * @param date
	 * @return converts a given date to GMT
	 */
	public static String convertUtilDateToGMT(String format, Date date)
	{
		DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(date);
	}

	/**
	 * @param format
	 * @param date
	 * @param timeZone
	 * @return String
	 */
	public static String formatLocalDate(String format, TimeZone timeZone, LocalDate date)
	{
		DateFormat dateFormat = new SimpleDateFormat(format);
		if (timeZone != null)
		{
			dateFormat.setTimeZone(timeZone);
		}
		return dateFormat.format(date);
	}

	/**
	 * @param format
	 * @param locale
	 * @param date
	 * @param timeZone
	 * @return String
	 */
	public static String formatLocalDate(String format, Locale locale, TimeZone timeZone, LocalDate date)
	{
		DateFormat dateFormat = new SimpleDateFormat(format, locale);
		if (timeZone != null)
		{
			dateFormat.setTimeZone(timeZone);
		}
		return dateFormat.format(date);
	}

	/**
	 * @param format
	 * @param date
	 * @param timeZone
	 * @return String
	 */
	public static String formatLocalDateTime(String format, TimeZone timeZone, LocalDate date)
	{
		DateFormat dateFormat = new SimpleDateFormat(format + " HH:mm aa");
		if (timeZone != null)
		{
			dateFormat.setTimeZone(timeZone);
		}
		return dateFormat.format(date);
	}

	/**
	 * @param format
	 * @param locale
	 * @param date
	 * @param timeZone
	 * @return String
	 */
	public static String formatLocalDateTime(String format, Locale locale, TimeZone timeZone, LocalDate date)
	{
		DateFormat dateFormat = new SimpleDateFormat(format + " HH:mm aa", locale);
		if (timeZone != null)
		{
			dateFormat.setTimeZone(timeZone);
		}
		return dateFormat.format(date);
	}

	/**
	 * 
	 * @param format
	 * @param date
	 * @param userTimezone
	 * @return converts a given Date which is in GMT to user TimeZone
	 */
	public static String convertLocalDateFromGMT(String format, TimeZone userTimezone, LocalDate date)
	{
		DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(userTimezone);
		return dateFormat.format(date);
	}

	/**
	 * @param format
	 * @param date
	 * @return converts a given date to GMT
	 */
	public static String convertLocalDateToGMT(String format, LocalDate date)
	{
		DateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(date);
	}

	/**
	 * @param java.util.date
	 * @return a LocalDate object
	 */
	public static LocalDate getLocalDateFromDate(Date date)
	{
		return LocalDate.from(Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()));
	}

	/**
	 * @param java.util.date
	 * @return a LocalDate object
	 */
	public static LocalDateTime getLocalDateTimeFromDate(java.sql.Date date)
	{
		return new Timestamp(date.getTime()).toLocalDateTime();
	}

	/**
	 * @param java.util.date
	 * @return a LocalDate object
	 */
	public static LocalDateTime getLocalDateTimeFromStamp(java.sql.Timestamp timestamp)
	{
		return timestamp.toLocalDateTime();
	}

	/**
	 * @param LocalDate
	 * @return a Date object
	 */
	public static Date getDateFromLocalDate(LocalDate localDate)
	{
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * @param LocalDate
	 * @return a Date object
	 */
	public static java.sql.Date getSqlDateFromLocalDate(LocalDate localDate)
	{
		return java.sql.Date.valueOf(localDate);
	}

	/**
	 * @param currentDate
	 * @return Date
	 */
	public static LocalDate getPreviousSaturdayDate(LocalDate currentDate)
	{

		if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY)
		{
			return currentDate;
		}
		return currentDate.with(TemporalAdjusters.previous(DayOfWeek.SATURDAY));

	}

	public static LocalDate getNextBusinessDate(LocalDate inputDate)
	{

		if (inputDate.getDayOfWeek() == DayOfWeek.FRIDAY)
		{
			return inputDate.plusDays(3);
		}
		if (inputDate.getDayOfWeek() == DayOfWeek.SATURDAY)
		{
			return inputDate.plusDays(2);
		}
		return inputDate.plusDays(1);

	}

	/**
	 * @param currentDate
	 * @return Date
	 */
	public static Date getPreviousSaturdayDate(Date currentDate)
	{
		Calendar inputDateCalendar = Calendar.getInstance();
		inputDateCalendar.setTime(currentDate);
		int day = inputDateCalendar.get(Field.DAY_OF_WEEK.getCalendarField());
		Date returnDate = null;
		if (day == Calendar.SATURDAY)
		{
			returnDate = new Date(inputDateCalendar.getTimeInMillis());
		}
		else
		{
			inputDateCalendar.add(Calendar.DATE, -day);
			returnDate = new Date(inputDateCalendar.getTimeInMillis());
		}
		return returnDate;
	}

	public static Date getNextBusinessDate(Date inputDate)
	{
		Calendar inputDateCalendar = Calendar.getInstance();
		inputDateCalendar.setTime(inputDate);
		int day = inputDateCalendar.get(Field.DAY_OF_WEEK.getCalendarField());
		if (day == Calendar.SATURDAY)
		{
			inputDateCalendar.add(Calendar.DATE, 2);
		}
		else if (day == Calendar.SUNDAY)
		{
			inputDateCalendar.add(Calendar.DATE, 1);
		}
		return new Date(inputDateCalendar.getTimeInMillis());
	}

	public static Timestamp getSQLTimestamp(Date specificDate)
	{
		long specificTime = specificDate.getTime();
		return new Timestamp(specificTime);
	}

	public static Timestamp getCurrentSQLTimestamp()
	{
		return new Timestamp(System.currentTimeMillis());
	}

	public static java.sql.Date getSQLDate(LocalDate localDate)
	{
		return localDate == null ? null : java.sql.Date.valueOf(localDate);
	}

	public static Date getSQLDate(LocalDateTime localDate)
	{
		return localDate == null ? null : getSQLDate(localDate.toLocalDate());
	}

	public static Timestamp getSQLTimeStamp(LocalDateTime localDateTime)
	{
		return localDateTime == null ? null : Timestamp.valueOf(localDateTime);
	}

	public static Timestamp getSQLTimestamp(LocalDate specificDate)
	{
		return specificDate == null ? null : Timestamp.valueOf(specificDate.atStartOfDay());
	}

	/**
	 * @param sql         SQL text string to append.
	 * @param sortColumns
	 * @return Sorting clause added in the provided sql
	 */
	public static StringBuilder addSorting(StringBuilder sql, List<Sort> sortColumns)
	{
		if (!ObjectUtils.isEmpty(sortColumns))
		{
			sql.append(" ORDER BY ");

			for (Sort sortInfo : sortColumns)
			{
				sql.append(sortInfo.getProperty()).append(" ").append(sortInfo.getOrder());

				if (sortInfo.getNullHandling() != null)
				{
					sql.append(" ").append("NULL ");

					switch (sortInfo.getNullHandling())
					{
						case NULLS_FIRST:
							sql.append("FIRST");
							break;
						case NULLS_LAST:
							sql.append("LAST");
							break;
					}
				}
				sql.append(", ");
			}

			sql.delete(sql.length() - 2, sql.length());
		}
		return sql;
	}

	/**
	 * @param sql
	 * @param page object instance.
	 * @return page clause along with sorting clause added into the sql
	 */
	public static StringBuilder addPaging(StringBuilder sql, Page page)
	{
		switch (page.getDbProvider())
		{
			case ORACLE:
				if (!ObjectUtils.isEmpty(page.getSortColumns()))
					addSorting(sql, page.getSortColumns());

				sql.append(" LIMIT ").append(page.getLimit()).append(" OFFSET ").append(page.getOffset());
				break;
			case MYSQL:

				break;
			default:
				// Do Nothing
		}

		return sql;
	}
}
