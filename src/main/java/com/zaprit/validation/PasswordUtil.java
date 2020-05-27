/**
 * 
 */
package com.zaprit.validation;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * @author vaibhav.singh
 */
public final class PasswordUtil
{
	private static int passwordScore = 0;

	private static final String SYSTEM_SALT = "Random$SaltValue#WithSpecialCharacters!@#$%^&*()0192384756#qazwsxedcrfvtgbyhnujmikolp";

	private PasswordUtil()
	{}

	public static boolean isPasaswordLengthValid(String password)
	{
		boolean result = true;
		if (FieldUtil.isBlank(password) || password.trim().length() > 32)
		{
			result = false;
		}
		return result;
	}

	/**
	 * @param password
	 * @param userName
	 * @return score
	 */
	public static int getPasswordStrength(String password, String userName)
	{
		if (password.length() < 4)
		{
			passwordScore += 5;
		}
		else if ((password.length() >= 4) && (password.length() <= 8))
		{
			passwordScore += 10;
		}
		else if ((password.length() >= 9) && (password.length() <= 14))
		{
			passwordScore += 15;
		}
		else if (password.length() >= 15)
		{
			passwordScore += 20;
		}

		Pattern p = Pattern.compile("[a-z]");
		if (p.matcher(password).find())
		{
			passwordScore += 1;
		}

		Pattern p2 = Pattern.compile("[A-Z]");
		if (p2.matcher(password).find())
		{
			passwordScore += 3;
		}

		Pattern p3 = Pattern.compile("[0-9]");
		if (p3.matcher(password).find())
		{
			passwordScore += 5;
		}

		Pattern p4 = Pattern.compile("[!,@,#,$,%,^,&,*,?,_,~]");
		if (p4.matcher(password).find())
		{
			passwordScore += 7;
		}

		Pattern p5 = Pattern.compile("([a-z].*[A-Z])|([A-Z].*[a-z])");
		if (p5.matcher(password).find())
		{
			passwordScore += 5;
		}

		Pattern p6 = Pattern.compile("([a-zA-Z].*[0-9])|([0-9].*[a-zA-Z])");
		if (p6.matcher(password).find())
		{
			passwordScore += 7;
		}

		Pattern p7 = Pattern.compile("([a-zA-Z0-9].*[!,@,#,$,%,^,&,*,?,_,~])|([!,@,#,$,%,^,&,*,?,_,~].*[a-zA-Z0-9])");
		if (p7.matcher(password).find())
		{
			passwordScore += 10;
		}

		Pattern p8 = Pattern.compile(userName);
		if (p8.matcher(password).find())
		{
			passwordScore = 5;
		}
		return passwordScore;
	}

	public static String generateDBPassword(String plainPassword) throws NoSuchAlgorithmException
	{
		String encryptedPassword = "";

		if ((plainPassword != null) && (plainPassword.trim().length() > 0))
		{
			/*
			 * MessageDigest md5 = MessageDigest.getInstance("MD5");
			 * md5.update((plainPassword.trim() + systemSalt).getBytes()); encryptedPassword
			 * = new String(Base64.getEncoder().encode(md5.digest()));
			 */
			String text = plainPassword.trim() + SYSTEM_SALT;
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
			encryptedPassword = Base64.getEncoder().encodeToString(hash);
		}
		return encryptedPassword;
	}
}
