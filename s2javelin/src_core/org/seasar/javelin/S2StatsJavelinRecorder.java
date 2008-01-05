package org.seasar.javelin;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.seasar.javelin.bean.Component;
import org.seasar.javelin.bean.ComponentMBean;
import org.seasar.javelin.bean.Container;
import org.seasar.javelin.bean.ContainerMBean;
import org.seasar.javelin.bean.Invocation;
import org.seasar.javelin.bean.InvocationMBean;
import org.seasar.javelin.communicate.AlarmListener;
import org.seasar.javelin.communicate.JavelinAcceptThread;
import org.seasar.javelin.helper.VMStatusHelper;
import org.seasar.javelin.log.S2StatsJavelinFileGenerator;
import org.seasar.javelin.util.ObjectNameUtil;
import org.seasar.javelin.util.StatsUtil;

/**
 * ���\�b�h�Ăяo�����̎擾���s���N���X�B
 */
public class S2StatsJavelinRecorder
{
    /** ����������t���O */
    private static boolean                  isInitialized;

    /** �v���b�g�t�H�[��MBeanServer */
    private static MBeanServer              server_     = ManagementFactory.getPlatformMBeanServer();

    /**
     * ���\�b�h�R�[���c���[�̋L�^�p�I�u�W�F�N�g�B
     */
    public static ThreadLocal<CallTree>     callTree_   = new ThreadLocal<CallTree>() {
                                                            protected synchronized CallTree initialValue()
                                                            {
                                                                return null;
                                                            }
                                                        };

    /**
     * ���\�b�h�̌Ăяo�����I�u�W�F�N�g�B
     */
    public static ThreadLocal<CallTreeNode> callerNode_ = new ThreadLocal<CallTreeNode>() {
                                                            protected synchronized CallTreeNode initialValue()
                                                            {
                                                                return null;
                                                            }
                                                        };

    private static VMStatusHelper         vmStatusHelper__ = new VMStatusHelper();

    /**
     * �����������B 
     * AlarmListener�̓o�^���s���B
     * MBeanServer�ւ�ContainerMBean�̓o�^���s���B
     * ���J�pHTTP�|�[�g���w�肳��Ă����ꍇ�́AHttpAdaptor�̐����Ɠo�^���s���B
     */
    private static void javelinInit(S2JavelinConfig config, MBeanServer server)
    {
        try
        {
            // �G���[���K�[������������B
            JavelinErrorLogger.initErrorLog(config);

            // AlarmListener��o�^����
            registerAlarmListeners(config);
            
            // MBean��o�^����
            server = ManagementFactory.getPlatformMBeanServer();

            ContainerMBean container;
            ObjectName containerName = new ObjectName(config.getDomain() + ".container:type="
                    + ContainerMBean.class.getName());
            if (server.isRegistered(containerName) == false)
            {
                container = new Container();
                server.registerMBean(container, containerName);
            }

            // �X���b�h�̊Ď����J�n����B
            vmStatusHelper__.init();

            // TCP�ł̐ڑ���t���J�n����B
            int port = config.getAcceptPort();
            JavelinAcceptThread.getInstance().start(port);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * AlarmListener�̃N���X��Javelin�ݒ肩��ǂݍ��݁A�o�^����B
     * �N���X�̃��[�h�́A�ȉ��̏��ŃN���X���[�_�ł̃��[�h�����݂�B
     * <ol>
     * <li>S2StatsJavelinRecorder�����[�h�����N���X���[�_</li>
     * <li>�R���e�L�X�g�N���X���[�_</li>
     * </ol>
     * 
     * @param config
     */
	private static void registerAlarmListeners(S2JavelinConfig config)
	{
		String[] alarmListeners = config.getAlarmListeners().split(",");
		for (String alarmListenerName : alarmListeners)
		{
		    try
		    {
		        Class<?> alarmListenerClass = loadClass(alarmListenerName);
				Object listener = alarmListenerClass.newInstance();
		        if (listener instanceof AlarmListener)
		        {
		            addListener((AlarmListener) listener);
			        JavelinErrorLogger.getInstance().log(alarmListenerName + "��AlarmListner�Ƃ��ēo�^���܂����B");
		        }
		        else
		        {
		            JavelinErrorLogger.getInstance().log(alarmListenerName + "��AlarmListener���������Ă��Ȃ����߁AAlarm�ʒm�ɗ��p���܂���B");
		        }
		    }
		    catch (Exception ex)
		    {
		        JavelinErrorLogger.getInstance().log(alarmListenerName + "�̓o�^�Ɏ��s�������߁AAlarm�ʒm�ɗ��p���܂���B", ex);
		    }
		}
	}

	/**
	 * �N���X�����[�h����B
     * �ȉ��̏��ŃN���X���[�_�ł̃��[�h�����݂�B
     * <ol>
     * <li>S2StatsJavelinRecorder�����[�h�����N���X���[�_</li>
     * <li>�R���e�L�X�g�N���X���[�_</li>
     * </ol>
	 * 
	 * @param className ���[�h����N���X�̖��O�B
	 * @return ���[�h�����N���X�B
	 * @throws ClassNotFoundException �S�ẴN���X���[�_�ŃN���X��������Ȃ��ꍇ
	 */
	private static Class<?> loadClass(String className)
			throws ClassNotFoundException {
		
		Class<?> clazz;
		try
		{
			clazz = Class.forName(className);
		}
		catch(ClassNotFoundException cnfe)
		{
	        JavelinErrorLogger.getInstance().log(className + "�̃��[�h�Ɏ��s�������߁A�R���e�L�X�g�N���X���[�_����̃��[�h���s���܂��B");
			clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
		}
		
		return clazz;
	}

    /**
     * MBean�ɓo�^���镶����𐶐�����
     * 
     * @param className
     *            �N���X��
     * @param methodName
     *            ���\�b�h��
     * @param config
     *            �ݒ�t�@�C������ǂݍ��񂾐ݒ�l
     * @return �o�^���镶����
     */
    private static String createInvocationBeanName(String className, String methodName,
            S2JavelinConfig config)
    {
        return config.getDomain() + ".invocation:type=" + InvocationMBean.class.getName()
                + ",class=" + ObjectNameUtil.encode(className) + ",method="
                + ObjectNameUtil.encode(methodName);
    }

    private static String createComponentBeanName(String className, S2JavelinConfig config)
    {
        return config.getDomain() + ".component:type=" + ComponentMBean.class.getName() + ",class="
                + ObjectNameUtil.encode(className);
    }

    /**
     * JavelinRecorder, JDBCJavelinRecorder����Ăяo�����Ƃ��̑O�����B
     * 
     * @param className
     * @param methodName
     * @param config
     * @param args
     */
    public static void preProcess(String className, String methodName, Object[] args,
            S2JavelinConfig config)
    {
        synchronized (S2StatsJavelinRecorder.class)
        {
            // ����������
            if (isInitialized == false)
            {
                javelinInit(config, server_);
                isInitialized = true;
            }
        }

        VMStatus vmStatus = vmStatusHelper__.createVMStatus();

        try
        {
            // ���[�g�N���X�������
            StringBuffer buffer = new StringBuffer();
            buffer.append(className);
            String rootClassName = buffer.toString();

            Component componentBeanRoot = MBeanManager.getComponent(rootClassName);

            String name = createComponentBeanName(className, config);
            ObjectName componentName = new ObjectName(name);

            // ���[�g�N���X�ꍇ�A���[�g�N���X����ݒ肷��
            CallTreeNode node = S2StatsJavelinRecorder.callerNode_.get();
            if ((node == null) || !(componentBeanRoot == null))
            {
                className = rootClassName;
            }

            Component componentBean = MBeanManager.getComponent(className);
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

            Invocation invocation = componentBean.getInvocation(methodName);
            name = createInvocationBeanName(className, methodName, config);
            ObjectName objName = new ObjectName(name);

            if (invocation == null)
            {
                String processName = VMStatusHelper.getProcessName();
            	invocation = new Invocation(processName, objName, componentName, className, methodName,
                                            config.getIntervalMax(), config.getThrowableMax(),
                                            config.getRecordThreshold(), config.getAlarmThreshold());

                componentBean.addInvocation(invocation);

                if (server_.isRegistered(objName))
                {
                    server_.unregisterMBean(objName);
                }
                server_.registerMBean(invocation, objName);
            }

            // ����Ăяo�����̓R�[���c���[������������B
            CallTree tree = callTree_.get();

            setNode(node, tree, null, args, invocation, vmStatus, config);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    /**
     * �O�����B
     * 
     * @param className
     *            �N���X��
     * @param methodName
     *            ���\�b�h��
     * @param args
     *            ����
     * @param stacktrace
     *            �X�^�b�N�g���[�X
     * @param config
     *            �ݒ�
     */
    public static void preProcess(String className, String methodName, Object[] args,
            StackTraceElement[] stacktrace, S2JavelinConfig config)
    {
        synchronized (S2StatsJavelinRecorder.class)
        {
            // ����������
            if (isInitialized == false)
            {
                javelinInit(config, server_);
                isInitialized = true;
            }
        }

        VMStatus vmStatus = vmStatusHelper__.createVMStatus();

        Component component = MBeanManager.getComponent(className);
        Invocation invocation = component.getInvocation(methodName);

        try
        {
            // �Ăяo�������擾�B
            CallTreeNode node = callerNode_.get();
            CallTree tree = null;

            if (node == null)
            {
                // ����Ăяo�����̓R�[���c���[������������B
                tree = callTree_.get();
                if (tree == null)
                {
                    tree = new CallTree();
                    callTree_.set(tree);
                    vmStatusHelper__.resetPeakMemoryUsage();
                }
                tree.setRootCallerName(config.getRootCallerName());
                tree.setEndCalleeName(config.getEndCalleeName());

                switch (config.getThreadModel())
                {
                case S2JavelinConfig.TM_THREAD_ID:
                    tree.setThreadID("" + Thread.currentThread().getId());
                    break;
                case S2JavelinConfig.TM_THREAD_NAME:
                    tree.setThreadID(Thread.currentThread().getName());
                    break;
                case S2JavelinConfig.TM_CONTEXT_PATH:
                    tree.setThreadID(methodName);
                    break;
                default:
                    break;
                }
            }
            setNode(node, tree, stacktrace, args, invocation, vmStatus, config);
        }
        catch (Exception ex)
        {
            // �z��O�̗�O�����������ꍇ�͕W���G���[�o�͂ɏo�͂��Ă����B
            ex.printStackTrace();
        }
    }

    /**
     * �m�[�h��ݒ肷��
     * 
     * @param node
     */
    private static void setNode(CallTreeNode node, CallTree tree, StackTraceElement[] stacktrace,
            Object[] args, Invocation invocation, VMStatus vmStatus, S2JavelinConfig config)
    {
        try
        {
            if (node == null)
            {
                // ����Ăяo�����̓R�[���c���[������������B
                if (tree == null)
                {
                    tree = new CallTree();
                    tree.setRootCallerName(config.getRootCallerName());
                    tree.setEndCalleeName(config.getEndCalleeName());
                    callTree_.set(tree);
                    vmStatusHelper__.resetPeakMemoryUsage();
                }

                node = new CallTreeNode();
                node.setStartTime(System.currentTimeMillis());
                node.setStartVmStatus(vmStatus);

                if (config.isLogStacktrace())
                {
                    node.setStacktrace(stacktrace);
                }
                if (tree != null)
                {
                    tree.setRootNode(node);
                }
            }
            else
            {
                // 2��ڈȍ~�́A�K�w�\�����`������
                CallTreeNode parent = node;
                node = new CallTreeNode();
                node.setStartTime(System.currentTimeMillis());
                node.setStartVmStatus(vmStatusHelper__.createVMStatus());
                if (config.isLogStacktrace())
                {
                    node.setStacktrace(stacktrace);
                }
                parent.addChild(node);
                invocation.addCaller(parent.getInvocation());
            }
            // �p�����[�^�ݒ肪�s���Ă���Ƃ��A�m�[�h�Ƀp�����[�^��ݒ肷��
            if (config.isLogArgs())
            {
                String[] argStrings = new String[args.length];
                for (int index = 0; index < args.length; index++)
                {
                    if (config.isArgsDetail())
                    {
                        int argsDetailDepth = config.getArgsDetailDepth();
                        argStrings[index] = StatsUtil.buildDetailString(args[index],
                                                                          argsDetailDepth);
                    }

                    argStrings[index] = StatsUtil.toStr(args[index], config.getStringLimitLength());
                }
                node.setArgs(argStrings);
            }

            node.setInvocation(invocation);

            // �Ăяo������A
            // ���񃍃O�o�͎��̌Ăяo�����Ƃ��Ďg�p���邽�߂ɕۑ�����B
            callerNode_.set(node);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * �㏈���i�{�����������j�B
     * 
     * @param returnValue
     *            �߂�l
     * @param config
     *            �ݒ�
     */
    public static void postProcess(Object returnValue, S2JavelinConfig config)
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
            
            VMStatus vmStatus = vmStatusHelper__.createVMStatus();
            node.setEndTime(System.currentTimeMillis());
            node.setEndVmStatus(vmStatus);
            if (returnValue != null)
            {
                String returnString;
                if (config.isReturnDetail())
                {
                    int returnDetailDepth = config.getReturnDetailDepth();
                    returnString = StatsUtil.buildDetailString(returnValue, returnDetailDepth);
                    returnString = StatsUtil.toStr(returnString, config.getStringLimitLength());
                }
                else
                {
                    returnString = StatsUtil.toStr(returnValue, config.getStringLimitLength());
                }
                node.setReturnValue(returnString);
            }

            CallTreeNode parent = node.getParent();
            if (parent != null)
            {
                callerNode_.set(parent);
            }
            else
            {
                // ���v�l�L�^��臒l�𒴂��Ă����ꍇ�ɁA�g�����U�N�V�������L�^����B
                if (node.getAccumulatedTime() >= config.getStatisticsThreshold())
                {
                    recordTransaction(node);
                }

                // �t�@�C���o�͂�臒l�𒴂��Ă����ꍇ�ɁAJavelin���O���t�@�C���ɏo�͂���B
                if (node.getAccumulatedTime() >= node.getInvocation().getRecordThreshold())
                {
                    S2StatsJavelinFileGenerator generator = new S2StatsJavelinFileGenerator(
                                                                                            config.getJavelinFileDir());
                    generator.generateJaveinFile(callTree_.get(), node);
                }

                // �A���[����臒l�𒴂��Ă����ꍇ�ɁA�A���[����ʒm����B
                if (node.getAccumulatedTime() >= node.getInvocation().getAlarmThreshold())
                {
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

    private static Throwable cause_;

    /**
     * �㏈���i�{�������s���j�B
     * 
     * @param cause
     */
    public static void postProcess(Throwable cause, S2JavelinConfig config)
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

            VMStatus vmStatus = vmStatusHelper__.createVMStatus();
            node.setEndTime(System.currentTimeMillis());
            node.setEndVmStatus(vmStatus);

            if (cause_ != cause)
            {
                node.setThrowable(cause);
                node.setThrowTime(System.currentTimeMillis());
            }

            CallTreeNode parent = node.getParent();
            if (parent != null)
            {
                callerNode_.set(parent);
            }
            else
            {
                recordTransaction(node);

                // Javelin���O�t�@�C�����o�͂���B
                S2StatsJavelinFileGenerator generator = new S2StatsJavelinFileGenerator(
                                                                                        config.getJavelinFileDir());
                CallTreeNode callTreeNode = callerNode_.get();
                CallTree callTree = callTree_.get();
                generator.generateJaveinFile(callTree, callTreeNode);

                // �A���[���𑗐M����B
                sendExceedThresholdAlarm(node);

                callerNode_.set(null);
            }

            if (cause_ == cause)
            {
                // �Ăяo������񂪎擾�ł��Ȃ��ꍇ�͏������L�����Z������B
                // (���łɋL�^�ς݂̗�O�̂��߁B)
                return;
            }

            // ����������O���L�^���Ă����B
            node.getInvocation().addThrowable(cause);

            cause_ = cause;
        }
        catch (Exception ex)
        {
            // �z��O�̗�O�����������ꍇ�͕W���G���[�o�͂ɏo�͂��Ă����B
            ex.printStackTrace();
        }
    }

    public static void dumpJavelinLog(S2JavelinConfig config)
    {
        // Javelin���O�t�@�C�����o�͂���B
        S2StatsJavelinFileGenerator generator = new S2StatsJavelinFileGenerator(
                                                                                config.getJavelinFileDir());

        CallTree callTree = callTree_.get();
        CallTreeNode root = callTree.getRootNode();
        if (root != null && callTree != null)
        {
            generator.generateJaveinFile(callTree, root);

        }
    }

    public static void preProcessField(String className, String methodName, S2JavelinConfig config)
    {
        synchronized (S2StatsJavelinRecorder.class)
        {
            // ����������
            if (isInitialized == false)
            {
                javelinInit(config, server_);
                isInitialized = true;
            }
        }

        try
        {
            // �Ăяo�������擾�B
            CallTreeNode node = callerNode_.get();
            CallTree tree = null;

            if (node == null)
            {
                // ����Ăяo�����̓R�[���c���[������������B
                tree = callTree_.get();
                if (tree == null)
                {
                    tree = new CallTree();
                    callTree_.set(tree);
                    vmStatusHelper__.resetPeakMemoryUsage();
                }
                tree.setRootCallerName(config.getRootCallerName());
                tree.setEndCalleeName(config.getEndCalleeName());
            }

            CallTreeNode parent = node;
            node = new CallTreeNode();
            node.setStartTime(System.currentTimeMillis());
            node.setStartVmStatus(vmStatusHelper__.createVMStatus());
            node.setFieldAccess(true);
            Invocation invocation = parent.getInvocation();
            node.setInvocation(invocation);
            parent.addChild(node);
            // �Ăяo������A���񃍃O�o�͎��̌Ăяo�����Ƃ��Ďg�p���邽�߂ɕۑ�����B
            callerNode_.set(node);
        }
        catch (Exception ex)
        {
            // �z��O�̗�O�����������ꍇ�͕W���G���[�o�͂ɏo�͂��Ă����B
            ex.printStackTrace();
        }
    }

    public static void postProcessField(S2JavelinConfig config)
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
            
            VMStatus vmStatus = vmStatusHelper__.createVMStatus();
            node.setEndTime(System.currentTimeMillis());
            node.setEndVmStatus(vmStatus);

            CallTreeNode parent = node.getParent();
            if (parent != null)
            {
                callerNode_.set(parent);
            }
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
    public static void recordTransaction(CallTreeNode node)
    {
        Invocation invocation = node.getInvocation();
        if (invocation != null)
        {
            invocation.addInterval(StatsUtil.getElapsedTime(node));
            if (node.getParent() != null)
            {
                invocation.addCaller(node.getParent().getInvocation());
            }
        }

        List children = node.getChildren();
        for (int index = 0; index < children.size(); index++)
        {
            CallTreeNode child = (CallTreeNode)children.get(index);
            recordTransaction(child);
        }
    }

    private static void sendExceedThresholdAlarm(CallTreeNode node)
    {
        sendExceedThresholdAlarmImpl(node);

        List children = node.getChildren();
        for (int index = 0; index < children.size(); index++)
        {
            CallTreeNode child = (CallTreeNode)children.get(index);
            sendExceedThresholdAlarm(child);
        }
    }

    private static void sendExceedThresholdAlarmImpl(CallTreeNode node)
    {
        Invocation invocation = node.getInvocation();

        synchronized (alarListenerList__)
        {
            for (AlarmListener alarmListener : alarListenerList__)
            {
                alarmListener.sendExceedThresholdAlarm(invocation);
            }
        }

    }

    private static final List<AlarmListener> alarListenerList__ = new ArrayList<AlarmListener>();

    /**
     * Alarm�ʒm�ɗ��p����AlarmListener��o�^����
     * @param alarmListener Alarm�ʒm�ɗ��p����AlarmListener
     */
    public static void addListener(AlarmListener alarmListener)
    {
        synchronized (alarListenerList__)
        {
            alarListenerList__.add(alarmListener);
        }
    }

    /**
     * ���\�b�h�Ăяo���c���[������������B
     * 
     * @param callTree
     *            ���\�b�h�Ăяo���c���[
     */
    public static void initCallTree(CallTree callTree)
    {
        callTree_.set(callTree);
    }

    /**
     * �X���b�h��ID��ݒ肷��
     * 
     * @param threadId
     *            �X���b�hID
     */
    public static void setThreadId(String threadId)
    {
        CallTree callTree = callTree_.get();
        callTree.setThreadID(threadId);
    }

}
