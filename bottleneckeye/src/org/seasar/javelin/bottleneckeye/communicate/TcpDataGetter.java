package org.seasar.javelin.bottleneckeye.communicate;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
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
                }
                catch (UnknownHostException objUnknownHostException)
                {
                    // エラーメッセージを出す
                    System.out.println("サーバへの接続に失敗しました。サーバアドレス、ポートを通りに設定していることを確認してください。");
                    return;
                    //            return false;
                }
                catch (IOException objIOException)
                {
                    // エラーメッセージを出す
                    System.out.println("サーバへの接続に失敗しました。サーバアドレス、ポートが正しく設定されていることを確認してください。");
                    return;
                    //            return false;
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
                    //            return false;
                }

                //        return true;
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
    public void close()
    {
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                if (TcpDataGetter.this.telegramReader_ != null)
                {
                    TcpDataGetter.this.telegramReader_.setRunning(false);
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
                    TcpDataGetter.this.telegramReader_.sendDisconnectNotify();
                }
                catch (IOException objIOException)
                {
                    // エラーを出す
                    objIOException.printStackTrace();
                }
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
     * @param editor TcpStatsVisionEditor
     */
    public void startRead()
    {
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                TcpDataGetter.this.telegramReader_ = new TelegramReader(TcpDataGetter.this);
                for (EditorTabInterface editorTab : TcpDataGetter.this.editorTabList_)
                {
                    TcpDataGetter.this.telegramReader_.addEditorTab(editorTab);
                }

                Thread readerThread =
                        new Thread(TcpDataGetter.this.telegramReader_, "BeyeReaderThread-"
                                + TcpDataGetter.this.hostName_ + ":"
                                + TcpDataGetter.this.portNumber_);

                readerThread.start();
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
}
