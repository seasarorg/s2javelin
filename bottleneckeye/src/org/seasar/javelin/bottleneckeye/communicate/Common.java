package org.seasar.javelin.bottleneckeye.communicate;

/**
 * 基本的な共通機能を提供する
 */
public class Common 
{

    /** 電文種別（状態通知） */
	public static final byte BYTE_TELEGRAM_KIND_ALERT = 0;

	/** 電文種別（状態取得） */
	public static final byte BYTE_TELEGRAM_KIND_GET = 1;
	
	/** 電文種別（リセット） */
	public static final byte BYTE_TELEGRAM_KIND_RESET = 2;

	/** 電文種別（リソース通知） */
    public static final byte BYTE_TELEGRAM_KIND_RESOURCENOTIFY = 3;

    /** 電文種別（設定変更） */
    public static final byte BYTE_TELEGRAM_KIND_CONFIGCHANGE = 4;

    /** 電文種別（機能呼び出し） */
    public static final byte BYTE_TELEGRAM_KIND_FUNCTIONCALL = 5;
    
    /** 電文種別(JVNログダウンロード) */
	public static final byte BYTE_TELEGRAM_KIND_JVN_FILE = 6;

    /** 電文種別(サーバプロパティ設定取得) */
	public static final byte BYTE_TELEGRAM_KIND_GET_PROPERTY = 7;

    /** 電文種別(サーバプロパティ設定更新) */
	public static final byte BYTE_TELEGRAM_KIND_UPDATE_PROPERTY = 8;
    
    /**
     * 電文種別(JVNログ一覧取得)
     */
    public static final byte BYTE_TELEGRAM_KIND_JVN_FILE_LIST = 9;
    
    /** 要求応答種別（通知） */
	public static final byte BYTE_REQUEST_KIND_NOTIFY = 0;

	/** 要求応答種別（要求） */
	public static final byte BYTE_REQUEST_KIND_REQUEST = 1;

	/** 要求応答種別（応答） */
	public static final byte BYTE_REQUEST_KIND_RESPONSE = 2;

    /** オブジェクト名（リソース通知でのリソース値） */
	public static final String OBJECTNAME_RESOURCE = "resources";

    /** オブジェクト名（機能呼び出しでの接続管理） */
	public static final String OBJECTNAME_CONNECTIONMANAGER = "connectionManager";

	/** オブジェクト名（機能呼び出しでの表示操作） */
	public static final String OBJECTNAME_VIEWOPERATION = "viewOperation";

    /** オブジェクト名（JVNファイル） */
    public static final String OBJECTNAME_JVN_FILE = "jvnFile";

    /** 項目名（リソース値での取得時刻） */
    public static final String ITEMNAME_ACQUIREDTIME = "acquiredTime";

    /** 項目名（リソース値でのCPU時間） */
    public static final String ITEMNAME_CPUTIME = "cpuTime";

    /** 項目名（リソース値でのJava稼働時間） */
    public static final String ITEMNAME_JAVAUPTIME = "javaUpTime";

    /** 項目名（リソース値でのプロセッサ数） */
    public static final String ITEMNAME_PROCESSORCOUNT = "processorCount";

    /** 項目名（リソース値でのヒープメモリコミット容量） */
    public static final String ITEMNAME_HEAPMEMORYCOMMITTED = "heapMemoryCommitted";

    /** 項目名（リソース値でのヒープメモリ使用量） */
    public static final String ITEMNAME_HEAPMEMORYUSED = "heapMemoryUsed";

    /** 項目名（リソース値でのヒープメモリ最大） */
    public static final String ITEMNAME_HEAPMEMORYMAX = "heapMemoryMax";

    /** 項目名（リソース値でのヒープ以外のメモリコミット容量） */
    public static final String ITEMNAME_NONHEAPMEMORYCOMMITTED = "nonHeapMemoryCommitted";

    /** 項目名（リソース値でのヒープ以外のメモリ使用量） */
    public static final String ITEMNAME_NONHEAPMEMORYUSED = "nonHeapMemoryUsed";

    /** 項目名（リソース値でのヒープ以外のメモリ最大） */
    public static final String ITEMNAME_NONHEAPMEMORYMAX = "nonHeapMemoryMax";

    /** 項目名（リソース値での仮想メモリ容量） */
    public static final String ITEMNAME_VIRTUALMEMORYSIZE = "virtualMemorySize";

    /** 項目名（リソース値での仮想マシンメモリ容量） */
    public static final String ITEMNAME_VIRTUALMACHINEMEMORYCAPACITY = "virtualMachineMemoryCapacity";

    /** 項目名（リソース値での仮想マシンメモリ空き容量） */
    public static final String ITEMNAME_VIRTUALMACHINEMEMORYFREE = "virtualMachineMemoryFree";

    /** 項目名（リソース値での物理メモリ容量） */
    public static final String ITEMNAME_PHYSICALMEMORYCAPACITY = "physicalMemoryCapacity";

    /** 項目名（リソース値での物理メモリ空き容量） */
    public static final String ITEMNAME_PHYSICALMEMORYFREE = "physicalMemoryFree";

    /** 項目名（リソース値でのスワップ領域容量） */
    public static final String ITEMNAME_SWAPSPACECAPACITY = "swapSpaceCapacity";

    /** 項目名（リソース値でのスワップ領域空き容量） */
    public static final String ITEMNAME_SWAPSPACEFREE = "swapSpaceFree";

    /** 項目名（リソース値での、プロセス全体のネットワークデータ受信量） */
    public static final String ITEMNAME_NETWORKINPUTSIZEOFPROCESS = "networkInputSizeOfProcess";

    /** 項目名（リソース値での、プロセス全体のネットワークデータ送信量） */
    public static final String ITEMNAME_NETWORKOUTPUTSIZEOFPROCESS = "networkOutputSizeOfProcess";

    /** 項目名（リソース値での、ファイル入力量） */
    public static final String ITEMNAME_FILEINPUTSIZEOFPROCESS = "fileInputSizeOfProcess";

    /** 項目名（リソース値での、ファイル出力量） */
    public static final String ITEMNAME_FILEOUTPUTSIZEOFPROCESS = "fileOutputSizeOfProcess";

    /** 項目名（リソース値での、スレッド数） */
    public static final String ITEMNAME_THREADCOUNT = "threadCount";

    /** 項目名（リソース値での、トータルのガベージコレクションの時間） */
    public static final String ITEMNAME_GARBAGETOTALTIME = "garbageTotalTime";

    /** 項目名（リソース値での、コレクションの数） */
    public static final String ITEMNAME_LISTCOUNT = "listCount";

    /** 項目名（リソース値での、コレクションの数） */
    public static final String ITEMNAME_QUEUECOUNT = "queueCount";

    /** 項目名（リソース値での、コレクションの数） */
    public static final String ITEMNAME_SETCOUNT = "setCount";

    /** 項目名（リソース値での、コレクションの数） */
    public static final String ITEMNAME_MAPCOUNT = "mapCount";

    /** 項目名（JVNファイル JVNファイル名） */
    public static final String ITEMNAME_JVN_FILE_NAME = "jvnFileName";

    /** 項目名（JVNファイル JVNファイル内容） */
    public static final String ITEMNAME_JVN_FILE_CONTENT = "jvnFileContent";

}
