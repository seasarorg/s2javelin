/**
 * 
 */
package org.seasar.javelin.bottleneckeye.communicate;

import org.seasar.javelin.bottleneckeye.editors.EditorTabInterface;

/**
 * サーバと通信するためのクライアント。
 *
 * @author Sakamoto
 */
public interface TelegramClientManager
{

    /**
     * 受信した電文を転送するオブジェクトをセットする。
     *
     * @param editorTab 転送先オブジェクト
     */
    void addEditorTab(EditorTabInterface editorTab);

    /**
     * サーバに電文を送信する。
     *
     * @param telegram 電文
     */
    void sendTelegram(Telegram telegram);

}
