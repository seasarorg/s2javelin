package org.seasar.javelin.util;

import javax.management.ObjectName;

import junit.framework.TestCase;

import org.seasar.javelin.S2JavelinConfig;

public class ObjectNameUtilTest extends TestCase
{
    public void testCreateComponentBeanName() throws Exception
    {
        String name;

        S2JavelinConfig javelinConfig = new S2JavelinConfig();
        javelinConfig.setDomain("");
        name = ObjectNameUtil.createComponentBeanName("", javelinConfig);
        System.out.println(new ObjectName("default.component:type=org.seasar.javelin.bean.ComponentMBean,class="));

//        
//        name = ObjectNameUtil.createComponentBeanName("", new S2JavelinConfig());
//        System.out.println(new ObjectName(name));
//        System.out.println(name);
    }

}
