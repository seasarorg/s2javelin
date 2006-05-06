package org.seasar.javelin.jmx;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class S2JmxJavelinFilter implements Filter
{
    private static final String PNAME_DOMAIN           = "domain";

    private static final String PNAME_INTERVAL_MAX     = "intervalMax";

    private static final String PNAME_THROWABLE_MAX    = "throwableMax";

    private static final String PNAME_RECORD_THRESHOLD = "recordThreshold";

    private static final String PNAME_ALARM_THRESHOLD  = "fileThreshold";

    private static final String PNAME_JAVELIN_DIR      = "javelinFileDir";

    private S2JmxJavelinConfig  config_;

    public void init(FilterConfig config)
        throws ServletException
    {
    	config_ = new S2JmxJavelinConfig();
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

	private String getInitParameter(FilterConfig config, String pname)
	{
		String value = config.getInitParameter(pname);
		return value;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
        throws IOException,
            ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest)request;

        String contextPath = httpRequest.getContextPath();
        String servletPath = httpRequest.getServletPath();

        try
        {
            S2JmxJavelinRecorder.preProcess(contextPath, servletPath, config_);

            //==================================================
            // メソッド呼び出し。
            chain.doFilter(request, response);
            //==================================================

            S2JmxJavelinRecorder.postProcess(config_);
        }
        catch (IOException ex)
        {
            S2JmxJavelinRecorder.postProcess(ex);
            throw ex;
        }
        catch (ServletException ex)
        {
            S2JmxJavelinRecorder.postProcess(ex);
            throw ex;
        }
    }

    public void destroy( )
    {
    	;
    }
}
