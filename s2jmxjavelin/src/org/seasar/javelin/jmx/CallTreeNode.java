package org.seasar.javelin.jmx;

import java.util.ArrayList;
import java.util.List;

import org.seasar.javelin.jmx.bean.Invocation;

public class CallTreeNode
{
	private Invocation invocation_;
	
	private long accumulatedTime_;

	private CallTreeNode parent_;
	
	private List<CallTreeNode> children_ = new ArrayList<CallTreeNode>();

	public Invocation getInvocation()
	{
		return invocation_;
	}

	public void setInvocation(Invocation invocation)
	{
		invocation_ = invocation;
	}

	public long getAccumulatedTime()
	{
		return accumulatedTime_;
	}

	public void setAccumulatedTime(long accumulatedTime)
	{
		accumulatedTime_ = accumulatedTime;
	}

	public CallTreeNode getParent()
	{
		return parent_;
	}

	public void setParent(CallTreeNode parent)
	{
		parent_ = parent;
	}

	public List<CallTreeNode> getChildren()
	{
		return children_;
	}
	
	public void addChild(CallTreeNode node)
	{
		children_.add(node);
		node.setParent(this);

		return;
	}
	
	public long getElapsedTime()
	{
		long elapsedTime = accumulatedTime_;
		for (CallTreeNode node : children_)
		{
			elapsedTime = 
				elapsedTime - node.getAccumulatedTime();
		}
		
		return elapsedTime;
	}
}
