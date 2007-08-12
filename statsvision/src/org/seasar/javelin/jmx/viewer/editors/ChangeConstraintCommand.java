package org.seasar.javelin.jmx.viewer.editors;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.seasar.javelin.jmx.viewer.model.ComponentModel;


public class ChangeConstraintCommand extends Command
{
	private ComponentModel model_; // このコマンドによって変更されるモデル

	private Rectangle constraint; // 変更する制約

	private Rectangle oldConstraint; // 以前の制約

	// オーバーライド
	public void execute()
	{
		// モデルの制約を変更する
		constraint.height = -1;
		constraint.width  = -1;
		model_.setConstraint(constraint);
	}

	public void setConstraint(Rectangle rect)
	{
		constraint = rect;
	}

	public void setModel(Object model)
	{
		model_ = (ComponentModel) model;
		// 変更前の情報を記録
		oldConstraint = model_.getConstraint();
	}

	// オーバーライド
	public void undo()
	{
		model_.setConstraint(oldConstraint);
	}
}
