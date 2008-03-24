package org.seasar.javelin.communicate;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.SystemLogger;
import org.seasar.javelin.S2StatsJavelinRecorder;
import org.seasar.javelin.bean.Invocation;
import org.seasar.javelin.communicate.entity.Telegram;

public class JavelinAcceptThread implements Runnable, AlarmListener {
	private static final int MAX_SOCKET = 30;

	static JavelinAcceptThread instance_ = new JavelinAcceptThread();

	ServerSocket objServerSocket = null;

	Socket clientSocket = null;

	List<JavelinClientThread> clientList = new ArrayList<JavelinClientThread>();

	boolean isRunning = false;

	private JavelinAcceptThread() {
		S2StatsJavelinRecorder.addListener(this);
	}
	
	/**
	 * ���������\�b�h
	 */
	public void init()
	{
	    // �������Ȃ��B
	}

	public static JavelinAcceptThread getInstance() {
		return instance_;
	}

	public void start(int port) {
		if (this.objServerSocket != null) {
			return;
		}

		try {
			this.objServerSocket = new ServerSocket(port);
			SystemLogger.getInstance().info(
					"S2Javelin��TCP�ڑ���t���J�n���܂��B �|�[�g[" + port + "]");

			// �N���C�A���g�ڑ��̎�t���J�n����B
			try {
				Thread acceptThread = new Thread(this, "JavelinAcceptThread");
				acceptThread.setDaemon(true);
				acceptThread.start();
			} catch (Exception objException) {
				SystemLogger.getInstance().warn(objException);
			}
		} catch (IOException objIOException) {
			SystemLogger.getInstance().info(
					"�|�[�g[" + port + "]�͂��łɊJ����Ă��܂��B�����𑱍s���܂��B");
		}
	}

	public void run() {
		ThreadGroup group = new ThreadGroup("JavelinThreadGroup");

		isRunning = true;
		while (isRunning) {
			try {
				accept(group);
			} catch (RuntimeException re) {
				SystemLogger.getInstance().warn("�d����M���ɗ\�����ʃG���[���������܂����B",
						re);
			}
		}

		synchronized (clientList) {
			for (int index = clientList.size() - 1; index >= 0; index--) {
				JavelinClientThread client = clientList.get(index);
				client.stop();
			}
		}

		try {
			this.objServerSocket.close();
		} catch (IOException ioe) {
			SystemLogger.getInstance().warn("�T�[�o�\�P�b�g�̃N���[�Y�Ɏ��s���܂����B", ioe);
		}
	}

	private void accept(ThreadGroup group) {
		try {
			// ���j�^�[
			clientSocket = objServerSocket.accept();
		} catch (IOException ioe) {
			SystemLogger.getInstance().warn("�T�[�o�\�P�b�g��accept�Ɏ��s���܂����B", ioe);
			return;
		}

		int clientCount = sweepClient();
		if (clientCount > MAX_SOCKET) {
			SystemLogger.getInstance().info(
					"�ڑ������ő吔[" + MAX_SOCKET + "]�𒴂������߁A�ڑ������ۂ��܂��B");
			try {
				clientSocket.close();
			} catch (IOException ioe) {
				SystemLogger.getInstance().warn("�N���C�A���g�\�P�b�g�̃N���[�Y�Ɏ��s���܂����B",
						ioe);
			}
			return;
		}

		InetAddress clientIP = clientSocket.getInetAddress();
		SystemLogger.getInstance().info(
				"�N���C�A���g����ڑ�����܂����BIP:[" + clientIP + "]");

		// �N���C�A���g����̗v����t�p�ɁA�����X���b�h���N������B
		JavelinClientThread clientRunnable;
		try {
			clientRunnable = new JavelinClientThread(clientSocket);
			Thread objHandleThread = new Thread(group, clientRunnable,
					"JavelinClientThread-" + clientCount);
			objHandleThread.setDaemon(true);
			objHandleThread.start();

			// �ʒm�̂��߂̃N���C�A���g���X�g�ɒǉ�����B
			synchronized (clientList) {
				clientList.add(clientRunnable);
			}
		} catch (IOException ioe) {
			SystemLogger.getInstance()
					.warn("�N���C�A���g�ʐM�X���b�h�̐����Ɏ��s���܂����B", ioe);
		}
	}

	private int sweepClient() {
		int size;
		synchronized (clientList) {
			for (int index = clientList.size() - 1; index >= 0; index--) {
				JavelinClientThread client = clientList.get(index);
				if (client.isClosed()) {
					clientList.remove(index);
				}
			}
			size = clientList.size();
		}

		return size;
	}

	public void sendExceedThresholdAlarm(String jvnFileName, CallTreeNode node) {
		Invocation invocation = node.getInvocation();
		Telegram objTelegram = TelegramUtil.create(Arrays
				.asList(new Object[] { invocation }),
				Common.BYTE_TELEGRAM_KIND_ALERT,
				Common.BYTE_REQUEST_KIND_NOTIFY);

		// �A���[���𑗐M����B
		sendTelegram(objTelegram);
	}

	/**
	 * �N���C�A���g��Telegram�𑗐M����B
	 * 
	 * @param telegram
	 *            ���M����d���B
	 */
	public void sendTelegram(Telegram telegram) {
		if (telegram == null) {
			return;
		}

		byte[] bytes = TelegramUtil.createTelegram(telegram);
		synchronized (clientList) {
			for (int index = clientList.size() - 1; index >= 0; index--) {
				JavelinClientThread client = clientList.get(index);
				client.sendAlarm(bytes);
			}
		}
	}

	public void stop() {
		this.isRunning = false;
	}

	/**
	 * ����AlarmListener�����[�g�m�[�h�݂̂��������邩�ǂ�����Ԃ��B �����̃N���X�ł́A���false��Ԃ��B
	 * 
	 * @see org.seasar.javelin.communicate.AlarmListener#isSendingRootOnly()
	 */
	public boolean isSendingRootOnly() {
		return false;
	}
	
	/**
	 * �N���C�A���g�������Ă��邩�ǂ����B
	 * @return
	 */
	public boolean hasClient() {
	    synchronized(clientList)
	    {
	        return (clientList.size() > 0);
	    }
	}
}
