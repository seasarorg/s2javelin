package org.seasar.javelin;

public class VMStatus
{
    private long cpuTime_;

    private long userTime_;

    private long blockedTime_;

    private long waitedTime_;

    private long blockedCount_;

    private long waitedCount_;
    
    private long collectionCount_;
    
    private long collectionTime_;
    
    private long peakMamoryUsage_;

    public long getBlockedTime( )
    {
        return this.blockedTime_;
    }

    public void setBlockedTime(long blockedTime)
    {
        this.blockedTime_ = blockedTime;
    }

    public long getCpuTime( )
    {
        return this.cpuTime_;
    }

    public void setCpuTime(long cpuTime)
    {
        this.cpuTime_ = cpuTime;
    }

    public long getUserTime( )
    {
        return this.userTime_;
    }

    public void setUserTime(long userTime)
    {
        this.userTime_ = userTime;
    }

    public long getWaitedTime( )
    {
        return this.waitedTime_;
    }

    public void setWaitedTime(long waitedTime)
    {
        this.waitedTime_ = waitedTime;
    }

    public long getBlockedCount( )
    {
        return this.blockedCount_;
    }

    public void setBlockedCount(long blockedCount)
    {
        this.blockedCount_ = blockedCount;
    }

    public long getWaitedCount( )
    {
        return this.waitedCount_;
    }

    public void setWaitedCount(long waitedCount)
    {
        this.waitedCount_ = waitedCount;
    }

    public long getCollectionCount()
    {
        return this.collectionCount_;
    }

    public void setCollectionCount(long collectionCount)
    {
        this.collectionCount_ = collectionCount;
    }

    public long getCollectionTime()
    {
        return this.collectionTime_;
    }

    public void setCollectionTime(long collectionTime)
    {
        this.collectionTime_ = collectionTime;
    }

    public long getPeakMamoryUsage()
    {
        return this.peakMamoryUsage_;
    }

    public void setPeakMamoryUsage(long peakMamoryUsage)
    {
        this.peakMamoryUsage_ = peakMamoryUsage;
    }
}
