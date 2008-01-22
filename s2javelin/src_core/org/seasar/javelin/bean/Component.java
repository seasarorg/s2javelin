package org.seasar.javelin.bean;

import java.util.HashMap;
import java.util.Map;

import javax.management.ObjectName;

public class Component implements ComponentMBean
{
    private String                  className_;

    private ObjectName              objName_;

    private Map<String, Invocation> invocationMap_ = new HashMap<String, Invocation>();

    public Component(ObjectName objName, String className)
    {
        objName_ = objName;
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

    public synchronized ObjectName[] getAllInvocationObjectName()
    {
        int size = invocationMap_.values().size();
        Invocation[] invocations = invocationMap_.values().toArray(new Invocation[size]);
        ObjectName[] objNames = new ObjectName[invocations.length];

        for (int index = 0; index < invocations.length; index++)
        {
            objNames[index] = invocations[index].getObjectName();
        }

        return objNames;
    }

    public synchronized Invocation[] getAllInvocation()
    {
        int size = invocationMap_.values().size();
        Invocation[] invocations = invocationMap_.values().toArray(new Invocation[size]);
        return invocations;
    }

    public synchronized void addInvocation(Invocation invocation)
    {
        invocationMap_.put(invocation.getMethodName(), invocation);
    }

    public synchronized Invocation getInvocation(String methodName)
    {
        return invocationMap_.get(methodName);
    }

    public synchronized void reset()
    {
        int size = invocationMap_.values().size();
        Invocation[] invocations = invocationMap_.values().toArray(new Invocation[size]);
        for (int index = 0; index < invocations.length; index++)
        {
            invocations[index].reset();
        }
    }
}
