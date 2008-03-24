package org.seasar.javelin.bottleneckeye.model.persistence;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Component
{
    private String       name;

    private int          x;

    private int          y;

    private List<Method> methods;

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
    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    @XmlAttribute
    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    @XmlElement(name = "method")
    public List<Method> getMethods()
    {
        return methods;
    }

    public void setMethods(List<Method> methods)
    {
        this.methods = methods;
    }
}
