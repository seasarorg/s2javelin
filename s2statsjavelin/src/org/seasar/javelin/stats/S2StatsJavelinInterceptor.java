package org.seasar.javelin.stats;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;

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
 * <li>alarmThreshold:�������ԋL�^�p��臒l�B
 *     ���̎��Ԃ��z�������\�b�h�Ăяo���̔�����Viwer�ɒʒm����B
 *     �f�t�H���g�l��1000�B</li>
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
public class S2StatsJavelinInterceptor extends AbstractInterceptor
{
    private static final long  serialVersionUID = 6661781313519708185L;

    private S2StatsJavelinConfig  config_ = new S2StatsJavelinConfig();

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
        try
        {
            // �Ăяo������擾�B
            String className = getTargetClass(invocation).getName();
            String methodName = invocation.getMethod().getName();

            StackTraceElement[] stacktrace  = null;
            if (config_.isLogStacktrace())
            {
            	stacktrace = Thread.currentThread().getStackTrace();
            }
            
            JmxRecorder.preProcess(className, methodName, config_);
            S2StatsJavelinRecorder.preProcess(className, methodName, invocation.getArguments(), stacktrace, config_);
        }
        catch(Throwable th)
        {
        	th.printStackTrace();
        }
        
        Object ret = null;
        try
        {
            // ���\�b�h�Ăяo���B
            ret = invocation.proceed();
        }
        catch (Throwable cause)
        {
            JmxRecorder.postProcess(cause);
            S2StatsJavelinRecorder.postProcess(cause);

            //��O���X���[���A�I������B
            throw cause;
        }

        try
        {
            JmxRecorder.postProcess();
            S2StatsJavelinRecorder.postProcess(ret, config_);
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
        config_.setHttpPort(httpPort);
    }
    
    public void setLogMethodArgsAndReturnValue(boolean value)
    {
    	config_.setLogMethodArgsAndReturnValue(value);
    }
    
    public void setLogStacktrace(boolean value)
    {
    	config_.setLogStacktrace(value);
    }
    
	public void setEndCalleeName(String endCalleeName)
	{
		config_.setEndCalleeName(endCalleeName);
	}

	public void setEndCallerName(String endCallerName)
	{
		config_.setEndCallerName(endCallerName);
	}

	public void setThreadModel(int threadModel)
	{
		config_.setThreadModel(threadModel);
	}
}