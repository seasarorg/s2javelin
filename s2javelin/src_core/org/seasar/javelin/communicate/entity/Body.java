package org.seasar.javelin.communicate.entity;

/**
 * �d���̖{�̃f�[�^�N���X
 */
public class Body
{
    /**
     * �I�u�W�F�N�g��
     */
	private String strObjName_ = "";
	
    /**
     * ���ږ�
     */
	private String strItemName_ = "";
	
    /**
     * ���ڌ^
     */
    private byte byteItemMode_ = 0;

    /**
     * �J��Ԃ���
     */
    private int intLoopCount_ = 0;

    /**
     * ����
     */
    private Object[] objItemValueArr_ = null;
    
	/**
     * �I�u�W�F�N�g���擾
     */
	public String getStrObjName() {
		return strObjName_;
	}

    /**
     * �I�u�W�F�N�g���ݒ�
     */
	public void setStrObjName(String strObjName_) {
		this.strObjName_ = strObjName_;
	}
	
    /**
     * ���ږ��擾
     */
	public String getStrItemName() {
		return strItemName_;
	}

    /**
     * ���ږ��ݒ�
     */
	public void setStrItemName(String strItemName_) {
		this.strItemName_ = strItemName_;
	}

    /**
     * ���ڌ^�擾
     */
    public byte getByteItemMode() {
        return byteItemMode_;
    }

    /**
     * ���ڌ^�ݒ�
     */
    public void setByteItemMode(byte byteItemMode_) {
        this.byteItemMode_ = byteItemMode_;
    }

    /**
     * �J��Ԃ��񐔎擾
     */
    public int getIntLoopCount() {
        return intLoopCount_;
    }

    /**
     * �J��Ԃ��񐔐ݒ�
     */
    public void setIntLoopCount(int intLoopCount_) {
        this.intLoopCount_ = intLoopCount_;
    }

    /**
     * �����擾
     */
    public Object[] getObjItemValueArr() {
        return objItemValueArr_;
    }

    /**
     * �����ݒ�
     */
    public void setObjItemValueArr(Object[] objItemValueArr_) {
        this.objItemValueArr_ = objItemValueArr_;
    }
}