package org.seasar.javelin.bean;

import java.util.List;

public interface InvocationMBean {

	String getClassName();
	String getMethodName();
	
	long getCount();
    long getMinimum();
    long getMaximum();
    long getAverage();
    long getCpuMinimum();
    long getCpuMaximum();
    long getCpuAverage();
	long getThrowableCount();
	long getLastUpdatedTime();
	List<Throwable> getThrowableList();

	long getRecordThreshold();
	void setRecordThreshold(long recordThreshold);
	
	long getAlarmThreshold();
	void setAlarmThreshold(long alarmThreshold);
	
	void reset();
}
