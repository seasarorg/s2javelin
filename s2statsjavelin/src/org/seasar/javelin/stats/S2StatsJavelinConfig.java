package org.seasar.javelin.stats;

public class S2StatsJavelinConfig
{
    /**
     * ドメイン名。
     * デフォルト値は"org.seasar.javelin.jmx.default"。
     */
	private String domain_ = "org.seasar.javelin.jmx.default";

    /** 呼び出し情報を記録する最大件数。デフォルト値は1000。 */
	private int intervalMax_ = 1000;

    /** 例外の発生履歴を記録する最大件数。デフォルト値は1000。 */
	private int throwableMax_ =1000;

    /** 
     *  呼び出し情報を記録する際の閾値。
     *  値（ミリ秒）を下回る処理時間の呼び出し情報は記録しない。
     *  デフォルト値は0。 
     */
	private long recordThreshold_;

	private long alarmThreshold_ = 1000;

	private boolean isLogStacktrace_ = false;

	private boolean isLogMethodArgsAndReturnValue_ = false;
	
	private String endCallerName_ = "unknown";
	
	private String endCalleeName_ = "unknown";
	
	private int threadModel_ = 1;
	
	public static final int TM_THREAD_ID    = 1;
	public static final int TM_THREAD_NAME  = 2;
	public static final int TM_CONTEXT_PATH = 3;
	
    /**
     * Javelinログファイルを出力するフォルダ名
     */
	private String javelinFileDir_ = System.getProperty("java.io.tmpdir");

	public long getAlarmThreshold()
	{
		return alarmThreshold_;
	}

	public void setAlarmThreshold(long alarmThreshold)
	{
		alarmThreshold_ = alarmThreshold;
	}

	public String getDomain()
	{
		return domain_;
	}

	public void setDomain(String domain)
	{
		domain_ = domain;
	}

	public int getIntervalMax()
	{
		return intervalMax_;
	}

	public void setIntervalMax(int intervalMax)
	{
		intervalMax_ = intervalMax;
	}

	public String getJavelinFileDir()
	{
		return javelinFileDir_;
	}

	public void setJavelinFileDir(String javelinFileDir)
	{
		javelinFileDir_ = javelinFileDir;
	}

	public long getRecordThreshold()
	{
		return recordThreshold_;
	}

	public void setRecordThreshold(long recordThreshold)
	{
		recordThreshold_ = recordThreshold;
	}

	public int getThrowableMax()
	{
		return throwableMax_;
	}

	public void setThrowableMax(int throwableMax)
	{
		throwableMax_ = throwableMax;
	}

	public boolean isLogMethodArgsAndReturnValue()
	{
		return isLogMethodArgsAndReturnValue_;
	}

	public void setLogMethodArgsAndReturnValue(boolean isLogMethodArgs)
	{
		isLogMethodArgsAndReturnValue_ = isLogMethodArgs;
	}

	public String getEndCalleeName()
	{
		return endCalleeName_;
	}

	public void setEndCalleeName(String endCalleeName)
	{
		endCalleeName_ = endCalleeName;
	}

	public String getRootCallerName()
	{
		return endCallerName_;
	}

	public void setEndCallerName(String endCallerName)
	{
		endCallerName_ = endCallerName;
	}

	public boolean isLogStacktrace()
	{
		return isLogStacktrace_;
	}

	public void setLogStacktrace(boolean isLogStacktrace)
	{
		isLogStacktrace_ = isLogStacktrace;
	}

	public int getThreadModel()
	{
		return threadModel_;
	}

	public void setThreadModel(int threadModel)
	{
		threadModel_ = threadModel;
	}
}
