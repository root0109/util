package com.zaprit.validation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author vaibhav.singh
 */
public final class FileUtil
{
	private FileUtil()
	{}

	/**
	 * @param text
	 * @param replace
	 * @return String
	 */
	public static String replaceInvalidFileNameCharacters(String text, String replace)
	{
		return text.replaceAll(":|\\*|\\?|\\\"|<|>|\\||/|\\\\", replace);
	}

	/**
	 * @param classPathFile
	 * @return String Buffer
	 * @throws IOException
	 */
	public static StringBuffer getFileAsStringBuffer(String classPathFile) throws IOException
	{
		return new StringBuffer(getFileAsString(classPathFile));
	}

	public static String getFileAsString(String classPathFile) throws IOException
	{
		// UNIX \n, WIndows \r\n
		return getFileAsStringStream(classPathFile).collect(Collectors.joining(System.lineSeparator()));
	}

	public static Stream<String> getFileAsStringStream(String classPathFile) throws IOException
	{
		try (Stream<String> lines = Files.lines(Paths.get(classPathFile)))
		{
			return lines;
		}
	}

	public static byte[] getBytesFromFile(String file) throws IOException
	{
		return getBytesFromFile(new File(file));
	}

	public static byte[] getBytesFromFile(File file) throws IOException
	{
		try (FileInputStream fileInputStream = new FileInputStream(file))
		{
			long length = file.length();
			// You cannot create an array using a long type.
			// It needs to be an int type.
			// Before converting to an int type, check
			// to ensure that file is not larger than Integer.MAX_VALUE.
			if (length > Integer.MAX_VALUE)
			{
				// File is too large
			}
			// Create the byte array to hold the data
			byte[] bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = fileInputStream.read(bytes, offset, bytes.length - offset)) >= 0)
			{
				offset += numRead;
			}
			if (offset < bytes.length)
			{
				throw new IOException("Could not completely read file " + file.getName());
			}
			return bytes;
		}
	}

	public static void writeBytesToFile(String file, byte[] content) throws IOException
	{
		writeBytesToFile(new File(file), content);
	}

	public static void writeBytesToFile(File file, byte[] content) throws IOException
	{
		writeBytesToFile(file, content, 0, content.length);
	}

	public static void appendBytesToFile(File file, byte[] content) throws IOException
	{
		writeBytesToFile(file, content, 0, content.length, true);
	}

	public static void writeBytesToFile(File file, byte[] content, int offset, int length) throws IOException
	{
		writeBytesToFile(file, content, offset, length, false);
	}

	public static void writeBytesToFile(File file, byte[] content, int offset, int length, boolean append) throws IOException
	{
		try (FileOutputStream fileOutputStream = new FileOutputStream(file, append))
		{
			fileOutputStream.write(content, offset, length);
		}
	}

	public static void writeStringToFile(String file, String content) throws IOException
	{
		writeStringToFile(new File(file), content);
	}

	public static void writeStringToFile(File file, String content) throws IOException
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file))))
		{
			bw.write(content);
			bw.flush();
		}
	}

	public static List<String> getStringListFromFile(String file) throws IOException
	{
		return getFileAsStringStream(file).collect(Collectors.toList());
	}

	public static String[] getStringArrayFromFile(String file) throws IOException
	{
		return getStringArrayFromFile(new File(file));
	}

	public static String[] getStringArrayFromFile(File file) throws IOException
	{
		ArrayList<String> records = new ArrayList<>();
		String theLine = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
		{
			while ((theLine = br.readLine()) != null)
			{
				records.add(theLine);
			}
			return records.toArray(new String[records.size()]);
		}
	}

	public static void writeStringArrayToFile(String file, String[] stringArray) throws IOException
	{
		writeStringArrayToFile(new File(file), stringArray);
	}

	public static void writeStringArrayToFile(File file, String[] stringArray) throws IOException
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file))))
		{
			for (int i = 0; i < stringArray.length; i++)
			{
				bw.write(stringArray[i]);
				bw.newLine();
			}
			bw.flush();
		}
	}

	public static void zipDirectory(String sourcePath, String destinationPath) throws IOException
	{
		sourcePath = sourcePath.replace('\\', '/');
		File source = new File(sourcePath);
		File destination = new File(destinationPath);
		if (!destination.exists())
		{
			new File(destination.getParent()).mkdirs();
		}
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(destination)))
		{
			if (source.isDirectory())
			{
				for (File inputFile : source.listFiles())
				{
					if (inputFile.isFile())
					{
						processFile(source, inputFile, zipOutputStream);
					}
					if (inputFile.isDirectory())
					{
						processDirectory(source, inputFile, zipOutputStream);
					}
				}
			}
		}
	}

	private static void processDirectory(File source, File directory, ZipOutputStream zipOutputStream) throws IOException
	{
		String pathStructure = directory.getAbsolutePath().replace(source.getPath(), "").substring(1).replace("\\", "/") + "/";
		ZipEntry zipEntry = new ZipEntry(pathStructure);
		zipOutputStream.putNextEntry(zipEntry);
		for (File file : directory.listFiles())
		{
			if (file.isDirectory())
			{
				processDirectory(source, file, zipOutputStream);
			}
			else if (file.isFile())
			{
				processFile(source, file, zipOutputStream);
			}
		}
		zipOutputStream.closeEntry();
	}

	/**
	 * @param source
	 * @param file
	 * @param zipOutputStream
	 * @throws IOException
	 * @throws IOException
	 */
	private static void processFile(File source, File file, ZipOutputStream zipOutputStream) throws IOException
	{
		String originalFilePath = file.getAbsolutePath().replace(source.getPath(), "").trim().replace("\\", "/");
		String structure = null;
		structure = originalFilePath.replace(file.getName(), "");
		try (InputStream fileInputStream = new FileInputStream(file))
		{
			byte[] buffer = new byte[1024];
			ZipEntry zipEntry = new ZipEntry(structure.substring(1) + file.getName());
			zipOutputStream.putNextEntry(zipEntry);
			int length = 0;
			while ((length = fileInputStream.read(buffer)) != -1)
			{
				zipOutputStream.write(buffer, 0, length);
			}
			zipOutputStream.closeEntry();
		}
	}

	public static void unZipDirectory(String sourcePath, String destinationPath) throws IOException
	{
		BufferedInputStream fileInputStream = null;
		BufferedOutputStream fileOutputStream = null;
		sourcePath = sourcePath.replace('\\', '/');
		File file = null;
		try
		{
			ZipFile zipFile = new ZipFile(sourcePath);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements())
			{
				ZipEntry unzipEntry = entries.nextElement();
				String currentUnzipEntry = unzipEntry.getName();
				if (!unzipEntry.isDirectory())
				{
					file = new File(destinationPath + "/" + currentUnzipEntry);
					if (!file.exists())
					{
						new File(file.getParent()).mkdirs();
					}
					fileInputStream = new BufferedInputStream(zipFile.getInputStream(unzipEntry));
					fileOutputStream = new BufferedOutputStream(new FileOutputStream(destinationPath + "/" + currentUnzipEntry));
					int i = 0;
					while ((i = fileInputStream.read()) != -1)
					{
						fileOutputStream.write(i);
					}
					fileInputStream.close();
					fileOutputStream.close();
				}
			}
			zipFile.close();
		}
		finally
		{
			if (fileInputStream != null)
			{
				fileInputStream.close();
				fileInputStream = null;
			}
			if (fileOutputStream != null)
			{
				fileOutputStream.close();
				fileOutputStream = null;
			}
		}
	}

	public static <V> byte[] gzip(V data) throws IOException
	{
		byte[] result = new byte[0];
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)
						{
			                {
				                def.setLevel(Deflater.BEST_COMPRESSION);
			                }
		                };
		                ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOutputStream))
		{
			objectOutputStream.writeObject(data);
			objectOutputStream.flush();
			result = byteArrayOutputStream.toByteArray();
			return result;
		}

	}

	@SuppressWarnings("unchecked")
	public static <V> V ungzip(byte[] bytes) throws IOException, ClassNotFoundException
	{
		V result;
		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		                GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
		                ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream))
		{

			result = (V) objectInputStream.readObject();
		}
		return result;
	}
}
