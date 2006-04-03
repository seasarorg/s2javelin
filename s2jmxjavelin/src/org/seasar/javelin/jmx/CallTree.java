package org.seasar.javelin.jmx;

public class CallTree
{
	private CallTreeNode rootNode_;

	public CallTreeNode getRootNode()
	{
		return rootNode_;
	}

	public void setRootNode(CallTreeNode rootNode)
	{
		rootNode_ = rootNode;
	}
}
