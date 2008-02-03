package org.seasar.javelin.bottleneckeye.editpart;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.seasar.javelin.bottleneckeye.StatsVisionPlugin;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ComponentType;

public class ComponentTreeEditPart extends StatsVisionTreeEditPart {
	private Image webImage_;
	private Image classImage_;
	private Image dbImage_;

	public ComponentTreeEditPart() {
		super();
		this.webImage_ = StatsVisionPlugin
				.getImageDescriptor("icons/outline_web.gif").createImage();
		this.dbImage_ = StatsVisionPlugin
				.getImageDescriptor("icons/outline_db.gif").createImage();
		this.classImage_ = StatsVisionPlugin
				.getImageDescriptor("icons/outline_class.gif").createImage();
	}

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
	 * ImageDescriptor����Amodel�ɑΉ�����A�C�R�����擾����B<br>
	 * ComponentType���ɁAWEB�A�f�[�^�x�[�X�A�N���X�̃A�C�R�����擾����B
	 * 
	 * @param model
	 *            �A�C�R�����쐬���郂�f���B
	 * @return �A�C�R��
	 */
	private Image createImage(ComponentModel model) {
		if (model == null) {
			return null;
		}

		Image image;
		if (model.getComponentType() == ComponentType.WEB) {
			image = this.webImage_;
		} else if (model.getComponentType() == ComponentType.DATABASE) {
			image = this.dbImage_;
		} else {
			image = this.classImage_;
		}
		return image;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// ���f���̃e�L�X�g�̕ύX���c���[�E�A�C�e���ɂ����f������
		if (evt.getPropertyName().equals(ComponentModel.P_CLASS_NAME))
			refreshVisuals();
	}
}
