package org.seasar.javelin.communicate;

import java.io.IOException;
import java.net.Socket;

import org.seasar.javelin.SystemLogger;
import org.seasar.javelin.communicate.entity.Telegram;

public class JavelinClientThread implements Runnable
{
    JavelinClientConnection clientConnection_;

    private boolean         isRunning;

    public JavelinClientThread(Socket objSocket)
        throws IOException
    {
        this.clientConnection_ = new JavelinClientConnection(objSocket);
    }

    public void run()
    {
        try
        {
            JavelinClientSendRunnable clientSendRunnable =
                    new JavelinClientSendRunnable(this.clientConnection_);
            String threadName = Thread.currentThread().getName() + "-Send";
            Thread clientSendThread = new Thread(clientSendRunnable, threadName);
            clientSendThread.start();

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

                this.clientConnection_.receiveTelegram(request);
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
