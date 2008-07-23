package org.seasar.javelin;

import org.seasar.javelin.util.JavelinConfigUtil;

/**
 * S2StatsJavelinの設定を保持するクラス。
 * @author eriguchi
 */
public class S2JavelinConfig
{

    /** スレッドモデルの値：スレッドID */
    public static final int      TM_THREAD_ID                = 1;

    /** スレッドモデルの値：スレッド名 */
    public static final int      TM_THREAD_NAME              = 2;

    /** スレッドモデルの値： */
    public static final int      TM_CONTEXT_PATH             = 3;

    /** Javelin系パラメータの接頭辞 */
    public static final String   JAVELIN_PREFIX              = "javelin.";

    /** メソッド平均時間を出力するために記録するInvocation数のプロパティ */
    public static final String   INTERVALMAX_KEY             = JAVELIN_PREFIX + "intervalMax";

    /** 例外の数を記録するためのInvocation数のプロパティ */
    public static final String   THROWABLEMAX_KEY            = JAVELIN_PREFIX + "throwableMax";

    /** メモリに保存する閾値のプロパティ */
    public static final String   STATISTICSTHRESHOLD_KEY     = JAVELIN_PREFIX + "statistics"
                                                                     + "Threshold";

    /** ファイルに出力するTATの閾値のプロパティ */
    public static final String   RECORDTHRESHOLD_KEY         = JAVELIN_PREFIX + "recordThreshold";

    /** アラームを通知するTATの閾値のプロパティ */
    public static final String   ALARMTHRESHOLD_KEY          = JAVELIN_PREFIX + "alarmThreshold";

    /** Javelinログを出力するファイル名のプロパティ */
    public static final String   JAVELINFILEDIR_KEY          = JAVELIN_PREFIX + "javelinFileDir";

    /** BottleneckEyeに利用するドメインのプロパティ */
    public static final String   DOMAIN_KEY                  = JAVELIN_PREFIX + "domain";

    /** スタックトレースを出力するかどうかを決定するプロパティ */
    public static final String   LOG_STACKTRACE_KEY          = JAVELIN_PREFIX + "log.stacktrace";

    /** 引数情報を出力するかどうかを決定するプロパティ */
    public static final String   LOG_ARGS_KEY                = JAVELIN_PREFIX + "log.args";

    /** JMXInfoを出力するかどうか決定するプロパティ */
    public static final String   LOG_MBEANINFO_KEY           = JAVELIN_PREFIX + "log.mbeaninfo";

    /** 端点で、JMXInfoを出力するかどうか決定するプロパティ */
    public static final String   LOG_MBEANINFO_ROOT_KEY      = JAVELIN_PREFIX + "log.mbeaninfo.root";

    /** 戻り値を出力するかどうかを決定するプロパティ */
    public static final String   LOG_RETURN_KEY              = JAVELIN_PREFIX + "log.return";

    /** 引数の詳細情報を出力するかどうかを決定するプロパティ */
    public static final String   ARGS_DETAIL_KEY             = JAVELIN_PREFIX + "log.args.detail";

    /** 戻り値の詳細情報を出力するかどうかを決定するプロパティ */
    public static final String   RETURN_DETAIL_KEY           = JAVELIN_PREFIX + "log.return.detail";

    /** 引数の詳細情報の深さを表すプロパティ */
    public static final String   ARGS_DETAIL_DEPTH_KEY       = JAVELIN_PREFIX + "log.args."
                                                                     + "detail.depth";

    /** 戻り値の詳細情報の深さを表すプロパティ */
    public static final String   RETURN_DETAIL_DEPTH_KEY     = JAVELIN_PREFIX + "log.return."
                                                                     + "detail.depth";

    /** 呼び出し元が不明のときに設定する名前のプロパティ */
    public static final String   ROOTCALLERNAME_KEY          = JAVELIN_PREFIX + "rootCallerName";

    /** 最も深い呼び出し先が不明のときに設定する名前のプロパティ */
    public static final String   ENDCALLEENAME_KEY           = JAVELIN_PREFIX + "endCalleeName";

    /** スレッドの名称の決定方法を表すプロパティ */
    public static final String   THREADMODEL_KEY             = JAVELIN_PREFIX + "threadModel";

    /** JMXのHTTPAdaptorを公開するポート番号を表すプロパティ */
    public static final String   HTTPPORT_KEY                = JAVELIN_PREFIX + "httpPort";

    /** StatsJavelinの待ちうけポートのプロパティ名 */
    public static final String   ACCEPTPORT_KEY              = JAVELIN_PREFIX + "acceptPort";

    /** Javelinのログ出力ON/OFF切替フラグのプロパティ名 */
    public static final String   JAVELINENABLE_KEY           = JAVELIN_PREFIX + "javelinEnable";

    /** 属性、戻り値情報の文字列長 */
    public static final String   STRINGLIMITLENGTH_KEY       = JAVELIN_PREFIX + "stringLimitLength";

    /** エラーログファイルのプロパティ名 */
    public static final String   SYSTEMLOG_KEY               = JAVELIN_PREFIX + "system.log";

    /** 利用するAlarmListener名 */
    public static final String   ALARM_LISTENERS_KEY         = JAVELIN_PREFIX + "alarmListeners";

    /** JMX通信による情報公開を行うかどうかを表すプロパティ名 */
    public static final String   RECORD_JMX_KEY              = JAVELIN_PREFIX + "record.jmx";

    /** jvnログファイルの最大数を表すプロパティ名 */
    public static final String   LOG_JVN_MAX_KEY             = JAVELIN_PREFIX + "log.jvn.max";

    /** jvnログファイルを圧縮したzipファイルの最大数を表すプロパティ名 */
    public static final String   LOG_ZIP_MAX_KEY             = JAVELIN_PREFIX + "log.zip.max";

    /** 記録条件判定クラス */
    public static final String   RECORDSTRATEGY_KEY          = JAVELIN_PREFIX + "recordStrategy";

    /** 利用するTelegramListener名 */
    public static final String   TELERAM_LISTENERS_KEY       = JAVELIN_PREFIX + "telegramListeners";

    /** Javelinのシステムログの最大ファイル数のキー */
    private static final String  SYSTEM_LOG_NUM_MAX_KEY      = JAVELIN_PREFIX
                                                                     + "system.log.num.max";

    /** Javelinのシステムログの最大ファイルサイズのキー */
    private static final String  SYSTEM_LOG_SIZE_MAX_KEY     = JAVELIN_PREFIX + "system.log."
                                                                     + "size.max";

    /** Javelinのシステムログのログレベルのキー */
    private static final String  SYSTEM_LOG_LEVEL_KEY        = JAVELIN_PREFIX + "system.log.level";

    /** MBeanManagerが持つ情報をシリアライズするファイル名 */
    public static final String   SERIALIZE_FILE_KEY          = JAVELIN_PREFIX + "serializeFile";

    /** 保存するCallTree数のプロパティ */
    private static final String  CALL_TREE_MAX_KEY           = JAVELIN_PREFIX + "call.tree.max";

    /** アプリケーション実行時の例外を出力するかどうかを決定するプロパティ */
    public static final String   RECORD_EXCEPTION_KEY        = JAVELIN_PREFIX + "recordException";

    /** アプリケーション実行時の例外を出力するかどうかを決定するプロパティ */
    public static final String   ALARM_EXCEPTION_KEY         = JAVELIN_PREFIX + "alarmException";

    /** 保存するCallTree数のデフォルト値 */
    private static final int     DEFAULT_CALL_TREE_MAX       = 1000000;

    /** メソッド平均時間を出力するために記録するInvocation数のデフォルト値 */
    private static final int     DEFAULT_INTERVALMAX         = 500;

    /** 例外の数を記録するためのInvocation数のデフォルト値 */
    private static final int     DEFAULT_THROWABLEMAX        = 500;

    /** メモリに保存する閾値のプロパティ */
    private static final long    DEFAULT_STATISTICSTHRESHOLD = 0;

    /** ファイルに出力するTATの閾値のプロパティ */
    private static final long    DEFAULT_RECORDTHRESHOLD     = 5000;

    /** アラームを通知するTATの閾値のプロパティ */
    private static final long    DEFAULT_ALARMTHRESHOLD      = 5000;

    /** Javelinログを出力するファイル名のプロパティ */
    private static final String  DEFAULT_JAVELINFILEDIR      = "../logs";

    /** BottleneckEyeに利用するドメインのデフォルト値 */
    private static final String  DEFAULT_DOMAIN              = "default";

    /** スタックトレースを出力するかどうかを決定するデフォルト値 */
    private static final boolean DEFAULT_LOG_STACKTRACE      = false;

    /** 引数情報を出力するかどうかを決定するデフォルト値 */
    private static final boolean DEFAULT_LOG_ARGS            = true;

    /** JMXInfoを出力するかどうか決定するデフォルト値 */
    private static final boolean DEFAULT_LOG_MBEANINFO       = true;

    /** JMXInfoを出力するかどうか決定するデフォルト値 */
    private static final boolean DEFAULT_LOG_MBEANINFO_ROOT  = true;

    /** 戻り値を出力するかどうかを決定するデフォルト値 */
    private static final boolean DEFAULT_LOG_RETURN          = true;

    /** 引数の詳細情報を出力するかどうかを決定するデフォルト値 */
    private static final boolean DEFAULT_ARGS_DETAIL         = false;

    /** 戻り値の詳細情報を出力するかどうかを決定するデフォルト値 */
    private static final boolean DEFAULT_RETURN_DETAIL       = false;

    /** 引数の詳細情報の深さのデフォルト値 */
    private static final int     DEFAULT_ARGS_DETAIL_DEPTH   = 1;

    /** 戻り値の詳細情報の深さのデフォルト値 */
    private static final int     DEFAULT_RETURN_DETAIL_DEPTH = 1;

    /** 呼び出し元が不明のときに設定する名前のプロパティ */
    private static final String  DEFAULT_ROOTCALLERNAME      = "root";

    /** 最も深い呼び出し先が不明のときに設定する名前のプロパティ */
    private static final String  DEFAULT_ENDCALLEENAME       = "unknown";

    /** スレッドの名称の決定方法を表すプロパティ */
    private static final int     DEFAULT_THREADMODEL         = 0;

    /** JMXのHTTPAdaptorを公開するポート番号を表すプロパティ */
    private static final int     DEFAULT_HTTPPORT            = 0;

    /** 属性、戻り値情報の文字列長のデフォルト値 */
    private static final int     DEFAULT_STRINGLIMITLENGTH   = 102400;

    /** Javelinログを出力するかどうかのデフォルト設定 */
    public static final boolean  DEFAULT_JAVELINENABLE       = false;

    /** 待ちうけポート番号のデフォルト値 */
    public static final int      DEFAULT_ACCEPTPORT          = 18000;

    /** Javelin実行エラーメッセージの出力先パスのデフォルト値 */
    public static final String   DEFAULT_SYSTEMLOG           = "../traces";

    /** デフォルトで利用するAlarmListener名 */
    private static final String  DEFAULT_ALARM_LISTENERS     = "org.seasar.javelin.communicate.JmxListener";

    /** デフォルトでJMX通信による情報公開を行うかどうか */
    private static final boolean DEFAULT_RECORD_JMX          = true;

    /** jvnログファイルの最大数のデフォルト */
    private static final int     DEFAULT_LOG_JVN_MAX         = 256;

    /** jvnログファイルを圧縮したzipファイルの最大数のデフォルト */
    private static final int     DEFAULT_LOG_ZIP_MAX         = 256;

    /** 記録条件判定クラスのデフォルト */
    public static final String   DEFAULT_RECORDSTRATEGY      = "org.seasar.javelin.S2DefaultRecordStrategy";

    /** デフォルトで利用するTelegramListener名 */
    private static final String  DEFAULT_TELEGEAM_LISTENERS  = "org.seasar.javelin.communicate.GetRequestTelegramListener,"
                                                                     + "org.seasar.javelin.communicate.ResetRequestTelegramListener";

    /** Javelinのシステムログの最大ファイル数のデフォルト */
    private static final int     DEFAULT_SYSTEM_LOG_NUM_MAX  = 16;

    /** Javelinのシステムログの最大ファイルサイズのデフォルト */
    private static final int     DEFAULT_SYSTEM_LOG_SIZE_MAX = 1000000;

    /** MBeanManagerが持つ情報をシリアライズするファイル名のデフォルト */
    public static final String   DEFAULT_SERIALIZE_FILE      = "../data/serialize.dat";

    /** Javelinのシステムログのログレベルのデフォルト */
    private static final String  DEFAULT_SYSTEM_LOG_LEVEL    = "INFO";

    /** アプリケーション実行時の例外時にログ出力するデフォルト値 */
    private static final boolean DEFAULT_RECORD_EXCEPTION    = true;

    /** アプリケーション実行時の例外時にアラーム通知するデフォルト値 */
    private static final boolean DEFAULT_ALARM_EXCEPTION     = true;

    /**
     * S2StatsJavelinの設定を保持するオブジェクトを作成する。
     */
    public S2JavelinConfig()
    {
        // 何もしない
    }

    /**
     * 呼び出し情報を記録する際の閾値を返す。
     *
     * @return 閾値（ミリ秒）
     */
    public long getAlarmThreshold()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getLong(ALARMTHRESHOLD_KEY, DEFAULT_ALARMTHRESHOLD);
    }

    /**
     * 呼び出し情報を記録する際の閾値が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetAlarmThreshold()
    {
        return isKeyExist(ALARMTHRESHOLD_KEY);
    }

    /**
     * 呼び出し情報を記録する際の閾値を返す。
     *
     * @param alarmThreshold 閾値（ミリ秒）
     */
    public void setAlarmThreshold(long alarmThreshold)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setLong(ALARMTHRESHOLD_KEY, alarmThreshold);
    }

    /**
     * JMX通信による情報公開を行うかどうかの設定を返す。
     *
     * @return JMX通信による情報公開を行うならtrue
     */
    public boolean isRecordJMX()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(RECORD_JMX_KEY, DEFAULT_RECORD_JMX);
    }

    /**
     * JMX通信による情報公開を行うかどうかを設定する。
     *
     * @param isRecordJMX JMX通信による情報公開を行うならtrue
     */
    public void setRecordJMX(boolean isRecordJMX)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(RECORD_JMX_KEY, isRecordJMX);
    }

    /**
     * ドメイン名を返す。
     *
     * @return ドメイン名
     */
    public String getDomain()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(DOMAIN_KEY, DEFAULT_DOMAIN);
    }

    /**
     * ドメイン名が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetDomain()
    {
        return isKeyExist(DOMAIN_KEY);
    }

    /**
     * ドメイン名をセットする。
     *
     * @param domain ドメイン名
     */
    public void setDomain(String domain)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setString(DOMAIN_KEY, domain);
    }

    /**
     * 呼び出し情報を記録する最大件数を返す。
     *
     * @return 件数
     */
    public int getIntervalMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(INTERVALMAX_KEY, DEFAULT_INTERVALMAX);
    }

    /**
     * 呼び出し情報を記録する最大件数が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetIntervalMax()
    {
        return isKeyExist(INTERVALMAX_KEY);
    }

    /**
     * 呼び出し情報を記録する最大件数をセットする。
     *
     * @param intervalMax 件数
     */
    public void setIntervalMax(int intervalMax)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(INTERVALMAX_KEY, intervalMax);
    }

    /**
     * Javelinログファイルの出力先を取得する。
     *
     * @return 出力先パス
     */
    public String getJavelinFileDir()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        String relativePath = configUtil.getString(JAVELINFILEDIR_KEY, DEFAULT_JAVELINFILEDIR);
        return configUtil.convertRelativePathtoAbsolutePath(relativePath);
    }

    /**
     * Javelinログファイルの出力先が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetJavelinFileDir()
    {
        return isKeyExist(JAVELINFILEDIR_KEY);
    }

    /**
     * Javelinログファイルの出力先をセットする。
     *
     * @param javelinFileDir 出力先パス
     */
    public void setJavelinFileDir(String javelinFileDir)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setString(JAVELINFILEDIR_KEY, javelinFileDir);
    }

    /**
     * 呼び出し情報を記録する際の閾値
     *
     * @return 閾値（ミリ秒）
     */
    public long getRecordThreshold()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getLong(RECORDTHRESHOLD_KEY, DEFAULT_RECORDTHRESHOLD);
    }

    /**
     * 呼び出し情報を記録する際の閾値が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetRecordThreshold()
    {
        return isKeyExist(RECORDTHRESHOLD_KEY);
    }

    /**
     * 呼び出し情報を記録する際の閾値をセットする。
     *
     * @param recordThreshold 閾値
     */
    public void setRecordThreshold(long recordThreshold)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setLong(RECORDTHRESHOLD_KEY, recordThreshold);
    }

    /**
     * 例外の発生履歴を記録する最大件数を返す。
     *
     * @return 件数
     */
    public int getThrowableMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(THROWABLEMAX_KEY, DEFAULT_THROWABLEMAX);
    }

    /**
     * 呼び出し情報を記録する際の閾値が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetThrowableMax()
    {
        return isKeyExist(THROWABLEMAX_KEY);
    }

    /**
     * 例外の発生履歴を記録する最大件数をセットする。
     *
     * @param throwableMax 件数
     */
    public void setThrowableMax(int throwableMax)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(THROWABLEMAX_KEY, throwableMax);
    }

    /**
     * 呼び出し先につける名称を返す。
     *
     * @return 名称
     */
    public String getEndCalleeName()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(ENDCALLEENAME_KEY, DEFAULT_ENDCALLEENAME);
    }

    /**
     * 呼び出し先につける名称が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetEndCalleeName()
    {
        return isKeyExist(ENDCALLEENAME_KEY);
    }

    /**
     * 呼び出し先につける名称をセットする。
     *
     * @param endCalleeName 名称
     */
    public void setEndCalleeName(String endCalleeName)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setString(ENDCALLEENAME_KEY, endCalleeName);
    }

    /**
     * 呼び出し元につける名称を返す。
     *
     * @return 名称
     */
    public String getRootCallerName()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(ROOTCALLERNAME_KEY, DEFAULT_ROOTCALLERNAME);
    }

    /**
     * 呼び出し元につける名称が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetRootCallerName()
    {
        return isKeyExist(ROOTCALLERNAME_KEY);
    }

    /**
     * 呼び出し元につける名称をセットする。
     *
     * @param rootCallerName 名称
     */
    public void setRootCallerName(String rootCallerName)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setString(ROOTCALLERNAME_KEY, rootCallerName);
    }

    /**
     * スタックトレースを出力するかどうかの設定を返す。
     *
     * @return スタックトレースを出力するならtrue
     */
    public boolean isLogStacktrace()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(LOG_STACKTRACE_KEY, DEFAULT_LOG_STACKTRACE);
    }

    /**
     * スタックトレースを出力するかどうかが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetLogStacktrace()
    {
        return isKeyExist(LOG_STACKTRACE_KEY);
    }

    /**
     * スタックトレースを出力するかどうかを設定する。
     *
     * @param isLogStacktrace スタックトレースを出力するならtrue
     */
    public void setLogStacktrace(boolean isLogStacktrace)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_STACKTRACE_KEY, isLogStacktrace);
    }

    /**
     * 引数を出力するかどうかの設定を返す。
     *
     * @return 引数を出力するならtrue
     */
    public boolean isLogArgs()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(LOG_ARGS_KEY, DEFAULT_LOG_ARGS);
    }

    /**
     * MBeanによって取得した情報を出力するかどうかの設定を返す。
     *
     * @return MBeanによって取得した情報を出力するならtrue
     */
    public boolean isLogMBeanInfo()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(LOG_MBEANINFO_KEY, DEFAULT_LOG_MBEANINFO);
    }

    /**
     * 端点で、MBeanによって取得した情報を出力するかどうかの設定を返す。
     *
     * @return 端点で、MBeanによって取得した情報を出力するならtrue
     */
    public boolean isLogMBeanInfoRoot()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(LOG_MBEANINFO_ROOT_KEY, DEFAULT_LOG_MBEANINFO_ROOT);
    }

    /**
     * 引数を出力するかどうかが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetLogArgs()
    {
        return isKeyExist(LOG_ARGS_KEY);
    }

    /**
     * 引数を出力するかどうかを設定する。
     *
     * @param isLogArgs 引数を出力するならtrue
     */
    public void setLogArgs(boolean isLogArgs)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_ARGS_KEY, isLogArgs);
    }

    /**
     * MBeanによって取得した情報を出力するかどうかを設定する。
     *
     * @param isLogMBeanInfo MBeanによって取得した情報を出力するならtrue
     */
    public void setLogMBeanInfo(boolean isLogMBeanInfo)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_MBEANINFO_KEY, isLogMBeanInfo);
    }

    /**
     * MBeanによって取得した情報（ルートノード）を出力するかどうかを設定する。
     *
     * @param isLogMBeanInfo MBeanによって取得した情報を出力するならtrue
     */
    public void setLogMBeanInfoRoot(boolean isLogMBeanInfo)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_MBEANINFO_ROOT_KEY, isLogMBeanInfo);
    }

    /**
     * 戻り値を出力するかどうかの設定を返す。
     *
     * @return 戻り値を出力するならtrue
     */
    public boolean isLogReturn()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(LOG_RETURN_KEY, DEFAULT_LOG_RETURN);
    }

    /**
     * 戻り値を出力するかどうかが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetLogReturn()
    {
        return isKeyExist(LOG_RETURN_KEY);
    }

    /**
     * 戻り値を出力するかどうかを設定する。
     *
     * @param isLogReturn 戻り値を出力するならtrue
     */
    public void setLogReturn(boolean isLogReturn)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_RETURN_KEY, isLogReturn);
    }

    /**
     * 引数の詳細を出力するかどうかの設定を返す。
     *
     * @return 引数の詳細を出力するならtrue
     */
    public boolean isArgsDetail()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(ARGS_DETAIL_KEY, DEFAULT_ARGS_DETAIL);
    }

    /**
     * 引数の詳細を出力するかどうかが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetArgsDetail()
    {
        return isKeyExist(ARGS_DETAIL_KEY);
    }

    /**
     * 引数の詳細を出力するかどうかを設定する。
     *
     * @param isArgsDetail 引数の詳細を出力するならtrue
     */
    public void setArgsDetail(boolean isArgsDetail)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(ARGS_DETAIL_KEY, isArgsDetail);
    }

    /**
     * 戻り値の詳細を出力するかどうかの設定を返す。
     *
     * @return 戻り値の詳細を出力するならtrue
     */
    public boolean isReturnDetail()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(RETURN_DETAIL_KEY, DEFAULT_RETURN_DETAIL);
    }

    /**
     * 戻り値の詳細を出力するかどうかが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetReturnDetail()
    {
        return isKeyExist(RETURN_DETAIL_KEY);
    }

    /**
     * 戻り値の詳細を出力するかどうかを設定する。
     *
     * @param isReturnDetail 戻り値の詳細を出力するならtrue
     */
    public void setReturnDetail(boolean isReturnDetail)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(RETURN_DETAIL_KEY, isReturnDetail);
    }

    /**
     * 引数の詳細を出力する階層数の設定を返す。
     *
     * @return 引数の詳細を出力する階層数
     */
    public int getArgsDetailDepth()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(ARGS_DETAIL_DEPTH_KEY, DEFAULT_ARGS_DETAIL_DEPTH);
    }

    /**
     * 引数の詳細を出力する階層数が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetArgsDetailDepth()
    {
        return isKeyExist(ARGS_DETAIL_DEPTH_KEY);
    }

    /**
     * 引数の詳細を出力する階層数を設定する。
     *
     * @param detailDepth 引数の詳細を出力する階層数
     */
    public void setArgsDetailDepth(int detailDepth)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(ARGS_DETAIL_DEPTH_KEY, detailDepth);
    }

    /**
     * 戻り値の詳細を出力する階層数の設定を返す。
     *
     * @return 詳細を出力する階層数
     */
    public int getReturnDetailDepth()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(RETURN_DETAIL_DEPTH_KEY, DEFAULT_RETURN_DETAIL_DEPTH);
    }

    /**
     * 戻り値の詳細を出力する階層数が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetReturnDetailDepth()
    {
        return isKeyExist(RETURN_DETAIL_DEPTH_KEY);
    }

    /**
     * 戻り値の詳細を出力する階層数を設定する。
     *
     * @param returnDetailDepth 戻り値の詳細を出力する階層数
     */
    public void setReturnDetailDepth(int returnDetailDepth)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(RETURN_DETAIL_DEPTH_KEY, returnDetailDepth);
    }

    /**
     * スレッドモデルを返す。
     *
     * @return スレッドモデル
     */
    public int getThreadModel()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(THREADMODEL_KEY, DEFAULT_THREADMODEL);
    }

    /**
     * スレッドモデルが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetThreadModel()
    {
        return isKeyExist(THREADMODEL_KEY);
    }

    /**
     * スレッドモデルをセットする。
     *
     * @param threadModel スレッドモデル
     */
    public void setThreadModel(int threadModel)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(THREADMODEL_KEY, threadModel);
    }

    /**
     * HTTPポートを返す。
     *
     * @return HTTPポート
     */
    public int getHttpPort()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(HTTPPORT_KEY, DEFAULT_HTTPPORT);
    }

    /**
     * HTTPポートが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetHttpPort()
    {
        return isKeyExist(HTTPPORT_KEY);
    }

    /**
     * HTTPポートをセットする
     *
     * @param httpPort HTTPポート
     */
    public void setHttpPort(int httpPort)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(HTTPPORT_KEY, httpPort);
    }

    /**
     * キーに対応する値がセットされているかどうかを調べる。
     *
     * @param key キー
     * @return 値がセットされていればtrue
     */
    private boolean isKeyExist(String key)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.isKeyExist(key);
    }

    /**
     * メモリに保存する閾値を取得する。
     * @return メモリに保存する閾値
     */
    public long getStatisticsThreshold()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getLong(STATISTICSTHRESHOLD_KEY, DEFAULT_STATISTICSTHRESHOLD);
    }

    /**
     * メモリに保存する閾値を設定する。
     * @param statisticsThreshold メモリに保存する閾値
     */
    public void setStatisticsThreshold(long statisticsThreshold)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setLong(STATISTICSTHRESHOLD_KEY, statisticsThreshold);
    }

    /**
     * ログに出力するArgsの長さの閾値を取得する。
     * @return Argsの長さの閾値
     */
    public int getStringLimitLength()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(STRINGLIMITLENGTH_KEY, DEFAULT_STRINGLIMITLENGTH);
    }

    /**
     * ログに出力するArgsの長さの閾値を設定する。
     * @param stringLimitLength Argsの長さの閾値
     */
    public void setStringLimitLength(int stringLimitLength)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setLong(STRINGLIMITLENGTH_KEY, stringLimitLength);
    }

    /**
     * Javelinログを出力するかどうかの設定を返す。
     *
     * @return ログを出力するならtrue
     */
    public boolean isJavelinEnable()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(JAVELINENABLE_KEY, DEFAULT_JAVELINENABLE);
    }

    /**
     * 待ちうけポート番号を返す。
     *
     * @return ポート番号
     */
    public int getAcceptPort()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(ACCEPTPORT_KEY, DEFAULT_ACCEPTPORT);
    }

    /**
     * Javelinシステムログの出力先ディレクトリを返す。
     *
     * @return Javelinシステムログの出力先ディレクトリ。
     */
    public String getSystemLog()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        String relativePath = configUtil.getString(SYSTEMLOG_KEY, DEFAULT_SYSTEMLOG);
        return configUtil.convertRelativePathtoAbsolutePath(relativePath);
    }

    /**
     * 利用するAlarmListener名を返す。
     * ","区切りで複数指定することができる。
     *
     * @return 利用するAlarmListener名
     */
    public String getAlarmListeners()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(ALARM_LISTENERS_KEY, DEFAULT_ALARM_LISTENERS);
    }

    /**
     * 利用するAlarmListener名が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetAlarmListeners()
    {
        return isKeyExist(ALARM_LISTENERS_KEY);
    }

    /**
     * 利用するAlarmListener名をセットする。
     * ","区切りで複数指定することができる。
     *
     * @param alarmListeners 利用するAlarmListener名
     */
    public void setAlarmListeners(String alarmListeners)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setString(ALARM_LISTENERS_KEY, alarmListeners);
    }

    /**
     * ログサイズの最大値を取得する。
     * @return ログサイズの最大値
     */
    public int getLogJvnMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(LOG_JVN_MAX_KEY, DEFAULT_LOG_JVN_MAX);
    }

    /**
     * Zip化するログのファイル数を取得する。
     * @return Zip化するログのファイル数
     */
    public int getLogZipMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(LOG_ZIP_MAX_KEY, DEFAULT_LOG_ZIP_MAX);
    }

    /**
     * ログをZip化するかどうかを返す。
     * @return true:ログをZip化する、false:ログをZip化しない。
     */
    public boolean isLogZipMax()
    {
        return isKeyExist(LOG_ZIP_MAX_KEY);
    }

    /**
     * 記録条件判定クラス名を返す
     *
     * @return クラス名
     */
    public String getRecordStrategy()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(RECORDSTRATEGY_KEY, DEFAULT_RECORDSTRATEGY);
    }

    /**
     * 記録条件判定クラス名が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isRecordStrategy()
    {
        return isKeyExist(RECORDSTRATEGY_KEY);
    }

    /**
     * 利用するTelegramListener名を返す。
     * ","区切りで複数指定することができる。
     *
     * @return 利用するTelegramListener名
     */
    public String getTelegramListeners()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(TELERAM_LISTENERS_KEY, DEFAULT_TELEGEAM_LISTENERS);
    }

    /**
     * 利用するTelegramListener名が設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetTelegramListener()
    {
        return isKeyExist(TELERAM_LISTENERS_KEY);
    }

    /**
     * Javelinのシステムログの最大ファイル数を取得する。
     *
     * @return Javelinのシステムログの最大ファイル数。
     */
    public int getSystemLogNumMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(SYSTEM_LOG_NUM_MAX_KEY, DEFAULT_SYSTEM_LOG_NUM_MAX);
    }

    /**
     * MBeanManagerが持つ情報をシリアライズするファイル名を返す。
     *
     * @return 利用するファイル名
     */
    public String getSerializeFile()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        String relativePath = configUtil.getString(SERIALIZE_FILE_KEY, DEFAULT_SERIALIZE_FILE);
        return configUtil.convertRelativePathtoAbsolutePath(relativePath);
    }

    /**
     * MBeanManagerが持つ情報をシリアライズするファイル名が設定されているかどうかを調べる。
     *
     * @return 利用するファイル名
     */
    public boolean isSetSerializeFile()
    {
        return isKeyExist(SERIALIZE_FILE_KEY);
    }

    /**
     * Javelinのシステムログの最大ファイルサイズを取得する。
     *
     * @return Javelinのシステムログの最大ファイルサイズ。
     */
    public int getSystemLogSizeMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(SYSTEM_LOG_SIZE_MAX_KEY, DEFAULT_SYSTEM_LOG_SIZE_MAX);
    }

    /**
     * システムログのレベルを取得する。
     * @return システムログのレベル
     */
    public String getSystemLogLevel()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(SYSTEM_LOG_LEVEL_KEY, DEFAULT_SYSTEM_LOG_LEVEL);
    }

    /**
     * CallTreeの最大値を取得する。
     * @return CallTreeの最大値
     */
    public int getCallTreeMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(CALL_TREE_MAX_KEY, DEFAULT_CALL_TREE_MAX);
    }

    /**
     * CallTreeの最大値を設定する。
     * @param callTreeMax CallTreeの最大値
     */
    public void setCallTreeMax(int callTreeMax)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(CALL_TREE_MAX_KEY, callTreeMax);
    }

    /**
     * アプリケーション実行時に例外をログに出力するかどうか。
     * @return true:ログに出力、false:ログに出力しない。
     */
    public boolean isRecordException()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(RECORD_EXCEPTION_KEY, DEFAULT_RECORD_EXCEPTION);
    }

    /**
     * アプリケーション実行時に例外をログに出力するかどうか設定する。
     *
     * @param isRecordException 例外をログに出力するならtrue
     */
    public void setRecordException(boolean isRecordException)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(RECORD_EXCEPTION_KEY, isRecordException);
    }

    /**
     * アプリケーション実行時に例外をアラーム通知するかどうか。
     * @return true:アラーム通知する、false:アラーム通知しない。
     */
    public boolean isAlarmException()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(ALARM_EXCEPTION_KEY, DEFAULT_ALARM_EXCEPTION);
    }

    /**
     * アプリケーション実行時に例外をアラーム通知するかどうか設定する。
     *
     * @param isAlarmException 例外をアラーム通知するならtrue
     */
    public void setAlarmException(boolean isAlarmException)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(ALARM_EXCEPTION_KEY, isAlarmException);
    }

}
