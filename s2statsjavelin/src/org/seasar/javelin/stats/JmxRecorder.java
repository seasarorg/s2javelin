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
    /** プラットフォームMBeanサーバ */
    private static MBeanServer               server_     = ManagementFactory.getPlatformMBeanServer();

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
            CallTreeNode node = S2StatsJavelinRecorder.callerNode_.get();
            if (node == null)
            {
                // 呼び出し元情報が取得できない場合は処理をキャンセルする。
                // (下位レイヤで例外が発生した場合のため。)
                return;
            }

            CallTreeNode parent = node.getParent();
            if (parent != null)
            {
            }
            else
            {
                // ルートノードでの経過時間が閾値を超えていた場合は、
                // トランザクションを記録する。
            	if (node.getAccumulatedTime() >= node.getInvocation().getAlarmThreshold())
            	{
                    sendExceedThresholdAlarm(node);
            	}
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
            CallTreeNode node = S2StatsJavelinRecorder.callerNode_.get();
            if (node == null)
            {
                // 呼び出し元情報が取得できない場合は処理をキャンセルする。
                // (すでに記録済みの例外のため。)
                return;
            }

            // 発生した例外を記録しておく。
            node.getInvocation().addThrowable(cause);

            //呼び出し元を消去しておく。
            S2StatsJavelinRecorder.callerNode_.set(null);
        }
        catch (Exception ex)
        {
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
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
