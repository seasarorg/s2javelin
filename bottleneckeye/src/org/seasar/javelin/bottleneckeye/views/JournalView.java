package org.seasar.javelin.bottleneckeye.views;

import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.seasar.javelin.bottleneckeye.event.DataChangeListener;
import org.seasar.javelin.bottleneckeye.model.InvocationModel;
import org.seasar.javelin.bottleneckeye.model.MainCtrl;
import org.seasar.javelin.bottleneckeye.util.NormalDateFormatter;

/**
 * ジャーナルを表示するビュー。
 * 
 * @author cero-t
 */
public class JournalView extends ViewPart implements DataChangeListener
{

    /** このビューの中で使用するビューア（テーブルやリスト） */
    private Control          viewer_;

    /** このビューで保持するイベントの最大数。これを超えると、最も古いイベントから削除される。 */
    private static final int JOURNAL_MAX = 1000;

    /** メインコントロール。 */
    private MainCtrl         main_;

    /**
     * {@inheritDoc}<br>
     * ビューを生成する。<br>
     * またコンテキストメニュー、ツールバー、ダブルクリック時のアクションなどを初期化する。
     */
    @Override
    public void createPartControl(Composite parent)
    {
        this.viewer_ = createComposite(parent);
        if (this.viewer_ != null)
        {
            hookContextMenu();
        }
        init();
    }

    /**
     * コンテキストメニューを初期化し、マウス操作で表示されるようにする。
     * メニューの項目は、fillContextMenuによって生成する。
     *
     */
    private void hookContextMenu()
    {
        //メニューマネージャを初期化する。
        MenuManager menuMgr = new MenuManager("#PopupMenu");

        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager)
            {
                // Do Nothing.
            }
        });

        Menu menu = menuMgr.createContextMenu(this.viewer_);
        this.viewer_.setMenu(menu);
    }

    /**
     * ビューで用いるテーブルを生成する。
     *  
     * @param parent 親のウィジット
     * @return テーブル
     * @see jp.co.smg.efv.views.AbstractView#createComposite(org.eclipse.swt.widgets.Composite)
     */
    public Control createComposite(Composite parent)
    {
        this.main_ = MainCtrl.getInstance();

        //テーブルのビューアを生成する。
        this.tbv_ = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI
        /*| SWT.VIRTUAL*/);

        //コンテンツプロバイダをセットする。
        this.tbv_.setContentProvider(new TableContentProvider());

        //ラベルプロバイダをセットする。
        this.tbv_.setLabelProvider(getLabelProvider());

        //テーブルヘッダを初期化する。
        initTableHeader(this.tbv_);

        //テーブルに表示する対象となる要素を取得し、ビューアにセットする。
        Object inputElement = getInputElement(this.main_);
        this.tbv_.setInput(inputElement);

        return this.tbv_.getControl();
    }

    /** テーブルビューワ */
    private TableViewer         tbv_;

    /** Timeカラム */
    private static final String COLMUN_TIME     = "Time";

    /** Nodeカラム */
    private static final String COLMUN_NODE     = "Node";

    /** Nameカラム */
    private static final String COLMUN_NAME     = "Name";

    /** Durationカラム */
    private static final String COLMUN_DURATION = "Duration";

    /**
     * デフォルトコンストラクタ。
     */
    public JournalView()
    {
        // Do Nothing.
    }

    /**
     * クラスの初期化を行う。
     */
    private void init()
    {
        MainCtrl.getInstance().addDataChangeListeners(this);
    }

    /**
     * {@inheritDoc}
     */
    public void updateData(final Object element)
    {
        Display.getDefault().asyncExec(new Runnable() {
            public void run()
            {
                addInputElement(element);
            }
        });
    }

    /**
     * 入力要素を追加する。
     * @param element 入力要素
     */
    public void addInputElement(Object element)
    {
        this.tbv_.add(element);
        Table table = this.tbv_.getTable();
        int itemCount = table.getItemCount();

        if (itemCount > JOURNAL_MAX)
        {
            table.remove(0);
            itemCount--;
        }

        int row = itemCount - 1;

        table.showItem(table.getItem(row));
    }

    /**
     * テーブルで表現する内容を取得する。<br>
     * このビューの場合は、アクションのリストを返す。
     * @param main メインコントロール
     * @return アクションのリスト
     */
    protected Object getInputElement(MainCtrl main)
    {
        List<InvocationModel> invocationList = main.getInvocationList();
        return invocationList;
    }

    /**
     * イベントを追加する。
     */
    public void addEvent()
    {
        // Do Nothing.
    }

    /**
     * ラベルプロバイダを取得する。
     * @return ラベルプロバイダ
     */
    private IBaseLabelProvider getLabelProvider()
    {
        return new ActionListLabelProvider();
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        // Do Nothing.
    }

    /**
     * テーブルヘッダを初期化する。
     * @param tbv テーブルビューワ
     */
    private void initTableHeader(TableViewer tbv)
    {
        TableColumn column = new TableColumn(tbv.getTable(), SWT.LEFT);

        column = new TableColumn(tbv.getTable(), SWT.LEFT);
        column.setText(COLMUN_TIME);
        column.setWidth(140);

        column = new TableColumn(tbv.getTable(), SWT.LEFT);
        column.setText(COLMUN_NODE);
        column.setWidth(100);

        column = new TableColumn(tbv.getTable(), SWT.LEFT);
        column.setText(COLMUN_NAME);
        column.setWidth(100);

        column = new TableColumn(tbv.getTable(), SWT.LEFT);
        column.setText(COLMUN_DURATION);
        column.setWidth(70);

        tbv.getTable().setHeaderVisible(true);
        this.tbv_ = tbv;
    }

    /**
     * Viewのラベルプロバイダ。
     * @author cero-t
     */
    private static class ActionListLabelProvider implements ITableLabelProvider,
            ITableColorProvider
    {
        /**
         * {@inheritDoc}<br>
         * elementを表す行の、column_index列目のテキストを返す。
         */
        public String getColumnText(Object element, int columIndex)
        {
            String ret;

            InvocationModel invocation = (InvocationModel)element;

            // TODO 行番号にハードコードした実装としたため、改善したい。。
            switch (columIndex)
            {
            case 0:
                ret = "-";
                break;
            case 1:
                long time = invocation.getDate().getTime();
                ret = NormalDateFormatter.format(time);
                break;
            case 2:
                ret = invocation.getClassName();
                break;
            case 3:
                ret = invocation.getMethodName();
                break;
            case 4:
                ret = String.valueOf(invocation.getAverage());
                break;
            default:
                ret = "";
            }

            if (ret == null)
            {
                return "";
            }

            return ret;
        }

        /**
         * {@inheritDoc}
         */
        public Image getColumnImage(Object element, int columnIndex)
        {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public Color getBackground(Object element, int columnIndex)
        {
            // TODO TANIMOTO 閾値の設定は検討する必要あり
            if (element == null || ((InvocationModel)element).getAverage() < 50)
            {
                return null;
            }

            return new Color(null, 255, 0, 0);
        }

        /**
         * {@inheritDoc}
         */
        public Color getForeground(Object element, int columnIndex)
        {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public void addListener(ILabelProviderListener ilabelproviderlistener)
        {
            // Do Nothing.
        }

        /**
         * {@inheritDoc}
         */
        public void dispose()
        {
            // Do Nothing.
        }

        /**
         * {@inheritDoc}
         */
        public boolean isLabelProperty(Object obj, String s)
        {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public void removeListener(ILabelProviderListener ilabelproviderlistener)
        {
            // Do Nothing.
        }
    }
}
