package org.seasar.javelin.jmx.viewer.model;

import java.util.List;

public class InvocationModel
{
	private ComponentModel component_;
	private String methodName_;
	private long count_;
	private long minimum_;
	private long maximum_;
	private long average_;
	private long throwableCount_;
	private List<Throwable> throwableList_;

	public long getAverage()
	{
		return average_;
	}

	public void setAverage(long average)
	{
		average_ = average;
	}

	public long getCount()
	{
		return count_;
	}

	public void setCount(long count)
	{
		count_ = count;
	}

	public long getMaximum()
	{
		return maximum_;
	}

	public void setMaximum(long maximum)
	{
		maximum_ = maximum;
	}

	public long getMinimum()
	{
		return minimum_;
	}

	public void setMinimum(long minimum)
	{
		minimum_ = minimum;
	}

	public long getThrowableCount()
	{
		return throwableCount_;
	}

	public void setThrowableCount(long throwableCount)
	{
		throwableCount_ = throwableCount;
	}

	public List<Throwable> getThrowableList()
	{
		return throwableList_;
	}

	public void setThrowableList(List<Throwable> throwableList)
	{
		throwableList_ = throwableList;
	}

	public ComponentModel getComponent()
	{
		return component_;
	}
	
	public void setComponent(ComponentModel component)
	{
		component_ = component;
	}

	public String getMethodName()
	{
		return methodName_;
	}

	public void setMethodName(String methodName)
	{
		methodName_ = methodName;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("ŒÄ‚Ño‚µ‰ñ”=");
		builder.append(getCount());
		builder.append(", •½‹Ïˆ—ŠÔ=");
		builder.append(getAverage());
		builder.append(", Å‘åˆ—ŠÔ=");
		builder.append(getMaximum());
		builder.append(", Å¬ˆ—ŠÔ=");
		builder.append(getMinimum());
		builder.append(", —áŠO”­¶‰ñ”=");
		builder.append(getThrowableCount());
		
		return builder.toString();
	}

	
	
//	public long getCount();
//	public long getMinimum();
//	public long getMaximum();
//	public long getAverage();
//	public long getThrowableCount();
//	public List<Throwable> getThrowableList();
//	public ObjectName[] getAllCallerObjectName();

}
