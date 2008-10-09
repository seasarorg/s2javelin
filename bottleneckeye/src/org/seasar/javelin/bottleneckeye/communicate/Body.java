package org.seasar.javelin.bottleneckeye.communicate;

/**
 * �d���̖{�̃f�[�^�N���X
 */
public class Body
{
    /**
     * �I�u�W�F�N�g��
     */
    private String           strObjName_      = "";

    /**
     * ���ږ�
     */
    private String           strItemName_     = "";

    /** ���ڌ^�i�P�o�C�g�����t�����j */
    public static final byte ITEMTYPE_BYTE    = 0;

    /** ���ڌ^�i�Q�o�C�g�����t�����j */
    public static final byte ITEMTYPE_INT16   = 1;

    /** ���ڌ^�i�S�o�C�g�����t�����j */
    public static final byte ITEMTYPE_INT32   = 2;

    /** ���ڌ^�i�W�o�C�g�����t�����j */
    public static final byte ITEMTYPE_INT64   = 3;

    /** ���ڌ^�i�S�o�C�g�����t�����j */
    public static final byte ITEMTYPE_FLOAT   = 4;

    /** ���ڌ^�i�W�o�C�g�����t�����j */
    public static final byte ITEMTYPE_DOUBLE  = 5;

    /** ���ڌ^�i������j */
    public static final byte ITEMTYPE_STRING  = 6;

    /**
     * ���ڌ^
     */
    private byte             byteItemMode_    = 0;

    /**
     * �J��Ԃ���
     */
    private int              intLoopCount_    = 0;

    /**
     * ����
     */
    private Object[]         objItemValueArr_ = null;

    /**
     * �I�u�W�F�N�g���擾
     * @return �I�u�W�F�N�g��
     */
    public String getStrObjName()
    {
        return this.strObjName_;
    }

    /**
     * �I�u�W�F�N�g���ݒ�
     * @param strObjName_ �I�u�W�F�N�g��
     */
    public void setStrObjName(String strObjName_)
    {
        this.strObjName_ = strObjName_;
    }

    /**
     * ���ږ��擾
     * @return ���ږ�
     */
    public String getStrItemName()
    {
        return this.strItemName_;
    }

    /**
     * ���ږ��ݒ�
     * @param strItemName_ ���ږ�
     */
    public void setStrItemName(String strItemName_)
    {
        this.strItemName_ = strItemName_;
    }

    /**
     * ���ڌ^�擾
     * @return ���ڌ^
     */
    public byte getByteItemMode()
    {
        return this.byteItemMode_;
    }

    /**
     * ���ڌ^�ݒ�
     * @param byteItemMode ���ڌ^
     */
    public void setByteItemMode(byte byteItemMode)
    {
        this.byteItemMode_ = byteItemMode;
    }

    /**
     * �J��Ԃ��񐔎擾
     * @return �J��Ԃ���
     */
    public int getIntLoopCount()
    {
        return this.intLoopCount_;
    }

    /**
     * �J��Ԃ��񐔐ݒ�
     * @param intLoopCount �J��Ԃ���
     */
    public void setIntLoopCount(int intLoopCount)
    {
        this.intLoopCount_ = intLoopCount;
    }

    /**
     * �����擾
     * @return ����
     */
    public Object[] getObjItemValueArr()
    {
        return this.objItemValueArr_;
    }

    /**
     * �����ݒ�
     * @param objItemValueArr ����
     */
    public void setObjItemValueArr(Object[] objItemValueArr)
    {
        this.objItemValueArr_ = objItemValueArr;
    }
}