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

	ServerSocket objServerSocket_ = null;

	Socket clientSocket_ = null;

	List<JavelinClientThread> clientList_ = new ArrayList<JavelinClientThread>();

	boolean isRunning_ = false;

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

	/**
	 * JavelinAcceptThread�̃C���X�^���X���擾����B
	 * @return JavelinAcceptThread
	 */
	public static JavelinAcceptThread getInstance() {
		return instance_;
	}

	/**
	 * �X���b�h���J�n����B
	 * @param port �|�[�g�ԍ�
	 */
	public void start(int port) {
		if (this.objServerSocket_ != null) {
			return;
		}

		try {
			this.objServerSocket_ = new ServerSocket(port);
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
			SystemLogger.getInstance().warn(
					"�|�[�g[" + port + "]�͂��łɊJ����Ă��܂��B�����𑱍s���܂��B");
		}
	}

	public void run() {
		ThreadGroup group = new ThreadGroup("JavelinThreadGroup");

		this.isRunning_ = true;
		while (this.isRunning_) {
			try {
				accept(group);
			} catch (RuntimeException re) {
				SystemLogger.getInstance().warn("�d����M���ɗ\�����ʃG���[���������܂����B",
						re);
			}
		}

		synchronized (this.clientList_) {
			for (int index = this.clientList_.size() - 1; index >= 0; index--) {
				JavelinClientThread client = this.clientList_.get(index);
				client.stop();
			}
		}

		try {
			this.objServerSocket_.close();
		} catch (IOException ioe) {
			SystemLogger.getInstance().warn("�T�[�o�\�P�b�g�̃N���[�Y�Ɏ��s���܂����B", ioe);
		}
	}

	private void accept(ThreadGroup group) {
		try {
			// ���j�^�[
		    this.clientSocket_ = this.objServerSocket_.accept();
		} catch (IOException ioe) {
			SystemLogger.getInstance().warn("�T�[�o�\�P�b�g��accept�Ɏ��s���܂����B", ioe);
			return;
		}

		int clientCount = sweepClient();
		if (clientCount > MAX_SOCKET) {
			SystemLogger.getInstance().info(
					"�ڑ������ő吔[" + MAX_SOCKET + "]�𒴂������߁A�ڑ������ۂ��܂��B");
			try {
			    this.clientSocket_.close();
			} catch (IOException ioe) {
				SystemLogger.getInstance().warn("�N���C�A���g�\�P�b�g�̃N���[�Y�Ɏ��s���܂����B",
						ioe);
			}
			return;
		}

		InetAddress clientIP = this.clientSocket_.getInetAddress();
		SystemLogger.getInstance().info(
				"�N���C�A���g����ڑ�����܂����BIP:[" + clientIP + "]");

		// �N���C�A���g����̗v����t�p�ɁA�����X���b�h���N������B
		JavelinClientThread clientRunnable;
		try {
			clientRunnable = new JavelinClientThread(this.clientSocket_);
			Thread objHandleThread = new Thread(group, clientRunnable,
					"JavelinClientThread-" + clientCount);
			objHandleThread.setDaemon(true);
			objHandleThread.start();

			// �ʒm�̂��߂̃N���C�A���g���X�g�ɒǉ�����B
			synchronized (this.clientList_) {
			    this.clientList_.add(clientRunnable);
			}
		} catch (IOException ioe) {
			SystemLogger.getInstance()
					.warn("�N���C�A���g�ʐM�X���b�h�̐����Ɏ��s���܂����B", ioe);
		}
	}

	private int sweepClient() {
		int size;
		synchronized (this.clientList_) {
			for (int index = this.clientList_.size() - 1; index >= 0; index--) {
				JavelinClientThread client = this.clientList_.get(index);
				if (client.isClosed()) {
				    this.clientList_.remove(index);
				}
			}
			size = this.clientList_.size();
		}

		return size;
	}

	/**
	 * {@inheritDoc}
	 */
	public void sendExceedThresholdAlarm(String jvnFileName, CallTreeNode node) {
		Invocation invocation = node.getInvocation();
		Telegram objTelegram = S2TelegramUtil.create(Arrays
				.asList(new Invocation[] { invocation }),
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

		byte[] bytes = S2TelegramUtil.createTelegram(telegram);
		synchronized (this.clientList_) {
			for (int index = this.clientList_.size() - 1; index >= 0; index--) {
				JavelinClientThread client = this.clientList_.get(index);
				client.sendAlarm(bytes);
                if (SystemLogger.getInstance().isDebugEnabled())
                {
                    client.logTelegram("�d���𑗐M���܂����B", telegram);
                }
			}
		}
	}

	/**
	 * �X���b�h���~����B
	 */
	public void stop() {
		this.isRunning_ = false;
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
	    synchronized(this.clientList_)
	    {
	        return (this.clientList_.size() > 0);
	    }
	}
}
