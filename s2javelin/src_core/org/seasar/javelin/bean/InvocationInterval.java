package org.seasar.javelin.bean;

import java.io.Serializable;

public class InvocationInterval implements Serializable
{
	private static final long serialVersionUID = 7855547784390235717L;

	private static final long INITIAL = -1;

    private long              interval;

    private long              cpuInterval;

    private long              userInterval;

    public InvocationInterval()
    {
        this.interval = INITIAL;
        this.cpuInterval = INITIAL;
        this.userInterval = INITIAL;
    }

    public long getInterval()
    {
        return interval;
    }

    public void setInterval(long interval)
    {
        this.interval = interval;
    }

    public long getCpuInterval()
    {
        return cpuInterval;
    }

    public void setCpuInterval(long cpuInterval)
    {
        this.cpuInterval = cpuInterval;
    }

    public long getUserInterval()
    {
        return userInterval;
    }

    public void setUserInterval(long userInterval)
    {
        this.userInterval = userInterval;
    }

    public InvocationInterval(long interval, long cpuInterval, long userInterval)
    {
        super();
        this.interval = interval;
        this.cpuInterval = cpuInterval;
        this.userInterval = userInterval;
    }
}
