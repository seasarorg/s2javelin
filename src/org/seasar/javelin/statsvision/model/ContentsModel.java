package org.seasar.javelin.statsvision.model;

import java.util.ArrayList;
import java.util.List;

public class ContentsModel extends AbstractModel {
	private List<ComponentModel> children = new ArrayList<ComponentModel>(); // �q���f���̃��X�g

	public void addChild(ComponentModel child) {
		children.add(child); // �q���f����ǉ�
	}

	public List<ComponentModel> getChildren() {
		return children; // �q���f����Ԃ�
	}
}
