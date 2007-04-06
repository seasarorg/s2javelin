package org.seasar.javelin.jmx;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.seasar.javelin.jmx.bean.Component;

public class MBeanManager 
{
	/** ComponentMBeanを登録したマップ。 */
	private static Map mBeanMap_;

	static 
	{
		mBeanMap_ = new HashMap();
	}

	public static Component[] getAllComponents() 
	{
		synchronized (mBeanMap_) 
		{
			Component[] components = (Component[])
			mBeanMap_.values().toArray(new Component[0]);
			return components;
		}
	}

	public static Component getComponent(String className) {
		synchronized (mBeanMap_) {
			return (Component) mBeanMap_.get(className);
		}
	}

	public static void setComponent(String className, Component component) {
		synchronized (mBeanMap_) {
			mBeanMap_.put(className, component);
		}
	}

	public static void reset() {
		synchronized (mBeanMap_) {
			Collection values = mBeanMap_.values();
			for (Iterator iter = values.iterator(); iter.hasNext();) {
				Component component = (Component) iter.next();
				synchronized (component) {
					component.reset();
				}
			}
		}
	}
}
