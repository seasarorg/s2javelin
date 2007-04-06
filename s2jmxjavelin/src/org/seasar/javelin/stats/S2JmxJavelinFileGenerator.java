package org.seasar.javelin.jmx;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class S2JmxJavelinFileGenerator
{
    static String javelinFileDir_ = "";

    public S2JmxJavelinFileGenerator(String javelinFielDir)
    {
        this.javelinFileDir_ = javelinFielDir;
    }
    
    /**
     * Javelin���O�Ƃ��āA�t�@�C���ɏo�͂���B
     * 
     * @param node
     */
    public void generateJaveinFile(CallTree tree, CallTreeNode node)
    {
        String threadID = tree.getThreadID();
        Writer writer = null;
        try
        {
            String jvnFileName = createJvnFileDir();
            writer = new FileWriter(jvnFileName, true);

            // �ċA�I��writer�ɏ������݂��s���B
            generateJavelinFileImpl(writer, threadID, node);
        }
        catch (IOException ioe)
        {
            // TODO �����������ꂽ catch �u���b�N
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
                    // TODO �����������ꂽ catch �u���b�N
                    ioe.printStackTrace();
                }
            }
        }
    }

    private String createJvnFileDir( )
    {
        SimpleDateFormat jvnFileFormat = new SimpleDateFormat(
                                                              "yyyyMMddhhmmssSSS");

        return javelinFileDir_ + File.separator + "javelin_" + jvnFileFormat.format(new Date())
                + ".jvn";
    }

    /**
     * Javelin���O�Ƃ��āA�t�@�C���ɏo�͂���B
     * 
     * @param node
     */
    private void generateJavelinFileImpl(Writer writer, String threadID,
            CallTreeNode node)
    {
        String jvnCallMessage = JavelinLogMaker.createJavelinLog(
                                                                 JavelinLogMaker.ID_CALL,
                                                                 node.getStartTime(),
                                                                 threadID, node);

        // �t�@�C����1���b�Z�[�W���������ށB
        try
        {
            writer.write(jvnCallMessage);
        }
        catch (IOException e)
        {
            // TODO �����������ꂽ catch �u���b�N
            e.printStackTrace();
        }

        List children = node.getChildren();
		for (int index = 0; index <  children.size(); index++)
        {
			CallTreeNode child = (CallTreeNode) children.get(index);
            generateJavelinFileImpl(writer, threadID, child);
        }

        String jvnReturnMessage = JavelinLogMaker.createJavelinLog(
                                                                   JavelinLogMaker.ID_RETURN,
                                                                   node.getEndTime(),
                                                                   threadID,
                                                                   node);
        // �t�@�C����1���b�Z�[�W���������ށB
        try
        {
            writer.write(jvnReturnMessage);
        }
        catch (IOException e)
        {
            // TODO �����������ꂽ catch �u���b�N
            e.printStackTrace();
        }
    }

}
