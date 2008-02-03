package org.seasar.javelin.bottleneckeye.editpart;

import org.eclipse.draw2d.Label;

public class LabelUpdateJob implements Runnable
{
	private Label label_;
	private String text_;

	public LabelUpdateJob(Label label, String text)
	{
		label_ = label;
		text_  = text;
	}

	public void run()
	{
		label_.setText(text_);
	}

}
