package org.seasar.javelin.stats;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class S2StatsJavelinFilter implements Filter
{
    private static final String PNAME_DOMAIN           = "domain";

    private static final String PNAME_INTERVAL_MAX     = "intervalMax";

    private static final String PNAME_THROWABLE_MAX    = "throwableMax";

    private static final String PNAME_RECORD_THRESHOLD = "recordThreshold";

    private static final String PNAME_ALARM_THRESHOLD  = "fileThreshold";

    private static final String PNAME_JAVELIN_DIR      = "javelinFileDir";

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

	public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
        throws IOException,
            ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest)request;

        try
        {
            String contextPath = httpRequest.getContextPath();
            String servletPath = httpRequest.getServletPath();
            S2StatsJavelinRecorder.preProcess(contextPath, servletPath, config_);
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
            S2StatsJavelinRecorder.postProcess(config_);
        }
        catch(Throwable th)
        {
        	th.printStackTrace();
        }
    }

    public void destroy( )
    {
    	;
    }
}
