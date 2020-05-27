/**
 * 
 */
package com.zaprit.file;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * @author vaibhav.singh
 */
public interface FileReader extends Closeable
{
	/**
	 * Reads and returns line by line as array of strings
	 * 
	 * @return String[]
	 * @throws IOException
	 */
	public String[] readNext() throws IOException;

	/**
	 * Reads and returns line by line as array of strings, Not to be used if file
	 * size is large
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<String[]> readAll() throws IOException;

	/**
	 * Skips the required no of lines before returning the result
	 * 
	 * @param skipLines
	 */
	public void skipLines(int skipLines);
}
