package org.seasar.javelin.bottleneckeye.communicate;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.seasar.javelin.bottleneckeye.editors.EditorTabInterface;

public class TcpDataGetter implements TelegramClientManager
{
    /**
     * 通信ソケット
     */
    private SocketChannel            socketChannel_  = null;

    /**
     * 出力流
     */
    private PrintStream              objPrintStream_ = null;

    /** 受信した電文を転送するターゲットオブジェクトのリスト */
    private List<EditorTabInterface> editorTabList_;

    private TelegramReader           telegramReader_;

    private ExecutorService          writeExecutor_  = createThreadPoolExecutor();

    /** ホスト名 */
    private String                   hostName_;

    /** ポート番号 */
    private int                      portNumber_;

    /** 接続状態 */
    private boolean                  isConnect_      = false;
    
    /** start状態 */
    private boolean isStart_ = true;

    private Thread readerThread_;

        public TcpDataGetter()
    {
        this.editorTabList_ = new ArrayList<EditorTabInterface>();
    }

    /**
     * ホスト名をセットする。
     *
     * @param hostName ホスト名
     */
    public void setHostName(String hostName)
    {
        this.hostName_ = hostName;
    }

    /**
     * ポート番号をセットする。
     *
     * @param portNumber ポート番号
     */
    public void setPortNumber(int portNumber)
    {
        this.portNumber_ = portNumber;
    }

    /**
     * サーバに接続する。
     */
    public void open()
    {
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                if (TcpDataGetter.this.isStart() == false)
                {
                    return;
                }
                connect();
            }
        });
    }

    /**
     * サーバに接続する。
     */
    public void start()
    {
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                connect();
                TcpDataGetter.this.isStart_ = true;
            }
        });
    }
    
    private ThreadPoolExecutor createThreadPoolExecutor()
    {
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
                                          public Thread newThread(Runnable r)
                                          {
                                              String name =
                                                      "BeyeWriterThread-"
                                                              + TcpDataGetter.this.hostName_ + ":"
                                                              + TcpDataGetter.this.portNumber_;
                                              return new Thread(r, name);
                                          }
                                      }, new ThreadPoolExecutor.DiscardPolicy());
    }


    /**
     * サーバに接続を除く
     */
    public void stop()
    {
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                closeImpl();
                TcpDataGetter.this.isStart_ = false;
                sendDisconnectNotify();
                notifyCommunicateStop();
            }
        });
    }

    /**
     * 切断されたことを各タブへ通知する。
     */
    public void sendDisconnectNotify()
    {
        synchronized (this)
        {
            for (EditorTabInterface editorTab : this.editorTabList_)
            {
                editorTab.disconnected();
            }
        }
    }
    
    /**
     * 接続が終了したことを各タブに通知する
     */
    private void notifyCommunicateStop()
    {
        synchronized (this)
        {
            for (EditorTabInterface editorTab : this.editorTabList_)
            {
                editorTab.notifyCommunicateStop();
            }
        }
    }    
    /**
     * サーバに接続を除く
     */
    public void close()
    {
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                closeImpl();
            }
        });
    }

    public void shutdown()
    {
        this.telegramReader_.setRunning(false);
        this.close();
        this.writeExecutor_.shutdown();
    }

    /**
     * サーバに状態取得電文を送る
     */
    public void request()
    {
        // 頭部データ対象を作って、データを設定する
        Header objHeader = new Header();
        objHeader.setByteTelegramKind(Common.BYTE_TELEGRAM_KIND_GET);
        objHeader.setByteRequestKind(Common.BYTE_REQUEST_KIND_REQUEST);

        // 頭部を電文対象に設定する
        Telegram objOutputTelegram = new Telegram();
        objOutputTelegram.setObjHeader(objHeader);

        sendTelegram(objOutputTelegram);
    }

    /**
     * サーバにリセット電文を送る。
     */
    public void sendReset()
    {
        // 頭部データ対象を作って、データを設定する
        Header objHeader = new Header();
        objHeader.setByteTelegramKind(Common.BYTE_TELEGRAM_KIND_RESET);
        objHeader.setByteRequestKind(Common.BYTE_REQUEST_KIND_REQUEST);

        // 頭部を電文対象に設定する
        Telegram objOutputTelegram = new Telegram();
        objOutputTelegram.setObjHeader(objHeader);

        sendTelegram(objOutputTelegram);
    }

    /**
     * 受信した電文を転送するオブジェクトをセットする。
     *
     * @param editorTab 転送先オブジェクト
     */
    public void addEditorTab(EditorTabInterface editorTab)
    {
        this.editorTabList_.add(editorTab);
    }

    /**
     * サーバからの電文受信を開始する。
     */
    public void startRead()
    {
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                if (TcpDataGetter.this.readerThread_ != null && TcpDataGetter.this.readerThread_.isAlive())
                {
                    return;
                }
                
                if (TcpDataGetter.this.telegramReader_ == null)
                {
                    initTelegramReader();
                }

                TcpDataGetter.this.readerThread_ =
                        new Thread(TcpDataGetter.this.telegramReader_, "BeyeReaderThread-"
                                + TcpDataGetter.this.hostName_ + ":"
                                + TcpDataGetter.this.portNumber_);

                TcpDataGetter.this.readerThread_.start();
            }
        });
    }

    /**
     * サーバに電文を送信する。
     *
     * @param telegram 電文
     */
    public void sendTelegram(final Telegram telegram)
    {
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                byte[] byteOutputArray = TelegramUtil.createTelegram(telegram);

                try
                {
                    if (TcpDataGetter.this.objPrintStream_ != null)
                    {
                        TcpDataGetter.this.objPrintStream_.write(byteOutputArray);
                        TcpDataGetter.this.objPrintStream_.flush();
                        // 強制終了が行われたとき、再接続を行う
                        if (TcpDataGetter.this.objPrintStream_.checkError())
                        {
                            System.err.println("通信が強制終了しました。");
                            close();
                            startRead();
                        }
                    }
                }
                catch (IOException objIOException)
                {
                    objIOException.printStackTrace();
                    TcpDataGetter.this.close();
                }
            }
        });
    }

    /**
     * ソケットチャネルを取得する
     * @return ソケットチャネル
     */
    public SocketChannel getChannel()
    {
        return this.socketChannel_;
    }

    /**
     * TelegramReaderを初期化する。
     */
    public void initTelegramReader()
    {
        TcpDataGetter.this.telegramReader_ = new TelegramReader(TcpDataGetter.this);
        for (EditorTabInterface editorTab : TcpDataGetter.this.editorTabList_)
        {
            TcpDataGetter.this.telegramReader_.addEditorTab(editorTab);
        }
    }

    /**
     * 接続状態を取得する。
     *
     * @return 接続されているなら <code>true</code> 、そうでないなら <code>false</code>
     */
    public boolean isConnected()
    {
        return this.isConnect_;
    }

    private void logConnectExceptioin(String ip, int port)
    {
        System.out.println("サーバへの接続に失敗しました。サーバアドレス(" + ip + ")、ポート(" + port
                + ")を正しく設定していることを確認してください。");
    }

    /**
     * クローズ処理を行う。
     */
    private void closeImpl()
    {
        if (TcpDataGetter.this.telegramReader_ != null)
        {
            TcpDataGetter.this.readerThread_.interrupt();
        }

        if (TcpDataGetter.this.isConnect_ == false)
        {
            return;
        }

        // 使用した通信対象をクリアする
        if (TcpDataGetter.this.objPrintStream_ != null)
        {
            TcpDataGetter.this.objPrintStream_.close();
            TcpDataGetter.this.objPrintStream_ = null;
        }

        try
        {
            if (TcpDataGetter.this.socketChannel_ != null)
            {
                TcpDataGetter.this.socketChannel_.close();
                TcpDataGetter.this.socketChannel_ = null;
            }

            System.out.println("サーバとの通信を終了しました。");
            TcpDataGetter.this.isConnect_ = false;
            TcpDataGetter.this.sendDisconnectNotify();
        }
        catch (IOException objIOException)
        {
            // エラーを出す
            objIOException.printStackTrace();
        }
    }


    /**
     * 接続されたことを各タブへ通知する。
     */
    public void sendConnectNotify()
    {
        synchronized (this)
        {
            for (EditorTabInterface editorTab : this.editorTabList_)
            {
                editorTab.connected();
            }
        }
    }

    /**
     * 接続が開始されたことを各タブに通知する
     */
    public void notifyCommunicateStart()
    {
        synchronized (this)
        {
            for (EditorTabInterface editorTab : this.editorTabList_)
            {
                editorTab.notifyCommunicateStart();
            }
        }
    }

    private void connect()
    {
        if (TcpDataGetter.this.isConnect_ == true)
        {
            return;
        }

        try
        {
            // サーバに接続する
            SocketAddress remote =
                    new InetSocketAddress(TcpDataGetter.this.hostName_,
                                          TcpDataGetter.this.portNumber_);
            TcpDataGetter.this.socketChannel_ = SocketChannel.open(remote);
            // 接続中のメッセージ
            System.out.println("\nサーバに接続しました:" + remote);
            TcpDataGetter.this.isConnect_ = true;
            if (TcpDataGetter.this.telegramReader_ == null)
            {
                initTelegramReader();
            }
            TcpDataGetter.this.sendConnectNotify();
        }
        catch (UnknownHostException objUnknownHostException)
        {
            // エラーメッセージを出す
            logConnectExceptioin(TcpDataGetter.this.hostName_,
                                 TcpDataGetter.this.portNumber_);
            return;
        }
        catch (IOException objIOException)
        {
            // エラーメッセージを出す
            logConnectExceptioin(TcpDataGetter.this.hostName_,
                                 TcpDataGetter.this.portNumber_);
            return;
        }
        catch (UnresolvedAddressException uae)
        {
            // エラーメッセージを出す
            logConnectExceptioin(TcpDataGetter.this.hostName_,
                                 TcpDataGetter.this.portNumber_);
            return;
        }

        try
        {
            TcpDataGetter.this.objPrintStream_ =
                    new PrintStream(
                                    TcpDataGetter.this.socketChannel_.socket().getOutputStream(),
                                    true);
        }
        catch (IOException objIOException)
        {
            // エラーメッセージを出す
            objIOException.printStackTrace();
            return;
        }
    }

    public boolean isStart()
    {
        return this.isStart_;
    }
}

