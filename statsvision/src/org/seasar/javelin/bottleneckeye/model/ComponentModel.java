package org.seasar.javelin.bottleneckeye.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.seasar.javelin.bottleneckeye.editpart.ComponentEditPart;

public class ComponentModel extends AbstractModel
{
    /**
     * コンストラクタ。
     */
	public ComponentModel()
	{
		super();
	}
	
	// 変更の種類を識別するための文字列
	public static final String P_CONSTRAINT = "_constraint";

	public static final String P_CLASS_NAME = "_className";

	public static final String P_INVOCATION = "_invocation";
	
	public static final String P_SOURCE_CONNECTION = "_source_connection";

	public static final String P_TARGET_CONNECTION = "_target_connection";

    public static final String P_EXCEEDED_THRESHOLD_ALARM = "exceededThresholdMethodName";
    
    private String exceededThresholdMethodName_ = "";
    
	private String className_;

	private ComponentEditPart part_;
	
	private List<InvocationModel> invocationList_ = 
		new ArrayList<InvocationModel>();

	private Rectangle constraint_; // 制約

	private ComponentType componentType_;

	// 以下IPropertySourceインターフェイスの
	// メソッドの一部をオーバーライドした部分
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		List<IPropertyDescriptor> list = 
			new ArrayList<IPropertyDescriptor>();
		list.add(new TextPropertyDescriptor(P_CLASS_NAME, "クラス名"));
		for (int index = 0; index < getInvocationList().size(); index++)
		{
			list.add(
			    list.size() - 1
				, new TextPropertyDescriptor(
					P_INVOCATION + index
					, getInvocationList().get(index).getMethodName()));
		}

		IPropertyDescriptor[] descriptors = 
			list.toArray(new IPropertyDescriptor[list.size()]);
		return descriptors;

	}

	public Object getPropertyValue(Object id)
	{
		if (id.equals(P_CLASS_NAME))
		{
			// プロパティ・ビューに表示するデータを返す
			return className_;
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

	public void setPropertyValue(Object id, Object value)
	{
	}

	public String getClassName()
	{
		return className_;
	}

	public void setClassName(String className)
	{
		className_ = className;
		componentType_ = ComponentType.getComponentType(className);
	    firePropertyChange(P_CLASS_NAME, null, className_);
	}

	public void addInvocation(InvocationModel invocation)
	{
		for(int index = invocationList_.size() - 1; index>=0 ; index--)
		{
			InvocationModel prevInvocation = invocationList_.get(index);
			if(prevInvocation.getMethodName().equals(invocation.getMethodName()))
			{
				invocationList_.set(index, invocation);
				return;
			}
		}
		invocationList_.add(invocation);
		invocation.setComponent(this);
		Collections.sort(invocationList_);
	}

	public List<InvocationModel> getInvocationList()
	{
		return invocationList_;
	}

	public Rectangle getConstraint()
	{
		return constraint_;
	}

	public void setConstraint(Rectangle constraint)
	{
		constraint_ = constraint;
		// 変更の通知
		firePropertyChange(P_CONSTRAINT, null, constraint);
	}

	public void setEditPart(ComponentEditPart part)
	{
		part_ = part;
	}
	
	public ComponentEditPart getEditPart()
	{
		return part_;
	}
	
	// このモデルから伸びているコネクションのリスト
	private List sourceConnections = new ArrayList();

	// このモデルに向かって張られているコネクションのリスト
	private List targetConnections = new ArrayList();

	// このモデルから出るコネクション モデルの追加
	public void addSourceConnection(Object connx)
	{
		sourceConnections.add(connx);
		firePropertyChange(P_SOURCE_CONNECTION, null, null);
	}

	// このモデルに接続されるコネクション モデルの追加
	public void addTargetConnection(Object connx)
	{
		targetConnections.add(connx);
		firePropertyChange(P_TARGET_CONNECTION, null, null);
	}

	// このモデルを接続元とするコネクションのリストを返す
	public List getModelSourceConnections()
	{
		return sourceConnections;
	}

	// このモデルを接続先とするコネクションのリストを返す
	public List getModelTargetConnections()
	{
		return targetConnections;
	}

	// このモデルをコネクションのソースから切り離す
	public void removeSourceConnection(Object connx)
	{
		sourceConnections.remove(connx);
		firePropertyChange(P_SOURCE_CONNECTION, null, null);
	}

	// このモデルをコネクションのターゲットから切り離す
	public void removeTargetConnection(Object connx)
	{
		targetConnections.remove(connx);
		firePropertyChange(P_TARGET_CONNECTION, null, null);
	}

    /**
     * @param exceededThresholdAlarm 設定する exceededThresholdAlarm。
     */
    public void setExceededThresholdAlarm(String exceededThresholdMethodName)
    {
    	if (exceededThresholdMethodName == null)
    	{
    		exceededThresholdMethodName_ = null;
    		return;
    	}
    	
        String oldMethodName = this.exceededThresholdMethodName_;
        this.exceededThresholdMethodName_ = exceededThresholdMethodName;
        this.firePropertyChange(
                P_EXCEEDED_THRESHOLD_ALARM, oldMethodName, this.exceededThresholdMethodName_);
    }

	public ComponentType getComponentType() {
		return componentType_;
	}
    
}
