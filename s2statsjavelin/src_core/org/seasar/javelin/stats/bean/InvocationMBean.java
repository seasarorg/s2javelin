package org.seasar.javelin.stats.bean;

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
	List getThrowableList();
	ObjectName[] getAllCallerObjectName();

	long getRecordThreshold();
	void setRecordThreshold(long recordThreshold);
	
	long getAlarmThreshold();
	void setAlarmThreshold(long alarmThreshold);
	
	void reset();
}
