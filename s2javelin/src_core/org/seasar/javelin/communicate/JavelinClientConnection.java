package org.seasar.javelin.communicate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.seasar.javelin.SystemLogger;
import org.seasar.javelin.communicate.entity.Header;
import org.seasar.javelin.communicate.entity.Telegram;

public class JavelinClientConnection
{
    private static final int      SO_TIMEOUT      = 10000;

    private static final int      SEND_QUEUE_SIZE = 100;

    Socket                        clientSocket_   = null;

    BufferedInputStream           inputStream_    = null;

    BufferedOutputStream          outputStream_   = null;

    private BlockingQueue<byte[]> queue_;

    public JavelinClientConnection(Socket objSocket)
        throws IOException
    {
        this.queue_ = new ArrayBlockingQueue<byte[]>(SEND_QUEUE_SIZE);
        this.clientSocket_ = objSocket;
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
    }

    void close()
    {
        try
        {
            if (this.clientSocket_ != null)
            {
                stopSendThread();
                this.clientSocket_.close();
            }
        }
        catch (IOException ioe)
        {
            SystemLogger.getInstance().warn("クライアント通信ソケットのクローズに失敗しました。", ioe);
        }

        SystemLogger.getInstance().info(
                                        "クライアントと切断しました。[" + this.clientSocket_.getInetAddress()
                                                + "]");
    }

    private void stopSendThread()
    {
        this.sendAlarm(new byte[0]);
    }

    void send(byte[] byteOutputArr)
        throws IOException
    {
        if (this.clientSocket_.isClosed() == true)
        {
            return;
        }

        int headerLength = S2TelegramUtil.TELEGRAM_HEADER_LENGTH;
        this.outputStream_.write(byteOutputArr, 0, headerLength);
        this.outputStream_.flush();

        this.outputStream_.write(byteOutputArr, headerLength, byteOutputArr.length - headerLength);
        this.outputStream_.flush();
    }

    public void logTelegram(String message, Telegram response, byte[] telegramByteArray)
    {
        String telegramStr = S2TelegramUtil.toPrintStr(response, telegramByteArray);
        SystemLogger.getInstance().debug(
                                         message
                                                 + this.clientSocket_.getInetAddress().getHostAddress()
                                                 + ":" + this.clientSocket_.getPort()
                                                 + SystemLogger.NEW_LINE + telegramStr);
    }

    /**
     * 受信電文のbyte配列を返す。
     * 
     * @return byte配列
     * @throws IOException 入出力例外の発生
     */
    byte[] recvRequest()
        throws IOException
    {
        byte[] header = new byte[Header.HEADER_LENGTH];

        this.clientSocket_.setSoTimeout(0);
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
        if (telegramLength > S2TelegramUtil.TELEGRAM_LENGTH_MAX)
        {
            throw new IOException();
        }

        SystemLogger.getInstance().debug("telegramLength  = [" + telegramLength + "]");

        this.clientSocket_.setSoTimeout(SO_TIMEOUT);
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
    public void sendAlarm(byte[] telegramArray)
    {
        boolean offerResult = this.queue_.offer(telegramArray);
        if (offerResult == false && telegramArray != null && telegramArray.length > 0)
        {
            SystemLogger.getInstance().warn("送信キューへの追加に失敗しました。通信を終了します。");
            close();
        }
    }

    public boolean isClosed()
    {
        return this.clientSocket_ == null || this.clientSocket_.isClosed();
    }

    byte[] take()
        throws InterruptedException
    {
        return queue_.take();

    }
}
