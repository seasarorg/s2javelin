package org.seasar.javelin.bottleneckeye.editors.view;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.seasar.javelin.bottleneckeye.communicate.Telegram;
import org.seasar.javelin.bottleneckeye.communicate.TelegramClientManager;
import org.seasar.javelin.bottleneckeye.editpart.ComponentEditPart;

public interface StatsVisionEditor extends IEditorPart, ISelectionListener
{
    void connect();

    void disconnect();

    /**
     * 初期表示
     */
    void initializeGraphicalViewer();

    /**
     * 通信の開始
     */
    void start();

    /**
     * 通信の終了
     */
    void stop();

    void setDomain(String domain);

    void setHostName(String hostName);

    void setPortNum(int portNum);

    void setMode(String mode);

    void setLineStyle(String text);

    // サーバIPを取得する
    String getHostName();

    // サーバPortを取得する
    int getPortNum();

    // ラインスタイルを取得する。
    String getLineStyle();

    void setWarningThreshold(long warningThreshold);

    void setAlarmThreshold(long alarmThreshold);

    void setBlnReload(boolean blnReload);

    void reset();

    /**
     * サーバからデータをリロードする。
     */
    void reload();

    /**
     * サーバに状態取得電文を送る。
     */
    void requestStatus();

    void listeningGraphicalViewer(Telegram telegram);

    void addResponseTelegram(Telegram telegram);

    void addComponentEditPart(String className, ComponentEditPart componentPart);

    void setDirty(boolean isDirty);

    GraphicalViewer getGraphicalViewer();

    /**
     * サーバと通信するクライアントオブジェクトを取得する。
     *
     * @return 通信オブジェクト
     */
    TelegramClientManager getTelegramClientManager();

    /**
     * 背景色をセットする。
     *
     * @param color 背景色
     */
    void setBackground(Color color);

    /**
     * 接続状態を取得する。
     *
     * @return 接続されているなら <code>true</code> 、そうでないなら <code>false</code>
     */
    boolean isConnected();

}
