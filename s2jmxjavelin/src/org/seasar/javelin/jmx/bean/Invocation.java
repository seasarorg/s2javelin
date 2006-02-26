package org.seasar.javelin.jmx.bean;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.management.ObjectName;

public class Invocation implements InvocationMBean
{
	private static final long INITIAL = -1;

	private ObjectName objName_;
	private ObjectName classObjName_;
	
	private String className_;
	private String methodName_;

	private int intervalMax_;
    private int throwableMax_;
	
	private long count_;
	private long minimum_ = INITIAL;
	private long maximum_ = INITIAL;
	
	private LinkedList<Long>      intervalList_  = new LinkedList<Long>();
	private LinkedList<Throwable> throwableList_ = new LinkedList<Throwable>();
	private Set<InvocationMBean>  callerSet_     = new HashSet<InvocationMBean>();

	public Invocation(ObjectName objName, ObjectName classObjName, String className, String methodName, int intervalMax, int throwableMax)
	{
		objName_      = objName;
		classObjName_ = classObjName;
		className_    = className;
		methodName_   = methodName;
		intervalMax_  = intervalMax;
		throwableMax_ = throwableMax;
	}

	public ObjectName getComponentObjectName()
	{
		return classObjName_;
	}
	
	public ObjectName getObjectName()
	{
		return objName_;
	}
	
	public String getClassName()
	{
		return className_;
	}

	public String getMethodName()
	{
		return methodName_;
	}
	
	public long getCount()
	{
		return count_;
	}

	public long getMinimum()
	{
		return minimum_;
	}

	public long getMaximum()
	{
		return maximum_;
	}

	public long getAverage()
	{
		if (intervalList_.size() == 0)
		{
			return 0;
		}
		
		long sum = 0;
		for (long value : intervalList_)
		{
			sum = sum + value;
		}
		
		return sum / intervalList_.size();
	}

	public List<Long> getIntervalList()
	{
		return intervalList_;
	}

	public long getThrowableCount() {
		return throwableList_.size();
	}

	public List<Throwable> getThrowableList()
	{
		return throwableList_;
	}
	
	public ObjectName[] getAllCallerObjectName()
	{
		Invocation[] invocations = callerSet_.toArray(new Invocation[0]);
		ObjectName[] objNames    = new ObjectName[invocations.length];

		for (int index = 0; index < invocations.length; index++)
		{
			objNames[index] = invocations[index].getObjectName();
		}
		
		return objNames;
	}
	
	public void addInterval(long interval, InvocationMBean caller)
	{
		count_++;
		
		intervalList_.add(interval);
		while (intervalList_.size() > intervalMax_)
		{
			intervalList_.removeFirst();
		}
		
		if (caller != null)
		{
			callerSet_.add(caller);
		}
		
		if (interval < minimum_ || minimum_ == INITIAL) minimum_ = interval;
		if (interval > maximum_ || maximum_ == INITIAL) maximum_ = interval;
	}
	
	public void addThrowable(Throwable throwable)
	{
		throwableList_.add(throwable);
		while (throwableList_.size() > throwableMax_)
		{
			throwableList_.removeFirst();
		}
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(256);
		builder.append(className_);
		builder.append("#");
		builder.append(methodName_);
		builder.append(",");
		builder.append(getCount());
		builder.append(",");
		builder.append(getMinimum());
		builder.append(",");
		builder.append(getMaximum());
		builder.append(",");
		builder.append(getAverage());
		builder.append(",");
		builder.append(getThrowableCount());
		
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object target)
	{
		if (!(target instanceof Invocation))
		{
			return false;
		}
		
		Invocation invocation = (Invocation)target;
		if (!className_.equals(invocation.getClassName())
			|| !methodName_.equals(invocation.getMethodName()))
		{
			return false;
		}
		
		return true;
	}

	@Override
	public int hashCode()
	{
		String id = className_ + "#" + methodName_;
		return id.hashCode();
	}

	public void reset()
	{
		count_ = 0;
		minimum_ = INITIAL;
		maximum_ = INITIAL;
		
		intervalList_.clear();
		throwableList_.clear();
	}
}
