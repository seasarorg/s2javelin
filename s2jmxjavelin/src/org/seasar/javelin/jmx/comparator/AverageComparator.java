package org.seasar.javelin.jmx.comparator;

import java.util.Comparator;

import org.seasar.javelin.jmx.bean.Invocation;

public class AverageComparator implements Comparator
{
	public int compare(Object arg0, Object arg1)
	{
		Invocation invocation0 = (Invocation)arg0;
		Invocation invocation1 = (Invocation)arg1;
		
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