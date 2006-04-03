package org.seasar.javelin.jmx.bean;

import java.util.List;

/**
 * ���v�����pMBean�B<br>
 * S2JmxJavelin�Œ~�ς������ɑ΂��ē��v�������s�������ʂ�Ԃ��B<br>
 * ����A�ȉ��̏����擾���邱�Ƃ��\�B
 * <ol>
 * <li>���ϒl�Ń\�[�g�������\�b�h�R�[�����B</li>
 * <li>�ő�l�Ń\�[�g�������\�b�h�R�[�����B</li>
 * <li>�ŏ��l�Ń\�[�g�������\�b�h�R�[�����B</li>
 * <li>��O�̔����񐔂Ń\�[�g�������\�b�h�R�[�����B</li>
 * </ol>
 * @author yamasaki
 *
 */
public interface StatisticsMBean
{
	public List<InvocationMBean> getInvocationListOrderByAverage();
	
	public List<InvocationMBean> getInvocationListOrderByMaximum();
	
	public List<InvocationMBean> getInvocationListOrderByMinimum();
	
	public List<InvocationMBean> getInvocationListOrderByThrowableCount();
}
