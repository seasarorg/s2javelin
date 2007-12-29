package org.seasar.javelin.communicate;

import org.seasar.javelin.bean.Invocation;

public interface AlarmListener
{
    void sendExceedThresholdAlarm(Invocation invocation);
}
