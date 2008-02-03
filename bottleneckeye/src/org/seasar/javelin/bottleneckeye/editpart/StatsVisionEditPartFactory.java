package org.seasar.javelin.bottleneckeye.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.seasar.javelin.bottleneckeye.editors.StatsVisionEditor;
import org.seasar.javelin.bottleneckeye.model.ArrowConnectionModel;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;


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

		// ƒ‚ƒfƒ‹‚ÌŒ^‚ğ’²‚×‚Ä‘Î‰‚·‚éEditPart‚ğì¬
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

		part.setModel(model); // ƒ‚ƒfƒ‹‚ğEditPart‚Éİ’è‚·‚é
		
		return part;
	}

}
