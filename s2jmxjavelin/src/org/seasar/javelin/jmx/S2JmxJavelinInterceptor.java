package org.seasar.javelin.jmx;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import mx4j.tools.adaptor.http.HttpAdaptor;
import mx4j.tools.adaptor.http.XSLTProcessor;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.javelin.jmx.bean.Component;
import org.seasar.javelin.jmx.bean.Container;
import org.seasar.javelin.jmx.bean.ContainerMBean;
import org.seasar.javelin.jmx.bean.Invocation;

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
 * <li>domain:MBeanを登録する際に使用するドメイン。
 *     実際のドメイン名は、
 *     "org.seasar.javelin.jmx." + [domainパラメータ] + [Mbeanの種類]となる。
 *     MBeanの種類は以下のものがある。
 *     <ol>
 *       <li>Container:全コンポーネントのObjectNameを管理する。</li>
 *       <li>Component:一つのコンポーネントに関する情報を公開するMBean。</li>
 *       <li>Invocation:メソッド呼び出しに関する情報を公開するMBean。</li>
 *     </ol>
 *     </li>
 * </ol>
 * 
 * @version 0.1
 * @author Masanori Yamasaki(SMG)
 */
public class S2JmxJavelinInterceptor extends AbstractInterceptor
{
    private static final long serialVersionUID = 6661781313519708185L;

    private static final MBeanServer server_ = 
    	MBeanServerFactory.createMBeanServer();

    /** ComponentMBeanを登録したマップ。 */
    private static final Map mBeanMap_ = new HashMap();

    /** 初期化フラグ。初期化済みの場合はtrue。 */
    private static boolean isInitialized_ = false;
    
    /** 呼び出し情報を記録する最大件数。デフォルト値は1000。 */
    private int intervalMax_  = 1000;

    /** 例外の発生履歴を記録する最大件数。デフォルト値は1000。 */
    private int throwableMax_ = 1000;

    /** 
     *  呼び出し情報を記録する際の閾値。
     *  値（ミリ秒）を下回る処理時間の呼び出し情報は記録しない。
     *  デフォルト値は0。 
     */
    private long recordThreshold_ = 0;

    /**
     * 情報を公開するHTTPポート番号。（MX4Jが必要。）
     */
    private int httpPort_ = 0;
    
    private String domain_ = "default";
    
    /**
     * メソッドの呼び出し元オブジェクト。
     */
    private ThreadLocal caller_ = new ThreadLocal()
    {
        protected synchronized Object initialValue()
        {
            return null;
        }
    };

    /**
     * 初期化処理。
     * MBeanServerへのContainerMBeanの登録を行う。
     * 公開用HTTPポートが指定されていた場合は、HttpAdaptorの生成と登録も行う。
     */
    private void init()
    {
    	try
    	{
    		if (httpPort_ != 0)
    		{
        	    XSLTProcessor processor = new XSLTProcessor();
        	    ObjectName processorName = 
        	    	new ObjectName("Server:name=XSLTProcessor");
                if (server_.isRegistered(processorName))
                {
                	Set beanSet = server_.queryMBeans(processorName, null);
                	if (beanSet.size() > 0)
                	{
                		XSLTProcessor[] processors =
                			(XSLTProcessor[])beanSet.toArray(
                			    new XSLTProcessor[beanSet.size()]);
                		processor = (XSLTProcessor)(processors[0]);
                	}
                }
                else
                {
            	    server_.registerMBean(processor, processorName);
                }
        	    
                HttpAdaptor adaptor;
        		ObjectName adaptorName = 
        			new ObjectName("Adaptor:name=adaptor,port=10000");
                if (server_.isRegistered(adaptorName))
                {
                	Set beanSet = server_.queryMBeans(adaptorName, null);
                	if (beanSet.size() > 0)
                	{
                		HttpAdaptor[] adaptors =
                			(HttpAdaptor[])beanSet.toArray(
                			    new HttpAdaptor[beanSet.size()]);
                		adaptor = (HttpAdaptor)(adaptors[0]);
                	}
                }
                else
                {
                    adaptor = new HttpAdaptor();
                    adaptor.setProcessor(processor);
                    adaptor.setPort(httpPort_);
                    server_.registerMBean(adaptor, adaptorName);
                    adaptor.start();
                }
                
    		}
    		
            ContainerMBean container;
    		ObjectName containerName = new ObjectName(
    				domain_ 
    				+ ".container:type=org.seasar.javelin.jmx.bean.ContainerMBean");        
            if (server_.isRegistered(containerName))
            {
            	Set beanSet = server_.queryMBeans(containerName, null);
            	if (beanSet.size() > 0)
            	{
            		ContainerMBean[] containers =
            			(ContainerMBean[])beanSet.toArray(
            			    new ContainerMBean[beanSet.size()]);
            		container = (ContainerMBean)(containers[0]);
            	}
            }
            else
            {
                container = new Container(mBeanMap_);
                server_.registerMBean(container, containerName);
            }
        }
    	catch(Exception ex)
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
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
    	synchronized (this.getClass())
		{
    		if (!isInitialized_)
    		{
    			isInitialized_ = true;
    			init();
    		}
		}
    	
        // 呼び出し先情報取得。
        String calleeClassName  = getTargetClass(invocation).getName();
        String calleeMethodName = invocation.getMethod().getName();
        
        Component  componentBean = (Component)(mBeanMap_.get(calleeClassName));
    	String name = 
    		domain_ 
    		+ ".component:type=org.seasar.javelin.jmx.bean.ComponentMBean" 
    		+ ",class="
    		+ calleeClassName;
        ObjectName componentName = new ObjectName(name);
        if (componentBean == null)
        {
        	componentBean = new Component(componentName, calleeClassName);

        	server_.registerMBean(componentBean, componentName);
        	mBeanMap_.put(calleeClassName, componentBean);
        }
        
        Invocation invocationBean = 
        	componentBean.getInvocation(calleeMethodName);
    	name = 
    		domain_ 
    		+ ".invocation:type=org.seasar.javelin.jmx.bean.InvocationMBean" 
    		+ ",class="
    		+ calleeClassName 
    		+ ",method="
    		+ calleeMethodName;
		ObjectName objName = new ObjectName(name);
        if (invocationBean == null)
        {
            
        	invocationBean = 
        		new Invocation(
        				objName
        				, componentName
        				, calleeClassName
        				, calleeMethodName
        				, intervalMax_
        				, throwableMax_);
        	
        	componentBean.addInvocation(invocationBean);
    		server_.registerMBean(invocationBean, objName);
        }

        // 呼び出し元情報取得。
        Invocation caller = (Invocation) caller_.get();

        Object ret = null;
        try
        {
            // 呼び出し先を、
        	// 次回ログ出力時の呼び出し元として使用するために保存する。
            caller_.set(invocationBean);
            
        	long start = System.currentTimeMillis();
        	
            // メソッド呼び出し。
            ret = invocation.proceed();
            
            long spent = System.currentTimeMillis() - start;
            if (spent >= recordThreshold_)
            {
                invocationBean.addInterval(spent, caller);
            }
        }
        catch (Throwable cause)
        {
        	// 発生した例外を記録しておく。
        	invocationBean.addThrowable(cause);
        	
            //例外をスローし、終了する。
            throw cause;
        }
        finally
        {
            //呼び出し先を消去しておく。
            caller_.set(null);
        }
        
        return ret;
    }

    /**
     * 
     * @param intervalMax
     */
	public void setIntervalMax(int intervalMax)
	{
		this.intervalMax_ = intervalMax;
	}

	/**
	 * 
	 * @param throwableMax
	 */
	public void setThrowableMax(int throwableMax)
	{
		this.throwableMax_ = throwableMax;
	}

	/**
	 * 
	 * @param recordThreshold
	 */
	public void setRecordThreshold(int recordThreshold)
	{
		this.recordThreshold_ = recordThreshold;
	}

	/**
	 * 
	 * @param httpPort
	 */
	public void setHttpPort(int httpPort)
	{
		httpPort_ = httpPort;
	}

	/**
	 * 
	 * @param domain
	 */
	public void setDomain(String domain)
	{
		domain_ = "org.seasar.javelin.jmx." + domain;
	}
}