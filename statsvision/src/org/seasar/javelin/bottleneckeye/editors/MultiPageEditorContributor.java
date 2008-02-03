package org.seasar.javelin.bottleneckeye.editors;

import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

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
	
	private IMenuManager    menuManager;
	
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
		        && !this.isInitialized)
        {
		    this.isInitialized = true;
		    
            this.toolBarManager.add(new Separator());
//            this.toolBarManager.add(getAction(this.activeEditorPart, GEFActionConstants.ZOOM_IN));
//            this.toolBarManager.add(getAction(this.activeEditorPart, GEFActionConstants.ZOOM_OUT));
	        
	        // 倍率を直接指定するコンボ・ボックスの追加
	        this.toolBarManager.add(new ZoomComboContributionItem(getPage()));      
	        
//	        this.menuManager.add(new PrintAction(activeEditorPart));
	    }
	}

	private boolean isInitialized = false;
	
	/**
	 * 初回表示時に１度だけ呼び出される。
	 */
	public void contributeToToolBar(IToolBarManager toolBarManager)
	{
	    super.contributeToToolBar(toolBarManager);
        this.toolBarManager = toolBarManager;
	}

    @Override
    public void contributeToMenu(IMenuManager menuManager)
    {
        super.contributeToMenu(menuManager);
        this.menuManager = menuManager;
    }
}
