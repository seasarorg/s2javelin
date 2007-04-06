package org.seasar.javelin.jmx;

public class S2JmxJavelinConfig
{
    /**
     * ドメイン名。
     * デフォルト値は"org.seasar.javelin.jmx.default"。
     */
	private String domain_ = "org.seasar.javelin.jmx.default";

    /** 呼び出し情報を記録する最大件数。デフォルト値は1000。 */
	private int intervalMax_;

    /** 例外の発生履歴を記録する最大件数。デフォルト値は1000。 */
	private int throwableMax_;

    /** 
     *  呼び出し情報を記録する際の閾値。
     *  値（ミリ秒）を下回る処理時間の呼び出し情報は記録しない。
     *  デフォルト値は0。 
     */
	private long recordThreshold_;

	private long alarmThreshold_ = 1000;

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

	
}
