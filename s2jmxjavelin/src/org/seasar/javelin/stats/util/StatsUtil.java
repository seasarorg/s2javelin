package org.seasar.javelin.stats.util;

import org.seasar.javelin.stats.CallTreeNode;

public class StatsUtil {

	public static long getElapsedTime(CallTreeNode node)
	{
	    long elapsedTime = node.getAccumulatedTime();
	    for (int index = 0; index < node.getChildren().size(); index++)
	    {
	    	CallTreeNode child = (CallTreeNode)  node.getChildren().get(index);
	        elapsedTime = elapsedTime - child.getAccumulatedTime();
	    }
	
	    return elapsedTime;
	}

	/**
	 * �X���b�h�����ʂ��邽�߂̕�������o�͂���B 
	 * �t�H�[�}�b�g�F�X���b�h��@�X���b�h�N���X��@�X���b�h�I�u�W�F�N�g��ID
	 * 
	 * @return �X���b�h�����ʂ��邽�߂̕�����
	 */
	public static String createThreadIDText( )
	{
	    Thread currentThread = Thread.currentThread();
	
	    StringBuffer threadId = new StringBuffer();
	    threadId.append(currentThread.getName());
	    threadId.append("@" + currentThread.getClass().getName());
	    threadId.append("@" + StatsUtil.getObjectID(currentThread));
	
	    return threadId.toString();
	}

	/**
	 * �I�u�W�F�N�gID��16�i�`���̕�����Ƃ��Ď擾����B
	 * 
	 * @param object �I�u�W�F�N�gID���擾�I�u�W�F�N�g�B
	 * @return �I�u�W�F�N�gID�B
	 */
	public static String getObjectID(Object object)
	{
	    // ������null�̏ꍇ��"null"��Ԃ��B
	    if (object == null)
	    {
	        return "null";
	    }
	
	    return Integer.toHexString(System.identityHashCode(object));
	}

}
