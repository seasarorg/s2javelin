package org.seasar.javelin.communicate;

import javax.management.AttributeChangeNotification;

import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.bean.Invocation;

public class JmxListener implements AlarmListener
{
    /**
     * ���������\�b�h
     */
    public void init()
    {
        //�@�������Ȃ��B
    }
    
    public void sendExceedThresholdAlarm(String jvnFileName, CallTreeNode node)
    {
        Invocation invocation = node.getInvocation();
        
        synchronized (invocation)
        {
            // ���b�Z�[�W
            String ararmMsg = "Alarm:EXCEED_THRESHOLD";

            // TODO: �K�؂�Notification�������̂ŁA
            // AttributeChangeNotification�ő�p
            AttributeChangeNotification notification = 
            	new AttributeChangeNotification(
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

    /**
     * ����AlarmListener�����[�g�m�[�h�݂̂��������邩�ǂ�����Ԃ��B
     * �����̃N���X�ł́A���false��Ԃ��B
     * 
     * @see org.seasar.javelin.communicate.AlarmListener#isSendingRootOnly()
     */
    public boolean isSendingRootOnly()
    {
        return false;
    }
}
