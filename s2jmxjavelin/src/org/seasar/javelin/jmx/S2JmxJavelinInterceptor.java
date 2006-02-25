package org.seasar.javelin.jmx;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServer;
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
 * Component�Ԃ̌Ăяo���֌W��MBean�Ƃ��Č��J���邽�߂�Interceptor�B
 * �ȉ��̏����AMBean�o�R�Ŏ擾���邱�Ƃ��\�B
 * <ol>
 * <li>���\�b�h�̌Ăяo����</li>
 * <li>���\�b�h�̕��Ϗ������ԁi�~���b�P�ʁj</li>
 * <li>���\�b�h�̍Œ��������ԁi�~���b�P�ʁj</li>
 * <li>���\�b�h�̍ŒZ�������ԁi�~���b�P�ʁj</li>
 * <li>���\�b�h�̌Ăяo����</li>
 * <li>��O�̔�����</li>
 * <li>��O�̔�������</li>
 * </ol>
 * �܂��A�ȉ��̏�����dicon�t�@�C���ł̃p�����[�^�Ŏw��\�B
 * <ol>
 * <li>httpPort:HTTP�Ō��J����ۂ̃|�[�g�ԍ��B�g�p�ɂ�MX4J���K�v�B</li>
 * <li>intervalMax:���\�b�h�̏������Ԃ����񕪋L�^���邩�B
 *     �f�t�H���g�l��1000�B</li>
 * <li>throwableMax:��O�̔��������񕪋L�^���邩�B
 *     �f�t�H���g�l��1000�B</li>
 * <li>recordThreshold:�������ԋL�^�p��臒l�B
 *     ���̎��Ԃ��z�������\�b�h�Ăяo���̂݋L�^����B
 *     �f�t�H���g�l��0�B</li>
 * <li>domain:MBean��o�^����ۂɎg�p����h���C���B
 *     ���ۂ̃h���C�����́A
 *     "org.seasar.javelin.jmx." + [domain�p�����[�^] + [Mbean�̎��]�ƂȂ�B
 *     MBean�̎�ނ͈ȉ��̂��̂�����B
 *     <ol>
 *       <li>Container:�S�R���|�[�l���g��ObjectName���Ǘ�����B</li>
 *       <li>Component:��̃R���|�[�l���g�Ɋւ���������J����MBean�B</li>
 *       <li>Invocation:���\�b�h�Ăяo���Ɋւ���������J����MBean�B</li>
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
    	ManagementFactory.getPlatformMBeanServer();
    
    private static final Map<String, Component> mBeanMap_ = 
    	new HashMap<String, Component>();
    
    private static boolean isInitialized_;
    
    private int intervalMax_  = 1000;
    
    private int throwableMax_ = 1000;
    
    private long recordThreshold_ = 0;
    
    private int httpPort_ = 0;
    
    private String domain_ = "default";
    
    /**
     * ���\�b�h�̌Ăяo�����I�u�W�F�N�g�B
     */
    private ThreadLocal<Invocation> caller_ = new ThreadLocal<Invocation>()
    {
        protected synchronized Invocation initialValue()
        {
            return null;
        }
    };

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
                	for (Object registered : beanSet)
                	{
                		processor = (XSLTProcessor)registered;
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
                	for (Object registered : beanSet)
                	{
                    	adaptor = (HttpAdaptor)registered;
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
            	for (Object registered : beanSet)
            	{
            		container = (ContainerMBean)registered;
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
     * Javelin���O�o�͗p��invoke���\�b�h�B
     * 
     * ���ۂ̃��\�b�h�Ăяo�������s����O��ŁA
     * �Ăяo���ƕԋp�̏ڍ׃��O���AJavelin�`���ŏo�͂���B
     * 
     * ���s���ɗ�O�����������ꍇ�́A���̏ڍׂ����O�o�͂���B
     * 
     * @param invocation
     *            �C���^�[�Z�v�^�ɂ���Ď擾���ꂽ�A�Ăяo�����\�b�h�̏��
     * @return invocation�����s�����Ƃ��̖߂�l
     * @throws Throwable invocation�����s�����Ƃ��ɔ���������O
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
    	
        // �Ăяo������擾�B
        String calleeClassName  = getTargetClass(invocation).getName();
        String calleeMethodName = invocation.getMethod().getName();
        
        Component  componentBean = mBeanMap_.get(calleeClassName);
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

        // �Ăяo�������擾�B
        Invocation caller = (Invocation) caller_.get();

        Object ret = null;
        try
        {
            // �Ăяo������A
        	// ���񃍃O�o�͎��̌Ăяo�����Ƃ��Ďg�p���邽�߂ɕۑ�����B
            caller_.set(invocationBean);
            
        	long start = System.currentTimeMillis();
        	
            // ���\�b�h�Ăяo���B
            ret = invocation.proceed();
            
            long spent = System.currentTimeMillis() - start;
            if (spent >= recordThreshold_)
            {
                invocationBean.addInterval(spent, caller);
            }
            
            //�Ăяo������������Ă����B
            caller_.set(null);
        }
        catch (Throwable cause)
        {
        	// ����������O���L�^���Ă����B
        	invocationBean.addThrowable(cause);
        	
            //��O���X���[���A�I������B
            throw cause;
        }
        
        return ret;
    }

	public void setIntervalMax(int intervalMax)
	{
		this.intervalMax_ = intervalMax;
	}

	public void setThrowableMax(int throwableMax)
	{
		this.throwableMax_ = throwableMax;
	}
	
	public void setRecordThreshold(int recordThreshold)
	{
		this.recordThreshold_ = recordThreshold;
	}

	public void setHttpPort(int httpPort)
	{
		httpPort_ = httpPort;
	}

	public void setDomain(String domain)
	{
		domain_ = "org.seasar.javelin.jmx." + domain;
	}
}