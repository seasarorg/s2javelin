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
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint)
	{
		// コマンドの作成
		ChangeConstraintCommand command = new ChangeConstraintCommand();
		// 編集対象のモデルの設定
		command.setModel(child.getModel());
		command.setConstraint((Rectangle) constraint);
		// コマンドを返す
		return command;
	}

	protected Command getCreateCommand(CreateRequest request)
	{
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	protected Command getDeleteDependantCommand(Request request)
	{
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
