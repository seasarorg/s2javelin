package org.seasar.javelin.bottleneckeye.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.seasar.javelin.bottleneckeye.editpart.ComponentEditPart;

/**
 * �R���|�[�l���g���f���B
 * @author smg
 */
public class ComponentModel extends AbstractModel
{
    /**
     * �R���X�g���N�^�B
     */
    public ComponentModel()
    {
        super();
    }

    /** ����̕ύX */
    public static final String            P_CONSTRAINT                 = "_constraint";

    /** �N���X���̕ύX */
    public static final String            P_CLASS_NAME                 = "_className";

    /** Invocation�̕ύX */
    public static final String            P_INVOCATION                 = "_invocation";

    /** �ڑ����R�l�N�V�����̕ύX */
    public static final String            P_SOURCE_CONNECTION          = "_source_connection";

    /** �ڑ���R�l�N�V�����̕ύX */
    public static final String            P_TARGET_CONNECTION          = "_target_connection";

    /** �x����臒l�̕ύX */
    public static final String            P_EXCEEDED_THRESHOLD_ALARM   = "exceededThresholdMethodName";

    /** �x����臒l */
    private String                        exceededThresholdMethodName_ = "";

    /** �N���X�� */
    private String                        className_;

    /** EditPart */
    private ComponentEditPart             part_;

    /** Invocation�̃��X�g */
    private List<InvocationModel>         invocationList_              = new ArrayList<InvocationModel>();

    /** ���� */
    private Rectangle                     constraint_;                                                            // ����

    /** �R���|�[�l���g�^�C�v */
    private ComponentType                 componentType_;

    /** ���̃��f������L�тĂ���R�l�N�V�����̃��X�g */
    private List<AbstractConnectionModel> sourceConnections_           = new ArrayList<AbstractConnectionModel>();

    /** ���̃��f���Ɍ������Ē����Ă���R�l�N�V�����̃��X�g */
    private List<AbstractConnectionModel> targetConnections_           = new ArrayList<AbstractConnectionModel>();

    /** ���̃��f�������[�U�ɂ���č폜����Ă���ꍇ�� <code>true</code> */
    private boolean isDeleted_ = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors()
    {
        List<IPropertyDescriptor> list = new ArrayList<IPropertyDescriptor>();
        list.add(new TextPropertyDescriptor(P_CLASS_NAME, "�N���X��"));
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
            // �v���p�e�B�E�r���[�ɕ\������f�[�^��Ԃ�
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
     * �N���X�����擾����B
     * @return �N���X��
     */
    public String getClassName()
    {
        return this.className_;
    }

    /**
     * �N���X����ݒ肷��B
     * @param className �N���X��
     */
    public void setClassName(String className)
    {
        this.className_ = className;
        this.componentType_ = ComponentType.getComponentType(className);
        firePropertyChange(P_CLASS_NAME, null, this.className_);
    }

    /**
     * Invocation��ǉ�����B
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
     * Invocation�̈ꗗ���擾����B
     * @return Invocation�̈ꗗ
     */
    public List<InvocationModel> getInvocationList()
    {
        return this.invocationList_;
    }

    /**
     * ������擾����B
     * @return ����
     */
    public Rectangle getConstraint()
    {
        return this.constraint_;
    }

    /**
     * �����ݒ肷��B
     * @param constraint ����
     */
    public void setConstraint(Rectangle constraint)
    {
        this.constraint_ = constraint;
        // �ύX�̒ʒm
        firePropertyChange(P_CONSTRAINT, null, constraint);
    }

    /**
     * EditPart��ݒ肷��B
     * @param part EditPart
     */
    public void setEditPart(ComponentEditPart part)
    {
        this.part_ = part;
    }

    /**
     * EditPart���擾����B
     * @return EditPart
     */
    public ComponentEditPart getEditPart()
    {
        return this.part_;
    }

    /**
     * ���̃��f�����ڑ����ƂȂ�R�l�N�V�������f����ǉ�����B
     * @param connx �R�l�N�V�������f��
     */
    public void addSourceConnection(AbstractConnectionModel connx)
    {
        this.sourceConnections_.add(connx);
        firePropertyChange(P_SOURCE_CONNECTION, null, null);
    }

    /**
     * ���̃��f�����ڑ���ƂȂ�R�l�N�V�������f����ǉ�����B
     * @param connx �R�l�N�V�������f��
     */
    public void addTargetConnection(AbstractConnectionModel connx)
    {
        this.targetConnections_.add(connx);
        firePropertyChange(P_TARGET_CONNECTION, null, null);
    }

    /**
     * ���̃��f�����ڑ����ƂȂ�R�l�N�V�������f���ꗗ���擾����B
     * @return �R�l�N�V�������f���ꗗ
     */
    public List<AbstractConnectionModel> getModelSourceConnections()
    {
        return this.sourceConnections_;
    }

    /**
     * ���̃��f�����ڑ���ƂȂ�R�l�N�V�������f���ꗗ���擾����B
     * @return �R�l�N�V�������f���ꗗ
     */
    public List<? extends AbstractConnectionModel> getModelTargetConnections()
    {
        return this.targetConnections_;
    }

    /**
     * ���̃��f����ڑ����Ƃ���R�l�N�V������؂藣���B
     * @param connx �R�l�N�V����
     */
    public void removeSourceConnection(AbstractConnectionModel connx)
    {
        this.sourceConnections_.remove(connx);
        firePropertyChange(P_SOURCE_CONNECTION, null, null);
    }

    /**
     * ���̃��f����ڑ���Ƃ���R�l�N�V������؂藣���B
     * @param connx �R�l�N�V����
     */
    public void removeTargetConnection(AbstractConnectionModel connx)
    {
        this.targetConnections_.remove(connx);
        firePropertyChange(P_TARGET_CONNECTION, null, null);
    }

    /**
     * �x����臒l��ݒ肷��B
     * @param exceededThresholdMethodName �x����臒l
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
     * �R���|�[�l���g�^�C�v���擾����B
     * @return �R���|�[�l���g�^�C�v
     */
    public ComponentType getComponentType()
    {
        return this.componentType_;
    }

    /**
     * ���̃��f�������[�U�ɂ���č폜����Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return ���̃��f�������[�U�ɂ���č폜����Ă���ꍇ�� <code>true</code>
     */
    public boolean isDeleted()
    {
        return this.isDeleted_;
    }

    /**
     * ���[�U�ɂ��폜�t���O��ݒ肷��B
     *
     * @param isDeleted ���̃��f�������[�U�ɂ���č폜����Ă���ꍇ�� <code>true</code>
     */
    public void setDeleted(boolean isDeleted)
    {
        this.isDeleted_ = isDeleted;
    }

}
