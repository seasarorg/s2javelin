/**
 * 
 */
package org.seasar.javelin.bottleneckeye.communicate;

import org.seasar.javelin.bottleneckeye.editors.EditorTabInterface;

/**
 * �T�[�o�ƒʐM���邽�߂̃N���C�A���g�B
 *
 * @author Sakamoto
 */
public interface TelegramClientManager
{

    /**
     * ��M�����d����]������I�u�W�F�N�g���Z�b�g����B
     *
     * @param editorTab �]����I�u�W�F�N�g
     */
    void addEditorTab(EditorTabInterface editorTab);

    /**
     * �T�[�o�ɓd���𑗐M����B
     *
     * @param telegram �d��
     */
    void sendTelegram(Telegram telegram);

}
