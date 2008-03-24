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
 * �^�u�̂P��ʂ�\���C���^�t�F�[�X�B
 *
 * @author Sakamoto
 */
public interface EditorTabInterface
{
    /**
     * �^�u�̒��g���쐬����i�R���|�W�b�g�j�B
     *
     * @param container �e�R���|�W�b�g
     * @param editorPart �^�u�𐶐�����G�f�B�^
     * @return ��ʃC���X�^���X
     */
    Composite createComposite(Composite container, MultiPageEditorPart editorPart);

    /**
     * �^�u�̖��O��Ԃ��B
     *
     * @return �^�u��
     */
    String getName();

    /**
     * ���M����Ă����d�����^�u�ɃZ�b�g����B
     *
     * @param telegram �d��
     * @return ���̓d�������������ꍇ�� <code>true</code>
     */
    boolean receiveTelegram(Telegram telegram);

    /**
     * �d�����M�I�u�W�F�N�g���Z�b�g����B
     *
     * @param telegramSender �d�����M�I�u�W�F�N�g�B
     */
    void setTelegramSender(TelegramSender telegramSender);

    /**
     * Start�{�^�����������ۂ̏����B
     */
    void onStart();

    /**
     * Reset�{�^�����������ۂ̏����B
     */
    void onReset();

    /**
     * Reload�{�^�����������ۂ̏����B
     */
    void onReload();

    /**
     * Stop�{�^�����������ۂ̏����B
     */
    void onStop();

    /**
     * Print�{�^�����������ۂ̏����B
     */
    void onPrint();

    /**
     * Copy�{�^�����������ۂ̏����B
     */
    void onCopy();

    /**
     * �ڑ����ꂽ�Ƃ��ɌĂ΂�鏈���B
     */
    void connected();

    /**
     * �ؒf���ꂽ�Ƃ��ɌĂ΂�鏈���B
     */
    void disconnected();

    /**
     * �ۑ����̏����B
     * @param persistence �i�������f��
     */
    void onSave(PersistenceModel persistence);

    /**
     * �ǂݍ��ݎ��̏����B
     * @param persistence �i�������f��
     */
    void onLoad(PersistenceModel persistence);
}
