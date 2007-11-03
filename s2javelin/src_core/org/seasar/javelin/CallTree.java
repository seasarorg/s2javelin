package org.seasar.javelin;

import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.util.StatsUtil;

/**
 * 
 * @author yamasaki
 */
public class CallTree
{
    private CallTreeNode rootNode_;

    private String       threadID_;

    private String		  rootCallerName_ = "unknown";
    
    private String 	  endCalleeName_ = "unknown";
    
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

	public void setThreadID(String threadID)
	{
		threadID_ = threadID;
	}

	public String getEndCalleeName()
	{
		return endCalleeName_;
	}

	public void setEndCalleeName(String endCalleeName)
	{
		if (endCalleeName == null) return;
		endCalleeName_ = endCalleeName;
	}

	public String getRootCallerName()
	{
		return rootCallerName_;
	}

	public void setRootCallerName(String rootCallerName)
	{
		if (rootCallerName == null) return;
		rootCallerName_ = rootCallerName;
	}


}
