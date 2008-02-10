package org.seasar.javelin;

import org.seasar.javelin.util.JavelinConfigUtil;

/**
 * S2StatsJavelinの設定を保持するクラス。
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

    public static final String  INTERVALMAX_KEY             = JAVELIN_PREFIX + "intervalMax";

    public static final String  THROWABLEMAX_KEY            = JAVELIN_PREFIX + "throwableMax";

    public static final String  STATISTICSTHRESHOLD_KEY     = JAVELIN_PREFIX
                                                                     + "statisticsThreshold";

    public static final String  RECORDTHRESHOLD_KEY         = JAVELIN_PREFIX + "recordThreshold";

    public static final String  ALARMTHRESHOLD_KEY          = JAVELIN_PREFIX + "alarmThreshold";

    public static final String  JAVELINFILEDIR_KEY          = JAVELIN_PREFIX + "javelinFileDir";

    public static final String  DOMAIN_KEY                  = JAVELIN_PREFIX + "domain";

    public static final String  LOG_STACKTRACE_KEY          = JAVELIN_PREFIX + "log.stacktrace";

    public static final String  LOG_ARGS_KEY                = JAVELIN_PREFIX + "log.args";

    public static final String  LOG_JMXNFO_KEY              = JAVELIN_PREFIX + "log.jmxinfo";

    public static final String  LOG_RETURN_KEY              = JAVELIN_PREFIX + "log.return";

    public static final String  ARGS_DETAIL_KEY             = JAVELIN_PREFIX + "log.args.detail";

    public static final String  RETURN_DETAIL_KEY           = JAVELIN_PREFIX + "log.return.detail";

    public static final String  ARGS_DETAIL_DEPTH_KEY       = JAVELIN_PREFIX
                                                                     + "log.args.detail.depth";

    public static final String  RETURN_DETAIL_DEPTH_KEY     = JAVELIN_PREFIX
                                                                     + "log.return.detail.depth";

    public static final String  ROOTCALLERNAME_KEY          = JAVELIN_PREFIX + "rootCallerName";

    public static final String  ENDCALLEENAME_KEY           = JAVELIN_PREFIX + "endCalleeName";

    public static final String  THREADMODEL_KEY             = JAVELIN_PREFIX + "threadModel";

    public static final String  HTTPPORT_KEY                = JAVELIN_PREFIX + "httpPort";

    public static final String  DEBUG_KEY                   = JAVELIN_PREFIX + "debug";

    /** StatsJavelinの待ちうけポートのプロパティ名 */
    public static final String   ACCEPTPORT_KEY              = JAVELIN_PREFIX + "acceptPort";

    /** Javelinのログ出力ON/OFF切替フラグのプロパティ名 */
    public static final String  JAVELINENABLE_KEY           = JAVELIN_PREFIX + "javelinEnable";

    /** 属性、戻り値情報の文字列長 */
    public static final String  STRINGLIMITLENGTH_KEY       = JAVELIN_PREFIX + "stringLimitLength";

    /** エラーログファイルのプロパティ名 */
    public static final String   ERRORLOG_KEY                = JAVELIN_PREFIX + "error.log";

    /** 利用するAlarmListener名 */
    public static final String  ALARM_LISTENERS_KEY         = JAVELIN_PREFIX + "alarmListeners";

    /** JMX通信による情報公開を行うかどうかを表すプロパティ名 */
    public static final String  RECORD_JMX_KEY              = JAVELIN_PREFIX + "record.jmx";

    /** jvnログファイルの最大数を表すプロパティ名 */
    public static final String  LOG_JVN_MAX_KEY             = JAVELIN_PREFIX + "log.jvn.max";

    /** jvnログファイルを圧縮したzipファイルの最大数を表すプロパティ名 */
    public static final String  LOG_ZIP_MAX_KEY             = JAVELIN_PREFIX + "log.zip.max";

    /** 記録条件判定クラス */
    public static final String  RECORDSTRATEGY_KEY         = JAVELIN_PREFIX + "recordStrategy";

    /** 利用するTelegramListener名 */
    public static final String  TELERAM_LISTENERS_KEY         = JAVELIN_PREFIX + "telegramListeners";

    private static final int     DEFAULT_INTERVALMAX         = 1000;

    private static final int     DEFAULT_THROWABLEMAX        = 1000;

    private static final long    DEFAULT_STATISTICSTHRESHOLD = 0;

    private static final long    DEFAULT_RECORDTHRESHOLD     = 0;

    private static final long    DEFAULT_ALARMTHRESHOLD      = 1000;

    private static final String  DEFAULT_JAVELINFILEDIR      = System.getProperty("java.io.tmpdir");

    private static final String  DEFAULT_DOMAIN              = "org.seasar.javelin.jmx.default";

    private static final boolean DEFAULT_LOG_STACKTRACE      = false;

    private static final boolean DEFAULT_LOG_ARGS            = true;

    private static final boolean DEFAULT_LOG_JMXINFO         = true;

    private static final boolean DEFAULT_LOG_RETURN          = true;

    private static final boolean DEFAULT_ARGS_DETAIL         = false;

    private static final boolean DEFAULT_RETURN_DETAIL       = false;

    private static final int     DEFAULT_ARGS_DETAIL_DEPTH   = 1;

    private static final int     DEFAULT_RETURN_DETAIL_DEPTH = 1;

    private static final String  DEFAULT_ROOTCALLERNAME      = "unknown";

    private static final String  DEFAULT_ENDCALLEENAME       = "unknown";

    private static final int     DEFAULT_THREADMODEL         = 1;

    private static final int     DEFAULT_HTTPPORT            = 0;

    private static final boolean DEFAULT_DEBUG               = false;

    private static final int     DEFAULT_STRINGLIMITLENGTH   = 1024;

    /** Javelinログを出力するかどうかのデフォルト設定 */
    public static final boolean  DEFAULT_JAVELINENABLE       = false;

    /** 待ちうけポート番号のデフォルト値 */
    public static final int      DEFAULT_ACCEPTPORT          = 32000;

    /** Javelin実行エラーメッセージの出力先パスのデフォルト値 */
    public static final String   DEFAULT_ERRORLOG            = "log/error.log";

    /** デフォルトで利用するAlarmListener名 */
    private static final String  DEFAULT_ALARM_LISTENERS     = "org.seasar.javelin.communicate.JmxListener";

    /** デフォルトでJMX通信による情報公開を行うかどうか */
    private static final boolean DEFAULT_RECORD_JMX          = true;

    /** jvnログファイルの最大数のデフォルト */
    private static final int     DEFAULT_LOG_JVN_MAX         = 256;

    /** jvnログファイルを圧縮したzipファイルの最大数のデフォルト */
    private static final int     DEFAULT_LOG_ZIP_MAX         = 256;

    /** 記録条件判定クラスのデフォルト */
    private static final String  DEFAULT_RECORDSTRATEGY      = "org.seasar.javelin.DefaultRecordStrategy";
    
    /** デフォルトで利用するTelegramListener名 */
    private static final String  DEFAULT_TELEGEAM_LISTENERS
        = "org.seasar.javelin.communicate.GetRequestTelegramListener,"
    	+ "org.seasar.javelin.communicate.ResetRequestTelegramListener";

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
     * @param  JMX通信による情報公開を行うならtrue
     */
    public void setRecordJMX(boolean isLogStacktrace)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_STACKTRACE_KEY, isLogStacktrace);
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
        return configUtil.getString(JAVELINFILEDIR_KEY, DEFAULT_JAVELINFILEDIR);
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
     * JMXによって取得した情報を出力するかどうかの設定を返す。
     *
     * @return JMXによって取得した情報を出力するならtrue
     */
    public boolean isLogJmxInfo()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(LOG_JMXNFO_KEY, DEFAULT_LOG_JMXINFO);
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
     * JMXによって取得した情報を出力するかどうかを設定する。
     *
     * @param isLogJmxInfo JMXによって取得した情報を出力するならtrue
     */
    public void setLogJmxInfo(boolean isLogJmxInfo)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_JMXNFO_KEY, isLogJmxInfo);
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
     * Debugモードが設定されているかどうかを調べる。
     *
     * @return 設定されていればtrue
     */
    public boolean isSetDebug()
    {
        return isKeyExist(DEBUG_KEY);
    }

    /**
     * Debugモードをセットする
     *
     * @param isDebug Debugモード
     */
    public void setDebug(boolean isDebug)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(DEBUG_KEY, isDebug);
    }

    /**
     * Debugモードを返す。
     *
     * @return Debugモード
     */
    public boolean isDebug()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        boolean result = DEFAULT_DEBUG;
        try
        {
            result = configUtil.getBoolean(DEBUG_KEY, DEFAULT_DEBUG);
        }
        catch (NumberFormatException nfe)
        {
            result = DEFAULT_DEBUG;
            this.setDebug(result);
        }
        return result;
    }

    public long getStatisticsThreshold()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getLong(STATISTICSTHRESHOLD_KEY, DEFAULT_STATISTICSTHRESHOLD);
    }

    public void setStatisticsThreshold(long statisticsThreshold)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setLong(STATISTICSTHRESHOLD_KEY, statisticsThreshold);
    }

    public int getStringLimitLength()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(STRINGLIMITLENGTH_KEY, DEFAULT_STRINGLIMITLENGTH);
    }

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
     * Javelin実行エラーメッセージの出力先パスを返す。
     *
     * @return パス
     */
    public String getErrorLog()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(ERRORLOG_KEY, DEFAULT_ERRORLOG);
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

    public int getLogJvnMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(LOG_JVN_MAX_KEY, DEFAULT_LOG_JVN_MAX);
    }

    public int getLogZipMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(LOG_ZIP_MAX_KEY, DEFAULT_LOG_ZIP_MAX);
    }

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
}
