package org.seasar.javelin.stats.bean;

import java.util.List;

/**
 * 統計処理用MBean。<br>
 * S2JmxJavelinで蓄積した情報に対して統計処理を行った結果を返す。<br>
 * 現状、以下の情報を取得することが可能。
 * <ol>
 * <li>平均値でソートしたメソッドコール情報。</li>
 * <li>最大値でソートしたメソッドコール情報。</li>
 * <li>最小値でソートしたメソッドコール情報。</li>
 * <li>例外の発生回数でソートしたメソッドコール情報。</li>
 * </ol>
 * @author yamasaki
 *
 */
public interface StatisticsMBean
{
	List<InvocationMBean> getInvocationListOrderByAverage();
	
	List<InvocationMBean> getInvocationListOrderByMaximum();
	
	List<InvocationMBean> getInvocationListOrderByMinimum();
	
	List<InvocationMBean> getInvocationListOrderByThrowableCount();
}
