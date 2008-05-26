package org.seasar.javelin.bottleneckeye.editors.profiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.CellEditorActionHandler;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.seasar.javelin.bottleneckeye.StatsVisionPlugin;
import org.seasar.javelin.bottleneckeye.communicate.Common;
import org.seasar.javelin.bottleneckeye.communicate.Header;
import org.seasar.javelin.bottleneckeye.communicate.Telegram;
import org.seasar.javelin.bottleneckeye.communicate.TelegramSender;
import org.seasar.javelin.bottleneckeye.editors.EditorTabInterface;
import org.seasar.javelin.bottleneckeye.model.InvocationModel;
import org.seasar.javelin.bottleneckeye.model.persistence.PersistenceModel;
import org.seasar.javelin.bottleneckeye.views.ColumnViewerSorter;

/**
 * プロファイラ。
 * @author cero-t
 */
public class ProfilerTab implements EditorTabInterface
{
    /** クラス名 */
    protected static final String        CLASS_NAME        = "クラス名";

    /** メソッド名 */
    protected static final String        METHOD_NAME       = "メソッド名";

    /** 合計処理時間 */
    protected static final String        TOTAL_TIME        = "合計処理時間";

    /** 平均処理時間 */
    protected static final String        AVERAGE_TIME      = "平均処理時間";

    /** 最大処理時間 */
    protected static final String        MAX_TIME          = "最大処理時間";

    /** 最小処理時間 */
    protected static final String        MIN_TIME          = "最小処理時間";

    /** 合計CPU時間 */
    protected static final String        TOTAL_CPU_TIME    = "合計CPU時間";

    /** 平均CPU時間 */
    protected static final String        AVERAGE_CPU_TIME  = "平均CPU時間";

    /** 最大CPU時間 */
    protected static final String        MAX_CPU_TIME      = "最大CPU時間";

    /** 最小CPU時間 */
    protected static final String        MIN_CPU_TIME      = "最小CPU時間";

    /** 合計USER時間 */
    protected static final String        TOTAL_USER_TIME   = "合計USER時間";

    /** 平均USER時間 */
    protected static final String        AVERAGE_USER_TIME = "平均USER時間";

    /** 最大USER時間 */
    protected static final String        MAX_USER_TIME     = "最大USER時間";

    /** 最小USER時間 */
    protected static final String        MIN_USER_TIME     = "最小USER時間";

    /** 呼び出し回数 */
    protected static final String        CALL_TIME         = "呼び出し回数";

    /** 例外発生回数 */
    protected static final String        THROWABLE_TIME    = "例外発生回数";

    /** テーブルビューワ。 */
    private TableViewer                  viewer_;

    /** テーブルに表示するデータの元となるマップ */
    private Map<String, InvocationModel> modelMap_         = new HashMap<String, InvocationModel>();

    /** テーブルに表示するデータのリスト */
    private List<InvocationModel>        modelList_        = new ArrayList<InvocationModel>();

    /** リロードボタン */
    private Button                       reloadButton_;

    /** 電文送信オブジェクト */
    private TelegramSender               telegramSender_;

    /**
     * {@inheritDoc}
     */
    public Composite createComposite(Composite container, MultiPageEditorPart editorPart)
    {
        Composite composite = new Composite(container, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);

        this.reloadButton_ = new Button(composite, SWT.PUSH | SWT.FLAT);
        this.reloadButton_.setEnabled(false);
        Image image = StatsVisionPlugin.getDefault().getImageRegistry().get(
                                                                            StatsVisionPlugin.IMG_REFRESH);
        this.reloadButton_.setImage(image);
        this.reloadButton_.setLayoutData(new GridData());
        this.reloadButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                onReload();
            }
        });

        this.viewer_ = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        this.viewer_.setContentProvider(new ArrayContentProvider());
        this.viewer_.getTable().setLinesVisible(true);
        this.viewer_.getTable().setHeaderVisible(true);
        this.viewer_.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));

        createColumns(this.viewer_);

        this.viewer_.setInput(this.modelList_);

        CellEditorActionHandler actionHandler = new CellEditorActionHandler(
                                                                            editorPart.getEditorSite().getActionBars());
        ProfilerCopyAction action = new ProfilerCopyAction(this.viewer_);
        actionHandler.setCopyAction(action);

        // F5リロードのためのキーリスナ
        KeyAdapter keyListener = new KeyAdapter() {
            public void keyReleased(KeyEvent e)
            {
                if (e.keyCode == SWT.F5)
                {
                    onReload();
                }
            }
        };
        this.viewer_.getTable().addKeyListener(keyListener);
        this.reloadButton_.addKeyListener(keyListener);
        composite.addKeyListener(keyListener);

        return composite;
    }

    /**
     * カラムを作成する。
     * @param viewer TableViewer
     */
    private void createColumns(TableViewer viewer)
    {
        TableViewerColumn column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(CLASS_NAME);
        column.getColumn().setWidth(200);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                return ((InvocationModel)element).getClassName();
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                String s1 = ((InvocationModel)e1).getClassName();
                String s2 = ((InvocationModel)e2).getClassName();
                return s1.compareToIgnoreCase(s2);
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(METHOD_NAME);
        column.getColumn().setWidth(150);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                return ((InvocationModel)element).getMethodName();
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                String s1 = ((InvocationModel)e1).getMethodName();
                String s2 = ((InvocationModel)e2).getMethodName();
                return s1.compareToIgnoreCase(s2);
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(TOTAL_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                long count = ((InvocationModel)element).getCount();
                long average = ((InvocationModel)element).getAverage();
                long total = count * average;
                return String.valueOf(total);
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getCount() * ((InvocationModel)e1).getAverage();
                long l2 = ((InvocationModel)e2).getCount() * ((InvocationModel)e2).getAverage();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(AVERAGE_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                return String.valueOf(((InvocationModel)element).getAverage());
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getAverage();
                long l2 = ((InvocationModel)e2).getAverage();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(MAX_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                return String.valueOf(((InvocationModel)element).getMaximum());
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getMaximum();
                long l2 = ((InvocationModel)e2).getMaximum();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(MIN_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                return String.valueOf(((InvocationModel)element).getMinimum());
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getMinimum();
                long l2 = ((InvocationModel)e2).getMinimum();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(TOTAL_CPU_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                long count = ((InvocationModel)element).getCount();
                long average = ((InvocationModel)element).getCpuAverage();
                long total = count * average;
                return String.valueOf(total);
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getCount() * ((InvocationModel)e1).getCpuAverage();
                long l2 = ((InvocationModel)e2).getCount() * ((InvocationModel)e2).getCpuAverage();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(AVERAGE_CPU_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                return String.valueOf(((InvocationModel)element).getCpuAverage());
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getCpuAverage();
                long l2 = ((InvocationModel)e2).getCpuAverage();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(MAX_CPU_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                return String.valueOf(((InvocationModel)element).getCpuMaximum());
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getCpuMaximum();
                long l2 = ((InvocationModel)e2).getCpuMaximum();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(MIN_CPU_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                return String.valueOf(((InvocationModel)element).getCpuMinimum());
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getCpuMinimum();
                long l2 = ((InvocationModel)e2).getCpuMinimum();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(TOTAL_USER_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                long count = ((InvocationModel)element).getCount();
                long average = ((InvocationModel)element).getUserAverage();
                long total = count * average;
                return String.valueOf(total);
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getCount() * ((InvocationModel)e1).getUserAverage();
                long l2 = ((InvocationModel)e2).getCount() * ((InvocationModel)e2).getUserAverage();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(AVERAGE_USER_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                return String.valueOf(((InvocationModel)element).getUserAverage());
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getUserAverage();
                long l2 = ((InvocationModel)e2).getUserAverage();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(MAX_USER_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                return String.valueOf(((InvocationModel)element).getUserMaximum());
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getUserMaximum();
                long l2 = ((InvocationModel)e2).getUserMaximum();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(MIN_USER_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                return String.valueOf(((InvocationModel)element).getUserMinimum());
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getUserMinimum();
                long l2 = ((InvocationModel)e2).getUserMinimum();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(CALL_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                return String.valueOf(((InvocationModel)element).getCount());
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getCount();
                long l2 = ((InvocationModel)e2).getCount();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };

        column = new TableViewerColumn(viewer, SWT.LEFT);
        column.getColumn().setText(THROWABLE_TIME);
        column.getColumn().setWidth(100);
        column.getColumn().setMoveable(true);
        column.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element)
            {
                return String.valueOf(((InvocationModel)element).getThrowableCount());
            }
        });
        new ColumnViewerSorter(viewer, column) {
            @Override
            protected int doCompare(Object e1, Object e2)
            {
                long l1 = ((InvocationModel)e1).getThrowableCount();
                long l2 = ((InvocationModel)e2).getThrowableCount();
                return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
            }
        };
    }

    /**
     * ビューの更新を行う。
     */
    public void refresh()
    {
        this.viewer_.refresh();
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return "Profiler";
    }

    /**
     * {@inheritDoc}
     */
    public boolean receiveTelegram(Telegram telegram)
    {
        // 状態取得応答電文を受信した場合
        boolean valid = isTargetTelegram(telegram);

        if (valid == true)
        {
            // InvocationMapに、該当InvocationModelを設定する
            addToModelList(telegram, this.modelList_);
            this.viewer_.getControl().getDisplay().asyncExec(new Runnable() {
                public void run()
                {
                    ProfilerTab.this.viewer_.refresh(true);
                }
            });
            return true;
        }

        return false;
    }

    /**
     * @param telegram 受信した電文
     * @return 処理対象の電文であるか(TRUEなら対象)
     */
    boolean isTargetTelegram(Telegram telegram)
    {
        Header header = telegram.getObjHeader();
        byte telegramKind = header.getByteTelegramKind();
        byte requestKind = header.getByteRequestKind();

        boolean result = true;
        
        if (telegramKind != Common.BYTE_TELEGRAM_KIND_GET)
        {
            return false;
        }
        else if (requestKind != Common.BYTE_REQUEST_KIND_RESPONSE)
        {
            return false;
        }

        return result;
    }
    
    /**
     * @param telegram
     */
    void addToModelList(Telegram telegram, List<InvocationModel> list)
    {
        InvocationModel[] invocations = InvocationModel.createFromTelegram(telegram, 0, 0);

        for (InvocationModel invocation : invocations)
        {
            this.modelMap_.put(invocation.getClassName() + "#" + invocation.getMethodName(),
                               invocation);
        }

        list.clear();
        list.addAll(this.modelMap_.values());
    }



    /**
     * {@inheritDoc}
     */
    public void setTelegramSender(TelegramSender telegramSender)
    {
        this.telegramSender_ = telegramSender;
    }

    /**
     * {@inheritDoc}
     */
    public void onCopy()
    {
        // Do Nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void onPrint()
    {
        // Do Nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void onReset()
    {
        // Do Nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void onStart()
    {
        this.modelMap_.clear();
        this.viewer_.getTable().clearAll();
        this.viewer_.refresh(true);
    }

    /**
     * {@inheritDoc}
     */
    public void onStop()
    {
        // Do Nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void onReload()
    {
        sendJvnLogListTelegram();
    }

    /**
     * {@inheritDoc}
     */
    public void connected()
    {
        setReloadButtonEnabled(true);
        sendJvnLogListTelegram();
    }

    /**
     * データ得るための電文を送信する。
     */
    private void sendJvnLogListTelegram()
    {
        if (this.telegramSender_ != null)
        {
            // 頭部データ対象を作って、データを設定する
            Header objHeader = new Header();
            objHeader.setByteTelegramKind(Common.BYTE_TELEGRAM_KIND_GET);
            objHeader.setByteRequestKind(Common.BYTE_REQUEST_KIND_REQUEST);

            // 頭部を電文対象に設定する
            Telegram objOutputTelegram = new Telegram();
            objOutputTelegram.setObjHeader(objHeader);

            this.telegramSender_.sendTelegram(objOutputTelegram);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void disconnected()
    {
        setReloadButtonEnabled(false);
    }

    /**
     * リロードボタンの状態を変更する。
     *
     * @param enabled 有効にするなら <code>true</code>
     */
    private void setReloadButtonEnabled(final boolean enabled)
    {
        if (ProfilerTab.this.reloadButton_.isDisposed() == false)
        {
            this.reloadButton_.getDisplay().asyncExec(new Runnable() {
                public void run()
                {
                    if (ProfilerTab.this.reloadButton_.isDisposed() == false)
                    {
                        ProfilerTab.this.reloadButton_.setEnabled(enabled);
                    }
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onLoad(PersistenceModel persistence)
    {
        // Do Nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void onSave(PersistenceModel persistence)
    {
        // Do Nothing.
    }
}
