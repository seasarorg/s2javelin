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
		// 自身をリスナとしてモデルに登録
		((AbstractModel) getModel()).addPropertyChangeListener(this);
	}

	public void deactivate()
	{
		super.deactivate();
		// モデルから削除
		((AbstractModel) getModel()).removePropertyChangeListener(this);
	}
}