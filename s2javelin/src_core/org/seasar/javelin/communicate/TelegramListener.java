package org.seasar.javelin.communicate;

import org.seasar.javelin.communicate.entity.Telegram;

/**
 * 電文処理を実装するインタフェース。
 */
public interface TelegramListener
{
    /**
     * 受信電文を処理し、応答電文を返す。</br>
     * 応答を返さない場合、nullを返すこと。
     * 
     * @param telegram 受信電文 
     * @return 応答電文
     */
	Telegram receiveTelegram(Telegram telegram) throws Exception;
}
