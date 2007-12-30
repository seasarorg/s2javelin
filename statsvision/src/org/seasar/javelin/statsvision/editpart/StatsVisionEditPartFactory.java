package org.seasar.javelin.statsvision.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.seasar.javelin.statsvision.editors.StatsVisionEditor;
import org.seasar.javelin.statsvision.model.ArrowConnectionModel;
import org.seasar.javelin.statsvision.model.ComponentModel;
import org.seasar.javelin.statsvision.model.ContentsModel;


public class StatsVisionEditPartFactory implements EditPartFactory
{
	StatsVisionEditor statsVisionEditor;
	
	public StatsVisionEditPartFactory(StatsVisionEditor statsVisionEditor)
	{
		this.statsVisionEditor = statsVisionEditor;
	}

	public EditPart createEditPart(EditPart context, Object model)
	{
		EditPart part = null;

		// モデルの型を調べて対応するEditPartを作成
		if (model instanceof ContentsModel)
		{
			part = new ContentsEditPart(statsVisionEditor);
		}
		else if (model instanceof ComponentModel)
		{
			ComponentModel    componentModel = (ComponentModel)model;
			ComponentEditPart componentPart  = new ComponentEditPart();
			statsVisionEditor.setComponentEditPart(componentPart);

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
