package org.seasar.javelin.bottleneckeye.editpart;

import java.beans.PropertyChangeListener;

import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.seasar.javelin.bottleneckeye.model.AbstractModel;

public abstract class StatsVisionTreeEditPart
    extends AbstractTreeEditPart implements PropertyChangeListener
{
      // オーバーライド
      public void activate() {
        super.activate();
        ((AbstractModel) getModel()).addPropertyChangeListener(this);
      }
      // オーバーライド
      public void deactivate() {
        ((AbstractModel) getModel()).removePropertyChangeListener(this);
        super.deactivate();
      }
}
