package org.seasar.javelin.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Field;

import org.seasar.javelin.SystemLogger;

/**
 * Thread���������[�e�B���e�B
 * @author eriguchi
 */
public class ThreadUtil
{
    /** �X���b�h���擾�pMXBean�B */
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
     * �X�^�b�N�g���[�X���擾����B
     * 
     * @return �X�^�b�N�g���[�X�B
     */
    public static StackTraceElement[] getCurrentStackTrace()
    {
        Throwable throwable = new Throwable();
        return throwable.getStackTrace();
    }

    /**
     * �X�^�b�N�g���[�X�𕶎���ɕϊ�����B
     *  
     * @param stacktraces �X�^�b�N�g���[�X�B
     * @return �X�^�b�N�g���[�X������B
     */
    public static String getStackTrace(StackTraceElement[] stacktraces, int depth)
    {
        StringBuffer traceBuffer = new StringBuffer();

        // �擪��javelin���܂ރX�^�b�N�͓ǂݔ�΂��B
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
     * �X�^�b�N�g���[�X�𕶎���ɕϊ�����B
     *  
     * @param stacktraces �X�^�b�N�g���[�X�B
     * @return �X�^�b�N�g���[�X������B
     */
    public static String getStackTrace(StackTraceElement[] stacktraces)
    {
        return getStackTrace(stacktraces, -1);
    }

    /**
     * �X���b�hID���擾����B
     * 
     * @return �X���b�hID�B
     */
    public static long getThreadId()
    {
        return tid__.get().longValue();
    }

    /**
     * �X���b�hID���擾����B
     * @param �X���b�h�B
     * 
     * @return �X���b�hID�B
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
     * �X���b�h�����擾����B
     * 
     * @param maxDepth
     * @param threadIdLong
     * @return�@�X���b�h���B
     */
    public static ThreadInfo getThreadInfo(Long threadIdLong, int maxDepth)
    {
        return threadMBean__.getThreadInfo(threadIdLong, maxDepth);
    }

    /**
     * �C���X�^���X�����֎~����B
     */
    private ThreadUtil()
    {
        //
    }

}
