package org.seasar.javelin.jmx.viewer.editpart;

import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Color;

public class AlarmJob implements Runnable
{
	Label label_;
	Color bg_;
	Color fg_;
	
	public AlarmJob(Label label, Color fg, Color bg)
	{
		label_ = label;
		fg_ = fg;
		bg_ = bg;
	}

	public void run()
	{
		label_.setForegroundColor(fg_);
		label_.setBackgroundColor(bg_);
	}
}
