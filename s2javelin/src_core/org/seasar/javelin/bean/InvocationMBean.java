package org.seasar.javelin.bean;

import java.util.List;

import javax.management.ObjectName;

public interface InvocationMBean {

	ObjectName getComponentObjectName();
	
	String getClassName();
	String getMethodName();
	
	long getCount();
	long getMinimum();
	long getMaximum();
	long getAverage();
	long getThrowableCount();
	List<Throwable> getThrowableList();
	ObjectName[] getAllCallerObjectName();

	long getRecordThreshold();
	void setRecordThreshold(long recordThreshold);
	
	long getAlarmThreshold();
	void setAlarmThreshold(long alarmThreshold);
	
	void reset();
}
