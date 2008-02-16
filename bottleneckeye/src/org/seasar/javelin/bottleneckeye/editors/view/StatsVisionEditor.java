package org.seasar.javelin.bottleneckeye.editors.view;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.ui.IEditorPart;
import org.seasar.javelin.bottleneckeye.communicate.Telegram;
import org.seasar.javelin.bottleneckeye.communicate.TelegramClientManager;
import org.seasar.javelin.bottleneckeye.editpart.ComponentEditPart;

public interface StatsVisionEditor extends IEditorPart
{
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

    void setLineStyle(String text);

    // �T�[�oIP���擾����
    String getHostName();

    // �T�[�oPort���擾����
    int getPortNum();

    // ���C���X�^�C�����擾����B
    String getLineStyle();

    void setWarningThreshold(long warningThreshold);

    void setAlarmThreshold(long alarmThreshold);

    void setBlnReload(boolean blnReload);

    void reset();

    void listeningGraphicalViewer(Telegram telegram);

    void addResponseTelegram(Telegram telegram);

    void setComponentEditPart(ComponentEditPart componentPart);

    void setDirty(boolean isDirty);

    GraphicalViewer getGraphicalViewer();

    /**
     * �T�[�o�ƒʐM����N���C�A���g�I�u�W�F�N�g���擾����B
     *
     * @return �ʐM�I�u�W�F�N�g
     */
    TelegramClientManager getTelegramClientManager();

}
