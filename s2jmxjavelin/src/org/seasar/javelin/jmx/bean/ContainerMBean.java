package org.seasar.javelin.jmx.bean;

import javax.management.ObjectName;

public interface ContainerMBean
{
    ObjectName[] getAllComponentObjectName();
	
	void reset();
}
