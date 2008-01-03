package org.seasar.javelin.statsvision.editpart;

import java.beans.PropertyChangeListener;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.seasar.javelin.statsvision.model.AbstractModel;

public abstract class StatsVisionTreeEditPart
    extends AbstractTreeEditPart implements PropertyChangeListener
{
      // �I�[�o�[���C�h
      public void activate() {
        super.activate();
        ((AbstractModel) getModel()).addPropertyChangeListener(this);
      }
      // �I�[�o�[���C�h
      public void deactivate() {
        ((AbstractModel) getModel()).removePropertyChangeListener(this);
        super.deactivate();
      }
}
