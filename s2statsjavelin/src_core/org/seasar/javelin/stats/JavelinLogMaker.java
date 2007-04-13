package org.seasar.javelin.stats;

import java.text.SimpleDateFormat;

import org.seasar.javelin.stats.bean.Invocation;

public class JavelinLogMaker
{
    /**
     * ���������ŁA���샍�O�̎�ނ���ʂ��邽�߂Ɏg�p����"Return"��ID�B <br>
     */
    public static final int ID_RETURN     = 1;

    /**
     * ���������ŁA���샍�O�̎�ނ���ʂ��邽�߂Ɏg�p����"Call"��ID�B <br>
     */
    public static final int ID_CALL       = 0;

    private static final String[] MESSAGE_TYPES = new String[]{"Call  ", "Return"};

    private static final String NEW_LINE      = "\n";

    /**
     * ���샍�O�o�͓����̃t�H�[�}�b�g�B
     */
    private static final String DATE_PATTERN  = "yyyy/MM/dd HH:mm:ss.SSS";

    public static String createJavelinLog(int messageType, long time,
            CallTree tree, CallTreeNode node)
    {
        CallTreeNode parent = node.getParent();

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
        jvnBuffer.append(",");

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        jvnBuffer.append(dateFormat.format(time));
        jvnBuffer.append(",");

        // �Ăяo���惁�\�b�h��
        jvnBuffer.append(callee.getMethodName());
        jvnBuffer.append(",");

        // �Ăяo����N���X��
        jvnBuffer.append(callee.getClassName());
        jvnBuffer.append(",");

        // TODO �Ăяo����I�u�W�F�N�gID
        jvnBuffer.append("unknown");
        jvnBuffer.append(",");

        // �Ăяo�������\�b�h��
        jvnBuffer.append(caller.getMethodName());
        jvnBuffer.append(",");

        // �Ăяo�����N���X��
        jvnBuffer.append(caller.getClassName());
        jvnBuffer.append(",");

        // TODO �Ăяo�����I�u�W�F�N�gID
        jvnBuffer.append("unknown");
        jvnBuffer.append(",");

        // TODO ���f�B�t�@�C�A
        jvnBuffer.append("");
        jvnBuffer.append(",");

        // �X���b�hID
        jvnBuffer.append(tree.getThreadID());
        jvnBuffer.append(NEW_LINE);

        if (messageType != ID_RETURN && node.getArgs() != null 
        		&& node.getArgs().length > 0)
        {
        	jvnBuffer.append("<<javelin.Args_START>>");
            jvnBuffer.append(NEW_LINE);
			for (int i = 0; i < node.getArgs().length; i++)
			{
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
        
        if (messageType != ID_RETURN && node.getStacktrace() != null)
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
        
        String jvnMessage = jvnBuffer.toString();
        return jvnMessage;
    }
}