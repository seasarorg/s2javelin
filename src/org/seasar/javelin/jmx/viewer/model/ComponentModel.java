package org.seasar.javelin.jmx.viewer.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class ComponentModel extends AbstractModel
{
	// �ύX�̎�ނ����ʂ��邽�߂̕�����
	public static final String P_CONSTRAINT = "_constraint";

	public static final String P_CLASS_NAME = "_className";

	public static final String P_INVOCATION = "_invocation";
	
	public static final String P_SOURCE_CONNECTION = "_source_connection";

	public static final String P_TARGET_CONNECTION = "_target_connection";

	private String className_;

	private List<InvocationModel> invocationList_ = 
		new ArrayList<InvocationModel>();

	private Rectangle constraint_; // ����

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
	    firePropertyChange(P_CLASS_NAME, null, className_);
	}

	public void addInvocation(InvocationModel invocation)
	{
		invocationList_.add(invocation);
		invocation.setComponent(this);
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
}
