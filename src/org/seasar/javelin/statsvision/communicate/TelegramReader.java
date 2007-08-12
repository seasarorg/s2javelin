package org.seasar.javelin.statsvision.communicate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.seasar.javelin.statsvision.editors.StatsVisionEditor;

public class TelegramReader implements Runnable {
	private StatsVisionEditor statsJavelinEditor_;

	private boolean isRunning;


	SocketChannel channel_;

	// �T�[�o������̃f�[�^��Head�p�ϐ�
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
				// TODO �Đڑ�
				this.isRunning = false;
				break;
			}
			Telegram telegram = TelegramUtil.recovryTelegram(telegramBytes);

			int telegramKind = telegram.getObjHeader().getByteTelegramKind();
			int requestKind = telegram.getObjHeader().getByteRequestKind();

			if (Common.BYTE_TELEGRAM_KIND_ALERT == telegramKind) {
				// �ʒm��M�������s��
				statsJavelinEditor_.listeningGraphicalViewer(telegram);
			} else if (Common.BYTE_TELEGRAM_KIND_GET == telegramKind
					&& Common.BYTE_REQUEST_KIND_RESPONSE == requestKind) {
				// ������M�������s���B
				statsJavelinEditor_.addResponseTelegram(telegram);
				System.out.println("�d����M[" + requestKind + "]");
			} else {
				// TODO ���O�o��
				System.out.println("����`�̗v��������ʂ���M���܂����B[" + requestKind + "]");
			}
		}

	}

	/**
	 * �T�[�o����f�[�^��ǂݍ���
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

		// �w�b�_�������Ȃ��ꍇ�͂��̂܂ܕԂ��B
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
