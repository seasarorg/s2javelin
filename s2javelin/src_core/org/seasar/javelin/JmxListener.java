package org.seasar.javelin;

import javax.management.AttributeChangeNotification;

import org.seasar.javelin.bean.Invocation;

public class JmxListener implements AlarmListener
{
    public void sendExceedThresholdAlarm(Invocation invocation)
    {
        synchronized (invocation)
        {
            // ���b�Z�[�W
            String ararmMsg = "Alarm:EXCEED_THRESHOLD";

            // TODO: �K�؂�Notification�������̂ŁA
            // AttributeChangeNotification�ő�p
            AttributeChangeNotification notification = new AttributeChangeNotification(
                                                                                       this,
                                                                                       0,
                                                                                       System.currentTimeMillis(),
                                                                                       ararmMsg, // �A���[�����b�Z�[�W
                                                                                       invocation.getMethodName(), // ������ => ���\�b�h��
                                                                                       "Method", // �����^�C�v => Method
                                                                                       invocation.getAverage(), // �ύX�O�̒l => 臒l�̎���
                                                                                       invocation.getMaximum()); // �ύX��̒l => �o�ߎ���

            invocation.sendNotification(notification);
        }
    }
}
