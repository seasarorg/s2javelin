package org.seasar.javelin;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.seasar.javelin.bean.Component;
import org.seasar.javelin.bean.Container;
import org.seasar.javelin.bean.ContainerMBean;
import org.seasar.javelin.bean.Invocation;
import org.seasar.javelin.bean.Statistics;
import org.seasar.javelin.bean.StatisticsMBean;
import org.seasar.javelin.communicate.JmxListener;
import org.seasar.javelin.helper.VMStatusHelper;
import org.seasar.javelin.util.ObjectNameUtil;

public class JmxRecorder
{
    /** �v���b�g�t�H�[��MBean�T�[�o */
    private static MBeanServer               server_;

    /** �������t���O�B�������ς݂̏ꍇ��true�B */
    private static boolean     isInitialized_   = false;

    /**
     * �����������B
     * MBeanServer�ւ�ContainerMBean�̓o�^���s���B
     * ���J�pHTTP�|�[�g���w�肳��Ă����ꍇ�́AHttpAdaptor�̐����Ɠo�^���s���B
     */
    private static void init(S2JavelinConfig config)
    {
        try
        {
            server_ = ManagementFactory.getPlatformMBeanServer();

            ContainerMBean container;
            ObjectName containerName = new ObjectName(config.getDomain()
                    + ".container:type=" + ContainerMBean.class.getName());
            if (server_.isRegistered(containerName) == false)
            {
                container = new Container();
                server_.registerMBean(container, containerName);
            }

            StatisticsMBean statistics;
            ObjectName statisticsName = new ObjectName(config.getDomain()
                    + ".statistics:type=" + StatisticsMBean.class.getName());
            if (server_.isRegistered(statisticsName) == false)
            {
                statistics = new Statistics();
                server_.registerMBean(statistics, statisticsName);
            }
        }
        catch (Throwable th)
        {
        	th.printStackTrace();
        }
    }

    /**
     * �O�����B
     *
     * @param className
     * @param methodName
     * @param config
     */
    public static void preProcess(
    		String className
    		, String methodName
    		, S2JavelinConfig config)
    {
        synchronized (JmxRecorder.class)
        {
            if (!isInitialized_)
            {
                isInitialized_ = true;
                init(config);
            }
        }

        try
        {
            Component componentBean = MBeanManager.getComponent(className);
            String name = ObjectNameUtil.createComponentBeanName(className, config);
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
            name = ObjectNameUtil.createInvocationBeanName(className, methodName, config);
            ObjectName objName = new ObjectName(name);

            if (invocationBean == null)
            {
                String processName = VMStatusHelper.getProcessName();
				invocationBean = 
                	new Invocation(
                		processName
                		, objName
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
        catch (Throwable th)
        {
            // �z��O�̗�O�����������ꍇ�͕W���G���[�o�͂ɏo�͂��Ă����B
        	// (��ʂɂ͓`�d�����Ȃ��B)
        	th.printStackTrace();
        }
    }

    /**
     * �㏈���i�{�����������j�B
     * @param spent
     */
    public static void postProcess()
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
        catch (Throwable th)
        {
            // �z��O�̗�O�����������ꍇ�͕W���G���[�o�͂ɏo�͂��Ă����B
        	// (��ʂɂ͓`�d�����Ȃ��B)
            th.printStackTrace();
        }
    }

	/**
     * �㏈���i�{�������s���j�B
     *
     * @param cause
     */
    public static void postProcess(Throwable cause)
    {
    }

    /**
     * 
     * @param node
     */
    private static void sendExceedThresholdAlarm(CallTreeNode node)
	{
        Invocation invocation = node.getInvocation();
        if (invocation != null)
        {
            JmxListener jmxListener = new JmxListener();
            jmxListener.sendExceedThresholdAlarm(node);
        }
        
        List children = node.getChildren();
		for (int index = 0; index <  children.size(); index++)
        {
			CallTreeNode child = (CallTreeNode) children.get(index);
        	sendExceedThresholdAlarm(child);
        }
	}
}
