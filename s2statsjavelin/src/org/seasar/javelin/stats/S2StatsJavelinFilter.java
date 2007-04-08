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

    private static final String PNAME_isLogMethodArgsAndReturnValue      = "isLogMethodArgsAndReturnValue";
    private static final String PNAME_isLogStacktrace      = "isLogStacktrace";
    
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
    	config_.setLogMethodArgsAndReturnValue(getInitParameter(config, PNAME_isLogMethodArgsAndReturnValue, false));
    	config_.setLogStacktrace(getInitParameter(config, PNAME_isLogStacktrace, false));
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

    /**
     * サーブレットフィルタの設定から、指定されたパラメータの値を取得する。
     * @param config サーブレットフィルタの設定。
     * @param pname 取得したいパラメータの名称。
     * @return パラメータの値。
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
            	args = new Object[4];
            	args[0] = httpRequest.getRemoteHost();
            	args[1] = httpRequest.getRemotePort();
            	args[2] = httpRequest.getMethod();
            	args[3] = httpRequest.getParameterMap();
            }
            
            S2StatsJavelinRecorder.preProcess(contextPath, servletPath, args, config_);
        }
        catch (Throwable th)
        {
        	th.printStackTrace();
        }
        
        try
        {
            //==================================================
            // メソッド呼び出し。
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
            S2StatsJavelinRecorder.postProcess(config_, returnValue);
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
