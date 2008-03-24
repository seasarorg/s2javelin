/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.view;

import org.eclipse.jface.action.Action;
import org.seasar.javelin.bottleneckeye.communicate.Common;
import org.seasar.javelin.bottleneckeye.communicate.Header;
import org.seasar.javelin.bottleneckeye.communicate.Telegram;


/**
 * クラス図をリロードするアクション。
 *
 * @author Sakamoto
 */
public class ClassReloadAction extends Action
{
    /** クラス図表示エディタ */
    private StatsVisionEditor editor_;

    /**
     * クラス図をリロードするアクションを生成する。
     *
     * @param editor クラス図表示エディタ
     */
    public ClassReloadAction(StatsVisionEditor editor)
    {
        this.editor_ = editor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {
        // 頭部データ対象を作って、データを設定する
        Header objHeader = new Header();
        objHeader.setByteTelegramKind(Common.BYTE_TELEGRAM_KIND_GET);
        objHeader.setByteRequestKind(Common.BYTE_REQUEST_KIND_REQUEST);

        // 頭部を電文対象に設定する
        Telegram objOutputTelegram = new Telegram();
        objOutputTelegram.setObjHeader(objHeader);

        ((TcpStatsVisionEditor)this.editor_).sendTelegram(objOutputTelegram);
    }

}
