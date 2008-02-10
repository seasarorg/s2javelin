package org.seasar.javelin.log;

import java.util.Date;

import org.seasar.javelin.CallTree;
import org.seasar.javelin.CallTreeNode;

class JavelinLogTask {
	private Date date_;
	private CallTree tree_;
	private CallTreeNode node_;
	private String jvnFileName_;

	public JavelinLogTask(Date date, String jvnFileName, CallTree tree, CallTreeNode node) {
		date_ = date;
		jvnFileName_ = jvnFileName;
		tree_ = tree;
		node_ = node;
	}

	public Date getDate() {
		return date_;
	}

	public String getJvnFileName() {
		return jvnFileName_;
	}

	public CallTree getTree() {
		return tree_;
	}

	public CallTreeNode getNode() {
		return node_;
	}
}
