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
     * �R���X�g���N�^�B
     */
	public ComponentModel()
	{
		super();
	}
	
	// �ύX�̎�ނ����ʂ��邽�߂̕�����
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

	private Rectangle constraint_; // ����

	private ComponentType componentType_;

	// �ȉ�IPropertySource�C���^�[�t�F�C�X��
	// ���\�b�h�̈ꕔ���I�[�o�[���C�h��������
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		List<IPropertyDescriptor> list = 
			new ArrayList<IPropertyDescriptor>();
		list.add(new TextPropertyDescriptor(P_CLASS_NAME, "�N���X��"));
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
			// �v���p�e�B�E�r���[�ɕ\������f�[�^��Ԃ�
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
		// �ύX�̒ʒm
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
	
	// ���̃��f������L�тĂ���R�l�N�V�����̃��X�g
	private List sourceConnections = new ArrayList();

	// ���̃��f���Ɍ������Ē����Ă���R�l�N�V�����̃��X�g
	private List targetConnections = new ArrayList();

	// ���̃��f������o��R�l�N�V���� ���f���̒ǉ�
	public void addSourceConnection(Object connx)
	{
		sourceConnections.add(connx);
		firePropertyChange(P_SOURCE_CONNECTION, null, null);
	}

	// ���̃��f���ɐڑ������R�l�N�V���� ���f���̒ǉ�
	public void addTargetConnection(Object connx)
	{
		targetConnections.add(connx);
		firePropertyChange(P_TARGET_CONNECTION, null, null);
	}

	// ���̃��f����ڑ����Ƃ���R�l�N�V�����̃��X�g��Ԃ�
	public List getModelSourceConnections()
	{
		return sourceConnections;
	}

	// ���̃��f����ڑ���Ƃ���R�l�N�V�����̃��X�g��Ԃ�
	public List getModelTargetConnections()
	{
		return targetConnections;
	}

	// ���̃��f�����R�l�N�V�����̃\�[�X����؂藣��
	public void removeSourceConnection(Object connx)
	{
		sourceConnections.remove(connx);
		firePropertyChange(P_SOURCE_CONNECTION, null, null);
	}

	// ���̃��f�����R�l�N�V�����̃^�[�Q�b�g����؂藣��
	public void removeTargetConnection(Object connx)
	{
		targetConnections.remove(connx);
		firePropertyChange(P_TARGET_CONNECTION, null, null);
	}

    /**
     * @param exceededThresholdAlarm �ݒ肷�� exceededThresholdAlarm�B
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
