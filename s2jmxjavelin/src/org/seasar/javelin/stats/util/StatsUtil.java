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
	 * スレッドを識別するための文字列を出力する。 
	 * フォーマット：スレッド名@スレッドクラス名@スレッドオブジェクトのID
	 * 
	 * @return スレッドを識別するための文字列
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
	 * オブジェクトIDを16進形式の文字列として取得する。
	 * 
	 * @param object オブジェクトIDを取得オブジェクト。
	 * @return オブジェクトID。
	 */
	public static String getObjectID(Object object)
	{
	    // 引数がnullの場合は"null"を返す。
	    if (object == null)
	    {
	        return "null";
	    }
	
	    return Integer.toHexString(System.identityHashCode(object));
	}

}
