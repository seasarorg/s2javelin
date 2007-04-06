package org.seasar.javelin.stats;

import java.io.IOException;
import java.util.Set;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import mx4j.tools.adaptor.http.HttpAdaptor;
import mx4j.tools.adaptor.http.XSLTProcessor;

public class Mx4JLauncher
{
	public static void execute(MBeanServer server, int httpPort)
		throws MalformedObjectNameException
		, InstanceAlreadyExistsException
		, MBeanRegistrationException
		, NotCompliantMBeanException
		, IOException
	{
        XSLTProcessor processor = new XSLTProcessor();
        ObjectName processorName = new ObjectName(
                                                  "Server:name=XSLTProcessor");
        if (server.isRegistered(processorName))
        {
            Set beanSet = server.queryMBeans(processorName, null);
            if (beanSet.size() > 0)
            {
                XSLTProcessor[] processors = (XSLTProcessor[])beanSet.toArray(new XSLTProcessor[beanSet.size()]);
                processor = (XSLTProcessor)(processors[0]);
            }
        }
        else
        {
            server.registerMBean(processor, processorName);
        }

        HttpAdaptor adaptor;
        ObjectName adaptorName = new ObjectName(
                                                "Adaptor:name=adaptor,port="
                                                        + httpPort);
        if (server.isRegistered(adaptorName))
        {
            Set beanSet = server.queryMBeans(adaptorName, null);
            if (beanSet.size() > 0)
            {
                HttpAdaptor[] adaptors = (HttpAdaptor[])beanSet.toArray(new HttpAdaptor[beanSet.size()]);
                adaptor = (HttpAdaptor)(adaptors[0]);
            }
        }
        else
        {
            adaptor = new HttpAdaptor();
            adaptor.setProcessor(processor);
            adaptor.setPort(httpPort);
            server.registerMBean(adaptor, adaptorName);
            adaptor.start();
        }
	}
}
