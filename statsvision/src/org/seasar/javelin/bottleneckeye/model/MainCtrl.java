package org.seasar.javelin.bottleneckeye.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.seasar.javelin.bottleneckeye.event.DataChangeListener;

public class MainCtrl {

	/** �f�[�^�ύX���X�i�B */
	private Set<DataChangeListener> dataChangeListeners_ = new HashSet<DataChangeListener>();

	/** InvocationModel�̃��X�g */
	private List invocationModelList_;

	/** MainCtrl�̗B��̃C���X�^���X�B */
	static private MainCtrl instance__;

	public MainCtrl()
	{
		this.invocationModelList_ = new ArrayList();
	}
	
	/**
	 * MainCtrl�̃C���X�^���X���擾����B
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
