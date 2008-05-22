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
	 * 初期化メソッド
	 */
	public void init()
	{
	    // 何もしない。
	}

	/**
	 * JavelinAcceptThreadのインスタンスを取得する。
	 * @return JavelinAcceptThread
	 */
	public static JavelinAcceptThread getInstance() {
		return instance_;
	}

	/**
	 * スレッドを開始する。
	 * @param port ポート番号
	 */
	public void start(int port) {
		if (this.objServerSocket_ != null) {
			return;
		}

		try {
			this.objServerSocket_ = new ServerSocket(port);
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
			SystemLogger.getInstance().warn(
					"ポート[" + port + "]はすでに開かれています。処理を続行します。");
		}
	}

	public void run() {
		ThreadGroup group = new ThreadGroup("JavelinThreadGroup");

		this.isRunning_ = true;
		while (this.isRunning_) {
			try {
				accept(group);
			} catch (RuntimeException re) {
				SystemLogger.getInstance().warn("電文受信中に予期せぬエラーが発生しました。",
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
			SystemLogger.getInstance().warn("サーバソケットのクローズに失敗しました。", ioe);
		}
	}

	private void accept(ThreadGroup group) {
		try {
			// モニター
		    this.clientSocket_ = this.objServerSocket_.accept();
		} catch (IOException ioe) {
			SystemLogger.getInstance().warn("サーバソケットのacceptに失敗しました。", ioe);
			return;
		}

		int clientCount = sweepClient();
		if (clientCount > MAX_SOCKET) {
			SystemLogger.getInstance().info(
					"接続数が最大数[" + MAX_SOCKET + "]を超えたため、接続を拒否します。");
			try {
			    this.clientSocket_.close();
			} catch (IOException ioe) {
				SystemLogger.getInstance().warn("クライアントソケットのクローズに失敗しました。",
						ioe);
			}
			return;
		}

		InetAddress clientIP = this.clientSocket_.getInetAddress();
		SystemLogger.getInstance().info(
				"クライアントから接続されました。IP:[" + clientIP + "]");

		// クライアントからの要求受付用に、処理スレッドを起動する。
		JavelinClientThread clientRunnable;
		try {
			clientRunnable = new JavelinClientThread(this.clientSocket_);
			Thread objHandleThread = new Thread(group, clientRunnable,
					"JavelinClientThread-" + clientCount);
			objHandleThread.setDaemon(true);
			objHandleThread.start();

			// 通知のためのクライアントリストに追加する。
			synchronized (this.clientList_) {
			    this.clientList_.add(clientRunnable);
			}
		} catch (IOException ioe) {
			SystemLogger.getInstance()
					.warn("クライアント通信スレッドの生成に失敗しました。", ioe);
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

		byte[] bytes = S2TelegramUtil.createTelegram(telegram);
		synchronized (this.clientList_) {
			for (int index = this.clientList_.size() - 1; index >= 0; index--) {
				JavelinClientThread client = this.clientList_.get(index);
				client.sendAlarm(bytes);
                if (SystemLogger.getInstance().isDebugEnabled())
                {
                    client.logTelegram("電文を送信しました。", telegram);
                }
			}
		}
	}

	/**
	 * スレッドを停止する。
	 */
	public void stop() {
		this.isRunning_ = false;
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
	    synchronized(this.clientList_)
	    {
	        return (this.clientList_.size() > 0);
	    }
	}
}
