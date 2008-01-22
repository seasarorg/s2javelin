package org.seasar.javelin.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.seasar.javelin.bean.InvocationMBean;

public class MinimumComparator implements Comparator<InvocationMBean>, Serializable
{
	private static final long serialVersionUID = 4440894441141274763L;

	public int compare(InvocationMBean invocation0, InvocationMBean invocation1)
	{
		if (invocation0.getMaximum() > invocation1.getMaximum())
		{
			return -1;
		}
		else if (invocation0.getMaximum() < invocation1.getMaximum())
		{
			return 1;
		}
		
		return 0;
	}

}
