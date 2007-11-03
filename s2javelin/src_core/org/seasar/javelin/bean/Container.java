package org.seasar.javelin.bean;

import org.seasar.javelin.MBeanManager;

import javax.management.ObjectName;

public class Container implements ContainerMBean
{
    public synchronized ObjectName[] getAllComponentObjectName()
    {
    	Component[] components = MBeanManager.getAllComponents();
		ObjectName[] objNames    = new ObjectName[components.length];

		for (int index = 0; index < components.length; index++)
		{
			objNames[index] = components[index].getObjectName();
		}
		
		return objNames;
    }

	public synchronized void reset()
	{
    	Component[] components = MBeanManager.getAllComponents();
		for (int index = 0; index < components.length; index++)
		{
			components[index].reset();
		}
	}
}
