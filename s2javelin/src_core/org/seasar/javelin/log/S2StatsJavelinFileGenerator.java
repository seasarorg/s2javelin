package org.seasar.javelin.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.seasar.javelin.CallTree;
import org.seasar.javelin.CallTreeNode;
import static org.seasar.javelin.JavelinConstants.ID_CALL;
import static org.seasar.javelin.JavelinConstants.ID_FIELD_READ;
import static org.seasar.javelin.JavelinConstants.ID_FIELD_WRITE;
import static org.seasar.javelin.JavelinConstants.ID_RETURN;
import static org.seasar.javelin.JavelinConstants.ID_THROW;
/**
 * Javelin�̃��O�t�@�C�������Ǘ�����
 * 
 * @author fujii
 * 
 */
public class S2StatsJavelinFileGenerator
{
    private static String javelinFileDir_ = "";
    
    /** �t�@�C���ɂ���V�[�P���X�i���o�[ */
    private static int sequenceNumber = 0;

    /**
     * Javelin�̃��O�t�@�C����ۑ�����f�B���N�g������ݒ肷��
     * 
     * @param javelinFielDir
     *            �t�@�C���̑��݂���f�B���N�g����
     */
    public S2StatsJavelinFileGenerator(String javelinFielDir)
    {
        javelinFileDir_ = javelinFielDir;
    }

    /**
     * Javelin���O�Ƃ��āA�t�@�C���ɏo�͂���B
     * 
     * @param tree
     * @param node
     */
    public void generateJaveinFile(CallTree tree, CallTreeNode node)
    {
        Writer writer = null;
        try
        {
            String jvnFileName = createJvnFileDir();
            writer = new FileWriter(jvnFileName, true);

            // �ċA�I��writer�ɏ������݂��s���B
            generateJavelinFileImpl(writer, tree, node);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        finally
        {
            if (writer != null)
            {
                try
                {
                    writer.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
    }

    /**
     * �t�@�C�����𐶐�����
     * @return �t�@�C����
     */
    private String createJvnFileDir( )
    {
        /** ���t�t�H�[�}�b�g(�~��(sec)�܂ŕ\��) */
        SimpleDateFormat jvnFileFormat = new SimpleDateFormat(
                "yyyyMMddHHmmssSSS");
        String fileName = "";

        fileName = javelinFileDir_ + File.separator + "javelin_"
                + jvnFileFormat.format(new Date()) + "_"
                + Integer.toString(sequenceNumber) + ".jvn";

        sequenceNumber++;
        return fileName;
    }

    /**
     * Javelin���O�Ƃ��āA�t�@�C���ɏo�͂���B
     * @param writer ���C�^�[
     * @param tree ���O�̊K�w��
     * @param node
     */
    public static void generateJavelinFileImpl(Writer writer, CallTree tree,
            CallTreeNode node)
    {
    	String jvnCallMessage;
    	if (node.isFieldAccess())
    	{
            jvnCallMessage = JavelinLogMaker.createJavelinLog(
                    ID_FIELD_READ, node.getStartTime(), tree, node);
    	}
    	else
    	{
            jvnCallMessage = JavelinLogMaker.createJavelinLog(
                    ID_CALL, node.getStartTime(), tree, node);
    	}

        // �t�@�C����1���b�Z�[�W���������ށB
        try
        {
        	if (jvnCallMessage != null)
        	{
                writer.write(jvnCallMessage);
        	}
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

        List children = node.getChildren();
        for (int index = 0; index < children.size(); index++)
        {
            CallTreeNode child = (CallTreeNode) children.get(index);
            generateJavelinFileImpl(writer, tree, child);
        }

        // Throw���O���������ށB
        if(node.getThrowable() != null)
        {
            writeThrowLog(writer, tree, node);
        }
        
        String jvnReturnMessage;
        if (node.isFieldAccess())
        {
            jvnReturnMessage = JavelinLogMaker.createJavelinLog(
                    ID_FIELD_WRITE, node.getEndTime(), tree, node);
        }
        else
        {
            jvnReturnMessage = JavelinLogMaker.createJavelinLog(
                    ID_RETURN, node.getEndTime(), tree, node);
        }
        
        // �t�@�C����1���b�Z�[�W���������ށB
        try
        {
        	if (jvnReturnMessage != null)
        	{
                writer.write(jvnReturnMessage);
        	}
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    private static void writeThrowLog(Writer writer, CallTree tree, CallTreeNode node)
    {
        String jvnThrowMessage = JavelinLogMaker.createJavelinLog(
                                                            ID_THROW, node.getThrowTime(), tree, node);
        
        // �t�@�C����1���b�Z�[�W���������ށB
        try
        {
            if (jvnThrowMessage != null)
            {
                writer.write(jvnThrowMessage);
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

}
