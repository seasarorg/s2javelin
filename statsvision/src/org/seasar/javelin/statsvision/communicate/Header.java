package org.seasar.javelin.statsvision.communicate;

/**
 * �d���̓����f�[�^�N���X
 */
public class Header
{
	public static final int HEADER_LENGTH = 6;

	/**
	 * �d����
	 */
	private int intSize_ = 0;
	
	/**
	 * �d�����
	 */
	private byte byteTelegramKind_ = 0;
	
	/**
	 * �v���������
	 */
	private byte byteRequestKind_ = 0;
	
	/**
	 * �d�����擾
	 */
	public int getIntSize() {
		return intSize_;
	}

	/**
	 * �d�����ݒ�
	 */
	public void setIntSize(int intSize_) {
		this.intSize_ = intSize_;
	}
	
	/**
	 * �d����ʎ擾
	 */
	public byte getByteTelegramKind() {
		return byteTelegramKind_;
	}

	/**
	 * �d����ʐݒ�
	 */
	public void setByteTelegramKind(byte byteTelegramKind_) {
		this.byteTelegramKind_ = byteTelegramKind_;
	}
	
	/**
	 * �v��������ʎ擾
	 */
	public byte getByteRequestKind() {
		return byteRequestKind_;
	}

	/**
	 * �v��������ʐݒ�
	 */
	public void setByteRequestKind(byte byteRequestKind_) {
		this.byteRequestKind_ = byteRequestKind_;
	}
}