package org.seasar.javelin.communicate.entity;

/**
 * �d���̖{�̃f�[�^�N���X
 * @author eriguchi
 */
public class Body
{
    /**
     * �I�u�W�F�N�g��
     */
    private String   strObjName_      = "";

    /**
     * ���ږ�
     */
    private String   strItemName_     = "";

    /**
     * ���ڌ^
     */
    private byte     byteItemMode_    = 0;

    /**
     * �J��Ԃ���
     */
    private int      intLoopCount_    = 0;

    /**
     * ����
     */
    private Object[] objItemValueArr_ = null;

    /**
     * �I�u�W�F�N�g���擾
     * 
     * @return �I�u�W�F�N�g��
     */
    public String getStrObjName()
    {
        return this.strObjName_;
    }

    /**
     * �I�u�W�F�N�g���ݒ�
     * @param strObjName �I�u�W�F�N�g��
     */
    public void setStrObjName(String strObjName)
    {
        this.strObjName_ = strObjName;
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
     * @param strItemName ���ږ�
     */
    public void setStrItemName(String strItemName)
    {
        this.strItemName_ = strItemName;
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