package org.seasar.javelin.stats;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class S2StatsJavelinFilter implements Filter
{
    private static final String PNAME_DOMAIN           = "domain";

    private static final String PNAME_INTERVAL_MAX     = "intervalMax";

    private static final String PNAME_THROWABLE_MAX    = "throwableMax";

    private static final String PNAME_RECORD_THRESHOLD = "recordThreshold";

    private static final String PNAME_ALARM_THRESHOLD  = "alarmThreshold";

    private static final String PNAME_JAVELIN_DIR      = "javelinFileDir";

    private static final String PNAME_IS_LOG_METHODARGS_AND_RETURNVALUE = "isLogMethodArgsAndReturnValue";

    private static final String PNAME_IS_LOG_STACKTRACE = "isLogStacktrace";
    
    private static final String PNAME_ROOT_CALLER_NAME = "rootCallerName";
    
    private static final String PNAME_END_CALLEE_NAME = "endCalleeName";
    
    private static final String PNAME_THREAD_MODEL = "threadModel";
    
    private S2StatsJavelinConfig  config_;

    public void init(FilterConfig config)
        throws ServletException
    {
    	config_ = new S2StatsJavelinConfig();
    	String domain = getInitParameter(config, PNAME_DOMAIN);
    	if (domain !=null && domain.trim().length() > 0)
    	{
        	config_.setDomain(domain);
    	}
    	
    	config_.setIntervalMax(getInitParameter(config, PNAME_INTERVAL_MAX, 1000));
    	config_.setThrowableMax(getInitParameter(config, PNAME_THROWABLE_MAX, 1000));
    	config_.setRecordThreshold(getInitParameter(config, PNAME_RECORD_THRESHOLD, 0));
    	config_.setAlarmThreshold(getInitParameter(config, PNAME_ALARM_THRESHOLD, 1000));
    	String javelinFileDir = getInitParameter(config, PNAME_JAVELIN_DIR);
    	if (javelinFileDir !=null && javelinFileDir.trim().length() > 0)
    	{
        	config_.setJavelinFileDir(javelinFileDir);
    	}
    	config_.setLogMethodArgsAndReturnValue(getInitParameter(config, PNAME_IS_LOG_METHODARGS_AND_RETURNVALUE, false));
    	config_.setLogStacktrace(getInitParameter(config, PNAME_IS_LOG_STACKTRACE, false));
    	config_.setEndCallerName(getInitParameter(config, PNAME_ROOT_CALLER_NAME));
    	config_.setEndCalleeName(getInitParameter(config, PNAME_END_CALLEE_NAME));
    	config_.setThreadModel(getInitParameter(config, PNAME_THREAD_MODEL, 1));
    }

    private int getInitParameter(FilterConfig config, String pname, int defaultValue)
	{
    	int value = 0;
		String configValue = config.getInitParameter(pname);
		if (configValue != null)
		{
			try
			{
				value = Integer.parseInt(configValue);
			}
			catch (NumberFormatException ex)
			{
				value = defaultValue;
			}
		}
		else
		{
			value = defaultValue;
		}

		return value;
	}

    private boolean getInitParameter(FilterConfig config, String pname, boolean defaultValue)
	{
    	boolean value = false;
		String configValue = config.getInitParameter(pname);
		if (configValue != null)
		{
			try
			{
				value = Boolean.valueOf(configValue);
			}
			catch (Exception ex)
			{
				value = defaultValue;
			}
		}
		else
		{
			value = defaultValue;
		}

		return value;
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
	 * 
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
            String contextPath = httpRequest.getContextPath();
            String servletPath = httpRequest.getServletPath();

            Object[] args = null;
            if (config_.isLogMethodArgsAndReturnValue())
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
            if (config_.isLogStacktrace())
            {
            	stacktrace = Thread.currentThread().getStackTrace();
            }
            
            S2StatsJavelinRecorder.preProcess(contextPath, servletPath, args, stacktrace, config_);
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
            S2StatsJavelinRecorder.postProcess(ex);
            throw ex;
        }
        catch (ServletException ex)
        {
            S2StatsJavelinRecorder.postProcess(ex);
            throw ex;
        }
        
        try
        {
        	Object returnValue = null;
        	if (config_.isLogMethodArgsAndReturnValue())
        	{
        		returnValue = createReturnValue(httpResponse);
        	}
            S2StatsJavelinRecorder.postProcess(returnValue, config_);
        }
        catch(Throwable th)
        {
        	th.printStackTrace();
        }
    }

    private Object createReturnValue(HttpServletResponse response)
	{
    	String returnValue;
    	returnValue = response.getContentType();
		return returnValue;
	}

	public void destroy( )
    {
    	;
    }
}
