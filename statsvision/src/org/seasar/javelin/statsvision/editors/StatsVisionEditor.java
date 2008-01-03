package org.seasar.javelin.statsvision.editors;

import org.eclipse.ui.IEditorPart;
import org.seasar.javelin.statsvision.communicate.Telegram;
import org.seasar.javelin.statsvision.editpart.ComponentEditPart;

public interface StatsVisionEditor extends IEditorPart {

	void connect();

	void disconnect();

	/**
	 * �����\��
	 */
	void initializeGraphicalViewer();

	/**
	 * �ʐM�̊J�n
	 */
	void start();

	/**
	 * �ʐM�̏I��
	 */
	void stop();

	void setDomain(String domain);

	void setHostName(String hostName);

	void setPortNum(int portNum);

	void setMode(String mode);
	
	// �T�[�oIP���擾����
	String getHostName();

	// �T�[�oPort���擾����
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