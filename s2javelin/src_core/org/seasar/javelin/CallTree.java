package org.seasar.javelin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.util.StatsUtil;

/**
 * Javelinログ出力用にコールスタックを記録するための、ツリークラス。
 * 
 * @author yamasaki
 */
public class CallTree
{
    private CallTreeNode rootNode_;

    private String threadID_;

    private String rootCallerName_ = "unknown";
    
    private String endCalleeName_ = "unknown";

    private List<Callback> callbackList_ = new ArrayList<Callback>();

    private Set<Object> flagSet_ = new HashSet<Object>();
    
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

	public void addCallback(Callback callback)
	{
		callbackList_.add(callback);
	}
	
	public void executeCallback()
	{
		for (Callback callback : callbackList_)
		{
			try
			{
				callback.execute();
			}
			catch(Exception ex)
			{
				JavelinErrorLogger.getInstance().log(ex);
			}
		}
	}
	
	public boolean set(Object flag)
	{
		return flagSet_.add(flag);
	}
	
	public boolean contains(Object flag)
	{
		return flagSet_.contains(flag);
	}
	
	public boolean remove(Object flag)
	{
		return flagSet_.remove(flag);
	}
}
