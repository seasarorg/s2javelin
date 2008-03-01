package org.seasar.javelin.bottleneckeye.editpart;

import java.util.TimerTask;

import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class Blinker extends TimerTask
{
    Display display_;

    Label   label_;

    Color   bg_;

    Color   fg_;

    public Blinker(Display display, Label label, Color fg, Color bg)
    {
        display_ = display;
        label_ = label;
        fg_ = fg;
        bg_ = bg;
    }

    public void run()
    {
        Runnable alarm;
        alarm = new AlarmJob(label_, fg_, bg_);
        display_.asyncExec(alarm);
    }
}
