/**
 * 
 */
package org.seasar.javelin.bottleneckeye.communicate;

import org.seasar.javelin.bottleneckeye.editors.view.TcpStatsVisionEditor;


/**
 * サーバに電文を送るSingletonクラス。
 *
 * @author Sakamoto
 */
public final class TelegramSender
{
    /** TCP通信用オブジェクト */
    private TcpStatsVisionEditor editor_;

    /**
     * 電文送信インスタンスを作成する。
     */
    public TelegramSender()
    {
        // 何もしない
    }

    /**
     * TCP通信用オブジェクトをセットする。
     *
     * @param editor TCP通信用オブジェクト
     */
    public void setTcpStatsVisionEditor(TcpStatsVisionEditor editor)
    {
        this.editor_ = editor;
    }


    /**
     * サーバに電文を送信する。
     *
     * @param telegram 電文
     */
    public void sendTelegram(Telegram telegram)
    {
        this.editor_.sendTelegram(telegram);
    }

}
