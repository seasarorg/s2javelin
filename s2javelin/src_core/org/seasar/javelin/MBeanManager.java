package org.seasar.javelin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.seasar.javelin.bean.Component;

public class MBeanManager {
	/** ComponentMBeanを登録したマップ。 */
	private static Map<String, Component> mBeanMap_;

	static {
		mBeanMap_ = new HashMap<String, Component>();
	}

	public static Component[] getAllComponents() {
		synchronized (mBeanMap_) {
			int size = mBeanMap_.values().size();
			Component[] components = (Component[]) mBeanMap_.values().toArray(
					new Component[size]);
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
			Collection<Component> values = mBeanMap_.values();
			for (Component component : values) {
				synchronized (component) {
					component.reset();
				}
			}
		}
	}
}
