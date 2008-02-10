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
 * Javelin�̃��O�t�@�C�������Ǘ�����
 * 
 * @author fujii
 * 
 */
public class S2StatsJavelinFileGenerator {
	private S2JavelinConfig javelinConfig_;

	/** �o�̓f�[�^��~�ς���L���[ */
	private static BlockingQueue<JavelinLogTask> queue_ = new ArrayBlockingQueue<JavelinLogTask>(
			1000);

	private static boolean isInitialized_ = false;

	/**
	 * Javelin�̃��O�t�@�C����ۑ�����f�B���N�g������ݒ肷��
	 * 
	 * @param javelinFielDir
	 *            �t�@�C���̑��݂���f�B���N�g����
	 */
	public S2StatsJavelinFileGenerator(S2JavelinConfig config) {
		javelinConfig_ = config;
	}

	/**
	 * Javelin���O�Ƃ��āA�t�@�C���ɏo�͂���B
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

		// �L���[�Ƀ^�X�N��ǉ�����B
		boolean result = queue_.offer(task);
		if (result == true) {
			jvnFileName = null;
		} else {
			// ���s�����ꍇ�́A���O�ɏo�͂���B
			String message = createLogMessage(tree, node);
			JavelinErrorLogger.getInstance().log(message);
		}

		return jvnFileName;
	}

	/**
	 * Javelin���O�Ƃ��āA�t�@�C���ɏo�͂���B
	 * 
	 * @param writer
	 *            ���C�^�[
	 * @param tree
	 *            ���O�̊K�w��
	 * @param node
	 */
	public static void generateJavelinFileImpl(Writer writer, CallTree tree,
			CallTreeNode node) {
		String jvnCallMessage;
		jvnCallMessage = createLogMessage(tree, node);

		// �t�@�C����1���b�Z�[�W���������ށB
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

		// Throw���O���������ށB
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

		// �t�@�C����1���b�Z�[�W���������ށB
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

		// �t�@�C����1���b�Z�[�W���������ށB
		try {
			if (jvnThrowMessage != null) {
				writer.write(jvnThrowMessage);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
