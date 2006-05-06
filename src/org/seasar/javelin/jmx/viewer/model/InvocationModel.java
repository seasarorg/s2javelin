package org.seasar.javelin.jmx.viewer.model;

import java.util.List;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationListener;

public class InvocationModel implements Comparable, NotificationListener
{
	private ComponentModel component_;
	private String methodName_;
	
	/** ���\�b�h�̌Ăяo���񐔁B */
	private long count_;
	
	/** ���\�b�h�̍ŒZ�������ԁi�P��:�~���b�j�B */
	private long minimum_;
	
	/** ���\�b�h�̍Œ��������ԁi�P��:�~���b�j�B */
	private long maximum_;
	
	/** ���\�b�h�̕��Ϗ������ԁi�P��:�~���b�j�B */
	private long average_;
	
	/** ���\�b�h���ł̗�O�����񐔁B */
	private long throwableCount_;
	
	/** ���\�b�h���Ŕ���������O�̗����B */
	private List<Throwable> throwableList_;

	/**  */
	private long warningThreshold_ = Long.MAX_VALUE;
	
	/**  */
	private long alarmThreshold_   = Long.MAX_VALUE;

    /** 臒l����p�̒萔������ */
    private static final String EXCEED_THRESHOLD_ALARM = "Alarm:EXCEED_THRESHOLD";
    
	public long getAverage()
	{
		return average_;
	}

	public void setAverage(long average)
	{
		average_ = average;
	}

	public long getCount()
	{
		return count_;
	}

	public void setCount(long count)
	{
		count_ = count;
	}

	public long getMaximum()
	{
		return maximum_;
	}

	public void setMaximum(long maximum)
	{
		maximum_ = maximum;
	}

	public long getMinimum()
	{
		return minimum_;
	}

	public void setMinimum(long minimum)
	{
		minimum_ = minimum;
	}

	public long getThrowableCount()
	{
		return throwableCount_;
	}

	public void setThrowableCount(long throwableCount)
	{
		throwableCount_ = throwableCount;
	}

	public List<Throwable> getThrowableList()
	{
		return throwableList_;
	}

	public void setThrowableList(List<Throwable> throwableList)
	{
		throwableList_ = throwableList;
	}

	public ComponentModel getComponent()
	{
		return component_;
	}
	
	public void setComponent(ComponentModel component)
	{
		component_ = component;
	}

	public String getMethodName()
	{
		return methodName_;
	}

	public void setMethodName(String methodName)
	{
		methodName_ = methodName;
	}

	public long getWarningThreshold()
	{
		return warningThreshold_;
	}

	public void setWarningThreshold(long warningThreshold)
	{
		warningThreshold_ = warningThreshold;
	}

	public long getAlarmThreshold()
	{
		return alarmThreshold_;
	}

	public void setAlarmThreshold(long alarmThreshold)
	{
		alarmThreshold_ = alarmThreshold;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("�Ăяo����=");
		builder.append(getCount());
		builder.append(", ���Ϗ�������=");
		builder.append(getAverage());
		builder.append(", �ő又������=");
		builder.append(getMaximum());
		builder.append(", �ŏ���������=");
		builder.append(getMinimum());
		builder.append(", ��O������=");
		builder.append(getThrowableCount());
		
		return builder.toString();
	}

	public int compareTo(Object arg0)
	{
		if (arg0 instanceof InvocationModel)
		{
			InvocationModel target = (InvocationModel)arg0;
			return this.getMethodName().compareTo(target.getMethodName());
		}
		
		return 0;
	}

    public void handleNotification(Notification notification, Object handback)
    {
        if( notification instanceof AttributeChangeNotification )
        {
            String alarmMsg = ((AttributeChangeNotification)notification).getMessage();
            if( EXCEED_THRESHOLD_ALARM.equals( alarmMsg ) == true )
            {
                handleExceedThresholdAlarm( (AttributeChangeNotification)notification );
            }
        }
        
    }
    
    private void handleExceedThresholdAlarm(AttributeChangeNotification notification)
    {
    	setAverage((Long)notification.getOldValue());
    	setMaximum((Long)notification.getNewValue());
        this.component_.setExceededThresholdAlarm( this.methodName_ );
    }
}
