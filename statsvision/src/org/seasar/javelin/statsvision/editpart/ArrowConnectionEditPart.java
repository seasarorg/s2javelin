package org.seasar.javelin.statsvision.editpart;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;

public class ArrowConnectionEditPart extends StatsVisionAbstractConnectionEditPart
{
	// �I�[�o�[���C�h
	protected IFigure createFigure()
	{
		// �R�l�N�V�����t�B�M���A�̍쐬
		PolylineConnection connection = new PolylineConnection();
		// �^�[�Q�b�g���ɖ��̑������{��
		connection.setTargetDecoration(new PolygonDecoration());
		
		return connection;
	}

}
