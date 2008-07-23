package org.seasar.javelin;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.framework.log.Logger;

/**
 * Javelinログを出力し、Component間の呼び出し関係を
 * MBeanとして公開するためのInterceptor。<br>
 * <br>
 * また、以下の情報を、MBean経由で取得することが可能。
 * <ol>
 * <li>メソッドの呼び出し回数</li>
 * <li>メソッドの平均処理時間（ミリ秒単位）</li>
 * <li>メソッドの最長処理時間（ミリ秒単位）</li>
 * <li>メソッドの最短処理時間（ミリ秒単位）</li>
 * <li>メソッドの呼び出し元</li>
 * <li>例外の発生回数</li>
 * <li>例外の発生履歴</li>
 * </ol>
 * また、以下の条件をdiconファイルでのパラメータで指定可能。
 * <ol>
 * <li>httpPort:HTTPで公開する際のポート番号。使用にはMX4Jが必要。</li>
 * <li>intervalMax:メソッドの処理時間を何回分記録するか。
 *     デフォルト値は1000。</li>
 * <li>throwableMax:例外の発生を何回分記録するか。
 *     デフォルト値は1000。</li>
 * <li>recordThreshold:処理時間記録用の閾値。
 *     この時間を越えたメソッド呼び出しのみ記録する。
 *     デフォルト値は0。</li>
 * <li>alarmThreshold:処理時間記録用の閾値。
 *     この時間を越えたメソッド呼び出しの発生をViwerに通知する。
 *     デフォルト値は1000。</li>
 * <li>domain:MBeanを登録する際に使用するドメイン。
 *     実際のドメイン名は、[domainパラメータ] + [Mbeanの種類]となる。
 *     MBeanの種類は以下のものがある。
 *     <ol>
 *       <li>container:全コンポーネントのObjectNameを管理する。</li>
 *       <li>component:一つのコンポーネントに関する情報を公開するMBean。</li>
 *       <li>invocation:メソッド呼び出しに関する情報を公開するMBean。</li>
 *     </ol>
 *     </li>
 * </ol>
 * 
 * @version 0.2
 * @author On Eriguchi (SMG), Tetsu Hayakawa (SMG)
 */
public class S2JavelinInterceptor extends AbstractInterceptor
{
    private static final long             serialVersionUID        = 6661781313519708185L;

    /**
     * メソッド呼び出し時にJavlinログの先頭に出力する識別子。
     */
    private static final String           CALL                    = "Call  ";

    /**
     * メソッド戻り時にJavlinログの先頭に出力する識別子。
     */
    private static final String           RETURN                  = "Return";

    /**
     * 例外throw時にJavlinログの先頭に出力する識別子。
     */
    private static final String           THROW                   = "Throw ";

    /**
     * 例外catch時にJavlinログの先頭に出力する識別子。
     */
    private static final String           CATCH                   = "Catch ";

    /** スタックトレースの深さ */
    private static final int              CALLER_STACKTRACE_INDEX = 3;

    /**
     * 改行文字。
     */
    private static final String           NEW_LINE                =
                                                                          System.getProperty("line.separator");

    /**
     * ログの区切り文字。
     */
    private static final String           DELIM                   = ",";

    /**
     * スレッド情報の区切り文字
     */
    private static final String           THREAD_DELIM            = "@";

    /**
     * ログ出力する時刻のフォーマット。
     */
    private static final String           TIME_FORMAT_STR         = "yyyy/MM/dd HH:mm:ss.SSS";

    /**
     * Javelinログに出力する時刻データ整形用フォーマッタ。
     */
    private ThreadLocal<DateFormat>       timeFormat_             = new ThreadLocal<DateFormat>() {
                                                                      protected synchronized DateFormat initialValue()
                                                                      {
                                                                          return new SimpleDateFormat(
                                                                                                      TIME_FORMAT_STR);
                                                                      }
                                                                  };

    /**
     * メソッドの呼び出し元オブジェクトを表す文字列。
     */
    private ThreadLocal<String>           callerLog_              = new ThreadLocal<String>() {
                                                                      protected synchronized String initialValue()
                                                                      {
                                                                          return "<unknown>,<unknown>,0";
                                                                      }
                                                                  };

    /**
     * Javelinログ出力用ロガー。
     */
    private static Logger                 logger_                 =
                                                                          Logger.getLogger(S2JavelinInterceptor.class);

    /**
     * 引数を出力するかどうか。
     * argsプロパティの値がセットされる。
     */
    private boolean                       isLogArgs_              = true;

    /**
     * 戻り値を出力するかどうか。
     * returnプロパティの値がセットされる。
     */
    private boolean                       isLogReturn_            = true;

    /**
     * スタックトレースを出力するかどうか。
     * stackTraceプロパティの値がセットされる。
     */
    private boolean                       isLogStackTrace_        = false;

    /**
     * 例外発生を記録するテーブル。
     * キーにスレッドの識別子、値に例外オブジェクトを入れる。
     * 例外が発生したらこのマップにその例外を登録し、
     * キャッチされた時点で削除する。
     */
    static private Map<String, Throwable> exceptionMap__          =
                                                                          Collections.synchronizedMap(new HashMap<String, Throwable>());

    private S2JavelinConfig               config_                 = new S2JavelinConfig();

    /** 設定値を標準出力に出力したらtrue */
    private boolean                       isPrintConfig_          = false;

    private boolean                       isInitialized_          = false;

    /**
     * Javelinログ出力用のinvokeメソッド。
     * 
     * 実際のメソッド呼び出しを実行する前後で、
     * 呼び出しと返却の詳細ログを、Javelin形式で出力する。
     * 実行時に例外が発生した場合は、その詳細もログ出力する。<br>
     * 
     * また、実際のメソッド呼び出しを実行する前後で、
     * 実行回数や実行時間をMBeanに記録する。
     * 
     * 実行時に例外が発生した場合は、
     * 例外の発生回数や発生履歴も記録する。
     * 
     * @param invocation インターセプタによって取得された、呼び出すメソッドの情報
     * @return invocationを実行したときの戻り値
     * @throws Throwable invocationを実行したときに発生した例外
     */
    public Object invoke(MethodInvocation invocation)
        throws Throwable
    {
        // 設定値を出力していなければ出力する
        synchronized (this)
        {
            if (this.isPrintConfig_ == false)
            {
                this.isPrintConfig_ = true;
                printConfigValue();
            }

            initialize();
        }

        // 呼び出し先情報取得。
        String calleeClassName = getTargetClass(invocation).getName();
        String calleeMethodName = invocation.getMethod().getName();

        try
        {
            // 呼び出し先情報取得。
            String className = calleeClassName;
            String methodName = calleeMethodName;

            StackTraceElement[] stacktrace = null;
            if (this.config_.isLogStacktrace())
            {
                stacktrace = Thread.currentThread().getStackTrace();
            }

            S2StatsJavelinRecorder.preProcess(className, methodName, invocation.getArguments(),
                                              stacktrace, this.config_);
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }

        String objectID = "";
        String threadInfo = "";

        StringBuffer methodCallBuff = new StringBuffer(256);
        // 呼び出し元情報取得。
        String currentCallerLog = "";

        if (this.config_.isJavelinEnable())
        {
            currentCallerLog = this.callerLog_.get();

            objectID = Integer.toHexString(System.identityHashCode(invocation.getThis()));
            String modifiers = Modifier.toString(invocation.getMethod().getModifiers());

            Thread currentThread = Thread.currentThread();
            String threadName = currentThread.getName();
            String threadClassName = currentThread.getClass().getName();
            String threadID = Integer.toHexString(System.identityHashCode(currentThread));

            threadInfo = threadName + THREAD_DELIM + threadClassName + THREAD_DELIM + threadID;

            // メソッド呼び出し共通部分生成。
            methodCallBuff.append(calleeMethodName);
            methodCallBuff.append(DELIM);
            methodCallBuff.append(calleeClassName);
            methodCallBuff.append(DELIM);
            methodCallBuff.append(objectID);

            // 呼び出し先のログを、次回ログ出力時の呼び出し元として使用するために保存する。
            this.callerLog_.set(methodCallBuff.toString());

            methodCallBuff.append(DELIM);
            methodCallBuff.append(currentCallerLog);
            methodCallBuff.append(DELIM);
            methodCallBuff.append(modifiers);
            methodCallBuff.append(DELIM);
            methodCallBuff.append(threadInfo);
            methodCallBuff.append(NEW_LINE);

            // Call 詳細ログ生成。
            StringBuffer callDetailBuff = createCallDetail(methodCallBuff, invocation);

            // Call ログ出力。
            logger_.debug(callDetailBuff);
        }

        //実際のメソッド呼び出しを実行する。
        //実行中に例外が発生したら、その詳細ログを出力する。
        Object ret = null;
        try
        {
            // メソッド呼び出し。
            ret = invocation.proceed();
        }
        catch (Throwable cause)
        {
            S2StatsJavelinRecorder.postProcess(cause, this.config_);

            if (this.config_.isJavelinEnable())
            {
                // Throw詳細ログ生成。
                StringBuffer throwBuff =
                        createThrowCatchDetail(THROW, calleeMethodName, calleeClassName, objectID,
                                               threadInfo, cause);

                // Throw ログ出力。
                logger_.debug(throwBuff);

                //例外発生を記録する。
                exceptionMap__.put(threadInfo, cause);
            }

            //例外をスローし、終了する。
            throw cause;
        }

        if (this.config_.isJavelinEnable())
        {
            //このスレッドで、直前に例外が発生していたかを確認する。
            //発生していてここにたどり着いたのであれば、この時点で例外が
            //catchされたということになるので、Catchログを出力する。
            boolean isExceptionThrowd = exceptionMap__.containsKey(threadInfo);
            if (isExceptionThrowd == true)
            {
                //発生していた例外オブジェクトをマップから取り出す。（かつ削除する）
                Throwable exception = exceptionMap__.remove(threadInfo);

                // Catch詳細ログ生成。
                StringBuffer throwBuff =
                        createThrowCatchDetail(CATCH, calleeMethodName, calleeClassName, objectID,
                                               threadInfo, exception);

                // Catch ログ出力。
                logger_.debug(throwBuff);
            }

            // Return詳細ログ生成。
            StringBuffer returnDetailBuff = createReturnDetail(methodCallBuff, ret);

            // Returnログ出力。
            logger_.debug(returnDetailBuff);

            this.callerLog_.set(currentCallerLog);
        }

        try
        {
            S2StatsJavelinRecorder.postProcess(ret, this.config_);
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }

        //invocationを実行した際の戻り値を返す。
        return ret;
    }

    /**
     * 初期化する。
     */
    private void initialize()
    {
        if (this.isInitialized_ == false)
        {
            if (this.config_.getHttpPort() != 0)
            {
                try
                {
                    Mx4JLauncher.execute(this.config_.getHttpPort());
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            this.isInitialized_ = true;
        }
    }

    /**
     * メソッド呼び出し（識別子Call）の詳細なログを生成する。
     * 
     * @param methodCallBuff メソッド呼び出しの文字列
     * @param invocation メソッド呼び出しの情報
     * @return メソッド呼び出しの詳細なログ
     */
    private StringBuffer createCallDetail(StringBuffer methodCallBuff, MethodInvocation invocation)
    {
        String timeStr = ((SimpleDateFormat)this.timeFormat_.get()).format(new Date());
        StringBuffer callDetailBuff = new StringBuffer(512);

        callDetailBuff.append(CALL);
        callDetailBuff.append(DELIM);
        callDetailBuff.append(timeStr);
        callDetailBuff.append(DELIM);
        callDetailBuff.append(methodCallBuff);

        // 引数のログを生成する。
        if (this.isLogArgs_ == true)
        {
            callDetailBuff.append("<<javelin.Args_START>>" + NEW_LINE);
            Object[] args = invocation.getArguments();
            if (args != null && args.length > 0)
            {
                for (int i = 0; i < args.length; ++i)
                {
                    callDetailBuff.append("    args[");
                    callDetailBuff.append(i);
                    callDetailBuff.append("] = ");
                    callDetailBuff.append(args[i]);
                    callDetailBuff.append(NEW_LINE);
                }
            }
            callDetailBuff.append("<<javelin.Args_END>>" + NEW_LINE);
        }

        // スタックトレースのログを生成する。
        if (this.isLogStackTrace_ == true)
        {
            callDetailBuff.append("<<javelin.StackTrace_START>>" + NEW_LINE);
            Throwable th = new Throwable();
            StackTraceElement[] stackTraceElements = th.getStackTrace();
            for (int index = CALLER_STACKTRACE_INDEX; index < stackTraceElements.length; index++)
            {
                callDetailBuff.append("    at ");
                callDetailBuff.append(stackTraceElements[index]);
                callDetailBuff.append(NEW_LINE);
            }

            callDetailBuff.append("<<javelin.StackTrace_END>>" + NEW_LINE);
        }

        if (invocation.getThis() instanceof Throwable)
        {
            callDetailBuff.append("<<javelin.Exception>>" + NEW_LINE);
        }
        return callDetailBuff;
    }

    /**
     * メソッドの戻り（識別子Return）の詳細なログを生成する。
     * 
     * @param methodCallBuff メソッド呼び出しの文字列
     * @param ret メソッドの戻り値
     * @return メソッドの戻りの詳細なログ
     */
    private StringBuffer createReturnDetail(StringBuffer methodCallBuff, Object ret)
    {
        StringBuffer returnDetailBuff = new StringBuffer(512);
        String returnTimeStr = ((SimpleDateFormat)this.timeFormat_.get()).format(new Date());
        returnDetailBuff.append(RETURN);
        returnDetailBuff.append(DELIM);
        returnDetailBuff.append(returnTimeStr);
        returnDetailBuff.append(DELIM);
        returnDetailBuff.append(methodCallBuff);

        // 戻り値のログを生成する。
        if (this.isLogReturn_)
        {
            returnDetailBuff.append("<<javelin.Return_START>>" + NEW_LINE);
            returnDetailBuff.append("    ");
            returnDetailBuff.append(ret);
            returnDetailBuff.append(NEW_LINE);
            returnDetailBuff.append("<<javelin.Return_END>>" + NEW_LINE);
        }
        return returnDetailBuff;
    }

    /**
     * 例外発生（識別子Throw）、または例外キャッチ（Catch）の詳細ログを生成する。
     * 
     * @param id 識別子。ThrowまたはCatch
     * @param calleeMethodName 呼び出し先メソッド名
     * @param calleeClassName 呼び出し先クラス名
     * @param objectID 呼び出し先クラスのオブジェクトID
     * @param threadInfo スレッド情報
     * @param cause 発生した例外
     * @return 例外発生の詳細なログ
     */
    private StringBuffer createThrowCatchDetail(String id, String calleeMethodName,
            String calleeClassName, String objectID, String threadInfo, Throwable cause)
    {
        String throwTimeStr = ((SimpleDateFormat)this.timeFormat_.get()).format(new Date());
        String throwableID = Integer.toHexString(System.identityHashCode(cause));
        StringBuffer throwBuff = new StringBuffer(512);
        throwBuff.append(id);
        throwBuff.append(DELIM);
        throwBuff.append(throwTimeStr);
        throwBuff.append(DELIM);
        throwBuff.append(cause.getClass().getName());
        throwBuff.append(DELIM);
        throwBuff.append(throwableID);
        throwBuff.append(DELIM);
        throwBuff.append(calleeMethodName);
        throwBuff.append(DELIM);
        throwBuff.append(calleeClassName);
        throwBuff.append(DELIM);
        throwBuff.append(objectID);
        throwBuff.append(DELIM);
        throwBuff.append(threadInfo);
        return throwBuff;
    }

    /**
     * 
     * @param intervalMax 平均実行時間を計算するための結果の最大数
     */
    public void setIntervalMax(int intervalMax)
    {
        if (this.config_.isSetIntervalMax() == false)
        {
            this.config_.setIntervalMax(intervalMax);
        }
    }

    /**
     * 
     * @param throwableMax Invocationに例外を保存する最大数
     */
    public void setThrowableMax(int throwableMax)
    {
        if (this.config_.isSetThrowableMax() == false)
        {
            this.config_.setThrowableMax(throwableMax);
        }
    }

    /**
     * 
     * @param recordThreshold ログ出力するTATの閾値
     */
    public void setRecordThreshold(int recordThreshold)
    {
        if (this.config_.isSetRecordThreshold() == false)
        {
            this.config_.setRecordThreshold(recordThreshold);
        }
    }

    /**
     * 
     * @param alarmThreshold アラームを通知するTATの閾値
     */
    public void setAlarmThreshold(int alarmThreshold)
    {
        if (this.config_.isSetAlarmThreshold() == false)
        {
            this.config_.setAlarmThreshold(alarmThreshold);
        }
    }

    /**
     * 
     * @param domain BottleneckEyeに利用するドメイン
     */
    public void setDomain(String domain)
    {
        if (this.config_.isSetDomain() == false)
        {
            this.config_.setDomain(domain);
        }
    }

    /**
     * StatsJavelinログを出力するディレクトリを指定する。
     *
     * @param javelinFileDir ディレクトリ名
     */
    public void setJavelinFileDir(String javelinFileDir)
    {
        if (this.config_.isSetJavelinFileDir() == false)
        {
            this.config_.setJavelinFileDir(javelinFileDir);
        }
    }

    /**
     * 
     * @param httpPort BottleneckEyeとの通信に利用するポート番号
     */
    public void setHttpPort(int httpPort)
    {
        if (this.config_.isSetHttpPort() == false)
        {
            this.config_.setHttpPort(httpPort);
        }
    }

    /**
     * 引数を出力するかどうかの設定を変更する。
     *
     * @param isLogArgs 引数を出力するならtrue
     */
    public void setLogArgs(boolean isLogArgs)
    {
        if (this.config_.isLogArgs() == false)
        {
            this.config_.setLogArgs(isLogArgs);
        }
    }

    /**
     * 戻り値を出力するかどうかの設定を変更する。
     *
     * @param isLogReturn 戻り値を出力するならtrue
     */
    public void setLogReturn(boolean isLogReturn)
    {
        if (this.config_.isLogReturn() == false)
        {
            this.config_.setLogArgs(isLogReturn);
        }
    }

    /**
     * スタックトレースを出力するかどうかの設定を変更する。
     *
     * @param isLogStacktrace スタックトレースを出力するならtrue
     */
    public void setLogStacktrace(boolean isLogStacktrace)
    {
        if (this.config_.isLogStacktrace() == false)
        {
            this.config_.setLogStacktrace(isLogStacktrace);
        }
    }

    /**
     * 
     * @param endCalleeName 末端の呼び出し先の名称
     */
    public void setEndCalleeName(String endCalleeName)
    {
        if (this.config_.isSetEndCalleeName() == false)
        {
            this.config_.setEndCalleeName(endCalleeName);
        }
    }

    /**
     * 
     * @param endCallerName 末端の呼び出し先の名称
     */
    public void setRootCallerName(String endCallerName)
    {
        if (this.config_.isSetRootCallerName() == false)
        {
            this.config_.setRootCallerName(endCallerName);
        }
    }

    /**
     * 
     * @param threadModel スレッド名称
     */
    public void setThreadModel(int threadModel)
    {
        if (this.config_.isSetThreadModel() == false)
        {
            this.config_.setThreadModel(threadModel);
        }
    }

    /**
     * 設定値を標準出力に出力する。
     */
    private void printConfigValue()
    {
        PrintStream out = System.out;
        out.println(">>>> Properties related with S2JavelinInterceptor");
        out.println("\tjavelin.intervalMax             : " + this.config_.getIntervalMax());
        out.println("\tjavelin.throwableMax            : " + this.config_.getThrowableMax());
        out.println("\tjavelin.statisticsThreshold     : " + this.config_.getStatisticsThreshold());
        out.println("\tjavelin.recordThreshold         : " + this.config_.getRecordThreshold());
        out.println("\tjavelin.alarmThreshold          : " + this.config_.getAlarmThreshold());
        out.println("\tjavelin.javelinFileDir          : " + this.config_.getJavelinFileDir());
        out.println("\tjavelin.recordException         : " + this.config_.isRecordException());
        out.println("\tjavelin.alarmException          : " + this.config_.isAlarmException());
        out.println("\tjavelin.domain                  : " + this.config_.getDomain());
        out.println("\tjavelin.log.stacktrace          : " + this.config_.isLogStacktrace());
        out.println("\tjavelin.log.args                : " + this.config_.isLogArgs());
        out.println("\tjavelin.log.jmxinfo             : " + this.config_.isLogMBeanInfo());
        out.println("\tjavelin.log.jmxinfo.root        : " + this.config_.isLogMBeanInfoRoot());
        out.println("\tjavelin.log.return              : " + this.config_.isLogReturn());
        out.println("\tjavelin.log.return.detail       : " + this.config_.isReturnDetail());
        out.println("\tjavelin.log.return.detail.depth : " + this.config_.getReturnDetailDepth());
        out.println("\tjavelin.log.args.detail         : " + this.config_.isArgsDetail());
        out.println("\tjavelin.log.args.detail.depth   : " + this.config_.getArgsDetailDepth());
        out.println("\tjavelin.rootCallerName          : " + this.config_.getRootCallerName());
        out.println("\tjavelin.endCalleeName           : " + this.config_.getEndCalleeName());
        out.println("\tjavelin.threadModel             : " + this.config_.getThreadModel());
        out.println("\tjavelin.httpPort                : " + this.config_.getHttpPort());
        out.println("\tjavelin.acceptPort              : " + this.config_.getAcceptPort());
        out.println("\tjavelin.stringLimitLength       : " + this.config_.getStringLimitLength());
        out.println("\tjavelin.system.log              : " + this.config_.getSystemLog());
        out.println("\tjavelin.log.jvn.max             : " + this.config_.getLogJvnMax());
        out.println("\tjavelin.log.zip.max             : " + this.config_.getLogZipMax());
        out.println("\tjavelin.system.log.num.max      : " + this.config_.getSystemLogNumMax());
        out.println("\tjavelin.system.log.size.max     : " + this.config_.getSystemLogSizeMax());
        out.println("\tjavelin.system.log.level        : " + this.config_.getSystemLogLevel());
        out.println("\tjavelin.call.tree.max           : " + this.config_.getCallTreeMax());
        out.println("\tjavelin.record.jmx              : " + this.config_.isRecordJMX());
        out.println("\tjavelin.recordStrategy          : " + this.config_.getRecordStrategy());
        out.println("\tjavelin.alarmListeners          : " + this.config_.getAlarmListeners());
        out.println("\tjavelin.telegramListeners       : " + this.config_.getTelegramListeners());
        out.println("\tjavelin.serializeFile           : " + this.config_.getSerializeFile());
        out.println("<<<<");
    }
}