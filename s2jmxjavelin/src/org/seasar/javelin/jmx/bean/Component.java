package org.seasar.javelin.jmx.bean;

import java.util.HashMap;
import java.util.Map;

import javax.management.ObjectName;

public class Component implements ComponentMBean
{
	private ObjectName objName_;
	private String     className_;
	
	private Map map_ = new HashMap();

	public Component(ObjectName objName, String className)
	{
		objName_   = objName;
		className_ = className;
	}
	
	public ObjectName getObjectName()
	{
		return objName_;
	}
	
	public String getClassName()
	{
		return className_;
	}
	
	public ObjectName[] getAllInvocationObjectName()
	{
		Invocation[] invocations = (Invocation[])map_.values().toArray(new Invocation[0]);
		ObjectName[] objNames    = new ObjectName[invocations.length];

		for (int index = 0; index < invocations.length; index++)
		{
			objNames[index] = invocations[index].getObjectName();
		}
		
		return objNames;
	}
	
	public void addInvocation(Invocation invocation)
	{
		map_.put(invocation.getMethodName(), invocation);
	}
	
	public Invocation getInvocation(String methodName)
	{
		return (Invocation)map_.get(methodName);
	}

	public void reset()
	{
		Invocation[] invocations = 
			(Invocation[])map_.values().toArray(new Invocation[map_.size()]);
		for (int index = 0; index < invocations.length; index++)
		{
			invocations[index].reset();
		}
	}
}
