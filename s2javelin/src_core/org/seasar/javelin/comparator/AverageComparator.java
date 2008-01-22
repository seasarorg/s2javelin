package org.seasar.javelin.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.seasar.javelin.bean.InvocationMBean;

public class AverageComparator implements Comparator<InvocationMBean>, Serializable
{
	private static final long serialVersionUID = 150841565321516412L;

	public int compare(InvocationMBean invocation0, InvocationMBean invocation1)
	{
		if (invocation0.getAverage() > invocation1.getAverage())
		{
			return -1;
		}
		else if (invocation0.getAverage() < invocation1.getAverage())
		{
			return 1;
		}
		
		return 0;
	}
}
