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

		// ���f���̌^�𒲂ׂđΉ�����EditPart���쐬
		if (model instanceof ContentsModel)
		{
			part = new ContentsEditPart();
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

		part.setModel(model); // ���f����EditPart�ɐݒ肷��
		
		return part;
	}

}
