/**
 * 
 */
package com.zaprit.file;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vaibhav.singh
 */
public final class FileReaderFactory
{
	private static final Map<String, FileReader> registeredFileReaderMap = new ConcurrentHashMap<>();

	private FileReaderFactory()
	{
		throw new IllegalArgumentException("Prevents Instatiation of this factory");
	}

	/**
	 * This function needs to be called to register all the File Readers within the system
	 * 
	 * @param fileType
	 * @param fileReader
	 */
	public static void registerFileReader(String fileType, FileReader fileReader)
	{
		registeredFileReaderMap.put(fileType, fileReader);
	}

	public static FileReader getFileReader(String fileExtension) throws IOException
	{
		FileReader fileReader = registeredFileReaderMap.get(fileExtension);
		if (fileReader == null)
		{
			throw new IllegalArgumentException("No File Reader registered with fileType: " + fileExtension);
		}
		return fileReader;
	}
}
