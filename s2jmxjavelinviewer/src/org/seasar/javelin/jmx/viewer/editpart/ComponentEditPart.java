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

		// 外枠とマージンの設定
		label.setBorder(new CompoundBorder(new LineBorder(),
				new MarginBorder(3)));

		// 背景色をオレンジに
		label.setBackgroundColor(ColorConstants.yellow);

		// 背景色を不透明に
		label.setOpaque(true);
		
		// 表示文字列の決定
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
		// 制約の取得
		Rectangle constraint = ((ComponentModel) getModel()).getConstraint();

		// Rectangleオブジェクトを制約としてビューに設定する
		// setLayoutConstraintメソッドは親EditPartから呼び出す
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), constraint);
	}

	protected void createEditPolicies()
	{
		// TODO 自動生成されたメソッド・スタブ

	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		// 変更の型がモデルの位置情報の変更を示すものかどうか
		if (evt.getPropertyName().equals(ComponentModel.P_CONSTRAINT))
			refreshVisuals(); // ビューを更新する
		else if (evt.getPropertyName().equals(
				ComponentModel.P_SOURCE_CONNECTION))
			refreshSourceConnections(); // コネクション・ソースの更新
		else if (evt.getPropertyName().equals(
				ComponentModel.P_TARGET_CONNECTION))
			refreshTargetConnections(); // コネクション・ターゲットの更新
	}

	protected List getModelSourceConnections()
	{
		// このEditPartを接続元とするコネクション・モデルのリストを返す
		return ((ComponentModel) getModel()).getModelSourceConnections();
	}

	protected List getModelTargetConnections()
	{
		// このEditPartを接続先とするコネクション・モデルのリストを返す
		return ((ComponentModel) getModel()).getModelTargetConnections();
	}
}
