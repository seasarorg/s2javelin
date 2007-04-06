package org.seasar.javelin.jmx.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.seasar.javelin.jmx.bean.Invocation;

public class MinimumComparator implements Comparator, Serializable
{
	private static final long serialVersionUID = 4440894441141274763L;

	public int compare(Object arg0, Object arg1)
	{
		Invocation invocation0 = (Invocation)arg0;
		Invocation invocation1 = (Invocation)arg1;
		
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
