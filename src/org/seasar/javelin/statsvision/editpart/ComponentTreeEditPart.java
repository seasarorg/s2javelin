package org.seasar.javelin.statsvision.editpart;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.seasar.javelin.statsvision.StatsVisionPlugin;
import org.seasar.javelin.statsvision.model.ComponentModel;
import org.seasar.javelin.statsvision.model.ComponentType;

public class ComponentTreeEditPart extends StatsVisionTreeEditPart {
	// オーバーライド
	protected void refreshVisuals() {
		ComponentModel model = (ComponentModel) getModel();
		// ツリー・アイテムのテキストとしてモデルのテキストを設定
		String className = model.getClassName();
		setWidgetText(className);

		// アウトラインにアイコンを表示する。
		Image image = createImage(model);
		if (image != null) {
			setWidgetImage(image);
		}
	}

	/**
	 * ImageDescriptorから、modelに対応するアイコンを生成する。<br>
	 * ComponentType毎に、WEB、データベース、クラスのアイコンを生成する。
	 * 
	 * @param model アイコンを作成するモデル。
	 * @return アイコン
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
		// モデルのテキストの変更をツリー・アイテムにも反映させる
		if (evt.getPropertyName().equals(ComponentModel.P_CLASS_NAME))
			refreshVisuals();
	}
}
