package org.seasar.javelin.jmx.viewer.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.seasar.javelin.jmx.viewer.model.ArrowConnectionModel;
import org.seasar.javelin.jmx.viewer.model.ComponentModel;
import org.seasar.javelin.jmx.viewer.model.ContentsModel;


public class S2JmxJavelinEditPartFactory implements EditPartFactory
{

	public EditPart createEditPart(EditPart context, Object model)
	{
		EditPart part = null;

		// モデルの型を調べて対応するEditPartを作成
		if (model instanceof ContentsModel)
		{
			part = new ContentsEditPart();
		}
		else if (model instanceof ComponentModel)
		{
			ComponentModel    componentModel = (ComponentModel)model;
			ComponentEditPart componentPart  = new ComponentEditPart();

			componentModel.setEditPart(componentPart);
			part = componentPart;
		}
		else if (model instanceof ArrowConnectionModel)
		{
			part = new ArrowConnectionEditPart();
		}

		part.setModel(model); // モデルをEditPartに設定する
		
		return part;
	}

}
