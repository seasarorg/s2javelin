package org.seasar.javelin.communicate;

import org.seasar.javelin.bean.Invocation;

/**
 * アラームを通知する処理が実装するインタフェース。
 */
public interface AlarmListener
{
    /**
     * しきい値超過のアラームを通知する際に使用する。
     * 
     * @param invocation しきい値超過した呼び出しの情報。
     */
    void sendExceedThresholdAlarm(Invocation invocation);
    
    /**
     * ルートノード(コールツリーの頂点)のみをAlarmの対象とするかどうかを指定する。
     * 
     * @return trueならばルートノードのみを対象とする。falseならば全てのAlarmを対象とする。
     */
    boolean isSendingRootOnly();
}
