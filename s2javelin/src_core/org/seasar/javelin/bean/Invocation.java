package org.seasar.javelin.bean;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

public class Invocation extends NotificationBroadcasterSupport implements InvocationMBean
{
    private static final long INITIAL            = -1;

    private ObjectName        objName_;

    private ObjectName        classObjName_;

    private String            className_;

    private String            methodName_;

    private int               intervalMax_;

    private int               throwableMax_;

    private long              count_;

    private long              minimum_           = INITIAL;

    private long              maximum_           = INITIAL;

    /** CPU消費時間の最小値。 */
    private long cpuMinimum_ = INITIAL;

    /** CPU消費時間の最大値。 */
    private long cpuMaximum_ = INITIAL;

    private LinkedList<Long> intervalList_ = new LinkedList<Long>();

    private LinkedList<Long> cpuIntervalList_ = new LinkedList<Long>();

    private LinkedList<Throwable> throwableList_ = new LinkedList<Throwable>();

    private Set<Invocation> callerSet_ = new HashSet<Invocation>();

    private boolean isFieldAccess_     = false;

    private boolean isReadFieldAccess_ = false;

    /** 呼び出し時間の合計値（平均値算出用) */
    private long intervalSum_ = 0;

    /** CPU呼び出し時間の合計値（平均値算出用) */
    private long cpuIntervalSum_ = 0;
    
	private long accumulatedTime_;

	/**
	 * accumulatedTime_の最大値。
	 * {@link #setAccumulatedTime}の中でaccumulatedTime_と共に更新判定を行う。
	 */
	private long maxAccumulatedTime_;

	/** maxAccumulatedTime_の更新回数 */
	private long maxAccumulatedTimeUpdateCount_;

    /**
     * 呼び出し情報を記録する際の閾値。 
     * 値（ミリ秒）を下回る処理時間の呼び出し情報は記録しない。
     */
    private long              recordThreshold_;

    /**
     * 呼び出し情報を赤くブリンクする際の閾値。 
     * 値（ミリ秒）を下回る処理時間の呼び出し情報は赤くブリンクしない。
     */
    private long              alarmThreshold_;

	private String processName_;

    public Invocation(
            String processName, 
            ObjectName objName, 
            ObjectName classObjName, 
            String className,
            String methodName, 
            int intervalMax, 
            int throwableMax, 
            long recordThreshold,
            long alarmThreshold)
    {
    	processName_ = processName;
        objName_ = objName;
        classObjName_ = classObjName;
        className_ = className;
        methodName_ = methodName;
        intervalMax_ = intervalMax;
        throwableMax_ = throwableMax;
        recordThreshold_ = recordThreshold;
        alarmThreshold_ = alarmThreshold;
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

    public synchronized long getAverage()
    {
        if (count_ == 0)
        {
            return 0;
        }

        return intervalSum_ / count_;
    }

    public long getCpuMinimum()
    {
        return cpuMinimum_;
    }

    public long getCpuMaximum()
    {
        return cpuMaximum_;
    }

    public synchronized long getCpuAverage()
    {
        if (count_ == 0)
        {
            return 0;
        }

        return cpuIntervalSum_ / count_;
    }
    
    public List<Long> getIntervalList()
    {
        return intervalList_;
    }

    public long getThrowableCount()
    {
        return throwableList_.size();
    }

    public List<Throwable> getThrowableList()
    {
        return throwableList_;
    }

    public synchronized ObjectName[] getAllCallerObjectName()
    {
        Invocation[] invocations = (Invocation[])callerSet_.toArray(new Invocation[callerSet_.size()]);
        ObjectName[] objNames = new ObjectName[invocations.length];

        for (int index = 0; index < invocations.length; index++)
        {
            objNames[index] = invocations[index].getObjectName();
        }

        return objNames;
    }

    public synchronized Invocation[] getAllCallerInvocation()
    {
        Invocation[] invocations = (Invocation[])callerSet_.toArray(new Invocation[callerSet_.size()]);

        return invocations;
    }
    
    /**
     * メソッドの消費時間を追加する。
     * 
     * @param interval メソッドの消費時間。
     * @param cpuInterval メソッドのCPU消費時間。
     */
    public synchronized void addInterval(long interval, long cpuInterval)
    {
        count_++;

        intervalSum_ += interval;
        intervalList_.add(interval);
        while (intervalList_.size() > intervalMax_)
        {
            intervalList_.removeFirst();
        }

        cpuIntervalSum_ += cpuInterval;
        cpuIntervalList_.add(cpuInterval);
        while (cpuIntervalList_.size() > intervalMax_)
        {
            cpuIntervalList_.removeFirst();
        }

        if (interval < minimum_ || minimum_ == INITIAL)
        {
            minimum_ = interval;
        }
        if (interval > maximum_ || maximum_ == INITIAL)
        {
            maximum_ = interval;
        }
        if (cpuInterval < cpuMinimum_ || cpuMinimum_ == INITIAL)
        {
            cpuMinimum_ = cpuInterval;
        }
        if (cpuInterval > cpuMaximum_ || cpuMaximum_ == INITIAL)
        {
            cpuMaximum_ = cpuInterval;
        }
    }

    public synchronized void addCaller(Invocation caller)
    {
        if (caller != null)
        {
            callerSet_.add(caller);
        }
    }

    public synchronized void addThrowable(Throwable throwable)
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

    public long getAlarmThreshold()
    {
        return alarmThreshold_;
    }

    public void setAlarmThreshold(long alarmThreshold)
    {
        alarmThreshold_ = alarmThreshold;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(256);
        buffer.append(processName_);
        buffer.append(":");
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

    public synchronized void reset()
    {
        count_ = 0;
        
        minimum_ = INITIAL;
        maximum_ = INITIAL;
        intervalSum_ = 0;
        intervalList_.clear();

        cpuMinimum_ = INITIAL;
        cpuMaximum_ = INITIAL;
        cpuIntervalSum_ = 0;
        cpuIntervalList_.clear();
        
        throwableList_.clear();
    }

    public boolean isFieldAccess()
    {
        return isFieldAccess_;
    }

    public void setFieldAccess(boolean isFieldAccess)
    {
        isFieldAccess_ = isFieldAccess;
    }

    public boolean isReadFieldAccess()
    {
        return isReadFieldAccess_;
    }

    public void setReadFieldAccess(boolean isReadFieldAccess)
    {
        isReadFieldAccess_ = isReadFieldAccess;
    }

	public String getProcessName() {
		return processName_;
	}
	
	public long getAccumulatedTime() {
		return accumulatedTime_;
	}

	public void setAccumulatedTime(long accumulatedTime) {
		accumulatedTime_ = accumulatedTime;
		if (accumulatedTime_ > maxAccumulatedTime_)
		{
			maxAccumulatedTime_ = accumulatedTime_;
			maxAccumulatedTimeUpdateCount_++;
		}
	}
	
	public long getMaxAccumulatedTime() {
		return maxAccumulatedTime_;
	}

	public long getMaxAccumulatedTimeUpdateCount() {
		return maxAccumulatedTimeUpdateCount_;
	}
}
