package org.seasar.javelin.stats.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.seasar.javelin.stats.MBeanManager;
import org.seasar.javelin.stats.comparator.AverageComparator;
import org.seasar.javelin.stats.comparator.MaximumComparator;
import org.seasar.javelin.stats.comparator.MinimumComparator;
import org.seasar.javelin.stats.comparator.ThrowableComparator;

public class Statistics implements StatisticsMBean
{
	public List<InvocationMBean> getInvocationListOrderByAverage()
	{
		List<InvocationMBean> list = createInvocationList();
		Collections.sort(list, new AverageComparator());
		
		return list;
	}

	public List<InvocationMBean> getInvocationListOrderByMaximum()
	{
		List<InvocationMBean> list = createInvocationList();
		Collections.sort(list, new MaximumComparator());
		
		return list;
	}

	public List<InvocationMBean> getInvocationListOrderByMinimum()
	{
		List<InvocationMBean> list = createInvocationList();
		Collections.sort(list, new MinimumComparator());
		
		return list;
	}

	public List<InvocationMBean> getInvocationListOrderByThrowableCount()
	{
		List<InvocationMBean> list = createInvocationList();
		Collections.sort(list, new ThrowableComparator());
		
		return list;
	}

	private List<InvocationMBean> createInvocationList()
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
