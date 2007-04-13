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

public class S2StatsJavelinRecorder
{
    /** �v���b�g�t�H�[��MBean�T�[�o */
    private static MBeanServer               server_     = ManagementFactory.getPlatformMBeanServer();

    /**
     * ���\�b�h�R�[���c���[�̋L�^�p�I�u�W�F�N�g�B
     */
    public static ThreadLocal<CallTree>     callTree_   = new ThreadLocal<CallTree>() {
                                                             protected synchronized CallTree initialValue( )
                                                             {
                                                                 return null;
                                                             }
                                                         };

    /**
     * ���\�b�h�̌Ăяo�����I�u�W�F�N�g�B
     */
    public static ThreadLocal<CallTreeNode> callerNode_ = new ThreadLocal<CallTreeNode>() {
                                                             protected synchronized CallTreeNode initialValue( )
                                                             {
                                                                 return null;
                                                             }
                                                         };

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

            // �Ăяo�������擾�B
            CallTreeNode node = callerNode_.get();

            if (node == null)
            {
                // ����Ăяo�����̓R�[���c���[������������B
                CallTree tree = new CallTree();
                callTree_.set(tree);
                tree.setRootCallerName(config.getRootCallerName());
                tree.setEndCalleeName(config.getEndCalleeName());

                switch(config.getThreadModel())
                {
                case S2StatsJavelinConfig.TM_THREAD_ID:
                	tree.setThreadID("" + Thread.currentThread().getId());
                	break;
                case S2StatsJavelinConfig.TM_THREAD_NAME:
                	tree.setThreadID(Thread.currentThread().getName());
                	break;
                case S2StatsJavelinConfig.TM_CONTEXT_PATH:
                	tree.setThreadID(methodName);
                	break;
                default:
                	break;
                }
                
                node = new CallTreeNode();
                node.setStartTime(System.currentTimeMillis());
                if (config.isLogMethodArgsAndReturnValue())
                {
                    String[] argumentsString = new String[arguments.length];
                    for (int index = 0; index < arguments.length; index++)
                    {
                    	if (arguments[index] != null)
                    	{
                        	argumentsString[index] = arguments[index].toString();
                    	}
                    	else
                    	{
                    		argumentsString[index] = "null";
                    	}
                    }
                    node.setArgs(argumentsString);
                }
                if (config.isLogStacktrace())
                {
                	node.setStacktrace(stacktrace);
                }
                
                tree.setRootNode(node);
            }
            else
            {
                CallTreeNode parent = node;
                node = new CallTreeNode();
                node.setStartTime(System.currentTimeMillis());
                if (config.isLogMethodArgsAndReturnValue())
                {
                    String[] argumentsString = new String[arguments.length];
                    for (int index = 0; index < arguments.length; index++)
                    {
                    	argumentsString[index] = arguments[index].toString();
                    }
                    node.setArgs(argumentsString);
                }
                if (config.isLogStacktrace())
                {
                	node.setStacktrace(stacktrace);
                }
                
                parent.addChild(node);
                
                invocationBean.addCaller(parent.getInvocation());
            }

            node.setInvocation(invocationBean);

            // �Ăяo������A
            // ���񃍃O�o�͎��̌Ăяo�����Ƃ��Ďg�p���邽�߂ɕۑ�����B
            callerNode_.set(node);
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
            CallTreeNode node = callerNode_.get();
            if (node == null)
            {
                // �Ăяo������񂪎擾�ł��Ȃ��ꍇ�͏������L�����Z������B
                // (���ʃ��C���ŗ�O�����������ꍇ�̂��߁B)
                return;
            }

            node.setEndTime(System.currentTimeMillis());
            node.setReturnValue(returnValue);

            CallTreeNode parent = node.getParent();
            if (parent != null)
            {
                callerNode_.set(parent);
            }
            else
            {
                // ���[�g�m�[�h�ł̌o�ߎ��Ԃ�臒l�𒴂��Ă����ꍇ�́A
                // �g�����U�N�V�������L�^����B
            	if (node.getAccumulatedTime() >= node.getInvocation().getRecordThreshold())
                {
                    recordTransaction(node);
                }
            	
            	if (node.getAccumulatedTime() >= node.getInvocation().getAlarmThreshold())
            	{
                    S2StatsJavelinFileGenerator generator = 
                    	new S2StatsJavelinFileGenerator(config.getJavelinFileDir());
                    generator.generateJaveinFile(callTree_.get(), node);

                    sendExceedThresholdAlarm(node);
            	}
            	
                callerNode_.set(null);
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
            CallTreeNode node = callerNode_.get();
            if (node == null)
            {
                // �Ăяo������񂪎擾�ł��Ȃ��ꍇ�͏������L�����Z������B
                // (���łɋL�^�ς݂̗�O�̂��߁B)
                return;
            }

            // ����������O���L�^���Ă����B
            node.getInvocation().addThrowable(cause);

            //�Ăяo�������������Ă����B
            callerNode_.set(null);
        }
        catch (Exception ex)
        {
            // �z��O�̗�O�����������ꍇ�͕W���G���[�o�͂ɏo�͂��Ă����B
            ex.printStackTrace();
        }
    }

    /**
     * �g�����U�N�V�������L�^����B
     * 
     * @param node
     */
    private static void recordTransaction(CallTreeNode node)
    {
        Invocation invocation = node.getInvocation();
        invocation.addInterval(StatsUtil.getElapsedTime(node));
        if (node.getParent() != null)
        {
            invocation.addCaller(node.getParent().getInvocation());
        }

        List children = node.getChildren();
		for (int index = 0; index <  children.size(); index++)
        {
			CallTreeNode child = (CallTreeNode) children.get(index);
            recordTransaction(child);
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
