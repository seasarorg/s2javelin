package org.seasar.javelin.bottleneckeye.communicate;

/**
 * �d���f�[�^�N���X
 */
public class Telegram
{
	/**
	 * ����
	 */
	private Header objHeader_ = null;
	
	/**
	 * �{��
	 */
	private Body[] objBody_ = null;
	
	/**
	 * �����擾
	 */
	public Header getObjHeader() {
		return objHeader_;
	}

	/**
	 * �����ݒ�
	 */
	public void setObjHeader(Header objHeader_) {
		this.objHeader_ = objHeader_;
	}

	/**
	 * �{�̎擾
	 */
	public Body[] getObjBody() {
		return objBody_;
	}

	/**
	 * �{�̐ݒ�
	 */
	public void setObjBody(Body[] objBody_) {
		this.objBody_ = objBody_;
	}
	
}