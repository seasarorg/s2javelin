package org.seasar.javelin.statsvision.editpart;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.seasar.javelin.statsvision.model.ContentsModel;

public class ContentsTreeEditPart extends StatsVisionTreeEditPart
{
    // �I�[�o�[���C�h
    protected List getModelChildren() {
      // �����ŕԂ��ꂽ�q���f�����c���[�̎q�A�C�e���ɂȂ�
      return ((ContentsModel)getModel()).getChildren();
    }

    public void propertyChange(PropertyChangeEvent evt) {
      // �q���f���̒ǉ��E�폜���c���[�ɔ��f������
//      if(evt.getPropertyName().equals(ContentsModel.P_CHILDREN))
//        refreshChildren();
    }
}
