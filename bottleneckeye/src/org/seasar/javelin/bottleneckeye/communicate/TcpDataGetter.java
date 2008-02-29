package org.seasar.javelin.bottleneckeye.communicate;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.seasar.javelin.bottleneckeye.editors.EditorTabInterface;
import org.seasar.javelin.bottleneckeye.editors.view.TcpStatsVisionEditor;

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

    /** ホスト名 */
    private String                   hostName_;

    /** ポート番号 */
    private int                      portNumber_;

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
    public boolean open()
    {
        try
        {
            // サーバに接続する
            SocketAddress remote = new InetSocketAddress(this.hostName_, this.portNumber_);
            this.socketChannel_ = SocketChannel.open(remote);
            // 接続中のメッセージ
            System.out.println("\nサーバに接続しました:" + remote);

        }
        catch (UnknownHostException objUnknownHostException)
        {
            // エラーメッセージを出す
            System.out.println("サーバへの接続に失敗しました。サーバアドレス、ポートを通りに設定していることを確認してください。");
            return false;
        }
        catch (IOException objIOException)
        {
            // エラーメッセージを出す
            System.out.println("サーバへの接続に失敗しました。サーバアドレス、ポートが正しく設定されていることを確認してください。");
            return false;
        }

        try
        {
            this.objPrintStream_ = new PrintStream(this.socketChannel_.socket().getOutputStream(),
                                                   true);
        }
        catch (IOException objIOException)
        {
            // エラーメッセージを出す
            objIOException.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * サーバに接続を除く
     */
    public void close()
    {
        if (this.telegramReader_ != null)
        {
            this.telegramReader_.setRunning(false);
        }

        // 使用した通信対象をクリアする
        if (this.objPrintStream_ != null)
        {
            this.objPrintStream_.close();
            this.objPrintStream_ = null;
        }

        try
        {
            if (this.socketChannel_ != null)
            {
                this.socketChannel_.close();
                this.socketChannel_ = null;
            }

            System.out.println("サーバとの通信を終了しました。");
        }
        catch (IOException objIOException)
        {
            // エラーを出す
            objIOException.printStackTrace();
        }
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
        this.telegramReader_ = new TelegramReader(this);
        for (EditorTabInterface editorTab : this.editorTabList_)
        {
            this.telegramReader_.addEditorTab(editorTab);
        }

        Thread readerThread = new Thread(this.telegramReader_, "StatsReaderThread");
        readerThread.start();
    }

    /**
     * サーバに電文を送信する。
     *
     * @param telegram 電文
     */
    public void sendTelegram(Telegram telegram)
    {
        byte[] byteOutputArray = TelegramUtil.createTelegram(telegram);

        try
        {
            if (this.objPrintStream_ != null)
            {
                this.objPrintStream_.write(byteOutputArray);
                this.objPrintStream_.flush();
                // 強制終了が行われたとき、再接続を行う
                if (this.objPrintStream_.checkError())
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
            this.close();
        }
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
