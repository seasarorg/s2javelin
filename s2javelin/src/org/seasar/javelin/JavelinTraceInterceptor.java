package org.seasar.javelin;

import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.framework.log.Logger;

/**
 * Javelinログを出力するためのTraceInterceptor。
 * 
 * @version 0.2
 * @author On Eriguchi (SMG), Tetsu Hayakawa (SMG)
 */
public class JavelinTraceInterceptor extends AbstractInterceptor
{
    private static final long serialVersionUID = 6661781313519708185L;

    /**
     * メソッド呼び出し時にJavlinログの先頭に出力する識別子。
     */
    private static final String CALL = "Call  ";

    /**
     * メソッド戻り時にJavlinログの先頭に出力する識別子。
     */
    private static final String RETURN = "Return";

    /**
     * 例外throw時にJavlinログの先頭に出力する識別子。
     */
    private static final String THROW = "Throw ";

    /**
     * 例外catch時にJavlinログの先頭に出力する識別子。
     */
    private static final String CATCH = "Catch ";

    
    private static final int CALLER_STACKTRACE_INDEX = 3;

    /**
     * 改行文字。
     */
    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * ログの区切り文字。
     */
    private static final String DELIM = ",";

    /**
     * スレッド情報の区切り文字
     */
    private static final String THREAD_DELIM = "@";

    /**
     * ログ出力する時刻のフォーマット。
     */
    private static final String TIME_FORMAT_STR = "yyyy/MM/dd HH:mm:ss.SSS";

    /**
     * Javelinログに出力する時刻データ整形用フォーマッタ。
     */
    private ThreadLocal timeFormat_ = new ThreadLocal()
    {
        protected synchronized Object initialValue()
        {
            return new SimpleDateFormat(TIME_FORMAT_STR);
        }
    };

    /**
     * メソッドの呼び出し元オブジェクトを表す文字列。
     */
    private ThreadLocal callerLog_ = new ThreadLocal()
    {
        protected synchronized Object initialValue()
        {
            return "<unknown>,<unknown>,0";
        }
    };

    /**
     * Javelinログ出力用ロガー。
     */
    private static Logger logger_ = Logger
            .getLogger(JavelinTraceInterceptor.class);

    /**
     * 引数を出力するかどうか。
     * argsプロパティの値がセットされる。
     */
    private boolean isLogArgs_ = true;

    /**
     * 戻り値を出力するかどうか。
     * returnプロパティの値がセットされる。
     */
    private boolean isLogReturn_ = true;

    /**
     * スタックトレースを出力するかどうか。
     * stackTraceプロパティの値がセットされる。
     */
    private boolean isLogStackTrace_ = false;

    /**
     * 例外発生を記録するテーブル。
     * キーにスレッドの識別子、値に例外オブジェクトを入れる。
     * 例外が発生したらこのマップにその例外を登録し、
     * キャッチされた時点で削除する。
     */
    static private Map exceptionMap__ = Collections.synchronizedMap(new HashMap());
    
    /**
     * Javelinログ出力用のinvokeメソッド。
     * 
     * 実際のメソッド呼び出しを実行する前後で、
     * 呼び出しと返却の詳細ログを、Javelin形式で出力する。
     * 
     * 実行時に例外が発生した場合は、その詳細もログ出力する。
     * 
     * @param invocation
     *            インターセプタによって取得された、呼び出すメソッドの情報
     * @return invocationを実行したときの戻り値
     * @throws Throwable invocationを実行したときに発生した例外
     */
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        StringBuffer methodCallBuff = new StringBuffer(256);

        // 呼び出し先情報取得。
        String calleeMethodName = invocation.getMethod().getName();
        String calleeClassName = getTargetClass(invocation).getName();
        String objectID = Integer.toHexString(System
                .identityHashCode(invocation.getThis()));
        String modifiers = Modifier.toString(invocation.getMethod()
                .getModifiers());

        // 呼び出し元情報取得。
        String currentCallerLog = (String) this.callerLog_.get();

        Thread currentThread = Thread.currentThread();
        String threadName = currentThread.getName();
        String threadClassName = currentThread.getClass().getName();
        String threadID = Integer.toHexString(System
                .identityHashCode(currentThread));
        String threadInfo = threadName + THREAD_DELIM + threadClassName + THREAD_DELIM + threadID;

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
        StringBuffer callDetailBuff = createCallDetail(methodCallBuff,
                invocation);

        // Call ログ出力。
        logger_.debug(callDetailBuff);

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
            // Throw詳細ログ生成。
            StringBuffer throwBuff = createThrowCatchDetail(THROW, calleeMethodName,
                    calleeClassName, objectID, threadInfo, cause);

            // Throw ログ出力。
            logger_.debug(throwBuff);

            //例外発生を記録する。
            exceptionMap__.put(threadInfo, cause);
            
            //例外をスローし、終了する。
            throw cause;
        }
        
        //このスレッドで、直前に例外が発生していたかを確認する。
        //発生していてここにたどり着いたのであれば、この時点で例外が
        //catchされたということになるので、Catchログを出力する。
        boolean isExceptionThrowd = exceptionMap__.containsKey(threadInfo);
        if(isExceptionThrowd == true) 
        {
            //発生していた例外オブジェクトをマップから取り出す。（かつ削除する）
            Throwable exception = (Throwable)exceptionMap__.remove(threadInfo);
            
            // Catch詳細ログ生成。
            StringBuffer throwBuff = createThrowCatchDetail(CATCH, calleeMethodName,
                    calleeClassName, objectID, threadInfo, exception);

            // Catch ログ出力。
            logger_.debug(throwBuff);
        }

        // Return詳細ログ生成。
        StringBuffer returnDetailBuff = createReturnDetail(methodCallBuff, ret);

        // Returnログ出力。
        logger_.debug(returnDetailBuff);

        this.callerLog_.set(currentCallerLog);

        //invocationを実行した際の戻り値を返す。
        return ret;
    }

    /**
     * メソッド呼び出し（識別子Call）の詳細なログを生成する。
     * 
     * @param methodCallBuff メソッド呼び出しの文字列
     * @param invocation メソッド呼び出しの情報
     * @return メソッド呼び出しの詳細なログ
     */
    private StringBuffer createCallDetail(StringBuffer methodCallBuff,
            MethodInvocation invocation)
    {
        String timeStr = ((SimpleDateFormat) this.timeFormat_.get())
                .format(new Date());
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
    private StringBuffer createReturnDetail(StringBuffer methodCallBuff,
            Object ret)
    {
        StringBuffer returnDetailBuff = new StringBuffer(512);
        String returnTimeStr = ((SimpleDateFormat) this.timeFormat_.get())
                .format(new Date());
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
            String calleeClassName, String objectID, String threadInfo,
            Throwable cause)
    {
        String throwTimeStr = ((SimpleDateFormat) this.timeFormat_.get())
                .format(new Date());
        String throwableID = Integer
                .toHexString(System.identityHashCode(cause));
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
     * 引数の内容をログ出力するかどうか設定する。
     * @param isLogArgs 引数をログ出力するならtrue
     */
    public void setLogArgs(boolean isLogArgs)
    {
        this.isLogArgs_ = isLogArgs;
    }

    /**
     * 戻り値の内容をログ出力するかどうか設定する。
     * @param isLogReturn 戻り値をログ出力するならtrue
     */
    public void setLogReturn(boolean isLogReturn)
    {
        this.isLogReturn_ = isLogReturn;
    }

    /**
     * メソッド呼び出しまでのスタックトレースをログ出力するか設定する。
     * @param isLogStackTrace スタックトレースをログ出力するならtrue
     */
    public void setLogStackTrace(boolean isLogStackTrace)
    {
        this.isLogStackTrace_ = isLogStackTrace;
    }
}