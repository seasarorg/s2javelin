package org.seasar.javelin.statsvision.editpart;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.seasar.javelin.statsvision.editors.StatsVisionXYLayoutEditPolicy;
import org.seasar.javelin.statsvision.model.ContentsModel;


public class ContentsEditPart extends AbstractGraphicalEditPart
{

	protected IFigure createFigure()
	{
	    Layer figure = new Layer();
	    figure.setLayoutManager(new XYLayout());
	    return figure;
	}

	protected void createEditPolicies()
	{
	    installEditPolicy(EditPolicy.LAYOUT_ROLE, new StatsVisionXYLayoutEditPolicy());
	}
	
	protected List getModelChildren() {
    	return ((ContentsModel) getModel()).getChildren();
	}
    
    
}
