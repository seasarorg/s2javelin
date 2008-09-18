package org.seasar.javelin.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Component implements ComponentMBean, Serializable
{
    private static final long       serialVersionUID = -6357402061431088667L;

    private String                  className_;

    private Map<String, Invocation> invocationMap_   = new HashMap<String, Invocation>();

    public Component(String className)
    {
        className_ = className;
    }

    public String getClassName()
    {
        return className_;
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

    public synchronized void addAndDeleteOldestInvocation(Invocation invocation)
    {
        int size = invocationMap_.size();
        Invocation[] invocations = invocationMap_.values().toArray(new Invocation[size]);
        Arrays.sort(invocations, new InvocationUpdatedTimeComparator());

        if (0 < size)
        {
            String deleteInvocationKey = invocations[0].getMethodName();
            invocationMap_.remove(deleteInvocationKey);
        }

        invocationMap_.put(invocation.getMethodName(), invocation);
    }

    public synchronized Invocation getInvocation(String methodName)
    {
        return invocationMap_.get(methodName);
    }

    public synchronized int getRecordedInvocationNum()
    {
        return invocationMap_.size();
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
