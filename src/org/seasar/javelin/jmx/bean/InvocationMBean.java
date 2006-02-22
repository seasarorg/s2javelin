package org.seasar.javelin.jmx.bean;

import java.util.List;

import javax.management.ObjectName;

public interface InvocationMBean {

	public ObjectName getComponentObjectName();
	
	public String getClassName();
	public String getMethodName();
	
	public long getCount();
	public long getMinimum();
	public long getMaximum();
	public long getAverage();
	public long getThrowableCount();
	public List<Throwable> getThrowableList();
	public ObjectName[] getAllCallerObjectName();
}
