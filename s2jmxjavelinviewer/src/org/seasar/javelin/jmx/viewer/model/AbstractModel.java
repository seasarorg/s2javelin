package org.seasar.javelin.jmx.viewer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

abstract public class AbstractModel implements IPropertySource
{

	// リスナのリスト
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	// リスナの追加
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.addPropertyChangeListener(listener);
	}

	// モデルの変更を通知
	public void firePropertyChange(String propName, Object oldValue,
			Object newValue)
	{
		listeners.firePropertyChange(propName, oldValue, newValue);
	}

	// リスナの削除
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.removePropertyChangeListener(listener);
	}

	public Object getEditableValue()
	{
		// 編集可能な値として自身を返す
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		// nullを返すと例外が発生するので長さ０の配列を返す
		return new IPropertyDescriptor[0];
	}

	public Object getPropertyValue(Object id)
	{
		return null;
	}

	public boolean isPropertySet(Object id)
	{
		return false;
	}

	public void resetPropertyValue(Object id)
	{
	}

	public void setPropertyValue(Object id, Object value)
	{
	}
}