package org.seasar.javelin.bottleneckeye.model.persistence;

import javax.xml.bind.annotation.XmlAttribute;

public class Relation
{
    private String sourceName;

    private String targetName;

    @XmlAttribute
    public String getSourceName()
    {
        return sourceName;
    }

    public void setSourceName(String sourceName)
    {
        this.sourceName = sourceName;
    }

    @XmlAttribute
    public String getTargetName()
    {
        return targetName;
    }

    public void setTargetName(String targetName)
    {
        this.targetName = targetName;
    }
}
