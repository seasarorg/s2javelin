package org.seasar.javelin.bottleneckeye.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.seasar.javelin.bottleneckeye.event.DataChangeListener;

/**
 * メインコントロール。
 * @author smg
 */
public final class MainCtrl
{
    /** データ変更リスナ。 */
    private Set<DataChangeListener> dataChangeListeners_ = new HashSet<DataChangeListener>();

    /** InvocationModelのリスト */
    private List<InvocationModel>   invocationModelList_;

    /** MainCtrlの唯一のインスタンス。 */
    private static MainCtrl         instance__;

    /**
     * デフォルトコンストラクタ。
     */
    private MainCtrl()
    {
        this.invocationModelList_ = new ArrayList<InvocationModel>();
    }

    /**
     * MainCtrlのインスタンスを取得する。
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
     * InvocationModelを追加する。
     * @param invocationModel InvocationModel
     */
    public synchronized void addInvocationModel(InvocationModel invocationModel)
    {
        this.invocationModelList_.add(invocationModel);
    }

    /**
     * InvocationModelのリストを取得する。
     * @return InvocationModelのリスト
     */
    public synchronized List<InvocationModel> getInvocationList()
    {
        return this.invocationModelList_;
    }

    /**
     * データ変更リスナを登録する。
     * @param listener リスナ
     */
    public void addDataChangeListeners(DataChangeListener listener)
    {
        this.dataChangeListeners_.add(listener);
    }

    /**
     * データ変更リスナを除外する。
     * @param listener リスナ
     */
    public void removeDataChangeListener(DataChangeListener listener)
    {
        this.dataChangeListeners_.remove(listener);
    }

    /**
     * リスナにデータ変更を通知する。
     * @param element 要素
     */
    public void notifyDataChangeListener(Object element)
    {
        for (DataChangeListener listener : this.dataChangeListeners_)
        {
            listener.updateData(element);
        }
    }
}
