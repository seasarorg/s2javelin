/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.seasar.javelin.bottleneckeye.communicate.Telegram;

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
     * Start�{�^�����������ۂ̏����B
     */
    void onStart();

    /**
     * Reset�{�^�����������ۂ̏����B
     */
    void onReset();

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
}
