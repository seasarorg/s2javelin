package org.seasar.javelin.jmx;

public class CallTree
{
    private CallTreeNode rootNode_;

    private String       threadID_;

    public CallTree()
    {
        this.threadID_ = createThreadIDText();
    }

    public CallTreeNode getRootNode( )
    {
        return rootNode_;
    }

    public void setRootNode(CallTreeNode rootNode)
    {
        rootNode_ = rootNode;
    }
    
    public String getThreadID()
    {
        return this.threadID_;
    }

    /**
     * スレッドを識別するための文字列を出力する。 
     * フォーマット：スレッド名@スレッドクラス名@スレッドオブジェクトのID
     * 
     * @return スレッドを識別するための文字列
     */
    private String createThreadIDText( )
    {
        Thread currentThread = Thread.currentThread();

        StringBuffer threadId = new StringBuffer();
        threadId.append(currentThread.getName());
        threadId.append("@" + currentThread.getClass().getName());
        threadId.append("@" + getObjectID(currentThread));

        return threadId.toString();
    }

    /**
     * オブジェクトIDを16進形式の文字列として取得する。
     * 
     * @param object オブジェクトIDを取得オブジェクト。
     * @return オブジェクトID。
     */
    private String getObjectID(Object object)
    {
        // 引数がnullの場合は"null"を返す。
        if (object == null)
        {
            return "null";
        }

        return Integer.toHexString(System.identityHashCode(object));
    }
}
