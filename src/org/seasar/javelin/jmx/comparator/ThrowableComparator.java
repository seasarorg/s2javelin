package org.seasar.javelin.jmx.comparator;

import java.util.Comparator;

import org.seasar.javelin.jmx.bean.Invocation;

public class ThrowableComparator implements Comparator<Invocation> {

	public int compare(Invocation arg0, Invocation arg1)
	{
		if (arg0.getThrowableCount() > arg1.getThrowableCount())
		{
			return -1;
		}
		else if (arg0.getThrowableCount() < arg1.getThrowableCount())
		{
			return 1;
		}
		
		return 0;
	}

}
