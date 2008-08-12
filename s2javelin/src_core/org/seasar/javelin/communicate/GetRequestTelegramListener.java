package org.seasar.javelin.communicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.seasar.javelin.MBeanManager;
import org.seasar.javelin.bean.Component;
import org.seasar.javelin.bean.Invocation;
import org.seasar.javelin.communicate.entity.Telegram;

/**
 * 状態取得処理クラス。
 * 
 * @author tsukano
 */
public class GetRequestTelegramListener implements TelegramListener
{
    /**
     * {@inheritDoc}
     */
    public Telegram receiveTelegram(Telegram telegram)
    {
        if (telegram.getObjHeader().getByteTelegramKind() == Common.BYTE_TELEGRAM_KIND_GET
                && telegram.getObjHeader().getByteRequestKind() == Common.BYTE_REQUEST_KIND_REQUEST)
        {
            Component[] objComponentArr = MBeanManager.getAllComponents();
            List<Invocation> invocationList = new ArrayList<Invocation>();

            // 電文数を統計する
            for (int i = 0; i < objComponentArr.length; i++)
            {
                invocationList.addAll(Arrays.asList(objComponentArr[i].getAllInvocation()));
            }

            Telegram objTelegram =
                    S2TelegramUtil.create(invocationList, Common.BYTE_TELEGRAM_KIND_GET,
                                          Common.BYTE_REQUEST_KIND_RESPONSE);
            return objTelegram;
        }
        return null;
    }
}
