package org.seasar.javelin.stats.bean;

import javax.management.ObjectName;

public interface ContainerMBean
{
    ObjectName[] getAllComponentObjectName();
	
	void reset();
}
