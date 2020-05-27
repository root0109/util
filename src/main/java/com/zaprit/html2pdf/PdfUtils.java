package com.zaprit.html2pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;

/**
 * @author vaibhav.singh
 *
 */
public class PdfUtils
{

	/**
	 * @param html
	 * @param outputStream
	 * @throws IOException
	 */
	public static void convertHtmlToPdf(String html, OutputStream outputStream) throws IOException
	{
		ByteArrayInputStream contentStream = null;
		try (ByteArrayOutputStream tidyOutputStream = new ByteArrayOutputStream();)
		{
			Tidy tidy = new Tidy();
			tidy.setXHTML(true);
			tidy.setXmlSpace(true);
			tidy.setInputEncoding("UTF-8");
			tidy.setOutputEncoding("UTF-8");
			tidy.setAsciiChars(false);
			tidy.setForceOutput(true);
			tidy.setFixUri(true);
			tidy.setFixBackslash(true);
			tidy.setQuoteNbsp(true);
			tidy.setQuoteMarks(true);
			tidy.setQuoteAmpersand(true);
			tidy.setBreakBeforeBR(false);
			tidy.setShowWarnings(false);
			tidy.setEncloseText(true);
			// tidy.setTrimEmptyElements(true);
			tidy.setSmartIndent(true);
			Document document = tidy.parseDOM(contentStream = new ByteArrayInputStream(html.getBytes("UTF-8")), tidyOutputStream);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			DOMResult result = new DOMResult();
			transformer.transform(source, result);
			Document copiedDocument = (Document) result.getNode();

			Element style = copiedDocument.createElement("style");
			style.setTextContent("@page { @bottom-right { content: 'Page ' counter(page);} }");

			Element root = copiedDocument.getDocumentElement();
			Element head = (Element) root.getElementsByTagName("head").item(0);
			head.appendChild(style);

			ITextRenderer renderer = new ITextRenderer();

			String pdfLanguageFont = System.getProperty("pdf.multilingual.font.ttf");
			if (pdfLanguageFont != null)
			{
				Element body = (Element) root.getElementsByTagName("body").item(0);
				body.setAttribute("style", "font-family:Arial Unicode MS;");
				renderer.getFontResolver().addFont(pdfLanguageFont, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			}
			renderer.setDocument(copiedDocument, "");
			renderer.layout();
			renderer.createPDF(outputStream);

		}
		catch (UnsupportedEncodingException | TransformerException | DocumentException e)
		{
			throw new IOException(e);
		}
		finally
		{
			try
			{
				if (contentStream != null)
				{
					contentStream.close();
					contentStream = null;
				}
			}
			catch (IOException e)
			{}
		}
	}

}
