package org.seasar.javelin.communicate;

/**
 * 基本的な共通機能を提供する
 */
public class Common
{
    /**
     * int ⇔ byte[]　変換時に対応のバイト数
     */
    public static final int    INT_BYTE_SWITCH_LENGTH             = 4;

    /**
     * long ⇔ byte[]　変換時に対応のバイト数
     */
    public static final int    LONG_BYTE_SWITCH_LENGTH            = 8;

    /**
     * 電文種別(アラーム)
     */
    public static final byte   BYTE_TELEGRAM_KIND_ALERT           = 0;

    /**
     * 電文種別(状態取得)
     */
    public static final byte   BYTE_TELEGRAM_KIND_GET             = 1;

    /**
     * 電文種別(リセット)
     */
    public static final byte   BYTE_TELEGRAM_KIND_RESET           = 2;

    /**
     * 電文種別（リソース通知）
     */
    public static final byte   BYTE_TELEGRAM_KIND_RESOURCENOTIFY  = 3;

    /**
     * 電文種別（設定変更）
     */
    public static final byte   BYTE_TELEGRAM_KIND_CONFIGCHANGE    = 4;

    /**
     * 電文種別（機能呼び出し）
     */
    public static final byte   BYTE_TELEGRAM_KIND_FUNCTIONCALL    = 5;

    /**
     * 電文種別(JVNログ出力通知)
     */
    public static final byte   BYTE_TELEGRAM_KIND_JVN_FILE        = 6;

    /** 電文種別(サーバプロパティ取得) */
    public static final byte   BYTE_TELEGRAM_KIND_GET_PROPERTY    = 7;

    /** 電文種別(サーバプロパティ更新) */
    public static final byte   BYTE_TELEGRAM_KIND_UPDATE_PROPERTY = 8;

    /**
     * 電文種別(JVNログ一覧)
     */
    public static final byte   BYTE_TELEGRAM_KIND_JVN_FILE_LIST   = 9;

    /**
     * 要求応答種別(通知)
     */
    public static final byte   BYTE_REQUEST_KIND_NOTIFY           = 0;

    /**
     * 要求応答種別(要求)
     */
    public static final byte   BYTE_REQUEST_KIND_REQUEST          = 1;

    /**
     * 要求応答種別(応答)
     */
    public static final byte   BYTE_REQUEST_KIND_RESPONSE         = 2;

    /** オブジェクト名（JVNファイル） */
    public static final String OBJECTNAME_JVN_FILE                = "jvnFile";

    /** 項目名（JVNファイル JVNファイル名） */
    public static final String ITEMNAME_JVN_FILE_NAME             = "jvnFileName";

    /** 項目名（JVNファイル JVNファイル内容） */
    public static final String ITEMNAME_JVN_FILE_CONTENT          = "jvnFileContent";

    public static final byte   BYTE_ITEMMODE_KIND_8BYTE_INT       = 3;

    public static final byte   BYTE_ITEMMODE_KIND_STRING          = 6;

    public static final int    INT_LOOP_COUNT_SINGLE              = 1;

}