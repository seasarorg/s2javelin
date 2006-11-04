package org.seasar.javelin.jmx.bean;

import java.util.HashMap;
import java.util.Map;

import javax.management.ObjectName;

public class Component implements ComponentMBean
{
	private ObjectName objName_;
	private String     className_;
	
	private Map<String, Invocation> invocationMap_ = new HashMap<String, Invocation>();

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
		int size = invocationMap_.values().size();
		Invocation[] invocations = 
			(Invocation[])invocationMap_.values().toArray(new Invocation[size]);
		ObjectName[] objNames    = new ObjectName[invocations.length];

		for (int index = 0; index < invocations.length; index++)
		{
			objNames[index] = invocations[index].getObjectName();
		}
		
		return objNames;
	}
	
	public Invocation[] getAllInvocation()
	{
		int size = invocationMap_.values().size();
		Invocation[] invocations = 
			(Invocation[])invocationMap_.values().toArray(new Invocation[size]);
		return invocations;
	}
	
	public void addInvocation(Invocation invocation)
	{
		invocationMap_.put(invocation.getMethodName(), invocation);
	}
	
	public Invocation getInvocation(String methodName)
	{
		return (Invocation)invocationMap_.get(methodName);
	}

	public void reset()
	{
		int size = invocationMap_.values().size();
		Invocation[] invocations = 
			invocationMap_.values().toArray(new Invocation[size]);
		for (int index = 0; index < invocations.length; index++)
		{
			invocations[index].reset();
		}
	}
}
