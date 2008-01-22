package org.seasar.javelin.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.seasar.javelin.bean.InvocationMBean;

public class ThrowableComparator implements Comparator<InvocationMBean>, Serializable
{
	private static final long serialVersionUID = 2243227887834030002L;

	public int compare(InvocationMBean invocation0, InvocationMBean invocation1)
	{
		if (invocation0.getThrowableCount() > invocation1.getThrowableCount())
		{
			return -1;
		}
		else if (invocation0.getThrowableCount() < invocation1.getThrowableCount())
		{
			return 1;
		}
		
		return 0;
	}
}
