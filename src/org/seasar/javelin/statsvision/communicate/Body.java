package org.seasar.javelin.statsvision.communicate;

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
}