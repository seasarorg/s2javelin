package org.seasar.javelin.communicate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.seasar.javelin.JavelinErrorLogger;
import org.seasar.javelin.MBeanManager;
import org.seasar.javelin.S2JavelinConfig;
import org.seasar.javelin.communicate.entity.Header;
import org.seasar.javelin.communicate.entity.Telegram;


public class JavelinClientThread implements Runnable {

	Socket clientSocket = null;

	InetAddress clientIP = null;

	BufferedInputStream inputStream = null;

	BufferedOutputStream outputStream_ = null;

	private boolean isRunning;

	public JavelinClientThread(Socket objSocket) throws IOException {
		this.clientSocket = objSocket;
		this.clientIP = clientSocket.getInetAddress();
		try {
			inputStream = new BufferedInputStream(clientSocket.getInputStream());
			outputStream_ = new BufferedOutputStream(clientSocket
					.getOutputStream());
		} catch (IOException ioe) {
			close();
			throw ioe;
		}
	}

	public void run() {
		try {
			this.isRunning = true;
			while (this.isRunning) {
				// �v������M����B
				byte[] byteInputArr = null;
				try {
					byteInputArr = recvRequest();
				} catch (IOException ioe) {
					JavelinErrorLogger.getInstance().log("������M���ɗ�O���������܂����B",
							ioe);
					break;
				}

				// byte���Telegram�ɕϊ�����B
				Telegram requestTelegram = TelegramUtil
						.recoveryTelegram(byteInputArr);

				byte byteTelegramKind = requestTelegram.getObjHeader()
						.getByteTelegramKind();
				byte byteRequestKind = requestTelegram.getObjHeader()
						.getByteRequestKind();
				if (byteTelegramKind == Common.BYTE_TELEGRAM_KIND_GET
						&& byteRequestKind == Common.BYTE_REQUEST_KIND_REQUEST) {
					try {
						// �����𑗐M����B
						sendResponse(requestTelegram);
					} catch (IOException ioe) {
						JavelinErrorLogger.getInstance().log(
								"�������M���ɗ�O���������܂����B", ioe);
						break;
					}
				} else if (byteTelegramKind == Common.BYTE_TELEGRAM_KIND_RESET) {
					// ���Z�b�g����B
					MBeanManager.reset();
				} else {
					String telegramStr = TelegramUtil.printTelegram(0,
							requestTelegram);
					JavelinErrorLogger.getInstance().log(
							"�N���C�A���g�����M�����d�����s���Ȃ��߁A�������܂���B[" + clientIP + "]\n"
									+ telegramStr);

				}

			}
		} finally {
			this.isRunning = false;
			close();
		}
	}

	void close() {
		try {
			if (this.clientSocket != null) {
				this.clientSocket.close();
			}
		} catch (IOException ioe) {
			JavelinErrorLogger.getInstance().log("�N���C�A���g�ʐM�\�P�b�g�̃N���[�Y�Ɏ��s���܂����B",
					ioe);
		}

		JavelinErrorLogger.getInstance()
				.log("�N���C�A���g�Ɛؒf���܂����B[" + clientIP + "]");
	}

	void sendResponse(Telegram requestTelegram) throws IOException {
		// �����𑗐M����B
		byte[] byteOutputArr = TelegramUtil.createAll();

		synchronized (outputStream_) {
			outputStream_.write(byteOutputArr);
			outputStream_.flush();
		}
	}

	byte[] recvRequest() throws IOException {
		byte[] header = new byte[Header.HEADER_LENGTH];

		int intInputCount = inputStream.read(header);
		if (intInputCount < 0) {
			throw new IOException();
		}
		ByteBuffer headerBuffer = ByteBuffer.wrap(header);
		int telegramLength = headerBuffer.getInt();

		if (telegramLength - Header.HEADER_LENGTH < 0) {
			throw new IOException();
		}
		
		S2JavelinConfig config = new S2JavelinConfig();
		if(config.isDebug())
		{
			JavelinErrorLogger.getInstance().log(
					"telegramLength  = [" + telegramLength + "]");
		}
		byte[] telegram = new byte[telegramLength];
		intInputCount = inputStream.read(telegram, Header.HEADER_LENGTH,
				telegramLength - Header.HEADER_LENGTH);
		if (intInputCount < 0) {
			throw new IOException();
		}

		System.arraycopy(header, 0, telegram, 0, Header.HEADER_LENGTH);
		return telegram;
	}

	// �X���b�h�O����Ă΂��B
	public void sendAlarm(byte[] byteExceedThresholdAlarmArr) {
		if (clientSocket.isClosed() == true) {
			return;
		}

		try {
			synchronized (outputStream_) {
				outputStream_.write(byteExceedThresholdAlarmArr);
				outputStream_.flush();
			}
		} catch (IOException ioe) {
			JavelinErrorLogger.getInstance().log("臒l���ߒʒm�d���̑��M�Ɏ��s���܂����B", ioe);
			this.close();
		}

	}

	public boolean isClosed() {
		return this.clientSocket.isClosed();
	}

	public void stop() {
		this.isRunning = false;
	}
}
