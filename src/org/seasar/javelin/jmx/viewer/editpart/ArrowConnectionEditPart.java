package org.seasar.javelin.jmx.viewer.editpart;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;

public class ArrowConnectionEditPart extends S2JmxJavelinAbstractConnectionEditPart
{
	// オーバーライド
	protected IFigure createFigure()
	{
		// コネクションフィギュアの作成
		PolylineConnection connection = new PolylineConnection();
		// ターゲット側に矢印の装飾を施す
		connection.setTargetDecoration(new PolygonDecoration());
		
		return connection;
	}

}
