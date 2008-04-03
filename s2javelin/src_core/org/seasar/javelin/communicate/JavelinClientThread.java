package org.seasar.javelin.communicate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.seasar.javelin.SystemLogger;
import org.seasar.javelin.S2JavelinConfig;
import org.seasar.javelin.communicate.entity.Header;
import org.seasar.javelin.communicate.entity.Telegram;


public class JavelinClientThread implements Runnable {

	Socket clientSocket = null;

	InetAddress clientIP = null;

	BufferedInputStream inputStream = null;

	BufferedOutputStream outputStream_ = null;

	private boolean isRunning;
	
	/** �d�������N���X�̃��X�g */
	private List<TelegramListener> telegramListenerList_
	    = new ArrayList<TelegramListener>();
	

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
		
		// �d�������N���X��o�^����
		registerTelegramListeners(new S2JavelinConfig());
	}

	public void run()
	{
		try
		{
			this.isRunning = true;
			while (this.isRunning)
			{
				// �v������M����B
				byte[] byteInputArr = null;
				byteInputArr = recvRequest();

				// byte���Telegram�ɕϊ�����B
				Telegram request = TelegramUtil
						.recoveryTelegram(byteInputArr);

				if (SystemLogger.getInstance().isDebugEnabled())
                {
                    logTelegram("�d������M���܂����B", request);
                }
				
				// �eTelegramListener�ŏ������s��
				for (TelegramListener listener : this.telegramListenerList_)
				{
					Telegram response = listener.receiveTelegram(request);
					
					// �����d��������ꍇ�̂݁A������Ԃ�
					if (response != null)
					{
						byte[] byteOutputArr = TelegramUtil.createTelegram(response);
						synchronized (outputStream_)
						{
							outputStream_.write(byteOutputArr);
							outputStream_.flush();
						}

		                if (SystemLogger.getInstance().isDebugEnabled())
		                {
		                    logTelegram("�d���𑗐M���܂����B", response);
		                }
					}
				}
			}
		}
		catch (Exception exception)
		{
			SystemLogger.getInstance().warn("��M�d���������ɗ�O���������܂����B",
				exception);
		}
		finally
		{
			this.isRunning = false;
			close();
		}
	}

    public void logTelegram(String message, Telegram response)
    {
        String telegramStr = TelegramUtil.toPrintStr(response);
        SystemLogger.getInstance().debug(
                                         message
                                                 + clientIP.getHostAddress()
                                                 + ":" + clientSocket.getPort()
                                                 + SystemLogger.NEW_LINE
                                                 + telegramStr);
    }

	void close() {
		try {
			if (this.clientSocket != null) {
				this.clientSocket.close();
			}
		} catch (IOException ioe) {
			SystemLogger.getInstance().warn("�N���C�A���g�ʐM�\�P�b�g�̃N���[�Y�Ɏ��s���܂����B",
					ioe);
		}

		SystemLogger.getInstance()
				.info("�N���C�A���g�Ɛؒf���܂����B[" + clientIP + "]");
	}

	void sendResponse(Telegram requestTelegram) throws IOException {
		// �����𑗐M����B
		byte[] byteOutputArr = TelegramUtil.createAll();

		synchronized (outputStream_) {
			outputStream_.write(byteOutputArr);
			outputStream_.flush();
		}
	}

	/**
	 * ��M�d����byte�z���Ԃ��B
	 * @return byte�z��
	 * @throws IOException
	 */
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
			SystemLogger.getInstance().debug(
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
			SystemLogger.getInstance().warn("臒l���ߒʒm�d���̑��M�Ɏ��s���܂����B", ioe);
			this.close();
		}
	}

	public boolean isClosed() {
		return this.clientSocket.isClosed();
	}

	public void stop() {
		this.isRunning = false;
	}
	

	/**
	 * TelegramListener�̃N���X��Javelin�ݒ肩��ǂݍ��݁A�o�^����B �N���X�̃��[�h�́A�ȉ��̏��ŃN���X���[�_�ł̃��[�h�����݂�B
	 * <ol>
	 * <li>JavelinClientThread�����[�h�����N���X���[�_</li>
	 * <li>�R���e�L�X�g�N���X���[�_</li>
	 * </ol>
	 * 
	 * @param config
	 */
	private void registerTelegramListeners(S2JavelinConfig config) {
		String[] listeners = config.getTelegramListeners().split(",");
		for (String listenerName : listeners) {
			try {
				Class<?> listenerClass = loadClass(listenerName);
				Object listener = listenerClass.newInstance();
				if (listener instanceof TelegramListener) {
					addListener((TelegramListener) listener);
					SystemLogger.getInstance().info(
						listenerName + "��TelegramListener�Ƃ��ēo�^���܂����B");
				} else {
					SystemLogger.getInstance().info(
						listenerName + "��TelegramListener���������Ă��Ȃ����߁A�d�������ɗ��p���܂���B");
				}
			} catch (Exception ex) {
				SystemLogger.getInstance().warn(
					listenerName + "�̓o�^�Ɏ��s�������߁A�d�������ɗ��p���܂���B", ex);
			}
		}
	}
	
	/**
	 * �N���X�����[�h����B �ȉ��̏��ŃN���X���[�_�ł̃��[�h�����݂�B
	 * <ol>
	 * <li>JavelinClientThread�����[�h�����N���X���[�_</li>
	 * <li>�R���e�L�X�g�N���X���[�_</li>
	 * </ol>
	 * 
	 * @param className
	 *            ���[�h����N���X�̖��O�B
	 * @return ���[�h�����N���X�B
	 * @throws ClassNotFoundException
	 *             �S�ẴN���X���[�_�ŃN���X��������Ȃ��ꍇ
	 */
	private Class<?> loadClass(String className)
			throws ClassNotFoundException {

		Class<?> clazz;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException cnfe) {
			SystemLogger.getInstance().info(
					className + "�̃��[�h�Ɏ��s�������߁A�R���e�L�X�g�N���X���[�_����̃��[�h���s���܂��B");
			clazz = Thread.currentThread().getContextClassLoader().loadClass(
					className);
		}

		return clazz;
	}
	
	/**
	 * �d�������ɗ��p����TelegramListener��o�^����
	 * 
	 * @param listener �d�������ɗ��p����TelegramListener
	 */
	public void addListener(TelegramListener listener) {
		synchronized (telegramListenerList_) {
			telegramListenerList_.add(listener);
		}
	}
}
