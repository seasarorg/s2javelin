package org.seasar.javelin.bottleneckeye.editpart;

import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class Blinker implements Runnable
{
	Display display_;
	Label label_;
	Color bg_;
	Color fg_;
	Color orgBg_;
	Color orgFg_;

	public Blinker(Display display, Label label, Color fg, Color bg, Color orgFg, Color orgBg)
	{
		display_ = display;
		label_ = label;
		fg_ = fg;
		bg_ = bg;
		orgFg_ = orgFg;
		orgBg_ = orgBg;
	}

	public void run()
	{
		for (int index = 0; index < 5; index++)
		{
			Runnable alarm;
	        alarm = new AlarmJob(label_, fg_, bg_);
	        display_.asyncExec(alarm);
	        
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
			
	        alarm = new AlarmJob(label_, orgFg_, orgBg_);
	        display_.asyncExec(alarm);
	        
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
		}
	}
}
