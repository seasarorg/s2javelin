package org.seasar.javelin.bean;


public interface ComponentMBean
{
	String getClassName();
    Invocation[] getAllInvocation();
	
	void reset();
}
