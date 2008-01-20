package org.seasar.javelin.log;

import static org.seasar.javelin.JavelinConstants.ID_CALL;
import static org.seasar.javelin.JavelinConstants.ID_FIELD_READ;
import static org.seasar.javelin.JavelinConstants.ID_FIELD_WRITE;
import static org.seasar.javelin.JavelinConstants.ID_RETURN;
import static org.seasar.javelin.JavelinConstants.ID_THROW;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPOutputStream;

import org.seasar.javelin.CallTree;
import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.JavelinErrorLogger;

/**
 * Javelinのログファイル名を管理する
 * 
 * @author fujii
 * 
 */
public class S2StatsJavelinFileGenerator {
	private static String javelinFileDir_ = "";

	/** ファイルにつけるシーケンスナンバー */
	private static int sequenceNumber = 0;

	/** gzファイルにつけるシーケンスナンバー */
	private static int gzipSequenceNumber = 0;

	/** 出力データを蓄積するキュー */
	private static BlockingQueue<LogTask> queue_ = new ArrayBlockingQueue<LogTask>(
			1000);

	static {
		synchronized (S2StatsJavelinFileGenerator.class) {
			LoggerThread thread = new LoggerThread();
			thread.start();
		}
	}

	/**
	 * Javelinのログファイルを保存するディレクトリ名を設定する
	 * 
	 * @param javelinFielDir
	 *            ファイルの存在するディレクトリ名
	 */
	public S2StatsJavelinFileGenerator(String javelinFielDir) {
		javelinFileDir_ = javelinFielDir;
	}

	/**
	 * Javelinログとして、ファイルに出力する。
	 * 
	 * @param tree
	 * @param node
	 */
	public void generateJaveinFile(CallTree tree, CallTreeNode node) {
		LogTask task = new LogTask(new Date(), tree, node);

		// キューにタスクを追加する。
		boolean result = queue_.offer(task);
		if (!result) {
			// 失敗した場合は、ログに出力する。
			String message = createLogMessage(tree, node);
			JavelinErrorLogger.getInstance().log(message);
		}
	}

	/**
	 * Javelinログとして、ファイルに出力する。
	 * 
	 * @param writer
	 *            ライター
	 * @param tree
	 *            ログの階層木
	 * @param node
	 */
	private static void generateJavelinFileImpl(Writer writer, CallTree tree,
			CallTreeNode node) {
		String jvnCallMessage;
		jvnCallMessage = createLogMessage(tree, node);

		// ファイルに1メッセージを書き込む。
		try {
			if (jvnCallMessage != null) {
				writer.write(jvnCallMessage);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		List<CallTreeNode> children = node.getChildren();
		for (int index = 0; index < children.size(); index++) {
			CallTreeNode child = children.get(index);
			generateJavelinFileImpl(writer, tree, child);
		}

		// Throwログを書き込む。
		if (node.getThrowable() != null) {
			writeThrowLog(writer, tree, node);
		}

		String jvnReturnMessage;
		if (node.isFieldAccess()) {
			jvnReturnMessage = JavelinLogMaker.createJavelinLog(ID_FIELD_WRITE,
					node.getEndTime(), tree, node);
		} else {
			jvnReturnMessage = JavelinLogMaker.createJavelinLog(ID_RETURN, node
					.getEndTime(), tree, node);
		}

		// ファイルに1メッセージを書き込む。
		try {
			if (jvnReturnMessage != null) {
				writer.write(jvnReturnMessage);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static String createLogMessage(CallTree tree, CallTreeNode node) {
		String jvnCallMessage;
		if (node.isFieldAccess()) {
			jvnCallMessage = JavelinLogMaker.createJavelinLog(ID_FIELD_READ,
					node.getStartTime(), tree, node);
		} else {
			jvnCallMessage = JavelinLogMaker.createJavelinLog(ID_CALL, node
					.getStartTime(), tree, node);
		}
		return jvnCallMessage;
	}

	private static void writeThrowLog(Writer writer, CallTree tree,
			CallTreeNode node) {
		String jvnThrowMessage = JavelinLogMaker.createJavelinLog(ID_THROW,
				node.getThrowTime(), tree, node);

		// ファイルに1メッセージを書き込む。
		try {
			if (jvnThrowMessage != null) {
				writer.write(jvnThrowMessage);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	static class LogTask {
		private Date date_;
		private CallTree tree_;
		private CallTreeNode node_;

		public LogTask(Date date, CallTree tree, CallTreeNode node) {
			date_ = date;
			tree_ = tree;
			node_ = node;
		}

		public Date getDate() {
			return date_;
		}

		public CallTree getTree() {
			return tree_;
		}

		public CallTreeNode getNode() {
			return node_;
		}
	}

	static class LoggerThread extends Thread {
		private static final String EXTENTION_JVN = ".jvn";
		private static final String EXTENTION_GZ = ".gz";

		/** jvnファイル名のフォーマット(日付フォーマット(ミリ(sec)まで表示) */
		private static final String JVN_FILE_FORMAT = "{0}" + File.separator
				+ "javelin_{1,date,yyyyMMddHHmmssSSS}_{2,number,00000}"
				+ EXTENTION_JVN;

		/** gzipファイル名のフォーマット(日付フォーマット(ミリ(sec)まで表示) */
		private static final String GZ_FILE_FORMAT = "{0}" + File.separator
				+ "javelin_{1,date,yyyyMMddHHmmssSSS}_{2,number,00000}"
				+ EXTENTION_GZ;

		private static final int BUFFER_SIZE = 1024;

		public LoggerThread() {
			super();
			setName("S2Javelin-LoggerThread-" + getId());
			setDaemon(true);
		}

		public void run() {
			Writer writer = null;

			while (true) {
				LogTask task;
				try {
					task = queue_.take();
				} catch (InterruptedException ex) {
					JavelinErrorLogger.getInstance().log(ex);
					continue;
				}

				// ログのgzip圧縮、ファイル数制限を行う。
				if (sequenceNumber % 100 == 0) {
					gzipAndDeleteLogFiles(100, javelinFileDir_, EXTENTION_JVN);
					removeLogFiles(100, javelinFileDir_, EXTENTION_GZ);
				}

				try {
					String jvnFileName = createJvnFileName(task.getDate());
					writer = new FileWriter(jvnFileName, true);

					// 再帰的にwriterに書き込みを行う。
					generateJavelinFileImpl(writer, task.getTree(), task
							.getNode());
				} catch (IOException ioEx) {
					JavelinErrorLogger.getInstance().log(ioEx);
				} finally {
					if (writer != null) {
						try {
							writer.close();
						} catch (IOException ioEx) {
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
		private String createJvnFileName(Date date) {
			String fileName;
			fileName = MessageFormat.format(JVN_FILE_FORMAT, javelinFileDir_,
					date, (sequenceNumber++));

			return fileName;
		}

		/**
		 * ファイル名を生成する
		 * 
		 * @return ファイル名
		 */
		private String createGzipFileName(Date date) {
			String fileName;
			fileName = MessageFormat.format(GZ_FILE_FORMAT, javelinFileDir_,
					date, (gzipSequenceNumber++));

			return fileName;
		}

		private void gzipAndDeleteLogFiles(int maxFileCount, String dirName,
				final String extention) {
			File[] files = listFile(dirName, extention);

			if (files == null || files.length < maxFileCount) {
				return;
			}

			String fileName = createGzipFileName(new Date());
			FileOutputStream fileOutputStream;
			GZIPOutputStream zStream = null;
			try {
				fileOutputStream = new FileOutputStream(fileName);
				zStream = new GZIPOutputStream(fileOutputStream);

				for (int index = 0; index < files.length; index++) {
					File file = files[index];
					gzipFile(zStream, file);
					JavelinErrorLogger.getInstance().log(
							"gzip file name = " + file.getName() + " to "
									+ fileName);

					file.delete();
					JavelinErrorLogger.getInstance().log(
							"Remove file name = " + file.getName());
				}

				zStream.finish();
			} catch (FileNotFoundException fnfe) {
				JavelinErrorLogger.getInstance().log(fnfe);
			} catch (IOException ioe) {
				JavelinErrorLogger.getInstance().log(ioe);
			} finally {
				if (zStream != null) {
					try {
						zStream.close();
					} catch (IOException ioe) {
						JavelinErrorLogger.getInstance().log(ioe);
					}
				}
			}
		}

		private void removeLogFiles(int maxFileCount, String dirName,
				final String extention) {
			File[] files = listFile(dirName, extention);

			if (files == null) {
				return;
			}

			for (int index = files.length; index > maxFileCount; index--) {
				files[files.length - index].delete();
				JavelinErrorLogger.getInstance().log(
						"Remove file name = "
								+ files[files.length - index].getName());
			}
		}

		private File[] listFile(String dirName, final String extention) {
			File dir = new File(dirName);
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name != null && name.endsWith(extention)) {
						return true;
					} else {
						return false;
					}
				}
			});

			if (files == null) {
				return null;
			}
			Arrays.sort(files);

			return files;
		}

		private void gzipFile(GZIPOutputStream zStream, File file) {
			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(file);
				copy(fileInputStream, zStream);
			} catch (FileNotFoundException fnfe) {
				JavelinErrorLogger.getInstance().log(fnfe);
			} catch (IOException ioe) {
				JavelinErrorLogger.getInstance().log(ioe);
			} finally {
				if (fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException ioe) {
						JavelinErrorLogger.getInstance().log(ioe);
					}
				}
			}
		}

		private long copy(InputStream input, OutputStream output)
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
	}

}
