package org.seasar.javelin.jmx.bean;

import javax.management.ObjectName;

public interface ContainerMBean
{
    public ObjectName[] getAllComponentObjectName();
	
	public void reset();
}
