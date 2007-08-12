package org.seasar.javelin.statsvision.editors;

import java.beans.PropertyChangeListener;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.seasar.javelin.statsvision.model.AbstractModel;


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