package org.seasar.javelin.bottleneckeye.editors;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.seasar.javelin.bottleneckeye.editpart.StatsVisionEditPartFactory;
import org.seasar.javelin.bottleneckeye.editpart.StatsVisionTreeEditPartFactory;
import org.seasar.javelin.bottleneckeye.model.ArrowConnectionModel;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;
import org.seasar.javelin.bottleneckeye.model.InvocationModel;

public abstract class AbstractStatsVisionEditor<T> 
    extends GraphicalEditor implements StatsVisionEditor
{
    private static final String START_OF_METHOD = "<START-OF-METHOD>";

    private static final String END_OF_METHOD = "<END-OF-METHOD>";
    
    private static final String START_OF_RELATION = "<START-OF-RELATION>";

    private static final String END_OF_RELATION = "<END-OF-RELATION>";
    
    private String           hostName_         = "";

    private int              portNum_          = 0;

    private String           domain_           = "";

    private boolean          isDirty_          = false;

    public long              warningThreshold_ = Long.MAX_VALUE;

    public long              alarmThreshold_   = Long.MAX_VALUE;

    public String            mode_             = "TCP";

    public String            lineStyle_        = "NORMAL";
    
    // Componentモデル設定用
    protected Map<T, ComponentModel> componentMap      
        = new HashMap<T, ComponentModel>();

    /** ファイルから読み込み時にクラス位置を保存するためのマップ。 */
    private   Map<T, Point> pointMap = new HashMap<T, Point>();

    /** コンポーネントの自動配置位置を決めるためのランク記憶用マップ。 */
    private   Map<ComponentModel, Integer> revRankMap
        = new HashMap<ComponentModel, Integer>();

    /** コンテンツのルートモデル。 */
    protected ContentsModel rootModel;

    // コンテンツ・アウトライナー・ページ
    class StatsVisionContentOutlinePage extends ContentOutlinePage {

        //ページをアウトラインとサムネイルに分離するコンポジット
        private SashForm sash;

        // サムネイルを表示する為のフィギュア
        private ScrollableThumbnail thumbnail;

        // Viewerの破棄と連携するためのリスナ。
        private DisposeListener disposeListener;
        
        public StatsVisionContentOutlinePage() {
          // GEFツリービューワを使用する
          super(new TreeViewer());
        }

        // オーバーライド
        public void createControl(Composite parent) {
          this.sash = new SashForm(parent, SWT.VERTICAL);
          
          // コンストラクタで指定したビューワの作成
          getViewer().createControl(sash);
          
          // エディット・ドメインの設定
          getViewer().setEditDomain(getEditDomain());
          // EditPartFactory の設定
          getViewer().setEditPartFactory(new StatsVisionTreeEditPartFactory());
          // グラフィカル・エディタのルート・モデルをツリー・ビューワにも設定
          getViewer().setContents(rootModel);
          // グラフィカル・エディタとツリー・ビューワとの間で選択を同期させる
          getSelectionSynchronizer().addViewer(getViewer());

          Canvas canvas = new Canvas(sash, SWT.BORDER);
          // サムネイル・フィギュアを配置する為の LightweightSystem
          LightweightSystem lws = new LightweightSystem(canvas);

          // RootEditPartのビューをソースとしてサムネイルを作成
          thumbnail = new ScrollableThumbnail(
              (Viewport) ((ScalableRootEditPart) getGraphicalViewer()
                  .getRootEditPart()).getFigure());
          thumbnail.setSource(((ScalableRootEditPart) getGraphicalViewer()
              .getRootEditPart())
              .getLayer(LayerConstants.PRINTABLE_LAYERS));
          
          lws.setContents(thumbnail);

          disposeListener = new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
              // サムネイル・イメージの破棄
              if (thumbnail != null) {
                thumbnail.deactivate();
                thumbnail = null;
              }
            }
          };
          // グラフィカル・ビューワが破棄されるときにサムネイルも破棄する
          getGraphicalViewer().getControl().addDisposeListener(
              disposeListener);
        }

        // オーバーライド
        public Control getControl() {
          // アウトライン・ビューをアクティブにした時に
          // フォーカスが設定されるコントロールを返す
          return sash;
        }
        
        // オーバーライド
        public void dispose() {
          // SelectionSynchronizer からTreeViewerを削除
          getSelectionSynchronizer().removeViewer(getViewer());

          if (getGraphicalViewer().getControl() != null
                  && !getGraphicalViewer().getControl().isDisposed())
                getGraphicalViewer().getControl().removeDisposeListener(disposeListener);

              super.dispose();
        }
    }

    public Object getAdapter(Class type) {
        if (type == ZoomManager.class) {
            return ((ScalableRootEditPart) getGraphicalViewer()
                    .getRootEditPart()).getZoomManager();
        }
        
        // IContentOutlinePage 型のアダプターの要求に対して
        // コンテンツ・アウトライナー・ページを返す
        if (type == IContentOutlinePage.class) {
          return new StatsVisionContentOutlinePage();
        }

        return super.getAdapter(type);
    }
    
    /* (non-Javadoc)
     * @see org.seasar.javelin.bottleneckeye.editors.StatsVisionEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave(IProgressMonitor monitor)
    {
        try
        {
            String data = createContent();

            IFile file = ((IFileEditorInput)getEditorInput()).getFile();
            InputStream stream = new ByteArrayInputStream(data.toString().getBytes());
            file.setContents(stream, true, false, monitor);

            getCommandStack().markSaveLocation();
            setDirty(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

	protected String createContent() {
		String lineSeparator = System.getProperty("line.separator");

		StringBuilder data = new StringBuilder(1024);
		data.append(getHostName()).append(lineSeparator);
		data.append(getPortNum()).append(lineSeparator);
		data.append(getDomain()).append(lineSeparator);
		data.append(getWarningThreshold()).append(lineSeparator);
		data.append(getAlarmThreshold()).append(lineSeparator);
		data.append(getMode()).append(lineSeparator);
		data.append(getLineStyle()).append(lineSeparator);

		if (getComponentMap() != null)
		{
		    Collection<ComponentModel> values = getComponentMap().values();
		    for (ComponentModel component : values)
		    {
		        String className = component.getClassName();
		        int x = component.getConstraint().x;
		        int y = component.getConstraint().y;

		        String componentInfo = className + "=" + x + "," + y;
		        data.append(componentInfo).append(lineSeparator);
		        Point point = new Point();
		        point.x = x;
		        point.y = y;
		        pointMap.put(getComponentKey(className), point);

		        // メソッド情報を保存する。
		        data.append(START_OF_METHOD).append(lineSeparator);
		        
		        for (InvocationModel invocation : component.getInvocationList())
		        {
		            data.append(invocation.getAverage());
		            data.append(",");
		            data.append(invocation.getMaximum());
		            data.append(",");
		            data.append(invocation.getMinimum());
		            data.append(",");
		            data.append(invocation.getThrowableCount());
		            data.append(",");
		            data.append(invocation.getWarningThreshold());
		            data.append(",");
		            data.append(invocation.getAlarmThreshold());
		            data.append(",");
		            data.append(invocation.getMethodName());
		            data.append(lineSeparator);
		        }
		        
		        data.append(END_OF_METHOD).append(lineSeparator);

		        data.append(START_OF_RELATION).append(lineSeparator);
		        
		        List targetList = component.getModelSourceConnections();
		        for (int index = 0; index < targetList.size(); index++)
		        {
		            ArrowConnectionModel connectionModel 
		                = (ArrowConnectionModel)(targetList.get(index));
		            
		            ComponentModel targetModel = connectionModel.getTarget();
		            data.append(targetModel.getClassName());
		            data.append(lineSeparator);
		        }
		        
		        data.append(END_OF_RELATION).append(lineSeparator);
		    }

		    for (T key : pointMap.keySet())
		    {
		        if (!getComponentMap().containsKey(key))
		        {
		            Point point = pointMap.get(key);
		            String className = key.toString();
		            int x = point.x;
		            int y = point.y;

		            String componentInfo = className + "=" + x + "," + y;
		            data.append(componentInfo).append(lineSeparator);
		        }
		    }
		}
		return new String(data);
	}

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

    protected void load()
    {
        try
        {
            IFile file = ((IFileEditorInput)getEditorInput()).getFile();
            InputStream stream = file.getContents();

            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader bReader = new BufferedReader(reader);

            loadContent(bReader);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

	protected void loadContent(BufferedReader reader) throws IOException {
		String line = reader.readLine();

		for (int index = 0; index < 7; index++)
		{
		    line = reader.readLine();
		}

		// クラス情報を読み込む。
		while (line != null)
		{
		    ComponentModel component = null;
		    
		    int index = line.lastIndexOf("=");
		    if (index > 0)
		    {
		        String className = line.substring(0, index);

		        int xyIndex = line.lastIndexOf(",");
		        int x = Integer.parseInt(line.substring(index + 1, xyIndex));
		        int y = Integer.parseInt(line.substring(xyIndex + 1));

		        Point point = new Point(x, y);
		        pointMap.put(getComponentKey(className), point);
		        
		        component = componentMap.get(getComponentKey(className));
		        
		        if (component == null)
		        {
		            component = new ComponentModel();
		            component.setClassName(className);
		            component.setConstraint(new Rectangle(0, 0, -1, -1));
		            rootModel.addChild(component);
		            
		            componentMap.put(getComponentKey(className), component);
		        }
		    }

		    line = reader.readLine();
		    while(line != null && !line.equals(START_OF_METHOD))
		    {
		        line = reader.readLine();
		    }
		    
		    // メソッド情報を読み込む。
		    line = reader.readLine();
		    while(line != null && !line.equals(END_OF_METHOD))
		    {
		        int    from = 0;
		        int    to;
		        String value;

		        to = line.indexOf(",", from);
		        value = line.substring(from, to);
		        long avg = Long.parseLong(value);

		        from = to + 1;
		        to = line.indexOf(",", from);
		        value = line.substring(from, to);
		        long max = Long.parseLong(value);

		        from = to + 1;
		        to = line.indexOf(",", from);
		        value = line.substring(from, to);
		        long min = Long.parseLong(value);

		        from = to + 1;
		        to = line.indexOf(",", from);
		        value = line.substring(from, to);
		        long err = Long.parseLong(value);
		        
		        from = to + 1;
		        to = line.indexOf(",", from);
		        value = line.substring(from, to);
		        long warn = Long.parseLong(value);
		        
		        from = to + 1;
		        to = line.indexOf(",", from);
		        value = line.substring(from, to);
		        long alarm = Long.parseLong(value);
		        
		        from = to + 1;
		        value = line.substring(from);
		        
		        InvocationModel invocation = new InvocationModel();
		        invocation.setMethodName(value);
		        invocation.setAverage(avg);
		        invocation.setMaximum(max);
		        invocation.setMinimum(min);
		        invocation.setThrowableCount(err);
		        invocation.setWarningThreshold(warn);
		        invocation.setAlarmThreshold(alarm);
		        
		        component.addInvocation(invocation);
		        
		        line = reader.readLine();
		    }

		    line = reader.readLine();
		    while(line != null && !line.equals(START_OF_RELATION))
		    {
		        line = reader.readLine();
		    }
		    
		    // 関連情報を読み込む。
		    line = reader.readLine();
		    while(line != null && !line.equals(END_OF_RELATION))
		    {
		        ArrowConnectionModel arrow = new ArrowConnectionModel();
		        component.addSourceConnection(arrow);
		        arrow.setSource(component);
		        
		        String className = line;
		        
		        ComponentModel target 
		            = componentMap.get(getComponentKey(className));
		        
		        if (target == null)
		        {
		            target = new ComponentModel();
		            target.setClassName(className);
		            target.setConstraint(new Rectangle(0, 0, -1, -1));
		            rootModel.addChild(target);
		            
		            componentMap.put(getComponentKey(className), target);
		        }
		        
		        target.addTargetConnection(arrow);
		        arrow.setTarget(target);
		        
		        line = reader.readLine();
		    }
		    
		    line = reader.readLine();
		}
	}

    protected void configureGraphicalViewer() {
        super.configureGraphicalViewer();

        GraphicalViewer viewer = getGraphicalViewer();
        
        IAction action;

        // ズーム可能なビューを作成するRootEditPartの設定
        ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
        viewer.setRootEditPart(rootEditPart);
        
        // ZoomManagerの取得
        ZoomManager manager = rootEditPart.getZoomManager();
        
        // ズームレベルの設定
        double[] zoomLevels = new double[] {
          0.25,0.5,0.75,1.0,1.5,2.0,2.5,3.0,4.0,5.0,10.0,20.0
        };
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
    }

    public IAction getAction(String id)
    {
        IAction action = getActionRegistry().getAction(id);
        return action;
    }
    
    protected abstract T getComponentKey(String className);

    protected void layoutModel(Map<T, ComponentModel> componentMap)
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
            revRankMap.put(component, rank);
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
                    point = pointMap.get(key);
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
        List<ArrowConnectionModel> list = (List<ArrowConnectionModel>)component.getModelTargetConnections();

        // ルートモデルのRankを取る
        if ((revRankMap.size() == 0))
        {
            return rank;
        }

        if (revRankMap.containsKey(component))
        {
            int currentRank = revRankMap.get(component);
            return currentRank;
        }

        int newRank = rank;

        // Ｒａｎｋもう取った呼び出す元から、一番大きいRank値を取って増える
        for (ArrowConnectionModel arrowModel : list)
        {

            if (!(revRankMap.size() == 0) && (!(revRankMap.containsKey(arrowModel.getSource()))))
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

    public boolean isDirty()
    {
        return isDirty_;
    }

    public void setDirty(boolean isDirty)
    {
        isDirty_ = isDirty;
        firePropertyChange(PROP_DIRTY);
    }

    public String getHostName()
    {
        return hostName_;
    }

    public void setHostName(String hostName)
    {
        hostName_ = hostName;
    }

    public int getPortNum()
    {
        return portNum_;
    }

    public void setPortNum(int portNum)
    {
        portNum_ = portNum;
    }

    public String getDomain()
    {
        return domain_;
    }

    public void setDomain(String domain)
    {
        domain_ = domain;
    }

    public long getWarningThreshold()
    {
        return warningThreshold_;
    }

    public void setWarningThreshold(long warningThreshold)
    {
        if (warningThreshold < 0)
            warningThreshold = Long.MAX_VALUE;
        warningThreshold_ = warningThreshold;
    }

    public long getAlarmThreshold()
    {
        return alarmThreshold_;
    }

    public void setAlarmThreshold(long alarmThreshold)
    {
        if (alarmThreshold < 0)
            alarmThreshold = Long.MAX_VALUE;
        alarmThreshold_ = alarmThreshold;
    }

    public String getMode()
    {
        return mode_;
    }

    public void setMode(String mode)
    {
        mode_ = mode;
    }

    public String getLineStyle()
    {
        return lineStyle_;
    }

    public void setLineStyle(String lineStyle)
    {
        lineStyle_ = lineStyle;
    }
    
    public Map<T, ComponentModel> getComponentMap()
    {
        return componentMap;
    }

    public void setComponentMap(Map<T, ComponentModel> componentMap)
    {
        this.componentMap = componentMap;
    }
    
    public GraphicalViewer getGraphicalViewer()
    {
        return super.getGraphicalViewer();
    }
}
