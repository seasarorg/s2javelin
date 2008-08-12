package org.seasar.javelin.bean;

import org.seasar.javelin.MBeanManager;

public class Container implements ContainerMBean
{
	public synchronized void reset()
	{
    	Component[] components = MBeanManager.getAllComponents();
		for (int index = 0; index < components.length; index++)
		{
			components[index].reset();
		}
	}
}
