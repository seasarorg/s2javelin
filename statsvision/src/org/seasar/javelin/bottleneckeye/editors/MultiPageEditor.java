package org.seasar.javelin.bottleneckeye.editors;

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
import org.eclipse.swt.widgets.Combo;
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

    public static final String LINE_STYLE_NORMAL = "NORMAL";
    
    public static final String LINE_STYLE_SHORTEST = "SHORTEST";
    
    public static final String LINE_STYLE_FAN = "FAN";
    
    public static final String LINE_STYLE_MANHATTAN = "MANHATTAN";
    
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
            this.editor.setLineStyle(this.lineStyle_);

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

    private Label  lineStyleLabel_;
    
    private Text   hostText_;

    private Text   portText_;

    private Text   domainText_;

    private Text   warningText_;

    private Text   alarmText_;

    private Combo  modeCombo_;
    
    private Combo  lineStyleCombo_;

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

    /** ラインスタイル */
    String         lineStyle_   = LINE_STYLE_NORMAL;
    
    /**
     * Creates page 1 of the multi-page editor, which allows you to change the
     * font used in page 2.
     */
    void createPage1()
    {
        Composite  composite = new Composite(getContainer(), SWT.NONE);
        GridLayout layout    = new GridLayout(12, true);
        composite.setLayout(layout);

        this.hostLabel_ = createLabel(composite, "Host:");
        this.hostText_ = createText(composite, 6, this.host_);
        createSpacer(composite, 4);
        
        this.portLabel_ = createLabel(composite, "Port:");
        this.portText_ = createText(composite, 6, Integer.toString(this.port_));
        createSpacer(composite, 4);
        
        this.domainLabel_ = createLabel(composite, "Domain:");
        this.domainText_ = createText(composite, 10, this.domain_);
        
        this.warningLabel_ = createLabel(composite, "Warning:");
        this.warningText_ = createText(composite, 6, Long.toString(this.warningThreshold_));
        createSpacer(composite, 4);
        
        this.alarmLabel_ = createLabel(composite, "Alarm:");
        this.alarmText_ = createText(composite, 6, Long.toString(this.alarmThreshold_));
        createSpacer(composite, 4);
        
        String[] values;
        values = new String[]{MODE_TCP, MODE_JMX};
        
        this.modeLabel_ = createLabel(composite, "Mode:");
        this.modeCombo_ = createCombo(composite, 4, values, this.mode_);
        createSpacer(composite, 6);

        values = new String[]{LINE_STYLE_NORMAL, LINE_STYLE_SHORTEST, LINE_STYLE_FAN, LINE_STYLE_MANHATTAN};
        
        this.lineStyleLabel_ = createLabel(composite, "Style:");
        this.lineStyleCombo_ = createCombo(composite, 4, values, this.lineStyle_);
        createSpacer(composite, 6);

        this.reloadButton_ = createButton(composite, "Reload", false);
        this.resetButton_ = createButton(composite, "Reset", false);
        this.startButton_ = createButton(composite, "Start", true);
        this.stopButton_ = createButton(composite, "Stop", false);
        createSpacer(composite, 4);
        
        this.printButton_ = createButton(composite, "Print", true);
        this.copyButton_ = createButton(composite, "Copy", true);
        // ------------------------------------------------------------
        
        this.hostText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                editor.setMode(hostText_.getText());
                editor.setDirty(true);
            }
        });


        this.portText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                port_ = Integer.parseInt(portText_.getText());
                editor.setPortNum(port_);
                editor.setDirty(true);
            }
        });

        this.domainText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                editor.setDomain(domainText_.getText());
                editor.setDirty(true);
            }
        });

        this.warningText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                warningThreshold_ = Long.parseLong(warningText_.getText());
                editor.setWarningThreshold(warningThreshold_);
                editor.setDirty(true);
            }
        });

        this.alarmText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                alarmThreshold_ = Long.parseLong(alarmText_.getText());
                editor.setAlarmThreshold(alarmThreshold_);
                editor.setDirty(true);
            }
        });

        this.modeCombo_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                editor.setMode(modeCombo_.getText());
                editor.setDirty(true);
            }
        });

        this.lineStyleCombo_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                editor.setLineStyle(lineStyleCombo_.getText());
                editor.setDirty(true);
            }
        });

        // ------------------------------------------------------------
        this.reloadButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                applyInputToEditor();

                editor.setBlnReload(true);
                editor.start();
            }
        });

        // ------------------------------------------------------------
        this.resetButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                try
                {
                    applyInputToEditor();

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
        this.startButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                applyInputToEditor();

            	startButton_.setEnabled(false);
                stopButton_.setEnabled(true);
                resetButton_.setEnabled(true);
                reloadButton_.setEnabled(true);
                hostText_.setEnabled(false);
                portText_.setEnabled(false);
                domainText_.setEnabled(false);
                warningText_.setEnabled(false);
                alarmText_.setEnabled(false);
                modeCombo_.setEnabled(false);
                lineStyleCombo_.setEnabled(false);
                editor.start();
            }
        });

        // ------------------------------------------------------------
        this.stopButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                startButton_.setEnabled(true);
                stopButton_.setEnabled(false);
                resetButton_.setEnabled(false);
                reloadButton_.setEnabled(false);
                hostText_.setEnabled(true);
                portText_.setEnabled(true);
                domainText_.setEnabled(true);
                warningText_.setEnabled(true);
                alarmText_.setEnabled(true);
                modeCombo_.setEnabled(true);
                lineStyleCombo_.setEnabled(true);
                editor.stop();
            }
        });

        // ------------------------------------------------------------
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
        applyInputToEditor();

        getEditor(0).doSave(monitor);
    }

	private void applyInputToEditor() {
		editor.setHostName(hostText_.getText());
        editor.setPortNum(Integer.parseInt(portText_.getText()));
        editor.setDomain(domainText_.getText());
        editor.setWarningThreshold(warningThreshold_);
        editor.setAlarmThreshold(alarmThreshold_);
        editor.setMode(modeCombo_.getText());
        editor.setLineStyle(lineStyleCombo_.getText());
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
            this.lineStyle_ = bufferedReader.readLine();
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
    
    /** 設定画面用のラベルを生成する。 */
    private Label createLabel(Composite composite, String text)
    {
        GridData grid = new GridData(GridData.BEGINNING);
        grid.horizontalSpan = 2;
        
        Label label = new Label(composite, SWT.NONE);
        label.setText(text);
        label.setLayoutData(grid);

        return label;
    }
    
    /** 設定画面用のテキストフィールドを生成する。 */
    private Text createText(Composite composite, int span, String value)
    {
        GridData grid = new GridData(GridData.BEGINNING);
        grid.horizontalSpan = span;
        grid.horizontalAlignment = GridData.FILL;
        
        Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
        text.setText(value);
        text.setLayoutData(grid);
        
        return text;
    }
    
    /** 設定画面用のボタンを生成する。 */
    private Button createButton(Composite composite, String text, boolean isEnabled)
    {
        GridData grid = new GridData(GridData.BEGINNING);
        grid.horizontalSpan      = 2;
        grid.horizontalAlignment = GridData.FILL;
        
        Button button = new Button(composite, SWT.NONE);
        button.setEnabled(isEnabled);
        button.setLayoutData(grid);
        button.setText(text);
        
        return button;
    }

    private Combo createCombo(Composite composite, int span, String[] values, String value)
    {
        GridData grid = new GridData(GridData.BEGINNING);
        grid.horizontalSpan      = span;
        grid.horizontalAlignment = GridData.FILL;
        
        Combo combo = new Combo(composite, SWT.READ_ONLY);
        combo.setLayoutData(grid);
        
        for (String item : values)
        {
            combo.add(item);
        }
        combo.setText(value);
        
        return combo;
    }

    /** 設定画面用のスペーサを生成する。 */
    private void createSpacer(Composite composite, int space)
    {
        GridData spacerGrid;
        spacerGrid = new GridData(GridData.BEGINNING);
        spacerGrid.horizontalSpan = space;
        
        Label    spacerLabel;
        spacerLabel = new Label(composite, SWT.NONE);
        spacerLabel.setLayoutData(spacerGrid);
    }
}
