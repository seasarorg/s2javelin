package org.seasar.javelin.bottleneckeye.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.seasar.javelin.bottleneckeye.event.DataChangeListener;

public class MainCtrl {

	/** データ変更リスナ。 */
	private Set<DataChangeListener> dataChangeListeners_ = new HashSet<DataChangeListener>();

	/** InvocationModelのリスト */
	private List invocationModelList_;

	/** MainCtrlの唯一のインスタンス。 */
	static private MainCtrl instance__;

	public MainCtrl()
	{
		this.invocationModelList_ = new ArrayList();
	}
	
	/**
	 * MainCtrlのインスタンスを取得する。
	 * 
	 * @return
	 */
	static public MainCtrl getInstance() {
		if (instance__ == null) {
			instance__ = new MainCtrl();
		}

		return instance__;
	}

	public void addInvocationModel(InvocationModel invocationModel) {
		this.invocationModelList_.add(invocationModel);
	}

	public List getInvocationList() {
		return this.invocationModelList_;
	}
	
	public void addDataChangeListeners(DataChangeListener listener) {
		this.dataChangeListeners_.add(listener);
	}

	public void removeDataChangeListener(DataChangeListener listener) {
		this.dataChangeListeners_.remove(listener);
	}
	public void notifyDataChangeListener(Object element) {
		for (Iterator iter = this.dataChangeListeners_.iterator(); iter
				.hasNext();) {
			DataChangeListener listener = (DataChangeListener) iter.next();
			listener.updateData(element);
		}
	}
}
