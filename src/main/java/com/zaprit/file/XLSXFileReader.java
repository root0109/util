/**
 * 
 */
package com.zaprit.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.RecordFormatException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vaibhav.singh
 */
@Slf4j
public class XLSXFileReader implements FileReader
{
	private Sheet		sheet		= null;
	private Workbook	workbook	= null;
	private int			rowIndex	= 0;
	private int			skipLines	= 0;
	private int			numRows		= 0;

	public XLSXFileReader(InputStream inputStream, String sheetName, String password, String charset) throws IOException
	{
		try
		{
			if (password != null)
				workbook = WorkbookFactory.create(inputStream, password);
			else
				workbook = WorkbookFactory.create(inputStream);

			if (sheetName != null)
			{
				sheet = workbook.getSheet(sheetName);
				if (sheet == null)
				{
					throw new IOException("XSpreadsheet does not have the sheet " + sheetName);
				}
			}
			else
			{
				sheet = workbook.getSheetAt(0);
			}
			numRows = sheet.getLastRowNum();
			FileReaderFactory.registerFileReader("xlsx", this);
		}
		catch (RecordFormatException rec)
		{
			log.error("Unable to open Xfile . Please check if the file is Password Protected");
			throw rec;
		}
		catch (Exception e)
		{
			throw new IOException("XFile Not Found ", e);
		}
	}

	@Override
	public String[] readNext() throws IOException
	{
		Row currRow = sheet.getRow(skipLines + rowIndex);
		if (currRow == null || currRow.getLastCellNum() <= 0)
			return null; // This whole row is empty
		int numCols = currRow.getLastCellNum();
		String[] row = new String[numCols];

		for (int colCount = 0; colCount < numCols; colCount++)
		{
			Cell currentCell = currRow.getCell(colCount);
			row[colCount] = currentCell != null ? ExcelUtil.parseCellValue(currentCell) : null;
		}
		boolean haveOnlyNulls = Arrays.stream(row).noneMatch(object -> object != null);
		rowIndex++;
		return haveOnlyNulls ? null : row;
	}

	@Override
	public List<String[]> readAll() throws IOException
	{
		List<String[]> rowsList = new ArrayList<>();
		for (int rowCount = 0; rowCount < numRows; rowCount++)
		{
			Row currentRow = sheet.getRow(skipLines + rowCount);
			if (currentRow == null)
			{
				break; // means reached end of sheet
			}
			int numColumns = currentRow.getLastCellNum();
			String[] row = new String[numColumns];

			for (int columnCount = 0; columnCount < numColumns; columnCount++)
			{
				Cell currentCell = currentRow.getCell(columnCount);
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
		if (workbook != null)
		{
			workbook.close();
			workbook = null;
		}
	}
}
