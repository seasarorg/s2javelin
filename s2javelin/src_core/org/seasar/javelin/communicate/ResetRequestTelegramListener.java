package org.seasar.javelin.communicate;

import org.seasar.javelin.MBeanManager;
import org.seasar.javelin.communicate.entity.Telegram;

/**
 * リセット要求処理クラス。
 * @author tsukano
 */
public class ResetRequestTelegramListener implements TelegramListener
{
	/**
	 * {@inheritDoc}
	 */
	public Telegram receiveTelegram(Telegram telegram) throws Exception
	{
		if (   telegram.getObjHeader().getByteTelegramKind() == Common.BYTE_TELEGRAM_KIND_RESET
			&& telegram.getObjHeader().getByteRequestKind() == Common.BYTE_REQUEST_KIND_REQUEST)
		{
			MBeanManager.reset();
		}
		return null;
	}

}
