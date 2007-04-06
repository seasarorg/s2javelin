package org.seasar.javelin.jmx;


import org.seasar.javelin.StatsUtil;


public class CallTree
{
    private CallTreeNode rootNode_;

    private String       threadID_;

    public CallTree()
    {
        this.threadID_ = StatsUtil.createThreadIDText();
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

	public void setThreadID(String threadID) {
		threadID_ = threadID;
	}
}
