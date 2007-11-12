package org.seasar.javelin;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class S2JavelinFilter implements Filter
{

    private static final String PNAME_DOMAIN           = "domain";

    private static final String PNAME_INTERVAL_MAX     = "intervalMax";

    private static final String PNAME_THROWABLE_MAX    = "throwableMax";

    private static final String PNAME_RECORD_THRESHOLD = "recordThreshold";

    private static final String PNAME_ALARM_THRESHOLD  = "alarmThreshold";

    private static final String PNAME_JAVELIN_DIR      = "javelinFileDir";

    private static final String PNAME_IS_LOG_ARGS = "log.args";

    private static final String PNAME_IS_LOG_RETURN = "log.return";

    private static final String PNAME_IS_LOG_STACKTRACE = "log.stacktrace";
    
    private static final String PNAME_ROOT_CALLER_NAME = "rootCallerName";
    
    private static final String PNAME_END_CALLEE_NAME = "endCalleeName";
    
    private static final String PNAME_THREAD_MODEL = "threadModel";
    
    private static final String PNAME_HTTP_PORT = "httpPort";
    
    private S2JavelinConfig config_;

    /**
     * �������B
     *
     * @param config web.xml�̐ݒ�
     * @throws ServletException
     */
    public void init(FilterConfig config)
    {
    	this.config_ = new S2JavelinConfig();

        String domain = getInitParameter(config, PNAME_DOMAIN);
    	if (this.config_.isSetDomain() == false
                && domain != null && domain.trim().length() > 0)
    	{
            this.config_.setDomain(domain);
    	}

        // web.xml�����ݒ�t�@�C���i*.conf�j�̕���D�悷��
        if (this.config_.isSetAlarmThreshold() == false
                && isParameterIntegerValue(config, PNAME_INTERVAL_MAX))
        {
            this.config_.setIntervalMax(getInitParameterInteger(config, PNAME_INTERVAL_MAX));
        }
        if (this.config_.isSetThrowableMax() == false
                && isParameterIntegerValue(config, PNAME_THROWABLE_MAX))
        {
            this.config_.setThrowableMax(getInitParameterInteger(config, PNAME_THROWABLE_MAX));
        }
        if (this.config_.isSetRecordThreshold() == false
                && isParameterIntegerValue(config, PNAME_RECORD_THRESHOLD))
        {
            this.config_.setRecordThreshold(getInitParameterInteger(config, PNAME_RECORD_THRESHOLD));
        }
        if (this.config_.isSetAlarmThreshold() == false
                && isParameterIntegerValue(config, PNAME_ALARM_THRESHOLD))
        {
            this.config_.setAlarmThreshold(getInitParameterInteger(config, PNAME_ALARM_THRESHOLD));
        }
        if (this.config_.isSetJavelinFileDir() == false
                && isParameterStringValue(config, PNAME_JAVELIN_DIR))
        {
        	String javelinFileDir = getInitParameter(config, PNAME_JAVELIN_DIR);
        	if (javelinFileDir.trim().length() > 0)
        	{
                this.config_.setJavelinFileDir(javelinFileDir);
        	}
        }
        if (this.config_.isSetLogArgs() == false
                && isParameterBooleanValue(config, PNAME_IS_LOG_ARGS))
        {
            this.config_.setLogArgs(getInitParameterBoolean(config, PNAME_IS_LOG_ARGS));
        }
        if (this.config_.isSetLogReturn() == false
                && isParameterBooleanValue(config, PNAME_IS_LOG_RETURN))
        {
            this.config_.setLogReturn(getInitParameterBoolean(config, PNAME_IS_LOG_RETURN));
        }
        if (this.config_.isSetLogStacktrace() == false
                && isParameterBooleanValue(config, PNAME_IS_LOG_STACKTRACE))
        {
            this.config_.setLogStacktrace(getInitParameterBoolean(config, PNAME_IS_LOG_STACKTRACE));
        }
        if (this.config_.isSetRootCallerName() == false
                && isParameterBooleanValue(config, PNAME_ROOT_CALLER_NAME))
        {
            this.config_.setRootCallerName(getInitParameter(config, PNAME_ROOT_CALLER_NAME));
        }
        if (this.config_.isSetEndCalleeName() == false
                && isParameterBooleanValue(config, PNAME_END_CALLEE_NAME))
        {
            this.config_.setEndCalleeName(getInitParameter(config, PNAME_END_CALLEE_NAME));
        }
        if (this.config_.isSetThreadModel() == false
                && isParameterIntegerValue(config, PNAME_THREAD_MODEL))
        {
            this.config_.setThreadModel(getInitParameterInteger(config, PNAME_THREAD_MODEL));
        }
        if (this.config_.isSetHttpPort() == false
                && isParameterIntegerValue(config, PNAME_HTTP_PORT))
        {
            this.config_.setHttpPort(getInitParameterInteger(config, PNAME_HTTP_PORT));
        }

        printConfigValue();
    }


    /**
     * �w�肳�ꂽ�I�v�V�����̒l�������񂩂ǂ����𒲂ׂ�B
     *
     * @param config �t�B���^�ݒ�
     * @param name �I�v�V������
     * @return �l��������Ȃ�true
     */
    private boolean isParameterStringValue(FilterConfig config, String name)
    {
        String configValue = config.getInitParameter(name);
        return (configValue != null);
    }


    /**
     * �w�肳�ꂽ�I�v�V�����̒l�����l���ǂ����𒲂ׂ�B
     *
     * @param config �t�B���^�ݒ�
     * @param name �I�v�V������
     * @return �l�����l�Ȃ�true
     */
    private boolean isParameterIntegerValue(FilterConfig config, String name)
    {
        String configValue = config.getInitParameter(name);
        if (configValue == null)
        {
            return false;
        }
        try
        {
            Integer.parseInt(configValue);
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
        return true;
    }


    /**
     * �w�肳�ꂽ�I�v�V�����̒l���u�[���l���ǂ����𒲂ׂ�B
     *
     * @param config �t�B���^�ݒ�
     * @param name �I�v�V������
     * @return �l���u�[���l�Ȃ�true
     */
    private boolean isParameterBooleanValue(FilterConfig config, String name)
    {
        String configValue = config.getInitParameter(name);
        if (configValue != null)
        {
            return true;
        }
        return false;
    }


    private int getInitParameterInteger(FilterConfig config, String pname)
	{
		String configValue = config.getInitParameter(pname);
		return Integer.parseInt(configValue);
	}

    private boolean getInitParameterBoolean(FilterConfig config, String pname)
	{
		String configValue = config.getInitParameter(pname);
		return Boolean.valueOf(configValue);
	}

    /**
     * �T�[�u���b�g�t�B���^�̐ݒ肩��A�w�肳�ꂽ�p�����[�^�̒l���擾����B
     * @param config �T�[�u���b�g�t�B���^�̐ݒ�B
     * @param pname �擾�������p�����[�^�̖��́B
     * @return �p�����[�^�̒l�B
     */
	private String getInitParameter(FilterConfig config, String pname)
	{
		String value = config.getInitParameter(pname);
		return value;
	}

	/**
	 * �t�B���^�����O�����B
	 * �T�[�u���b�g�̏������Ԃ��v�����A�e�폈�����s���B
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
        throws IOException,
            ServletException
    {
        HttpServletRequest  httpRequest  = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;

        try
        {
        	// ThreadLocal����擾����CallTree�ɑ΂��ČĂяo���\����ۑ�����B
            CallTree callTree = S2JavelinCommonPool.getCallTree();
            S2StatsJavelinRecorder.initCallTree(callTree);

            /**
        	 * ThreadLocal����N���X���A���\�b�h�����Ăяo���B
        	 * �N���X���A���\�b�h�������݂��Ȃ��ꍇ�A
        	 * Http Request�̃R���e�L�X�g�p�X�A�T�[�u���b�g�p�X���Ăяo���B
        	 */
            String contextPath = S2JavelinCommonPool.getClassName();
            String servletPath = S2JavelinCommonPool.getMethodName();
            if (contextPath == null || servletPath == null)
            {	
                servletPath = httpRequest.getPathInfo();
                if (servletPath == null)
                {
                    contextPath = httpRequest.getContextPath();
                    servletPath = httpRequest.getServletPath();
                }
                else
                {
                    contextPath = httpRequest.getContextPath() + httpRequest.getServletPath();
                }
            
                contextPath = httpRequest.getContextPath();
                servletPath = httpRequest.getServletPath();
        	}

            Object[] args = null;
            if (this.config_.isLogArgs())
            {
            	args = new Object[6];
            	args[0] = httpRequest.getRemoteHost();
            	args[1] = httpRequest.getRemotePort();
            	args[2] = contextPath;
            	args[3] = servletPath;
            	args[4] = httpRequest.getMethod();
            	args[5] = httpRequest.getParameterMap();
            }
            
            StackTraceElement[] stacktrace = null;
            if (this.config_.isLogStacktrace())
            {
            	stacktrace = Thread.currentThread().getStackTrace();
            }
            
            JmxRecorder.preProcess(contextPath, servletPath, this.config_);
            S2StatsJavelinRecorder.preProcess(contextPath, servletPath, args, stacktrace, this.config_);
            
            S2StatsJavelinRecorder.preProcessField(contextPath, servletPath, config_);
        }
        catch (Throwable th)
        {
        	th.printStackTrace();
        }
        
        try
        {
            //==================================================
            // ���\�b�h�Ăяo���B
            chain.doFilter(request, response);
            //==================================================
        }
        catch (IOException ex)
        {
            JmxRecorder.postProcess(ex);
            S2StatsJavelinRecorder.postProcessField(config_);
            S2StatsJavelinRecorder.postProcess(ex, this.config_);
            throw ex;
        }
        catch (ServletException ex)
        {
            JmxRecorder.postProcess(ex);
            S2StatsJavelinRecorder.postProcessField(config_);
            S2StatsJavelinRecorder.postProcess(ex, this.config_);
            throw ex;
        }
        catch (RuntimeException ex)
        {
            JmxRecorder.postProcess(ex);
            S2StatsJavelinRecorder.postProcessField(config_);
            S2StatsJavelinRecorder.postProcess(ex, this.config_);
            throw ex;
        }
        catch (Error error)
        {
            JmxRecorder.postProcess(error);
            S2StatsJavelinRecorder.postProcessField(config_);
            S2StatsJavelinRecorder.postProcess(error, this.config_);
            throw error;
        }
        
        
        try
        {
            Object returnValue = null;
            if (this.config_.isLogReturn())
            {
                returnValue = createReturnValue(httpResponse);
            }
            

            JmxRecorder.postProcess();
            S2StatsJavelinRecorder.postProcessField(config_);
            S2StatsJavelinRecorder.postProcess(returnValue, this.config_);
        }
        catch(Throwable th)
        {
            th.printStackTrace();
        }
    }

	/**
	 * HttpServletResponse����A�߂�l�𐶐�����B
	 * 
	 * @param response �T�[�u���b�g���X�|���X�B
	 * @return �߂�l�B
	 */
    private Object createReturnValue(HttpServletResponse response)
	{
    	String returnValue;
    	returnValue = response.getContentType();
		return returnValue;
	}

	public void destroy( )
    {
    	// �������Ȃ�
    }


    /**
     * �ݒ�l��W���o�͂ɏo�͂���B
     */
    private void printConfigValue()
    {
        PrintStream out = System.out;
        out.println(">>>> Properties related with S2JavelinFilter");
        out.println("\tjavelin.intervalMax             : " + this.config_.getIntervalMax());
        out.println("\tjavelin.throwableMax            : " + this.config_.getThrowableMax());
        out.println("\tjavelin.recordThreshold         : " + this.config_.getRecordThreshold());
        out.println("\tjavelin.alarmThreshold          : " + this.config_.getAlarmThreshold());
        out.println("\tjavelin.javelinFileDir          : " + this.config_.getJavelinFileDir());
        out.println("\tjavelin.domain                  : " + this.config_.getDomain());
        out.println("\tjavelin.log.stacktrace          : " + this.config_.isLogStacktrace());
        out.println("\tjavelin.log.args                : " + this.config_.isLogArgs());
        out.println("\tjavelin.log.return              : " + this.config_.isLogReturn());
        out.println("\tjavelin.log.args.detail         : " + this.config_.isArgsDetail());
        out.println("\tjavelin.log.args.detail.depth   : " + this.config_.getArgsDetailDepth());
        out.println("\tjavelin.log.return.detail       : " + this.config_.isReturnDetail());
        out.println("\tjavelin.log.return.detail.depth : " + this.config_.getReturnDetailDepth());
        out.println("\tjavelin.rootCallerName          : " + this.config_.getRootCallerName());
        out.println("\tjavelin.endCalleeName           : " + this.config_.getEndCalleeName());
        out.println("\tjavelin.threadModel             : " + this.config_.getThreadModel());
        out.println("\tjavelin.httpPort                : " + this.config_.getHttpPort());
        out.println("<<<<");
    }

}
