package org.seasar.javelin.bottleneckeye.communicate;

/**
 * 電文データクラス
 */
public class Telegram
{
	/**
	 * 頭部
	 */
	private Header objHeader_ = null;
	
	/**
	 * 本体
	 */
	private Body[] objBody_ = null;
	
	/**
	 * 頭部取得
	 */
	public Header getObjHeader() {
		return objHeader_;
	}

	/**
	 * 頭部設定
	 */
	public void setObjHeader(Header objHeader_) {
		this.objHeader_ = objHeader_;
	}

	/**
	 * 本体取得
	 */
	public Body[] getObjBody() {
		return objBody_;
	}

	/**
	 * 本体設定
	 */
	public void setObjBody(Body[] objBody_) {
		this.objBody_ = objBody_;
	}
	
}