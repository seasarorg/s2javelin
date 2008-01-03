package org.seasar.javelin.statsvision.editors;

import org.eclipse.ui.IEditorPart;
import org.seasar.javelin.statsvision.communicate.Telegram;
import org.seasar.javelin.statsvision.editpart.ComponentEditPart;

public interface StatsVisionEditor extends IEditorPart {

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
	
	// サーバIPを取得する
	String getHostName();

	// サーバPortを取得する
	int getPortNum();

	void setWarningThreshold(long warningThreshold);

	void setAlarmThreshold(long alarmThreshold);

	void setBlnReload(boolean blnReload);

	void reset();

	void listeningGraphicalViewer(Telegram telegram);

	void addResponseTelegram(Telegram telegram);

	void setComponentEditPart(ComponentEditPart componentPart);
	
	void setDirty(boolean isDirty);
}