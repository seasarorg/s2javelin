package org.seasar.javelin.stats;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.seasar.javelin.stats.bean.Component;
import org.seasar.javelin.stats.bean.ComponentMBean;
import org.seasar.javelin.stats.bean.Container;
import org.seasar.javelin.stats.bean.ContainerMBean;
import org.seasar.javelin.stats.bean.Invocation;
import org.seasar.javelin.stats.bean.InvocationMBean;
import org.seasar.javelin.stats.bean.Statistics;
import org.seasar.javelin.stats.bean.StatisticsMBean;
import org.seasar.javelin.stats.util.StatsUtil;

public class JmxRecorder
{
    /** プラットフォームMBeanサーバ */
    private static MBeanServer               server_     = ManagementFactory.getPlatformMBeanServer();

    /** 初期化フラグ。初期化済みの場合はtrue。 */
    private static boolean     isInitialized_   = false;

    /**
     * 初期化処理。
     * MBeanServerへのContainerMBeanの登録を行う。
     * 公開用HTTPポートが指定されていた場合は、HttpAdaptorの生成と登録も行う。
     */
    private static void init(S2StatsJavelinConfig config)
    {
        try
        {
            server_ = ManagementFactory.getPlatformMBeanServer();

            if (config.getHttpPort() != 0)
            {
            	try
            	{
                	Mx4JLauncher.execute(server_, config.getHttpPort());
            	}
            	catch(Exception ex)
            	{
            		ex.printStackTrace();
            	}
            }

            ContainerMBean container;
            ObjectName containerName = new ObjectName(config.getDomain()
                    + ".container:type=" + ContainerMBean.class.getName());
            if (server_.isRegistered(containerName))
            {
                Set beanSet = server_.queryMBeans(containerName, null);
                if (beanSet.size() > 0)
                {
                    ContainerMBean[] containers = (ContainerMBean[])beanSet.toArray(new ContainerMBean[beanSet.size()]);
                    container = (ContainerMBean)(containers[0]);
                }
            }
            else
            {
                container = new Container();
                server_.registerMBean(container, containerName);
            }

            StatisticsMBean statistics;
            ObjectName statisticsName = new ObjectName(config.getDomain()
                    + ".statistics:type=" + StatisticsMBean.class.getName());
            if (server_.isRegistered(statisticsName))
            {
                Set beanSet = server_.queryMBeans(statisticsName, null);
                if (beanSet.size() > 0)
                {
                    StatisticsMBean[] statisticses = (StatisticsMBean[])beanSet.toArray(new StatisticsMBean[beanSet.size()]);
                    statistics = (StatisticsMBean)(statisticses[0]);
                }
            }
            else
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
     * 前処理。
     * 
     * @param invocationBean
     */
    public static void preProcess(
    		String className
    		, String methodName
    		, S2StatsJavelinConfig config)
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
        catch (Throwable th)
        {
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
        	// (上位には伝播させない。)
        	th.printStackTrace();
        }
    }

    /**
     * 後処理（本処理成功時）。
     * @param spent
     */
    public static void postProcess()
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
        catch (Throwable th)
        {
            // 想定外の例外が発生した場合は標準エラー出力に出力しておく。
        	// (上位には伝播させない。)
            th.printStackTrace();
        }
    }

	/**
     * 後処理（本処理失敗時）。
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
        node.getInvocation().sendExceedThresholdAlarm();
        
        List children = node.getChildren();
		for (int index = 0; index <  children.size(); index++)
        {
			CallTreeNode child = (CallTreeNode) children.get(index);
        	sendExceedThresholdAlarm(child);
        }
	}
}
