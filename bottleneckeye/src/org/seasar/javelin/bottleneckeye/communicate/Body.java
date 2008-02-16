package org.seasar.javelin.bottleneckeye.communicate;

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
	

    /** 項目型（１バイト符号付整数） */
    public static final byte ITEMTYPE_BYTE = 0;

    /** 項目型（２バイト符号付整数） */
    public static final byte ITEMTYPE_INT16 = 1;

    /** 項目型（４バイト符号付整数） */
    public static final byte ITEMTYPE_INT32 = 2;

    /** 項目型（８バイト符号付整数） */
    public static final byte ITEMTYPE_INT64 = 3;

    /** 項目型（４バイト符号付整数） */
    public static final byte ITEMTYPE_FLOAT = 4;

    /** 項目型（８バイト符号付整数） */
    public static final byte ITEMTYPE_DOUBLE = 5;

    /** 項目型（文字列） */
    public static final byte ITEMTYPE_STRING = 6;

    /**
     * 項目型
     */
    private byte byteItemMode_ = 0;

    /**
     * 繰り返し回数
     */
    private int intLoopCount_ = 0;

    /**
     * 説明
     */
    private Object[] objItemValueArr_ = null;
    
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
    /**
     * 項目型取得
     */
    public byte getByteItemMode() {
        return byteItemMode_;
    }

    /**
     * 項目型設定
     */
    public void setByteItemMode(byte byteItemMode_) {
        this.byteItemMode_ = byteItemMode_;
    }

    /**
     * 繰り返し回数取得
     */
    public int getIntLoopCount() {
        return intLoopCount_;
    }

    /**
     * 繰り返し回数設定
     */
    public void setIntLoopCount(int intLoopCount_) {
        this.intLoopCount_ = intLoopCount_;
    }

    /**
     * 説明取得
     */
    public Object[] getObjItemValueArr() {
        return objItemValueArr_;
    }

    /**
     * 説明設定
     */
    public void setObjItemValueArr(Object[] objItemValueArr_) {
        this.objItemValueArr_ = objItemValueArr_;
    }
}