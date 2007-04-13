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
    /** プラットフォームMBeanサーバ */
    private static MBeanServer               server_     = ManagementFactory.getPlatformMBeanServer();

    /**
     * メソッドコールツリーの記録用オブジェクト。
     */
    public static ThreadLocal<CallTree>     callTree_   = new ThreadLocal<CallTree>() {
                                                             protected synchronized CallTree initialValue( )
                                                             {
                                                                 return null;
                                                             }
                                                         };

    /**
     * メソッドの呼び出し元オブジェクト。
     */
    public static ThreadLocal<CallTreeNode> callerNode_ = new ThreadLocal<CallTreeNode>() {
                                                             protected synchronized CallTreeNode initialValue( )
                                                             {
                                                                 return null;
                                                             }
                                                         };

    /**
     * 前処理。
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

            // 呼び出し元情報取得。
            CallTreeNode node = callerNode_.get();

            if (node == null)
            {
                // 初回呼び出し時はコールツリーを初期化する。
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

            // 呼び出し先を、
            // 次回ログ出力時の呼び出し元として使用するために保存する。
            callerNode_.set(node);
        }
        catch (Exception ex)
        {
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
            ex.printStackTrace();
        }
    }

    /**
     * 後処理（本処理成功時）。
     * @param spent
     */
    public static void postProcess(Object returnValue, S2StatsJavelinConfig config)
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

            node.setEndTime(System.currentTimeMillis());
            node.setReturnValue(returnValue);

            CallTreeNode parent = node.getParent();
            if (parent != null)
            {
                callerNode_.set(parent);
            }
            else
            {
                // ルートノードでの経過時間が閾値を超えていた場合は、
                // トランザクションを記録する。
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
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
            ex.printStackTrace();
        }
    }

	/**
     * 後処理（本処理失敗時）。
     * @param cause
     */
    public static void postProcess(Throwable cause)
    {
        try
        {
            // 呼び出し元情報取得。
            CallTreeNode node = callerNode_.get();
            if (node == null)
            {
                // 呼び出し元情報が取得できない場合は処理をキャンセルする。
                // (すでに記録済みの例外のため。)
                return;
            }

            // 発生した例外を記録しておく。
            node.getInvocation().addThrowable(cause);

            //呼び出し元を消去しておく。
            callerNode_.set(null);
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
