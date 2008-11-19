package org.seasar.javelin.communicate;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.seasar.javelin.S2JavelinConfig;
import org.seasar.javelin.SystemLogger;
import org.seasar.javelin.communicate.entity.Telegram;

public class JavelinClientThread implements Runnable
{
    JavelinClientConnection        clientConnection_;

    private boolean                isRunning;

    /** �d�������N���X�̃��X�g */
    private List<TelegramListener> telegramListenerList_ = new ArrayList<TelegramListener>();

    public JavelinClientThread(Socket objSocket)
        throws IOException
    {
        this.clientConnection_ = new JavelinClientConnection(objSocket);

        // �d�������N���X��o�^����
        registerTelegramListeners(new S2JavelinConfig());
    }

    public void run()
    {
        try
        {
            // ���M�X���b�h���J�n����B
            startSendThread();

            this.isRunning = true;
            while (this.isRunning)
            {
                // �v������M����B
                byte[] byteInputArr = null;
                byteInputArr = this.clientConnection_.recvRequest();

                // byte���Telegram�ɕϊ�����B
                Telegram request = S2TelegramUtil.recoveryTelegram(byteInputArr);

                if (SystemLogger.getInstance().isDebugEnabled())
                {
                    this.clientConnection_.logTelegram("�d������M���܂����B", request, byteInputArr);
                }

                if (request == null)
                {
                    continue;
                }

                this.receiveTelegram(request);
            }
        }
        catch (Exception exception)
        {
            SystemLogger.getInstance().warn("��M�d���������ɗ�O���������܂����B", exception);
        }
        finally
        {
            this.isRunning = false;
            this.clientConnection_.close();
        }
    }

    private void startSendThread()
    {
        JavelinClientSendRunnable clientSendRunnable =
                new JavelinClientSendRunnable(this.clientConnection_);
        String threadName = Thread.currentThread().getName() + "-Send";
        Thread clientSendThread = new Thread(clientSendRunnable, threadName);
        clientSendThread.start();
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
                this.clientConnection_.sendAlarm(byteOutputArr);
            }
        }
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
                if ("".equals(listenerName))
                {
                    continue;
                }

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
     * �X���b�h���~����B
     */
    public void stop()
    {
        this.isRunning = false;
    }

    public boolean isClosed()
    {
        return this.clientConnection_.isClosed();
    }

    public void sendAlarm(byte[] bytes)
    {
        this.clientConnection_.sendAlarm(bytes);
    }

    public void logTelegram(String string, Telegram telegram, byte[] bytes)
    {
        this.clientConnection_.logTelegram(string, telegram, bytes);
    }
}
