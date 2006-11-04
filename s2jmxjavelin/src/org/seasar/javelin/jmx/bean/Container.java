package org.seasar.javelin.jmx.bean;

import javax.management.ObjectName;

import org.seasar.javelin.jmx.MBeanManager;

public class Container implements ContainerMBean
{
    public ObjectName[] getAllComponentObjectName()
    {
    	Component[] components = MBeanManager.getAllComponents();
		ObjectName[] objNames    = new ObjectName[components.length];

		for (int index = 0; index < components.length; index++)
		{
			objNames[index] = components[index].getObjectName();
		}
		
		return objNames;
    }

	public void reset()
	{
    	Component[] components = MBeanManager.getAllComponents();
		for (int index = 0; index < components.length; index++)
		{
			components[index].reset();
		}
	}
}
