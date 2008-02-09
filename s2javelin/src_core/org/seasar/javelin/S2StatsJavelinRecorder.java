package org.seasar.javelin;

import java.util.ArrayList;
import java.util.List;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.seasar.javelin.bean.Component;
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
    private static boolean                  isInitialized;

    /**
     * メソッドコールツリーの記録用オブジェクト。
     */
    public static final ThreadLocal<CallTree>     callTree_   = new ThreadLocal<CallTree>() {
                                                            protected synchronized CallTree initialValue()
                                                            {
                                                                return null;
                                                            }
                                                        };

    /**
     * メソッドの呼び出し元オブジェクト。
     */
    public static final ThreadLocal<CallTreeNode> callerNode_ = new ThreadLocal<CallTreeNode>() {
                                                            protected synchronized CallTreeNode initialValue()
                                                            {
                                                                return null;
                                                            }
                                                        };

    private static VMStatusHelper         vmStatusHelper__ = new VMStatusHelper();

    /** 記録条件判定クラス */
    private static RecordStrategy recordStrategy_;
    
    /**
     * 初期化処理。 
     * AlarmListenerの登録を行う。
     * RecordStrategyを初期化する。
     * MBeanServerへのContainerMBeanの登録を行う。
     * 公開用HTTPポートが指定されていた場合は、HttpAdaptorの生成と登録も行う。
     */
    private static void javelinInit(S2JavelinConfig config)
    {
        try
        {
            // エラーロガーを初期化する。
            JavelinErrorLogger.initErrorLog(config);

            // AlarmListenerを登録する
            registerAlarmListeners(config);
            
            // RecordStrategyを初期化する
            String strategyName = config.getRecordStrategy();
            recordStrategy_ = (RecordStrategy) Class.forName(strategyName).newInstance();
            
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
			        JavelinErrorLogger.getInstance().log(alarmListenerName + "をAlarmListenerとして登録しました。");
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
            if (isInitialized == false)
            {
                javelinInit(config);
                isInitialized = true;
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
                // TODO 自動生成された catch ブロック
                ex.printStackTrace();
            }
            catch (NullPointerException ex)
            {
                // TODO 自動生成された catch ブロック
                ex.printStackTrace();
            }
        }
        if(component == null)
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
                invocation = new Invocation(processName, objName, componentName, className, methodName,
                                            config.getIntervalMax(), config.getThrowableMax(),
                                            config.getRecordThreshold(), config.getAlarmThreshold());

                component.addInvocation(invocation);
            }
            catch (MalformedObjectNameException ex)
            {
                // TODO 自動生成された catch ブロック
                ex.printStackTrace();
            }
            catch (NullPointerException ex)
            {
                // TODO 自動生成された catch ブロック
                ex.printStackTrace();
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
                if (recordStrategy_.judgeGenerateJaveinFile(node) == true)
                {
                    S2StatsJavelinFileGenerator generator = new S2StatsJavelinFileGenerator(
                                                                                            config);
                    generator.generateJaveinFile(callTree_.get(), node);
                }

                // アラームの閾値を超えていた場合に、アラームを通知する。
                if (recordStrategy_.judgeSendExceedThresholdAlarm(node) == true)
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
        JmxRecorder.postProcess(cause);
        
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
                                                                                        config);
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
        S2StatsJavelinFileGenerator generator = 
        	new S2StatsJavelinFileGenerator(config);

        CallTree callTree = callTree_.get();
        if(callTree == null)
        {
            return;
        }
        
        CallTreeNode root = callTree.getRootNode();
        if (root != null)
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
                javelinInit(config);
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
            if(parent != null)
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

        List<CallTreeNode> children = node.getChildren();
        for (int index = 0; index < children.size(); index++)
        {
            CallTreeNode child = (CallTreeNode)children.get(index);
            recordTransaction(child);
        }
    }

    private static void sendExceedThresholdAlarm(CallTreeNode node)
    {
        sendExceedThresholdAlarmImpl(node);

        List<CallTreeNode> children = node.getChildren();
        for (int index = 0; index < children.size(); index++)
        {
            CallTreeNode child = (CallTreeNode)children.get(index);
            sendExceedThresholdAlarm(child);
        }
    }

    private static void sendExceedThresholdAlarmImpl(CallTreeNode node)
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
                alarmListener.sendExceedThresholdAlarm(node);
            }
        }

    }

    private static final List<AlarmListener> alarmListenerList__ = new ArrayList<AlarmListener>();

    /**
     * Alarm通知に利用するAlarmListenerを登録する
     * @param alarmListener Alarm通知に利用するAlarmListener
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

}
