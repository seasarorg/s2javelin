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
	private static final String PNAME_DOMAIN = "domain";
	private static final String PNAME_INTERVAL_MAX = "intervalMax";
	private static final String PNAME_THROWABLE_MAX = "throwableMax";
	private static final String PNAME_RECORD_THRESHOLD = "recordThreshold";
	
	private String domain_;
	private int intervalMax_;
	private int throwableMax_;
	private long recordThreshold_;
	
	public void init(FilterConfig config) throws ServletException
	{
		domain_ = config.getInitParameter(PNAME_DOMAIN);
		intervalMax_ = 
			Integer.parseInt(config.getInitParameter(PNAME_INTERVAL_MAX));
		throwableMax_ = 
			Integer.parseInt(config.getInitParameter(PNAME_THROWABLE_MAX));
		recordThreshold_ = 
			Long.parseLong(config.getInitParameter(PNAME_RECORD_THRESHOLD));
	}

	public void doFilter(
			ServletRequest request
			, ServletResponse response
			,FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest httpRequest =
			(HttpServletRequest)request;
		
		String contextPath = httpRequest.getContextPath();
		String servletPath = httpRequest.getServletPath();

    	try
    	{
        	S2JmxJavelinRecorder.preProcess(
        			domain_
        			, contextPath
        			, servletPath
        			, intervalMax_
        			, throwableMax_
        			, recordThreshold_);
        	
        	//==================================================
            // メソッド呼び出し。
        	chain.doFilter(request, response);
        	//==================================================
        	
            S2JmxJavelinRecorder.postProcess();
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

	public void destroy()
	{
	}
}
