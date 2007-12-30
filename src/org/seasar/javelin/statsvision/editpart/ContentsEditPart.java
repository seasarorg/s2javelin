package org.seasar.javelin.statsvision.editpart;

import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.seasar.javelin.statsvision.editors.StatsVisionEditor;
import org.seasar.javelin.statsvision.editors.StatsVisionXYLayoutEditPolicy;
import org.seasar.javelin.statsvision.model.ContentsModel;


public class ContentsEditPart extends AbstractGraphicalEditPart
{
	private StatsVisionEditor statsVisionEditor_;
	
	public ContentsEditPart(StatsVisionEditor editor)
	{
		super();
		statsVisionEditor_ = editor;
	}
	
	protected IFigure createFigure()
	{
	    Layer figure = new Layer();
	    figure.setLayoutManager(new XYLayout());
	    
	    ConnectionLayer connectionLayer = 
	    	(ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
	    connectionLayer.setConnectionRouter(new ShortestPathConnectionRouter(figure));
	    
	    return figure;
	}

	protected void createEditPolicies()
	{
	    installEditPolicy(
	    		EditPolicy.LAYOUT_ROLE
	    		, new StatsVisionXYLayoutEditPolicy(statsVisionEditor_));
	}
	
	protected List getModelChildren() {
    	return ((ContentsModel) getModel()).getChildren();
	}   
}
