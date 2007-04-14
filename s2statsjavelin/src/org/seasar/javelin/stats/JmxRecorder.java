package org.seasar.javelin.stats;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.seasar.javelin.stats.bean.Component;
import org.seasar.javelin.stats.bean.ComponentMBean;
import org.seasar.javelin.stats.bean.Invocation;
import org.seasar.javelin.stats.bean.InvocationMBean;
import org.seasar.javelin.stats.util.StatsUtil;

public class JmxRecorder
{
    /** �v���b�g�t�H�[��MBean�T�[�o */
    private static MBeanServer               server_     = ManagementFactory.getPlatformMBeanServer();

    /**
     * �O�����B
     * 
     * @param invocationBean
     */
    public static void preProcess(
    		String className
    		, String methodName
    		, Object[] arguments
    		, StackTraceElement[] stacktrace
    		, S2StatsJavelinConfig config)
    {
        try
        {
            Component componentBean = MBeanManager.getComponent(className);
            String name = config.getDomain() + ".component:type="
                    + ComponentMBean.class.getName() + ",class=" + className;
            ObjectName componentName = new ObjectName(name);
            if (componentBean == null)
            {
                componentBean = new Component(componentName, className);

                if (server_.isRegistered(componentName))
                {
                    server_.unregisterMBean(componentName);
                }
                server_.registerMBean(componentBean, componentName);
                MBeanManager.setComponent(className, componentBean);
            }

            Invocation invocationBean = componentBean.getInvocation(methodName);
            name = config.getDomain() + ".invocation:type="
                    + InvocationMBean.class.getName() + ",class=" + className
                    + ",method=" + methodName;
            ObjectName objName = new ObjectName(name);

            if (invocationBean == null)
            {
                invocationBean = 
                	new Invocation(
                		objName
                		, componentName
                		, className
                		, methodName
                		, config.getIntervalMax()
                		, config.getThrowableMax()
                		, config.getRecordThreshold()
                		, config.getAlarmThreshold());

                componentBean.addInvocation(invocationBean);
                if (server_.isRegistered(objName))
                {
                    server_.unregisterMBean(objName);
                }
                server_.registerMBean(invocationBean, objName);
            }
        }
        catch (Exception ex)
        {
            // �z��O�̗�O�����������ꍇ�͕W���G���[�o�͂ɏo�͂��Ă����B
            ex.printStackTrace();
        }
    }

    /**
     * �㏈���i�{�����������j�B
     * @param spent
     */
    public static void postProcess(Object returnValue, S2StatsJavelinConfig config)
    {
        try
        {
            // �Ăяo�������擾�B
            CallTreeNode node = S2StatsJavelinRecorder.callerNode_.get();
            if (node == null)
            {
                // �Ăяo������񂪎擾�ł��Ȃ��ꍇ�͏������L�����Z������B
                // (���ʃ��C���ŗ�O�����������ꍇ�̂��߁B)
                return;
            }

            CallTreeNode parent = node.getParent();
            if (parent != null)
            {
            }
            else
            {
                // ���[�g�m�[�h�ł̌o�ߎ��Ԃ�臒l�𒴂��Ă����ꍇ�́A
                // �g�����U�N�V�������L�^����B
            	if (node.getAccumulatedTime() >= node.getInvocation().getAlarmThreshold())
            	{
                    sendExceedThresholdAlarm(node);
            	}
            }
        }
        catch (Exception ex)
        {
            // �z��O�̗�O�����������ꍇ�͕W���G���[�o�͂ɏo�͂��Ă����B
            ex.printStackTrace();
        }
    }

	/**
     * �㏈���i�{�������s���j�B
     * @param cause
     */
    public static void postProcess(Throwable cause)
    {
        try
        {
            // �Ăяo�������擾�B
            CallTreeNode node = S2StatsJavelinRecorder.callerNode_.get();
            if (node == null)
            {
                // �Ăяo������񂪎擾�ł��Ȃ��ꍇ�͏������L�����Z������B
                // (���łɋL�^�ς݂̗�O�̂��߁B)
                return;
            }

            // ����������O���L�^���Ă����B
            node.getInvocation().addThrowable(cause);

            //�Ăяo�������������Ă����B
            S2StatsJavelinRecorder.callerNode_.set(null);
        }
        catch (Exception ex)
        {
            // �z��O�̗�O�����������ꍇ�͕W���G���[�o�͂ɏo�͂��Ă����B
            ex.printStackTrace();
        }
    }

    /**
     * 
     * @param node
     */
    private static void sendExceedThresholdAlarm(CallTreeNode node)
	{
        node.getInvocation().sendExceedThresholdAlarm();
        
        List children = node.getChildren();
		for (int index = 0; index <  children.size(); index++)
        {
			CallTreeNode child = (CallTreeNode) children.get(index);
        	sendExceedThresholdAlarm(child);
        }
	}
}
