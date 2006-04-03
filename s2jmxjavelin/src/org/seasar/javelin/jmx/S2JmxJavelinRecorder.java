package org.seasar.javelin.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.seasar.javelin.jmx.bean.Component;
import org.seasar.javelin.jmx.bean.ComponentMBean;
import org.seasar.javelin.jmx.bean.Invocation;
import org.seasar.javelin.jmx.bean.InvocationMBean;

public class S2JmxJavelinRecorder
{
    /** �v���b�g�t�H�[��MBean�T�[�o */
    private static MBeanServer server_ = 
    	ManagementFactory.getPlatformMBeanServer();


    /**
     * ���\�b�h�R�[���c���[�̋L�^�p�I�u�W�F�N�g�B
     */
    private static ThreadLocal<CallTree> callTree_ = new ThreadLocal<CallTree>()
    {
        protected synchronized CallTree initialValue()
        {
            return null;
        }
    };

    /**
     * ���\�b�h�̌Ăяo�����I�u�W�F�N�g�B
     */
    private static ThreadLocal<CallTreeNode> callerNode_ = new ThreadLocal<CallTreeNode>()
    {
        protected synchronized CallTreeNode initialValue()
        {
            return null;
        }
    };

    /**
     * �O�����B
     * 
     * @param invocationBean
     */
	public static void preProcess(
			String domain
			, String className
			, String methodName
			, int  intervalMax
			, int  throwableMax
			, long recordThreshold)
	{
		try
		{
	        Component  componentBean = MBeanManager.getComponent(className);
	    	String name = 
	    		domain
	    		+ ".component:type=" 
	    		+ ComponentMBean.class.getName() 
	    		+ ",class="
	    		+ className;
	        ObjectName componentName = new ObjectName(name);
	        if (componentBean == null)
	        {
	        	componentBean = new Component(componentName, className);

	        	if (server_.isRegistered(componentName))
	        	{
		        	server_.unregisterMBean(componentName);
	        	}
	        	server_.registerMBean(componentBean, componentName);
	        	MBeanManager.setComponent(className, componentBean);
	        }
	        
	        Invocation invocationBean = 
	        	componentBean.getInvocation(methodName);
	    	name = 
	    		domain
	    		+ ".invocation:type="
	    		+ InvocationMBean.class.getName()
	    		+ ",class="
	    		+ className 
	    		+ ",method="
	    		+ methodName;
			ObjectName objName = new ObjectName(name);
			
	        if (invocationBean == null)
	        {
	        	invocationBean = 
	        		new Invocation(
	        				objName
	        				, componentName
	        				, className
	        				, methodName
	        				, intervalMax
	        				, throwableMax
	        				, recordThreshold);
	        	
	        	componentBean.addInvocation(invocationBean);
	        	if (server_.isRegistered(objName))
	        	{
		    		server_.unregisterMBean(objName);
	        	}
	    		server_.registerMBean(invocationBean, objName);
	        }

	        // �Ăяo�������擾�B
	        CallTreeNode node = callerNode_.get();

	        if (node == null)
	        {
	            // ����Ăяo�����̓R�[���c���[������������B
	        	CallTree tree = new CallTree();
	        	callTree_.set(tree);
	        	
	        	node = new CallTreeNode();
	        	tree.setRootNode(node);
	        }
	        else
	        {
	        	CallTreeNode parent = node;
	        	node = new CallTreeNode();
	        	parent.addChild(node);
	        }
	        
	    	node.setInvocation(invocationBean);
	    	
	        // �Ăяo������A
	    	// ���񃍃O�o�͎��̌Ăяo�����Ƃ��Ďg�p���邽�߂ɕۑ�����B
	    	callerNode_.set(node);
		}
		catch(Exception ex)
		{
			// �z��O�̗�O�����������ꍇ�͕W���G���[�o�͂ɏo�͂��Ă����B
			ex.printStackTrace();
		}
	}

	/**
	 * �㏈���i�{�����������j�B
	 * @param spent
	 */
	public static void postProcess(long spent)
	{
		try
		{
	        // �Ăяo�������擾�B
	        CallTreeNode node = callerNode_.get();
	        if (node == null)
	        {
	        	// �Ăяo������񂪎擾�ł��Ȃ��ꍇ�͏������L�����Z������B
	        	// (���ʃ��C���ŗ�O�����������ꍇ�̂��߁B)
	        	return;
	        }

	        node.setAccumulatedTime(spent);
	        
	        CallTreeNode parent = node.getParent();
	        if (parent != null)
	        {
	            callerNode_.set(parent);
	        }
	        else if (spent >= node.getInvocation().getRecordThreshold())
	        {
	        	// ���[�g�m�[�h�ł̌o�ߎ��Ԃ�臒l�𒴂��Ă����ꍇ�́A
	        	// �g�����U�N�V�������L�^����B
	        	recordTransaction(node);
	        	callerNode_.set(null);
	        }
		}
		catch(Exception ex)
		{
			// �z��O�̗�O�����������ꍇ�͕W���G���[�o�͂ɏo�͂��Ă����B
			ex.printStackTrace();
		}
	}

	/**
	 * �㏈���i�{�������s���j�B
	 * @param cause
	 */
	public static void postProcess(Throwable cause)
	{
		try
		{
	        // �Ăяo�������擾�B
	        CallTreeNode node = callerNode_.get();
	        if (node == null)
	        {
	        	// �Ăяo������񂪎擾�ł��Ȃ��ꍇ�͏������L�����Z������B
	        	// (���łɋL�^�ς݂̗�O�̂��߁B)
	        	return;
	        }

	    	// ����������O���L�^���Ă����B
	        node.getInvocation().addThrowable(cause);

	        //�Ăяo�������������Ă����B
	    	callerNode_.set(null);
		}
		catch(Exception ex)
		{
			// �z��O�̗�O�����������ꍇ�͕W���G���[�o�͂ɏo�͂��Ă����B
			ex.printStackTrace();
		}
	}
	
	/**
	 * �g�����U�N�V�������L�^����B
	 * 
	 * @param node
	 */
	private static void recordTransaction(CallTreeNode node)
	{
    	Invocation invocation = node.getInvocation();
    	invocation.addInterval(node.getElapsedTime());
    	if (node.getParent() != null)
    	{
    		invocation.addCaller(node.getParent().getInvocation());
    	}
    	
    	for (CallTreeNode child : node.getChildren())
    	{
    		recordTransaction(child);
    	}
	}
}
