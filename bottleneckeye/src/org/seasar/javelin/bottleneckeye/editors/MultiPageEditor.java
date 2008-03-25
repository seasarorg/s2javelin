package org.seasar.javelin.bottleneckeye.editors;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.seasar.javelin.bottleneckeye.StatsVisionPlugin;
import org.seasar.javelin.bottleneckeye.communicate.TelegramClientManager;
import org.seasar.javelin.bottleneckeye.communicate.TelegramSender;
import org.seasar.javelin.bottleneckeye.editors.settings.SettingsEditorTab;
import org.seasar.javelin.bottleneckeye.editors.view.StatsVisionEditor;
import org.seasar.javelin.bottleneckeye.editors.view.StatsVisionEditorTab;
import org.seasar.javelin.bottleneckeye.editors.view.TcpStatsVisionEditor;
import org.seasar.javelin.bottleneckeye.model.persistence.PersistenceModel;
import org.seasar.javelin.bottleneckeye.model.persistence.Settings;
import org.seasar.javelin.bottleneckeye.util.ModelSerializer;

/**
 * An example showing how to create a multi-page editor. This example has 3
 * pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class MultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener, ISelectionListener
{
    /** �^�u�̐ݒ肪������Ă���t�@�C�� */
    private static final String             TAB_SETTINGS_FILE             =
                                                                                  "/org/seasar/javelin/bottleneckeye/editors/tabs.txt";

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

    /** �d�����M�I�u�W�F�N�g�B */
    private TelegramSender                  telegramSender_;

    /** �z�X�g�̃f�t�H���g�l */
    private static final String             DEFAULT_HOST                  = "localhost";

    /** �|�[�g�ԍ��̃f�t�H���g�l */
    private static final int                DEFAULT_PORT                  = 0;

    /** �h���C���̃f�t�H���g�l */
    private static final String             DEFAULT_DOMAIN                = "default";

    /** Warning臒l�̃f�t�H���g�l */
    private static final int                DEFAULT_WARNING               = 0;

    /** Alarm臒l�̃f�t�H���g�l */
    private static final int                DEFAULT_ALARM                 = 0;

    /** ���[�h�̃f�t�H���g�l */
    private static final String             DEFAULT_MODE                  = "TCP";

    /** �X�^�C���̃f�t�H���g�l */
    private static final String             DEFAULT_STYLE                 = "NORMAL";

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
     * �^�C�g���摜���Z�b�g����B
     *
     * @param key �A�C�R���̃L�[
     */
    public void setTitleImage(String key)
    {
        super.setTitleImage(StatsVisionPlugin.getDefault().getImageRegistry().get(key));
    }

    /**
     * Creates the pages of the multi-page editor.
     * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
     */
    protected void createPages()
    {
        setTitleImage(StatsVisionPlugin.IMG_DISCONNECT_TITLE);

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

        TelegramSender sender = new TelegramSender();
        sender.setTcpStatsVisionEditor((TcpStatsVisionEditor)this.editor_);
        this.telegramSender_ = sender;

        // �ݒ��ʃ^�u���쐬����B
        SettingsEditorTab settingsEditorTab = new SettingsEditorTab(this, this.editor_);
        Composite tabComposite = settingsEditorTab.createComposite(getContainer(), this);

        loadFileContent();

        // �t�@�C�������Ƀ^�u�𐶐�����
        initTabs();

        // �ݒ��ʃ^�u����ʂɒǉ�����
        index = addPage(tabComposite);
        setPageText(index, settingsEditorTab.getName());

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

        // MultiPageEditor��SelectionListener�Ƃ��ēo�^����
        IWorkbenchPartSite site = getSite();
        IWorkbenchWindow window = site.getWorkbenchWindow();
        ISelectionService service = window.getSelectionService();
        service.addSelectionListener(this);

        // �ڑ����J�n����B
        settingsEditorTab.start();
    }

    private void loadFileContent()
    {
        IFileEditorInput input = (IFileEditorInput)getEditorInput();
        setPartName(input.getName());

        PersistenceModel persistence = null;
        InputStream in = null;
        try
        {
            in = input.getFile().getContents();
            persistence = ModelSerializer.deserialize(in);
        }
        catch (IOException ex)
        {
            // TODO: ��O����
            ex.printStackTrace();
        }
        catch (CoreException ex)
        {
            // TODO: ��O����
            ex.printStackTrace();
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException ex)
                {
                    // ignore
                    ex.printStackTrace();
                }
            }
        }

        if (persistence == null)
        {
            return;
        }

        loadSetting(persistence.getSettings());
        notifyLoad(persistence);
    }

    /**
     * ���������͂��A������0�ȏ�̐����̏ꍇ�A���̒l�𐮐��ɂ��ĕԂ��B ����ȊO�̏ꍇ�̓f�t�H���g�l��Ԃ��B
     * 
     * @param key �L�[
     * @param input ����
     * @param defaultValue �f�t�H���g�l
     * @return �f�[�^�Ƃ��ē��͂����l
     */
    private int setInteger(String key, String input, int defaultValue)
    {
        int value;
        if (input == null)
        {
            System.err.println("[BottleneckEye]" + key + "���ݒ肳��Ă��܂���B�f�t�H���g�l(" + defaultValue
                    + ")���g�p���܂��B");
            return defaultValue;
        }
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
            System.err.println("[BottleneckEye]" + key + "�ɕs���Ȓl�����͂���܂����B�f�t�H���g�l(" + defaultValue
                    + ")���g�p���܂��B");
            return defaultValue;
        }
    }

    /**
     * ���������͂��A�����񂪋�łȂ���΁A���͂��ꂽ�������Ԃ��B
     * �����񂪋�ł���΁A�f�t�H���g�l��Ԃ��B
     * 
     * @param key �L�[
     * @param input ����
     * @param defaultValue �f�t�H���g�l
     * @return �f�[�^�Ƃ��ē��͂����l
     */
    private String setString(String key, String input, String defaultValue)
    {
        if (input == null || "".equals(input))
        {
            System.err.println("[BottleneckEye]" + key + "���ݒ肳��Ă��܂���B�f�t�H���g�l(" + defaultValue
                    + ")���g�p���܂��B");
            return defaultValue;
        }
        return input;
    }

    private void initTabs()
    {
        BufferedReader reader = null;
        try
        {
            InputStream tabStream = MultiPageEditor.class.getResourceAsStream(TAB_SETTINGS_FILE);
            if (tabStream == null)
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
        Class<?> clazz = Class.forName(className);
        Class<? extends EditorTabInterface> tabClass = clazz.asSubclass(EditorTabInterface.class);
        EditorTabInterface editorTab = tabClass.newInstance();

        Composite tabComposite = editorTab.createComposite(getContainer(), this);
        int index = addPage(tabComposite);
        setPageText(index, editorTab.getName());

        editorTab.setTelegramSender(this.telegramSender_);

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
        this.editor_.setDirty(false);
    }

    /**
     * The <code>MultiPageEditorPart</code> implementation of this
     * <code>IWorkbenchPart</code> method disposes all nested editors.
     * Subclasses may extend.
     */
    public void dispose()
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onStop();
        }
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public void doSave(IProgressMonitor monitor)
    {
        PersistenceModel persistence = new PersistenceModel();
        notifySave(persistence);

        byte[] data;
        try
        {
            data = ModelSerializer.serialize(persistence);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return;
        }

        InputStream stream = new ByteArrayInputStream(data);

        IFile file = ((IFileEditorInput)getEditorInput()).getFile();

        try
        {
            file.setContents(stream, true, false, monitor);
        }
        catch (CoreException ex)
        {
            ex.printStackTrace();
        }

        //        this.editor_.doSave(monitor);
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

        // �����l��ݒ肷��B
        this.host_ = DEFAULT_HOST;
        this.port_ = DEFAULT_PORT;
        this.domain_ = DEFAULT_DOMAIN;
        this.warningThreshold_ = DEFAULT_WARNING;
        this.alarmThreshold_ = DEFAULT_ALARM;
        this.mode_ = DEFAULT_MODE;
        this.lineStyle_ = DEFAULT_STYLE;
    }

    /**
     * �ݒ��ǂݍ��ށB
     * @param file
     */
    protected void loadSetting(Settings settings)
    {
        this.host_ = settings.getHostName();

        if (settings.getPortNum() != null)
        {
            this.port_ = settings.getPortNum();
        }

        this.domain_ = settings.getDomain();

        if (settings.getWarningThreshold() != null)
        {
            this.warningThreshold_ = settings.getWarningThreshold();
        }

        if (settings.getAlarmThreshold() != null)
        {
            this.alarmThreshold_ = settings.getAlarmThreshold();
        }

        this.mode_ = settings.getMode();
        this.lineStyle_ = settings.getLineStyle();
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
                    FileEditorInput input =
                            (FileEditorInput)MultiPageEditor.this.editor_.getEditorInput();
                    if (input.getFile().getProject().equals(event.getResource()))
                    {
                        IEditorPart editorPart =
                                pages[i].findEditor(MultiPageEditor.this.editor_.getEditorInput());
                        pages[i].closeEditor(editorPart, true);
                    }
                }
            }
        });
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
     * Reload��ʒm����B
     */
    public void notifyReload()
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onReload();
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

    /**
     * Save��ʒm����B
     * @param persistence �i�������f��
     */
    public void notifySave(PersistenceModel persistence)
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onSave(persistence);
        }
    }

    /**
     * Load��ʒm����B
     * @param persistence �i�������f��
     */
    public void notifyLoad(PersistenceModel persistence)
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onLoad(persistence);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {
        if (this.equals(getSite().getPage().getActiveEditor()))
        {
            if (this.editor_.equals(getActiveEditor()))
            {
                this.editor_.selectionChanged(part, selection);
            }
        }
    }
}
