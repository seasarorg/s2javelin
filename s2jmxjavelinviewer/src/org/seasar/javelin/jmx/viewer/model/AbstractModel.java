package org.seasar.javelin.jmx.viewer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

abstract public class AbstractModel implements IPropertySource
{

	// ���X�i�̃��X�g
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	// ���X�i�̒ǉ�
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.addPropertyChangeListener(listener);
	}

	// ���f���̕ύX��ʒm
	public void firePropertyChange(String propName, Object oldValue,
			Object newValue)
	{
		listeners.firePropertyChange(propName, oldValue, newValue);
	}

	// ���X�i�̍폜
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.removePropertyChangeListener(listener);
	}

	public Object getEditableValue()
	{
		// �ҏW�\�Ȓl�Ƃ��Ď��g��Ԃ�
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		// null��Ԃ��Ɨ�O����������̂Œ����O�̔z���Ԃ�
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