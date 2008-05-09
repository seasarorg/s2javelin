package org.seasar.javelin.bottleneckeye.editpart;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FanRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.seasar.javelin.bottleneckeye.editors.EditPartWithListener;
import org.seasar.javelin.bottleneckeye.editors.MultiPageEditor;
import org.seasar.javelin.bottleneckeye.editors.StatsVisionXYLayoutEditPolicy;
import org.seasar.javelin.bottleneckeye.editors.view.StatsVisionEditor;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;


public class ContentsEditPart extends EditPartWithListener
{
    private StatsVisionEditor statsVisionEditor_;
	
	public ContentsEditPart(StatsVisionEditor editor)
	{
		super();
		this.statsVisionEditor_ = editor;
	}
	
	protected IFigure createFigure()
	{
	    Layer figure = new Layer();
	    figure.setLayoutManager(new XYLayout());
	    
	    ConnectionLayer connectionLayer = 
	    	(ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
	    
	    
        if (MultiPageEditor.LINE_STYLE_SHORTEST.equals(this.statsVisionEditor_.getLineStyle()))
        {
            connectionLayer.setConnectionRouter(new ShortestPathConnectionRouter(figure));
        }
        else if (MultiPageEditor.LINE_STYLE_FAN.equals(this.statsVisionEditor_.getLineStyle()))
        {
            connectionLayer.setConnectionRouter(new FanRouter());
        }
        else if (MultiPageEditor.LINE_STYLE_MANHATTAN.equals(this.statsVisionEditor_.getLineStyle()))
        {
            connectionLayer.setConnectionRouter(new ManhattanConnectionRouter());
        }
        else
        {
            connectionLayer.setConnectionRouter(null);
        }
	    
	    return figure;
	}

	protected void createEditPolicies()
	{
	    installEditPolicy(
	    		EditPolicy.LAYOUT_ROLE,
	    		new StatsVisionXYLayoutEditPolicy(this.statsVisionEditor_));
	}

	/**
	 * {@inheritDoc}
	 */
	protected List<ComponentModel> getModelChildren()
	{
    	return ((ContentsModel)getModel()).getChildren();
	}

    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (ContentsModel.P_CHILDREN.equals(evt.getPropertyName()))
        {
            refreshChildren();
        }
    } 
}
