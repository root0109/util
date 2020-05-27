package com.zaprit.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

/**
 * @author vaibhav.singh
 */
public class CSVFileReader implements FileReader
{
	private CSVReader	csvReader	= null;
	private int			skipLines	= 0;

	public CSVFileReader(InputStream inputStream, String sheetName, String password, String charset) throws IOException
	{
		CSVParser csvParser = new CSVParserBuilder().withQuoteChar('"').withSeparator(',').build();
		csvReader = new CSVReaderBuilder(new InputStreamReader(inputStream)).withCSVParser(csvParser).build();
		FileReaderFactory.registerFileReader("csv", this);
	}

	@Override
	public String[] readNext() throws IOException
	{
		try
		{
			while (skipLines != 0)
			{
				skipLines--;
				csvReader.readNext();
			}
			return csvReader.readNext();
		}
		catch (CsvValidationException e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public List<String[]> readAll() throws IOException
	{
		try
		{
			return csvReader.readAll();
		}
		catch (CsvException e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public void skipLines(int skipLines)
	{
		this.skipLines = skipLines;
	}

	@Override
	public void close() throws IOException
	{
		if (csvReader != null)
		{
			csvReader.close();
			csvReader = null;
		}
	}
}
