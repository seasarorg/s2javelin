package org.seasar.javelin.jmx.viewer.editors;

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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.seasar.javelin.jmx.viewer.model.ArrowConnectionModel;
import org.seasar.javelin.jmx.viewer.model.ComponentModel;
import org.seasar.javelin.jmx.viewer.model.ContentsModel;
import org.seasar.javelin.jmx.viewer.model.InvocationModel;


public class S2JmxJavelinEditor extends GraphicalEditor
{

	protected void initializeGraphicalViewer()
	{
		Map<ObjectName, ComponentModel> componentMap = 
			new HashMap<ObjectName, ComponentModel>();

		GraphicalViewer viewer = getGraphicalViewer();

		// ç≈è„à ÇÃÉÇÉfÉãÇÃê›íË
		ContentsModel rootModel = new ContentsModel();

		try
		{
			String hostName = "localhost";
			int portNum = 10001;
			JMXServiceURL u = new JMXServiceURL(
					"service:jmx:rmi:///jndi/rmi://" 
					+ hostName 
					+ ":" 
					+ portNum
					+ "/jmxrmi");
			JMXConnector c = JMXConnectorFactory.connect(u);
			MBeanServerConnection connection;
			connection = c.getMBeanServerConnection();

			ObjectName objName = new ObjectName(
					"org.seasar.javelin.jmx.s2jsfexample.container:type=org.seasar.javelin.jmx.ContainerMBean");
			Set set = connection.queryMBeans(objName, null);
			ObjectInstance instance = (ObjectInstance) set.toArray()[0];

			ObjectName[] names;
			names = (ObjectName[]) connection.getAttribute(
					instance.getObjectName(), "AllComponentObjectName");
			
			for (ObjectName name : names)
			{
				String className  = (String)connection.getAttribute(name, "ClassName");
				
				ComponentModel invocation = new ComponentModel();
				invocation.setClassName(className);
				invocation.setConstraint(new Rectangle(0, 0, -1, -1));
				
				rootModel.addChild(invocation);
				componentMap.put(name, invocation);
				
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
					
					String methodName = 
						(String)connection.getAttribute(invocationName, "MethodName");
					
					InvocationModel invocation = new InvocationModel();
					invocation.setCount(count.longValue());
					invocation.setAverage(average.longValue());
					invocation.setMinimum(minimum.longValue());
					invocation.setMaximum(maximum.longValue());
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

		viewer.setContents(rootModel);
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
		viewer.setEditPartFactory(new MyEditPartFactory());
	}

}
