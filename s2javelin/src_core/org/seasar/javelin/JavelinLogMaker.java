package org.seasar.javelin;

import java.text.SimpleDateFormat;

import org.seasar.javelin.bean.Invocation;
import org.seasar.javelin.util.StatsUtil;

public class JavelinLogMaker
{
    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Call"のID。 <br>
     */
    public static final int       ID_CALL       = 0;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Return"のID。 <br>
     */
    public static final int       ID_RETURN     = 1;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Read"のID。 <br>
     */
    public static final int       ID_READ       = 2;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Write"のID。 <br>
     */
    public static final int       ID_WRITE      = 3;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Throw"のID。 <br>
     */
    public static final int       ID_THROW      = 4;

    private static final String[] MESSAGE_TYPES = new String[]{"Call  ", "Return", "Read  ",
            "Write ", "Throw "                  };

    private static final String   NEW_LINE      = "\r\n";

    /**
     * 動作ログ出力日時のフォーマット。
     */
    private static final String   DATE_PATTERN  = "yyyy/MM/dd HH:mm:ss.SSS";

    public static String createJavelinLog(int messageType, long time, CallTree tree,
            CallTreeNode node)
    {
        if (time == 0l)
        {
            return null;
        }

        CallTreeNode parent = node.getParent();
        S2StatsJavelinConfig config = new S2StatsJavelinConfig();
        boolean isReturnDetail = config.isReturnDetail();
        int returnDetailDepth = config.getReturnDetailDepth();

        StringBuffer jvnBuffer = new StringBuffer();

        Invocation callee = node.getInvocation();
        Invocation caller;
        if (parent == null)
        {
            caller = new Invocation(null, null, tree.getRootCallerName(), "unknown", 0, 0, 0, 0);
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

        if (messageType == ID_THROW)
        {
            // 例外クラス名
            addToJvnBuffer(node.getThrowable().getClass().getName(), jvnBuffer);

            // 例外オブジェクトID
            addToJvnBuffer(StatsUtil.getObjectID(node.getThrowable()), jvnBuffer);
        }

        // 呼び出し先メソッド名
        addToJvnBuffer(callee.getMethodName(), jvnBuffer);

        // 呼び出し先クラス名
        addToJvnBuffer(callee.getClassName(), jvnBuffer);

        // TODO 呼び出し先オブジェクトID
        addToJvnBuffer("unknown", jvnBuffer);

        if (messageType == ID_READ || messageType == ID_WRITE)
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

        if (messageType == ID_CALL && node.getArgs() != null && node.getArgs().length > 0)
        {
            jvnBuffer.append("<<javelin.Args_START>>");
            jvnBuffer.append(NEW_LINE);
            for (int i = 0; i < node.getArgs().length; i++)
            {
                jvnBuffer.append("args[");
                jvnBuffer.append(i);
                jvnBuffer.append("] = ");

                jvnBuffer.append(StatsUtil.toStr(node.getArgs()[i]));

                jvnBuffer.append(NEW_LINE);
            }
            jvnBuffer.append("<<javelin.Args_END>>");
            jvnBuffer.append(NEW_LINE);
        }

        if (messageType == ID_RETURN && node.getReturnValue() != null)
        {
            jvnBuffer.append("<<javelin.Return_START>>");
            jvnBuffer.append(NEW_LINE);
            if (isReturnDetail)
            {
                jvnBuffer.append("Detail").append(returnDetailDepth).append("Depth: ");
                jvnBuffer.append(node.getReturnValue());
            }
            else
            {
                jvnBuffer.append(StatsUtil.toStr(node.getReturnValue()));
            }
            jvnBuffer.append(NEW_LINE);
            jvnBuffer.append("<<javelin.Return_END>>");
            jvnBuffer.append(NEW_LINE);
        }

        if (messageType == ID_CALL && node.getStacktrace() != null)
        {
            jvnBuffer.append("<<javelin.StackTrace_START>>");
            jvnBuffer.append(NEW_LINE);
            for (int i = 0; i < node.getStacktrace().length; i++)
            {
                jvnBuffer.append(node.getStacktrace()[i]);
                jvnBuffer.append(NEW_LINE);
            }
            jvnBuffer.append("<<javelin.StackTrace_END>>");
            jvnBuffer.append(NEW_LINE);
        }

        
        if (messageType == ID_THROW)
        {
            jvnBuffer.append("<<javelin.StackTrace_START>>");
            jvnBuffer.append(NEW_LINE);
            StackTraceElement[] stacktrace = node.getThrowable().getStackTrace();
            for (int i = 0; i < stacktrace.length; i++)
            {
                jvnBuffer.append(stacktrace[i]);
                jvnBuffer.append(NEW_LINE);
            }
            jvnBuffer.append("<<javelin.StackTrace_END>>");
            jvnBuffer.append(NEW_LINE);
        }
        
        String jvnMessage = jvnBuffer.toString();
        return jvnMessage;
    }

    private static void addToJvnBuffer(String element, StringBuffer jvnBuffer)
    {
        jvnBuffer.append(",\"");
        jvnBuffer.append(element);
        jvnBuffer.append("\"");
    }

}
