package org.seasar.javelin.statsvision.communicate;

/**
 * 基本的な共通機能を提供する
 */
public class Common 
{
	/**
	 * int ⇔ byte[]　変換時に対応のバイト数
	 */
	public static final int INT_BYTE_SWITCH_LENGTH = 4;
    
	/**
	 * long ⇔ byte[]　変換時に対応のバイト数
	 */
	public static final int LONG_BYTE_SWITCH_LENGTH = 8;

	/**
	 * 電文種別
	 */
	public static byte BYTE_TELEGRAM_KIND_ALERT = 0;

	/**
	 * 電文種別
	 */
	public static byte BYTE_TELEGRAM_KIND_GET = 1;
	
	/**
	 * 電文種別(リセット)
	 */
	public static byte BYTE_TELEGRAM_KIND_RESET = 2;
	
	/**
	 * 要求応答種別(通知)
	 */
	public static byte BYTE_REQUEST_KIND_NOTIFY = 0;


	/**
	 * 要求応答種別(要求)
	 */
	public static byte BYTE_REQUEST_KIND_REQUEST = 1;

	/**
	 * 要求応答種別(応答)
	 */
	public static byte BYTE_REQUEST_KIND_RESPONSE = 2;


	public static final byte BYTE_ITEMMODE_KIND_8BYTE_INT = 3;
	
	public static final byte BYTE_ITEMMODE_KIND_STRING = 6;
	
	public static final int INT_LOOP_COUNT_SINGLE = 1;
	/**
	 * 二つbyte[]　を　一つbyte[]　に纏める
	 */
    public static byte[] arrayAdd(byte[] byteBeforeArr,byte[] byteAfterArr)
    {
    	// 返却用
	    byte[] byteResultArr = null;
	    
	    int byteBeforeArrLength = 0;
	    int byteAfterArrLength = 0;
	    
	    // 前分　byte[]　のサイズを取得
	    if (byteBeforeArr != null)
	    	byteBeforeArrLength = byteBeforeArr.length;
	    
	    // 後分　byte[]　のサイズを取得
	    if (byteAfterArr != null)
	    	byteAfterArrLength = byteAfterArr.length;
	    
	    // 返却用　byte[]　を作る
	    if (byteBeforeArrLength + byteAfterArrLength > 0)
	    	byteResultArr = new byte[byteBeforeArrLength + byteAfterArrLength];
	    
	    // 前分　byte[]　を返却用　byte[]　に設定する
	    if (byteBeforeArrLength > 0)
	    	System.arraycopy(byteBeforeArr,0,byteResultArr,0,byteBeforeArrLength);
	    
	    // 後分　byte[]　を返却用　byte[]　に設定する
	    if (byteAfterArrLength > 0)
	    	System.arraycopy(byteAfterArr,0,byteResultArr,byteBeforeArrLength,byteAfterArrLength);
	    
	    // 返却する
	    return byteResultArr;
    }
    
	/**
	 * byte[] から、一分データを消す
	 */
    public static byte[] arrayDel(byte[] byteSoruceArr,int intDelCount)
    {
    	// 返却用
    	byte[] byteResultArr = new byte[byteSoruceArr.length - intDelCount];
    	
    	// 前の一分データを消す
    	System.arraycopy(byteSoruceArr,intDelCount,byteResultArr,0,byteResultArr.length);
    	
    	// 返却する
    	return byteResultArr;
    }

}