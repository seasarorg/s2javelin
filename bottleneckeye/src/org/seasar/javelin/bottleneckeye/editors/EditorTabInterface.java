/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.seasar.javelin.bottleneckeye.communicate.Telegram;
import org.seasar.javelin.bottleneckeye.communicate.TelegramSender;
import org.seasar.javelin.bottleneckeye.model.persistence.PersistenceModel;

/**
 * タブの１画面を表すインタフェース。
 *
 * @author Sakamoto
 */
public interface EditorTabInterface
{
    /**
     * タブの中身を作成する（コンポジット）。
     *
     * @param container 親コンポジット
     * @param editorPart タブを生成するエディタ
     * @return 画面インスタンス
     */
    Composite createComposite(Composite container, MultiPageEditorPart editorPart);

    /**
     * タブの名前を返す。
     *
     * @return タブ名
     */
    String getName();

    /**
     * 送信されてきた電文をタブにセットする。
     *
     * @param telegram 電文
     * @return この電文を処理した場合は <code>true</code>
     */
    boolean receiveTelegram(Telegram telegram);

    /**
     * 電文送信オブジェクトをセットする。
     *
     * @param telegramSender 電文送信オブジェクト。
     */
    void setTelegramSender(TelegramSender telegramSender);

    /**
     * Startボタンを押した際の処理。
     */
    void onStart();

    /**
     * Resetボタンを押した際の処理。
     */
    void onReset();

    /**
     * Reloadボタンを押した際の処理。
     */
    void onReload();

    /**
     * Stopボタンを押した際の処理。
     */
    void onStop();

    /**
     * Printボタンを押した際の処理。
     */
    void onPrint();

    /**
     * Copyボタンを押した際の処理。
     */
    void onCopy();

    /**
     * 接続されたときに呼ばれる処理。
     */
    void connected();

    /**
     * 切断されたときに呼ばれる処理。
     */
    void disconnected();

    /**
     * 保存時の処理。
     * @param persistence 永続化モデル
     */
    void onSave(PersistenceModel persistence);

    /**
     * 読み込み時の処理。
     * @param persistence 永続化モデル
     */
    void onLoad(PersistenceModel persistence);
}
