package org.seasar.javelin.communicate;

import java.io.IOException;

import org.seasar.javelin.SystemLogger;
import org.seasar.javelin.communicate.entity.Telegram;

public class JavelinClientSendRunnable implements Runnable
{
    JavelinClientConnection clientConnection_;

    public JavelinClientSendRunnable(JavelinClientConnection clientConnection)
    {
        this.clientConnection_ = clientConnection;
    }

    public void run()
    {
        try
        {
            while (true)
            {
                byte[] telegramArray;
                try
                {
                    telegramArray = clientConnection_.take();
                }
                catch (InterruptedException ex)
                {
                    SystemLogger.getInstance().warn(ex);
                    continue;
                }

                if(telegramArray == null || telegramArray.length == 0)
                {
                    // 切断し、スレッドを終了する。
                    this.clientConnection_.close();
                    return;
                }
                
                this.clientConnection_.send(telegramArray);

                if (SystemLogger.getInstance().isDebugEnabled())
                {
                    Telegram telegram = S2TelegramUtil.recoveryTelegram(telegramArray);
                    this.clientConnection_.logTelegram("電文を送信しました。", telegram, telegramArray);
                }
            }
        }
        catch (IOException ex)
        {
            SystemLogger.getInstance().warn("電文送信中に例外が発生しました。", ex);
            this.clientConnection_.close();
        }
    }
}