package org.seasar.javelin.bottleneckeye.editpart;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Color;
import org.seasar.javelin.bottleneckeye.editors.EditPartWithListener;
import org.seasar.javelin.bottleneckeye.editors.StatsVisionDeleteEditPolicy;
import org.seasar.javelin.bottleneckeye.editors.view.AbstractStatsVisionEditor;
import org.seasar.javelin.bottleneckeye.model.AbstractConnectionModel;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ComponentType;
import org.seasar.javelin.bottleneckeye.model.InvocationModel;

/**
 * �N���X��\�� EditPart �B
 */
public class ComponentEditPart extends EditPartWithListener implements NodeEditPart
{

    /** �N���X�̔w�i�F�B */
    private static final Color               YELLOW                  =
                                                                             new Color(null, 255,
                                                                                       255, 206);

    /** ���W���[�x���F�B */
    private static final Color               RED                     = ColorConstants.red;

    /** �}�C�i�[�x���F�B */
    private static final Color               ORANGE                  = new Color(null, 224, 160, 0);

    /** �L���F�i�Ăяo����Ă���N���X�^���\�b�h�j�B */
    private static final Color               BLACK                   = ColorConstants.black;

    /** �����F�i�Ăяo����Ă��Ȃ��N���X�^���\�b�h�j�B */
    private static final Color               GRAY                    = ColorConstants.gray;

    /** �R���|�[�l���g���̍ő咷�B������z���镔����...�ŕ\������B */
    private static final int                 COMPONENTNAME_MAXLENGTH = 80;

    /** ���\�b�h���̍ő咷�B������z���镔����...�ŕ\������B */
    private static final int                 METHODNAME_MAXLENGTH    = 80;

    /** �R���|�[�l���g�� */
    private String                           componentName_;

    /** StatsVisionEditor */
    private AbstractStatsVisionEditor<?>     statsVisionEditor_;

    /** �u�����N�p */
    private HashMap<String, Label>           invocationLabelMap_     = new HashMap<String, Label>();

    /** �u�����N�p */
    private HashMap<String, InvocationModel> invocationMap_          =
                                                                             new HashMap<String, InvocationModel>();

    /**
     * �N���X�� EditPart �𐶐�����B
     *
     * @param statsVisionEditor �G�f�B�^
     */
    public ComponentEditPart(AbstractStatsVisionEditor<?> statsVisionEditor)
    {
        this.statsVisionEditor_ = statsVisionEditor;
    }

    /**
     * View���쐬����B
     * @return View 
     */
    protected IFigure createFigure()
    {
        ComponentModel model = (ComponentModel)getModel();

        LineBorder border = new LineBorder();
        border.setColor(BLACK);

        Layer layer = new Layer();
        layer.setBorder(border);
        layer.setOpaque(true);
        layer.setBackgroundColor(YELLOW);
        layer.setLayoutManager(new ToolbarLayout());

        // �R���|�[�l���g���p���x���̐ݒ�B
        Label label = new Label();

        // �w�i�F�����F��
        label.setBackgroundColor(YELLOW);

        // �w�i�F��s������
        label.setOpaque(true);

        // �\��������̌���
        String className = model.getClassName();
        this.componentName_ = className;
        ComponentType componentType = model.getComponentType();
        String componentText = createComponentText(componentType, className);

        label.setForegroundColor(GRAY);
        for (InvocationModel invocation : model.getInvocationList())
        {
            if (invocation.getMaximum() >= 0)
            {
                label.setForegroundColor(BLACK);
            }
        }

        label.setText(toStr(componentText, COMPONENTNAME_MAXLENGTH));
        layer.add(label);

        CompartmentFigure figure = new CompartmentFigure();
        figure.setBackgroundColor(YELLOW);

        for (InvocationModel invocation : model.getInvocationList())
        {
            Label invocationLabel = new Label();
            invocationLabel.setOpaque(true);
            invocationLabel.setBackgroundColor(YELLOW);
            invocationLabel.setTextAlignment(PositionConstants.ALWAYS_LEFT);
            String methodName = invocation.getMethodName();
            String methodLabelText = createMethodLabelText(invocation);
            invocationLabel.setText(methodLabelText);
            if (invocation.getMaximum() > invocation.getAlarmThreshold())
            {
                invocationLabel.setForegroundColor(RED);
            }
            else if (invocation.getMaximum() > invocation.getWarningThreshold())
            {
                invocationLabel.setForegroundColor(ORANGE);
            }
            else if (invocation.getMaximum() < 0 && invocation.getAverage() == 0)
            {
                invocationLabel.setForegroundColor(GRAY);
            }
            else
            {
                invocationLabel.setForegroundColor(BLACK);
            }

            // �u�N���X���A���\�b�h���v�ŃL�[��ݒ肷��
            StringBuffer strKeyTemp = new StringBuffer();
            //strKeyTemp.append(model.getClassName());
            strKeyTemp.append(methodName);
            String strKey = strKeyTemp.toString();

            this.invocationLabelMap_.put(strKey, invocationLabel);
            this.invocationMap_.put(strKey, invocation);

            figure.add(invocationLabel);
        }

        layer.add(figure);

        return layer;
    }

    /**
     * �R���|�[�l���g�̃e�L�X�g���쐬����B
     * @param componentType �^�C�v�FWEB�ACLASS�DATABESE
     * @param className �N���X��
     * @return �R���|�[�l���g�̃e�L�X�g
     */
    private String createComponentText(ComponentType componentType, String className)
    {
        String componentText = "";
        if (componentType == ComponentType.WEB || componentType == ComponentType.DATABASE)
        {
            componentText = className;
        }
        else
        {
            int lastIndexOf = className.lastIndexOf(".");
            if (lastIndexOf >= 0)
            {
                componentText = className.substring(lastIndexOf + 1);
            }
            else
            {
                componentText = className;
            }
        }
        return componentText;
    }

    /**
     * �R���|�[�l���g�����擾����B
     *
     * @return �R���|�[�l���g��
     */
    public String getComponentName()
    {
        return this.componentName_;
    }

    /**
     * �w�肳�ꂽ���\�b�h���ɑΉ����郉�x�����擾����B
     *
     * @param methodName ���\�b�h��
     * @return ���x���B���x�����Ȃ��ꍇ�� <code>null</code>
     */
    public Label getMethodLabel(String methodName)
    {
        return this.invocationLabelMap_.get(methodName);
    }

    /**
     * �w�肳�ꂽ���\�b�h���ɑΉ����� Invocation ���擾����B
     *
     * @param methodName ���\�b�h��
     * @return Invocation �B������Ȃ��ꍇ�� <code>null</code>
     */
    public InvocationModel getInvocationModel(String methodName)
    {
        return this.invocationMap_.get(methodName);
    }

    /**
     * ���\�b�h�̃��x���ɕ\������e�L�X�g���쐬����B
     * @param invocation Invocation
     * @return ���\�b�h�̃��x��
     */
    private String createMethodLabelText(InvocationModel invocation)
    {
        String methodName;
        methodName = invocation.getMethodName();
        methodName = toStr(methodName, METHODNAME_MAXLENGTH);
        StringBuilder builder = new StringBuilder(methodName);

        builder.append("(");

        long max = invocation.getMaximum();
        if (max < 0)
        {
            builder.append("-");
            builder.append(":");
            builder.append("-");
        }
        else
        {
            builder.append(max);
            builder.append(":");
            long avg = invocation.getAverage();
            builder.append(avg);
        }

        builder.append(")");

        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public ConnectionAnchor getSourceConnectionAnchor(Request request)
    {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * {@inheritDoc}
     */
    public ConnectionAnchor getTargetConnectionAnchor(Request request)
    {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * {@inheritDoc}
     */
    public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection)
    {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * {@inheritDoc}
     */
    public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection)
    {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * �X�V
     */
    protected void refreshVisuals()
    {
        // ����̎擾
        Rectangle constraint = ((ComponentModel)getModel()).getConstraint();

        // Rectangle�I�u�W�F�N�g�𐧖�Ƃ��ăr���[�ɐݒ肷��
        // setLayoutConstraint���\�b�h�͐eEditPart����Ăяo��
        ((GraphicalEditPart)getParent()).setLayoutConstraint(this, getFigure(), constraint);
    }

    /**
     * {@inheritDoc}
     */
    protected void createEditPolicies()
    {
        installEditPolicy(EditPolicy.COMPONENT_ROLE,
                          new StatsVisionDeleteEditPolicy(this.statsVisionEditor_));
    }

    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        // �ύX�̌^�����f���̈ʒu���̕ύX���������̂��ǂ���
        if (evt.getPropertyName().equals(ComponentModel.P_CONSTRAINT))
        {
            refreshVisuals(); // �r���[���X�V����
        }
        else if (ComponentModel.P_SOURCE_CONNECTION.equals(evt.getPropertyName()))
        {
            refreshSourceConnections(); // �R�l�N�V�����E�\�[�X�̍X�V
        }
        else if (ComponentModel.P_TARGET_CONNECTION.equals(evt.getPropertyName()))
        {
            refreshTargetConnections(); // �R�l�N�V�����E�^�[�Q�b�g�̍X�V
        }
        else if (ComponentModel.P_EXCEEDED_THRESHOLD_ALARM.equals(evt.getPropertyName()))
        {
            String methodName = (String)evt.getNewValue();
            this.statsVisionEditor_.exceededThresholdAlarm(this.componentName_, methodName, this);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected List<AbstractConnectionModel> getModelSourceConnections()
    {
        // ����EditPart��ڑ����Ƃ���R�l�N�V�����E���f���̃��X�g��Ԃ�
        return ((ComponentModel)getModel()).getModelSourceConnections();
    }

    /**
     * {@inheritDoc}
     */
    protected List<? extends AbstractConnectionModel> getModelTargetConnections()
    {
        // ����EditPart��ڑ���Ƃ���R�l�N�V�����E���f���̃��X�g��Ԃ�
        return ((ComponentModel)getModel()).getModelTargetConnections();
    }
    
    /**
     * ���ʂ�\������B
     * @param result ����
     * @param length ����
     * @return ����
     */
    public static String toStr(String result, int length)
    {
        // ������null�̏ꍇ��"null"��Ԃ��B
        if (result == null)
        {
            return null;
        }

        if (length == 0)
        {
            result = "";
        }
        else if (result.length() > length)
        {
            result = result.substring(0, length) + "...";
        }

        return result;
    }

    /**
     * �w�i�F���擾����B
     * @return �w�i�F
     */
    public Color getBgColor()
    {
        return YELLOW;
    }

    /**
     * �r���[�̔w�i�F���擾����B
     * @param invocation Invocation
     * @return �r���[�̔w�i�F
     */
    public Color getFgColor(InvocationModel invocation)
    {
        if (invocation.getMaximum() > invocation.getAlarmThreshold())
        {
            return RED;
        }
        else if (invocation.getMaximum() > invocation.getWarningThreshold())
        {
            return ORANGE;
        }
        else
        {
            return ColorConstants.black;
        }
    }
}
