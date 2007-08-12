package org.seasar.javelin.statsvision.communicate;

/**
 * 電文の頭部データクラス
 */
public class Header
{
	public static final int HEADER_LENGTH = 6;

	/**
	 * 電文長
	 */
	private int intSize_ = 0;
	
	/**
	 * 電文種別
	 */
	private byte byteTelegramKind_ = 0;
	
	/**
	 * 要求応答種別
	 */
	private byte byteRequestKind_ = 0;
	
	/**
	 * 電文長取得
	 */
	public int getIntSize() {
		return intSize_;
	}

	/**
	 * 電文長設定
	 */
	public void setIntSize(int intSize_) {
		this.intSize_ = intSize_;
	}
	
	/**
	 * 電文種別取得
	 */
	public byte getByteTelegramKind() {
		return byteTelegramKind_;
	}

	/**
	 * 電文種別設定
	 */
	public void setByteTelegramKind(byte byteTelegramKind_) {
		this.byteTelegramKind_ = byteTelegramKind_;
	}
	
	/**
	 * 要求応答種別取得
	 */
	public byte getByteRequestKind() {
		return byteRequestKind_;
	}

	/**
	 * 要求応答種別設定
	 */
	public void setByteRequestKind(byte byteRequestKind_) {
		this.byteRequestKind_ = byteRequestKind_;
	}
}