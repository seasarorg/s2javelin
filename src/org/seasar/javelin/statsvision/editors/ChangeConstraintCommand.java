package org.seasar.javelin.statsvision.editors;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.seasar.javelin.statsvision.model.ComponentModel;


public class ChangeConstraintCommand extends Command
{
	private ComponentModel model_; // ���̃R�}���h�ɂ���ĕύX����郂�f��

	private Rectangle constraint; // �ύX���鐧��

	private Rectangle oldConstraint; // �ȑO�̐���

	private StatsVisionEditor statsVisionEditor_;

	public ChangeConstraintCommand(StatsVisionEditor editor)
	{
		super();
		statsVisionEditor_ = editor;
	}
	
	// �I�[�o�[���C�h
	public void execute()
	{
		// ���f���̐����ύX����
		constraint.height = -1;
		constraint.width  = -1;
		model_.setConstraint(constraint);
		statsVisionEditor_.setDirty(true);
	}

	public void setConstraint(Rectangle rect)
	{
		constraint = rect;
	}

	public void setModel(Object model)
	{
		model_ = (ComponentModel) model;
		// �ύX�O�̏����L�^
		oldConstraint = model_.getConstraint();
	}

	// �I�[�o�[���C�h
	public void undo()
	{
		model_.setConstraint(oldConstraint);
	}
}
