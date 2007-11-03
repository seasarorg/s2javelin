package org.seasar.javelin;

import org.seasar.javelin.bean.Invocation;

public interface AlarmListener
{
    void sendExceedThresholdAlarm(Invocation invocation);
}
