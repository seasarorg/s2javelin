package org.seasar.javelin.bottleneckeye.communicate;

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
	

    /** ���ڌ^�i�P�o�C�g�����t�����j */
    public static final byte ITEMTYPE_BYTE = 0;

    /** ���ڌ^�i�Q�o�C�g�����t�����j */
    public static final byte ITEMTYPE_INT16 = 1;

    /** ���ڌ^�i�S�o�C�g�����t�����j */
    public static final byte ITEMTYPE_INT32 = 2;

    /** ���ڌ^�i�W�o�C�g�����t�����j */
    public static final byte ITEMTYPE_INT64 = 3;

    /** ���ڌ^�i�S�o�C�g�����t�����j */
    public static final byte ITEMTYPE_FLOAT = 4;

    /** ���ڌ^�i�W�o�C�g�����t�����j */
    public static final byte ITEMTYPE_DOUBLE = 5;

    /** ���ڌ^�i������j */
    public static final byte ITEMTYPE_STRING = 6;

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