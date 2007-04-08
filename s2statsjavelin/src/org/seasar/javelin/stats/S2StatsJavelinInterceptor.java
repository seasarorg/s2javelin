package org.seasar.javelin.stats;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.javelin.stats.bean.Container;
import org.seasar.javelin.stats.bean.ContainerMBean;
import org.seasar.javelin.stats.bean.Statistics;
import org.seasar.javelin.stats.bean.StatisticsMBean;

/**
 * Component間の呼び出し関係をMBeanとして公開するためのInterceptor。
 * 以下の情報を、MBean経由で取得することが可能。
 * <ol>
 * <li>メソッドの呼び出し回数</li>
 * <li>メソッドの平均処理時間（ミリ秒単位）</li>
 * <li>メソッドの最長処理時間（ミリ秒単位）</li>
 * <li>メソッドの最短処理時間（ミリ秒単位）</li>
 * <li>メソッドの呼び出し元</li>
 * <li>例外の発生回数</li>
 * <li>例外の発生履歴</li>
 * </ol>
 * また、以下の条件をdiconファイルでのパラメータで指定可能。
 * <ol>
 * <li>httpPort:HTTPで公開する際のポート番号。使用にはMX4Jが必要。</li>
 * <li>intervalMax:メソッドの処理時間を何回分記録するか。
 *     デフォルト値は1000。</li>
 * <li>throwableMax:例外の発生を何回分記録するか。
 *     デフォルト値は1000。</li>
 * <li>recordThreshold:処理時間記録用の閾値。
 *     この時間を越えたメソッド呼び出しのみ記録する。
 *     デフォルト値は0。</li>
 * <li>alarmThreshold:処理時間記録用の閾値。
 *     この時間を越えたメソッド呼び出しの発生をViwerに通知する。
 *     デフォルト値は1000。</li>
 * <li>domain:MBeanを登録する際に使用するドメイン。
 *     実際のドメイン名は、[domainパラメータ] + [Mbeanの種類]となる。
 *     MBeanの種類は以下のものがある。
 *     <ol>
 *       <li>container:全コンポーネントのObjectNameを管理する。</li>
 *       <li>component:一つのコンポーネントに関する情報を公開するMBean。</li>
 *       <li>invocation:メソッド呼び出しに関する情報を公開するMBean。</li>
 *     </ol>
 *     </li>
 * </ol>
 * 
 * @version 0.1
 * @author Masanori Yamasaki(SMG)
 */
public class S2StatsJavelinInterceptor extends AbstractInterceptor
{
    private static final long  serialVersionUID = 6661781313519708185L;

    /** プラットフォームMBeanサーバ */
    private static MBeanServer server_;

    /** 初期化フラグ。初期化済みの場合はtrue。 */
    private static boolean     isInitialized_   = false;

    /**
     * 情報を公開するHTTPポート番号。（MX4Jが必要。）
     */
    private int                httpPort_        = 0;

    private S2StatsJavelinConfig  config_ = new S2StatsJavelinConfig();

    /**
     * 初期化処理。
     * MBeanServerへのContainerMBeanの登録を行う。
     * 公開用HTTPポートが指定されていた場合は、HttpAdaptorの生成と登録も行う。
     */
    private void init( )
    {
        try
        {
            server_ = ManagementFactory.getPlatformMBeanServer();

            if (httpPort_ != 0)
            {
            	try
            	{
                	Mx4JLauncher.execute(server_, httpPort_);
            	}
            	catch(Exception ex)
            	{
            		ex.printStackTrace();
            	}
            }

            ContainerMBean container;
            ObjectName containerName = new ObjectName(config_.getDomain()
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
            ObjectName statisticsName = new ObjectName(config_.getDomain()
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
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 呼び出し情報取得用のinvokeメソッド。
     * 
     * 実際のメソッド呼び出しを実行する前後で、
     * 実行回数や実行時間をMBeanに記録する。
     * 
     * 実行時に例外が発生した場合は、
     * 例外の発生回数や発生履歴も記録する。
     * 
     * @param invocation
     *            インターセプタによって取得された、呼び出すメソッドの情報
     * @return invocationを実行したときの戻り値
     * @throws Throwable invocationを実行したときに発生した例外
     */
    public Object invoke(MethodInvocation invocation)
        throws Throwable
    {
        synchronized (this.getClass())
        {
            if (!isInitialized_)
            {
                isInitialized_ = true;
                init();
            }
        }

        try
        {
            // 呼び出し先情報取得。
            String className = getTargetClass(invocation).getName();
            String methodName = invocation.getMethod().getName();

            S2StatsJavelinRecorder.preProcess(className, methodName, invocation.getArguments(), config_);
        }
        catch(Throwable th)
        {
        	th.printStackTrace();
        }
        
        Object ret = null;
        try
        {
            // メソッド呼び出し。
            ret = invocation.proceed();
        }
        catch (Throwable cause)
        {
            S2StatsJavelinRecorder.postProcess(cause);

            //例外をスローし、終了する。
            throw cause;
        }

        try
        {
            S2StatsJavelinRecorder.postProcess(config_, ret);
        }
        catch(Throwable th)
        {
        	th.printStackTrace();
        }
        
        return ret;
    }

    /**
     * 
     * @param intervalMax
     */
    public void setIntervalMax(int intervalMax)
    {
        config_.setIntervalMax(intervalMax);
    }

    /**
     * 
     * @param throwableMax
     */
    public void setThrowableMax(int throwableMax)
    {
        config_.setThrowableMax(throwableMax);
    }

    /**
     * 
     * @param recordThreshold
     */
    public void setRecordThreshold(int recordThreshold)
    {
    	config_.setRecordThreshold(recordThreshold);
    }

    /**
     * 
     * @param alarmThreshold
     */
    public void setAlarmThreshold(int alarmThreshold)
    {
    	config_.setAlarmThreshold(alarmThreshold);
    }

    /**
     * 
     * @param domain
     */
    public void setDomain(String domain)
    {
        config_.setDomain(domain);
    }

    public void setJavelinFileDir(String javelinFileDir)
    {
    	config_.setJavelinFileDir(javelinFileDir);
    }
    
    /**
     * 
     * @param httpPort
     */
    public void setHttpPort(int httpPort)
    {
        httpPort_ = httpPort;
    }
}