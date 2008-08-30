package org.seasar.javelin.bean;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Invocation implements InvocationMBean, Serializable
{
    private static final long              serialVersionUID    = -5812420032608047545L;

    private static final long              INITIAL             = -1;

    private String                         className_;

    private String                         methodName_;

    private int                            intervalMax_;

    private int                            throwableMax_;

    private long                           count_;

    private InvocationInterval             minimumInterval_    = new InvocationInterval();

    private InvocationInterval             maximumInterval_    = new InvocationInterval();

    private LinkedList<InvocationInterval> intervalList_       =
                                                                       new LinkedList<InvocationInterval>();

    private LinkedList<Throwable>          throwableList_      = new LinkedList<Throwable>();

    private Map<Invocation, Invocation>    callerSet_          =
                                                                       new ConcurrentHashMap<Invocation, Invocation>();

    private boolean                        isFieldAccess_      = false;

    private boolean                        isReadFieldAccess_  = false;

    private InvocationInterval             intervalSum_        = new InvocationInterval(0, 0, 0);

    private long                           accumulatedTime_;

    /** ���O���o�͂���CPU���Ԃ�臒l */
    private long                           recordCpuThreshold_ = 0;

    /** �x���𔭐�������CPU���Ԃ�臒l */
    private long                           alarmCpuThreshold_  = 0;

    /**
     * accumulatedTime_�̍ő�l�B {@link #setAccumulatedTime}�̒���accumulatedTime_�Ƌ��ɍX�V������s���B
     */
    private long                           maxAccumulatedTime_;

    /** maxAccumulatedTime_�̍X�V�� */
    private long                           maxAccumulatedTimeUpdateCount_;

    /**
     * �Ăяo�������L�^����ۂ�臒l�B �l�i�~���b�j������鏈�����Ԃ̌Ăяo�����͋L�^���Ȃ��B
     */
    private long                           recordThreshold_;

    /**
     * �Ăяo������Ԃ��u�����N����ۂ�臒l�B �l�i�~���b�j������鏈�����Ԃ̌Ăяo�����͐Ԃ��u�����N���Ȃ��B
     */
    private long                           alarmThreshold_;

    private String                         processName_;

    /** �n�b�V���R�[�h�B */
    private int                            code_               = 0;

    /** �ŏI�X�V���� */
    private long                           lastUpdatedTime_;

    /**
     * 
     * @param processName
     * @param className
     * @param methodName
     * @param intervalMax
     * @param throwableMax
     * @param recordThreshold
     * @param alarmThreshold
     */
    public Invocation(String processName, String className, String methodName, int intervalMax,
            int throwableMax, long recordThreshold, long alarmThreshold)
    {
        this.processName_ = processName;
        this.className_ = className;
        this.methodName_ = methodName;
        this.intervalMax_ = intervalMax;
        this.throwableMax_ = throwableMax;
        this.recordThreshold_ = recordThreshold;
        this.alarmThreshold_ = alarmThreshold;

        String id = this.className_ + "#" + this.methodName_;
        this.code_ = id.hashCode();

        this.lastUpdatedTime_ = System.currentTimeMillis();
    }

    public String getClassName()
    {
        return this.className_;
    }

    public String getMethodName()
    {
        return this.methodName_;
    }

    public long getCount()
    {
        return this.count_;
    }

    public long getMinimum()
    {
        return this.minimumInterval_.getInterval();
    }

    public long getMaximum()
    {
        return this.maximumInterval_.getInterval();
    }

    public long getAverage()
    {
        if (this.count_ == 0)
        {
            return 0;
        }

        return this.intervalSum_.getInterval() / this.count_;
    }

    public long getCpuMinimum()
    {
        return this.minimumInterval_.getCpuInterval();
    }

    public long getCpuMaximum()
    {
        return this.maximumInterval_.getCpuInterval();
    }

    public long getCpuAverage()
    {
        if (this.count_ == 0)
        {
            return 0;
        }

        return this.intervalSum_.getCpuInterval() / this.count_;
    }

    public long getUserMinimum()
    {
        return this.minimumInterval_.getUserInterval();
    }

    public long getUserMaximum()
    {
        return this.maximumInterval_.getUserInterval();
    }

    public long getUserAverage()
    {
        if (this.count_ == 0)
        {
            return 0;
        }

        return this.intervalSum_.getUserInterval() / this.count_;
    }

    public List<InvocationInterval> getIntervalList()
    {
        return this.intervalList_;
    }

    public long getThrowableCount()
    {
        return this.throwableList_.size();
    }

    public List<Throwable> getThrowableList()
    {
        return this.throwableList_;
    }

    public synchronized String[] getAllCallerName()
    {
        Invocation[] invocations =
                (Invocation[])callerSet_.keySet().toArray(new Invocation[callerSet_.size()]);
        String[] objNames = new String[invocations.length];

        for (int index = 0; index < invocations.length; index++)
        {
            objNames[index] =
                    invocations[index].getClassName() + "#" + invocations[index].getMethodName();
        }

        return objNames;
    }

    public synchronized Invocation[] getAllCallerInvocation()
    {
        Invocation[] invocations =
                (Invocation[])callerSet_.keySet().toArray(new Invocation[callerSet_.size()]);

        return invocations;
    }

    /**
     * ���\�b�h�̏���Ԃ�ǉ�����B
     * 
     * @param allInterval ���\�b�h�̏���ԁB
     * @param cpuInterval ���\�b�h��CPU����ԁB
     */
    public synchronized void addInterval(InvocationInterval interval)
    {
        count_++;

        InvocationInterval intervalSum = this.intervalSum_;
        updateIntervalSum(intervalSum, interval);
        this.intervalList_.add(interval);
        while (this.intervalList_.size() > this.intervalMax_)
        {
            this.intervalList_.removeFirst();
        }

        InvocationInterval minInterval = this.minimumInterval_;
        updateMinInterval(interval, minInterval);

        InvocationInterval maxInterval = this.maximumInterval_;
        updateMaxInterval(interval, maxInterval);
        
        updateLastUpdatedTime();
    }

    private void updateIntervalSum(InvocationInterval intervalSum, InvocationInterval interval)
    {
        long sum = intervalSum.getInterval() + interval.getInterval();
        long cpuSum = intervalSum.getCpuInterval() + interval.getCpuInterval();
        long userSum = intervalSum.getUserInterval() + interval.getUserInterval();

        intervalSum.setInterval(sum);
        intervalSum.setCpuInterval(cpuSum);
        intervalSum.setUserInterval(userSum);
        
        updateLastUpdatedTime();
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
        
        updateLastUpdatedTime();
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
        
        updateLastUpdatedTime();
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

    public void addCaller(Invocation caller)
    {
        if (caller != null)
        {
            callerSet_.put(caller, caller);
        }
        
        updateLastUpdatedTime();
    }

    public synchronized void addThrowable(Throwable throwable)
    {
        throwableList_.add(throwable);
        while (throwableList_.size() > throwableMax_)
        {
            throwableList_.removeFirst();
        }
        
        updateLastUpdatedTime();
    }

    public long getRecordThreshold()
    {
        return recordThreshold_;
    }

    public void setRecordThreshold(long recordThreshold)
    {
        recordThreshold_ = recordThreshold;
        
        updateLastUpdatedTime();
    }

    public long getAlarmThreshold()
    {
        return alarmThreshold_;
    }

    public void setAlarmThreshold(long alarmThreshold)
    {
        alarmThreshold_ = alarmThreshold;
        
        updateLastUpdatedTime();
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
        return code_;
    }

    public synchronized void reset()
    {
        count_ = 0;

        this.minimumInterval_ = new InvocationInterval();
        this.maximumInterval_ = new InvocationInterval();
        this.intervalSum_ = new InvocationInterval(0, 0, 0);
        intervalList_.clear();

        throwableList_.clear();
        
        updateLastUpdatedTime();
    }

    public boolean isFieldAccess()
    {
        return isFieldAccess_;
    }

    public void setFieldAccess(boolean isFieldAccess)
    {
        isFieldAccess_ = isFieldAccess;
        
        updateLastUpdatedTime();
    }

    public boolean isReadFieldAccess()
    {
        return isReadFieldAccess_;
    }

    public void setReadFieldAccess(boolean isReadFieldAccess)
    {
        isReadFieldAccess_ = isReadFieldAccess;
        
        updateLastUpdatedTime();
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
        
        updateLastUpdatedTime();
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
     * ���O���o�͂���CPU���Ԃ�臒l���擾����
     * 
     * @return CPU���Ԃ�臒l
     */
    public long getRecordCpuThreshold()
    {
        return this.recordCpuThreshold_;
    }

    /**
     * ���O���o�͂���CPU���Ԃ�臒l��ݒ肷��
     * 
     * @param recordCpuThreshold CPU���Ԃ�臒l
     */
    public void setRecordCpuThreshold(long recordCpuThreshold)
    {
        this.recordCpuThreshold_ = recordCpuThreshold;
        updateLastUpdatedTime();
    }

    /**
     * �x���𔭐�������CPU���Ԃ�臒l���擾����
     * 
     * @return CPU���Ԃ�臒l
     */
    public long getAlarmCpuThreshold()
    {
        return this.alarmCpuThreshold_;
    }

    /**
     * �x���𔭐�������CPU���Ԃ�臒l��ݒ肷��
     * 
     * @param recordCpuThreshold CPU���Ԃ�臒l
     */
    public void setAlarmCpuThreshold(long alarmCpuThreshold)
    {
        this.alarmCpuThreshold_ = alarmCpuThreshold;
        updateLastUpdatedTime();
    }

    /**
     * �ŏI�X�V�������擾
     * @return �ŏI�X�V����
     */
    public long getLastUpdatedTime()
    {
        return this.lastUpdatedTime_;
    }
    
    /**
     * �ŏI�X�V�������X�V
     */
    private void updateLastUpdatedTime()
    {
        this.lastUpdatedTime_ = System.currentTimeMillis();
    }
}
