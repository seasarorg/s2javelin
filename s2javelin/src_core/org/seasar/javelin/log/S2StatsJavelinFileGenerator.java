package org.seasar.javelin.log;

import static org.seasar.javelin.JavelinConstants.ID_CALL;
import static org.seasar.javelin.JavelinConstants.ID_FIELD_READ;
import static org.seasar.javelin.JavelinConstants.ID_FIELD_WRITE;
import static org.seasar.javelin.JavelinConstants.ID_RETURN;
import static org.seasar.javelin.JavelinConstants.ID_THROW;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.seasar.javelin.CallTree;
import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.JavelinErrorLogger;

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
    
    /** �o�̓f�[�^��~�ς���L���[ */
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
    	LogTask task = new LogTask(new Date(), tree, node);
    	
    	// �L���[�Ƀ^�X�N��ǉ�����B
    	boolean result = queue_.offer(task);
    	if (!result)
    	{
    		// ���s�����ꍇ�́A���O�ɏo�͂���B
    		String message = createLogMessage(tree, node);
    		JavelinErrorLogger.getInstance().log(message);
    	}
    }

    /**
     * Javelin���O�Ƃ��āA�t�@�C���ɏo�͂���B
     * @param writer ���C�^�[
     * @param tree ���O�̊K�w��
     * @param node
     */
    private static void generateJavelinFileImpl(Writer writer, CallTree tree,
            CallTreeNode node)
    {
    	String jvnCallMessage;
    	jvnCallMessage = createLogMessage(tree, node);

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
    	public LoggerThread()
    	{
    		super();
    		setName("LoggerThread<S2Javelin>-" + getId());
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
    			
                try
                {
                    String jvnFileName = createJvnFileDir(task.getDate());
                    writer = new FileWriter(jvnFileName, true);

                    // �ċA�I��writer�ɏ������݂��s���B
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
         * �t�@�C�����𐶐�����
         * @return �t�@�C����
         */
        private String createJvnFileDir(Date date)
        {
            /** ���t�t�H�[�}�b�g(�~��(sec)�܂ŕ\��) */
            SimpleDateFormat jvnFileFormat = 
            	new SimpleDateFormat("yyyyMMddHHmmssSSS");
            
            String fileName;
            fileName = javelinFileDir_ + File.separator + "javelin_"
                    + jvnFileFormat.format(date) + "_"
                    + (sequenceNumber++) + ".jvn";

            return fileName;
        }

    }
}
