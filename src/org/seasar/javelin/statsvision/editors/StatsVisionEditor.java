package org.seasar.javelin.statsvision.editors;

import org.eclipse.ui.IEditorPart;
import org.seasar.javelin.statsvision.communicate.Telegram;
import org.seasar.javelin.statsvision.editpart.ComponentEditPart;

public interface StatsVisionEditor extends IEditorPart {

	public abstract void connect();

	public abstract void disconnect();

	/**
	 * 初期表示
	 */
	public abstract void initializeGraphicalViewer();

	/**
	 * 通信の開始
	 */
	public abstract void start();

	/**
	 * 通信の終了
	 */
	public abstract void stop();

	public abstract void setDomain(String domain);

	public abstract void setHostName(String hostName);

	public abstract void setPortNum(int portNum);

	// サーバIPを取得する
	public abstract String getHostName();

	// サーバPortを取得する
	public abstract int getPortNum();

	public abstract void setWarningThreshold(long warningThreshold);

	public abstract void setAlarmThreshold(long alarmThreshold);

	public abstract void setBlnReload(boolean blnReload);

	public abstract void reset();

	public abstract void listeningGraphicalViewer(Telegram telegram);

	public abstract void addResponseTelegram(Telegram telegram);

	public abstract void setComponentEditPart(ComponentEditPart componentPart);

}