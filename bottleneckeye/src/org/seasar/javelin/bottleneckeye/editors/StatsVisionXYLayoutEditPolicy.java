package org.seasar.javelin.bottleneckeye.editors;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

public class StatsVisionXYLayoutEditPolicy extends XYLayoutEditPolicy
{
	private StatsVisionEditor statsVisionEditor_;

	public StatsVisionXYLayoutEditPolicy(StatsVisionEditor editor)
	{
		super();
		statsVisionEditor_ = editor;
	}
	
	protected Command createAddCommand(EditPart child, Object constraint)
	{
		return null;
	}

	protected Command createChangeConstraintCommand(
			EditPart child,
			Object constraint)
	{
		// コマンドの作成
		ChangeConstraintCommand command = 
			new ChangeConstraintCommand(statsVisionEditor_);
		// 編集対象のモデルの設定
		command.setModel(child.getModel());
		command.setConstraint((Rectangle) constraint);
		// コマンドを返す
		return command;
	}

	protected Command getCreateCommand(CreateRequest request)
	{
		return null;
	}

	protected Command getDeleteDependantCommand(Request request)
	{
		return null;
	}

}
