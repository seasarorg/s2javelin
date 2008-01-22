package org.seasar.javelin.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.seasar.javelin.bean.InvocationMBean;

public class MaximumComparator implements Comparator<InvocationMBean>, Serializable
{
	private static final long serialVersionUID = 1255690461628306016L;

	public int compare(InvocationMBean invocation0, InvocationMBean invocation1)
	{
		if (invocation0.getMinimum() > invocation1.getMinimum())
		{
			return -1;
		}
		else if (invocation0.getMinimum() < invocation1.getMinimum())
		{
			return 1;
		}
		
		return 0;
	}

}
