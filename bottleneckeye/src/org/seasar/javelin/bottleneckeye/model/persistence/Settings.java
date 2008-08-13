package org.seasar.javelin.bottleneckeye.model.persistence;

import javax.xml.bind.annotation.XmlAttribute;

public class Settings
{
    private String  hostName;

    private Integer portNum;

    private String  domain;

    private Long    warningThreshold;

    private Long    alarmThreshold;

    /** View画面のクラス１つに表示するメソッドの最大数 */
    private Long    maxMethodCount;

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

    /**
     * View画面のクラス１つに表示するメソッドの最大数を返す。
     *
     * @return View画面のクラス１つに表示するメソッドの最大数
     */
    @XmlAttribute
    public Long getMaxMethodCount()
    {
        return this.maxMethodCount;
    }

    /**
     * View画面のクラス１つに表示するメソッドの最大数をセットする。
     *
     * @param maxMethodCount View画面のクラス１つに表示するメソッドの最大数
     */
    public void setMaxMethodCount(Long maxMethodCount)
    {
        this.maxMethodCount = maxMethodCount;
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
