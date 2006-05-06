package org.seasar.javelin.jmx;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import mx4j.tools.adaptor.http.HttpAdaptor;
import mx4j.tools.adaptor.http.XSLTProcessor;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.javelin.jmx.bean.Container;
import org.seasar.javelin.jmx.bean.ContainerMBean;
import org.seasar.javelin.jmx.bean.Statistics;
import org.seasar.javelin.jmx.bean.StatisticsMBean;

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
 *     ���ۂ̃h���C�����́A[domain�p�����[�^] + [Mbean�̎��]�ƂȂ�B
 *     MBean�̎�ނ͈ȉ��̂��̂�����B
 *     <ol>
 *       <li>container:�S�R���|�[�l���g��ObjectName���Ǘ�����B</li>
 *       <li>component:��̃R���|�[�l���g�Ɋւ���������J����MBean�B</li>
 *       <li>invocation:���\�b�h�Ăяo���Ɋւ���������J����MBean�B</li>
 *     </ol>
 *     </li>
 * </ol>
 * 
 * @version 0.1
 * @author Masanori Yamasaki(SMG)
 */
public class S2JmxJavelinInterceptor extends AbstractInterceptor
{
    private static final long  serialVersionUID = 6661781313519708185L;

    /** �v���b�g�t�H�[��MBean�T�[�o */
    private static MBeanServer server_;

    /** �������t���O�B�������ς݂̏ꍇ��true�B */
    private static boolean     isInitialized_   = false;

    /**
     * �������J����HTTP�|�[�g�ԍ��B�iMX4J���K�v�B�j
     */
    private int                httpPort_        = 0;

    private S2JmxJavelinConfig  config_ = new S2JmxJavelinConfig();

    /**
     * �����������B
     * MBeanServer�ւ�ContainerMBean�̓o�^���s���B
     * ���J�pHTTP�|�[�g���w�肳��Ă����ꍇ�́AHttpAdaptor�̐����Ɠo�^���s���B
     */
    private void init( )
    {
        try
        {
            server_ = ManagementFactory.getPlatformMBeanServer();

            if (httpPort_ != 0)
            {
                XSLTProcessor processor = new XSLTProcessor();
                ObjectName processorName = new ObjectName(
                                                          "Server:name=XSLTProcessor");
                if (server_.isRegistered(processorName))
                {
                    Set beanSet = server_.queryMBeans(processorName, null);
                    if (beanSet.size() > 0)
                    {
                        XSLTProcessor[] processors = (XSLTProcessor[])beanSet.toArray(new XSLTProcessor[beanSet.size()]);
                        processor = (XSLTProcessor)(processors[0]);
                    }
                }
                else
                {
                    server_.registerMBean(processor, processorName);
                }

                HttpAdaptor adaptor;
                ObjectName adaptorName = new ObjectName(
                                                        "Adaptor:name=adaptor,port="
                                                                + httpPort_);
                if (server_.isRegistered(adaptorName))
                {
                    Set beanSet = server_.queryMBeans(adaptorName, null);
                    if (beanSet.size() > 0)
                    {
                        HttpAdaptor[] adaptors = (HttpAdaptor[])beanSet.toArray(new HttpAdaptor[beanSet.size()]);
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
     * �Ăяo�����擾�p��invoke���\�b�h�B
     * 
     * ���ۂ̃��\�b�h�Ăяo�������s����O��ŁA
     * ���s�񐔂���s���Ԃ�MBean�ɋL�^����B
     * 
     * ���s���ɗ�O�����������ꍇ�́A
     * ��O�̔����񐔂┭���������L�^����B
     * 
     * @param invocation
     *            �C���^�[�Z�v�^�ɂ���Ď擾���ꂽ�A�Ăяo�����\�b�h�̏��
     * @return invocation�����s�����Ƃ��̖߂�l
     * @throws Throwable invocation�����s�����Ƃ��ɔ���������O
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

        // �Ăяo������擾�B
        String className = getTargetClass(invocation).getName();
        String methodName = invocation.getMethod().getName();

        Object ret = null;
        try
        {
            S2JmxJavelinRecorder.preProcess(className, methodName, config_);

            //==================================================
            // ���\�b�h�Ăяo���B
            ret = invocation.proceed();
            //==================================================

            S2JmxJavelinRecorder.postProcess(config_);
        }
        catch (Throwable cause)
        {
            S2JmxJavelinRecorder.postProcess(cause);

            //��O���X���[���A�I������B
            throw cause;
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