package org.seasar.javelin.bottleneckeye.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.seasar.javelin.bottleneckeye.event.DataChangeListener;

/**
 * ���C���R���g���[���B
 * @author smg
 */
public final class MainCtrl
{
    /** �f�[�^�ύX���X�i�B */
    private Set<DataChangeListener> dataChangeListeners_ = new HashSet<DataChangeListener>();

    /** InvocationModel�̃��X�g */
    private List<InvocationModel>   invocationModelList_;

    /** MainCtrl�̗B��̃C���X�^���X�B */
    private static MainCtrl         instance__;

    /**
     * �f�t�H���g�R���X�g���N�^�B
     */
    private MainCtrl()
    {
        this.invocationModelList_ = new ArrayList<InvocationModel>();
    }

    /**
     * MainCtrl�̃C���X�^���X���擾����B
     * @return MainCtrl
     */
    public static MainCtrl getInstance()
    {
        synchronized (MainCtrl.class)
        {
            if (instance__ == null)
            {
                instance__ = new MainCtrl();
            }

            return instance__;
        }
    }

    /**
     * InvocationModel��ǉ�����B
     * @param invocationModel InvocationModel
     */
    public synchronized void addInvocationModel(InvocationModel invocationModel)
    {
        this.invocationModelList_.add(invocationModel);
    }

    /**
     * InvocationModel�̃��X�g���擾����B
     * @return InvocationModel�̃��X�g
     */
    public synchronized List<InvocationModel> getInvocationList()
    {
        return this.invocationModelList_;
    }

    /**
     * �f�[�^�ύX���X�i��o�^����B
     * @param listener ���X�i
     */
    public void addDataChangeListeners(DataChangeListener listener)
    {
        this.dataChangeListeners_.add(listener);
    }

    /**
     * �f�[�^�ύX���X�i�����O����B
     * @param listener ���X�i
     */
    public void removeDataChangeListener(DataChangeListener listener)
    {
        this.dataChangeListeners_.remove(listener);
    }

    /**
     * ���X�i�Ƀf�[�^�ύX��ʒm����B
     * @param element �v�f
     */
    public void notifyDataChangeListener(Object element)
    {
        for (DataChangeListener listener : this.dataChangeListeners_)
        {
            listener.updateData(element);
        }
    }
}
