package com.zaprit.file;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * 
 * @author vaibhav.singh
 *
 */
public final class ExcelUtil
{
	private ExcelUtil()
	{
		throw new IllegalArgumentException("This class should not instantiated");
	}

	public static String parseCellValue(Cell currentCell)
	{
		String value = null;
		switch (currentCell.getCellType())
		{
			case BOOLEAN:
				value = String.valueOf(currentCell.getBooleanCellValue());
				break;
			case FORMULA:
				switch (currentCell.getCachedFormulaResultType())
				{
					case NUMERIC:
						value = String.valueOf(currentCell.getNumericCellValue());
						break;
					case STRING:
						value = currentCell.getStringCellValue();
						break;
					case BOOLEAN:
						value = String.valueOf(currentCell.getBooleanCellValue());
						break;
					case ERROR:
					default:
						value = null;
						//cellType = CellType.BLANK;
						break;
				}
				break;
			case NUMERIC:
				value = parseNumericCell(currentCell);
				break;
			case STRING:
				value = currentCell.getStringCellValue();
				if (value.isEmpty())
					value = " ";
				break;
			case BLANK:
				value = " ";
				break;
			case ERROR:
			default:
				break;
		}
		return value;
	}

	public static String parseNumericCell(Cell numCell)
	{
		String value = null;
		if (DateUtil.isCellDateFormatted(numCell))
		{
			value = numCell.getDateCellValue().toString();
		}
		else
		{
			double dVal = numCell.getNumericCellValue();
			if (!Double.isNaN(dVal))
			{
				long lVal = (long) numCell.getNumericCellValue();
				value = String.valueOf(lVal);
			}
		}
		return value;
	}
}
