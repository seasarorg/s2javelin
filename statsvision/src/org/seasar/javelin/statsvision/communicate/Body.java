package org.seasar.javelin.statsvision.communicate;

/**
 * 電文の本体データクラス
 */
public class Body
{
    /**
     * オブジェクト名
     */
	private String strObjName_ = "";
	
    /**
     * 項目名
     */
	private String strItemName_ = "";
	
	/**
     * オブジェクト名取得
     */
	public String getStrObjName() {
		return strObjName_;
	}

    /**
     * オブジェクト名設定
     */
	public void setStrObjName(String strObjName_) {
		this.strObjName_ = strObjName_;
	}
	
    /**
     * 項目名取得
     */
	public String getStrItemName() {
		return strItemName_;
	}

    /**
     * 項目名設定
     */
	public void setStrItemName(String strItemName_) {
		this.strItemName_ = strItemName_;
	}
}