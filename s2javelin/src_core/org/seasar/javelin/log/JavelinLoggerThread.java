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
	/** �t�@�C���ɂ���V�[�P���X�i���o�[ */
	private static int sequenceNumber = 0;

	/** zip�t�@�C���ɂ���V�[�P���X�i���o�[ */
	private static int zipSequenceNumber = 0;

	private static final String EXTENTION_JVN = ".jvn";

	private static final String EXTENTION_ZIP = ".zip";

	/** jvn�t�@�C�����̃t�H�[�}�b�g(���t�t�H�[�}�b�g(�~��(sec)�܂ŕ\��) */
	private static final String JVN_FILE_FORMAT = "javelin_{0,date,yyyyMMddHHmmssSSS}_{1,number,00000}"
			+ EXTENTION_JVN;

	/** zip�t�@�C�����̃t�H�[�}�b�g(���t�t�H�[�}�b�g(�~��(sec)�܂ŕ\��) */
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
        
        // jvn���O�o�͐�f�B���N�g�����쐬����B
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

			// ���O��zip���k�A�t�@�C�����������s���B
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

				// �ċA�I��writer�ɏ������݂��s���B
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
	 * jvn�t�@�C�����𐶐�����
	 * 
	 * @return jvn�t�@�C����
	 */
	public static String createJvnFileName(Date date) {
		String fileName;
		fileName = MessageFormat.format(JVN_FILE_FORMAT, date,
				(sequenceNumber++));

		return fileName;
	}

	/**
	 * �t�@�C�����𐶐�����
	 * 
	 * @return �t�@�C����
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
