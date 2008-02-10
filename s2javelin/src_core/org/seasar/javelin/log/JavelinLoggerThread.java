package org.seasar.javelin.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.seasar.javelin.JavelinErrorLogger;
import org.seasar.javelin.S2JavelinConfig;

class JavelinLoggerThread extends Thread
{
    /** ファイルにつけるシーケンスナンバー */
    private static int             sequenceNumber    = 0;

    /** zipファイルにつけるシーケンスナンバー */
    private static int             zipSequenceNumber = 0;

    private static final String    EXTENTION_JVN     = ".jvn";

    private static final String    EXTENTION_ZIP     = ".zip";

    /** jvnファイル名のフォーマット(日付フォーマット(ミリ(sec)まで表示) */
    private static final String    JVN_FILE_FORMAT   = "{0}"
                                                             + File.separator
                                                             + "javelin_{1,date,yyyyMMddHHmmssSSS}_{2,number,00000}"
                                                             + EXTENTION_JVN;

    /** zipファイル名のフォーマット(日付フォーマット(ミリ(sec)まで表示) */
    private static final String    ZIP_FILE_FORMAT   = "{0}"
                                                             + File.separator
                                                             + "javelin_{1,date,yyyyMMddHHmmssSSS}_{2,number,00000}"
                                                             + EXTENTION_ZIP;
    
    private static final int       BUFFER_SIZE       = 1024;

    private S2JavelinConfig        javelinConfig;

    private BlockingQueue<JavelinLogTask> queue_;

    public JavelinLoggerThread(S2JavelinConfig javelinConfig, BlockingQueue<JavelinLogTask> queue)
    {
        super();
        setName("S2Javelin-LoggerThread-" + getId());
        setDaemon(true);
        this.javelinConfig = javelinConfig;
        this.queue_ = queue;
    }

    public void run()
    {
        Writer writer = null;

        boolean isZipFileMax = javelinConfig.isLogZipMax();
        int jvnFileMax = javelinConfig.getLogJvnMax();
        int zipFileMax = javelinConfig.getLogZipMax();

        String javelinFileDir = javelinConfig.getJavelinFileDir();

        while (true)
        {
            JavelinLogTask task;
            try
            {
                task = queue_.take();
            }
            catch (InterruptedException ex)
            {
                JavelinErrorLogger.getInstance().log(ex);
                continue;
            }

            // ログのzip圧縮、ファイル数制限を行う。
            if (sequenceNumber % jvnFileMax == 0)
            {
                if (isZipFileMax)
                {
                    zipAndDeleteLogFiles(jvnFileMax, javelinFileDir, EXTENTION_JVN);
                    removeLogFiles(zipFileMax, javelinFileDir, EXTENTION_ZIP);
                }
                else
                {
                    removeLogFiles(jvnFileMax, javelinFileDir, EXTENTION_JVN);
                }
            }
            
            try
            {
                String jvnFileName = task.getJvnFileName();
                writer = new FileWriter(jvnFileName, true);

                // 再帰的にwriterに書き込みを行う。
                S2StatsJavelinFileGenerator.generateJavelinFileImpl(writer, task.getTree(),
                                                                    task.getNode());
            }
            catch (IOException ioEx)
            {
                JavelinErrorLogger.getInstance().log(ioEx);
            }
            finally
            {
                if (writer != null)
                {
                    try
                    {
                        writer.close();
                    }
                    catch (IOException ioEx)
                    {
                        JavelinErrorLogger.getInstance().log(ioEx);
                    }
                }
            }
        }
    }

    /**
     * jvnファイル名を生成する
     * 
     * @return jvnファイル名
     */
    public static String createJvnFileName(S2JavelinConfig javelinConfig, Date date)
    {
        String fileName;
        fileName = MessageFormat.format(JVN_FILE_FORMAT, javelinConfig.getJavelinFileDir(), date,
                                        (sequenceNumber++));

        return fileName;
    }

    /**
     * ファイル名を生成する
     * 
     * @return ファイル名
     */
    private String createZipFileName(Date date)
    {
        String fileName;
        fileName = MessageFormat.format(ZIP_FILE_FORMAT, javelinConfig.getJavelinFileDir(), date,
                                        (zipSequenceNumber++));

        return fileName;
    }

    private void zipAndDeleteLogFiles(int maxFileCount, String dirName, final String extention)
    {
        File[] files = listFile(dirName, extention);

        if (files == null || files.length < maxFileCount)
        {
            return;
        }

        String fileName = createZipFileName(new Date());
        FileOutputStream fileOutputStream;
        ZipOutputStream zStream = null;
        try
        {
            fileOutputStream = new FileOutputStream(fileName);
            zStream = new ZipOutputStream(fileOutputStream);

            for (int index = 0; index < files.length; index++)
            {
                File file = files[index];
                zipFile(zStream, file);
                JavelinErrorLogger.getInstance().log(
                                                     "zip file name = " + file.getName() + " to "
                                                             + fileName);

                file.delete();
                JavelinErrorLogger.getInstance().log("Remove file name = " + file.getName());
            }

            zStream.finish();
        }
        catch (FileNotFoundException fnfe)
        {
            JavelinErrorLogger.getInstance().log(fnfe);
        }
        catch (IOException ioe)
        {
            JavelinErrorLogger.getInstance().log(ioe);
        }
        finally
        {
            if (zStream != null)
            {
                try
                {
                    zStream.close();
                }
                catch (IOException ioe)
                {
                    JavelinErrorLogger.getInstance().log(ioe);
                }
            }
        }
    }

    private void removeLogFiles(int maxFileCount, String dirName, final String extention)
    {
        File[] files = listFile(dirName, extention);

        if (files == null)
        {
            return;
        }

        for (int index = files.length; index > maxFileCount; index--)
        {
            files[files.length - index].delete();
            JavelinErrorLogger.getInstance().log(
                                                 "Remove file name = "
                                                         + files[files.length - index].getName());
        }
    }

    private File[] listFile(String dirName, final String extention)
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

    private void zipFile(ZipOutputStream zStream, File file)
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
            JavelinErrorLogger.getInstance().log(fnfe);
        }
        catch (IOException ioe)
        {
            JavelinErrorLogger.getInstance().log(ioe);
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
                    JavelinErrorLogger.getInstance().log(ioe);
                }
            }
        }
    }

    private long copy(InputStream input, OutputStream output)
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
}
