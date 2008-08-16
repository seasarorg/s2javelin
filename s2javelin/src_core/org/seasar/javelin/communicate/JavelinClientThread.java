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
                // 要求を受信する。
                byte[] byteInputArr = null;
                byteInputArr = this.clientConnection_.recvRequest();

                // byte列をTelegramに変換する。
                Telegram request = S2TelegramUtil.recoveryTelegram(byteInputArr);

                if (SystemLogger.getInstance().isDebugEnabled())
                {
                    this.clientConnection_.logTelegram("電文を受信しました。", request, byteInputArr);
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
            SystemLogger.getInstance().warn("受信電文処理中に例外が発生しました。", exception);
        }
        finally
        {
            this.isRunning = false;
            this.clientConnection_.close();
        }
    }

    /**
     * スレッドを停止する。
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
