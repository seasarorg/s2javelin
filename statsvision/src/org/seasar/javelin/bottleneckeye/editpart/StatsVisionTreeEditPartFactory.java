package org.seasar.javelin.bottleneckeye.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;

public class StatsVisionTreeEditPartFactory implements EditPartFactory
{
    public EditPart createEditPart(EditPart context, Object model) {
        EditPart part = null;

        if (model instanceof ContentsModel)
          part = new ContentsTreeEditPart();
        else if (model instanceof ComponentModel)
          part = new ComponentTreeEditPart();

        if (part != null)
          part.setModel(model);

        return part;
      }
}
