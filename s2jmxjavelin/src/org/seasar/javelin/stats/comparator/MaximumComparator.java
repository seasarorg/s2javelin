package org.seasar.javelin.stats.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.seasar.javelin.stats.bean.Invocation;

public class MaximumComparator implements Comparator, Serializable
{
	private static final long serialVersionUID = 1255690461628306016L;

	public int compare(Object arg0, Object arg1)
	{
		Invocation invocation0 = (Invocation)arg0;
		Invocation invocation1 = (Invocation)arg1;
		
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
