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

public class JavelinClientThread implements Runnable
{

    Socket                         clientSocket_          = null;

    InetAddress                    clientIP_              = null;

    BufferedInputStream            inputStream_           = null;

    BufferedOutputStream           outputStream_         = null;

    private boolean                isRunning;

    /** 電文処理クラスのリスト */
    private List<TelegramListener> telegramListenerList_ = new ArrayList<TelegramListener>();

    public JavelinClientThread(Socket objSocket)
        throws IOException
    {
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

        // 電文処理クラスを登録する
        registerTelegramListeners(new S2JavelinConfig());
    }

    public void run()
    {
        try
        {
            this.isRunning = true;
            while (this.isRunning)
            {
                // 要求を受信する。
                byte[] byteInputArr = null;
                byteInputArr = recvRequest();

                // byte列をTelegramに変換する。
                Telegram request = S2TelegramUtil.recoveryTelegram(byteInputArr);

                if (SystemLogger.getInstance().isDebugEnabled())
                {
                    logTelegram("電文を受信しました。", request);
                }

                if(request == null)
                {
                    continue;
                }
                
                // 各TelegramListenerで処理を行う
                for (TelegramListener listener : this.telegramListenerList_)
                {
                    Telegram response = listener.receiveTelegram(request);

                    // 応答電文がある場合のみ、応答を返す
                    if (response != null)
                    {
                        byte[] byteOutputArr = S2TelegramUtil.createTelegram(response);
                        synchronized (this.outputStream_)
                        {
                            this.outputStream_.write(byteOutputArr);
                            this.outputStream_.flush();
                        }

                        if (SystemLogger.getInstance().isDebugEnabled())
                        {
                            logTelegram("電文を送信しました。", response);
                        }
                    }
                }
            }
        }
        catch (Exception exception)
        {
            SystemLogger.getInstance().warn("受信電文処理中に例外が発生しました。", exception);
        }
        finally
        {
            this.isRunning = false;
            close();
        }
    }

    public void logTelegram(String message, Telegram response)
    {
        String telegramStr = S2TelegramUtil.toPrintStr(response);
        SystemLogger.getInstance().debug(
                                         message + this.clientIP_.getHostAddress() + ":"
                                                 + this.clientSocket_.getPort() + SystemLogger.NEW_LINE
                                                 + telegramStr);
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
            SystemLogger.getInstance().warn("クライアント通信ソケットのクローズに失敗しました。", ioe);
        }

        SystemLogger.getInstance().info("クライアントと切断しました。[" + this.clientIP_ + "]");
    }

    void sendResponse(Telegram requestTelegram)
        throws IOException
    {
        // 応答を送信する。
        byte[] byteOutputArr = S2TelegramUtil.createAll();

        synchronized (this.outputStream_)
        {
            this.outputStream_.write(byteOutputArr);
            this.outputStream_.flush();
        }
    }

    /**
     * 受信電文のbyte配列を返す。
     * 
     * @return byte配列
     * @throws IOException
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

        S2JavelinConfig config = new S2JavelinConfig();
        if (config.isDebug())
        {
            SystemLogger.getInstance().debug("telegramLength  = [" + telegramLength + "]");
        }
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

    // スレッド外から呼ばれる。
    public void sendAlarm(byte[] byteExceedThresholdAlarmArr)
    {
        if (this.clientSocket_.isClosed() == true)
        {
            return;
        }

        try
        {
            synchronized (this.outputStream_)
            {
                this.outputStream_.write(byteExceedThresholdAlarmArr);
                this.outputStream_.flush();
            }
        }
        catch (IOException ioe)
        {
            SystemLogger.getInstance().warn("閾値超過通知電文の送信に失敗しました。", ioe);
            this.close();
        }
    }

    public boolean isClosed()
    {
        return this.clientSocket_.isClosed();
    }

    /**
     * スレッドを停止する。
     */
    public void stop()
    {
        this.isRunning = false;
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
}
