package org.seasar.javelin.communicate.entity;


/**
 * �d���̉����{�̃f�[�^�N���X
 */
public class ResponseBody extends Body
{
	/**
	 * ���ڌ^
	 */
	private byte byteItemMode_ = 0;

	/**
	 * �J��Ԃ���
	 */
	private int intLoopCount_ = 1;

	/**
	 * ����
	 */
	private Object[] objItemValueArr_ = null;
	
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