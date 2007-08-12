package org.seasar.javelin.jmx.viewer.model;

import java.util.ArrayList;
import java.util.List;

public class ContentsModel
{
	  private List children = new ArrayList(); // 子モデルのリスト

	  public void addChild(Object child) { 
	    children.add(child); // 子モデルを追加
	  }

	  public List getChildren() {
	    return children; // 子モデルを返す
	  }
}
