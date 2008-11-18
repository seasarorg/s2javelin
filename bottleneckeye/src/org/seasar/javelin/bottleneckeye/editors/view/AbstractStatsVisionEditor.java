package org.seasar.javelin.bottleneckeye.editors.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.seasar.javelin.bottleneckeye.communicate.Telegram;
import org.seasar.javelin.bottleneckeye.editpart.Blinker;
import org.seasar.javelin.bottleneckeye.editpart.ComponentEditPart;
import org.seasar.javelin.bottleneckeye.editpart.StatsVisionEditPartFactory;
import org.seasar.javelin.bottleneckeye.editpart.StatsVisionTreeEditPartFactory;
import org.seasar.javelin.bottleneckeye.model.ArrowConnectionModel;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;
import org.seasar.javelin.bottleneckeye.model.InvocationModel;
import org.seasar.javelin.bottleneckeye.model.MainCtrl;

/**
 * StatsVisionEditorクラス。
 * @param <T>
 */
public abstract class AbstractStatsVisionEditor<T> extends GraphicalEditor implements
        StatsVisionEditor
{
    /** 赤くブリンクする回数 */
    private static final int BLINK_COUNT = 5;

    /** メジャー警告色 */
    private static final Color RED    = ColorConstants.red;

    /** 接続状態のときの背景色 */
    static final Color                   CONNECTED_BACKCOLOR    = ColorConstants.white;

    /** 切断状態のときの背景色 */
    static final Color                   DISCONNECTED_BACKCOLOR = ColorConstants.lightGray;

    private String                       hostName_              = "";

    private int                          portNum_               = 0;

    private String                       domain_                = "";

    private boolean                      isDirty_               = false;

    private long                         warningThreshold_      = Long.MAX_VALUE;

    private long                         alarmThreshold_        = Long.MAX_VALUE;

    /** View画面のクラス１つに表示するメソッドの最大数 */
    private long                         maxMethodCount_        = Long.MAX_VALUE;

    private String                       mode_                  = "TCP";

    private String                       lineStyle_             = "NORMAL";

    /** ブリンク用タイマー。 */
    private static Timer                 blinkTimer__           =
                                                                        new Timer(
                                                                                  "BottleneckEye-BlinkThread");

    /** このエディタで管理するコンポーネントのリスト。 */
    private Map<String, ComponentEditPart> componentEditPartMap_     =
                                                                        new HashMap<String, ComponentEditPart>();

    /** Componentモデル設定用 */
    protected Map<T, ComponentModel>     componentMap           = new HashMap<T, ComponentModel>();

    /** ファイルから読み込み時にクラス位置を保存するためのマップ。 */
    private Map<T, Point>                pointMap               = new HashMap<T, Point>();

    /** コンポーネントの自動配置位置を決めるためのランク記憶用マップ。 */
    private Map<ComponentModel, Integer> revRankMap_            =
                                                                        new HashMap<ComponentModel, Integer>();

    /** コンテンツのルートモデル。 */
    protected ContentsModel              rootModel;

    /** ビューワ */
    private GraphicalViewer              viewer_;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeGraphicalViewer()
    {
        // GEFビューを作る
        setViewer(getGraphicalViewer());

        // 最上位のモデルの設定
        this.rootModel = new ContentsModel();
        this.rootModel.setContentsName(getTitle());
        getViewer().setContents(this.rootModel);

        setBackground(DISCONNECTED_BACKCOLOR);
    }

    /**
     * コンテンツ・アウトライナー・ページ
     */
    class StatsVisionContentOutlinePage extends ContentOutlinePage
    {

        /** ページをアウトラインとサムネイルに分離するコンポジット */
        private SashForm            sash;

        /** サムネイルを表示する為のフィギュア */
        private ScrollableThumbnail thumbnail;

        /** Viewerの破棄と連携するためのリスナ */
        private DisposeListener     disposeListener;

        /**
         * コンストラクタ。
         * GEFツリービューワを使用する。
         */
        public StatsVisionContentOutlinePage()
        {
            super(new TreeViewer());
        }

        // オーバーライド
        @Override
        public void createControl(Composite parent)
        {
            this.sash = new SashForm(parent, SWT.VERTICAL);

            // コンストラクタで指定したビューワの作成
            getViewer().createControl(this.sash);

            // エディット・ドメインの設定
            getViewer().setEditDomain(getEditDomain());
            // EditPartFactory の設定
            getViewer().setEditPartFactory(new StatsVisionTreeEditPartFactory());
            // グラフィカル・エディタのルート・モデルをツリー・ビューワにも設定
            getViewer().setContents(AbstractStatsVisionEditor.this.rootModel);
            // グラフィカル・エディタとツリー・ビューワとの間で選択を同期させる
            getSelectionSynchronizer().addViewer(getViewer());

            Canvas canvas = new Canvas(this.sash, SWT.BORDER);
            // サムネイル・フィギュアを配置する為の LightweightSystem
            LightweightSystem lws = new LightweightSystem(canvas);

            // RootEditPartのビューをソースとしてサムネイルを作成
            this.thumbnail =
                    new ScrollableThumbnail(
                                            (Viewport)((ScalableRootEditPart)getGraphicalViewer().getRootEditPart()).getFigure());
            this.thumbnail.setSource(((ScalableRootEditPart)getGraphicalViewer().getRootEditPart()).getLayer(LayerConstants.PRINTABLE_LAYERS));

            lws.setContents(this.thumbnail);

            this.disposeListener = new DisposeListener() {
                public void widgetDisposed(DisposeEvent e)
                {
                    // サムネイル・イメージの破棄
                    if (StatsVisionContentOutlinePage.this.thumbnail != null)
                    {
                        StatsVisionContentOutlinePage.this.thumbnail.deactivate();
                        StatsVisionContentOutlinePage.this.thumbnail = null;
                    }
                }
            };
            // グラフィカル・ビューワが破棄されるときにサムネイルも破棄する
            getGraphicalViewer().getControl().addDisposeListener(this.disposeListener);
        }

        // オーバーライド
        @Override
        public Control getControl()
        {
            // アウトライン・ビューをアクティブにした時に
            // フォーカスが設定されるコントロールを返す
            return this.sash;
        }

        // オーバーライド
        @Override
        public void dispose()
        {
            // SelectionSynchronizer からTreeViewerを削除
            getSelectionSynchronizer().removeViewer(getViewer());

            if (getGraphicalViewer().getControl() != null
                    && !getGraphicalViewer().getControl().isDisposed())
                getGraphicalViewer().getControl().removeDisposeListener(this.disposeListener);

            super.dispose();
        }

        /**
         * サムネイルを再描画する。
         */
        public void repaint()
        {
            this.thumbnail.repaint();
        }
    }

    @Override
    public Object getAdapter(Class type)
    {
        if (type == ZoomManager.class)
        {
            return ((ScalableRootEditPart)getGraphicalViewer().getRootEditPart()).getZoomManager();
        }

        // IContentOutlinePage 型のアダプターの要求に対して
        // コンテンツ・アウトライナー・ページを返す
        if (type == IContentOutlinePage.class)
        {
            return new StatsVisionContentOutlinePage();
        }

        return super.getAdapter(type);
    }

    /* (non-Javadoc)
     * @see org.seasar.javelin.bottleneckeye.editors.StatsVisionEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor monitor)
    {
        // Do Nothing
    }

    /**
     * {@inheritDoc}
     */
    public void reset()
    {
        for (ComponentModel component : this.componentMap.values())
        {
            for (InvocationModel invocation : component.getInvocationList())
            {
                invocation.setAverage(0);
                invocation.setMaximum(-1);
                invocation.setMinimum(-1);
                invocation.setCount(0);
                invocation.setThrowableCount(0);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public abstract boolean isConnected();

    /**
     * {@inheritDoc}
     */
    public void reload()
    {
        stop();
        start();
    }

    /**
     * クラス位置を更新する。
     * @param file
     */
    public void updatePointMap(Collection<ComponentModel> components)
    {
        for (ComponentModel component : components)
        {
            T key = getComponentKey(component.getClassName());
            this.pointMap.put(key, component.getConstraint().getTopLeft());
        }
    }

    /**
     * ファイルの内容をモデルに反映する。
     * @param file
     */
    public void loadContent(ContentsModel contents)
    {
        for (ComponentModel component : contents.getChildren())
        {
            T key = getComponentKey(component.getClassName());
            this.componentMap.put(key, component);
            this.pointMap.put(key, component.getConstraint().getTopLeft());
            this.rootModel.addChild(component);
        }
    }

    @Override
    protected void configureGraphicalViewer()
    {
        super.configureGraphicalViewer();

        GraphicalViewer viewer = getGraphicalViewer();

        IAction action;

        // ズーム可能なビューを作成するRootEditPartの設定
        ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
        viewer.setRootEditPart(rootEditPart);

        // ZoomManagerの取得
        ZoomManager manager = rootEditPart.getZoomManager();

        // ズームレベルの設定
        double[] zoomLevels =
                new double[]{0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0, 5.0, 10.0, 20.0};
        manager.setZoomLevels(zoomLevels);

        // ズーム レベル コントリビューションの設定
        List<String> zoomContributions = new ArrayList<String>();
        zoomContributions.add(ZoomManager.FIT_ALL);
        zoomContributions.add(ZoomManager.FIT_HEIGHT);
        zoomContributions.add(ZoomManager.FIT_WIDTH);
        manager.setZoomLevelContributions(zoomContributions);

        // 拡大アクションの作成と登録
        action = new ZoomInAction(manager);
        getActionRegistry().registerAction(action);
        // 縮小アクションの作成と登録
        action = new ZoomOutAction(manager);
        getActionRegistry().registerAction(action);

        // EditPartFactoryの作成と設定
        viewer.setEditPartFactory(new StatsVisionEditPartFactory(this));

        // キー・ハンドラの作成
        KeyHandler keyHandler = new KeyHandler();

        // DELキーと削除アクションを結びつける
        action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
        keyHandler.put(
            KeyStroke.getPressed(SWT.DEL, 127, 0),
            action);

        action = new ClassReloadAction(this);
        keyHandler.put(KeyStroke.getPressed(SWT.F5, 0), action);

        // グラフィカル・ビューワにキー・ハンドラを設定
        viewer.setKeyHandler(
            new GraphicalViewerKeyHandler(viewer).setParent(
                keyHandler));
    }

    public IAction getAction(String id)
    {
        IAction action = getActionRegistry().getAction(id);
        return action;
    }

    protected abstract T getComponentKey(String className);

    protected void layoutModel()
    {
        Map<Integer, List<ComponentModel>> rankMap = new HashMap<Integer, List<ComponentModel>>();

        // 全てのモデルのRANKを取る
        for (ComponentModel component : componentMap.values())
        {
            int rank = getRank(0, component);
            if ((rankMap.containsKey(rank)) && (!(rankMap.get(rank).contains(component))))
            {
                rankMap.get(rank).add(component);
            }
            else
            {
                List<ComponentModel> list = new ArrayList<ComponentModel>();
                list.add(component);
                rankMap.put(rank, list);
            }
            this.revRankMap_.put(component, rank);
        }

        for (int rank : rankMap.keySet())
        {
            List<ComponentModel> list = rankMap.get(rank);
            int order = 32;
            for (ComponentModel component : list)
            {

                T key = null;
                try
                {
                    key = getComponentKey(component.getClassName());
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                Point point = null;
                if (key != null)
                {
                    point = this.pointMap.get(key);
                }

                if (point != null)
                {
                    component.getConstraint().x = point.x;
                    component.getConstraint().y = point.y;
                }
                else
                {
                    component.getConstraint().x = rank * 240 + 32;
                    component.getConstraint().y = order;
                    order = order + component.getInvocationList().size() * 16;
                    order = order + 32;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected int getRank(int rank, ComponentModel component)
    {

        // このモデルを接続先とするコネクションのリストを返す
        List<ArrowConnectionModel> list =
                (List<ArrowConnectionModel>)component.getModelTargetConnections();

        // ルートモデルのRankを取る
        if ((this.revRankMap_.size() == 0))
        {
            return rank;
        }

        if (this.revRankMap_.containsKey(component))
        {
            int currentRank = this.revRankMap_.get(component);
            return currentRank;
        }

        int newRank = rank;

        // Ｒａｎｋもう取った呼び出す元から、一番大きいRank値を取って増える
        for (ArrowConnectionModel arrowModel : list)
        {

            if (!(this.revRankMap_.size() == 0)
                    && (!(this.revRankMap_.containsKey(arrowModel.getSource()))))
            {
                continue;
            }

            if (arrowModel.getSource() == component)
            {
                continue;
            }
            int aRank = getRank(rank, arrowModel.getSource()) + 1;
            if (aRank > newRank)
            {
                newRank = aRank;
            }
        }

        return newRank;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        stop();
    }

    @Override
    public boolean isDirty()
    {
        return this.isDirty_;
    }

    public void setDirty(boolean isDirty)
    {
        this.isDirty_ = isDirty;
        firePropertyChange(PROP_DIRTY);
    }

    public String getHostName()
    {
        return this.hostName_;
    }

    public void setHostName(String hostName)
    {
        this.hostName_ = hostName;
    }

    public int getPortNum()
    {
        return this.portNum_;
    }

    public void setPortNum(int portNum)
    {
        this.portNum_ = portNum;
    }

    public String getDomain()
    {
        return this.domain_;
    }

    public void setDomain(String domain)
    {
        this.domain_ = domain;
    }

    public long getWarningThreshold()
    {
        return this.warningThreshold_;
    }

    public void setWarningThreshold(long warningThreshold)
    {
        if (warningThreshold < 0)
            warningThreshold = Long.MAX_VALUE;
        this.warningThreshold_ = warningThreshold;
    }

    public long getAlarmThreshold()
    {
        return this.alarmThreshold_;
    }

    public void setAlarmThreshold(long alarmThreshold)
    {
        if (alarmThreshold < 0)
        {
            alarmThreshold = Long.MAX_VALUE;
        }
        this.alarmThreshold_ = alarmThreshold;
    }

    /**
     * 表示するメソッド数の最大数を返す。
     *
     * @return 表示するメソッド数の最大数
     */
    public long getMaxMethodCount()
    {
        return this.maxMethodCount_;
    }

    /**
     * {@inheritDoc}
     */
    public void setMaxMethodCount(long maxMethodCount)
    {
        if (maxMethodCount < 0)
        {
            maxMethodCount = Long.MAX_VALUE;
        }
        this.maxMethodCount_ = maxMethodCount;
    }

    public String getMode()
    {
        return this.mode_;
    }

    public void setMode(String mode)
    {
        this.mode_ = mode;
    }

    public String getLineStyle()
    {
        return this.lineStyle_;
    }

    public void setLineStyle(String lineStyle)
    {
        this.lineStyle_ = lineStyle;
    }

    public Map<T, ComponentModel> getComponentMap()
    {
        return this.componentMap;
    }

    public void setComponentMap(Map<T, ComponentModel> componentMap)
    {
        this.componentMap = componentMap;
    }

    @Override
    public GraphicalViewer getGraphicalViewer()
    {
        return super.getGraphicalViewer();
    }

    /**
     * {@inheritDoc}
     */
    public void addComponentEditPart(String className, ComponentEditPart componentPart)
    {
        this.componentEditPartMap_.put(className, componentPart);
    }

    /**
     * {@inheritDoc}
     */
    public void listeningGraphicalViewer(Telegram telegram)
    {
        if (telegram.getObjBody().length == 0)
        {
            return;
        }

        // InvocationMapに、該当InvocationModelを設定する
        InvocationModel[] invocations =
                InvocationModel.createFromTelegram(telegram, this.alarmThreshold_,
                                                   this.warningThreshold_);

        // TODO ハードコーディング
        InvocationModel invocation = invocations[0];
        MainCtrl.getInstance().addInvocationModel(invocation);
        MainCtrl.getInstance().notifyDataChangeListener(invocation);

        // 「クラス名、メソッド名」で赤くブリンクメソッドのキーを取得する
        String className = invocation.getClassName();
        String methodName = invocation.getMethodName();
        if (className.startsWith("ROOT-"))
        {
            className = className.substring("ROOT-".length());
        }

        // 赤くブリンクで表示する
        ComponentEditPart componentEditPart = this.componentEditPartMap_.get(className);
        if (componentEditPart != null)
        {
            List<TimerTask> blinkTaskList =
                exceededThresholdAlarm(className, methodName, componentEditPart);
            for (int index = 0; index < blinkTaskList.size(); index++)
            {
                TimerTask blinkTask = blinkTaskList.get(index);
                blinkTimer__.schedule(blinkTask, 1000 * index);
            }
        }

    }

    /**
     * アラームの閾値を超えた時に呼ばれるメソッド。
     *
     * @param className クラス名
     * @param methodName メソッド名
     * @param componentEditPart コンポーネントの EditPart
     * @return ブリンクさせるタイマータスクのリスト
     */
    public List<TimerTask> exceededThresholdAlarm(String className, String methodName, ComponentEditPart componentEditPart)
    {
        List<TimerTask> result = new ArrayList<TimerTask>();

        Label label = componentEditPart.getMethodLabel(methodName);
        InvocationModel invocation = componentEditPart.getInvocationModel(methodName);

        if (label == null || invocation == null 
                || invocation.getClassName() == null || invocation.getMethodName() == null)
        {
            return result;
        }

        Control control = null;
        try
        {
            EditPartViewer viewer = getViewer();
            if (viewer == null)
            {
                return result;
            }
            control = viewer.getControl();
            if (control == null)
            {
                return result;
            }
        }
        catch (NullPointerException npe)
        {
            return result;
        }

        Display display = control.getDisplay();

        ((ComponentModel)componentEditPart.getModel()).setExceededThresholdAlarm(null);

        Map<String, ComponentEditPart> componentEditPartMap = Collections.synchronizedMap(this.componentEditPartMap_);
        for(int index = 0; index < BLINK_COUNT; index++)
        {
            TimerTask blinkJobRed = new Blinker(display, className, methodName, componentEditPartMap, ColorConstants.black, RED);
            TimerTask blinkJobNormal = new Blinker(display, className, methodName, componentEditPartMap, componentEditPart.getFgColor(invocation), componentEditPart.getBgColor());
            result.add(blinkJobRed);
            result.add(blinkJobNormal);
        }
            
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void setBackground(Color color)
    {
        GraphicalViewer viewer = getGraphicalViewer();
        if (viewer != null)
        {
            Control control = viewer.getControl();
            if (control != null)
            {
                control.setBackground(color);
            }
            ((ScalableRootEditPart)viewer.getRootEditPart()).getLayer(
                                                                      LayerConstants.PRINTABLE_LAYERS).repaint();
        }
    }

    public GraphicalViewer getViewer()
    {
        return this.viewer_;
    }

    public void setViewer(GraphicalViewer viewer)
    {
        this.viewer_ = viewer;
    }

    /**
     * {@inheritDoc}
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {
//        if (this.equals(part))
//        { // Propagated from MyMultiPageEditor. 
            updateActions(getSelectionActions()); 
//        }
    }

    /**
     * ComponentEditPart リストをクリアする。
     */
    public void clearComponentEditPartList()
    {
        this.componentEditPartMap_.clear();
    }

}
