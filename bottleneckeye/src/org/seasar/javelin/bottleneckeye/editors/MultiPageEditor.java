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
    /** タブの設定が書かれているファイル */
    private static final String             TAB_SETTINGS_FILE             =
                                                                                  "/org/seasar/javelin/bottleneckeye/editors/tabs.txt";

    /** タブの設定で、コメント行を表す接頭辞 */
    private static final String             TAB_SETTING_COMMENT_LINE_MARK = "#";

    /** 通信モード（JMX） */
    public static final String              MODE_JMX                      = "JMX";

    /** 通信モード（TCP） */
    public static final String              MODE_TCP                      = "TCP";

    /** 線の種類（NORMAL） */
    public static final String              LINE_STYLE_NORMAL             = "NORMAL";

    /** 線の種類（NORMAL） */
    public static final String              LINE_STYLE_SHORTEST           = "SHORTEST";

    /** 線の種類（FAN） */
    public static final String              LINE_STYLE_FAN                = "FAN";

    /** 線の種類（MANHATTAN） */
    public static final String              LINE_STYLE_MANHATTAN          = "MANHATTAN";

    /** タブのマップ（キーはクラス名） */
    private Map<String, EditorTabInterface> editorTabMap_;

    /** The text editor used in page 0. */
    private StatsVisionEditor               editor_;

    /** ホスト名 */
    private String                          host_                         = "";

    /** ポート番号 */
    private int                             port_;

    /** ドメイン名 */
    private String                          domain_                       = "";

    /** 警告閾値 */
    private long                            warningThreshold_;

    /** アラーム閾値 */
    private long                            alarmThreshold_;

    /** モード */
    private String                          mode_                         = MODE_TCP;

    /** ラインスタイル */
    private String                          lineStyle_                    = LINE_STYLE_NORMAL;

    /** 電文送信オブジェクト。 */
    private TelegramSender                  telegramSender_;

    /** ホストのデフォルト値 */
    private static final String             DEFAULT_HOST                  = "localhost";

    /** ポート番号のデフォルト値 */
    private static final int                DEFAULT_PORT                  = 0;

    /** ドメインのデフォルト値 */
    private static final String             DEFAULT_DOMAIN                = "default";

    /** Warning閾値のデフォルト値 */
    private static final int                DEFAULT_WARNING               = 0;

    /** Alarm閾値のデフォルト値 */
    private static final int                DEFAULT_ALARM                 = 0;

    /** モードのデフォルト値 */
    private static final String             DEFAULT_MODE                  = "TCP";

    /** スタイルのデフォルト値 */
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
     * タイトル画像をセットする。
     *
     * @param key アイコンのキー
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

        // StatsVisionEditorタブを作成する。
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

        // 設定画面タブを作成する。
        SettingsEditorTab settingsEditorTab = new SettingsEditorTab(this, this.editor_);
        Composite tabComposite = settingsEditorTab.createComposite(getContainer(), this);

        loadFileContent();

        // ファイルを元にタブを生成する
        initTabs();

        // 設定画面タブを画面に追加する
        index = addPage(tabComposite);
        setPageText(index, settingsEditorTab.getName());

        // 電文転送先を登録する
        TelegramClientManager clientManager = this.editor_.getTelegramClientManager();
        if (clientManager != null)
        {
            for (EditorTabInterface editorTabElem : this.editorTabMap_.values())
            {
                clientManager.addEditorTab(editorTabElem);
            }
        }


        // 初期設定をビューに通知する
        initSettingEditorTab(settingsEditorTab);

        // MultiPageEditorをSelectionListenerとして登録する
        IWorkbenchPartSite site = getSite();
        IWorkbenchWindow window = site.getWorkbenchWindow();
        ISelectionService service = window.getSelectionService();
        service.addSelectionListener(this);

        // 接続を開始する。
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
            // TODO: 例外処理
            ex.printStackTrace();
        }
        catch (CoreException ex)
        {
            // TODO: 例外処理
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
     * 文字列を入力し、文字列が0以上の整数の場合、その値を整数にして返す。 それ以外の場合はデフォルト値を返す。
     * 
     * @param key キー
     * @param input 入力
     * @param defaultValue デフォルト値
     * @return データとして入力される値
     */
    private int setInteger(String key, String input, int defaultValue)
    {
        int value;
        if (input == null)
        {
            System.err.println("[BottleneckEye]" + key + "が設定されていません。デフォルト値(" + defaultValue
                    + ")を使用します。");
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
            System.err.println("[BottleneckEye]" + key + "に不正な値が入力されました。デフォルト値(" + defaultValue
                    + ")を使用します。");
            return defaultValue;
        }
    }

    /**
     * 文字列を入力し、文字列が空でなければ、入力された文字列を返す。
     * 文字列が空であれば、デフォルト値を返す。
     * 
     * @param key キー
     * @param input 入力
     * @param defaultValue デフォルト値
     * @return データとして入力される値
     */
    private String setString(String key, String input, String defaultValue)
    {
        if (input == null || "".equals(input))
        {
            System.err.println("[BottleneckEye]" + key + "が設定されていません。デフォルト値(" + defaultValue
                    + ")を使用します。");
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
                    // 行が空でなく、かつ "#" 以外で始まる時は、タブを生成しない
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
     * クラス名を元にタブを生成する。
     * 
     * @param className
     *            クラス名
     * @throws ClassNotFoundException
     *             クラスが見つからない場合
     * @throws InstantiationException
     *             インスタンスが生成できない場合
     * @throws IllegalAccessException
     *             インスタンスが生成できない場合
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
     * Editorタブを取得する。
     * @param className Editorタブのクラス名
     * @return Editorタブ
     */
    public EditorTabInterface getEditorTab(String className)
    {
        return this.editorTabMap_.get(className);
    }

    /**
     * 初期値を設定用タブに設定する。
     * @param settingsEditorTab 設定用タブ
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
     * マーカーへジャンプする。
     * @param marker マーカー
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

        // 初期値を設定する。
        this.host_ = DEFAULT_HOST;
        this.port_ = DEFAULT_PORT;
        this.domain_ = DEFAULT_DOMAIN;
        this.warningThreshold_ = DEFAULT_WARNING;
        this.alarmThreshold_ = DEFAULT_ALARM;
        this.mode_ = DEFAULT_MODE;
        this.lineStyle_ = DEFAULT_STYLE;
    }

    /**
     * 設定を読み込む。
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
     * Startを通知する。
     */
    public void notifyStart()
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onStart();
        }
    }

    /**
     * Stopを通知する。
     */
    public void notifyStop()
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onStop();
        }
    }

    /**
     * Resetを通知する。
     */
    public void notifyReset()
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onReset();
        }
    }

    /**
     * Reloadを通知する。
     */
    public void notifyReload()
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onReload();
        }
    }

    /**
     * Copyを通知する。
     */
    public void notifyCopy()
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onCopy();
        }
    }

    /**
     * Printを通知する。
     */
    public void notifyPrint()
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onPrint();
        }
    }

    /**
     * Saveを通知する。
     * @param persistence 永続化モデル
     */
    public void notifySave(PersistenceModel persistence)
    {
        for (EditorTabInterface editorTab : this.editorTabMap_.values())
        {
            editorTab.onSave(persistence);
        }
    }

    /**
     * Loadを通知する。
     * @param persistence 永続化モデル
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
