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