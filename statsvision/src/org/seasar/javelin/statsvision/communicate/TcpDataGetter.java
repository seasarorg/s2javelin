package org.seasar.javelin.statsvision.communicate;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import org.seasar.javelin.statsvision.editors.StatsVisionEditor;

public class TcpDataGetter {
	/**
	 * 通信ソケット
	 */
	SocketChannel socketChannel = null;

	/**
	 * 出力流
	 */
	PrintStream objPrintStream = null;

	/**
	 * 使っているEditor
	 */
	StatsVisionEditor statsJavelinEditor = null;
	
	public TcpDataGetter() {
	}


	public void setStatsJavelinEditor(StatsVisionEditor statsJavelinEditor) {
		this.statsJavelinEditor = statsJavelinEditor;
	}

	/**
	 * サーバに接続する。
	 */
	public void open() {
		try {
			// サーバに接続する
			SocketAddress remote = new InetSocketAddress(statsJavelinEditor.getHostName(),
					statsJavelinEditor.getPortNum());
			socketChannel = SocketChannel.open(remote);
			// 接続中のメッセージ
			System.out.println("\nサーバに接続しました:" + remote);

		} catch (UnknownHostException objUnknownHostException) {
			// エラーメッセージを出す
			System.out.println(
					"サーバに接続するのは失敗しました。サーバアドレス、ポートを通りに設定していることを確認ください。");
			return;
		} catch (IOException objIOException) {
			// エラーメッセージを出す
			System.out.println(
					"サーバに接続するのは失敗しました。サーバアドレス、ポートを通りに設定していることを確認ください。");
			return;
		}

		try {
			objPrintStream = new PrintStream(socketChannel.socket()
					.getOutputStream(), true);
		} catch (IOException objIOException) {
			// エラーメッセージを出す
			objIOException.printStackTrace();
		}
	}

	/**
	 * サーバに接続を除く
	 */
	public void close() {
		try {
			telegramReader.setRunning(false);

			// 使用した通信対象をクリアする
			objPrintStream.close();
			socketChannel.close();
			
			System.out.println("サーバと通信が終了されました。");
		} catch (IOException objIOException) {
			// エラーを出す
			objIOException.printStackTrace();
		}
	}

	/**
	 * サーバに状態取得電文を送る
	 */
	public void request() {
		// 出力流データを格納
		byte[] byteOutputArr = null;

		// 頭部データ対象を作って、データを設定する
		Header objHeader = new Header();
		objHeader.setByteTelegramKind(Common.BYTE_TELEGRAM_KIND_GET);
		objHeader.setByteRequestKind(Common.BYTE_REQUEST_KIND_REQUEST);

		// 頭部を電文対象に設定する
		Telegram objOutputTelegram = new Telegram();
		objOutputTelegram.setObjHeader(objHeader);

		// 電文は、object ⇒ byte[] に変換する
		byteOutputArr = TelegramUtil.createTelegram(objOutputTelegram);

		try {
			// 出力流を出力する
			objPrintStream.write(byteOutputArr);
		} catch (IOException objIOException) {
			// エラーメッセージを出す
			objIOException.printStackTrace();
			this.close();
		}
	}

	/**
	 * サーバにリセット電文を送る
	 */
	public void sendReset() {
		// 出力流データを格納
		byte[] byteOutputArr = null;

		// 頭部データ対象を作って、データを設定する
		Header objHeader = new Header();
		objHeader.setByteTelegramKind(Common.BYTE_TELEGRAM_KIND_RESET);
		objHeader.setByteRequestKind(Common.BYTE_REQUEST_KIND_NOTIFY);

		// 頭部を電文対象に設定する
		Telegram objOutputTelegram = new Telegram();
		objOutputTelegram.setObjHeader(objHeader);

		// 電文は、object ⇒ byte[] に変換する
		byteOutputArr = TelegramUtil.createTelegram(objOutputTelegram);

		try {
			// 出力流を出力する
			objPrintStream.write(byteOutputArr);
		} catch (IOException objIOException) {
			// エラーメッセージを出す
			objIOException.printStackTrace();
			this.close();
		}
	}
	static TelegramReader telegramReader;

	public void startRead() {
		telegramReader = new TelegramReader(statsJavelinEditor, socketChannel);
		Thread readerThread = new Thread(telegramReader, "StatsReaderThread");
		readerThread.start();
	}
}
