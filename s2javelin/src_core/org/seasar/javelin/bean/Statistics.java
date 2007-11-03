package org.seasar.javelin.bean;

import org.seasar.javelin.MBeanManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.seasar.javelin.comparator.AverageComparator;
import org.seasar.javelin.comparator.MaximumComparator;
import org.seasar.javelin.comparator.MinimumComparator;
import org.seasar.javelin.comparator.ThrowableComparator;

public class Statistics implements StatisticsMBean
{
	public synchronized List<InvocationMBean> getInvocationListOrderByAverage()
	{
		List<InvocationMBean> list = createInvocationList();
		Collections.sort(list, new AverageComparator());
		
		return list;
	}

	public synchronized List<InvocationMBean> getInvocationListOrderByMaximum()
	{
		List<InvocationMBean> list = createInvocationList();
		Collections.sort(list, new MaximumComparator());
		
		return list;
	}

	public synchronized List<InvocationMBean> getInvocationListOrderByMinimum()
	{
		List<InvocationMBean> list = createInvocationList();
		Collections.sort(list, new MinimumComparator());
		
		return list;
	}

	public synchronized List<InvocationMBean> getInvocationListOrderByThrowableCount()
	{
		List<InvocationMBean> list = createInvocationList();
		Collections.sort(list, new ThrowableComparator());
		
		return list;
	}

	private synchronized List<InvocationMBean> createInvocationList()
	{
		List<InvocationMBean> list = new ArrayList<InvocationMBean>();
		
		for (ComponentMBean component : MBeanManager.getAllComponents())
		{
			for (InvocationMBean invocation : component.getAllInvocation())
			{
				list.add(invocation);
			}
		}

		return list;
	}
}
