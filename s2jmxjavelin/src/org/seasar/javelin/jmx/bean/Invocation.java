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
	private Set<Invocation>       callerSet_     = new HashSet<Invocation>();

    /** 
     *  呼び出し情報を記録する際の閾値。
     *  値（ミリ秒）を下回る処理時間の呼び出し情報は記録しない。
     */
    private long recordThreshold_ = 0L;

	public Invocation(
			ObjectName objName
			, ObjectName classObjName
			, String className
			, String methodName
			, int intervalMax
			, int throwableMax
			, long recordThreshold)
	{
		objName_         = objName;
		classObjName_    = classObjName;
		className_       = className;
		methodName_      = methodName;
		intervalMax_     = intervalMax;
		throwableMax_    = throwableMax;
		recordThreshold_ = recordThreshold;
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
		for (int index = 0; index < intervalList_.size(); index++)
		{
			Long value = (Long)(intervalList_.get(index));
			sum = sum + value.longValue();
		}
		
		return sum / intervalList_.size();
	}

	public List getIntervalList()
	{
		return intervalList_;
	}

	public long getThrowableCount() {
		return throwableList_.size();
	}

	public List getThrowableList()
	{
		return throwableList_;
	}
	
	public ObjectName[] getAllCallerObjectName()
	{
		Invocation[] invocations = 
			(Invocation[])callerSet_.toArray(new Invocation[callerSet_.size()]);
		ObjectName[] objNames    = new ObjectName[invocations.length];

		for (int index = 0; index < invocations.length; index++)
		{
			objNames[index] = invocations[index].getObjectName();
		}
		
		return objNames;
	}
	
	public void addInterval(long interval)
	{
		count_++;
		
		intervalList_.add(new Long(interval));
		while (intervalList_.size() > intervalMax_)
		{
			intervalList_.removeFirst();
		}
		
		if (interval < minimum_ || minimum_ == INITIAL) minimum_ = interval;
		if (interval > maximum_ || maximum_ == INITIAL) maximum_ = interval;
	}
	
	public void addCaller(Invocation caller)
	{
		if (caller != null)
		{
			callerSet_.add(caller);
		}
	}
	
	public void addThrowable(Throwable throwable)
	{
		throwableList_.add(throwable);
		while (throwableList_.size() > throwableMax_)
		{
			throwableList_.removeFirst();
		}
	}
	
	public long getRecordThreshold()
	{
		return recordThreshold_;
	}

	public void setRecordThreshold(long recordThreshold)
	{
		recordThreshold_ = recordThreshold;
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer(256);
		buffer.append(className_);
		buffer.append("#");
		buffer.append(methodName_);
		buffer.append(",");
		buffer.append(getCount());
		buffer.append(",");
		buffer.append(getMinimum());
		buffer.append(",");
		buffer.append(getMaximum());
		buffer.append(",");
		buffer.append(getAverage());
		buffer.append(",");
		buffer.append(getThrowableCount());
		
		return buffer.toString();
	}
	
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
