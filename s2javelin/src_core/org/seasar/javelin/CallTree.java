package org.seasar.javelin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

    private Throwable cause_;

    private List<Callback> callbackList_ = new ArrayList<Callback>();

    private Map<String, Object> flagMap_ = new HashMap<String, Object>();
    
    private Map<String, Object> loggingValueMap_ = 
    	new TreeMap<String, Object>();
    
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
				SystemLogger.getInstance().warn(ex);
			}
		}
		
		callbackList_.clear();
	}
	
	public boolean setFlag(String flag, Object value)
	{
		return (flagMap_.put(flag, value) != null);
	}
	
	public Object getFlag(String flag)
	{
		return flagMap_.get(flag);
	}
	
	public boolean containsFlag(String flag)
	{
		return flagMap_.containsKey(flag);
	}
	
	public boolean removeFlag(String flag)
	{
		return (flagMap_.remove(flag) != null);
	}
	
	public void setLoggingValue(String key, Object value)
	{
		loggingValueMap_.put(key, value);
	}
	
	public String[] getLoggingKeys()
	{
		Set<String> keySet = loggingValueMap_.keySet();
		String[] keys = keySet.toArray(new String[keySet.size()]);
		
		return keys;
	}
	
	public Object getLoggingValue(String key)
	{
		return loggingValueMap_.get(key);
	}

    public Throwable getCause()
    {
        return cause_;
    }

    public void setCause(Throwable cause)
    {
        cause_ = cause;
    }
}
