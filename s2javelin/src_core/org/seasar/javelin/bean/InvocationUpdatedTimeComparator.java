package org.seasar.javelin.bean;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Invocationの最終更新時刻を確認してソートを行うためのコンパレータ
 */
public class InvocationUpdatedTimeComparator implements Comparator<InvocationMBean>, Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = -2116528440652950284L;

    public int compare(InvocationMBean invocation0, InvocationMBean invocation1)
    {
        if (invocation0.getLastUpdatedTime() < invocation1.getLastUpdatedTime())
        {
            return -1;
        }
        else if (invocation0.getLastUpdatedTime() > invocation1.getLastUpdatedTime())
        {
            return 1;
        }

        return 0;
    }

}
