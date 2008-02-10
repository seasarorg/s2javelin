package org.seasar.javelin.log;

import static org.seasar.javelin.JavelinConstants.ID_CALL;
import static org.seasar.javelin.JavelinConstants.ID_FIELD_READ;
import static org.seasar.javelin.JavelinConstants.ID_FIELD_WRITE;
import static org.seasar.javelin.JavelinConstants.ID_RETURN;
import static org.seasar.javelin.JavelinConstants.ID_THROW;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.seasar.javelin.CallTree;
import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.JavelinErrorLogger;
import org.seasar.javelin.S2JavelinConfig;

/**
 * Javelinのログファイル名を管理する
 * 
 * @author fujii
 * 
 */
public class S2StatsJavelinFileGenerator {
	private S2JavelinConfig javelinConfig_;

	/** 出力データを蓄積するキュー */
	private static BlockingQueue<JavelinLogTask> queue_ = new ArrayBlockingQueue<JavelinLogTask>(
			1000);

	private static boolean isInitialized_ = false;

	/**
	 * Javelinのログファイルを保存するディレクトリ名を設定する
	 * 
	 * @param javelinFielDir
	 *            ファイルの存在するディレクトリ名
	 */
	public S2StatsJavelinFileGenerator(S2JavelinConfig config) {
		javelinConfig_ = config;
	}

	/**
	 * Javelinログとして、ファイルに出力する。
	 * 
	 * @param tree
	 * @param node
	 */
	public String generateJaveinFile(CallTree tree, CallTreeNode node) {
		synchronized (S2StatsJavelinFileGenerator.class) {
			if (isInitialized_ == false) {
				JavelinLoggerThread thread = new JavelinLoggerThread(
						javelinConfig_, queue_);
				thread.start();
				isInitialized_ = true;
			}
		}

		Date date = new Date();
		String jvnFileName = JavelinLoggerThread.createJvnFileName(date);
		JavelinLogTask task = new JavelinLogTask(date, jvnFileName, tree, node);

		// キューにタスクを追加する。
		boolean result = queue_.offer(task);
		if (result == true) {
			jvnFileName = null;
		} else {
			// 失敗した場合は、ログに出力する。
			String message = createLogMessage(tree, node);
			JavelinErrorLogger.getInstance().log(message);
		}

		return jvnFileName;
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
	public static void generateJavelinFileImpl(Writer writer, CallTree tree,
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

}
