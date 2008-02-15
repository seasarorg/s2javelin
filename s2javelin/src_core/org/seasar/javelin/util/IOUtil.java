package org.seasar.javelin.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.seasar.javelin.JavelinErrorLogger;

public class IOUtil {
	private static final int BUFFER_SIZE = 1024;

	public static long copy(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
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
			throws IOException {
		char[] buffer = new char[BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (count < maxBytes && (-1 != (n = input.read(buffer)))) {
		    if(count + n > maxBytes)
		    {
		        n = (int)(maxBytes - count);
		    }
		    
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

    public static String readFileToString(String jvnFileName) {
        return readFileToString(jvnFileName, -1);
    }
    
    public static String readFileToString(String jvnFileName, int maxBytes) {
		String content = "";
		Reader input = null;
		StringWriter output = null;
		try {
			input = new FileReader(jvnFileName);
			output = new StringWriter();
			copy(input, output, maxBytes);
		} catch (FileNotFoundException fnfe) {
			JavelinErrorLogger.getInstance().log(fnfe);
		} catch (IOException ioe) {
			JavelinErrorLogger.getInstance().log(ioe);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException ioe) {
					JavelinErrorLogger.getInstance().log(ioe);
				}
			}
		}
	
		if (output != null) {
			content = output.toString();
		}
		return content;
	}

}
