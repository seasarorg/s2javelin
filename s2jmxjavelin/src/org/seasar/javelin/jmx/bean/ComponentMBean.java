package org.seasar.javelin.jmx.bean;

import javax.management.ObjectName;

public interface ComponentMBean
{
	public String getClassName();
    public ObjectName[] getAllInvocationObjectName();
    public Invocation[] getAllInvocation();
	
	public void reset();
}
