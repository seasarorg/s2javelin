package org.seasar.javelin.communicate;

import javax.management.AttributeChangeNotification;

import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.bean.Invocation;

public class JmxListener implements AlarmListener
{
    /**
     * 初期化メソッド
     */
    public void init()
    {
        //　何もしない。
    }
    
    public void sendExceedThresholdAlarm(String jvnFileName, CallTreeNode node)
    {
        Invocation invocation = node.getInvocation();
        
        synchronized (invocation)
        {
            // メッセージ
            String ararmMsg = "Alarm:EXCEED_THRESHOLD";

            // TODO: 適切なNotificationが無いので、
            // AttributeChangeNotificationで代用
            AttributeChangeNotification notification = 
            	new AttributeChangeNotification(
            			this,
                        0,
                        System.currentTimeMillis(),
                        ararmMsg, // アラームメッセージ
                        invocation.getMethodName(), // 属性名 => メソッド名
                        "Method", // 属性タイプ => Method
                        invocation.getAverage(), // 変更前の値 => 閾値の時間
                        invocation.getMaximum()); // 変更後の値 => 経過時間

            invocation.sendNotification(notification);
        }
    }

    /**
     * このAlarmListenerがルートノードのみを処理するかどうかを返す。
     * ※このクラスでは、常にfalseを返す。
     * 
     * @see org.seasar.javelin.communicate.AlarmListener#isSendingRootOnly()
     */
    public boolean isSendingRootOnly()
    {
        return false;
    }
}
