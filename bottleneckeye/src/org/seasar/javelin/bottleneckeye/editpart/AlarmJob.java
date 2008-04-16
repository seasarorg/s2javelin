package org.seasar.javelin.bottleneckeye.editpart;

import java.util.Map;

import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Color;

public class AlarmJob implements Runnable
{
    private String className_;
    private String methodName_;
    private Map<String, ComponentEditPart> componentEditPartMap_;
	private Color bg_;
	private Color fg_;
	
	public AlarmJob(String className, String methodName, Map<String, ComponentEditPart> componentEditPartMap, Color fg, Color bg)
	{
	    this.className_ = className;
	    this.methodName_ = methodName;
		this.componentEditPartMap_ = componentEditPartMap;
		this.fg_ = fg;
		this.bg_ = bg;
	}

	public void run()
	{
        ComponentEditPart editPart = this.componentEditPartMap_.get(this.className_);
	    if (editPart != null)
	    {
            Label label = editPart.getMethodLabel(this.methodName_);
            if (label != null)
            {
                label.setForegroundColor(this.fg_);
                label.setBackgroundColor(this.bg_);
            }
	    }
	}
}
