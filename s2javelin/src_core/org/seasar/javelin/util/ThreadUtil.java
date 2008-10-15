package org.seasar.javelin.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Field;

import org.seasar.javelin.SystemLogger;

/**
 * Threadを扱うユーティリティ
 * @author eriguchi
 */
public class ThreadUtil
{
    /** スレッド情報取得用MXBean。 */
    private static ThreadMXBean      threadMBean__ = ManagementFactory.getThreadMXBean();

    private static Field             tidField__;
    static
    {

        try
        {
            try
            {
                tidField__ = Thread.class.getDeclaredField("tid");
            }
            catch (SecurityException se)
            {
                SystemLogger.getInstance().warn(se);
            }
            catch (NoSuchFieldException nsfe)
            {
                tidField__ = Thread.class.getDeclaredField("uniqueId");
            }

            if (tidField__ != null)
            {
                tidField__.setAccessible(true);
            }

            if (threadMBean__.isThreadContentionMonitoringSupported())
            {
                threadMBean__.setThreadContentionMonitoringEnabled(true);
            }
            if (threadMBean__.isThreadCpuTimeSupported())
            {
                threadMBean__.setThreadCpuTimeEnabled(true);
            }
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
    }

    private static ThreadLocal<Long> tid__         = new ThreadLocal<Long>() {
                                                       protected Long initialValue()
                                                       {
                                                           Thread thread = Thread.currentThread();
                                                           Long tid =
                                                                   ThreadUtil.getThreadId(thread);
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
    public static String getStackTrace(StackTraceElement[] stacktraces, int depth)
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

        for (; index < stacktraces.length && depth > 0; index++)
        {
            StackTraceElement stackTraceElement = stacktraces[index];
            String stackTraceLine = stackTraceElement.toString();
            traceBuffer.append("\tat ");
            traceBuffer.append(stackTraceLine);
            traceBuffer.append(SystemLogger.NEW_LINE);

            depth--;
        }

        return traceBuffer.toString();
    }

    /**
     * スタックトレースを文字列に変換する。
     *  
     * @param stacktraces スタックトレース。
     * @return スタックトレース文字列。
     */
    public static String getStackTrace(StackTraceElement[] stacktraces)
    {
        return getStackTrace(stacktraces, -1);
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
            else
            {
                tid = thread.getId();
            }
        }
        catch (Exception ex)
        {
            SystemLogger.getInstance().warn(ex);
        }
        return tid;
    }

    public static long[] getAllThreadIds()
    {
        return threadMBean__.getAllThreadIds();
    }

    /**
     * スレッド情報を取得する。
     * 
     * @param maxDepth
     * @param threadIdLong
     * @return　スレッド情報。
     */
    public static ThreadInfo getThreadInfo(Long threadIdLong, int maxDepth)
    {
        return threadMBean__.getThreadInfo(threadIdLong, maxDepth);
    }

    /**
     * インスタンス化を禁止する。
     */
    private ThreadUtil()
    {
        //
    }

}
