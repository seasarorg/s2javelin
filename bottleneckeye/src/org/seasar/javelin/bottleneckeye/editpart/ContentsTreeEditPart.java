package org.seasar.javelin.bottleneckeye.editpart;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ComponentType;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;

public class ContentsTreeEditPart extends StatsVisionTreeEditPart {
	// �I�[�o�[���C�h
	protected void refreshVisuals() {
		ContentsModel model = (ContentsModel) getModel();
		// �c���[�E�A�C�e���̃e�L�X�g�Ƃ��ă��f���̃e�L�X�g��ݒ�
		setWidgetText(model.getContentsName());
	}

	/**
	 * �A�E�g���C���ɕ\������v�f��Ԃ��B
	 * �R���|�[�l���g�̎�ނŃ\�[�g����B
	 */
	protected List getModelChildren() {
		// �����ŕԂ��ꂽ�q���f�����c���[�̎q�A�C�e���ɂȂ�
		List<ComponentModel> sortedChildren = ((ContentsModel) getModel())
				.getChildren();
		Collections.sort(sortedChildren, new Comparator<ComponentModel>() {
			public int compare(ComponentModel componentModel1,
					ComponentModel componentModel2) {
				ComponentType componentType1 = componentModel1.getComponentType();
				ComponentType componentType2 = componentModel2.getComponentType();
				
				return componentType1.ordinal() - componentType2.ordinal();
			}

		});

		return sortedChildren;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// �q���f���̒ǉ��E�폜���c���[�ɔ��f������
		if (evt.getPropertyName().equals(ContentsModel.P_CONTENTS_NAME)) {
			refreshVisuals();
		}
		// �q���f���̒ǉ��E�폜���c���[�ɔ��f������
		if (evt.getPropertyName().equals(ContentsModel.P_CHILDREN)) {
			refreshChildren();
		}
	}
}
