package org.seasar.javelin.bottleneckeye.model.persistence;

import javax.xml.bind.annotation.XmlAttribute;

public class Settings
{
    private String  hostName;

    private Integer portNum;

    private String  domain;

    private Long    warningThreshold;

    private Long    alarmThreshold;

    private String  mode;

    private String  lineStyle;

    @XmlAttribute
    public String getHostName()
    {
        return this.hostName;
    }

    public void setHostName(String hostName)
    {
        this.hostName = hostName;
    }

    @XmlAttribute
    public Integer getPortNum()
    {
        return this.portNum;
    }

    public void setPortNum(Integer portNum)
    {
        this.portNum = portNum;
    }

    @XmlAttribute
    public String getDomain()
    {
        return this.domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }

    @XmlAttribute
    public Long getWarningThreshold()
    {
        return this.warningThreshold;
    }

    public void setWarningThreshold(Long warningThreshold)
    {
        this.warningThreshold = warningThreshold;
    }

    @XmlAttribute
    public Long getAlarmThreshold()
    {
        return this.alarmThreshold;
    }

    public void setAlarmThreshold(Long alarmThreshold)
    {
        this.alarmThreshold = alarmThreshold;
    }

    @XmlAttribute
    public String getMode()
    {
        return this.mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    @XmlAttribute
    public String getLineStyle()
    {
        return this.lineStyle;
    }

    public void setLineStyle(String lineStyle)
    {
        this.lineStyle = lineStyle;
    }
}
