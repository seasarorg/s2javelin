package org.seasar.javelin.communicate.entity;


/**
 * “d•¶‚Ì‰“š–{‘Ìƒf[ƒ^ƒNƒ‰ƒX
 */
public class ResponseBody extends Body
{
	/**
	 * €–ÚŒ^
	 */
	private byte byteItemMode_ = 0;

	/**
	 * ŒJ‚è•Ô‚µ‰ñ”
	 */
	private int intLoopCount_ = 1;

	/**
	 * à–¾
	 */
	private Object[] objItemValueArr_ = null;
	
	/**
	 * €–ÚŒ^æ“¾
	 */
	public byte getByteItemMode() {
		return byteItemMode_;
	}

	/**
	 * €–ÚŒ^İ’è
	 */
	public void setByteItemMode(byte byteItemMode_) {
		this.byteItemMode_ = byteItemMode_;
	}

	/**
	 * ŒJ‚è•Ô‚µ‰ñ”æ“¾
	 */
	public int getIntLoopCount() {
		return intLoopCount_;
	}

	/**
	 * ŒJ‚è•Ô‚µ‰ñ”İ’è
	 */
	public void setIntLoopCount(int intLoopCount_) {
		this.intLoopCount_ = intLoopCount_;
	}

	/**
	 * à–¾æ“¾
	 */
	public Object[] getObjItemValueArr() {
		return objItemValueArr_;
	}

	/**
	 * à–¾İ’è
	 */
	public void setObjItemValueArr(Object[] objItemValueArr_) {
		this.objItemValueArr_ = objItemValueArr_;
	}
}