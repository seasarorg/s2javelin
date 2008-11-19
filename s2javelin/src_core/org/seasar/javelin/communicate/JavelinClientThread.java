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

    /** 電文処理クラスのリスト */
    private List<TelegramListener> telegramListenerList_ = new ArrayList<TelegramListener>();

    public JavelinClientThread(Socket objSocket)
        throws IOException
    {
        this.clientConnection_ = new JavelinClientConnection(objSocket);

        // 電文処理クラスを登録する
        registerTelegramListeners(new S2JavelinConfig());
    }

    public void run()
    {
        try
        {
            // 送信スレッドを開始する。
            startSendThread();

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

                this.receiveTelegram(request);
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

    private void startSendThread()
    {
        JavelinClientSendRunnable clientSendRunnable =
                new JavelinClientSendRunnable(this.clientConnection_);
        String threadName = Thread.currentThread().getName() + "-Send";
        Thread clientSendThread = new Thread(clientSendRunnable, threadName);
        clientSendThread.start();
    }

    /**
     * 電文処理に利用するTelegramListenerを登録する
     * 
     * @param listener 電文処理に利用するTelegramListener
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
        // 各TelegramListenerで処理を行う
        for (TelegramListener listener : this.telegramListenerList_)
        {
            Telegram response = listener.receiveTelegram(request);

            // 応答電文がある場合のみ、応答を返す
            if (response != null)
            {
                byte[] byteOutputArr = S2TelegramUtil.createTelegram(response);
                this.clientConnection_.sendAlarm(byteOutputArr);
            }
        }
    }

    /**
     * TelegramListenerのクラスをJavelin設定から読み込み、登録する。 クラスのロードは、以下の順でクラスローダでのロードを試みる。
     * <ol> <li>JavelinClientThreadをロードしたクラスローダ</li> <li>コンテキストクラスローダ</li>
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
                    SystemLogger.getInstance().info(listenerName + "をTelegramListenerとして登録しました。");
                }
                else
                {
                    SystemLogger.getInstance().info(
                                                    listenerName
                                                            + "はTelegramListenerを実装していないため、電文処理に利用しません。");
                }
            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(listenerName + "の登録に失敗したため、電文処理に利用しません。", ex);
            }
        }
    }

    /**
     * クラスをロードする。 以下の順でクラスローダでのロードを試みる。 <ol> <li>JavelinClientThreadをロードしたクラスローダ</li>
     * <li>コンテキストクラスローダ</li> </ol>
     * 
     * @param className ロードするクラスの名前。
     * @return ロードしたクラス。
     * @throws ClassNotFoundException 全てのクラスローダでクラスが見つからない場合
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
            SystemLogger.getInstance().info(className + "のロードに失敗したため、コンテキストクラスローダからのロードを行います。");
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        }

        return clazz;
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
