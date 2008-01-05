package org.seasar.javelin.statsvision.editpart;

import java.beans.PropertyChangeEvent;

import org.seasar.javelin.statsvision.model.ComponentModel;

public class ComponentTreeEditPart extends StatsVisionTreeEditPart
{
    // �I�[�o�[���C�h
    protected void refreshVisuals() {
      ComponentModel model = (ComponentModel)getModel();
      // �c���[�E�A�C�e���̃e�L�X�g�Ƃ��ă��f���̃e�L�X�g��ݒ�
      setWidgetText(model.getClassName());
      
      // TODO:�A�E�g���C���ɃA�C�R����\������B
//      setWidgetImage(image);
    }

    public void propertyChange(PropertyChangeEvent evt) {
      // ���f���̃e�L�X�g�̕ύX���c���[�E�A�C�e���ɂ����f������
      if (evt.getPropertyName().equals(ComponentModel.P_CLASS_NAME))
        refreshVisuals();
    }
}
