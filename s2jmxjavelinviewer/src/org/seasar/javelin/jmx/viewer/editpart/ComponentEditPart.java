package org.seasar.javelin.jmx.viewer.editpart;

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
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.seasar.javelin.jmx.viewer.editors.EditPartWithListener;
import org.seasar.javelin.jmx.viewer.model.ComponentModel;
import org.seasar.javelin.jmx.viewer.model.InvocationModel;


public class ComponentEditPart extends EditPartWithListener implements
		NodeEditPart
{
	/** �N���X�̔w�i�F�B */
	private static final Color YELLOW = new Color(null,255, 255, 206);
	
	/** ���W���[�x���F�B */
	private static final Color RED = ColorConstants.red;
    
	/** �}�C�i�[�x���F�B */
	private static final Color ORANGE = new Color(null,224, 160,   0);
	
	/** ���W���[�u�����N */
	/** �}�C�i�[�u�����N */
    
    HashMap<String, Label> invocationLabelMap = new HashMap<String, Label>();
    HashMap<String, InvocationModel> invocationMap = new HashMap<String, InvocationModel>();
    
	protected IFigure createFigure()
	{
		ComponentModel model = (ComponentModel) getModel();

	    Layer layer = new Layer();
	    layer.setBorder(new LineBorder());
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
		String componentName = "";
		if (model.getClassName().indexOf("/") > -1)
		{
//			componentName = 
//				model.getClassName().substring(
//					model.getClassName().lastIndexOf("/") + 1);
			componentName = model.getClassName();
		}
		else
		{
			componentName = 
				model.getClassName().substring(
					model.getClassName().lastIndexOf(".") + 1);
		}
		
		label.setText(componentName);
		layer.add(label);
        
		CompartmentFigure figure = new CompartmentFigure();
		figure.setBackgroundColor(YELLOW);
		
		for (InvocationModel invocation : model.getInvocationList())
		{
			Label invocationLabel = new Label();
			invocationLabel.setOpaque(true);
			invocationLabel.setBackgroundColor(YELLOW);
			invocationLabel.setTextAlignment(PositionConstants.ALWAYS_LEFT);
			invocationLabel.setText(
					invocation.getMethodName() 
					+ "(" 
					+ invocation.getMaximum() 
					+ ":"
					+ invocation.getAverage()
					+ ")");
			if (invocation.getMaximum() > invocation.getAlarmThreshold())
			{
				invocationLabel.setForegroundColor(RED);
			}
			else if (invocation.getMaximum() > invocation.getWarningThreshold())
			{
				invocationLabel.setForegroundColor(ORANGE);
			}
			else
			{
				invocationLabel.setForegroundColor(ColorConstants.black);
			}
			
            invocationLabelMap.put(invocation.getMethodName(), invocationLabel);
            invocationMap.put(invocation.getMethodName(), invocation);
            
			figure.add(invocationLabel);
		}
		
		layer.add(figure);
		
		return layer;
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request)
	{
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request)
	{
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection)
	{
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection)
	{
		return new ChopboxAnchor(getFigure());
	}

	protected void refreshVisuals()
	{
		// ����̎擾
		Rectangle constraint = ((ComponentModel) getModel()).getConstraint();

		// Rectangle�I�u�W�F�N�g�𐧖�Ƃ��ăr���[�ɐݒ肷��
		// setLayoutConstraint���\�b�h�͐eEditPart����Ăяo��
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), constraint);
	}

	protected void createEditPolicies()
	{
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		// �ύX�̌^�����f���̈ʒu���̕ύX���������̂��ǂ���
		if (evt.getPropertyName().equals(ComponentModel.P_CONSTRAINT))
        {
			refreshVisuals(); // �r���[���X�V����
        }
		else if (evt.getPropertyName().equals(
				ComponentModel.P_SOURCE_CONNECTION))
        {
			refreshSourceConnections(); // �R�l�N�V�����E�\�[�X�̍X�V
        }
		else if (evt.getPropertyName().equals(
				ComponentModel.P_TARGET_CONNECTION))
        {
			refreshTargetConnections(); // �R�l�N�V�����E�^�[�Q�b�g�̍X�V
        }
        else if (evt.getPropertyName().equals(
                ComponentModel.P_EXCEEDED_THRESHOLD_ALARM))
        {
            String methodName = (String) evt.getNewValue();
            exceededThresholdAlarm(methodName);
        }
	}

	protected List getModelSourceConnections()
	{
		// ����EditPart��ڑ����Ƃ���R�l�N�V�����E���f���̃��X�g��Ԃ�
		return ((ComponentModel) getModel()).getModelSourceConnections();
	}

	protected List getModelTargetConnections()
	{
		// ����EditPart��ڑ���Ƃ���R�l�N�V�����E���f���̃��X�g��Ԃ�
		return ((ComponentModel) getModel()).getModelTargetConnections();
	}
    
    private void exceededThresholdAlarm(String methodName)
    {
        Label label = invocationLabelMap.get(methodName);

        InvocationModel invocation = invocationMap.get(methodName);

        Display display = getViewer().getControl().getDisplay();
        
        String message =
			invocation.getMethodName() 
			+ "(" 
			+ invocation.getMaximum() 
			+ ":"
			+ invocation.getAverage()
			+ ")";
        
        display.asyncExec(new LabelUpdateJob(label, message));

        Runnable blinkJob = new Blinker(display, label, ColorConstants.black, RED);
        Thread blinker = new Thread(blinkJob);
        blinker.start();
        
        ((ComponentModel) getModel()).setExceededThresholdAlarm(null);
    }
}
