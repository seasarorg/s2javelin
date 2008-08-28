package org.seasar.javelin;

import java.io.IOException;
import java.io.PrintStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * S2JavelinFilter
 * 
 * @author eriguchi
 * 
 */
public class S2JavelinFilter implements Filter
{

    /** 引数の数. */
    private static final int    ARGS_NUM                = 6;

    /** ドメイン */
    private static final String PNAME_DOMAIN            = "domain";

    /** メモリ中に蓄積する(メソッド毎の)呼び出し情報の件数 */
    private static final String PNAME_INTERVAL_MAX      = "intervalMax";

    /** メモリ中に蓄積する(メソッド毎の)例外発生情報の件数 */
    private static final String PNAME_THROWABLE_MAX     = "throwableMax";

    /** ファイル出力を行うターン・アラウンド・タイムの閾値(単位:ミリ秒) */
    private static final String PNAME_RECORD_THRESHOLD  = "recordThreshold";

    /** BottleneckEyeへの通知を行うCPUタイムの閾値(単位:ミリ秒) */
    private static final String PNAME_ALARM_THRESHOLD   = "alarmThreshold";

    /** jvnファイルを出力するディレクトリへのパス。 */
    private static final String PNAME_JAVELIN_DIR       = "javelinFileDir";

    /** メソッド呼び出しの引数情報を出力する。 */
    private static final String PNAME_IS_LOG_ARGS       = "log.args";

    /** メソッド呼び出しの戻り値情報を出力する。 */
    private static final String PNAME_IS_LOG_RETURN     = "log.return";

    /** メソッド呼び出しの時のスタックトレースを出力する。 */
    private static final String PNAME_IS_LOG_STACKTRACE = "log.stacktrace";

    /** メソッド呼び出しのルートノードにつける名前。 */
    private static final String PNAME_ROOT_CALLER_NAME  = "rootCallerName";

    /** メソッド呼び出しのエンドノードの名前が決定できない場合につける名前。 */
    private static final String PNAME_END_CALLEE_NAME   = "endCalleeName";

    /**
     * スレッドの名称の決定方法 0:スレッド名@スレッドID(スレッドクラス名@オブジェクトID) 1:スレッドID 2:スレッド名
     * 3:メソッド名（Servletフィルタ組み込み時にはサーブレットパスとなる。）
     */
    private static final String PNAME_THREAD_MODEL      = "threadModel";

    /** S2Javelinの設定値 */
    private S2JavelinConfig     config_;

    /**
     * 初期化。
     * 
     * @param config web.xmlの設定
     * @throws ServletException
     */
    public void init(FilterConfig config)
    {
        this.config_ = new S2JavelinConfig();

        String domain = getInitParameter(config, PNAME_DOMAIN);
        if (this.config_.isSetDomain() == false && domain != null && domain.trim().length() > 0)
        {
            this.config_.setDomain(domain);
        }

        // web.xmlよりも設定ファイル（*.conf）の方を優先する
        if (this.config_.isSetAlarmThreshold() == false
                && isParameterIntegerValue(config, PNAME_INTERVAL_MAX))
        {
            this.config_.setIntervalMax(getInitParameterInteger(config, PNAME_INTERVAL_MAX));
        }
        if (this.config_.isSetThrowableMax() == false
                && isParameterIntegerValue(config, PNAME_THROWABLE_MAX))
        {
            this.config_.setThrowableMax(getInitParameterInteger(config, PNAME_THROWABLE_MAX));
        }
        if (this.config_.isSetRecordThreshold() == false
                && isParameterIntegerValue(config, PNAME_RECORD_THRESHOLD))
        {
            this.config_.setRecordThreshold(getInitParameterInteger(config, PNAME_RECORD_THRESHOLD));
        }
        if (this.config_.isSetAlarmThreshold() == false
                && isParameterIntegerValue(config, PNAME_ALARM_THRESHOLD))
        {
            this.config_.setAlarmThreshold(getInitParameterInteger(config, PNAME_ALARM_THRESHOLD));
        }
        if (this.config_.isSetJavelinFileDir() == false
                && isParameterStringValue(config, PNAME_JAVELIN_DIR))
        {
            String javelinFileDir = getInitParameter(config, PNAME_JAVELIN_DIR);
            if (javelinFileDir.trim().length() > 0)
            {
                this.config_.setJavelinFileDir(javelinFileDir);
            }
        }
        if (this.config_.isSetLogArgs() == false
                && isParameterBooleanValue(config, PNAME_IS_LOG_ARGS))
        {
            this.config_.setLogArgs(getInitParameterBoolean(config, PNAME_IS_LOG_ARGS));
        }
        if (this.config_.isSetLogReturn() == false
                && isParameterBooleanValue(config, PNAME_IS_LOG_RETURN))
        {
            this.config_.setLogReturn(getInitParameterBoolean(config, PNAME_IS_LOG_RETURN));
        }
        if (this.config_.isSetLogStacktrace() == false
                && isParameterBooleanValue(config, PNAME_IS_LOG_STACKTRACE))
        {
            this.config_.setLogStacktrace(getInitParameterBoolean(config, PNAME_IS_LOG_STACKTRACE));
        }
        if (this.config_.isSetRootCallerName() == false
                && isParameterBooleanValue(config, PNAME_ROOT_CALLER_NAME))
        {
            this.config_.setRootCallerName(getInitParameter(config, PNAME_ROOT_CALLER_NAME));
        }
        if (this.config_.isSetEndCalleeName() == false
                && isParameterBooleanValue(config, PNAME_END_CALLEE_NAME))
        {
            this.config_.setEndCalleeName(getInitParameter(config, PNAME_END_CALLEE_NAME));
        }
        if (this.config_.isSetThreadModel() == false
                && isParameterIntegerValue(config, PNAME_THREAD_MODEL))
        {
            this.config_.setThreadModel(getInitParameterInteger(config, PNAME_THREAD_MODEL));
        }

        synchronized (S2StatsJavelinRecorder.class)
        {
            S2StatsJavelinRecorder.javelinInit(this.config_);
        }

        printConfigValue();
    }

    /**
     * 指定されたオプションの値が文字列かどうかを調べる。
     * 
     * @param config フィルタ設定
     * @param name オプション名
     * @return 値が文字列ならtrue
     */
    private boolean isParameterStringValue(FilterConfig config, String name)
    {
        String configValue = config.getInitParameter(name);
        return (configValue != null);
    }

    /**
     * 指定されたオプションの値が数値かどうかを調べる。
     * 
     * @param config フィルタ設定
     * @param name オプション名
     * @return 値が数値ならtrue
     */
    private boolean isParameterIntegerValue(FilterConfig config, String name)
    {
        String configValue = config.getInitParameter(name);
        if (configValue == null)
        {
            return false;
        }
        try
        {
            Integer.parseInt(configValue);
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
        return true;
    }

    /**
     * 指定されたオプションの値がブール値かどうかを調べる。
     * 
     * @param config フィルタ設定
     * @param name オプション名
     * @return 値がブール値ならtrue
     */
    private boolean isParameterBooleanValue(FilterConfig config, String name)
    {
        String configValue = config.getInitParameter(name);
        // 
        if (configValue != null)
        {
            return true;
        }
        return false;
    }

    /**
     * パラメータを取得し、int型にして返す。
     * @param config Javelinの設定値
     * @param pname パラメータ名
     * @return パラメータ
     */
    private int getInitParameterInteger(FilterConfig config, String pname)
    {
        String configValue = config.getInitParameter(pname);
        return Integer.parseInt(configValue);
    }

    /**
     * パラメータを取得し、boolean型にして返す。
     * @param config Javelinの設定値
     * @param pname パラメータ名
     * @return パラメータ
     */
    private boolean getInitParameterBoolean(FilterConfig config, String pname)
    {
        String configValue = config.getInitParameter(pname);
        return Boolean.valueOf(configValue);
    }

    /**
     * サーブレットフィルタの設定から、指定されたパラメータの値を取得する。
     * 
     * @param config サーブレットフィルタの設定。
     * @param pname 取得したいパラメータの名称。
     * @return パラメータの値。
     */
    private String getInitParameter(FilterConfig config, String pname)
    {
        String value = config.getInitParameter(pname);
        return value;
    }

    /**
     * フィルタリング処理。 サーブレットの処理時間を計測し、各種処理を行う。
     * 
     * @param request リクエスト
     * @param response レスポンス
     * @param chain フィルターチェイン
     * @throws IOException 入出力例外
     * @throws ServletException サーブレット例外
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException,
            ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;

        try
        {
            // ThreadLocalから取得したCallTreeに対して呼び出し構造を保存する。
            CallTree callTree = S2JavelinCommonPool.getCallTree();
            if (callTree != null)
            {
                S2StatsJavelinRecorder.initCallTree(callTree);
            }

            /**
             * ThreadLocalからクラス名、メソッド名を呼び出す。 クラス名、メソッド名が存在しない場合、 Http
             * Requestのコンテキストパス、サーブレットパスを呼び出す。
             */
            String contextPath = S2JavelinCommonPool.getClassName();
            String servletPath = S2JavelinCommonPool.getMethodName();
            if (contextPath == null || servletPath == null)
            {
                servletPath = httpRequest.getPathInfo();
                if (servletPath == null)
                {
                    contextPath = httpRequest.getContextPath();
                    servletPath = httpRequest.getServletPath();
                }
                else
                {
                    contextPath = httpRequest.getContextPath() + httpRequest.getServletPath();
                }

                contextPath = httpRequest.getContextPath();
                servletPath = httpRequest.getServletPath();
            }

            Object[] args = null;
            if (this.config_.isLogArgs())
            {
                args = new Object[ARGS_NUM];
                args[0] = httpRequest.getRemoteHost();
                args[1] = httpRequest.getRemotePort();
                args[2] = contextPath;
                args[3] = servletPath;
                args[4] = httpRequest.getMethod();
                args[5] = httpRequest.getParameterMap();
            }

            StackTraceElement[] stacktrace = null;
            if (this.config_.isLogStacktrace())
            {
                stacktrace = Thread.currentThread().getStackTrace();
            }

            S2StatsJavelinRecorder.preProcess(contextPath, servletPath, args, stacktrace,
                                              this.config_);
            // S2StatsJavelinRecorder.preProcessField(contextPath, servletPath,
            // config_);
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }

        try
        {
            // ==================================================
            // メソッド呼び出し。
            chain.doFilter(request, response);
            // ==================================================
        }
        catch (IOException ex)
        {
            // S2StatsJavelinRecorder.postProcessField(this.config_);
            S2StatsJavelinRecorder.postProcess(ex, this.config_);
            throw ex;
        }
        catch (ServletException ex)
        {
            // S2StatsJavelinRecorder.postProcessField(this.config_);
            S2StatsJavelinRecorder.postProcess(ex, this.config_);
            throw ex;
        }
        catch (RuntimeException ex)
        {
            // S2StatsJavelinRecorder.postProcessField(this.config_);
            S2StatsJavelinRecorder.postProcess(ex, this.config_);
            throw ex;
        }
        catch (Error error)
        {
            // S2StatsJavelinRecorder.postProcessField(this.config_);
            S2StatsJavelinRecorder.postProcess(error, this.config_);
            throw error;
        }

        try
        {
            Object returnValue = null;
            if (this.config_.isLogReturn())
            {
                returnValue = createReturnValue(httpResponse);
            }

            // S2StatsJavelinRecorder.postProcessField(this.config_);
            S2StatsJavelinRecorder.postProcess(returnValue, this.config_);
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }
    }

    /**
     * HttpServletResponseから、戻り値を生成する。
     * 
     * @param response サーブレットレスポンス。
     * @return 戻り値。
     */
    private Object createReturnValue(HttpServletResponse response)
    {
        String returnValue;
        returnValue = response.getContentType();
        return returnValue;
    }

    /**
     * {non-javadoc}
     */
    public void destroy()
    {
        // 何もしない
    }

    /**
     * 設定値を標準出力に出力する。
     */
    private void printConfigValue()
    {
        PrintStream out = System.out;
        out.println(">>>> Properties related with S2JavelinFilter");
        out.println("\tjavelin.intervalMax               : " + this.config_.getIntervalMax());
        out.println("\tjavelin.throwableMax              : " + this.config_.getThrowableMax());
        out.println("\tjavelin.statisticsThreshold       : " + this.config_.getStatisticsThreshold());
        out.println("\tjavelin.recordThreshold           : " + this.config_.getRecordThreshold());
        out.println("\tjavelin.alarmThreshold            : " + this.config_.getAlarmThreshold());
        out.println("\tjavelin.javelinFileDir            : " + this.config_.getJavelinFileDir());
        out.println("\tjavelin.domain                    : " + this.config_.getDomain());
        out.println("\tjavelin.recordException           : " + this.config_.isRecordException());
        out.println("\tjavelin.alarmException            : " + this.config_.isAlarmException());
        out.println("\tjavelin.log.stacktrace            : " + this.config_.isLogStacktrace());
        out.println("\tjavelin.log.args                  : " + this.config_.isLogArgs());
        out.println("\tjavelin.log.jmxinfo               : " + this.config_.isLogMBeanInfo());
        out.println("\tjavelin.log.jmxinfo.root          : " + this.config_.isLogMBeanInfoRoot());
        out.println("\tjavelin.log.return                : " + this.config_.isLogReturn());
        out.println("\tjavelin.log.return.detail         : " + this.config_.isReturnDetail());
        out.println("\tjavelin.log.return.detail.depth   : " + this.config_.getReturnDetailDepth());
        out.println("\tjavelin.log.args.detail           : " + this.config_.isArgsDetail());
        out.println("\tjavelin.log.args.detail.depth     : " + this.config_.getArgsDetailDepth());
        out.println("\tjavelin.rootCallerName            : " + this.config_.getRootCallerName());
        out.println("\tjavelin.endCalleeName             : " + this.config_.getEndCalleeName());
        out.println("\tjavelin.threadModel               : " + this.config_.getThreadModel());
        out.println("\tjavelin.acceptPort                : " + this.config_.getAcceptPort());
        out.println("\tjavelin.stringLimitLength         : " + this.config_.getStringLimitLength());
        out.println("\tjavelin.system.log                : " + this.config_.getSystemLog());
        out.println("\tjavelin.log.jvn.max               : " + this.config_.getLogJvnMax());
        out.println("\tjavelin.log.zip.max               : " + this.config_.getLogZipMax());
        out.println("\tjavelin.system.log.num.max        : " + this.config_.getSystemLogNumMax());
        out.println("\tjavelin.system.log.size.max       : " + this.config_.getSystemLogSizeMax());
        out.println("\tjavelin.system.log.level          : " + this.config_.getSystemLogLevel());
        out.println("\tjavelin.call.tree.max             : " + this.config_.getCallTreeMax());
        out.println("\tjavelin.record.jmx                : " + this.config_.isRecordJMX());
        out.println("\tjavelin.recordStrategy            : " + this.config_.getRecordStrategy());
        out.println("\tjavelin.alarmListeners            : " + this.config_.getAlarmListeners());
        out.println("\tjavelin.telegramListeners         : " + this.config_.getTelegramListeners());
        out.println("\tjavelin.serializeFile             : " + this.config_.getSerializeFile());
        out.println("\tjavelin.record.invocation.num.max : " + this.config_.getRecordInvocationMax());
        out.println("<<<<");
    }
}
