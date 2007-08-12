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
	 * �ʐM�\�P�b�g
	 */
	SocketChannel socketChannel = null;

	/**
	 * �o�͗�
	 */
	PrintStream objPrintStream = null;

	/**
	 * �g���Ă���Editor
	 */
	StatsVisionEditor statsJavelinEditor = null;
	
	public TcpDataGetter() {
	}


	public void setStatsJavelinEditor(StatsVisionEditor statsJavelinEditor) {
		this.statsJavelinEditor = statsJavelinEditor;
	}

	/**
	 * �T�[�o�ɐڑ�����B
	 */
	public void open() {
		try {
			// �T�[�o�ɐڑ�����
			SocketAddress remote = new InetSocketAddress(statsJavelinEditor.getHostName(),
					statsJavelinEditor.getPortNum());
			socketChannel = SocketChannel.open(remote);
			// �ڑ����̃��b�Z�[�W
			System.out.println("\n�T�[�o�ɐڑ����܂���:" + remote);

		} catch (UnknownHostException objUnknownHostException) {
			// �G���[���b�Z�[�W���o��
			System.out.println(
					"�T�[�o�ɐڑ�����͎̂��s���܂����B�T�[�o�A�h���X�A�|�[�g��ʂ�ɐݒ肵�Ă��邱�Ƃ��m�F���������B");
			return;
		} catch (IOException objIOException) {
			// �G���[���b�Z�[�W���o��
			System.out.println(
					"�T�[�o�ɐڑ�����͎̂��s���܂����B�T�[�o�A�h���X�A�|�[�g��ʂ�ɐݒ肵�Ă��邱�Ƃ��m�F���������B");
			return;
		}

		try {
			objPrintStream = new PrintStream(socketChannel.socket()
					.getOutputStream(), true);
		} catch (IOException objIOException) {
			// �G���[���b�Z�[�W���o��
			objIOException.printStackTrace();
		}
	}

	/**
	 * �T�[�o�ɐڑ�������
	 */
	public void close() {
		try {
			telegramReader.setRunning(false);

			// �g�p�����ʐM�Ώۂ��N���A����
			objPrintStream.close();
			socketChannel.close();
			
			System.out.println("�T�[�o�ƒʐM���I������܂����B");
		} catch (IOException objIOException) {
			// �G���[���o��
			objIOException.printStackTrace();
		}
	}

	/**
	 * �T�[�o�ɏ�Ԏ擾�d���𑗂�
	 */
	public void request() {
		// �o�͗��f�[�^���i�[
		byte[] byteOutputArr = null;

		// �����f�[�^�Ώۂ�����āA�f�[�^��ݒ肷��
		Header objHeader = new Header();
		objHeader.setByteTelegramKind(Common.BYTE_TELEGRAM_KIND_GET);
		objHeader.setByteRequestKind(Common.BYTE_REQUEST_KIND_REQUEST);

		// ������d���Ώۂɐݒ肷��
		Telegram objOutputTelegram = new Telegram();
		objOutputTelegram.setObjHeader(objHeader);

		// �d���́Aobject �� byte[] �ɕϊ�����
		byteOutputArr = TelegramUtil.createTelegram(objOutputTelegram);

		try {
			// �o�͗����o�͂���
			objPrintStream.write(byteOutputArr);
		} catch (IOException objIOException) {
			// �G���[���b�Z�[�W���o��
			objIOException.printStackTrace();
			this.close();
		}
	}

	/**
	 * �T�[�o�Ƀ��Z�b�g�d���𑗂�
	 */
	public void sendReset() {
		// �o�͗��f�[�^���i�[
		byte[] byteOutputArr = null;

		// �����f�[�^�Ώۂ�����āA�f�[�^��ݒ肷��
		Header objHeader = new Header();
		objHeader.setByteTelegramKind(Common.BYTE_TELEGRAM_KIND_RESET);
		objHeader.setByteRequestKind(Common.BYTE_REQUEST_KIND_NOTIFY);

		// ������d���Ώۂɐݒ肷��
		Telegram objOutputTelegram = new Telegram();
		objOutputTelegram.setObjHeader(objHeader);

		// �d���́Aobject �� byte[] �ɕϊ�����
		byteOutputArr = TelegramUtil.createTelegram(objOutputTelegram);

		try {
			// �o�͗����o�͂���
			objPrintStream.write(byteOutputArr);
		} catch (IOException objIOException) {
			// �G���[���b�Z�[�W���o��
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
