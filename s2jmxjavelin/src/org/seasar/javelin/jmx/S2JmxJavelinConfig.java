package org.seasar.javelin.jmx;

public class S2JmxJavelinConfig
{
    /**
     * �h���C�����B
     * �f�t�H���g�l��"org.seasar.javelin.jmx.default"�B
     */
	private String domain_ = "org.seasar.javelin.jmx.default";

    /** �Ăяo�������L�^����ő匏���B�f�t�H���g�l��1000�B */
	private int intervalMax_;

    /** ��O�̔����������L�^����ő匏���B�f�t�H���g�l��1000�B */
	private int throwableMax_;

    /** 
     *  �Ăяo�������L�^����ۂ�臒l�B
     *  �l�i�~���b�j������鏈�����Ԃ̌Ăяo�����͋L�^���Ȃ��B
     *  �f�t�H���g�l��0�B 
     */
	private long recordThreshold_;

	private long alarmThreshold_ = 1000;

    /**
     * Javelin���O�t�@�C�����o�͂���t�H���_��
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
