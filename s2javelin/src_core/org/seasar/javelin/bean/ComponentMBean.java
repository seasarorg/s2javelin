package org.seasar.javelin.bean;

import javax.management.ObjectName;

public interface ComponentMBean
{
	String getClassName();
    ObjectName[] getAllInvocationObjectName();
    Invocation[] getAllInvocation();
	
	void reset();
}
