package org.seasar.javelin.jmx.viewer.editors;

import java.beans.PropertyChangeListener;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.seasar.javelin.jmx.viewer.model.AbstractModel;


abstract public class EditPartWithListener extends AbstractGraphicalEditPart
		implements PropertyChangeListener
{

	public void activate()
	{
		super.activate();
		// ���g�����X�i�Ƃ��ă��f���ɓo�^
		((AbstractModel) getModel()).addPropertyChangeListener(this);
	}

	public void deactivate()
	{
		super.deactivate();
		// ���f������폜
		((AbstractModel) getModel()).removePropertyChangeListener(this);
	}
}