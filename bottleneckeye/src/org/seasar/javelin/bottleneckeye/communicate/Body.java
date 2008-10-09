package org.seasar.javelin.bottleneckeye.communicate;

/**
 * 電文の本体データクラス
 */
public class Body
{
    /**
     * オブジェクト名
     */
    private String           strObjName_      = "";

    /**
     * 項目名
     */
    private String           strItemName_     = "";

    /** 項目型（１バイト符号付整数） */
    public static final byte ITEMTYPE_BYTE    = 0;

    /** 項目型（２バイト符号付整数） */
    public static final byte ITEMTYPE_INT16   = 1;

    /** 項目型（４バイト符号付整数） */
    public static final byte ITEMTYPE_INT32   = 2;

    /** 項目型（８バイト符号付整数） */
    public static final byte ITEMTYPE_INT64   = 3;

    /** 項目型（４バイト符号付整数） */
    public static final byte ITEMTYPE_FLOAT   = 4;

    /** 項目型（８バイト符号付整数） */
    public static final byte ITEMTYPE_DOUBLE  = 5;

    /** 項目型（文字列） */
    public static final byte ITEMTYPE_STRING  = 6;

    /**
     * 項目型
     */
    private byte             byteItemMode_    = 0;

    /**
     * 繰り返し回数
     */
    private int              intLoopCount_    = 0;

    /**
     * 説明
     */
    private Object[]         objItemValueArr_ = null;

    /**
     * オブジェクト名取得
     * @return オブジェクト名
     */
    public String getStrObjName()
    {
        return this.strObjName_;
    }

    /**
     * オブジェクト名設定
     * @param strObjName_ オブジェクト名
     */
    public void setStrObjName(String strObjName_)
    {
        this.strObjName_ = strObjName_;
    }

    /**
     * 項目名取得
     * @return 項目名
     */
    public String getStrItemName()
    {
        return this.strItemName_;
    }

    /**
     * 項目名設定
     * @param strItemName_ 項目名
     */
    public void setStrItemName(String strItemName_)
    {
        this.strItemName_ = strItemName_;
    }

    /**
     * 項目型取得
     * @return 項目型
     */
    public byte getByteItemMode()
    {
        return this.byteItemMode_;
    }

    /**
     * 項目型設定
     * @param byteItemMode 項目型
     */
    public void setByteItemMode(byte byteItemMode)
    {
        this.byteItemMode_ = byteItemMode;
    }

    /**
     * 繰り返し回数取得
     * @return 繰り返し回数
     */
    public int getIntLoopCount()
    {
        return this.intLoopCount_;
    }

    /**
     * 繰り返し回数設定
     * @param intLoopCount 繰り返し回数
     */
    public void setIntLoopCount(int intLoopCount)
    {
        this.intLoopCount_ = intLoopCount;
    }

    /**
     * 説明取得
     * @return 説明
     */
    public Object[] getObjItemValueArr()
    {
        return this.objItemValueArr_;
    }

    /**
     * 説明設定
     * @param objItemValueArr 説明
     */
    public void setObjItemValueArr(Object[] objItemValueArr)
    {
        this.objItemValueArr_ = objItemValueArr;
    }
}