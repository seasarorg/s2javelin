package org.seasar.javelin.log;

import static org.seasar.javelin.JavelinConstants.ID_CALL;
import static org.seasar.javelin.JavelinConstants.ID_FIELD_READ;
import static org.seasar.javelin.JavelinConstants.ID_FIELD_WRITE;
import static org.seasar.javelin.JavelinConstants.ID_RETURN;
import static org.seasar.javelin.JavelinConstants.ID_THROW;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPOutputStream;

import org.seasar.javelin.CallTree;
import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.JavelinErrorLogger;

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
    
    /** 出力データを蓄積するキュー */
    private static BlockingQueue<LogTask> queue_ = 
    	new ArrayBlockingQueue<LogTask>(1000);

    static
    {
    	synchronized (S2StatsJavelinFileGenerator.class)
		{
			LoggerThread thread = new LoggerThread();
			thread.start();
		}
    }
    
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
    	LogTask task = new LogTask(new Date(), tree, node);
    	
    	// キューにタスクを追加する。
    	boolean result = queue_.offer(task);
    	if (!result)
    	{
    		// 失敗した場合は、ログに出力する。
    		String message = createLogMessage(tree, node);
    		JavelinErrorLogger.getInstance().log(message);
    	}
    }

    /**
     * Javelinログとして、ファイルに出力する。
     * @param writer ライター
     * @param tree ログの階層木
     * @param node
     */
    private static void generateJavelinFileImpl(Writer writer, CallTree tree,
            CallTreeNode node)
    {
    	String jvnCallMessage;
    	jvnCallMessage = createLogMessage(tree, node);

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
                    ID_FIELD_WRITE, node.getEndTime(), tree, node);
        }
        else
        {
            jvnReturnMessage = JavelinLogMaker.createJavelinLog(
                    ID_RETURN, node.getEndTime(), tree, node);
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

	private static String createLogMessage(CallTree tree, CallTreeNode node)
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
		return jvnCallMessage;
	}

    private static void writeThrowLog(Writer writer, CallTree tree, CallTreeNode node)
    {
        String jvnThrowMessage = JavelinLogMaker.createJavelinLog(
                                                            ID_THROW, node.getThrowTime(), tree, node);
        
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

    static class LogTask
    {
    	private Date         date_;
    	private CallTree     tree_;
    	private CallTreeNode node_;
    	
    	public LogTask(Date date, CallTree tree, CallTreeNode node)
    	{
    		date_ = date;
    		tree_ = tree;
    		node_ = node;
    	}

		public Date getDate()
		{
			return date_;
		}

		public CallTree getTree()
		{
			return tree_;
		}

		public CallTreeNode getNode()
		{
			return node_;
		}
    }
    
    static class LoggerThread extends Thread
    {
    	private static final String EXTENTION_JVN = ".jvn";

    	/** jvnファイル名のフォーマット(日付フォーマット(ミリ(sec)まで表示) */
        private static final String JVN_FILE_FORMAT = "{0}" + File.separator
                            + "javelin_{1,date,yyyyMMddHHmmssSSS}_{2,number,00000}" + EXTENTION_JVN;

        public LoggerThread()
    	{
    		super();
    		setName("S2Javelin-LoggerThread-" + getId());
    		setDaemon(true);
    	}
    	
    	public void run()
    	{
            Writer writer = null;
            
    		while(true)
    		{
    			LogTask task;
				try
				{
					task = queue_.take();
				}
				catch (InterruptedException ex)
				{
					JavelinErrorLogger.getInstance().log(ex);
					continue;
				}
    			
				if(sequenceNumber % 20 == 0)
				{
				    removeLogFiles(10, javelinFileDir_, EXTENTION_JVN);
				}
				
                try
                {
                    String jvnFileName = createJvnFileDir(task.getDate());
                    writer = new FileWriter(jvnFileName, true);

                    // 再帰的にwriterに書き込みを行う。
                    generateJavelinFileImpl(
                    		writer, task.getTree(), task.getNode());
                }
                catch (IOException ioEx)
                {
					JavelinErrorLogger.getInstance().log(ioEx);
                }
                finally
                {
                    if (writer != null)
                    {
                        try
                        {
                            writer.close();
                        }
                        catch (IOException ioEx)
                        {
        					JavelinErrorLogger.getInstance().log(ioEx);
                        }
                    }
                }
    		}
    	}
    	
        /**
         * ファイル名を生成する
         * @return ファイル名
         */
        private String createJvnFileDir(Date date)
        {
            String fileName;
            fileName = MessageFormat.format(JVN_FILE_FORMAT,
                                            javelinFileDir_, date, (sequenceNumber++));

            return fileName;
        }

        private void removeLogFiles(int maxFileCount, String dirName, final String extention)
        {
            File dir = new File(dirName);
            File[] files = dir.listFiles(new FilenameFilter(){
                public boolean accept(File dir, String name)
                {
                    if(name != null && name.endsWith(extention))
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }}
            );
            if (files == null)
            {
                return;
            }
            Arrays.sort(files);
            
            for (int index = files.length; index > maxFileCount; index--)
            {
                files[files.length - index].delete();
                JavelinErrorLogger.getInstance().log("Remove file name = " + files[files.length - index].getName());
            }
        }

        private void monitor() throws Exception{
            
            String fileName = "";
            FileOutputStream fStream = new FileOutputStream(fileName);
            GZIPOutputStream zStream = new GZIPOutputStream(fStream);
            PrintStream      pStream = new PrintStream(zStream);
                
           pStream.print(":");
                
            pStream.close();
        }
    }

}
