package org.seasar.javelin.bottleneckeye.model;

import java.util.ArrayList;
import java.util.List;

/**
 * StatsVisionのルートコンテンツモデル。
 * 
 * @author yamasaki
 */
public class ContentsModel extends AbstractModel {
    
    /** 変更の種類を識別するための文字列。 */
    public static final String P_CONTENTS_NAME = "_contents_name";

    /** 子モデルの変更を識別するための文字列。 */
    public static final String P_CHILDREN = "_children";
    
    // 子モデルのリスト
	private List<ComponentModel> children = new ArrayList<ComponentModel>();

	// モデルの名前
	private String contentsName;
	
	/**
	 * コンストラクタ。
	 */
    public ContentsModel()
    {
        super();
    }

    /**
     * モデルの名前を取得する。
     * 
     * @return モデルの名前。
     */
    public String getContentsName()
    {
        return this.contentsName;
    }

    /**
     * モデルの名前を設定する。
     * 
     * @param contentsName モデルの名前。
     */
    public void setContentsName(String contentsName)
    {
        this.contentsName = contentsName;
        firePropertyChange(P_CONTENTS_NAME, null, this.contentsName);
    }
    
    /**
	 * 子モデルを追加する。
	 * 
	 * @param child 追加する子モデル。
	 */
	public void addChild(ComponentModel child) {
	    this.children.add(child);
        firePropertyChange(P_CHILDREN, null, this.children);
	}

	/**
	 * 子モデルのリストを取得する。
	 * 
	 * @return 子モデルのリスト。
	 */
	public List<ComponentModel> getChildren() {
		return this.children;
	}
}
