package org.seasar.javelin.util;

import java.lang.reflect.Field;

import org.seasar.javelin.SystemLogger;

/**
 * Threadを扱うユーティリティ
 * @author eriguchi
 */
public class ThreadUtil
{
    private static Field             tidField__;
    static
    {
        try
        {
            tidField__ = Thread.class.getDeclaredField("tid");
            tidField__.setAccessible(true);
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
    }

    private static ThreadLocal<Long> tid__ = new ThreadLocal<Long>() {
                                               protected Long initialValue()
                                               {
                                                   Thread thread = Thread.currentThread();
                                                   Long tid = ThreadUtil.getThreadId(thread);
                                                   return tid;
                                               }
                                           };

    /**
     * スタックトレースを取得する。
     * 
     * @return スタックトレース。
     */
    public static StackTraceElement[] getCurrentStackTrace()
    {
        Throwable throwable = new Throwable();
        return throwable.getStackTrace();
    }

    /**
     * スタックトレースを文字列に変換する。
     *  
     * @param stacktraces スタックトレース。
     * @return スタックトレース文字列。
     */
    public static String getStackTrace(StackTraceElement[] stacktraces)
    {
        StringBuffer traceBuffer = new StringBuffer();

        // 先頭のjavelinを含むスタックは読み飛ばす。
        int index;
        for (index = 0; index < stacktraces.length; index++)
        {
            StackTraceElement stackTraceElement = stacktraces[index];
            String stackTraceLine = stackTraceElement.toString();
            if (stackTraceLine.contains("javelin") == false)
            {
                break;
            }
        }

        for (; index < stacktraces.length; index++)
        {
            StackTraceElement stackTraceElement = stacktraces[index];
            String stackTraceLine = stackTraceElement.toString();
            traceBuffer.append("\tat ");
            traceBuffer.append(stackTraceLine);
            traceBuffer.append(SystemLogger.NEW_LINE);
        }
        return traceBuffer.toString();
    }

    /**
     * スレッドIDを取得する。
     * 
     * @return スレッドID。
     */
    public static long getThreadId()
    {
        return tid__.get().longValue();
    }

    /**
     * スレッドIDを取得する。
     * @param スレッド。
     * 
     * @return スレッドID。
     */
    public static Long getThreadId(Thread thread)
    {
        Long tid = Long.valueOf(0);
        try
        {
            if (tidField__ != null)
            {
                tid = (Long)tidField__.get(thread);
            }
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        return tid;
    }

    /**
     * インスタンス化を禁止する。
     */
    private ThreadUtil()
    {
        //
    }

}
