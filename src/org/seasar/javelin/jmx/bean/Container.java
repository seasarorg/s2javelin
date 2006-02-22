package org.seasar.javelin.jmx.bean;

import java.util.Map;
import javax.management.ObjectName;

public class Container implements ContainerMBean
{
    private static Map<String, Component> map_;

    public Container(Map<String, Component> map)
    {
    	map_ = map;
    }
    
    public ObjectName[] getAllComponentObjectName()
    {
    	Component[] components = map_.values().toArray(new Component[0]);
		ObjectName[] objNames    = new ObjectName[components.length];

		for (int index = 0; index < components.length; index++)
		{
			objNames[index] = components[index].getObjectName();
		}
		
		return objNames;
    }
}
