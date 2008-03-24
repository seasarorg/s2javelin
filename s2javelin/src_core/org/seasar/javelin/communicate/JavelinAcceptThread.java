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
	 * 初期化メソッド
	 */
	public void init()
	{
	    // 何もしない。
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
					"S2JavelinのTCP接続受付を開始します。 ポート[" + port + "]");

			// クライアント接続の受付を開始する。
			try {
				Thread acceptThread = new Thread(this, "JavelinAcceptThread");
				acceptThread.setDaemon(true);
				acceptThread.start();
			} catch (Exception objException) {
				SystemLogger.getInstance().warn(objException);
			}
		} catch (IOException objIOException) {
			SystemLogger.getInstance().info(
					"ポート[" + port + "]はすでに開かれています。処理を続行します。");
		}
	}

	public void run() {
		ThreadGroup group = new ThreadGroup("JavelinThreadGroup");

		isRunning = true;
		while (isRunning) {
			try {
				accept(group);
			} catch (RuntimeException re) {
				SystemLogger.getInstance().warn("電文受信中に予期せぬエラーが発生しました。",
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
			SystemLogger.getInstance().warn("サーバソケットのクローズに失敗しました。", ioe);
		}
	}

	private void accept(ThreadGroup group) {
		try {
			// モニター
			clientSocket = objServerSocket.accept();
		} catch (IOException ioe) {
			SystemLogger.getInstance().warn("サーバソケットのacceptに失敗しました。", ioe);
			return;
		}

		int clientCount = sweepClient();
		if (clientCount > MAX_SOCKET) {
			SystemLogger.getInstance().info(
					"接続数が最大数[" + MAX_SOCKET + "]を超えたため、接続を拒否します。");
			try {
				clientSocket.close();
			} catch (IOException ioe) {
				SystemLogger.getInstance().warn("クライアントソケットのクローズに失敗しました。",
						ioe);
			}
			return;
		}

		InetAddress clientIP = clientSocket.getInetAddress();
		SystemLogger.getInstance().info(
				"クライアントから接続されました。IP:[" + clientIP + "]");

		// クライアントからの要求受付用に、処理スレッドを起動する。
		JavelinClientThread clientRunnable;
		try {
			clientRunnable = new JavelinClientThread(clientSocket);
			Thread objHandleThread = new Thread(group, clientRunnable,
					"JavelinClientThread-" + clientCount);
			objHandleThread.setDaemon(true);
			objHandleThread.start();

			// 通知のためのクライアントリストに追加する。
			synchronized (clientList) {
				clientList.add(clientRunnable);
			}
		} catch (IOException ioe) {
			SystemLogger.getInstance()
					.warn("クライアント通信スレッドの生成に失敗しました。", ioe);
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

		// アラームを送信する。
		sendTelegram(objTelegram);
	}

	/**
	 * クライアントにTelegramを送信する。
	 * 
	 * @param telegram
	 *            送信する電文。
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
	 * このAlarmListenerがルートノードのみを処理するかどうかを返す。 ※このクラスでは、常にfalseを返す。
	 * 
	 * @see org.seasar.javelin.communicate.AlarmListener#isSendingRootOnly()
	 */
	public boolean isSendingRootOnly() {
		return false;
	}
	
	/**
	 * クライアントを持っているかどうか。
	 * @return
	 */
	public boolean hasClient() {
	    synchronized(clientList)
	    {
	        return (clientList.size() > 0);
	    }
	}
}
