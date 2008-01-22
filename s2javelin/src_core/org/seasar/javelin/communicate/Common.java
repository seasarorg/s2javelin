package org.seasar.javelin.communicate;


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
	 * 電文種別(アラーム)
	 */
	public static final byte BYTE_TELEGRAM_KIND_ALERT = 0;

	/**
	 * 電文種別(状態取得)
	 */
	public static final byte BYTE_TELEGRAM_KIND_GET = 1;
	
	/**
	 * 電文種別(リセット)
	 */
	public static final byte BYTE_TELEGRAM_KIND_RESET = 2;

	/**
	 * 要求応答種別(通知)
	 */
	public static final byte BYTE_REQUEST_KIND_NOTIFY = 0;


	/**
	 * 要求応答種別(要求)
	 */
	public static final byte BYTE_REQUEST_KIND_REQUEST = 1;

	/**
	 * 要求応答種別(応答)
	 */
	public static final byte BYTE_REQUEST_KIND_RESPONSE = 2;

	
	public static final byte BYTE_ITEMMODE_KIND_8BYTE_INT = 3;
	
	public static final byte BYTE_ITEMMODE_KIND_STRING = 6;
	
	public static final int INT_LOOP_COUNT_SINGLE = 1;

}