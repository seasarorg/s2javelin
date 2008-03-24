package org.seasar.javelin.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.zip.ZipOutputStream;

import org.seasar.javelin.SystemLogger;
import org.seasar.javelin.S2JavelinConfig;
import org.seasar.javelin.util.IOUtil;

class JavelinLoggerThread extends Thread {
	/** ファイルにつけるシーケンスナンバー */
	private static int sequenceNumber = 0;

	/** zipファイルにつけるシーケンスナンバー */
	private static int zipSequenceNumber = 0;

	private static final String EXTENTION_JVN = ".jvn";

	private static final String EXTENTION_ZIP = ".zip";

	/** jvnファイル名のフォーマット(日付フォーマット(ミリ(sec)まで表示) */
	private static final String JVN_FILE_FORMAT = "javelin_{0,date,yyyyMMddHHmmssSSS}_{1,number,00000}"
			+ EXTENTION_JVN;

	/** zipファイル名のフォーマット(日付フォーマット(ミリ(sec)まで表示) */
	private static final String ZIP_FILE_FORMAT = "{0}" + File.separator
			+ "javelin_{1,date,yyyyMMddHHmmssSSS}_{2,number,00000}"
			+ EXTENTION_ZIP;

	private S2JavelinConfig javelinConfig;

	private BlockingQueue<JavelinLogTask> queue_;

	public JavelinLoggerThread(S2JavelinConfig javelinConfig,
			BlockingQueue<JavelinLogTask> queue) {
		super();
		setName("S2Javelin-LoggerThread-" + getId());
		setDaemon(true);
		this.javelinConfig = javelinConfig;
		this.queue_ = queue;
	}

	public void run() {
		Writer writer = null;

		boolean isZipFileMax = javelinConfig.isLogZipMax();
		int jvnFileMax = javelinConfig.getLogJvnMax();
		int zipFileMax = javelinConfig.getLogZipMax();

		String javelinFileDir = javelinConfig.getJavelinFileDir();
        
        // jvnログ出力先ディレクトリを作成する。
        File javelinFileDirFile = new File(javelinFileDir);
        if(javelinFileDirFile.exists() == false)
        {
            javelinFileDirFile.mkdirs();
        }
        
		while (true) {
			JavelinLogTask task;
			try {
				task = queue_.take();
			} catch (InterruptedException ex) {
				SystemLogger.getInstance().warn(ex);
				continue;
			}

			// ログのzip圧縮、ファイル数制限を行う。
			if (sequenceNumber % jvnFileMax == 0) {
				if (isZipFileMax) {
					zipAndDeleteLogFiles(jvnFileMax, javelinFileDir,
							EXTENTION_JVN);
					IOUtil.removeLogFiles(zipFileMax, javelinFileDir, EXTENTION_ZIP);
				} else {
					IOUtil.removeLogFiles(jvnFileMax, javelinFileDir, EXTENTION_JVN);
				}
			}

			try {
                String jvnFileDir = javelinConfig.getJavelinFileDir();
				String jvnFileName = jvnFileDir + File.separator + task.getJvnFileName();
				writer = new FileWriter(jvnFileName, true);

				// 再帰的にwriterに書き込みを行う。
				S2StatsJavelinFileGenerator.generateJavelinFileImpl(writer,
						task.getTree(), task.getNode());
				
				writer.flush();

	            JavelinLogCallback callback = task.getJavelinLogCallback();
	            if(callback != null)
	            {
	                try
	                {
	                    callback.execute(task.getJvnFileName());
	                }
	                catch(Exception ex)
	                {
	                    SystemLogger.getInstance().warn(ex);
	                }
	            }
			} catch (IOException ioEx) {
				SystemLogger.getInstance().warn(ioEx);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException ioEx) {
						SystemLogger.getInstance().warn(ioEx);
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
	public static String createJvnFileName(Date date) {
		String fileName;
		fileName = MessageFormat.format(JVN_FILE_FORMAT, date,
				(sequenceNumber++));

		return fileName;
	}

	/**
	 * ファイル名を生成する
	 * 
	 * @return ファイル名
	 */
	private String createZipFileName(Date date) {
		String fileName;
		fileName = MessageFormat.format(ZIP_FILE_FORMAT, javelinConfig
				.getJavelinFileDir(), date, (zipSequenceNumber++));

		return fileName;
	}

	private void zipAndDeleteLogFiles(int maxFileCount, String dirName,
			final String extention) {
		File[] files = IOUtil.listFile(dirName, extention);

		if (files == null || files.length < maxFileCount) {
			return;
		}

		String fileName = createZipFileName(new Date());
		FileOutputStream fileOutputStream;
		ZipOutputStream zStream = null;
		try {
			fileOutputStream = new FileOutputStream(fileName);
			zStream = new ZipOutputStream(fileOutputStream);

			for (int index = 0; index < files.length; index++) {
				File file = files[index];
                IOUtil.zipFile(zStream, file);
                SystemLogger.getInstance().debug(
                                                 "zip file name = " + file.getName() + " to "
                                                         + fileName);

                file.delete();
                SystemLogger.getInstance().debug("Remove file name = " + file.getName());
			}

			zStream.finish();
		} catch (FileNotFoundException fnfe) {
			SystemLogger.getInstance().warn(fnfe);
		} catch (IOException ioe) {
			SystemLogger.getInstance().warn(ioe);
		} finally {
			if (zStream != null) {
				try {
					zStream.close();
				} catch (IOException ioe) {
					SystemLogger.getInstance().warn(ioe);
				}
			}
		}
	}

}
