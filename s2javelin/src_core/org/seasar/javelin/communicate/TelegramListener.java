package org.seasar.javelin.communicate;

import org.seasar.javelin.communicate.entity.Telegram;

/**
 * �d����������������C���^�t�F�[�X�B
 */
public interface TelegramListener
{
    /**
     * ��M�d�����������A�����d����Ԃ��B</br>
     * ������Ԃ��Ȃ��ꍇ�Anull��Ԃ����ƁB
     * 
     * @param telegram ��M�d�� 
     * @return �����d��
     */
	Telegram receiveTelegram(Telegram telegram) throws Exception;
}
