package org.seasar.javelin.communicate;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.JavelinErrorLogger;
import org.seasar.javelin.S2StatsJavelinRecorder;
import org.seasar.javelin.bean.Invocation;
import org.seasar.javelin.communicate.entity.Telegram;

public class JavelinAcceptThread implements Runnable, AlarmListener
{
    private static final int   MAX_SOCKET      = 30;

    static JavelinAcceptThread instance_       = new JavelinAcceptThread();

    ServerSocket               objServerSocket = null;

    Socket                     clientSocket    = null;

    List<JavelinClientThread>  clientList      = new ArrayList<JavelinClientThread>();

    boolean                    isRunning       = false;

    private JavelinAcceptThread()
    {
        S2StatsJavelinRecorder.addListener(this);
    }

    public static JavelinAcceptThread getInstance()
    {
        return instance_;
    }

    public void start(int port)
    {
        if (this.objServerSocket != null)
        {
            return;
        }

        try
        {
            this.objServerSocket = new ServerSocket(port);
            JavelinErrorLogger.getInstance().log("S2JavelinのTCP接続受付を開始します。 ポート[" + port + "]");

            // クライアント接続の受付を開始する。
            try
            {
                Thread acceptThread = new Thread(this, "JavelinAcceptThread");
                acceptThread.setDaemon(true);
                acceptThread.start();
            }
            catch (Exception objException)
            {
                JavelinErrorLogger.getInstance().log(objException);
            }
        }
        catch (IOException objIOException)
        {
            JavelinErrorLogger.getInstance().log("ポート[" + port + "]はすでに開かれています。処理を続行します。");
        }
    }

    public void run()
    {
        ThreadGroup group = new ThreadGroup("JavelinThreadGroup");

        isRunning = true;
        while (isRunning)
        {
            try
            {
                accept(group);
            }
            catch (RuntimeException re)
            {
                JavelinErrorLogger.getInstance().log("電文受信中に予期せぬエラーが発生しました。", re);
            }
        }

        synchronized (clientList)
        {
            for (int index = clientList.size() - 1; index >= 0; index--)
            {
                JavelinClientThread client = clientList.get(index);
                client.stop();
            }
        }

        try
        {
            this.objServerSocket.close();
        }
        catch (IOException ioe)
        {
            JavelinErrorLogger.getInstance().log("サーバソケットのクローズに失敗しました。", ioe);
        }
    }

    private void accept(ThreadGroup group)
    {
        try
        {
            // モニター
            clientSocket = objServerSocket.accept();
        }
        catch (IOException ioe)
        {
            JavelinErrorLogger.getInstance().log("サーバソケットのacceptに失敗しました。", ioe);
            return;
        }

        int clientCount = sweepClient();
        if (clientCount > MAX_SOCKET)
        {
            JavelinErrorLogger.getInstance().log("接続数が最大数[" + MAX_SOCKET + "]を超えたため、接続を拒否します。");
            try
            {
                clientSocket.close();
            }
            catch (IOException ioe)
            {
                JavelinErrorLogger.getInstance().log("クライアントソケットのクローズに失敗しました。", ioe);
            }
            return;
        }

        InetAddress clientIP = clientSocket.getInetAddress();
        JavelinErrorLogger.getInstance().log("クライアントから接続されました。IP:[" + clientIP + "]");

        // クライアントからの要求受付用に、処理スレッドを起動する。
        JavelinClientThread clientRunnable;
        try
        {
            clientRunnable = new JavelinClientThread(clientSocket);
            Thread objHandleThread = new Thread(group, clientRunnable, "JavelinClientThread-"
                    + clientCount);
            objHandleThread.setDaemon(true);
            objHandleThread.start();

            // 通知のためのクライアントリストに追加する。
            synchronized (clientList)
            {
                clientList.add(clientRunnable);
            }
        }
        catch (IOException ioe)
        {
            JavelinErrorLogger.getInstance().log("クライアント通信スレッドの生成に失敗しました。", ioe);
        }

    }

    private int sweepClient()
    {
        int size;
        synchronized (clientList)
        {
            for (int index = clientList.size() - 1; index >= 0; index--)
            {
                JavelinClientThread client = clientList.get(index);
                if (client.isClosed())
                {
                    clientList.remove(index);
                }
            }
            size = clientList.size();
        }

        return size;
    }

    public void sendExceedThresholdAlarm(CallTreeNode node)
    {
        Invocation invocation = node.getInvocation();
        Telegram objTelegram = TelegramUtil.create(Arrays.asList(new Object[]{invocation}),
                                                   Common.BYTE_TELEGRAM_KIND_ALERT,
                                                   Common.BYTE_REQUEST_KIND_NOTIFY);

        // 赤くブリンクデータを取る
        byte[] byteExceedThresholdAlarmArr = TelegramUtil.createTelegram(objTelegram);

        // 赤くブリンクデータを送る
        synchronized (clientList)
        {
            for (int index = clientList.size() - 1; index >= 0; index--)
            {
                JavelinClientThread client = clientList.get(index);
                client.sendAlarm(byteExceedThresholdAlarmArr);
            }
        }
    }

    public void stop()
    {
        this.isRunning = false;
    }

    /**
     * このAlarmListenerがルートノードのみを処理するかどうかを返す。
     * ※このクラスでは、常にfalseを返す。
     * 
     * @see org.seasar.javelin.communicate.AlarmListener#isSendingRootOnly()
     */
    public boolean isSendingRootOnly()
    {
        return false;
    }
}
