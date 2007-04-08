package org.seasar.javelin.stats;

import java.text.SimpleDateFormat;

import org.seasar.javelin.stats.bean.Invocation;

public class JavelinLogMaker
{
    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Return"のID。 <br>
     */
    public static final int ID_RETURN     = 1;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Call"のID。 <br>
     */
    public static final int ID_CALL       = 0;

    private static final String[] MESSAGE_TYPES = new String[]{"Call  ", "Return"};

    private static final String NEW_LINE      = "\n";

    /**
     * 動作ログ出力日時のフォーマット。
     */
    private static final String DATE_PATTERN  = "yyyy/MM/dd HH:mm:ss.SSS";

    public static String createJavelinLog(int messageType, long time,
            String threadID, CallTreeNode node)
    {
        CallTreeNode parent = node.getParent();

        StringBuffer jvnBuffer = new StringBuffer();

        Invocation callee = node.getInvocation();
        Invocation caller;
        if (parent == null)
        {
            caller = new Invocation(null, null, "unknown", "unknown", 0, 0, 0, 0);
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
        jvnBuffer.append(",");

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        jvnBuffer.append(dateFormat.format(time));
        jvnBuffer.append(",");

        // 呼び出し先メソッド名
        jvnBuffer.append(callee.getMethodName());
        jvnBuffer.append(",");

        // 呼び出し先クラス名
        jvnBuffer.append(callee.getClassName());
        jvnBuffer.append(",");

        // TODO 呼び出し先オブジェクトID
        jvnBuffer.append("unknown");
        jvnBuffer.append(",");

        // 呼び出し元メソッド名
        jvnBuffer.append(caller.getMethodName());
        jvnBuffer.append(",");

        // 呼び出し元クラス名
        jvnBuffer.append(caller.getClassName());
        jvnBuffer.append(",");

        // TODO 呼び出し元オブジェクトID
        jvnBuffer.append("unknown");
        jvnBuffer.append(",");

        // TODO モディファイア
        jvnBuffer.append("");
        jvnBuffer.append(",");

        // スレッドID
        jvnBuffer.append(threadID);
        jvnBuffer.append(NEW_LINE);

        if (messageType != ID_RETURN && node.getArgs() != null 
        		&& node.getArgs().length > 0)
        {
        	jvnBuffer.append("<<javelin.Args_START>>");
            jvnBuffer.append(NEW_LINE);
			for (int i = 0; i < node.getArgs().length; i++) {
				jvnBuffer.append("args[");
				jvnBuffer.append(i);
				jvnBuffer.append("] = ");
				jvnBuffer.append(node.getArgs()[i]);
				jvnBuffer.append(NEW_LINE);
			}
        	jvnBuffer.append("<<javelin.Args_END>>");
            jvnBuffer.append(NEW_LINE);
        }
        
        if (messageType == ID_RETURN 
        		&& node.getReturnValue() != null)
        {
        	jvnBuffer.append("<<javelin.Return_START>>");
            jvnBuffer.append(NEW_LINE);
            jvnBuffer.append(node.getReturnValue().toString());
            jvnBuffer.append(NEW_LINE);
        	jvnBuffer.append("<<javelin.Return_END>>");
            jvnBuffer.append(NEW_LINE);
        }
        
        String jvnMessage = jvnBuffer.toString();
        return jvnMessage;
    }
}
