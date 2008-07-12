package org.seasar.javelin.bottleneckeye.editors.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.seasar.javelin.bottleneckeye.communicate.Telegram;
import org.seasar.javelin.bottleneckeye.communicate.TelegramClientManager;
import org.seasar.javelin.bottleneckeye.editpart.ComponentEditPart;
import org.seasar.javelin.bottleneckeye.model.ArrowConnectionModel;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;
import org.seasar.javelin.bottleneckeye.model.InvocationModel;

public class JmxStatsVisionEditor extends AbstractStatsVisionEditor<ObjectName>
{
    /**
     * Listeningするとき、初期表示時使うEditPartを持つ
     */
    public ComponentEditPart componentEditPart = null;

    public JmxStatsVisionEditor()
    {
        setEditDomain(new DefaultEditDomain(this));
    }

    /**
     * {@inheritDoc}
     */
    public void setBlnReload(boolean blnReload)
    {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void start()
    {
        Map<ObjectName, ComponentModel> componentMap = new HashMap<ObjectName, ComponentModel>();

        GraphicalViewer viewer = getGraphicalViewer();

        // 最上位のモデルの設定
        ContentsModel rootModel = new ContentsModel();
        rootModel.setContentsName(getTitle());

        try
        {
            JMXServiceURL url =
                    new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + getHostName() + ":"
                            + getPortNum() + "/jmxrmi");
            JMXConnector connector = JMXConnectorFactory.connect(url);
            MBeanServerConnection connection = connector.getMBeanServerConnection();

            ObjectName objName =
                    new ObjectName(getDomain()
                            + ".container:type=org.seasar.javelin.bean.ContainerMBean");
            Set set = connection.queryMBeans(objName, null);
            if (set.size() == 0)
            {
                return;
            }

            ObjectInstance instance = (ObjectInstance)set.toArray()[0];

            ObjectName[] names;
            names =
                    (ObjectName[])connection.getAttribute(instance.getObjectName(),
                                                          "AllComponentObjectName");

            for (ObjectName name : names)
            {
                String className = (String)connection.getAttribute(name, "ClassName");

                ComponentModel component = new ComponentModel();
                component.setClassName(className);
                component.setConstraint(new Rectangle(0, 0, -1, -1));

                rootModel.addChild(component);
                componentMap.put(name, component);
            }

            for (ObjectName name : names)
            {
                ComponentModel target = componentMap.get(name);

                if (target == null)
                    continue;

                ObjectName[] invocationNames =
                        (ObjectName[])connection.getAttribute(name, "AllInvocationObjectName");
                for (ObjectName invocationName : invocationNames)
                {
                    Long count = (Long)connection.getAttribute(invocationName, "Count");
                    Long average = (Long)connection.getAttribute(invocationName, "Average");
                    Long minimum = (Long)connection.getAttribute(invocationName, "Minimum");
                    Long maximum = (Long)connection.getAttribute(invocationName, "Maximum");
                    Long throwableCount =
                            (Long)connection.getAttribute(invocationName, "ThrowableCount");

                    String methodName =
                            (String)connection.getAttribute(invocationName, "MethodName");

                    InvocationModel invocation = new InvocationModel();
                    invocation.setCount(count.longValue());
                    invocation.setAverage(average.longValue());
                    invocation.setMinimum(minimum.longValue());
                    invocation.setMaximum(maximum.longValue());
                    invocation.setThrowableCount(throwableCount);
                    invocation.setAlarmThreshold(getAlarmThreshold());
                    invocation.setWarningThreshold(getWarningThreshold());
                    invocation.setMethodName(methodName);

                    target.addInvocation(invocation);

                    connection.addNotificationListener(invocationName, invocation, null, null);

                    ObjectName[] callerNames =
                            (ObjectName[])connection.getAttribute(invocationName,
                                                                  "AllCallerObjectName");
                    for (ObjectName callerName : callerNames)
                    {
                        ObjectName callerComponentName =
                                (ObjectName)connection.getAttribute(callerName,
                                                                    "ComponentObjectName");
                        ComponentModel source = componentMap.get(callerComponentName);

                        if (source == null)
                            continue;

                        ArrowConnectionModel arrow = new ArrowConnectionModel();
                        source.addSourceConnection(arrow);
                        target.addTargetConnection(arrow);
                        arrow.setSource(source);
                        arrow.setTarget(target);
                    }
                }
            }

        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (MalformedObjectNameException e)
        {
            e.printStackTrace();
        }
        catch (AttributeNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (InstanceNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (MBeanException e)
        {
            e.printStackTrace();
        }
        catch (ReflectionException e)
        {
            e.printStackTrace();
        }

        setComponentMap(componentMap);
        layoutModel();

        viewer.setContents(rootModel);
    }

    /**
     * {@inheritDoc}
     */
    public void connect()
    {
        // 未実装
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect()
    {
        // 未実装
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        // 未実装
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConnected()
    {
        // 未実装
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void addResponseTelegram(Telegram telegram)
    {
        // 未実装
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addComponentEditPart(String className, ComponentEditPart componentPart)
    {
        this.componentEditPart = componentPart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset()
    {
        super.reset();

        try
        {
            JMXServiceURL url =
                    new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + getHostName() + ":"
                            + getPortNum() + "/jmxrmi");
            JMXConnector connector = JMXConnectorFactory.connect(url);
            MBeanServerConnection connection = connector.getMBeanServerConnection();

            ObjectName objName =
                    new ObjectName(getDomain()
                            + ".container:type=org.seasar.javelin.bean.ContainerMBean");
            Set set = connection.queryMBeans(objName, null);
            if (set.size() == 0)
            {
                return;
            }

            ObjectInstance instance = (ObjectInstance)set.toArray()[0];

            connection.invoke(instance.getObjectName(), "reset", null, null);
        }
        catch (Exception ex)
        {
            // Do nothing.
        }
    }

    /**
     * {@inheritDoc}
     */
    public void requestStatus()
    {
        // Do nothing.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObjectName getComponentKey(String className)
    {
        try
        {
            return new ObjectName("component:type=" + className);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    public TelegramClientManager getTelegramClientManager()
    {
        return null;
    }

}
