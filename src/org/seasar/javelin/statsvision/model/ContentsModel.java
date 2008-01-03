package org.seasar.javelin.statsvision.model;

import java.util.ArrayList;
import java.util.List;

public class ContentsModel extends AbstractModel {
	private List<ComponentModel> children = new ArrayList<ComponentModel>(); // 子モデルのリスト

	public void addChild(ComponentModel child) {
		children.add(child); // 子モデルを追加
	}

	public List<ComponentModel> getChildren() {
		return children; // 子モデルを返す
	}
}
