package org.seasar.javelin.bean;

import javax.management.ObjectName;

public interface ContainerMBean
{
    ObjectName[] getAllComponentObjectName();
	
	void reset();
}
