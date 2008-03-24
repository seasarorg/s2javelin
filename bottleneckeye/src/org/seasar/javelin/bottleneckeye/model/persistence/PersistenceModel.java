package org.seasar.javelin.bottleneckeye.model.persistence;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "bottleneckEye")
public class PersistenceModel
{
    private Settings settings;

    private View    view;

    @XmlElement
    public Settings getSettings()
    {
        return this.settings;
    }

    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }

    @XmlElement
    public View getView()
    {
        return this.view;
    }

    public void setView(View view)
    {
        this.view = view;
    }
}
