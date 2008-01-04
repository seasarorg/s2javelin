package org.seasar.javelin.statsvision.editors;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PrintFigureOperation;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.print.PrintGraphicalViewerOperation;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
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
 * An example showing how to create a multi-page editor. This example has 3
 * pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class MultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener
{

    private static final String MODE_JMX = "JMX";

    private static final String MODE_TCP = "TCP";

    /** The text editor used in page 0. */
    private StatsVisionEditor   editor;

    /**
     * Creates a multi-page editor example.
     */
    public MultiPageEditor()
    {
        super();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    /**
     * Creates page 0 of the multi-page editor, which contains a text editor.
     */
    void createPage0()
    {
        try
        {
            if (MODE_JMX.equalsIgnoreCase(this.mode_))
            {
                this.editor = new JmxStatsVisionEditor();
            }
            else
            {
                this.mode_ = MODE_TCP;
                this.editor = new TcpStatsVisionEditor();
            }
            this.editor.setHostName(this.host_);
            this.editor.setPortNum(this.port_);
            this.editor.setDomain(this.domain_);
            this.editor.setWarningThreshold(this.warningThreshold_);
            this.editor.setAlarmThreshold(this.alarmThreshold_);
            this.editor.setMode(this.mode_);

            int index = addPage(this.editor, getEditorInput());
            setPageText(index, "View");
            // setPageText(index, editor.getTitle());
        }
        catch (PartInitException e)
        {
            ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null,
                                  e.getStatus());
        }
    }

    private Label  hostLabel_;

    private Label  portLabel_;

    private Label  domainLabel_;

    private Label  warningLabel_;

    private Label  alarmLabel_;

    private Label  modeLabel_;

    private Text   hostText_;

    private Text   portText_;

    private Text   domainText_;

    private Text   warningText_;

    private Text   alarmText_;

    private Text   modeText_;

    private Button startButton_;

    private Button stopButton_;

    private Button resetButton_;

    private Button reloadButton_;

    private Button printButton_;

    private Button copyButton_;
    
    private String host_   = "";

    private int    port_;

    private String domain_ = "";

    private long   warningThreshold_;

    private long   alarmThreshold_;

    /** モード */
    String         mode_   = MODE_TCP;

    /**
     * Creates page 1 of the multi-page editor, which allows you to change the
     * font used in page 2.
     */
    void createPage1()
    {

        Label spacerLabel;
        GridData spacerGrid;

        Composite composite = new Composite(getContainer(), SWT.NONE);
        GridLayout layout = new GridLayout(12, true);
        composite.setLayout(layout);

        // ------------------------------------------------------------
        this.hostLabel_ = new Label(composite, SWT.NONE);
        this.hostLabel_.setText("Host:");
        GridData hostGrid = new GridData(GridData.BEGINNING);
        hostGrid.horizontalSpan = 2;
        this.hostLabel_.setLayoutData(hostGrid);

        // ------------------------------------------------------------
        this.hostText_ = new Text(composite, SWT.SINGLE | SWT.BORDER);
        this.hostText_.setText(this.host_);
        hostGrid = new GridData(GridData.BEGINNING);
        hostGrid.horizontalSpan = 6;
        hostGrid.horizontalAlignment = GridData.FILL;
        this.hostText_.setLayoutData(hostGrid);

        this.hostText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                editor.setMode(hostText_.getText());
                editor.setDirty(true);
            }
        });

        // ============================================================
        spacerLabel = new Label(composite, SWT.NONE);
        spacerGrid = new GridData(GridData.BEGINNING);
        spacerGrid.horizontalSpan = 4;
        spacerLabel.setLayoutData(spacerGrid);
        // ============================================================

        // ------------------------------------------------------------
        this.portLabel_ = new Label(composite, SWT.NONE);
        this.portLabel_.setText("Port:");
        GridData portGrid = new GridData(GridData.BEGINNING);
        portGrid.horizontalSpan = 2;
        this.portLabel_.setLayoutData(portGrid);

        // ------------------------------------------------------------
        this.portText_ = new Text(composite, SWT.SINGLE | SWT.BORDER);
        this.portText_.setText(Integer.toString(this.port_));
        portGrid = new GridData(GridData.BEGINNING);
        portGrid.horizontalSpan = 6;
        portGrid.horizontalAlignment = GridData.FILL;
        this.portText_.setLayoutData(portGrid);

        this.portText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                port_ = Integer.parseInt(portText_.getText());
                editor.setPortNum(port_);
                editor.setDirty(true);
            }
        });

        // ============================================================
        spacerLabel = new Label(composite, SWT.NONE);
        spacerGrid = new GridData(GridData.BEGINNING);
        spacerGrid.horizontalSpan = 4;
        spacerLabel.setLayoutData(spacerGrid);
        // ============================================================

        // ------------------------------------------------------------
        this.domainLabel_ = new Label(composite, SWT.NONE);
        this.domainLabel_.setText("Domain:");
        GridData domainGrid = new GridData(GridData.BEGINNING);
        domainGrid.horizontalSpan = 2;
        this.domainLabel_.setLayoutData(domainGrid);

        // ------------------------------------------------------------
        this.domainText_ = new Text(composite, SWT.SINGLE | SWT.BORDER);
        this.domainText_.setText(domain_);
        domainGrid = new GridData(GridData.BEGINNING);
        domainGrid.horizontalSpan = 10;
        domainGrid.horizontalAlignment = GridData.FILL;
        this.domainText_.setLayoutData(domainGrid);

        this.domainText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                editor.setMode(domainText_.getText());
                editor.setDirty(true);
            }
        });

        // ------------------------------------------------------------
        this.warningLabel_ = new Label(composite, SWT.NONE);
        this.warningLabel_.setText("Warning:");
        GridData warningGrid = new GridData(GridData.BEGINNING);
        warningGrid.horizontalSpan = 2;
        this.warningLabel_.setLayoutData(warningGrid);

        // ------------------------------------------------------------
        this.warningText_ = new Text(composite, SWT.SINGLE | SWT.BORDER);
        this.warningText_.setText(Long.toString(warningThreshold_));
        warningGrid = new GridData(GridData.BEGINNING);
        warningGrid.horizontalSpan = 6;
        warningGrid.horizontalAlignment = GridData.FILL;
        this.warningText_.setLayoutData(warningGrid);

        this.warningText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                warningThreshold_ = Long.parseLong(warningText_.getText());
                editor.setWarningThreshold(warningThreshold_);
                editor.setDirty(true);
            }
        });

        // ============================================================
        spacerLabel = new Label(composite, SWT.NONE);
        spacerGrid = new GridData(GridData.BEGINNING);
        spacerGrid.horizontalSpan = 4;
        spacerLabel.setLayoutData(spacerGrid);
        // ============================================================

        // ------------------------------------------------------------
        this.alarmLabel_ = new Label(composite, SWT.NONE);
        this.alarmLabel_.setText("Alarm:");
        GridData alarmGrid = new GridData(GridData.BEGINNING);
        alarmGrid.horizontalSpan = 2;
        this.alarmLabel_.setLayoutData(alarmGrid);

        // ------------------------------------------------------------
        this.alarmText_ = new Text(composite, SWT.SINGLE | SWT.BORDER);
        this.alarmText_.setText(Long.toString(this.alarmThreshold_));
        alarmGrid = new GridData(GridData.BEGINNING);
        alarmGrid.horizontalSpan = 6;
        alarmGrid.horizontalAlignment = GridData.FILL;
        this.alarmText_.setLayoutData(alarmGrid);

        this.alarmText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                alarmThreshold_ = Long.parseLong(alarmText_.getText());
                editor.setAlarmThreshold(alarmThreshold_);
                editor.setDirty(true);
            }
        });

        // ============================================================
        spacerLabel = new Label(composite, SWT.NONE);
        spacerGrid = new GridData(GridData.BEGINNING);
        spacerGrid.horizontalSpan = 4;
        spacerLabel.setLayoutData(spacerGrid);
        // ============================================================

        // ------------------------------------------------------------
        this.modeLabel_ = new Label(composite, SWT.NONE);
        this.modeLabel_.setText("Mode:");
        GridData modeGrid = new GridData(GridData.BEGINNING);
        modeGrid.horizontalSpan = 2;
        this.modeLabel_.setLayoutData(modeGrid);

        // ------------------------------------------------------------
        this.modeText_ = new Text(composite, SWT.SINGLE | SWT.BORDER);
        this.modeText_.setText(this.mode_);
        modeGrid = new GridData(GridData.BEGINNING);
        modeGrid.horizontalSpan = 4;
        modeGrid.horizontalAlignment = GridData.FILL;
        this.modeText_.setLayoutData(modeGrid);

        this.modeText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                editor.setMode(modeText_.getText());
                editor.setDirty(true);
            }
        });

        // ============================================================
        spacerLabel = new Label(composite, SWT.NONE);
        spacerGrid = new GridData(GridData.BEGINNING);
        spacerGrid.horizontalSpan = 6;
        spacerLabel.setLayoutData(spacerGrid);
        // ============================================================

        // ------------------------------------------------------------
        this.reloadButton_ = new Button(composite, SWT.NONE);
        GridData reloadGrid = new GridData(GridData.BEGINNING);
        reloadGrid.horizontalSpan = 2;
        reloadGrid.horizontalAlignment = GridData.FILL;
        this.reloadButton_.setEnabled(false);
        this.reloadButton_.setLayoutData(reloadGrid);
        this.reloadButton_.setText("Reload");

        this.reloadButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                editor.setHostName(hostText_.getText());
                editor.setPortNum(Integer.parseInt(portText_.getText()));
                editor.setDomain(domainText_.getText());
                editor.setWarningThreshold(warningThreshold_);
                editor.setAlarmThreshold(alarmThreshold_);

                editor.setBlnReload(true);
                editor.start();
            }
        });

        // ------------------------------------------------------------
        this.resetButton_ = new Button(composite, SWT.NONE);
        GridData resetGrid = new GridData(GridData.BEGINNING);
        resetGrid.horizontalSpan = 2;
        resetGrid.horizontalAlignment = GridData.FILL;
        this.resetButton_.setEnabled(false);
        this.resetButton_.setLayoutData(resetGrid);
        this.resetButton_.setText("Reset");

        this.resetButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                try
                {
                    // リセットを行う。
                    editor.reset();
                }
                catch (Exception ex)
                {
                    ;
                }
            }
        });

        // ------------------------------------------------------------
        this.startButton_ = new Button(composite, SWT.NONE);
        GridData startGrid = new GridData(GridData.BEGINNING);
        startGrid.horizontalSpan = 2;
        startGrid.horizontalAlignment = GridData.FILL;
        this.startButton_.setLayoutData(startGrid);
        this.startButton_.setText("Start");

        this.startButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                startButton_.setEnabled(false);
                stopButton_.setEnabled(true);
                resetButton_.setEnabled(true);
                reloadButton_.setEnabled(true);
                hostText_.setEnabled(false);
                portText_.setEnabled(false);
                domainText_.setEnabled(false);
                warningText_.setEnabled(false);
                alarmText_.setEnabled(false);
                modeText_.setEnabled(false);
                editor.start();
            }
        });

        // ------------------------------------------------------------
        this.stopButton_ = new Button(composite, SWT.NONE);
        GridData stopGrid = new GridData(GridData.BEGINNING);
        stopGrid.horizontalSpan = 2;
        stopGrid.horizontalAlignment = GridData.FILL;
        this.stopButton_.setEnabled(false);
        this.stopButton_.setLayoutData(stopGrid);
        this.stopButton_.setText("Stop");

        this.stopButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                // TODO: TANIMOTO ブリンク停止
                startButton_.setEnabled(true);
                stopButton_.setEnabled(false);
                resetButton_.setEnabled(false);
                reloadButton_.setEnabled(false);
                hostText_.setEnabled(true);
                portText_.setEnabled(true);
                domainText_.setEnabled(true);
                warningText_.setEnabled(true);
                alarmText_.setEnabled(true);
                modeText_.setEnabled(true);
                editor.stop();
            }
        });

        if (MODE_JMX.equalsIgnoreCase(this.mode_))
        {
            this.stopButton_.setEnabled(false);
        }

        // ============================================================
        spacerLabel = new Label(composite, SWT.NONE);
        spacerGrid = new GridData(GridData.BEGINNING);
        spacerGrid.horizontalSpan = 4;
        spacerLabel.setLayoutData(spacerGrid);
        // ============================================================
        
        // ------------------------------------------------------------
        this.printButton_ = new Button(composite, SWT.NONE);
        GridData printGrid = new GridData(GridData.BEGINNING);
        printGrid.horizontalSpan = 2;
        printGrid.horizontalAlignment = GridData.FILL;
        this.printButton_.setEnabled(true);
        this.printButton_.setLayoutData(printGrid);
        this.printButton_.setText("Print");

        this.printButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                PrintDialog dialog = new PrintDialog(getSite().getShell(), SWT.NULL);
                PrinterData data = dialog.open();
                if (data != null) {
                    Printer printer = new Printer(data);
                    PrintGraphicalViewerOperation op 
                        = new PrintGraphicalViewerOperation(
                                  printer
                                  , editor.getGraphicalViewer());
                    op.setPrintMode(PrintFigureOperation.FIT_PAGE);
                    op.run("StatVision - "+ getTitle());
                }
            }
        });
        
        // ------------------------------------------------------------
        this.copyButton_ = new Button(composite, SWT.NONE);
        GridData copyGrid = new GridData(GridData.BEGINNING);
        copyGrid.horizontalSpan = 2;
        copyGrid.horizontalAlignment = GridData.FILL;
        this.copyButton_.setEnabled(true);
        this.copyButton_.setLayoutData(copyGrid);
        this.copyButton_.setText("Copy");

        this.copyButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                ScalableRootEditPart part 
                    = (ScalableRootEditPart)
                          (editor.getGraphicalViewer().getRootEditPart());
                
                FileDialog dialog = new FileDialog(editor.getEditorSite().getShell(), SWT.SAVE);
                dialog.setFilterExtensions(new String[]{"bmp"});
                dialog.setFileName(getTitle());
                if (dialog.open() == null)
                {
                    return;
                }
                
                IFigure layer = part.getLayer(LayerConstants.PRINTABLE_LAYERS);

                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int w = layer.getSize().width;
                    int h = layer.getSize().height;
                    Image image = new Image(Display.getDefault(), w, h);
                    GC gc = new GC(image);
                    SWTGraphics graphics = new SWTGraphics(gc);
                    layer.paint(graphics);
                    graphics.dispose();
                    gc.dispose();

                    ImageLoader imageLoader = new ImageLoader();
                    imageLoader.data = new ImageData[] { image.getImageData() };
                    imageLoader.save(out, SWT.IMAGE_BMP);
                    byte[] buffer = out.toByteArray();
                    out.close();

                    String filename = 
                        dialog.getFilterPath() 
                        +  "/" + dialog.getFileName();
                    
                    if (!filename.endsWith(".bmp"))
                    {
                        filename = filename + ".bmp";
                    }

                    FileOutputStream fos = new FileOutputStream(filename);
                    fos.write(buffer);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        int index = addPage(composite);
        setPageText(index, "Settings");
    }

    /**
     * Creates the pages of the multi-page editor.
     */
    protected void createPages()
    {
        createPage0();
        createPage1();
    }

    /**
     * The <code>MultiPageEditorPart</code> implementation of this
     * <code>IWorkbenchPart</code> method disposes all nested editors.
     * Subclasses may extend.
     */
    public void dispose()
    {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        super.dispose();
    }

    /**
     * Saves the multi-page editor's document.
     */
    public void doSave(IProgressMonitor monitor)
    {
        getEditor(0).doSave(monitor);
    }

    /**
     * Saves the multi-page editor's document as another file. Also updates the
     * text for page 0's tab, and updates this multi-page editor's input to
     * correspond to the nested editor's.
     */
    public void doSaveAs()
    {
        IEditorPart editor = getEditor(0);
        editor.doSaveAs();
        setPageText(0, editor.getTitle());
        setInput(editor.getEditorInput());
    }

    /**
     * (non-Javadoc) Method declared on IEditorPart
     */
    public void gotoMarker(IMarker marker)
    {
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

            setPartName(input.getName());

            this.host_ = bufferedReader.readLine();
            this.port_ = setInteger(bufferedReader.readLine(), 0);
            this.domain_ = bufferedReader.readLine();
            this.warningThreshold_ = setInteger(bufferedReader.readLine(), 0);
            this.alarmThreshold_ = setInteger(bufferedReader.readLine(), 0);
            this.mode_ = bufferedReader.readLine();
        }
        catch (CoreException e)
        {
            this.host_ = "";
            this.domain_ = "";
            e.printStackTrace();
        }
        catch (IOException e)
        {
            this.host_ = "";
            this.domain_ = "";
            e.printStackTrace();
        }
        catch (NumberFormatException e)
        {
            this.host_ = "";
            this.domain_ = "";
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc) Method declared on IEditorPart.
     */
    public boolean isSaveAsAllowed()
    {
        return true;
    }

    /**
     * Calculates the contents of page 2 when the it is activated.
     */
    protected void pageChange(int newPageIndex)
    {
        super.pageChange(newPageIndex);
        if (newPageIndex == 2)
        {
            // sortWords();
        }
    }

    /**
     * Closes all project files on project close.
     */
    public void resourceChanged(final IResourceChangeEvent event)
    {
        if (event.getType() == IResourceChangeEvent.PRE_CLOSE)
        {
            Display.getDefault().asyncExec(new Runnable() {
                public void run()
                {
                    IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
                    for (int i = 0; i < pages.length; i++)
                    {
                        if (((FileEditorInput)editor.getEditorInput()).getFile().getProject().equals(
                                                                                                     event.getResource()))
                        {
                            IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
                            pages[i].closeEditor(editorPart, true);
                        }
                    }
                }
            });
        }
    }

    /**
     * 文字列を入力し、文字列が0以上の整数の場合、その値を整数にして返す。
     * それ以外の場合はデフォルト値を返す。
     * @param input 入力
     * @param defaultValue デフォルト値
     * @return データとして入力される値
     */
    private int setInteger(String input, int defaultValue)
    {
        int value;
        try
        {
            value = Integer.parseInt(input);
            if (value < 0)
            {
                return defaultValue;
            }
            return value;
        }
        catch (NumberFormatException ex)
        {
            System.err.println("[StatsVision]不正な値が入力されました。デフォルト値:" + defaultValue + "を使用します。");
            return defaultValue;
        }
    }
}
