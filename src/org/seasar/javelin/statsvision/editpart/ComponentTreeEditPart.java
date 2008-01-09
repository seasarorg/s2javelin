package org.seasar.javelin.statsvision.editpart;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.seasar.javelin.statsvision.StatsVisionPlugin;
import org.seasar.javelin.statsvision.model.ComponentModel;
import org.seasar.javelin.statsvision.model.ComponentType;

public class ComponentTreeEditPart extends StatsVisionTreeEditPart {
	// �I�[�o�[���C�h
	protected void refreshVisuals() {
		ComponentModel model = (ComponentModel) getModel();
		// �c���[�E�A�C�e���̃e�L�X�g�Ƃ��ă��f���̃e�L�X�g��ݒ�
		String className = model.getClassName();
		setWidgetText(className);

		// �A�E�g���C���ɃA�C�R����\������B
		Image image = createImage(model);
		if (image != null) {
			setWidgetImage(image);
		}
	}

	/**
	 * ImageDescriptor����Amodel�ɑΉ�����A�C�R���𐶐�����B<br>
	 * ComponentType���ɁAWEB�A�f�[�^�x�[�X�A�N���X�̃A�C�R���𐶐�����B
	 * 
	 * @param model �A�C�R�����쐬���郂�f���B
	 * @return �A�C�R��
	 */
	private Image createImage(ComponentModel model) {
		if (model == null) {
			return null;
		}

		ImageDescriptor imageDescriptor = null;
		if (model.getComponentType() == ComponentType.WEB) {
			imageDescriptor = StatsVisionPlugin
					.getImageDescriptor("icons/outline_web.gif");
		} else if (model.getComponentType() == ComponentType.DATABASE) {
			imageDescriptor = StatsVisionPlugin
					.getImageDescriptor("icons/outline_db.gif");
		} else {
			imageDescriptor = StatsVisionPlugin
					.getImageDescriptor("icons/outline_class.gif");
		}

		if (imageDescriptor == null) {
			return null;
		}

		Image image = imageDescriptor.createImage();
		return image;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// ���f���̃e�L�X�g�̕ύX���c���[�E�A�C�e���ɂ����f������
		if (evt.getPropertyName().equals(ComponentModel.P_CLASS_NAME))
			refreshVisuals();
	}
}
