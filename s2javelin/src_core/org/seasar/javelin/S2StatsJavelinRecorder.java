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
 * メソッド呼び出し情報の取得を行うクラス。
 */
public class S2StatsJavelinRecorder
{
    /** 初期化判定フラグ */
    private static boolean                  isInitialized;

    /** プラットフォームMBeanServer */
    private static MBeanServer              server_     = ManagementFactory.getPlatformMBeanServer();

    /**
     * メソッドコールツリーの記録用オブジェクト。
     */
    public static ThreadLocal<CallTree>     callTree_   = new ThreadLocal<CallTree>() {
                                                            protected synchronized CallTree initialValue()
                                                            {
                                                                return null;
                                                            }
                                                        };

    /**
     * メソッドの呼び出し元オブジェクト。
     */
    public static ThreadLocal<CallTreeNode> callerNode_ = new ThreadLocal<CallTreeNode>() {
                                                            protected synchronized CallTreeNode initialValue()
                                                            {
                                                                return null;
                                                            }
                                                        };

    private static VMStatusHelper         vmStatusHelper__ = new VMStatusHelper();

    /**
     * 初期化処理。 
     * AlarmListenerの登録を行う。
     * MBeanServerへのContainerMBeanの登録を行う。
     * 公開用HTTPポートが指定されていた場合は、HttpAdaptorの生成と登録も行う。
     */
    private static void javelinInit(S2JavelinConfig config, MBeanServer server)
    {
        try
        {
            // エラーロガーを初期化する。
            JavelinErrorLogger.initErrorLog(config);

            // AlarmListenerを登録する
            registerAlarmListeners(config);
            
            // MBeanを登録する
            server = ManagementFactory.getPlatformMBeanServer();

            ContainerMBean container;
            ObjectName containerName = new ObjectName(config.getDomain() + ".container:type="
                    + ContainerMBean.class.getName());
            if (server.isRegistered(containerName) == false)
            {
                container = new Container();
                server.registerMBean(container, containerName);
            }

            // スレッドの監視を開始する。
            vmStatusHelper__.init();

            // TCPでの接続受付を開始する。
            int port = config.getAcceptPort();
            JavelinAcceptThread.getInstance().start(port);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * AlarmListenerのクラスをJavelin設定から読み込み、登録する。
     * クラスのロードは、以下の順でクラスローダでのロードを試みる。
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
		            addListener((AlarmListener) listener);
			        JavelinErrorLogger.getInstance().log(alarmListenerName + "をAlarmListnerとして登録しました。");
		        }
		        else
		        {
		            JavelinErrorLogger.getInstance().log(alarmListenerName + "はAlarmListenerを実装していないため、Alarm通知に利用しません。");
		        }
		    }
		    catch (Exception ex)
		    {
		        JavelinErrorLogger.getInstance().log(alarmListenerName + "の登録に失敗したため、Alarm通知に利用しません。", ex);
		    }
		}
	}

	/**
	 * クラスをロードする。
     * 以下の順でクラスローダでのロードを試みる。
     * <ol>
     * <li>S2StatsJavelinRecorderをロードしたクラスローダ</li>
     * <li>コンテキストクラスローダ</li>
     * </ol>
	 * 
	 * @param className ロードするクラスの名前。
	 * @return ロードしたクラス。
	 * @throws ClassNotFoundException 全てのクラスローダでクラスが見つからない場合
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
	        JavelinErrorLogger.getInstance().log(className + "のロードに失敗したため、コンテキストクラスローダからのロードを行います。");
			clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
		}
		
		return clazz;
	}

    /**
     * MBeanに登録する文字列を生成する
     * 
     * @param className
     *            クラス名
     * @param methodName
     *            メソッド名
     * @param config
     *            設定ファイルから読み込んだ設定値
     * @return 登録する文字列
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
        synchronized (S2StatsJavelinRecorder.class)
        {
            // 初期化処理
            if (isInitialized == false)
            {
                javelinInit(config, server_);
                isInitialized = true;
            }
        }

        VMStatus vmStatus = vmStatusHelper__.createVMStatus();

        try
        {
            // ルートクラス名を作る
            StringBuffer buffer = new StringBuffer();
            buffer.append(className);
            String rootClassName = buffer.toString();

            Component componentBeanRoot = MBeanManager.getComponent(rootClassName);

            String name = createComponentBeanName(className, config);
            ObjectName componentName = new ObjectName(name);

            // ルートクラス場合、ルートクラス名を設定する
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

            // 初回呼び出し時はコールツリーを初期化する。
            CallTree tree = callTree_.get();

            setNode(node, tree, null, args, invocation, vmStatus, config);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

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
        synchronized (S2StatsJavelinRecorder.class)
        {
            // 初期化処理
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
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
            ex.printStackTrace();
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
                // 初回呼び出し時はコールツリーを初期化する。
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
                invocation.addCaller(parent.getInvocation());
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

                    argStrings[index] = StatsUtil.toStr(args[index], config.getStringLimitLength());
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
            ex.printStackTrace();
        }
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
                if (node.getAccumulatedTime() >= node.getInvocation().getRecordThreshold())
                {
                    S2StatsJavelinFileGenerator generator = new S2StatsJavelinFileGenerator(
                                                                                            config.getJavelinFileDir());
                    generator.generateJaveinFile(callTree_.get(), node);
                }

                // アラームの閾値を超えていた場合に、アラームを通知する。
                if (node.getAccumulatedTime() >= node.getInvocation().getAlarmThreshold())
                {
                    sendExceedThresholdAlarm(node);
                }

                callerNode_.set(null);
            }
        }
        catch (Exception ex)
        {
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
            ex.printStackTrace();
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
                S2StatsJavelinFileGenerator generator = new S2StatsJavelinFileGenerator(
                                                                                        config.getJavelinFileDir());
                CallTreeNode callTreeNode = callerNode_.get();
                CallTree callTree = callTree_.get();
                generator.generateJaveinFile(callTree, callTreeNode);

                // アラームを送信する。
                sendExceedThresholdAlarm(node);

                callerNode_.set(null);
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
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
            ex.printStackTrace();
        }
    }

    public static void dumpJavelinLog(S2JavelinConfig config)
    {
        // Javelinログファイルを出力する。
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
            // 初期化処理
            if (isInitialized == false)
            {
                javelinInit(config, server_);
                isInitialized = true;
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
            Invocation invocation = parent.getInvocation();
            node.setInvocation(invocation);
            parent.addChild(node);
            // 呼び出し先を、次回ログ出力時の呼び出し元として使用するために保存する。
            callerNode_.set(node);
        }
        catch (Exception ex)
        {
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
            ex.printStackTrace();
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
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
            ex.printStackTrace();
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
     * Alarm通知に利用するAlarmListenerを登録する
     * @param alarmListener Alarm通知に利用するAlarmListener
     */
    public static void addListener(AlarmListener alarmListener)
    {
        synchronized (alarListenerList__)
        {
            alarListenerList__.add(alarmListener);
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

}
