package org.seasar.javelin.communicate.entity;


/**
 * 電文の応答本体データクラス
 */
public class ResponseBody extends Body
{
	/**
	 * 項目型
	 */
	private byte byteItemMode_ = 0;

	/**
	 * 繰り返し回数
	 */
	private int intLoopCount_ = 1;

	/**
	 * 説明
	 */
	private Object[] objItemValueArr_ = null;
	
	/**
	 * 項目型取得
	 */
	public byte getByteItemMode() {
		return byteItemMode_;
	}

	/**
	 * 項目型設定
	 */
	public void setByteItemMode(byte byteItemMode_) {
		this.byteItemMode_ = byteItemMode_;
	}

	/**
	 * 繰り返し回数取得
	 */
	public int getIntLoopCount() {
		return intLoopCount_;
	}

	/**
	 * 繰り返し回数設定
	 */
	public void setIntLoopCount(int intLoopCount_) {
		this.intLoopCount_ = intLoopCount_;
	}

	/**
	 * 説明取得
	 */
	public Object[] getObjItemValueArr() {
		return objItemValueArr_;
	}

	/**
	 * 説明設定
	 */
	public void setObjItemValueArr(Object[] objItemValueArr_) {
		this.objItemValueArr_ = objItemValueArr_;
	}
}