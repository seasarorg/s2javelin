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

            try
            {
                this.clientConnection_.send(telegramArray);

                if (SystemLogger.getInstance().isDebugEnabled())
                {
                    Telegram telegram = S2TelegramUtil.recoveryTelegram(telegramArray);
                    this.clientConnection_.logTelegram("ìdï∂ÇëóêMÇµÇ‹ÇµÇΩÅB", telegram, telegramArray);
                }
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}