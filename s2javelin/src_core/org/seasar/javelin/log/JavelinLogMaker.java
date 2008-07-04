package org.seasar.javelin.log;

import java.text.SimpleDateFormat;

import org.seasar.javelin.CallTree;
import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.S2JavelinConfig;
import org.seasar.javelin.VMStatus;
import org.seasar.javelin.bean.Invocation;
import org.seasar.javelin.helper.VMStatusHelper;
import org.seasar.javelin.util.StatsUtil;

import static org.seasar.javelin.JavelinConstants.ID_CALL;
import static org.seasar.javelin.JavelinConstants.ID_FIELD_READ;
import static org.seasar.javelin.JavelinConstants.ID_FIELD_WRITE;
import static org.seasar.javelin.JavelinConstants.ID_RETURN;
import static org.seasar.javelin.JavelinConstants.ID_THROW;
import static org.seasar.javelin.JavelinConstants.MSG_CALL;
import static org.seasar.javelin.JavelinConstants.MSG_FIELD_READ;
import static org.seasar.javelin.JavelinConstants.MSG_FIELD_WRITE;
import static org.seasar.javelin.JavelinConstants.MSG_RETURN;
import static org.seasar.javelin.JavelinConstants.MSG_THROW;
import static org.seasar.javelin.JavelinConstants.MSG_CATCH;

import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME_DELTA;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_THREAD_CURRENT_THREAD_USER_TIME;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_THREAD_CURRENT_THREAD_USER_TIME_DELTA;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_THREAD_THREADINFO_BLOCKED_COUNT;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_THREAD_THREADINFO_BLOCKED_COUNT_DELTA;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_THREAD_THREADINFO_BLOCKED_TIME;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_THREAD_THREADINFO_BLOCKED_TIME_DELTA;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_THREAD_THREADINFO_WAITED_COUNT;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_THREAD_THREADINFO_WAITED_COUNT_DELTA;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_THREAD_THREADINFO_WAITED_TIME;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_THREAD_THREADINFO_WAITED_TIME_DELTA;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_GARBAGECOLLECTOR_COLLECTION_COUNT;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_GARBAGECOLLECTOR_COLLECTION_TIME;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_GARBAGECOLLECTOR_COLLECTION_COUNT_DELTA;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_GARBAGECOLLECTOR_COLLECTION_TIME_DELTA;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_MEMORYPOOL_PEAKUSAGE_USAGE;
import static org.seasar.javelin.log.JavelinLogConstants.JMXPARAM_MEMORYPOOL_PEAKUSAGE_USAGE_DELTA;
import static org.seasar.javelin.log.JavelinLogConstants.EXTRAPARAM_DURATION;

import static org.seasar.javelin.log.JavelinLogConstants.DATE_PATTERN;
import static org.seasar.javelin.log.JavelinLogConstants.JAVELIN_ARGS_END;
import static org.seasar.javelin.log.JavelinLogConstants.JAVELIN_ARGS_START;
import static org.seasar.javelin.log.JavelinLogConstants.JAVELIN_RETURN_END;
import static org.seasar.javelin.log.JavelinLogConstants.JAVELIN_RETURN_START;
import static org.seasar.javelin.log.JavelinLogConstants.JAVELIN_STACKTRACE_END;
import static org.seasar.javelin.log.JavelinLogConstants.JAVELIN_STACKTRACE_START;
import static org.seasar.javelin.log.JavelinLogConstants.JAVELIN_JMXINFO_END;
import static org.seasar.javelin.log.JavelinLogConstants.JAVELIN_JMXINFO_START;
import static org.seasar.javelin.log.JavelinLogConstants.JAVELIN_EXTRAINFO_END;
import static org.seasar.javelin.log.JavelinLogConstants.JAVELIN_EXTRAINFO_START;

/**
 * Javelinログの一要素作成するクラス。
 * 
 * @author eriguchi
 */
public class JavelinLogMaker
{
    private static final String[] MESSAGE_TYPES = new String[]{MSG_CALL, MSG_RETURN,
            MSG_FIELD_READ, MSG_FIELD_WRITE, MSG_THROW, MSG_CATCH};

    private static final String   NEW_LINE      = "\r\n";

    public static String createJavelinLog(int messageType, long time, CallTree tree,
            CallTreeNode node)
    {
        if (time == 0l)
        {
            return null;
        }

        CallTreeNode parent = node.getParent();
        S2JavelinConfig config = new S2JavelinConfig();
        boolean isReturnDetail = config.isReturnDetail();

        StringBuffer jvnBuffer = new StringBuffer();

        Invocation callee = node.getInvocation();
        Invocation caller;
        if (parent == null)
        {
            String processName = VMStatusHelper.getProcessName();
            caller = new Invocation(processName, null, null, tree.getRootCallerName(), "unknown", 0, 0, 0, 0);
        }
        else
        {
            caller = parent.getInvocation();
        }

        if (callee == null)
        {
            return "";
        }

        jvnBuffer.append(MESSAGE_TYPES[messageType]);

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        jvnBuffer.append(",");
        jvnBuffer.append(dateFormat.format(time));

        Throwable throwable = node.getThrowable();
        if (messageType == ID_THROW)
        {
            // 例外クラス名
            addToJvnBuffer(throwable.getClass().getName(), jvnBuffer);

            // 例外オブジェクトID
            addToJvnBuffer(StatsUtil.getObjectID(throwable), jvnBuffer);
        }

        // 呼び出し先メソッド名
        addToJvnBuffer(callee.getMethodName(), jvnBuffer);

        // 呼び出し先クラス名
        addToJvnBuffer(callee.getClassName(), jvnBuffer);

        // TODO 呼び出し先オブジェクトID
        addToJvnBuffer("unknown", jvnBuffer);

        if (messageType == ID_FIELD_READ || messageType == ID_FIELD_WRITE)
        {
            // TODO アクセス元メソッド名
            addToJvnBuffer("", jvnBuffer);

            // TODO アクセス元クラス名
            addToJvnBuffer("", jvnBuffer);

            // TODO アクセス元オブジェクトID
            addToJvnBuffer("", jvnBuffer);

            // TODO アクセス先フィールドの型
            addToJvnBuffer("", jvnBuffer);
        }
        else if (messageType == ID_CALL || messageType == ID_RETURN)
        {
            // 呼び出し元メソッド名
            addToJvnBuffer(caller.getMethodName(), jvnBuffer);

            // 呼び出し元クラス名
            addToJvnBuffer(caller.getClassName(), jvnBuffer);

            // TODO 呼び出し元オブジェクトID
            addToJvnBuffer("unknown", jvnBuffer);

            // TODO モディファイア
            addToJvnBuffer("", jvnBuffer);
        }

        // スレッドID
        addToJvnBuffer(tree.getThreadID(), jvnBuffer);
        jvnBuffer.append(NEW_LINE);

        int stringLimitLength = config.getStringLimitLength();
        String[] args = node.getArgs();
        if (messageType == ID_CALL && args != null && args.length > 0)
        {
            addArgs(jvnBuffer, stringLimitLength, args);
        }

        if (messageType == ID_RETURN)
        {
            String returnValue = node.getReturnValue();
            if (returnValue != null)
            {
                addReturn(jvnBuffer, returnValue, isReturnDetail, stringLimitLength);
            }
        }

        if (config.isLogJmxInfo() || (config.isLogJmxInfoRoot() && parent == null))
        {
            if (messageType == ID_CALL || messageType == ID_RETURN)
            {
                // VM実行情報
                VMStatus startStatus = node.getStartVmStatus();
                VMStatus endStatus = node.getEndVmStatus();

                jvnBuffer.append(JAVELIN_JMXINFO_START);
                jvnBuffer.append(NEW_LINE);

                if (messageType == ID_CALL)
                {
                    addVMStatus(jvnBuffer, startStatus);
                }
                else
                {
                    addVMStatus(jvnBuffer, endStatus);
                }
                addVMStatusDiff(jvnBuffer, startStatus, endStatus);

                // diff
                jvnBuffer.append(JAVELIN_JMXINFO_END);
                jvnBuffer.append(NEW_LINE);
            }
        }

        if (messageType == ID_CALL)
        {
            jvnBuffer.append(JAVELIN_EXTRAINFO_START);
            jvnBuffer.append(NEW_LINE);

            long duration = node.getAccumulatedTime();
            if(duration >= 0)
            {
                addParam(jvnBuffer, EXTRAPARAM_DURATION, duration);
            }

            if (node.getParent() == null)
            {
                for (String key : tree.getLoggingKeys())
                {
                    Object value = tree.getLoggingValue(key);
                    addParam(jvnBuffer, key, value.toString());
                }
            }
            for (String key : node.getLoggingKeys())
            {
            	Object value = node.getLoggingValue(key);
                addParam(jvnBuffer, key, value.toString());
            }
            
            jvnBuffer.append(JAVELIN_EXTRAINFO_END);
            jvnBuffer.append(NEW_LINE);
        }

        StackTraceElement[] stacktrace = node.getStacktrace();
        if (messageType == ID_CALL && stacktrace != null)
        {
            addStackTrace(jvnBuffer, stacktrace);
        }

        if (messageType == ID_THROW)
        {
            addThrowable(jvnBuffer, throwable);
        }

        String jvnMessage = jvnBuffer.toString();
        return jvnMessage;
    }

    private static void addStackTrace(StringBuffer jvnBuffer, StackTraceElement[] stacktrace)
    {
        jvnBuffer.append(JAVELIN_STACKTRACE_START);
        jvnBuffer.append(NEW_LINE);
        for (int i = 0; i < stacktrace.length; i++)
        {
            jvnBuffer.append(stacktrace[i]);
            jvnBuffer.append(NEW_LINE);
        }
        jvnBuffer.append(JAVELIN_STACKTRACE_END);
        jvnBuffer.append(NEW_LINE);
    }

    private static void addThrowable(StringBuffer jvnBuffer, Throwable throwable)
    {
        jvnBuffer.append(JAVELIN_STACKTRACE_START);
        jvnBuffer.append(NEW_LINE);
        jvnBuffer.append(throwable.getClass().getName() + ":" + throwable.getLocalizedMessage());
        jvnBuffer.append(NEW_LINE);
        StackTraceElement[] stacktrace = throwable.getStackTrace();
        for (int i = 0; i < stacktrace.length; i++)
        {
            jvnBuffer.append(stacktrace[i]);
            jvnBuffer.append(NEW_LINE);
        }
        jvnBuffer.append(JAVELIN_STACKTRACE_END);
        jvnBuffer.append(NEW_LINE);
    }

    private static void addReturn(StringBuffer jvnBuffer, String returnValue,
            boolean isReturnDetail, int stringLimitLength)
    {
        jvnBuffer.append(JAVELIN_RETURN_START);
        jvnBuffer.append(NEW_LINE);
        jvnBuffer.append(StatsUtil.toStr(returnValue, stringLimitLength));
        jvnBuffer.append(NEW_LINE);
        jvnBuffer.append(JAVELIN_RETURN_END);
        jvnBuffer.append(NEW_LINE);
    }

    private static void addArgs(StringBuffer jvnBuffer, int stringLimitLength, String[] args)
    {
        jvnBuffer.append(JAVELIN_ARGS_START);
        jvnBuffer.append(NEW_LINE);
        for (int i = 0; i < args.length; i++)
        {
            jvnBuffer.append("args[");
            jvnBuffer.append(i);
            jvnBuffer.append("] = ");

            jvnBuffer.append(StatsUtil.toStr(args[i], stringLimitLength));

            jvnBuffer.append(NEW_LINE);
        }
        jvnBuffer.append(JAVELIN_ARGS_END);
        jvnBuffer.append(NEW_LINE);
    }

    private static void addVMStatus(StringBuffer jvnBuffer, VMStatus vmStatus)
    {
        if (vmStatus == null)
        {
            return;
        }

        addParam(jvnBuffer, JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME, vmStatus.getCpuTime());
        addParam(jvnBuffer, JMXPARAM_THREAD_CURRENT_THREAD_USER_TIME, vmStatus.getUserTime());
        addParam(jvnBuffer, JMXPARAM_THREAD_THREADINFO_BLOCKED_TIME, vmStatus.getBlockedTime());
        addParam(jvnBuffer, JMXPARAM_THREAD_THREADINFO_BLOCKED_COUNT, vmStatus.getBlockedCount());
        addParam(jvnBuffer, JMXPARAM_THREAD_THREADINFO_WAITED_TIME, vmStatus.getWaitedTime());
        addParam(jvnBuffer, JMXPARAM_THREAD_THREADINFO_WAITED_COUNT, vmStatus.getWaitedCount());
        addParam(jvnBuffer, JMXPARAM_GARBAGECOLLECTOR_COLLECTION_COUNT,
                 vmStatus.getCollectionCount());
        addParam(jvnBuffer, JMXPARAM_GARBAGECOLLECTOR_COLLECTION_TIME, vmStatus.getCollectionTime());
        addParam(jvnBuffer, JMXPARAM_MEMORYPOOL_PEAKUSAGE_USAGE, vmStatus.getPeakMamoryUsage());
    }

    private static void addVMStatusDiff(StringBuffer jvnBuffer, VMStatus startStatus,
            VMStatus endStatus)
    {
        if (startStatus == null || endStatus == null)
        {
            return;
        }

        addParamDelta(jvnBuffer, JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME_DELTA,
                      endStatus.getCpuTime() - startStatus.getCpuTime());
        addParamDelta(jvnBuffer, JMXPARAM_THREAD_CURRENT_THREAD_USER_TIME_DELTA,
                      endStatus.getUserTime() - startStatus.getUserTime());
        addParamDelta(jvnBuffer, JMXPARAM_THREAD_THREADINFO_BLOCKED_TIME_DELTA,
                      endStatus.getBlockedTime() - startStatus.getBlockedTime());
        addParamDelta(jvnBuffer, JMXPARAM_THREAD_THREADINFO_BLOCKED_COUNT_DELTA,
                      endStatus.getBlockedCount() - startStatus.getBlockedCount());
        addParamDelta(jvnBuffer, JMXPARAM_THREAD_THREADINFO_WAITED_TIME_DELTA,
                      endStatus.getWaitedTime() - startStatus.getWaitedTime());
        addParamDelta(jvnBuffer, JMXPARAM_THREAD_THREADINFO_WAITED_COUNT_DELTA,
                      endStatus.getWaitedCount() - startStatus.getWaitedCount());
        addParamDelta(jvnBuffer, JMXPARAM_GARBAGECOLLECTOR_COLLECTION_COUNT_DELTA,
                      endStatus.getCollectionCount() - startStatus.getCollectionCount());
        addParamDelta(jvnBuffer, JMXPARAM_GARBAGECOLLECTOR_COLLECTION_TIME_DELTA,
                      endStatus.getCollectionTime() - startStatus.getCollectionTime());
        addParamDelta(jvnBuffer, JMXPARAM_MEMORYPOOL_PEAKUSAGE_USAGE_DELTA,
                      endStatus.getPeakMamoryUsage() - startStatus.getPeakMamoryUsage());
    }

    private static void addParamDelta(
    		StringBuffer jvnBuffer, String paramName, long paramValue)
    {
        if (paramValue == 0)
        {
            return;
        }
        jvnBuffer.append(paramName);
        jvnBuffer.append(" = ");
        jvnBuffer.append(paramValue);
        jvnBuffer.append(NEW_LINE);
    }

    private static void addParam(
    		StringBuffer jvnBuffer, String paramName, String paramValue)
    {
        jvnBuffer.append(paramName);
        jvnBuffer.append(" = ");
        jvnBuffer.append(paramValue);
        jvnBuffer.append(NEW_LINE);
    }

    private static void addParam(StringBuffer jvnBuffer, String paramName, long paramValue)
    {
        jvnBuffer.append(paramName);
        jvnBuffer.append(" = ");
        jvnBuffer.append(paramValue);
        jvnBuffer.append(NEW_LINE);
    }

    private static void addToJvnBuffer(String element, StringBuffer jvnBuffer)
    {
        jvnBuffer.append(",\"");
        jvnBuffer.append(element);
        jvnBuffer.append("\"");
    }

}
