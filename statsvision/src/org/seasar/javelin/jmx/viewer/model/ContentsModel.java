package org.seasar.javelin.jmx.viewer.model;

import java.util.ArrayList;
import java.util.List;

public class ContentsModel
{
	  private List children = new ArrayList(); // �q���f���̃��X�g

	  public void addChild(Object child) { 
	    children.add(child); // �q���f����ǉ�
	  }

	  public List getChildren() {
	    return children; // �q���f����Ԃ�
	  }
}
