/**
 * 
 */
package com.zaprit.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.mail.Address;
import javax.mail.SendFailedException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import com.zaprit.common.bo.ApplicationEmail;
import com.zaprit.common.bo.Attachment;
import com.zaprit.validation.FileUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
public final class EmailUtility
{
	private EmailUtility()
	{}

	/**
	 * @param applicationEmail
	 * @throws EmailException
	 */
	public static void sendEmailNotification(ApplicationEmail applicationEmail) throws EmailException
	{
		System.setProperty("mail.smtp.sendpartial", "true");
		HtmlEmail simpleEmail = new HtmlEmail();
		EmailAttachment emailAttachment = null;
		simpleEmail.setHostName(applicationEmail.getHostName());
		String message = applicationEmail.getMessage();
		message = message.replace("<script>", "&lt;script&gt;");
		message = message.replace("</script>", "&lt;/script&gt;");
		List<Attachment> endOfMailattachments = applicationEmail.getEndOfMailAttachments();
		try
		{
			if (endOfMailattachments != null)
			{
				for (Attachment attachment : endOfMailattachments)
				{
					emailAttachment = new EmailAttachment();
					String fileName = FileUtil.replaceInvalidFileNameCharacters(attachment.getName(), "");
					emailAttachment.setName(MimeUtility.encodeText(fileName));
					emailAttachment.setPath(attachment.getPath());
					emailAttachment.setDisposition(EmailAttachment.ATTACHMENT);
					simpleEmail.attach(emailAttachment);
				}
			}
			simpleEmail.setFrom(applicationEmail.getFromAddress(), applicationEmail.getFromName());
			Set<String> replyToRecipients = applicationEmail.getReplyToRecipients();
			if (replyToRecipients != null && !replyToRecipients.isEmpty())
			{
				simpleEmail.setReplyTo(convertToInternetAddress(replyToRecipients));
			}
			Set<String> toRecipients = applicationEmail.getToRecipients();
			simpleEmail.setTo(convertToInternetAddress(toRecipients));
			Set<String> ccRecipients = applicationEmail.getToRecipients();
			if (!ccRecipients.isEmpty())
			{
				simpleEmail.setCc(convertToInternetAddress(ccRecipients));
			}
			Set<String> bccRecipients = applicationEmail.getToRecipients();
			if (!bccRecipients.isEmpty())
			{
				simpleEmail.setBcc(convertToInternetAddress(bccRecipients));
			}
			simpleEmail.setSubject(applicationEmail.getSubject());
			simpleEmail.setHtmlMsg(message);
			simpleEmail.setCharset("UTF-8");
			simpleEmail.setSentDate(applicationEmail.getSentDate());
			simpleEmail.setSmtpPort(applicationEmail.getSmtpPort());
			simpleEmail.send();
		}
		catch (EmailException emailException)
		{
			Throwable exception = emailException.getCause();
			if (exception instanceof SendFailedException)
			{
				SendFailedException sendFailedException = ((SendFailedException) exception);
				Set<String> failureAddresses = new HashSet<>();
				Address[] invalidAddresses = sendFailedException.getInvalidAddresses();
				if (invalidAddresses != null)
				{
					for (Address address : invalidAddresses)
					{
						failureAddresses.add(address.toString());
					}
					sendFailureMail(applicationEmail);
				}
			}
		}
		catch (AddressException | UnsupportedEncodingException e)
		{
			throw new EmailException(e);
		}
		finally
		{
			if (endOfMailattachments != null)
			{
				for (Attachment attachment : endOfMailattachments)
				{
					org.apache.commons.io.FileUtils.deleteQuietly(new File(attachment.getPath()));
				}
			}
		}
	}

	private static List<InternetAddress> convertToInternetAddress(Set<String> recipients) throws AddressException
	{
		List<InternetAddress> internetAddresses = new ArrayList<>();
		if (!recipients.isEmpty())
		{
			for (String recipient : recipients)
			{
				internetAddresses.add(new InternetAddress(recipient));
			}
		}
		return internetAddresses;
	}

	private static void sendFailureMail(ApplicationEmail applicationEmail)
	{
		// Body content should contain html open and close tags
		HtmlEmail simpleEmail = new HtmlEmail();
		simpleEmail.setHostName(applicationEmail.getHostName());
		Set<String> toAddress = new HashSet<>();
		List<Attachment> endOfMailattachments = applicationEmail.getEndOfMailAttachments();
		try
		{
			simpleEmail.setFrom(applicationEmail.getFromAddress());
			toAddress.add(applicationEmail.getBounceEmailAddress());
			EmailAttachment emailAttachment = null;
			if (endOfMailattachments != null)
			{
				for (Attachment attachment : endOfMailattachments)
				{
					emailAttachment = new EmailAttachment();
					String fileName = FileUtil.replaceInvalidFileNameCharacters(attachment.getName(), "");
					emailAttachment.setName(MimeUtility.encodeText(fileName));
					emailAttachment.setPath(attachment.getPath());
					simpleEmail.attach(emailAttachment);
				}
			}
			simpleEmail.setTo(convertToInternetAddress(toAddress));
			simpleEmail.setSubject(applicationEmail.getSubject());
			simpleEmail.setHtmlMsg(applicationEmail.getMessage());
			simpleEmail.setCharset("UTF-8");
			simpleEmail.send();
		}
		catch (AddressException | EmailException | UnsupportedEncodingException e)
		{
			log.error("Error while sending failure e-mail: " + toAddress.toString(), e);
		}
	}
}
