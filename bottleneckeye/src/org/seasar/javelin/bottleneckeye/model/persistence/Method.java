package org.seasar.javelin.bottleneckeye.model.persistence;

import javax.xml.bind.annotation.XmlAttribute;

public class Method
{
    private String name;

    private Long   average;

    private Long   maximum;

    private Long   minimum;

    private Long   throwableCount;

    private Long   warningThreshold;

    private Long   alarmThreshold;

    @XmlAttribute
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @XmlAttribute
    public Long getAverage()
    {
        return average;
    }

    public void setAverage(Long average)
    {
        this.average = average;
    }

    @XmlAttribute
    public Long getMaximum()
    {
        return maximum;
    }

    public void setMaximum(Long maximum)
    {
        this.maximum = maximum;
    }

    @XmlAttribute
    public Long getMinimum()
    {
        return minimum;
    }

    public void setMinimum(Long minimum)
    {
        this.minimum = minimum;
    }

    @XmlAttribute
    public Long getThrowableCount()
    {
        return throwableCount;
    }

    public void setThrowableCount(Long throwableCount)
    {
        this.throwableCount = throwableCount;
    }

    @XmlAttribute
    public Long getWarningThreshold()
    {
        return warningThreshold;
    }

    public void setWarningThreshold(Long warningThreshold)
    {
        this.warningThreshold = warningThreshold;
    }

    @XmlAttribute
    public Long getAlarmThreshold()
    {
        return alarmThreshold;
    }

    public void setAlarmThreshold(Long alarmThreshold)
    {
        this.alarmThreshold = alarmThreshold;
    }
}
