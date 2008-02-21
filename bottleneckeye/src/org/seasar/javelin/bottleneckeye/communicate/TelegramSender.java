/**
 * 
 */
package org.seasar.javelin.bottleneckeye.communicate;

import org.seasar.javelin.bottleneckeye.editors.view.TcpStatsVisionEditor;


/**
 * �T�[�o�ɓd���𑗂�Singleton�N���X�B
 *
 * @author Sakamoto
 */
public final class TelegramSender
{

    /** Singleton�̃C���X�^���X */
    private static TelegramSender sender__ = new TelegramSender(); 

    /** TCP�ʐM�p�I�u�W�F�N�g */
    private TcpStatsVisionEditor editor_;


    /**
     * �d�����M�C���X�^���X���쐬����B
     */
    private TelegramSender()
    {
        // �������Ȃ�
    }


    /**
     * �d�����M�C���X�^���X��Ԃ��B
     *
     * @return �C���X�^���X
     */
    public static TelegramSender getInstance()
    {
        return sender__;
    }


    /**
     * TCP�ʐM�p�I�u�W�F�N�g���Z�b�g����B
     *
     * @param editor TCP�ʐM�p�I�u�W�F�N�g
     */
    public void setTcpStatsVisionEditor(TcpStatsVisionEditor editor)
    {
        this.editor_ = editor;
    }


    /**
     * �T�[�o�ɓd���𑗐M����B
     *
     * @param telegram �d��
     */
    public void sendTelegram(Telegram telegram)
    {
        this.editor_.sendTelegram(telegram);
    }

}