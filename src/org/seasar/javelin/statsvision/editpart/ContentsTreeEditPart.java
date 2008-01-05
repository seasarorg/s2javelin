package org.seasar.javelin.statsvision.editpart;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.seasar.javelin.statsvision.model.ContentsModel;

public class ContentsTreeEditPart extends StatsVisionTreeEditPart
{
    // オーバーライド
    protected void refreshVisuals() {
        ContentsModel model = (ContentsModel)getModel();
        // ツリー・アイテムのテキストとしてモデルのテキストを設定
        setWidgetText(model.getContentsName());
    }
    
    protected List getModelChildren() {
      // ここで返された子モデルがツリーの子アイテムになる
      return ((ContentsModel)getModel()).getChildren();
    }

    public void propertyChange(PropertyChangeEvent evt) {
      // 子モデルの追加・削除をツリーに反映させる
      if(evt.getPropertyName().equals(ContentsModel.P_CONTENTS_NAME))
      {
          refreshVisuals();
      }
      // 子モデルの追加・削除をツリーに反映させる
      if(evt.getPropertyName().equals(ContentsModel.P_CHILDREN))
      {
          refreshChildren();
      }
    }
}
