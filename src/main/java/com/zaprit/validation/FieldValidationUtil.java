/**
 * 
 */
package com.zaprit.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author vaibhav.singh
 *
 */
public class FieldValidationUtil
{

	private static String urlPatternStr = "^(https?|ftp)://[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].+)?$";

	private static Pattern urlPattern = Pattern.compile(urlPatternStr, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	private static String	emailPatternStr	= "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9]+(\\.[a-z0-9]+)*(\\.[a-z]{2,})$";
	private static Pattern	emailPattern	= Pattern.compile(emailPatternStr, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	private static String	codePatternStr	= "^[ ]*[a-z0-9]{1}[a-z0-9_#\\-]*[ ]*$";
	private static Pattern	codePattern		= Pattern.compile(codePatternStr, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	private static String	phonePatternStr	= "^[0-9\\+\\-#\\*\\(\\)]*$";
	private static Pattern	phonePattern	= Pattern.compile(phonePatternStr, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	private static Map<String, String> zipCodePatterns = new HashMap<String, String>();

	static
	{
		zipCodePatterns.put("US", "\\A\\b[0-9]{5}(?:-[0-9]{4})?\\b\\z");
		zipCodePatterns.put("UK", "\\A\\b[A-Z]{1,2}[0-9][A-Z0-9]? [0-9][ABD-HJLNP-UW-Z]{2}\\b\\z");
		zipCodePatterns.put("GB", "\\A\\b[A-Z]{1,2}[0-9][A-Z0-9]? [0-9][ABD-HJLNP-UW-Z]{2}\\b\\z");
		zipCodePatterns.put("CA", "\\A\\b[ABCEGHJKLMNPRSTVXY][0-9][A-Z][ ]?[0-9][A-Z][0-9]\\b\\z");
		zipCodePatterns.put("IT", "^[0-9]{5}$");
		zipCodePatterns.put("DE", "^[0-9]{5}$");
		zipCodePatterns.put("BE", "^[1-9]{1}[0-9]{3}$");

	}

	/**
	 * @param data
	 * @return true if success else false
	 */
	public static boolean validateUniqueCode(String data)
	{
		return codePattern.matcher(data.trim()).matches();
	}

	/**
	 * @param data
	 * @return true if success else false
	 */
	public static boolean validateURL(String data)
	{
		return urlPattern.matcher(data.trim()).matches();
	}

	/**
	 * @param data
	 * @return true if success else false
	 */
	public static boolean validateEmail(String data)
	{
		return emailPattern.matcher(data.trim()).matches();
	}

	/**
	 * @param countryCode
	 * @param data
	 * @return true if success else false
	 */
	public static boolean validateZipCode(String countryCode, String data)
	{
		String pattern = zipCodePatterns.get(countryCode.trim().toUpperCase());
		if (pattern != null)
		{
			return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE).matcher(data).matches();

		}
		return true;
	}

	/**
	 * 
	 * @param data
	 * @return true if success else false
	 */
	public static boolean validatePhone(String data)
	{
		return phonePattern.matcher(data.trim()).matches();
	}

	/**
	 * @param pattern
	 * @param data
	 * @return true if success else false
	 */
	public static boolean validateRegexData(String pattern, String data)
	{
		return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE).matcher(data).matches();
	}

}
