package org.seasar.javelin.bottleneckeye.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.seasar.javelin.bottleneckeye.editpart.ComponentEditPart;

/**
 * コンポーネントモデル。
 * @author smg
 */
public class ComponentModel extends AbstractModel
{
    /**
     * コンストラクタ。
     */
    public ComponentModel()
    {
        super();
    }

    /** 制約の変更 */
    public static final String            P_CONSTRAINT                 = "_constraint";

    /** クラス名の変更 */
    public static final String            P_CLASS_NAME                 = "_className";

    /** Invocationの変更 */
    public static final String            P_INVOCATION                 = "_invocation";

    /** 接続元コネクションの変更 */
    public static final String            P_SOURCE_CONNECTION          = "_source_connection";

    /** 接続先コネクションの変更 */
    public static final String            P_TARGET_CONNECTION          = "_target_connection";

    /** 警告の閾値の変更 */
    public static final String            P_EXCEEDED_THRESHOLD_ALARM   = "exceededThresholdMethodName";

    /** 警告の閾値 */
    private String                        exceededThresholdMethodName_ = "";

    /** クラス名 */
    private String                        className_;

    /** EditPart */
    private ComponentEditPart             part_;

    /** Invocationのリスト */
    private List<InvocationModel>         invocationList_              = new ArrayList<InvocationModel>();

    /** 制約 */
    private Rectangle                     constraint_;                                                            // 制約

    /** コンポーネントタイプ */
    private ComponentType                 componentType_;

    /** このモデルから伸びているコネクションのリスト */
    private List<AbstractConnectionModel> sourceConnections_           = new ArrayList<AbstractConnectionModel>();

    /** このモデルに向かって張られているコネクションのリスト */
    private List<AbstractConnectionModel> targetConnections_           = new ArrayList<AbstractConnectionModel>();

    /** このモデルがユーザによって削除されている場合は <code>true</code> */
    private boolean isDeleted_ = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        List<IPropertyDescriptor> list = new ArrayList<IPropertyDescriptor>();
        list.add(new TextPropertyDescriptor(P_CLASS_NAME, "クラス名"));
        for (int index = 0; index < getInvocationList().size(); index++)
        {
            list.add(list.size() - 1,
                     new TextPropertyDescriptor(P_INVOCATION + index,
                                                getInvocationList().get(index).getMethodName()));
        }

        IPropertyDescriptor[] descriptors = list.toArray(new IPropertyDescriptor[list.size()]);
        return descriptors;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getPropertyValue(Object id)
    {
        if (id.equals(P_CLASS_NAME))
        {
            // プロパティ・ビューに表示するデータを返す
            return this.className_;
        }
        if (id instanceof String)
        {
            String text = (String)id;
            if (text.startsWith(P_INVOCATION))
            {
                int num = Integer.parseInt(text.substring(P_INVOCATION.length()));
                return getInvocationList().get(num);
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPropertySet(Object id)
    {
        if (id.equals(P_CLASS_NAME))
        {
            return true;
        }

        if (id instanceof String)
        {
            String text = (String)id;
            if (text.startsWith(P_INVOCATION))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValue(Object id, Object value)
    {
        // Do Nothing.
    }

    /**
     * クラス名を取得する。
     * @return クラス名
     */
    public String getClassName()
    {
        return this.className_;
    }

    /**
     * クラス名を設定する。
     * @param className クラス名
     */
    public void setClassName(String className)
    {
        this.className_ = className;
        this.componentType_ = ComponentType.getComponentType(className);
        firePropertyChange(P_CLASS_NAME, null, this.className_);
    }

    /**
     * Invocationを追加する。
     * @param invocation Invocation
     */
    public void addInvocation(InvocationModel invocation)
    {
        for (int index = this.invocationList_.size() - 1; index >= 0; index--)
        {
            InvocationModel prevInvocation = this.invocationList_.get(index);
            if (prevInvocation.getMethodName().equals(invocation.getMethodName()))
            {
                this.invocationList_.set(index, invocation);
                return;
            }
        }
        this.invocationList_.add(invocation);
        invocation.setComponent(this);
        Collections.sort(this.invocationList_);
    }

    /**
     * Invocationの一覧を取得する。
     * @return Invocationの一覧
     */
    public List<InvocationModel> getInvocationList()
    {
        return this.invocationList_;
    }

    /**
     * 制約を取得する。
     * @return 制約
     */
    public Rectangle getConstraint()
    {
        return this.constraint_;
    }

    /**
     * 制約を設定する。
     * @param constraint 制約
     */
    public void setConstraint(Rectangle constraint)
    {
        this.constraint_ = constraint;
        // 変更の通知
        firePropertyChange(P_CONSTRAINT, null, constraint);
    }

    /**
     * EditPartを設定する。
     * @param part EditPart
     */
    public void setEditPart(ComponentEditPart part)
    {
        this.part_ = part;
    }

    /**
     * EditPartを取得する。
     * @return EditPart
     */
    public ComponentEditPart getEditPart()
    {
        return this.part_;
    }

    /**
     * このモデルが接続元となるコネクションモデルを追加する。
     * @param connx コネクションモデル
     */
    public void addSourceConnection(AbstractConnectionModel connx)
    {
        this.sourceConnections_.add(connx);
        firePropertyChange(P_SOURCE_CONNECTION, null, null);
    }

    /**
     * このモデルが接続先となるコネクションモデルを追加する。
     * @param connx コネクションモデル
     */
    public void addTargetConnection(AbstractConnectionModel connx)
    {
        this.targetConnections_.add(connx);
        firePropertyChange(P_TARGET_CONNECTION, null, null);
    }

    /**
     * このモデルが接続元となるコネクションモデル一覧を取得する。
     * @return コネクションモデル一覧
     */
    public List<AbstractConnectionModel> getModelSourceConnections()
    {
        return this.sourceConnections_;
    }

    /**
     * このモデルが接続先となるコネクションモデル一覧を取得する。
     * @return コネクションモデル一覧
     */
    public List<? extends AbstractConnectionModel> getModelTargetConnections()
    {
        return this.targetConnections_;
    }

    /**
     * このモデルを接続元とするコネクションを切り離す。
     * @param connx コネクション
     */
    public void removeSourceConnection(AbstractConnectionModel connx)
    {
        this.sourceConnections_.remove(connx);
        firePropertyChange(P_SOURCE_CONNECTION, null, null);
    }

    /**
     * このモデルを接続先とするコネクションを切り離す。
     * @param connx コネクション
     */
    public void removeTargetConnection(AbstractConnectionModel connx)
    {
        this.targetConnections_.remove(connx);
        firePropertyChange(P_TARGET_CONNECTION, null, null);
    }

    /**
     * 警告の閾値を設定する。
     * @param exceededThresholdMethodName 警告の閾値
     */
    public void setExceededThresholdAlarm(String exceededThresholdMethodName)
    {
        if (exceededThresholdMethodName == null)
        {
            this.exceededThresholdMethodName_ = null;
            return;
        }

        String oldMethodName = this.exceededThresholdMethodName_;
        this.exceededThresholdMethodName_ = exceededThresholdMethodName;
        this.firePropertyChange(P_EXCEEDED_THRESHOLD_ALARM, oldMethodName,
                                this.exceededThresholdMethodName_);
    }

    /**
     * コンポーネントタイプを取得する。
     * @return コンポーネントタイプ
     */
    public ComponentType getComponentType()
    {
        return this.componentType_;
    }

    /**
     * このモデルがユーザによって削除されているかどうかを調べる。
     *
     * @return このモデルがユーザによって削除されている場合は <code>true</code>
     */
    public boolean isDeleted()
    {
        return this.isDeleted_;
    }

    /**
     * ユーザによる削除フラグを設定する。
     *
     * @param isDeleted このモデルがユーザによって削除されている場合は <code>true</code>
     */
    public void setDeleted(boolean isDeleted)
    {
        this.isDeleted_ = isDeleted;
    }

}
