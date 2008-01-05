package org.seasar.javelin.communicate;

import org.seasar.javelin.CallTreeNode;

/**
 * アラームを通知する処理が実装するインタフェース。
 */
public interface AlarmListener
{
    /**
     * しきい値超過のアラームを通知する際に使用する。
     * 
     * @param node しきい値超過した呼び出しの情報。
     */
    void sendExceedThresholdAlarm(CallTreeNode node);
    
    /**
     * ルートノード(コールツリーの頂点)のみをAlarmの対象とするかどうかを指定する。
     * 
     * @return trueならばルートノードのみを対象とする。falseならば全てのAlarmを対象とする。
     */
    boolean isSendingRootOnly();
}
