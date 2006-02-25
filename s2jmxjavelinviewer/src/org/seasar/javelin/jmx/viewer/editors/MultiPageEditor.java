package org.seasar.javelin.jmx.viewer.editors;


import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * An example showing how to create a multi-page editor.
 * This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class MultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener{

	/** The text editor used in page 0. */
	private S2JmxJavelinEditor editor;

	/**
	 * Creates a multi-page editor example.
	 */
	public MultiPageEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	/**
	 * Creates page 0 of the multi-page editor,
	 * which contains a text editor.
	 */
	void createPage0() {
		try {
			editor = new S2JmxJavelinEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, "View");
//			setPageText(index, editor.getTitle());
		} catch (PartInitException e) {
			ErrorDialog.openError(
				getSite().getShell(),
				"Error creating nested text editor",
				null,
				e.getStatus());
		}
	}
	/**
	 * Creates page 1 of the multi-page editor,
	 * which allows you to change the font used in page 2.
	 */
	void createPage1() {

		Composite composite = new Composite(getContainer(), SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 2;

		//------------------------------------------------------------
		Label hostLabel = new Label(composite, SWT.NONE);
		hostLabel.setText("Host:");
		GridData hostGrid = new GridData(GridData.BEGINNING);
		hostGrid.horizontalSpan = 1;
		hostLabel.setLayoutData(hostGrid);

		//------------------------------------------------------------
		Text  hostText  = new Text(composite, SWT.NONE);
		hostGrid = new GridData(GridData.BEGINNING);
		hostGrid.horizontalSpan = 1;
		hostText.setLayoutData(hostGrid);

		//------------------------------------------------------------
		Label portLabel = new Label(composite, SWT.NONE);
		portLabel.setText("Port:");
		GridData portGrid = new GridData(GridData.BEGINNING);
		portGrid.horizontalSpan = 1;
		portLabel.setLayoutData(hostGrid);
		
		//------------------------------------------------------------
		Text  portText  = new Text(composite, SWT.NONE);
		portGrid = new GridData(GridData.BEGINNING);
		portGrid.horizontalSpan = 1;
		portText.setLayoutData(portGrid);

		//------------------------------------------------------------
		Label domainLabel = new Label(composite, SWT.NONE);
		domainLabel.setText("Domain:");
		GridData domainGrid = new GridData(GridData.BEGINNING);
		domainGrid.horizontalSpan = 1;
		domainLabel.setLayoutData(hostGrid);
		
		//------------------------------------------------------------
		Text  domainText  = new Text(composite, SWT.NONE);
		domainGrid = new GridData(GridData.BEGINNING);
		domainGrid.horizontalSpan = 1;
		domainText.setLayoutData(domainGrid);

		//------------------------------------------------------------
		Label intervalLabel = new Label(composite, SWT.NONE);
		intervalLabel.setText("Interval:");
		GridData intervalGrid = new GridData(GridData.BEGINNING);
		intervalGrid.horizontalSpan = 1;
		intervalLabel.setLayoutData(hostGrid);
		
		//------------------------------------------------------------
		Text  intervalText  = new Text(composite, SWT.NONE);
		intervalGrid = new GridData(GridData.BEGINNING);
		intervalGrid.horizontalSpan = 1;
		intervalText.setLayoutData(domainGrid);

		//------------------------------------------------------------
		Button reloadButton = new Button(composite, SWT.NONE);
		GridData reloadGrid = new GridData(GridData.BEGINNING);
		reloadGrid.horizontalSpan = 2;
		reloadButton.setLayoutData(reloadGrid);
		reloadButton.setText("Reload");
		
		reloadButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
			}
		});

		int index = addPage(composite);
		setPageText(index, "Properties");
	}
	
	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
	}
	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}
	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
		throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}
	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == 2) {
//			sortWords();
		}
	}
	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event){
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE){
			Display.getDefault().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i<pages.length; i++){
						if(((FileEditorInput)editor.getEditorInput()).getFile().getProject().equals(event.getResource())){
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}
}
