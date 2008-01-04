package org.seasar.javelin.statsvision.editors;

import java.awt.font.ImageGraphicAttribute;

import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
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

	private IToolBarManager toolBarManager;
	
	/**
	 * Creates a multi-page contributor.
	 */
	public MultiPageEditorContributor() {
		super();
	}

	/**
	 * Returns the action registed with the given text editor.
	 * 
	 * @return IAction or null if editor is null.
	 */
	protected IAction getAction(IEditorPart editor, String actionID)
	{
	    AbstractStatsVisionEditor statsEditor = (AbstractStatsVisionEditor)editor;
		return (editor == null ? null : statsEditor.getAction(actionID));
	}

	/*
	 * (non-JavaDoc) Method declared in
	 * AbstractMultiPageEditorActionBarContributor.
	 */
	public void setActivePage(IEditorPart part)
	{
		if (this.activeEditorPart == part)
		{
            return;
		}

		this.activeEditorPart = part;
		
		if (this.activeEditorPart instanceof StatsVisionEditor 
		        && !isInitialized)
        {
		    isInitialized = true;
		    
	        this.toolBarManager.add(getAction(this.activeEditorPart, GEFActionConstants.ZOOM_IN));
	        this.toolBarManager.add(getAction(this.activeEditorPart, GEFActionConstants.ZOOM_OUT));
	        
	        // 倍率を直接指定するコンボ・ボックスの追加
	        this.toolBarManager.add(new ZoomComboContributionItem(getPage()));      
	    }
	}

	private boolean isInitialized = false;
	
	/**
	 * 初回表示時に１度だけ呼び出される。
	 */
	public void contributeToToolBar(IToolBarManager toolBarManager)
	{
        this.toolBarManager = toolBarManager;
	}
}
