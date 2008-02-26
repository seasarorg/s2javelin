package org.seasar.javelin.bottleneckeye.communicate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.seasar.javelin.bottleneckeye.editors.EditorTabInterface;

public class TelegramReader implements Runnable
{
    private boolean                  isRunning_;

    /** 電文を転送するターゲットオブジェクトのリスト */
    private List<EditorTabInterface> editorTabList_;

    private SocketChannel            channel_;

    /** サーバ側からのデータのHead用変数 */
    private ByteBuffer               headerBuffer = ByteBuffer.allocate(Header.HEADER_LENGTH);

    /**
     * 電文を受信するオブジェクトを作成する。
     *
     * @param stasJavelinEditor
     * @param channel
     */
    public TelegramReader(SocketChannel channel)
    {
        this.channel_ = channel;
        this.isRunning_ = false;
        this.editorTabList_ = new ArrayList<EditorTabInterface>();
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
            byte[] telegramBytes = null;
            try
            {
                telegramBytes = this.readTelegramBytes();
            }
            catch (IOException ioe)
            {
                // TODO 再接続
                this.isRunning_ = false;
                break;
            }
            Telegram telegram = TelegramUtil.recoveryTelegram(telegramBytes);
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

            int telegramKind = telegram.getObjHeader().getByteTelegramKind();
            int requestKind = telegram.getObjHeader().getByteRequestKind();

            /*            if (Common.BYTE_TELEGRAM_KIND_ALERT == telegramKind)
                        {
                            // 通知受信処理を行う
                            statsJavelinEditor_.listeningGraphicalViewer(telegram);
                        }
                        else if (Common.BYTE_TELEGRAM_KIND_GET == telegramKind
                                && Common.BYTE_REQUEST_KIND_RESPONSE == requestKind)
                        {
                            // 応答受信処理を行う。
                            statsJavelinEditor_.addResponseTelegram(telegram);
                            System.out.println("電文受信[" + requestKind + "]");
                        }
                        else
                        {
                            // TODO ログ出力
                            System.out.println("未定義の要求応答種別を受信しました。[" + requestKind + "]");
                        }*/
        }

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
            int count = channel_.read(headerBuffer);
            if (count < 0)
            {
                throw new IOException();
            }
            else
            {
                readCount += count;
            }
        }

        headerBuffer.rewind();
        int telegramLength = headerBuffer.getInt();

        // ヘッダ部しかない場合はそのまま返す。
        if (telegramLength <= Header.HEADER_LENGTH)
        {
            headerBuffer.rewind();
            return headerBuffer.array();
        }

        readCount = 0;
        ByteBuffer bodyBuffer = ByteBuffer.allocate(telegramLength);
        bodyBuffer.put(headerBuffer.array());

        while (bodyBuffer.remaining() > 0)
        {
            channel_.read(bodyBuffer);
        }

        headerBuffer.rewind();
        return bodyBuffer.array();
    }

    public void setRunning(boolean isRunning)
    {
        this.isRunning_ = isRunning;
    }

}
