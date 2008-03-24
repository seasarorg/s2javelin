package org.seasar.javelin.bottleneckeye.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.seasar.javelin.bottleneckeye.editors.view.AbstractStatsVisionEditor;
import org.seasar.javelin.bottleneckeye.model.ArrowConnectionModel;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;


public class StatsVisionEditPartFactory implements EditPartFactory
{
	private AbstractStatsVisionEditor<?> statsVisionEditor_;
	
	public StatsVisionEditPartFactory(AbstractStatsVisionEditor<?> statsVisionEditor)
	{
		this.statsVisionEditor_ = statsVisionEditor;
	}

	public EditPart createEditPart(EditPart context, Object model)
	{
		EditPart part = null;

		// ���f���̌^�𒲂ׂđΉ�����EditPart���쐬
		if (model instanceof ContentsModel)
		{
			part = new ContentsEditPart(this.statsVisionEditor_);
		}
		else if (model instanceof ComponentModel)
		{
			ComponentModel    componentModel = (ComponentModel)model;
			ComponentEditPart componentPart  = new ComponentEditPart(this.statsVisionEditor_);
			this.statsVisionEditor_.addComponentEditPart(componentModel.getClassName(), componentPart);

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
