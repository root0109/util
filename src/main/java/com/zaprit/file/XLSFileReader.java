/**
 * 
 */
package com.zaprit.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.RecordFormatException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
public class XLSFileReader implements FileReader
{
	private HSSFSheet		sheet		= null;
	private HSSFWorkbook	workbook	= null;
	private int				skipLines	= 0;
	private int				numRows		= 0;
	private int				currRowNum	= 0;

	public XLSFileReader(InputStream inputStream, String sheetName, String password, String charset) throws IOException
	{
		try
		{
			Biff8EncryptionKey.setCurrentUserPassword(password);
			POIFSFileSystem poiFileSystem = new POIFSFileSystem(inputStream);
			workbook = new HSSFWorkbook(poiFileSystem);
			if (sheetName != null)
			{
				sheet = workbook.getSheet(sheetName);
				if (sheet == null)
				{
					throw new IOException("Spreadsheet does not have the sheet:: " + sheetName);
				}
			}
			else
			{
				sheet = workbook.getSheetAt(0);
			}
			numRows = sheet.getLastRowNum();
			FileReaderFactory.registerFileReader("xls", this);
		}
		catch (RecordFormatException rec)
		{
			log.error("Unable to open file . Please check if the file is Password Protected");
			throw rec;
		}
		catch (Exception e)
		{
			throw new IOException("File/Sheet Not Found ", e);
		}

	}

	@Override
	public String[] readNext() throws IOException
	{
		HSSFRow currRow = sheet.getRow(skipLines + currRowNum);
		if (currRow == null || currRow.getLastCellNum() <= 0)
			return null; // This whole row is empty

		int numCols = currRow.getLastCellNum();
		String[] row = new String[numCols];

		for (int colCount = 0; colCount < numCols; colCount++)
		{
			HSSFCell currentCell = currRow.getCell(colCount);
			row[colCount] = currentCell != null ? ExcelUtil.parseCellValue(currentCell) : null;
		}
		boolean allNullValues = Arrays.stream(row).allMatch(value -> value == null);
		currRowNum++;
		return allNullValues ? null : row;
	}

	@Override
	public List<String[]> readAll() throws IOException
	{
		List<String[]> rowsList = new ArrayList<>();
		for (int rowCount = 0; rowCount < numRows; rowCount++)
		{
			HSSFRow currentRow = sheet.getRow(skipLines + rowCount);
			if (currentRow == null)
			{
				break; // means reached end of sheet
			}
			int numColumns = currentRow.getLastCellNum();
			String[] row = new String[numColumns];

			for (int columnCount = 0; columnCount < numColumns; columnCount++)
			{
				HSSFCell currentCell = currentRow.getCell(columnCount);
				row[columnCount] = currentCell != null ? ExcelUtil.parseCellValue(currentCell) : null;
			}
			rowsList.add(row);
		}
		return rowsList;
	}

	@Override
	public void skipLines(int skipLines)
	{
		this.skipLines = skipLines;
	}

	@Override
	public void close() throws IOException
	{
		Biff8EncryptionKey.setCurrentUserPassword(null);
		if (workbook != null)
		{
			workbook.close();
			workbook = null;
		}
	}
}
