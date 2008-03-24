package org.seasar.javelin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.seasar.javelin.SystemLogger;

public class IOUtil
{
    private static final int BUFFER_SIZE = 1024;

    public static long copy(InputStream input, OutputStream output)
        throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer)))
        {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static long copy(Reader input, Writer output)
        throws IOException
    {
        return copy(input, output, -1);
    }

    public static long copy(Reader input, Writer output, int maxBytes)
        throws IOException
    {
        char[] buffer = new char[BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (count < maxBytes && (-1 != (n = input.read(buffer))))
        {
            if (count + n > maxBytes)
            {
                n = (int)(maxBytes - count);
            }

            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static String readFileToString(String jvnFileName)
    {
        return readFileToString(jvnFileName, -1);
    }

    public static String readFileToString(String jvnFileName, int maxBytes)
    {
        String content = "";
        Reader input = null;
        StringWriter output = null;
        try
        {
            input = new FileReader(jvnFileName);
            output = new StringWriter();
            copy(input, output, maxBytes);
        }
        catch (FileNotFoundException fnfe)
        {
            SystemLogger.getInstance().warn(fnfe);
        }
        catch (IOException ioe)
        {
            SystemLogger.getInstance().warn(ioe);
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException ioe)
                {
                    SystemLogger.getInstance().warn(ioe);
                }
            }
        }

        if (output != null)
        {
            content = output.toString();
        }
        return content;
    }

    public static boolean createDirs(String filePath)
    {
        File file = new File(filePath);
        if (file.exists() == false)
        {
            return file.mkdirs();
        }

        return false;
    }

    public static File[] listFile(String dirName, final String extention)
    {
        File dir = new File(dirName);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name)
            {
                if (name != null && name.endsWith(extention))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        if (files == null)
        {
            return null;
        }
        Arrays.sort(files);

        return files;
    }

    public static void removeLogFiles(int maxFileCount, String dirName, final String extention)
    {
        File[] files = listFile(dirName, extention);

        if (files == null)
        {
            return;
        }

        for (int index = files.length; index > maxFileCount; index--)
        {
            files[files.length - index].delete();
            //			JavelinErrorLogger.getInstance().log(
            //					"Remove file name = "
            //							+ files[files.length - index].getName());
        }
    }

    public static void zipFile(ZipOutputStream zStream, File file)
    {
        FileInputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(file);
            ZipEntry target = new ZipEntry(file.getName());
            zStream.putNextEntry(target);
            copy(fileInputStream, zStream);
            zStream.closeEntry();
        }
        catch (FileNotFoundException fnfe)
        {
            SystemLogger.getInstance().warn(fnfe);
        }
        catch (IOException ioe)
        {
            SystemLogger.getInstance().warn(ioe);
        }
        finally
        {
            if (fileInputStream != null)
            {
                try
                {
                    fileInputStream.close();
                }
                catch (IOException ioe)
                {
                    SystemLogger.getInstance().warn(ioe);
                }
            }
        }
    }
}
