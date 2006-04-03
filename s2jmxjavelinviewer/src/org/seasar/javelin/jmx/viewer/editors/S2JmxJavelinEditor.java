package org.seasar.javelin.jmx.viewer.editors;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.seasar.javelin.jmx.viewer.editpart.S2JmxJavelinEditPartFactory;
import org.seasar.javelin.jmx.viewer.model.ArrowConnectionModel;
import org.seasar.javelin.jmx.viewer.model.ComponentModel;
import org.seasar.javelin.jmx.viewer.model.ContentsModel;
import org.seasar.javelin.jmx.viewer.model.InvocationModel;


public class S2JmxJavelinEditor extends GraphicalEditor
{
	private String hostName_ = "";
	private int    portNum_  = 0;
	private String domain_   = "";
	private long   warningThreshold_ = Long.MAX_VALUE;
	private long   alarmThreshold_   = Long.MAX_VALUE;

	protected void initializeGraphicalViewer()
	{
		Map<ObjectName, ComponentModel> componentMap = 
			new HashMap<ObjectName, ComponentModel>();

		GraphicalViewer viewer = getGraphicalViewer();

		// ç≈è„à ÇÃÉÇÉfÉãÇÃê›íË
		ContentsModel rootModel = new ContentsModel();

		try
		{
			JMXServiceURL url = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://" 
					+ hostName_
					+ ":" 
					+ portNum_
					+ "/jmxrmi");
			JMXConnector connector = JMXConnectorFactory.connect(url);
			MBeanServerConnection connection = 
				connector.getMBeanServerConnection();

			ObjectName objName = 
				new ObjectName(
					domain_ 
					+ ".container:type=org.seasar.javelin.jmx.bean.ContainerMBean");
			Set set = connection.queryMBeans(objName, null);
			if (set.size() == 0)
			{
				return;
			}
			
			ObjectInstance instance = (ObjectInstance) set.toArray()[0];

			ObjectName[] names;
			names = (ObjectName[]) connection.getAttribute(
					instance.getObjectName(), "AllComponentObjectName");
			
			for (ObjectName name : names)
			{
				String className  = (String)connection.getAttribute(name, "ClassName");
				
				ComponentModel component = new ComponentModel();
				component.setClassName(className);
				component.setConstraint(new Rectangle(0, 0, -1, -1));
				
				rootModel.addChild(component);
				componentMap.put(name, component);
			}
			
			for (ObjectName name : names)
			{
				ComponentModel target = componentMap.get(name);
				
				if (target == null) continue;
				
				ObjectName[] invocationNames = 
					(ObjectName[])connection.getAttribute(name, "AllInvocationObjectName");
				for (ObjectName invocationName : invocationNames)
				{
					Long count = 
						(Long)connection.getAttribute(invocationName, "Count");
					Long average = 
						(Long)connection.getAttribute(invocationName, "Average");
					Long minimum = 
						(Long)connection.getAttribute(invocationName, "Minimum");
					Long maximum = 
						(Long)connection.getAttribute(invocationName, "Maximum");
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
					invocation.setAlarmThreshold(alarmThreshold_);
					invocation.setWarningThreshold(warningThreshold_);
					invocation.setMethodName(methodName);
					
					target.addInvocation(invocation);
					
					ObjectName[] callerNames = 
						(ObjectName[])connection.getAttribute(invocationName, "AllCallerObjectName");
					for (ObjectName callerName : callerNames)
					{
						ObjectName callerComponentName = 
							(ObjectName)connection.getAttribute(callerName, "ComponentObjectName");
						ComponentModel source = componentMap.get(callerComponentName);

						if (source == null) continue;
						
						ArrowConnectionModel arrow = new ArrowConnectionModel();
						source.addSourceConnection(arrow);
						target.addTargetConnection(arrow);
						arrow.setSource(source);
						arrow.setTarget(target);
					}
				}
			}
			
			layoutModel(componentMap);
			
			viewer.setContents(rootModel);
			
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
	}

	private void layoutModel(Map<ObjectName, ComponentModel> componentMap)
	{
		Map<Integer, List<ComponentModel>> rankMap = 
			new HashMap<Integer, List<ComponentModel>>();
		
		for(ComponentModel component : componentMap.values())
		{
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
			
		}
		
		for (int rank : rankMap.keySet())
		{
			List<ComponentModel> list = rankMap.get(rank);
			int order = 32;
			for (ComponentModel component : list)
			{
				component.getConstraint().x = rank * 240 + 32;
				component.getConstraint().y = order;
				order = order + component.getInvocationList().size() * 16;
				order = order + 32;
			}
		}
	}

	private int getRank(int rank, ComponentModel component)
	{
		List<ArrowConnectionModel> list = 
			(List<ArrowConnectionModel>)
			component.getModelTargetConnections();

		if (list.size() > 0)
		{
			rank = rank + 1;
		}
		
		int newRank = rank;
		for (ArrowConnectionModel arrowModel : list)
		{
			int aRank = getRank(rank, arrowModel.getSource());
			if (aRank > newRank)
			{
				newRank = aRank;
			}
		}

		return newRank;
	}

	public void doSave(IProgressMonitor monitor)
	{
	}

	public void doSaveAs()
	{
	}

	public boolean isSaveAsAllowed()
	{
		return false;
	}

	public S2JmxJavelinEditor()
	{
		setEditDomain(new DefaultEditDomain(this));
	}

	protected void configureGraphicalViewer()
	{
		super.configureGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();
		// EditPartFactoryÇÃçÏê¨Ç∆ê›íË
		viewer.setEditPartFactory(new S2JmxJavelinEditPartFactory());
	}

	public void setDomain(String domain)
	{
		domain_ = domain;
	}

	public void setHostName(String hostName)
	{
		hostName_ = hostName;
	}

	public void setPortNum(int portNum)
	{
		portNum_ = portNum;
	}

	public void setWarningThreshold(long warningThreshold)
	{
		if (warningThreshold < 1) warningThreshold = Long.MAX_VALUE;
		warningThreshold_ = warningThreshold;
	}

	public void setAlarmThreshold(long alarmThreshold)
	{
		if (alarmThreshold < 1) alarmThreshold = Long.MAX_VALUE;
		alarmThreshold_ = alarmThreshold;
	}
}
