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

    private MainCtrl         main_;

    /**
     * ビューを生成しする。
     * またコンテキストメニュー、ツールバー、
     * ダブルクリック時のアクションなどを初期化する。
     * 
     * @param parent 親のウィジット
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
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
            {}
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
        tbv = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI
        /*| SWT.VIRTUAL*/);

        //コンテンツプロバイダをセットする。
        tbv.setContentProvider(new TableContentProvider());

        //ラベルプロバイダをセットする。
        tbv.setLabelProvider(getLabelProvider());

        //テーブルヘッダを初期化する。
        initTableHeader(tbv);

        //テーブルに表示する対象となる要素を取得し、ビューアにセットする。
        Object inputElement = getInputElement(this.main_);
        tbv.setInput(inputElement);

        return this.tbv.getControl();
    }

    private TableViewer         tbv;

    static final private String COLMUN_TIME     = "Time";

    static final private String COLMUN_NODE     = "Node";

    static final private String COLMUN_NAME     = "Name";

    static final private String COLMUN_DURATION = "Duration";

    public JournalView()
    {}

    protected void init()
    {
        MainCtrl.getInstance().addDataChangeListeners(this);
    }

    public void updateData(final Object element)
    {
        Display.getDefault().asyncExec(new Runnable() {
            public void run()
            {
                addInputElement(element);
            }
        });
    }

    public void addInputElement(Object element)
    {
        this.tbv.add(element);
        Table table = this.tbv.getTable();
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
     * テーブルで表現する内容を取得する。 このビューの場合は、アクションのリストを返す。
     * 
     * 
     */
    protected Object getInputElement(MainCtrl main)
    {
        List list = main.getInvocationList();
        return list;
    }

    public void addEvent()
    {

    }

    /**
     * ラベルプロバイダを返す。
     */
    protected IBaseLabelProvider getLabelProvider()
    {
        return new ActionListLabelProvider();
    }

    /**
     * 
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus()
    {}

    /**
     * テーブルヘッダを初期化する。
     * 
     */
    protected void initTableHeader(TableViewer tbv)
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
        this.tbv = tbv;
    }

    class ActionListLabelProvider implements ITableLabelProvider, ITableColorProvider
    {
        /**
         * elementを表す行の、column_index列目のテキストを返す。
         * 
         * @param element
         * @param column_index
         * @return
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
         *      int)
         */
        public String getColumnText(Object element, int column_index)
        {
            String ret;

            InvocationModel invocation = (InvocationModel)element;

            // TODO 行番号にハードコードした実装としたため、改善したい。。
            switch (column_index)
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

        public Image getColumnImage(Object element, int column_index)
        {
            return null;
        }

        public Color getBackground(Object element, int columnIndex)
        {
            // TODO TANIMOTO 閾値の設定は検討する必要あり
            if (element == null || ((InvocationModel)element).getAverage() < 50)
            {
                return null;
            }

            return new Color(null, 255, 0, 0);
        }

        public Color getForeground(Object element, int columnIndex)
        {
            // TODO 自動生成されたメソッド・スタブ
            return null;
        }

        public void addListener(ILabelProviderListener ilabelproviderlistener)
        {}

        public void dispose()
        {}

        public boolean isLabelProperty(Object obj, String s)
        {
            return false;
        }

        public void removeListener(ILabelProviderListener ilabelproviderlistener)
        {}
    }
}