package org.seasar.javelin.communicate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.seasar.javelin.S2JavelinConfig;
import org.seasar.javelin.SystemLogger;
import org.seasar.javelin.communicate.entity.Header;
import org.seasar.javelin.communicate.entity.Telegram;

public class JavelinClientConnection
{
    Socket                         clientSocket_         = null;

    InetAddress                    clientIP_             = null;

    BufferedInputStream            inputStream_          = null;

    BufferedOutputStream           outputStream_         = null;

    private BlockingQueue<byte[]>  queue_;

    /** �d�������N���X�̃��X�g */
    private List<TelegramListener> telegramListenerList_ = new ArrayList<TelegramListener>();

    public JavelinClientConnection(Socket objSocket)
        throws IOException
    {
        this.queue_ = new ArrayBlockingQueue<byte[]>(1000);
        this.clientSocket_ = objSocket;
        this.clientIP_ = this.clientSocket_.getInetAddress();
        try
        {
            this.inputStream_ = new BufferedInputStream(this.clientSocket_.getInputStream());
            this.outputStream_ = new BufferedOutputStream(this.clientSocket_.getOutputStream());
        }
        catch (IOException ioe)
        {
            close();
            throw ioe;
        }

        // �d�������N���X��o�^����
        registerTelegramListeners(new S2JavelinConfig());
    }

    void close()
    {
        try
        {
            if (this.clientSocket_ != null)
            {
                this.clientSocket_.close();
            }
        }
        catch (IOException ioe)
        {
            SystemLogger.getInstance().warn("�N���C�A���g�ʐM�\�P�b�g�̃N���[�Y�Ɏ��s���܂����B", ioe);
        }

        SystemLogger.getInstance().info("�N���C�A���g�Ɛؒf���܂����B[" + this.clientIP_ + "]");
    }

    public void send(byte[] byteOutputArr)
        throws IOException
    {
        if (this.clientSocket_.isClosed() == true)
        {
            return;
        }

        synchronized (this.outputStream_)
        {
            this.outputStream_.write(byteOutputArr);
            this.outputStream_.flush();
        }
    }

    public void logTelegram(String message, Telegram response, byte[] telegramByteArray)
    {
        String telegramStr = S2TelegramUtil.toPrintStr(response, telegramByteArray);
        SystemLogger.getInstance().debug(
                                         message + this.clientIP_.getHostAddress() + ":"
                                                 + this.clientSocket_.getPort()
                                                 + SystemLogger.NEW_LINE + telegramStr);
    }

    /**
     * ��M�d����byte�z���Ԃ��B
     * 
     * @return byte�z��
     * @throws IOException ���o�͗�O�̔���
     */
    byte[] recvRequest()
        throws IOException
    {
        byte[] header = new byte[Header.HEADER_LENGTH];

        int intInputCount = this.inputStream_.read(header);
        if (intInputCount < 0)
        {
            throw new IOException();
        }
        ByteBuffer headerBuffer = ByteBuffer.wrap(header);
        int telegramLength = headerBuffer.getInt();

        if (telegramLength - Header.HEADER_LENGTH < 0)
        {
            throw new IOException();
        }

        SystemLogger.getInstance().debug("telegramLength  = [" + telegramLength + "]");

        byte[] telegram = new byte[telegramLength];
        intInputCount =
                this.inputStream_.read(telegram, Header.HEADER_LENGTH, telegramLength
                        - Header.HEADER_LENGTH);
        if (intInputCount < 0)
        {
            throw new IOException();
        }

        System.arraycopy(header, 0, telegram, 0, Header.HEADER_LENGTH);
        return telegram;
    }

    // �X���b�h�O����Ă΂��B
    public void sendAlarm(byte[] telegramArray)
    {
        this.queue_.offer(telegramArray);
    }

    public boolean isClosed()
    {
        return this.clientSocket_.isClosed();
    }

    /**
     * TelegramListener�̃N���X��Javelin�ݒ肩��ǂݍ��݁A�o�^����B �N���X�̃��[�h�́A�ȉ��̏��ŃN���X���[�_�ł̃��[�h�����݂�B
     * <ol> <li>JavelinClientThread�����[�h�����N���X���[�_</li> <li>�R���e�L�X�g�N���X���[�_</li>
     * </ol>
     * 
     * @param config
     */
    private void registerTelegramListeners(S2JavelinConfig config)
    {
        String[] listeners = config.getTelegramListeners().split(",");
        for (String listenerName : listeners)
        {
            try
            {
                Class<?> listenerClass = loadClass(listenerName);
                Object listener = listenerClass.newInstance();
                if (listener instanceof TelegramListener)
                {
                    addListener((TelegramListener)listener);
                    SystemLogger.getInstance().info(listenerName + "��TelegramListener�Ƃ��ēo�^���܂����B");
                }
                else
                {
                    SystemLogger.getInstance().info(
                                                    listenerName
                                                            + "��TelegramListener���������Ă��Ȃ����߁A�d�������ɗ��p���܂���B");
                }
            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(listenerName + "�̓o�^�Ɏ��s�������߁A�d�������ɗ��p���܂���B", ex);
            }
        }
    }

    /**
     * �N���X�����[�h����B �ȉ��̏��ŃN���X���[�_�ł̃��[�h�����݂�B <ol> <li>JavelinClientThread�����[�h�����N���X���[�_</li>
     * <li>�R���e�L�X�g�N���X���[�_</li> </ol>
     * 
     * @param className ���[�h����N���X�̖��O�B
     * @return ���[�h�����N���X�B
     * @throws ClassNotFoundException �S�ẴN���X���[�_�ŃN���X��������Ȃ��ꍇ
     */
    private Class<?> loadClass(String className)
        throws ClassNotFoundException
    {

        Class<?> clazz;
        try
        {
            clazz = Class.forName(className);
        }
        catch (ClassNotFoundException cnfe)
        {
            SystemLogger.getInstance().info(className + "�̃��[�h�Ɏ��s�������߁A�R���e�L�X�g�N���X���[�_����̃��[�h���s���܂��B");
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        }

        return clazz;
    }

    /**
     * �d�������ɗ��p����TelegramListener��o�^����
     * 
     * @param listener �d�������ɗ��p����TelegramListener
     */
    public void addListener(TelegramListener listener)
    {
        synchronized (this.telegramListenerList_)
        {
            this.telegramListenerList_.add(listener);
        }
    }

    void receiveTelegram(Telegram request)
        throws Exception,
            IOException
    {
        // �eTelegramListener�ŏ������s��
        for (TelegramListener listener : this.telegramListenerList_)
        {
            Telegram response = listener.receiveTelegram(request);

            // �����d��������ꍇ�̂݁A������Ԃ�
            if (response != null)
            {
                byte[] byteOutputArr = S2TelegramUtil.createTelegram(response);
                queue_.offer(byteOutputArr);
            }
        }
    }

    byte[] take()
        throws InterruptedException
    {
        return queue_.take();

    }
}
