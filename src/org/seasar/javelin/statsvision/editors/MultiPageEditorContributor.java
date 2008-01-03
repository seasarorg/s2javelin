package org.seasar.javelin.statsvision.editors;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Manages the installation/deinstallation of global actions for multi-page
 * editors. Responsible for the redirection of global actions to the active
 * editor. Multi-page contributor replaces the contributors for the individual
 * editors in the multi-page editor.
 */
public class MultiPageEditorContributor extends
		MultiPageEditorActionBarContributor
{
	private IEditorPart activeEditorPart;

	/**
	 * Creates a multi-page contributor.
	 */
	public MultiPageEditorContributor() {
		super();
		createActions();
	}

	/**
	 * Returns the action registed with the given text editor.
	 * 
	 * @return IAction or null if editor is null.
	 */
	protected IAction getAction(ITextEditor editor, String actionID)
	{
		return (editor == null ? null : editor.getAction(actionID));
	}

	/*
	 * (non-JavaDoc) Method declared in
	 * AbstractMultiPageEditorActionBarContributor.
	 */

	public void setActivePage(IEditorPart part)
	{
		if (activeEditorPart == part)
		{
            return;
		}

		activeEditorPart = part;
	}

	private void createActions()
	{
        IActionBars actionBars = getActionBars();
        if (actionBars != null)
        {
        }
 	}

	public void contributeToToolBar(IToolBarManager toolBarManager)
	{
	    toolBarManager.add(new Separator());
	    
        IActionBars actionBars = getActionBars();
        if (actionBars != null)
        {
        }
	}
}
