package org.seasar.javelin.bottleneckeye.editpart;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ComponentType;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;

public class ContentsTreeEditPart extends StatsVisionTreeEditPart {
	// オーバーライド
	protected void refreshVisuals() {
		ContentsModel model = (ContentsModel) getModel();
		// ツリー・アイテムのテキストとしてモデルのテキストを設定
		setWidgetText(model.getContentsName());
	}

	/**
	 * アウトラインに表示する要素を返す。
	 * コンポーネントの種類でソートする。
	 */
	protected List getModelChildren() {
		// ここで返された子モデルがツリーの子アイテムになる
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
		// 子モデルの追加・削除をツリーに反映させる
		if (evt.getPropertyName().equals(ContentsModel.P_CONTENTS_NAME)) {
			refreshVisuals();
		}
		// 子モデルの追加・削除をツリーに反映させる
		if (evt.getPropertyName().equals(ContentsModel.P_CHILDREN)) {
			refreshChildren();
		}
	}
}
