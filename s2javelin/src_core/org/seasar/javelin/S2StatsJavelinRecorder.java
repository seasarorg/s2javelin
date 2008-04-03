package org.seasar.javelin;

import java.util.ArrayList;
import java.util.List;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.seasar.javelin.bean.Component;
import org.seasar.javelin.bean.InvocationInterval;
import org.seasar.javelin.bean.Invocation;
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
    private static boolean                        initialized_;

    /**
     * ���\�b�h�R�[���c���[�̋L�^�p�I�u�W�F�N�g�B
     */
    public static final ThreadLocal<CallTree>     callTree_        = new ThreadLocal<CallTree>() {
                                                                       protected synchronized CallTree initialValue()
                                                                       {
                                                                           return new CallTree();
                                                                       }
                                                                   };

    /**
     * ���\�b�h�̌Ăяo�����I�u�W�F�N�g�B
     */
    public static final ThreadLocal<CallTreeNode> callerNode_      = new ThreadLocal<CallTreeNode>() {
                                                                       protected synchronized CallTreeNode initialValue()
                                                                       {
                                                                           return null;
                                                                       }
                                                                   };

    private static VMStatusHelper                 vmStatusHelper__ = new VMStatusHelper();

    /** �L�^��������N���X */
    private static RecordStrategy                 recordStrategy_;

    /**
     * �����������B AlarmListener�̓o�^���s���B RecordStrategy������������B
     * MBeanServer�ւ�ContainerMBean�̓o�^���s���B
     * ���J�pHTTP�|�[�g���w�肳��Ă����ꍇ�́AHttpAdaptor�̐����Ɠo�^���s���B
     * @param config �p�����[�^�̐ݒ�l��ۑ�����I�u�W�F�N�g
     */
    public static void javelinInit(S2JavelinConfig config)
    {
        if (initialized_ == true)
        {
        	return;
        }
        
        try
        {
            // �G���[���K�[������������B
            SystemLogger.initSystemLog(config);

            // AlarmListener��o�^����
            registerAlarmListeners(config);

            // RecordStrategy������������
            String strategyName = config.getRecordStrategy();
            recordStrategy_ = (RecordStrategy)loadClass(strategyName).newInstance();

            // �X���b�h�̊Ď����J�n����B
            vmStatusHelper__.init();

            // TCP�ł̐ڑ���t���J�n����B
            int port = config.getAcceptPort();
            JavelinAcceptThread.getInstance().start(port);
            
            initialized_ = true;

        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
    }

    /**
     * AlarmListener�̃N���X��Javelin�ݒ肩��ǂݍ��݁A�o�^����B �N���X�̃��[�h�́A�ȉ��̏��ŃN���X���[�_�ł̃��[�h�����݂�B
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
                    AlarmListener alarmListener = (AlarmListener)listener;
                    alarmListener.init();
                    addListener(alarmListener);
                    SystemLogger.getInstance().info(
                                                         alarmListenerName
                                                                 + "��AlarmListener�Ƃ��ēo�^���܂����B");
                }
                else
                {
                    SystemLogger.getInstance().info(
                                                         alarmListenerName
                                                                 + "��AlarmListener���������Ă��Ȃ����߁AAlarm�ʒm�ɗ��p���܂���B");
                }
            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(
                                                     alarmListenerName
                                                             + "�̓o�^�Ɏ��s�������߁AAlarm�ʒm�ɗ��p���܂���B", ex);
            }
        }
    }

    /**
     * �N���X�����[�h����B �ȉ��̏��ŃN���X���[�_�ł̃��[�h�����݂�B
     * <ol>
     * <li>S2StatsJavelinRecorder�����[�h�����N���X���[�_</li>
     * <li>�R���e�L�X�g�N���X���[�_</li>
     * </ol>
     * 
     * @param className
     *            ���[�h����N���X�̖��O�B
     * @return ���[�h�����N���X�B
     * @throws ClassNotFoundException
     *             �S�ẴN���X���[�_�ŃN���X��������Ȃ��ꍇ
     */
    private static Class<?> loadClass(String className)
        throws ClassNotFoundException
    {

        Class<?> clazz;
        try
        {
            clazz = Class.forName(className);
        }
        catch (ClassNotFoundException cnfe)
        {
            SystemLogger.getInstance().info(className + "�̃��[�h�Ɏ��s�������߁A�R���e�L�X�g�N���X���[�_����̃��[�h���s���܂��B");
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        }

        return clazz;
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
        preProcess(className, methodName, args, null, config);
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
        JmxRecorder.preProcess(className, methodName, config);

        synchronized (S2StatsJavelinRecorder.class)
        {
            // ����������
            if (initialized_ == false)
            {
                javelinInit(config);
            }
        }

        VMStatus vmStatus = vmStatusHelper__.createVMStatus();

        Component component = MBeanManager.getComponent(className);
        ObjectName componentName = null;
        if (component == null)
        {
            String name = ObjectNameUtil.createComponentBeanName(className, config);
            try
            {
                componentName = new ObjectName(name);
                component = new Component(componentName, className);

                MBeanManager.setComponent(className, component);
            }
            catch (MalformedObjectNameException ex)
            {
                SystemLogger.getInstance().warn(ex);
            }
            catch (NullPointerException ex)
            {
                SystemLogger.getInstance().warn(ex);
            }
        }
        if (component == null)
        {
            return;
        }
        Invocation invocation = component.getInvocation(methodName);
        if (invocation == null)
        {
            String name = ObjectNameUtil.createInvocationBeanName(className, methodName, config);
            ObjectName objName;
            try
            {
                objName = new ObjectName(name);
                String processName = VMStatusHelper.getProcessName();
                invocation = new Invocation(processName, objName, componentName, className,
                                            methodName, config.getIntervalMax(),
                                            config.getThrowableMax(), config.getRecordThreshold(),
                                            config.getAlarmThreshold());

                component.addInvocation(invocation);
            }
            catch (MalformedObjectNameException ex)
            {
                SystemLogger.getInstance().warn(ex);
            }
            catch (NullPointerException ex)
            {
                SystemLogger.getInstance().warn(ex);
            }
        }

        try
        {
            // �Ăяo�������擾�B
            CallTreeNode node = callerNode_.get();
            CallTree tree = callTree_.get();

            if (node == null)
            {
                vmStatusHelper__.resetPeakMemoryUsage();
                // ����Ăяo�����̓R�[���c���[������������B
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
            SystemLogger.getInstance().warn(ex);
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
                node = initCallTree(tree, stacktrace, vmStatus, config);
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
                if(parent.getChildren().size() > config.getCallTreeMax())
                {
                    String jvnFileName = dumpJavelinLog(config);
                    SystemLogger.getInstance().warn(
                                                    "CallTree�̃T�C�Y��臒l�𒴂��܂����BCallTree���N���A���Ajvn���O���o�͂��܂��B�t�@�C����:"
                                                            + jvnFileName);
                    node = initCallTree(tree, stacktrace, vmStatus, config);
                }
                else
                {
                    invocation.addCaller(parent.getInvocation());
                }
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
                    else
                    {
                        argStrings[index] = StatsUtil.toStr(args[index],
                                                            config.getStringLimitLength());
                    }
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
            SystemLogger.getInstance().warn(ex);
        }
    }

    private static CallTreeNode initCallTree(CallTree tree, StackTraceElement[] stacktrace,
            VMStatus vmStatus, S2JavelinConfig config)
    {
        CallTreeNode node;
        vmStatusHelper__.resetPeakMemoryUsage();

        // ����Ăяo�����̓R�[���c���[������������B

        node = new CallTreeNode();
        node.setStartTime(System.currentTimeMillis());
        node.setStartVmStatus(vmStatus);

        if (config.isLogStacktrace())
        {
            node.setStacktrace(stacktrace);
        }

        if (tree != null)
        {
            tree.setRootCallerName(config.getRootCallerName());
            tree.setEndCalleeName(config.getEndCalleeName());
            tree.setRootNode(node);
        }
        return node;
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
        JmxRecorder.postProcess(config);
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

            long endCpuTime   = node.getEndVmStatus().getCpuTime();
            long startCpuTime = node.getStartVmStatus().getCpuTime();
            long cpuTime      = endCpuTime - startCpuTime;
            if (cpuTime < 0)
            {
                cpuTime = 0;
            }
            node.setCpuTime(cpuTime);

            long endUserTime   = node.getEndVmStatus().getUserTime();
            long startUserTime = node.getStartVmStatus().getUserTime();
            long userTime      = endUserTime - startUserTime;
            if (userTime < 0)
            {
                userTime = 0;
            }
            node.setUserTime(userTime);
            
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
                String jvnLogFileName = null;
                if (recordStrategy_.judgeGenerateJaveinFile(node) == true)
                {
                    S2StatsJavelinFileGenerator generator = new S2StatsJavelinFileGenerator(config);
                    jvnLogFileName = generator.generateJaveinFile(
                                                                  callTree_.get(),
                                                                  node,
                                                                  recordStrategy_.createCallback(node));
                }

                // �A���[����臒l�𒴂��Ă����ꍇ�ɁA�A���[����ʒm����B
                if (recordStrategy_.judgeSendExceedThresholdAlarm(node) == true)
                {
                    sendExceedThresholdAlarm(jvnLogFileName, node);
                }

                CallTree tree = callTree_.get();
                if (tree != null)
                {
                    tree.executeCallback();
                }

                callerNode_.set(null);
                callTree_.set(new CallTree());
            }
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
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
        JmxRecorder.postProcess(cause);

        try
        {
            CallTree callTree = callTree_.get();
            if(callTree == null)
            {
                return;
            }

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
            
            long endCpuTime = node.getEndVmStatus().getCpuTime();
            long startCpuTime = node.getStartVmStatus().getCpuTime();
            long cpuTime = endCpuTime - startCpuTime;
            if (cpuTime < 0)
            {
                cpuTime = 0;
            }
            node.setCpuTime(cpuTime);

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
                S2StatsJavelinFileGenerator generator = new S2StatsJavelinFileGenerator(config);
                CallTreeNode callTreeNode = callerNode_.get();
                String jvnLogFileName = generator.generateJaveinFile(
                                                                     callTree,
                                                                     callTreeNode,
                                                                     recordStrategy_.createCallback(node));

                // �A���[���𑗐M����B
                sendExceedThresholdAlarm(jvnLogFileName, node);

                if (callTree != null)
                {
                    callTree.executeCallback();
                }

                callerNode_.set(null);
                callTree_.set(new CallTree());
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
            SystemLogger.getInstance().warn(ex);
        }
    }

    public static String dumpJavelinLog(S2JavelinConfig config)
    {
        String fileName = "";
        // Javelin���O�t�@�C�����o�͂���B
        S2StatsJavelinFileGenerator generator = new S2StatsJavelinFileGenerator(config);

        CallTree callTree = callTree_.get();
        if (callTree == null)
        {
            return fileName;
        }

        CallTreeNode root = callTree.getRootNode();
        if (root != null)
        {
            fileName =
                    generator.generateJaveinFile(callTree, root,
                                                 recordStrategy_.createCallback(root));
        }
        
        return fileName;
    }

    public static void preProcessField(String className, String methodName, S2JavelinConfig config)
    {
        synchronized (S2StatsJavelinRecorder.class)
        {
            // ����������
            if (initialized_ == false)
            {
                javelinInit(config);
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
            if (parent != null)
            {
                Invocation invocation = parent.getInvocation();
                node.setInvocation(invocation);
                parent.addChild(node);
            }
            // �Ăяo������A���񃍃O�o�͎��̌Ăяo�����Ƃ��Ďg�p���邽�߂ɕۑ�����B
            callerNode_.set(node);
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
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
            SystemLogger.getInstance().warn(ex);
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
            InvocationInterval interval    = StatsUtil.getElapsedTime(node);
            invocation.addInterval(interval);
            if (node.getParent() != null)
            {
                invocation.addCaller(node.getParent().getInvocation());
            }
        }

        List<CallTreeNode> children = node.getChildren();
        for (int index = 0; index < children.size(); index++)
        {
            CallTreeNode child = (CallTreeNode)children.get(index);
            recordTransaction(child);
        }
    }

    private static void sendExceedThresholdAlarm(String jvnFileName, CallTreeNode node)
    {
        sendExceedThresholdAlarmImpl(jvnFileName, node);

        List<CallTreeNode> children = node.getChildren();
        for (int index = 0; index < children.size(); index++)
        {
            CallTreeNode child = (CallTreeNode)children.get(index);
            sendExceedThresholdAlarm(jvnFileName, child);
        }
    }

    private static void sendExceedThresholdAlarmImpl(String jvnFileName, CallTreeNode node)
    {
        synchronized (alarmListenerList__)
        {
            for (AlarmListener alarmListener : alarmListenerList__)
            {
                // ���[�g�m�[�h�̂�Alarm�𑗐M����AlarmListener�́A
                // �e�����m�[�h�𖳎�����B
                boolean sendingRootOnly = alarmListener.isSendingRootOnly();
                if (sendingRootOnly == true && node.getParent() != null)
                {
                    continue;
                }

                // AlarmListener�ɂ�CallTreeNode�����̂܂ܓn��
                // ���A���[���ʒm�ŗݐώ��Ԃ��g�p������̂������
                alarmListener.sendExceedThresholdAlarm(jvnFileName, node);
            }
        }

    }

    private static final List<AlarmListener> alarmListenerList__ = new ArrayList<AlarmListener>();

    /**
     * Alarm�ʒm�ɗ��p����AlarmListener��o�^����
     * 
     * @param alarmListener
     *            Alarm�ʒm�ɗ��p����AlarmListener
     */
    public static void addListener(AlarmListener alarmListener)
    {
        synchronized (alarmListenerList__)
        {
            alarmListenerList__.add(alarmListener);
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
    
    /**
     * ����������Ă��邩��Ԃ�.
     * @return�@true:����������Ă���Afalse:����������Ă��Ȃ�.
     */
    public static boolean isInitialized()
    {
        return initialized_;
    }

}
