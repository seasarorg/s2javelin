package org.seasar.javelin.jmx.viewer.editpart;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Color;
import org.seasar.javelin.jmx.viewer.editors.EditPartWithListener;
import org.seasar.javelin.jmx.viewer.model.ComponentModel;
import org.seasar.javelin.jmx.viewer.model.InvocationModel;


public class ComponentEditPart extends EditPartWithListener implements
		NodeEditPart
{
	protected IFigure createFigure()
	{
		ComponentModel model = (ComponentModel) getModel();

	    Layer layer = new Layer();
	    layer.setBorder(new LineBorder());
	    layer.setOpaque(true);
	    layer.setBackgroundColor(new Color(null,255,255,206));
	    layer.setLayoutManager(new ToolbarLayout());

	    // コンポーネント名用ラベルの設定。
		Label label = new Label();

		// 背景色をオレンジに
		label.setBackgroundColor(new Color(null,255,255,206));

		// 背景色を不透明に
		label.setOpaque(true);
		
		// 表示文字列の決定
		String componentName = 
			model.getClassName().substring(
				model.getClassName().lastIndexOf(".") + 1);
		
		label.setText(componentName);
		layer.add(label);
		
		CompartmentFigure figure = new CompartmentFigure();
		figure.setBackgroundColor(new Color(null,255,255,206));
		
		for (InvocationModel invocation : model.getInvocationList())
		{
			Label invocationLabel = new Label();
			invocationLabel.setOpaque(true);
			invocationLabel.setBackgroundColor(new Color(null,255,255,206));
			invocationLabel.setTextAlignment(PositionConstants.ALWAYS_LEFT);
			invocationLabel.setText(
					invocation.getMethodName() 
					+ "(" 
					+ invocation.getAverage() 
					+ ")");
			
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
		// 制約の取得
		Rectangle constraint = ((ComponentModel) getModel()).getConstraint();

		// Rectangleオブジェクトを制約としてビューに設定する
		// setLayoutConstraintメソッドは親EditPartから呼び出す
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), constraint);
	}

	protected void createEditPolicies()
	{
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
