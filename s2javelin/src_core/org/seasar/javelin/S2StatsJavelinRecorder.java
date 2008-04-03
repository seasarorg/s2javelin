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
 * メソッド呼び出し情報の取得を行うクラス。
 */
public class S2StatsJavelinRecorder
{
    /** 初期化判定フラグ */
    private static boolean                        initialized_;

    /**
     * メソッドコールツリーの記録用オブジェクト。
     */
    public static final ThreadLocal<CallTree>     callTree_        = new ThreadLocal<CallTree>() {
                                                                       protected synchronized CallTree initialValue()
                                                                       {
                                                                           return new CallTree();
                                                                       }
                                                                   };

    /**
     * メソッドの呼び出し元オブジェクト。
     */
    public static final ThreadLocal<CallTreeNode> callerNode_      = new ThreadLocal<CallTreeNode>() {
                                                                       protected synchronized CallTreeNode initialValue()
                                                                       {
                                                                           return null;
                                                                       }
                                                                   };

    private static VMStatusHelper                 vmStatusHelper__ = new VMStatusHelper();

    /** 記録条件判定クラス */
    private static RecordStrategy                 recordStrategy_;

    /**
     * 初期化処理。 AlarmListenerの登録を行う。 RecordStrategyを初期化する。
     * MBeanServerへのContainerMBeanの登録を行う。
     * 公開用HTTPポートが指定されていた場合は、HttpAdaptorの生成と登録も行う。
     * @param config パラメータの設定値を保存するオブジェクト
     */
    public static void javelinInit(S2JavelinConfig config)
    {
        if (initialized_ == true)
        {
        	return;
        }
        
        try
        {
            // エラーロガーを初期化する。
            SystemLogger.initSystemLog(config);

            // AlarmListenerを登録する
            registerAlarmListeners(config);

            // RecordStrategyを初期化する
            String strategyName = config.getRecordStrategy();
            recordStrategy_ = (RecordStrategy)loadClass(strategyName).newInstance();

            // スレッドの監視を開始する。
            vmStatusHelper__.init();

            // TCPでの接続受付を開始する。
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
     * AlarmListenerのクラスをJavelin設定から読み込み、登録する。 クラスのロードは、以下の順でクラスローダでのロードを試みる。
     * <ol>
     * <li>S2StatsJavelinRecorderをロードしたクラスローダ</li>
     * <li>コンテキストクラスローダ</li>
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
                                                                 + "をAlarmListenerとして登録しました。");
                }
                else
                {
                    SystemLogger.getInstance().info(
                                                         alarmListenerName
                                                                 + "はAlarmListenerを実装していないため、Alarm通知に利用しません。");
                }
            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(
                                                     alarmListenerName
                                                             + "の登録に失敗したため、Alarm通知に利用しません。", ex);
            }
        }
    }

    /**
     * クラスをロードする。 以下の順でクラスローダでのロードを試みる。
     * <ol>
     * <li>S2StatsJavelinRecorderをロードしたクラスローダ</li>
     * <li>コンテキストクラスローダ</li>
     * </ol>
     * 
     * @param className
     *            ロードするクラスの名前。
     * @return ロードしたクラス。
     * @throws ClassNotFoundException
     *             全てのクラスローダでクラスが見つからない場合
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
            SystemLogger.getInstance().info(className + "のロードに失敗したため、コンテキストクラスローダからのロードを行います。");
            clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        }

        return clazz;
    }

    /**
     * JavelinRecorder, JDBCJavelinRecorderから呼び出したときの前処理。
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
     * 前処理。
     * 
     * @param className
     *            クラス名
     * @param methodName
     *            メソッド名
     * @param args
     *            引数
     * @param stacktrace
     *            スタックトレース
     * @param config
     *            設定
     */
    public static void preProcess(String className, String methodName, Object[] args,
            StackTraceElement[] stacktrace, S2JavelinConfig config)
    {
        JmxRecorder.preProcess(className, methodName, config);

        synchronized (S2StatsJavelinRecorder.class)
        {
            // 初期化処理
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
            // 呼び出し元情報取得。
            CallTreeNode node = callerNode_.get();
            CallTree tree = callTree_.get();

            if (node == null)
            {
                vmStatusHelper__.resetPeakMemoryUsage();
                // 初回呼び出し時はコールツリーを初期化する。
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
     * ノードを設定する
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
                // 2回目以降は、階層構造を形成する
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
                                                    "CallTreeのサイズが閾値を超えました。CallTreeをクリアし、jvnログを出力します。ファイル名:"
                                                            + jvnFileName);
                    node = initCallTree(tree, stacktrace, vmStatus, config);
                }
                else
                {
                    invocation.addCaller(parent.getInvocation());
                }
            }
            // パラメータ設定が行われているとき、ノードにパラメータを設定する
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

            // 呼び出し先を、
            // 次回ログ出力時の呼び出し元として使用するために保存する。
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

        // 初回呼び出し時はコールツリーを初期化する。

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
     * 後処理（本処理成功時）。
     * 
     * @param returnValue
     *            戻り値
     * @param config
     *            設定
     */
    public static void postProcess(Object returnValue, S2JavelinConfig config)
    {
        JmxRecorder.postProcess(config);
        try
        {
            // 呼び出し元情報取得。
            CallTreeNode node = callerNode_.get();
            if (node == null)
            {
                // 呼び出し元情報が取得できない場合は処理をキャンセルする。
                // (下位レイヤで例外が発生した場合のため。)
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
                // 統計値記録の閾値を超えていた場合に、トランザクションを記録する。
                if (node.getAccumulatedTime() >= config.getStatisticsThreshold())
                {
                    recordTransaction(node);
                }

                // ファイル出力の閾値を超えていた場合に、Javelinログをファイルに出力する。
                String jvnLogFileName = null;
                if (recordStrategy_.judgeGenerateJaveinFile(node) == true)
                {
                    S2StatsJavelinFileGenerator generator = new S2StatsJavelinFileGenerator(config);
                    jvnLogFileName = generator.generateJaveinFile(
                                                                  callTree_.get(),
                                                                  node,
                                                                  recordStrategy_.createCallback(node));
                }

                // アラームの閾値を超えていた場合に、アラームを通知する。
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
     * 後処理（本処理失敗時）。
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

            // 呼び出し元情報取得。
            CallTreeNode node = callerNode_.get();

            if (node == null)
            {
                // 呼び出し元情報が取得できない場合は処理をキャンセルする。
                // (下位レイヤで例外が発生した場合のため。)
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

                // Javelinログファイルを出力する。
                S2StatsJavelinFileGenerator generator = new S2StatsJavelinFileGenerator(config);
                CallTreeNode callTreeNode = callerNode_.get();
                String jvnLogFileName = generator.generateJaveinFile(
                                                                     callTree,
                                                                     callTreeNode,
                                                                     recordStrategy_.createCallback(node));

                // アラームを送信する。
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
                // 呼び出し元情報が取得できない場合は処理をキャンセルする。
                // (すでに記録済みの例外のため。)
                return;
            }

            // 発生した例外を記録しておく。
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
        // Javelinログファイルを出力する。
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
            // 初期化処理
            if (initialized_ == false)
            {
                javelinInit(config);
            }
        }

        try
        {
            // 呼び出し元情報取得。
            CallTreeNode node = callerNode_.get();
            CallTree tree = null;

            if (node == null)
            {
                // 初回呼び出し時はコールツリーを初期化する。
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
            // 呼び出し先を、次回ログ出力時の呼び出し元として使用するために保存する。
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
            // 呼び出し元情報取得。
            CallTreeNode node = callerNode_.get();
            if (node == null)
            {
                // 呼び出し元情報が取得できない場合は処理をキャンセルする。
                // (下位レイヤで例外が発生した場合のため。)
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
     * トランザクションを記録する。
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
                // ルートノードのみAlarmを送信するAlarmListenerは、
                // 親を持つノードを無視する。
                boolean sendingRootOnly = alarmListener.isSendingRootOnly();
                if (sendingRootOnly == true && node.getParent() != null)
                {
                    continue;
                }

                // AlarmListenerにはCallTreeNodeをそのまま渡す
                // →アラーム通知で累積時間を使用するものがある為
                alarmListener.sendExceedThresholdAlarm(jvnFileName, node);
            }
        }

    }

    private static final List<AlarmListener> alarmListenerList__ = new ArrayList<AlarmListener>();

    /**
     * Alarm通知に利用するAlarmListenerを登録する
     * 
     * @param alarmListener
     *            Alarm通知に利用するAlarmListener
     */
    public static void addListener(AlarmListener alarmListener)
    {
        synchronized (alarmListenerList__)
        {
            alarmListenerList__.add(alarmListener);
        }
    }

    /**
     * メソッド呼び出しツリーを初期化する。
     * 
     * @param callTree
     *            メソッド呼び出しツリー
     */
    public static void initCallTree(CallTree callTree)
    {
        callTree_.set(callTree);
    }

    /**
     * スレッドのIDを設定する
     * 
     * @param threadId
     *            スレッドID
     */
    public static void setThreadId(String threadId)
    {
        CallTree callTree = callTree_.get();
        callTree.setThreadID(threadId);
    }
    
    /**
     * 初期化されているかを返す.
     * @return　true:初期化されている、false:初期化されていない.
     */
    public static boolean isInitialized()
    {
        return initialized_;
    }

}
