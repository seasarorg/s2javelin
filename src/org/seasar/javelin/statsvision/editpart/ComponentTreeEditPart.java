package org.seasar.javelin.statsvision.editpart;

import java.beans.PropertyChangeEvent;

import org.seasar.javelin.statsvision.model.ComponentModel;

public class ComponentTreeEditPart extends StatsVisionTreeEditPart
{
    // オーバーライド
    protected void refreshVisuals() {
      ComponentModel model = (ComponentModel)getModel();
      // ツリー・アイテムのテキストとしてモデルのテキストを設定
      setWidgetText(model.getClassName());
      
      // TODO:アウトラインにアイコンを表示する。
//      setWidgetImage(image);
    }

    public void propertyChange(PropertyChangeEvent evt) {
      // モデルのテキストの変更をツリー・アイテムにも反映させる
      if (evt.getPropertyName().equals(ComponentModel.P_CLASS_NAME))
        refreshVisuals();
    }
}
