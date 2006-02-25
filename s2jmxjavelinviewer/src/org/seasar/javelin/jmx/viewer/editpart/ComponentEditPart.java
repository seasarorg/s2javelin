package org.seasar.javelin.jmx.viewer.editpart;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.seasar.javelin.jmx.viewer.editors.EditPartWithListener;
import org.seasar.javelin.jmx.viewer.model.ComponentModel;
import org.seasar.javelin.jmx.viewer.model.InvocationModel;


public class ComponentEditPart extends EditPartWithListener implements
		NodeEditPart
{

	protected IFigure createFigure()
	{
		ComponentModel model = (ComponentModel) getModel();

		Label label = new Label();

		// �O�g�ƃ}�[�W���̐ݒ�
		label.setBorder(new CompoundBorder(new LineBorder(),
				new MarginBorder(3)));

		// �w�i�F���I�����W��
		label.setBackgroundColor(ColorConstants.yellow);

		// �w�i�F��s������
		label.setOpaque(true);
		
		// �\��������̌���
		StringBuilder builder = new StringBuilder(256);
		builder.append(model.getClassName().substring(
				model.getClassName().lastIndexOf(".") + 1));
		for (InvocationModel invocation : model.getInvocationList())
		{
			builder.append("\n");
			builder.append(invocation.getMethodName());
			builder.append("(");
			builder.append(invocation.getAverage());
			builder.append(")");
		}
		label.setText(builder.toString());

		return label;
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
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		// �ύX�̌^�����f���̈ʒu���̕ύX���������̂��ǂ���
		if (evt.getPropertyName().equals(ComponentModel.P_CONSTRAINT))
			refreshVisuals(); // �r���[���X�V����
		else if (evt.getPropertyName().equals(
				ComponentModel.P_SOURCE_CONNECTION))
			refreshSourceConnections(); // �R�l�N�V�����E�\�[�X�̍X�V
		else if (evt.getPropertyName().equals(
				ComponentModel.P_TARGET_CONNECTION))
			refreshTargetConnections(); // �R�l�N�V�����E�^�[�Q�b�g�̍X�V
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
}
