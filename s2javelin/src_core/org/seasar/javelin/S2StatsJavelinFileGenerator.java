package org.seasar.javelin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Javelinのログファイル名を管理する
 * 
 * @author fujii
 * 
 */
public class S2StatsJavelinFileGenerator
{
    private static String javelinFileDir_ = "";
    
    /** ファイルにつけるシーケンスナンバー */
    private static int sequenceNumber = 0;

    /**
     * Javelinのログファイルを保存するディレクトリ名を設定する
     * 
     * @param javelinFielDir
     *            ファイルの存在するディレクトリ名
     */
    public S2StatsJavelinFileGenerator(String javelinFielDir)
    {
        javelinFileDir_ = javelinFielDir;
    }

    /**
     * Javelinログとして、ファイルに出力する。
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

            // 再帰的にwriterに書き込みを行う。
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
     * ファイル名を生成する
     * @return ファイル名
     */
    private String createJvnFileDir( )
    {
        /** 日付フォーマット(ミリ(sec)まで表示) */
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
     * Javelinログとして、ファイルに出力する。
     * @param writer ライター
     * @param tree ログの階層木
     * @param node
     */
    public static void generateJavelinFileImpl(Writer writer, CallTree tree,
            CallTreeNode node)
    {
    	String jvnCallMessage;
    	if (node.isFieldAccess())
    	{
            jvnCallMessage = JavelinLogMaker.createJavelinLog(
                    JavelinLogMaker.ID_READ, node.getStartTime(), tree, node);
    	}
    	else
    	{
            jvnCallMessage = JavelinLogMaker.createJavelinLog(
                    JavelinLogMaker.ID_CALL, node.getStartTime(), tree, node);
    	}

        // ファイルに1メッセージを書き込む。
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

        // Throwログを書き込む。
        if(node.getThrowable() != null)
        {
            writeThrowLog(writer, tree, node);
        }
        
        String jvnReturnMessage;
        if (node.isFieldAccess())
        {
            jvnReturnMessage = JavelinLogMaker.createJavelinLog(
                    JavelinLogMaker.ID_WRITE, node.getEndTime(), tree, node);
        }
        else
        {
            jvnReturnMessage = JavelinLogMaker.createJavelinLog(
                    JavelinLogMaker.ID_RETURN, node.getEndTime(), tree, node);
        }
        
        // ファイルに1メッセージを書き込む。
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
                                                            JavelinLogMaker.ID_THROW, node.getThrowTime(), tree, node);
        
        // ファイルに1メッセージを書き込む。
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
