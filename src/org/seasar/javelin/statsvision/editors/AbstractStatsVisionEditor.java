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
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.ui.IFileEditorInput;
import org.seasar.javelin.statsvision.model.ArrowConnectionModel;
import org.seasar.javelin.statsvision.model.ComponentModel;

public abstract class AbstractStatsVisionEditor<T> extends GraphicalEditor implements
        StatsVisionEditor
{
    private String                       hostName_         = "";

    private int                          portNum_          = 0;

    private String                       domain_           = "";

    private boolean                      isDirty_          = false;

    public long                          warningThreshold_ = Long.MAX_VALUE;

    public long                          alarmThreshold_   = Long.MAX_VALUE;

    public String                        mode_             = "TCP";

    // Componentモデル設定用
    private Map<T, ComponentModel>       componentMap      = new HashMap<T, ComponentModel>();

    private Map<T, Point>                pointMap          = new HashMap<T, Point>();

    private Map<ComponentModel, Integer> revRankMap        = new HashMap<ComponentModel, Integer>();

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

            while (line != null)
            {
                int index = line.lastIndexOf("=");
                if (index > 0)
                {
                    String className = line.substring(0, index);

                    int xyIndex = line.lastIndexOf(",");
                    int x = Integer.parseInt(line.substring(index + 1, xyIndex));
                    int y = Integer.parseInt(line.substring(xyIndex + 1));

                    Point point = new Point(x, y);
                    pointMap.put(getComponentKey(className), point);
                }

                line = bReader.readLine();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    protected abstract T getComponentKey(String className);

    protected void layoutModel(Map<T, ComponentModel> componentMap)
    {

        Map<Integer, List<ComponentModel>> rankMap = new HashMap<Integer, List<ComponentModel>>();

        // 先ず、ルートモデルのRankを取る
        for (ComponentModel component : componentMap.values())
        {
            String rootFlag = component.getClassName().substring(0, 5);
            if (rootFlag.endsWith("ROOT-"))
            {
                component.setClassName(component.getClassName().substring(5));
                int rank = getRank(0, component);
                if (rankMap.containsKey(rank))
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
        }

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
                    key = getComponentKey(getDomain()
                            + ".component:type=org.seasar.javelin.bean.ComponentMBean,class="
                            + component.getClassName());
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
