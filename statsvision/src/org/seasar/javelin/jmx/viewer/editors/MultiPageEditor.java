package org.seasar.javelin.jmx.viewer.editors;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
			editor.setHostName(host_);
			editor.setPortNum(port_);
			editor.setDomain(domain_);
			editor.setWarningThreshold(warningThreshold_);
			editor.setAlarmThreshold(alarmThreshold_);
			
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
	
	private Label hostLabel_;
	private Label portLabel_;
	private Label domainLabel_;
	private Label warningLabel_;
	private Label alarmLabel_;
	
	private Text  hostText_;
	private Text  portText_;
	private Text  domainText_;
	private Text  warningText_;
	private Text  alarmText_;
	
	private String host_   = "";
	private int    port_;
	private String domain_ = "";
	private long   warningThreshold_;
	private long   alarmThreshold_;
	
	/**
	 * Creates page 1 of the multi-page editor,
	 * which allows you to change the font used in page 2.
	 */
	void createPage1() {

		Composite composite = new Composite(getContainer(), SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 4;

		//------------------------------------------------------------
		hostLabel_ = new Label(composite, SWT.NONE);
		hostLabel_.setText("Host:");
		GridData hostGrid = new GridData(GridData.BEGINNING);
		hostGrid.horizontalSpan = 2;
		hostLabel_.setLayoutData(hostGrid);

		//------------------------------------------------------------
		hostText_  = new Text(composite, SWT.NONE);
		hostText_.setText(host_);
		hostGrid = new GridData(GridData.BEGINNING);
		hostGrid.horizontalSpan = 2;
		hostText_.setLayoutData(hostGrid);

		//------------------------------------------------------------
		portLabel_ = new Label(composite, SWT.NONE);
		portLabel_.setText("Port:");
		GridData portGrid = new GridData(GridData.BEGINNING);
		portGrid.horizontalSpan = 2;
		portLabel_.setLayoutData(portGrid);
		
		//------------------------------------------------------------
		portText_  = new Text(composite, SWT.NONE);
		portText_.setText(Integer.toString(port_));
		portGrid = new GridData(GridData.BEGINNING);
		portGrid.horizontalSpan = 2;
		portText_.setLayoutData(portGrid);

		//------------------------------------------------------------
		domainLabel_ = new Label(composite, SWT.NONE);
		domainLabel_.setText("Domain:");
		GridData domainGrid = new GridData(GridData.BEGINNING);
		domainGrid.horizontalSpan = 2;
		domainLabel_.setLayoutData(domainGrid);
		
		//------------------------------------------------------------
		domainText_  = new Text(composite, SWT.NONE);
		domainText_.setText(domain_);
		domainGrid = new GridData(GridData.BEGINNING);
		domainGrid.horizontalSpan = 2;
		domainText_.setLayoutData(domainGrid);

		//------------------------------------------------------------
		warningLabel_ = new Label(composite, SWT.NONE);
		warningLabel_.setText("Warning:");
		GridData warningGrid = new GridData(GridData.BEGINNING);
		warningGrid.horizontalSpan = 2;
		warningLabel_.setLayoutData(warningGrid);
		
		//------------------------------------------------------------
		warningText_ = new Text(composite, SWT.NONE);
		warningText_.setText(Long.toString(warningThreshold_));
		warningGrid = new GridData(GridData.BEGINNING);
		warningGrid.horizontalSpan = 2;
		warningText_.setLayoutData(warningGrid);

		//------------------------------------------------------------
		alarmLabel_ = new Label(composite, SWT.NONE);
		alarmLabel_.setText("Alarm:");
		GridData alarmGrid = new GridData(GridData.BEGINNING);
		alarmGrid.horizontalSpan = 2;
		alarmLabel_.setLayoutData(alarmGrid);
		
		//------------------------------------------------------------
		alarmText_ = new Text(composite, SWT.NONE);
		alarmText_.setText(Long.toString(alarmThreshold_));
		alarmGrid = new GridData(GridData.BEGINNING);
		alarmGrid.horizontalSpan = 2;
		alarmText_.setLayoutData(alarmGrid);

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
						editor.setHostName(hostText_.getText());
						editor.setPortNum(Integer.parseInt(portText_.getText()));
						editor.setDomain(domainText_.getText());
						editor.setWarningThreshold(warningThreshold_);
						editor.setAlarmThreshold(alarmThreshold_);
						
						editor.initializeGraphicalViewer();
					}
				});

		//------------------------------------------------------------
		Button resetButton = new Button(composite, SWT.NONE);
		GridData resetGrid = new GridData(GridData.BEGINNING);
		resetGrid.horizontalSpan = 2;
		resetButton.setLayoutData(reloadGrid);
		resetButton.setText("Reset");
		
		resetButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				try
				{
					JMXServiceURL url = new JMXServiceURL(
							"service:jmx:rmi:///jndi/rmi://" 
							+ hostText_.getText()
							+ ":" 
							+ portText_.getText()
							+ "/jmxrmi");
					JMXConnector connector = JMXConnectorFactory.connect(url);
					MBeanServerConnection connection = 
						connector.getMBeanServerConnection();

					ObjectName objName = 
						new ObjectName(
							domain_ 
							+ ".container:type=org.seasar.javelin.jmx.bean.ContainerMBean");
					Set set = connection.queryMBeans(objName, null);
					if (set.size() == 0)
					{
						return;
					}
					
					ObjectInstance instance = (ObjectInstance) set.toArray()[0];

					connection.invoke(instance.getObjectName(), "reset", null, null);
				}
				catch (Exception ex)
				{
					;
				}
			}
		});

		int index = addPage(composite);
		setPageText(index, "Settings");
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
		throws PartInitException
	{
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
		
		IFileEditorInput input = (IFileEditorInput)editorInput;

		try
		{
			InputStream stream = input.getFile().getContents();
			InputStreamReader reader = new InputStreamReader(stream);
			BufferedReader bufferedReader = new BufferedReader(reader);
			
			host_             = bufferedReader.readLine();
			port_             = Integer.parseInt(bufferedReader.readLine());
			domain_           = bufferedReader.readLine();
			warningThreshold_ = Integer.parseInt(bufferedReader.readLine());
			alarmThreshold_   = Integer.parseInt(bufferedReader.readLine());
		}
		catch (CoreException e)
		{
			host_   = "";
			domain_ = "";
			e.printStackTrace();
		}
		catch (IOException e)
		{
			host_   = "";
			domain_ = "";
			e.printStackTrace();
		}
		catch(NumberFormatException e)
		{
			host_   = "";
			domain_ = "";
			e.printStackTrace();
		}
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
