package org.seasar.javelin.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Component implements ComponentMBean, Serializable
{
    private static final long       serialVersionUID = -6357402061431088667L;

    private String                  className_;

    private Map<String, Invocation> invocationMap_   = new ConcurrentHashMap<String, Invocation>();

    private Queue<String>           methodNameQueue_ = new ConcurrentLinkedQueue<String>();

    public Component(String className)
    {
        className_ = className;
    }

    public String getClassName()
    {
        return className_;
    }

    public Invocation[] getAllInvocation()
    {
        int size = invocationMap_.values().size();
        Invocation[] invocations = invocationMap_.values().toArray(new Invocation[size]);
        return invocations;
    }

    public void addInvocation(Invocation invocation)
    {
        String methodName = invocation.getMethodName();
        methodNameQueue_.offer(methodName);
        invocationMap_.put(methodName, invocation);
    }

    public void addAndDeleteOldestInvocation(Invocation invocation)
    {
        int size = methodNameQueue_.size();
        if (0 < size)
        {
            String deleteInvocationKey = methodNameQueue_.poll();
            invocationMap_.remove(deleteInvocationKey);
        }

        addInvocation(invocation);
    }

    public Invocation getInvocation(String methodName)
    {
        return invocationMap_.get(methodName);
    }

    public int getRecordedInvocationNum()
    {
        return invocationMap_.size();
    }

    public void reset()
    {
        for (Invocation invocation : invocationMap_.values())
        {
            invocation.reset();
        }
    }
}
