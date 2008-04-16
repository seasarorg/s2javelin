package org.seasar.javelin.bottleneckeye.editpart;

import java.util.Map;
import java.util.TimerTask;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class Blinker extends TimerTask
{
    private Display display_;

    private Map<String, ComponentEditPart> componentEditPartMap_;

    private String className_;

    private String methodName_;

    private Color bg_;

    private Color fg_;

    public Blinker(Display display, String className, String methodName, Map<String, ComponentEditPart> componentEditPartMap, Color fg, Color bg)
    {
        this.display_ = display;
        this.componentEditPartMap_ = componentEditPartMap;
        this.className_ = className;
        this.methodName_ = methodName;
        this.fg_ = fg;
        this.bg_ = bg;
    }

    public void run()
    {
        Runnable alarm = new AlarmJob(this.className_, this.methodName_, this.componentEditPartMap_, this.fg_, this.bg_);
        this.display_.asyncExec(alarm);
    }
}
