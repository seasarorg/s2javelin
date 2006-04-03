package org.seasar.javelin.jmx;

import java.util.HashMap;
import java.util.Map;

import org.seasar.javelin.jmx.bean.Component;

public class MBeanManager
{
    /** ComponentMBeanを登録したマップ。 */
    private static Map<String, Component> mBeanMap_;

	static
	{
		mBeanMap_ = new HashMap<String, Component>();
	}

	public static Component[] getAllComponents()
	{
		Component[] components = 
			mBeanMap_.values().toArray(new Component[mBeanMap_.size()]);
		return components;
	}
	
    public static Component getComponent(String className)
    {
    	return (Component)mBeanMap_.get(className);
    }
    
    public static void setComponent(String className, Component component)
    {
    	mBeanMap_.put(className, component);
    }
}
