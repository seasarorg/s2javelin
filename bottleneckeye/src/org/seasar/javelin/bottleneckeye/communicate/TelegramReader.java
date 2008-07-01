package org.seasar.javelin.bottleneckeye.communicate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.seasar.javelin.bottleneckeye.editors.EditorTabInterface;

/**
 * 電文を受信するクラス。
 */
public class TelegramReader implements Runnable
{
    private volatile boolean         isRunning_;

    /** 電文を転送するターゲットオブジェクトのリスト */
    private List<EditorTabInterface> editorTabList_;

    private SocketChannel            channel_;

    /** サーバ側からのデータのHead用変数 */
    private ByteBuffer               headerBuffer   = ByteBuffer.allocate(Header.HEADER_LENGTH);

    /**　再起動用TcpStatsVisionEditor */
    private TcpDataGetter            tcpDataGetter_ = null;

    /** 前に送った通知が接続通知なら <code>true</code> 、切断通知なら <code>false</code> */
    private boolean                  isPrevNotifyConnect_;

    /** リトライ時間 */
    private static final int         RETRY_INTERVAL = 10000;

    /**
     * 電文を受信するオブジェクトを作成する。
     *
     * @param tcpDataGetter StatsVisionEditor
     * @param channel チャネル
     */
    public TelegramReader(TcpDataGetter tcpDataGetter)
    {
        this.isRunning_ = false;
        this.isPrevNotifyConnect_ = false;
        this.editorTabList_ = new ArrayList<EditorTabInterface>();
        this.tcpDataGetter_ = tcpDataGetter;
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
     * 電文受信ループ。
     */
    public void run()
    {
        this.isRunning_ = true;
        
        while (this.isRunning_)
        {
            notifyCommunicateStart();
            this.channel_ = this.tcpDataGetter_.getChannel();
            if (this.channel_ == null)
            {
                sendDisconnectNotify();
                retry();
                continue;
            }
            sendConnectNotify();

            byte[] telegramBytes = null;
            try
            {
                telegramBytes = this.readTelegramBytes();
            }
            catch (IOException ioe)
            {
                // 切断された
                sendDisconnectNotify();
                retry();
                continue;
            }
            Telegram telegram = TelegramUtil.recoveryTelegram(telegramBytes);

            if (telegram == null)
            {
                System.out.println("受信した電文の読み込みに失敗しました。");
                continue;
            }

            boolean isProcess = false;
            for (EditorTabInterface editorTab : this.editorTabList_)
            {
                isProcess |= editorTab.receiveTelegram(telegram);
            }
            if (isProcess == false)
            {
                // TODO ログ出力
                int requestKind = telegram.getObjHeader().getByteRequestKind();
                System.out.println("未定義の要求応答種別を受信しました。[" + requestKind + "]");
            }
        }

        sendDisconnectNotify();
        notifyCommunicateStop();
    }

    /**
     * サーバからデータを読み込む
     *
     * @return 受信したデータ
     * @throws IOException
     */
    public byte[] readTelegramBytes()
        throws IOException
    {

        int readCount = 0;
        while (readCount < Header.HEADER_LENGTH)
        {
            int count = this.channel_.read(this.headerBuffer);
            if (count < 0)
            {
                throw new IOException();
            }

            readCount += count;
        }

        this.headerBuffer.rewind();
        int telegramLength = this.headerBuffer.getInt();

        // ヘッダ部しかない場合はそのまま返す。
        if (telegramLength <= Header.HEADER_LENGTH)
        {
            this.headerBuffer.rewind();
            return this.headerBuffer.array();
        }

        readCount = 0;
        ByteBuffer bodyBuffer = ByteBuffer.allocate(telegramLength);
        bodyBuffer.put(this.headerBuffer.array());

        while (bodyBuffer.remaining() > 0)
        {
            this.channel_.read(bodyBuffer);
        }

        this.headerBuffer.rewind();
        return bodyBuffer.array();
    }

    public void setRunning(boolean isRunning)
    {
        this.isRunning_ = isRunning;
    }

    /**
     * リトライする。
     */
    private void retry()
    {
        if (this.isRunning_ == false)
        {
            return;
        }

        try
        {
            Thread.sleep(RETRY_INTERVAL);
        }
        catch (InterruptedException ex)
        {
            // 切断時にinterruptする。
        }

        if (this.isRunning_)
        {
            this.tcpDataGetter_.open();
        }
    }

    /**
     * 接続されたことを各タブへ通知する。
     */
    private void sendConnectNotify()
    {
        synchronized (this)
        {
            if (this.isPrevNotifyConnect_ == false)
            {
                this.isPrevNotifyConnect_ = true;
                for (EditorTabInterface editorTab : this.editorTabList_)
                {
                    editorTab.connected();
                }
            }
        }
    }

    /**
     * 切断されたことを各タブへ通知する。
     */
    public void sendDisconnectNotify()
    {
        synchronized (this)
        {
            if (this.isPrevNotifyConnect_ == true)
            {
                this.isPrevNotifyConnect_ = false;
                for (EditorTabInterface editorTab : this.editorTabList_)
                {
                    editorTab.disconnected();
                }
            }
        }
    }

    /**
     * 接続が開始されたことを各タブに通知する
     */
    private void notifyCommunicateStart()
    {
        synchronized (this)
        {
            if (this.isPrevNotifyConnect_ == false)
            {
                this.isPrevNotifyConnect_ = true;
                for (EditorTabInterface editorTab : this.editorTabList_)
                {
                    editorTab.notifyCommunicateStart();
                }
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
            if (this.isPrevNotifyConnect_ == false)
            {
                this.isPrevNotifyConnect_ = true;
                for (EditorTabInterface editorTab : this.editorTabList_)
                {
                    editorTab.notifyCommunicateStop();
                }
            }
        }
    }

}
