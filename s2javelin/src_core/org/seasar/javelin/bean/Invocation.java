package org.seasar.javelin.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Invocation implements InvocationMBean, Serializable
{
	private static final long serialVersionUID = -5812420032608047545L;

	private static final long              INITIAL             = -1;

    private String                         className_;

    private String                         methodName_;

    private int                            intervalMax_;

    private int                            throwableMax_;

    private long                           count_;

    private InvocationInterval             minimumInterval_    = new InvocationInterval();

    private InvocationInterval             maximumInterval_    = new InvocationInterval();

    private LinkedList<InvocationInterval> intervalList_ = new LinkedList<InvocationInterval>();

    private LinkedList<Throwable>          throwableList_      = new LinkedList<Throwable>();

    private Set<Invocation>                callerSet_          = new HashSet<Invocation>();

    private boolean                        isFieldAccess_      = false;

    private boolean                        isReadFieldAccess_  = false;

    private InvocationInterval             intervalSum_        = new InvocationInterval(0, 0, 0);

    private long                           accumulatedTime_;

    /** ログを出力するCPU時間の閾値 */
    private long                           recordCpuThreshold_ = 0;

    /** 警告を発生させるCPU時間の閾値 */
    private long                           alarmCpuThreshold_  = 0;

    /**
     * accumulatedTime_の最大値。 {@link #setAccumulatedTime}の中でaccumulatedTime_と共に更新判定を行う。
     */
    private long                           maxAccumulatedTime_;

    /** maxAccumulatedTime_の更新回数 */
    private long                           maxAccumulatedTimeUpdateCount_;

    /**
     * 呼び出し情報を記録する際の閾値。 値（ミリ秒）を下回る処理時間の呼び出し情報は記録しない。
     */
    private long                           recordThreshold_;

    /**
     * 呼び出し情報を赤くブリンクする際の閾値。 値（ミリ秒）を下回る処理時間の呼び出し情報は赤くブリンクしない。
     */
    private long                           alarmThreshold_;

    private String                         processName_;

    public Invocation(String processName, String className, String methodName,
            int intervalMax, int throwableMax, long recordThreshold, long alarmThreshold)
    {
        processName_ = processName;
        className_ = className;
        methodName_ = methodName;
        intervalMax_ = intervalMax;
        throwableMax_ = throwableMax;
        recordThreshold_ = recordThreshold;
        alarmThreshold_ = alarmThreshold;
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
        return minimumInterval_.getInterval();
    }

    public long getMaximum()
    {
        return maximumInterval_.getInterval();
    }

    public synchronized long getAverage()
    {
        if (count_ == 0)
        {
            return 0;
        }

        return intervalSum_.getInterval() / count_;
    }

    public long getCpuMinimum()
    {
        return minimumInterval_.getCpuInterval();
    }

    public long getCpuMaximum()
    {
        return maximumInterval_.getCpuInterval();
    }

    public synchronized long getCpuAverage()
    {
        if (count_ == 0)
        {
            return 0;
        }

        return intervalSum_.getCpuInterval() / count_;
    }

    public long getUserMinimum()
    {
        return minimumInterval_.getUserInterval();
    }

    public long getUserMaximum()
    {
        return maximumInterval_.getUserInterval();
    }

    public synchronized long getUserAverage()
    {
        if (count_ == 0)
        {
            return 0;
        }

        return intervalSum_.getUserInterval() / count_;
    }    
    public List<InvocationInterval> getIntervalList()
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

    public synchronized String[] getAllCallerName()
    {
        Invocation[] invocations =
                (Invocation[])callerSet_.toArray(new Invocation[callerSet_.size()]);
        String[] objNames = new String[invocations.length];

        for (int index = 0; index < invocations.length; index++)
        {
            objNames[index] = invocations[index].getClassName() + "#"
					+ invocations[index].getMethodName();
        }

        return objNames;
    }

    public synchronized Invocation[] getAllCallerInvocation()
    {
        Invocation[] invocations =
                (Invocation[])callerSet_.toArray(new Invocation[callerSet_.size()]);

        return invocations;
    }

    /**
     * メソッドの消費時間を追加する。
     * 
     * @param allInterval メソッドの消費時間。
     * @param cpuInterval メソッドのCPU消費時間。
     */
    public synchronized void addInterval(InvocationInterval interval)
    {
        count_++;

        InvocationInterval intervalSum = this.intervalSum_;
        updateIntervalSum(intervalSum, interval);
        intervalList_.add(interval);
        while (intervalList_.size() > intervalMax_)
        {
            intervalList_.removeFirst();
        }

        InvocationInterval minInterval = this.minimumInterval_;
        updateMinInterval(interval, minInterval);

        InvocationInterval maxInterval = this.maximumInterval_;
        updateMaxInterval(interval, maxInterval);
    }

    private void updateIntervalSum(InvocationInterval intervalSum, InvocationInterval interval)
    {
        long sum = intervalSum.getInterval() + interval.getInterval();
        long cpuSum = intervalSum.getCpuInterval() + interval.getCpuInterval();
        long userSum = intervalSum.getUserInterval() + interval.getUserInterval();

        intervalSum.setInterval(sum);
        intervalSum.setCpuInterval(cpuSum);
        intervalSum.setUserInterval(userSum);
    }

    private void updateMaxInterval(InvocationInterval interval, InvocationInterval maxInterval)
    {
        long newMaxInterval =
                calcUpdateMaxInterval(maxInterval.getInterval(), interval.getInterval());
        long newCpuMaxInterval =
                calcUpdateMaxInterval(maxInterval.getCpuInterval(), interval.getCpuInterval());
        long newUserMaxInterval =
                calcUpdateMaxInterval(maxInterval.getUserInterval(), interval.getUserInterval());

        maxInterval.setInterval(newMaxInterval);
        maxInterval.setCpuInterval(newCpuMaxInterval);
        maxInterval.setUserInterval(newUserMaxInterval);
    }

    private void updateMinInterval(InvocationInterval interval, InvocationInterval minInterval)
    {
        long newMinInterval =
                calcUpdateMinInterval(minInterval.getInterval(), interval.getInterval());
        long newMinCpuInterval =
                calcUpdateMinInterval(minInterval.getCpuInterval(), interval.getCpuInterval());
        long newMinUserInterval =
                calcUpdateMinInterval(minInterval.getUserInterval(), interval.getUserInterval());
        
        minInterval.setInterval(newMinInterval);
        minInterval.setCpuInterval(newMinCpuInterval);
        minInterval.setUserInterval(newMinUserInterval);
    }

    private long calcUpdateMinInterval(long oldValue, long newValue)
    {
        long result = oldValue;
        if (newValue < oldValue || oldValue == INITIAL)
        {
            result = newValue;
        }

        return result;
    }

    private long calcUpdateMaxInterval(long oldValue, long newValue)
    {
        long result = oldValue;
        if (newValue > oldValue || oldValue == INITIAL)
        {
            result = newValue;
        }

        return result;
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

        this.minimumInterval_ = new InvocationInterval();
        this.maximumInterval_ = new InvocationInterval();
        this.intervalSum_ = new InvocationInterval(0, 0, 0);
        intervalList_.clear();

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

    public String getProcessName()
    {
        return processName_;
    }

    public long getAccumulatedTime()
    {
        return accumulatedTime_;
    }

    public void setAccumulatedTime(long accumulatedTime)
    {
        accumulatedTime_ = accumulatedTime;
        if (accumulatedTime_ > maxAccumulatedTime_)
        {
            maxAccumulatedTime_ = accumulatedTime_;
            maxAccumulatedTimeUpdateCount_++;
        }
    }

    public long getMaxAccumulatedTime()
    {
        return maxAccumulatedTime_;
    }

    public long getMaxAccumulatedTimeUpdateCount()
    {
        return maxAccumulatedTimeUpdateCount_;
    }

    /**
     * ログを出力するCPU時間の閾値を取得する
     * 
     * @return CPU時間の閾値
     */
    public long getRecordCpuThreshold()
    {
        return this.recordCpuThreshold_;
    }

    /**
     * ログを出力するCPU時間の閾値を設定する
     * 
     * @param recordCpuThreshold CPU時間の閾値
     */
    public void setRecordCpuThreshold(long recordCpuThreshold)
    {
        this.recordCpuThreshold_ = recordCpuThreshold;
    }

    /**
     * 警告を発生させるCPU時間の閾値を取得する
     * 
     * @return CPU時間の閾値
     */
    public long getAlarmCpuThreshold()
    {
        return this.alarmCpuThreshold_;
    }

    /**
     * 警告を発生させるCPU時間の閾値を設定する
     * 
     * @param recordCpuThreshold CPU時間の閾値
     */
    public void setAlarmCpuThreshold(long alarmCpuThreshold)
    {
        this.alarmCpuThreshold_ = alarmCpuThreshold;
    }
}
