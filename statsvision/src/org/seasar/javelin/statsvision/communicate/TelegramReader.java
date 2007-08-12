package org.seasar.javelin.statsvision.communicate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.seasar.javelin.statsvision.editors.StatsVisionEditor;

public class TelegramReader implements Runnable {
	private StatsVisionEditor statsJavelinEditor_;

	private boolean isRunning;


	SocketChannel channel_;

	// サーバ側からのデータのHead用変数
	ByteBuffer headerBuffer = ByteBuffer.allocate(Header.HEADER_LENGTH);
	
	
	public TelegramReader(StatsVisionEditor stasJavelinEditor,
			SocketChannel channel) {
		this.channel_ = channel;
		this.statsJavelinEditor_ = stasJavelinEditor;
		this.isRunning = false;
	}

	public void run() {
		this.isRunning = true;
		while (this.isRunning) {
			byte[] telegramBytes = null;
			try {
				telegramBytes = this.readTelegramBytes();
			} catch (IOException ioe) {
				// TODO 再接続
				this.isRunning = false;
				break;
			}
			Telegram telegram = TelegramUtil.recovryTelegram(telegramBytes);

			int telegramKind = telegram.getObjHeader().getByteTelegramKind();
			int requestKind = telegram.getObjHeader().getByteRequestKind();

			if (Common.BYTE_TELEGRAM_KIND_ALERT == telegramKind) {
				// 通知受信処理を行う
				statsJavelinEditor_.listeningGraphicalViewer(telegram);
			} else if (Common.BYTE_TELEGRAM_KIND_GET == telegramKind
					&& Common.BYTE_REQUEST_KIND_RESPONSE == requestKind) {
				// 応答受信処理を行う。
				statsJavelinEditor_.addResponseTelegram(telegram);
				System.out.println("電文受信[" + requestKind + "]");
			} else {
				// TODO ログ出力
				System.out.println("未定義の要求応答種別を受信しました。[" + requestKind + "]");
			}
		}

	}

	/**
	 * サーバからデータを読み込む
	 * 
	 * @throws IOException
	 */
	public byte[] readTelegramBytes() throws IOException {

		int readCount = 0;
		while (readCount < Header.HEADER_LENGTH) {
			readCount += channel_.read(headerBuffer);
		}

		headerBuffer.rewind();
		int telegramLength = headerBuffer.getInt();

		// ヘッダ部しかない場合はそのまま返す。
		if (telegramLength <= Header.HEADER_LENGTH) {
			headerBuffer.rewind();
			return headerBuffer.array();
		}

		readCount = 0;
		ByteBuffer bodyBuffer = ByteBuffer.allocate(telegramLength);
		bodyBuffer.put(headerBuffer.array());

		while (bodyBuffer.remaining() > 0) {
			channel_.read(bodyBuffer);
		}

		headerBuffer.rewind();
		return bodyBuffer.array();
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

}
