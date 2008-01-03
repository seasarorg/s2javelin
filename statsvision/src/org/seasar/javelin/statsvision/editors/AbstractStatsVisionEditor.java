package org.seasar.javelin.statsvision.editors;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.seasar.javelin.statsvision.editpart.StatsVisionEditPartFactory;
import org.seasar.javelin.statsvision.model.ArrowConnectionModel;
import org.seasar.javelin.statsvision.model.ComponentModel;
import org.seasar.javelin.statsvision.model.ContentsModel;
import org.seasar.javelin.statsvision.model.InvocationModel;

public abstract class AbstractStatsVisionEditor<T> 
    extends GraphicalEditor implements StatsVisionEditor
{
    private static final String START_OF_METHOD = "<START-OF-METHOD>";

    private static final String END_OF_METHOD = "<END-OF-METHOD>";
    
    private String                       hostName_         = "";

    private int                          portNum_          = 0;

    private String                       domain_           = "";

    private boolean                      isDirty_          = false;

    public long                          warningThreshold_ = Long.MAX_VALUE;

    public long                          alarmThreshold_   = Long.MAX_VALUE;

    public String                        mode_             = "TCP";

    // Component���f���ݒ�p
    protected Map<T, ComponentModel>       componentMap      = new HashMap<T, ComponentModel>();

    private   Map<T, Point>                pointMap          = new HashMap<T, Point>();

    private   Map<ComponentModel, Integer> revRankMap        = new HashMap<ComponentModel, Integer>();

    protected ContentsModel rootModel;

    // �y�[�W���A�E�g���C���ƃT���l�C���ɕ�������R���|�W�b�g
    private SashForm sash;
    
    // �T���l�C����\������ׂ̃t�B�M���A
    private ScrollableThumbnail thumbnail;

    private DisposeListener disposeListener;
    
    /* (non-Javadoc)
     * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave(IProgressMonitor monitor)
    {
        try
        {
            String lineSeparator = System.getProperty("line.separator");

            StringBuilder data = new StringBuilder(1024);
            data.append(getHostName()).append(lineSeparator);
            data.append(getPortNum()).append(lineSeparator);
            data.append(getDomain()).append(lineSeparator);
            data.append(getWarningThreshold()).append(lineSeparator);
            data.append(getAlarmThreshold()).append(lineSeparator);
            data.append(getMode()).append(lineSeparator);

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

                    // ���\�b�h����ۑ�����B
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
                        data.append(invocation.getMethodName());
                        data.append(lineSeparator);
                    }
                    
                    data.append(END_OF_METHOD).append(lineSeparator);
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

    protected void load()
    {
        try
        {
            IFile file = ((IFileEditorInput)getEditorInput()).getFile();
            InputStream stream = file.getContents();

            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader bReader = new BufferedReader(reader);

            String line = bReader.readLine();

            for (int index = 0; index < 6; index++)
            {
                line = bReader.readLine();
            }

            // �N���X����ǂݍ��ށB
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
                    
                    component = new ComponentModel();
                    component.setClassName(className);
                    component.setConstraint(new Rectangle(0, 0, -1, -1));
                    rootModel.addChild(component);
                    
                    componentMap.put(getComponentKey(className), component);
                    
                    
                }

                line = bReader.readLine();
                while(line != null && !line.equals(START_OF_METHOD))
                {
                    line = bReader.readLine();
                }
                
                // ���\�b�h����ǂݍ��ށB
                line = bReader.readLine();
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
                    value = line.substring(from);
                    
                    InvocationModel invocation = new InvocationModel();
                    invocation.setMethodName(value);
                    invocation.setAverage(avg);
                    invocation.setMaximum(max);
                    invocation.setMinimum(min);
                    invocation.setThrowableCount(err);
                    
                    component.addInvocation(invocation);
                    
                    line = bReader.readLine();
                }
                
                line = bReader.readLine();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    // �I�[�o�[���C�h
    public void init(IPageSite pageSite) {
//      super.init(pageSite);
        // �O���t�B�J���E�G�f�B�^�ɓo�^����Ă���A�N�V�������擾
        ActionRegistry registry = getActionRegistry();
        // �A�E�g���C���E�y�[�W�ŗL���ɂ���A�N�V����
        IActionBars bars = pageSite.getActionBars();

        String id = ActionFactory.UNDO.getId();
        bars.setGlobalActionHandler(id, registry.getAction(id));

        id = ActionFactory.REDO.getId();
        bars.setGlobalActionHandler(id, registry.getAction(id));

        id = ActionFactory.DELETE.getId();
        bars.setGlobalActionHandler(id, registry.getAction(id));
        bars.updateActionBars();
    }
    
    public void createControl(Composite parent) {
        sash = new SashForm(parent, SWT.VERTICAL);
        
        Canvas canvas = new Canvas(sash, SWT.BORDER);
        
        // �T���l�C���E�t�B�M���A��z�u����ׂ� LightweightSystem
        LightweightSystem lws = new LightweightSystem(canvas);

        // RootEditPart�̃r���[���\�[�X�Ƃ��ăT���l�C�����쐬
        thumbnail = new ScrollableThumbnail(
            (Viewport) ((ScalableRootEditPart) getGraphicalViewer()
                .getRootEditPart()).getFigure());
        thumbnail.setSource(((ScalableRootEditPart) getGraphicalViewer()
            .getRootEditPart())
            .getLayer(LayerConstants.PRINTABLE_LAYERS));
        
        lws.setContents(thumbnail);

        disposeListener = new DisposeListener() {
          public void widgetDisposed(DisposeEvent e) {
            // �T���l�C���E�C���[�W�̔j��
            if (thumbnail != null) {
              thumbnail.deactivate();
              thumbnail = null;
            }
          }
        };
        
        // �O���t�B�J���E�r���[�����j�������Ƃ��ɃT���l�C�����j������
        getGraphicalViewer().getControl().addDisposeListener(
            disposeListener);
    }

    // �I�[�o�[���C�h
    public Control getControl() {
      // �A�E�g���C���E�r���[���A�N�e�B�u�ɂ�������
      // �t�H�[�J�X���ݒ肳���R���g���[����Ԃ�
      return sash;
    }
    
    // �I�[�o�[���C�h
    public void dispose() {
      if (getGraphicalViewer().getControl() != null
          && !getGraphicalViewer().getControl().isDisposed())
        getGraphicalViewer().getControl().removeDisposeListener(disposeListener);

      super.dispose();
    }
    
    protected void configureGraphicalViewer() {
        super.configureGraphicalViewer();

        GraphicalViewer viewer = getGraphicalViewer();
        
        // �Y�[���\�ȃr���[���쐬����RootEditPart�̐ݒ�
        ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
        viewer.setRootEditPart(rootEditPart);
        
        // ZoomManager�̎擾
        ZoomManager manager = rootEditPart.getZoomManager();
        
        // �Y�[�����x���̐ݒ�
        double[] zoomLevels = new double[] {
          0.25,0.5,0.75,1.0,1.5,2.0,2.5,3.0,4.0,5.0,10.0,20.0
        };
        manager.setZoomLevels(zoomLevels);
        
        // �Y�[�� ���x�� �R���g���r���[�V�����̐ݒ�
        List<String> zoomContributions = new ArrayList<String>();
        zoomContributions.add(ZoomManager.FIT_ALL);
        zoomContributions.add(ZoomManager.FIT_HEIGHT);
        zoomContributions.add(ZoomManager.FIT_WIDTH);
        manager.setZoomLevelContributions(zoomContributions);
        
        // �g��A�N�V�����̍쐬�Ɠo�^
        IAction action = new ZoomInAction(manager);
        getActionRegistry().registerAction(action);
        // �k���A�N�V�����̍쐬�Ɠo�^
        action = new ZoomOutAction(manager);
        getActionRegistry().registerAction(action);

        // EditPartFactory�̍쐬�Ɛݒ�
        viewer.setEditPartFactory(new StatsVisionEditPartFactory(this));
    }

    protected abstract T getComponentKey(String className);

    protected void layoutModel(Map<T, ComponentModel> componentMap)
    {

        Map<Integer, List<ComponentModel>> rankMap = new HashMap<Integer, List<ComponentModel>>();

        // �S�Ẵ��f����RANK�����
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

        // ���̃��f����ڑ���Ƃ���R�l�N�V�����̃��X�g��Ԃ�
        List<ArrowConnectionModel> list = (List<ArrowConnectionModel>)component.getModelTargetConnections();

        // ���[�g���f����Rank�����
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

        // �q����������������Ăяo��������A��ԑ傫��Rank�l������đ�����
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

    public Map<T, ComponentModel> getComponentMap()
    {
        return componentMap;
    }

    public void setComponentMap(Map<T, ComponentModel> componentMap)
    {
        this.componentMap = componentMap;
    }
}
