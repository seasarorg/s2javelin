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
	 * ImageDescriptorから、modelに対応するアイコンを取得する。<br>
	 * ComponentType毎に、WEB、データベース、クラスのアイコンを取得する。
	 * 
	 * @param model
	 *            アイコンを作成するモデル。
	 * @return アイコン
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
		// モデルのテキストの変更をツリー・アイテムにも反映させる
		if (evt.getPropertyName().equals(ComponentModel.P_CLASS_NAME))
			refreshVisuals();
	}
}
