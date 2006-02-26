package org.seasar.javelin.jmx.bean;

import java.util.Map;
import javax.management.ObjectName;

public class Container implements ContainerMBean
{
    private static Map map_;

    public Container(Map map)
    {
    	map_ = map;
    }
    
    public ObjectName[] getAllComponentObjectName()
    {
    	Component[] components = 
    		(Component[])map_.values().toArray(new Component[0]);
		ObjectName[] objNames    = new ObjectName[components.length];

		for (int index = 0; index < components.length; index++)
		{
			objNames[index] = components[index].getObjectName();
		}
		
		return objNames;
    }

	public void reset()
	{
		Component[] components = 
			(Component[])map_.values().toArray(new Component[map_.size()]);
		for (int index = 0; index < components.length; index++)
		{
			components[index].reset();
		}
	}
}
