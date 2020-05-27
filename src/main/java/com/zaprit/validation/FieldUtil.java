/**
 * 
 */
package com.zaprit.validation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author vaibhav.singh
 */
public final class FieldUtil
{
	private static final char[] whiteSpace = { '\n', '\r', '\t' };

	private FieldUtil()
	{
		throw new IllegalArgumentException("Object cannot be created");
	}

	/**
	 * Checks if both the object refer to the same memory location
	 * @param obj1
	 * @param obj2
	 * @return success/failure
	 */
	public static boolean isSameRef(Object obj1, Object obj2)
	{
		return obj1 == obj2;
	}

	/**
	 * @param collection
	 * @return boolean
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isCollectionEmpty(Collection collection)
	{
		boolean result = true;
		if ((collection != null) && (!collection.isEmpty()))
		{
			result = false;
		}
		return result;
	}

	public static boolean isArrayEmpty(Object[] collection)
	{
		boolean result = true;
		if ((collection != null) && (collection.length > 0))
		{
			result = false;
		}
		return result;
	}

	public static boolean isBlank(String str)
	{
		boolean result = false;
		if ((str == null) || (str.trim().equals("")))
		{
			result = true;
		}
		return result;
	}

	public static boolean isMailHostValid(String mailHost)
	{
		boolean result = false;
		String regex = "^(([a-z]|[a-z0-9][a-z0-9]*[a-z0-9])[\\.|\\-])*([a-z]|[a-z0-9][a-z0-9]*[a-z0-9])$";
		Pattern pattern = Pattern.compile(regex);
		if (null == mailHost)
		{
			result = true;
		}
		Matcher matcher = pattern.matcher(mailHost);
		if (matcher.matches())
		{
			result = true;
		}
		return result;
	}

	public static boolean inRange(String str, int min, int max)
	{
		boolean result = false;
		if ((str.length() >= min) && (str.length() <= max))
		{
			result = true;
		}
		return result;
	}

	public static boolean isNumber(String str)
	{
		try
		{
			Long.parseLong(str);
		}
		catch (NumberFormatException excp)
		{
			return false;
		}
		return true;
	}

	public static String getString(String str, int length)
	{
		if ((str != null) && (str.length() > length))
		{
			return str.substring(0, length);
		}
		return str;
	}

	public static boolean isZipCodeValid(String zipCode)
	{
		if (isBlank(zipCode))
		{
			return false;
		}
		String zipCodePattern1 = "\\d{5}-\\d{4}";
		String zipCodePattern2 = "[a-zA-Z\\d]*";
		if (zipCode.length() == 10)
		{
			if (!zipCode.matches(zipCodePattern1))
			{
				return false;
			}
		}
		else if (zipCode.length() < 10)
		{
			if (!zipCode.matches(zipCodePattern2))
			{
				return false;
			}
		}
		else
		{
			return false;
		}
		return true;
	}

	public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string)
	{
		return Enum.valueOf(c, string.trim());
	}

	public static boolean isEmailAddressCorrect(String emailAddress)
	{
		String regexInvalidCharacter = "([^\\\";.:\\[\\]<>()@,\\\\])";
		String regexValidCharacter = "[[\\p{L}{M}\\*]\\p{Digit}[\\p{Punct}&&[^;:\\\"\\[\\]<>(),@\\\\]]]*";
		String regexDomainPart = "@([\\w\\-]+\\.)+[A-Za-z]{2,4}$";
		String regex = regexInvalidCharacter + regexValidCharacter + regexInvalidCharacter + regexDomainPart;
		if (!emailAddress.contains("@"))
		{
			return false;
		}
		if (isSpace(emailAddress))
		{
			return false;
		}
		String localPart = emailAddress.substring(0, emailAddress.indexOf('@'));
		if ((localPart != null) && (localPart.length() > 0))
		{
			if (localPart.length() == 1)
			{
				regex = regexValidCharacter + regexDomainPart;
			}
			else if (localPart.length() == 2)
			{
				regex = regexValidCharacter + regexInvalidCharacter + regexDomainPart;
			}
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(emailAddress);
		return matcher.matches() && emailAddress.matches("[^.]++(?:.[^.]++)*+");
	}

	public static String removeSpecialCharacters(String str)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); i++)
		{
			if (((str.charAt(i) >= '0') && (str.charAt(i) <= '9')) || ((str.charAt(i) >= 'A') && (str.charAt(i) <= 'z')) || (str.charAt(i) == '.')
			    || (str.charAt(i) == '_'))
			{
				sb.append(str.charAt(i));
			}
		}
		return sb.toString();
	}

	public static boolean isSpace(String parameter)
	{
		return parameter.contains(" ");
	}

	/**
	 * Removes the white spaces from a String
	 *
	 * @param source
	 * @return String
	 */
	public static String stripWhiteSpaces(String source)
	{
		if (source == null)
		{
			return null;
		}
		String strReturn = source;
		for (int i = 0; i < whiteSpace.length; i++)
		{
			strReturn = strReturn.replace(whiteSpace[i], ' ');
		}
		while ((strReturn.indexOf("  ")) != -1)
		{
			strReturn = strReturn.replaceAll("  ", " ");
		}
		return strReturn;
	}

	public static boolean compareIps(String ip1, String ip2)
	{
		String[] ip1Array = ip1.trim().split("\\.");
		String[] ip2Array = ip2.trim().split("\\.");
		for (int i = 0; i < ip2Array.length; i++)
		{
			if (ip1Array[i].equals("*"))
			{
				return true;
			}
			if (!ip1Array[i].equals(ip2Array[i]))
			{
				return false;
			}
		}
		return true;
	}

	public static boolean validateIPAddress(String ip)
	{
		Pattern pattern = Pattern.compile(
		                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	/**
	 * 
	 * @param format
	 * @param currency
	 * @param number
	 * @return String
	 */
	public static String formatCurrency(String format, Currency currency, double number)
	{
		NumberFormat numberFormat = new DecimalFormat(format);
		/*
		 * String symbol = currency.getSymbol(); symbol = symbol.length() == 1 ? symbol
		 * : symbol + " ";
		 */
		return currency.getCurrencyCode() + " " + numberFormat.format(number);
	}

	/**
	 * 
	 * @param format
	 * @param currency
	 * @param number
	 * @return String
	 */
	public static String formatCurrencySymbol(String format, Currency currency, double number)
	{
		NumberFormat numberFormat = new DecimalFormat(format);
		String symbol = currency.getSymbol();
		symbol = symbol.length() == 1 ? symbol : symbol + " ";
		return symbol + numberFormat.format(number);
	}

	/**
	 * @param format
	 * @param currency
	 * @param locale
	 * @param number
	 * @return String
	 */
	public static String formatCurrency(String format, Currency currency, Locale locale, double number)
	{
		NumberFormat numberFormat = new DecimalFormat(format);
		/*
		 * String symbol = currency.getSymbol(locale); symbol = symbol.length() == 1 ?
		 * symbol : symbol + " ";
		 */
		return currency.getCurrencyCode() + numberFormat.format(number);
	}

	/**
	 * @param specialCharacter
	 * @return String
	 */
	public static String escapeSpecialCharacter(String specialCharacter)
	{
		return specialCharacter.replaceAll("\\$", "\\\\\\$");
	}

	/**
	 * @param specialCharacter
	 * @return String
	 */
	public static String replaceSpecialCharacterWithUnderscore(String specialCharacter)
	{
		return specialCharacter.replaceAll("[<#@!$%^&()/*|:?\">\\\\]+", "_");
	}

	/**
	 * round to 8 decimals
	 * 
	 * @param amount
	 * @return double
	 */
	public static double roundPrice(double amount)
	{
		return new BigDecimal(amount).setScale(8, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * round to 4 decimals
	 * 
	 * @param amount
	 * @return double
	 */
	public static double roundPrice4(double amount)
	{
		return new BigDecimal(amount).setScale(4, RoundingMode.HALF_UP).doubleValue();
	}
}
