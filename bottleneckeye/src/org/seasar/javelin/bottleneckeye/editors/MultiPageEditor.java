package org.seasar.javelin.bottleneckeye.editors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.seasar.javelin.bottleneckeye.communicate.TelegramClientManager;
import org.seasar.javelin.bottleneckeye.communicate.TelegramSender;
import org.seasar.javelin.bottleneckeye.editors.settings.SettingsEditorTab;
import org.seasar.javelin.bottleneckeye.editors.view.StatsVisionEditor;
import org.seasar.javelin.bottleneckeye.editors.view.StatsVisionEditorTab;
import org.seasar.javelin.bottleneckeye.editors.view.TcpStatsVisionEditor;

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
    /** �^�u�̐ݒ肪������Ă���t�@�C�� */
    private static final String             TAB_SETTINGS_FILE             = "/org/seasar/javelin/bottleneckeye/editors/tabs.txt";

    /** �^�u�̐ݒ�ŁA�R�����g�s��\���ړ��� */
    private static final String             TAB_SETTING_COMMENT_LINE_MARK = "#";

    /** �ʐM���[�h�iJMX�j */
    public static final String              MODE_JMX                      = "JMX";

    /** �ʐM���[�h�iTCP�j */
    public static final String              MODE_TCP                      = "TCP";

    /** ���̎�ށiNORMAL�j */
    public static final String              LINE_STYLE_NORMAL             = "NORMAL";

    /** ���̎�ށiNORMAL�j */
    public static final String              LINE_STYLE_SHORTEST           = "SHORTEST";

    /** ���̎�ށiFAN�j */
    public static final String              LINE_STYLE_FAN                = "FAN";

    /** ���̎�ށiMANHATTAN�j */
    public static final String              LINE_STYLE_MANHATTAN          = "MANHATTAN";

    /** �^�u�̃}�b�v�i�L�[�̓N���X���j */
    private Map<String, EditorTabInterface> editorTabMap_;

    /** The text editor used in page 0. */
    private StatsVisionEditor               editor_;

    /** �z�X�g�� */
    private String                          host_                         = "";

    /** �|�[�g�ԍ� */
    private int                             port_;

    /** �h���C���� */
    private String                          domain_                       = "";

    /** �x��臒l */
    private long                            warningThreshold_;

    /** �A���[��臒l */
    private long                            alarmThreshold_;

    /** ���[�h */
    private String                          mode_                         = MODE_TCP;

    /** ���C���X�^�C�� */
    private String                          lineStyle_                    = LINE_STYLE_NORMAL;

    /**
     * Creates a multi-page editor example.
     */
    public MultiPageEditor()
    {
        super();
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
        this.editorTabMap_ = new LinkedHashMap<String, EditorTabInterface>();
    }

    /**
     * Creates the pages of the multi-page editor.
     * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
     */
    protected void createPages()
    {
        // StatsVisionEditor�^�u���쐬����B
        StatsVisionEditorTab statsVisionEditorTab = new StatsVisionEditorTab();
        statsVisionEditorTab.createEditor(getContainer(), this);
        this.editor_ = statsVisionEditorTab.getStatsVisionEditor();

        int index = 0;
        try
        {
            index = addPage(this.editor_, getEditorInput());
        }
        catch (PartInitException ex1)
        {
            // ignore
            ex1.printStackTrace();
        }
        setPageText(index, statsVisionEditorTab.getName());
        this.editorTabMap_.put(StatsVisionEditorTab.class.getName(), statsVisionEditorTab);

        TelegramSender sender = TelegramSender.getInstance();
        sender.setTcpStatsVisionEditor((TcpStatsVisionEditor)this.editor_);

        // �ݒ��ʃ^�u���쐬����B
        SettingsEditorTab settingsEditorTab = new SettingsEditorTab(this, this.editor_);
        Composite tabComposite = settingsEditorTab.createComposite(getContainer(), this);
        index = addPage(tabComposite);
        setPageText(index, settingsEditorTab.getName());

        // �t�@�C�������Ƀ^�u�𐶐�����
        initTabs();

        // �d���]�����o�^����
        TelegramClientManager clientManager = this.editor_.getTelegramClientManager();
        if (clientManager != null)
        {
            for (EditorTabInterface editorTabElem : this.editorTabMap_.values())
            {
                clientManager.addEditorTab(editorTabElem);
            }
        }

        // �����ݒ���r���[�ɒʒm����
        initSettingEditorTab(settingsEditorTab);
    }

    private void initTabs()
    {
        BufferedReader reader = null;
        try
        {
            InputStream tabStream = MultiPageEditor.class.getResourceAsStream(TAB_SETTINGS_FILE);
            if(tabStream == null)
            {
                return;
            }

            reader = new BufferedReader(new InputStreamReader(tabStream));
            String line;
            while ((line = reader.readLine()) != null)
            {
                try
                {
                    // �s����łȂ��A���� "#" �ȊO�Ŏn�܂鎞�́A�^�u�𐶐����Ȃ�
                    if (line.length() > 0
                            && line.startsWith(TAB_SETTING_COMMENT_LINE_MARK) == false)
                    {
                        createPageFromClassName(line);
                    }
                }
                catch (ClassCastException ex)
                {
                    ex.printStackTrace();
                }
                catch (ClassNotFoundException ex)
                {
                    ex.printStackTrace();
                }
                catch (InstantiationException ex)
                {
                    ex.printStackTrace();
                }
                catch (IllegalAccessException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * �N���X�������Ƀ^�u�𐶐�����B
     * 
     * @param className
     *            �N���X��
     * @throws ClassNotFoundException
     *             �N���X��������Ȃ��ꍇ
     * @throws InstantiationException
     *             �C���X�^���X�������ł��Ȃ��ꍇ
     * @throws IllegalAccessException
     *             �C���X�^���X�������ł��Ȃ��ꍇ
     */
    private void createPageFromClassName(String className)
        throws ClassNotFoundException,
            InstantiationException,
            IllegalAccessException
    {
        Class<EditorTabInterface> tabClass = (Class<EditorTabInterface>)Class.forName(className);
        EditorTabInterface editorTab = tabClass.newInstance();

        Composite tabComposite = editorTab.createComposite(getContainer(), this);
        int index = addPage(tabComposite);
        setPageText(index, editorTab.getName());

        this.editorTabMap_.put(className, editorTab);
    }

    /**
     * Editor�^�u���擾����B
     * @param className Editor�^�u�̃N���X��
     * @return Editor�^�u
     */
    public EditorTabInterface getEditorTab(String className)
    {
        return this.editorTabMap_.get(className);
    }

    /**
     * �����l��ݒ�p�^�u�ɐݒ肷��B
     * @param settingsEditorTab �ݒ�p�^�u
     */
    private void initSettingEditorTab(SettingsEditorTab settingsEditorTab)
    {
        settingsEditorTab.setAlarmThreshold(Long.valueOf(this.alarmThreshold_));
        settingsEditorTab.setDomain(this.domain_);
        settingsEditorTab.setHostName(this.host_);
        settingsEditorTab.setLineStyle(this.lineStyle_);
        settingsEditorTab.setMode(this.mode_);
        settingsEditorTab.setPortNum(Integer.valueOf(this.port_));
        settingsEditorTab.setWarningThreshold(Long.valueOf(this.warningThreshold_));
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
     * {@inheritDoc}
     */
    public void doSave(IProgressMonitor monitor)
    {
        this.editor_.doSave(monitor);
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
     * �}�[�J�[�փW�����v����B
     * @param marker �}�[�J�[
     */
    public void gotoMarker(IMarker marker)
    {
        setActivePage(0);
        IDE.gotoMarker(getEditor(0), marker);
    }

    /**
     * {@inheritDoc}
     */
    public void init(IEditorSite site, IEditorInput editorInput)
        throws PartInitException
    {
        if (!(editorInput instanceof IFileEditorInput))
        {
            throw new PartInitException("Invalid Input: Must be IFileEditorInput");
        }
        super.init(site, editorInput);

        IFileEditorInput input = (IFileEditorInput)editorInput;

        BufferedReader bufferedReader = null;
        try
        {
            InputStream stream = input.getFile().getContents();
            InputStreamReader reader = new InputStreamReader(stream);
            bufferedReader = new BufferedReader(reader);

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
        finally
        {
            if (bufferedReader != null)
            {
                try
                {
                    bufferedReader.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSaveAsAllowed()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void resourceChanged(final IResourceChangeEvent event)
    {
        if (event.getType() != IResourceChangeEvent.PRE_CLOSE)
        {
            return;
        }

        Display.getDefault().asyncExec(new Runnable() {
            public void run()
            {
                IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
                for (int i = 0; i < pages.length; i++)
                {
                    FileEditorInput input = (FileEditorInput)MultiPageEditor.this.editor_.getEditorInput();
                    if (input.getFile().getProject().equals(event.getResource()))
                    {
                        IEditorPart editorPart = pages[i].findEditor(MultiPageEditor.this.editor_.getEditorInput());
                        pages[i].closeEditor(editorPart, true);
                    }
                }
            }
        });
    }

    /**
     * ���������͂��A������0�ȏ�̐����̏ꍇ�A���̒l�𐮐��ɂ��ĕԂ��B ����ȊO�̏ꍇ�̓f�t�H���g�l��Ԃ��B
     * 
     * @param input
     *            ����
     * @param defaultValue
     *            �f�t�H���g�l
     * @return �f�[�^�Ƃ��ē��͂����l
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
            System.err.println("[StatsVision]�s���Ȓl�����͂���܂����B�f�t�H���g�l:" + defaultValue + "���g�p���܂��B");
            return defaultValue;
        }
    }

    /**
     * Start��ʒm����B
     */
    public void notifyStart()
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onStart();
        }
    }

    /**
     * Stop��ʒm����B
     */
    public void notifyStop()
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onStop();
        }
    }

    /**
     * Reset��ʒm����B
     */
    public void notifyReset()
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onReset();
        }
    }

    /**
     * Copy��ʒm����B
     */
    public void notifyCopy()
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onCopy();
        }
    }

    /**
     * Print��ʒm����B
     */
    public void notifyPrint()
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onPrint();
        }
    }
}
