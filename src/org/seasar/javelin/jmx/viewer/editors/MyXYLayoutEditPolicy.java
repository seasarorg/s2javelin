package org.seasar.javelin.jmx.viewer.editors;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

public class MyXYLayoutEditPolicy extends XYLayoutEditPolicy
{

	protected Command createAddCommand(EditPart child, Object constraint)
	{
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return null;
	}

	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint)
	{
		// �R�}���h�̍쐬
		ChangeConstraintCommand command = new ChangeConstraintCommand();
		// �ҏW�Ώۂ̃��f���̐ݒ�
		command.setModel(child.getModel());
		command.setConstraint((Rectangle) constraint);
		// �R�}���h��Ԃ�
		return command;
	}

	protected Command getCreateCommand(CreateRequest request)
	{
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return null;
	}

	protected Command getDeleteDependantCommand(Request request)
	{
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return null;
	}

}
