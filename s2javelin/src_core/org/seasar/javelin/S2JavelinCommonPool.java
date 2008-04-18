package org.seasar.javelin;

/**
 * @author fujii
 */
public class S2JavelinCommonPool {
	/** �N���X�� */
	private static ThreadLocal<String> className_ = new ThreadLocal<String>();
	
	/** ���\�b�h�� */
	private static ThreadLocal<String> methodName_ = new ThreadLocal<String>();
	
	/** 臒l */
	private static ThreadLocal<Integer> threshold_ = new ThreadLocal<Integer>();

	/** callTree */
	private static ThreadLocal<CallTree> callTree_ = new ThreadLocal<CallTree>();

	/**
	 * call tree ���擾����
	 * @return calltree
	 */
	public static CallTree getCallTree() 
	{
		return callTree_.get();
	}

	/**
	 * call tree ���Z�b�g����
	 * @param callTree
	 */
	public static void setCallTree(CallTree callTree) 
	{
		callTree_.set(callTree);
	}

	/**
	 * �N���X�����擾����
	 * @return �N���X��
	 */
	public static String getClassName() 
	{
		return className_.get();
	}

	/**
	 * �N���X�����Z�b�g����
	 * @param className �N���X��
	 */
	public static void setClassName(String className) 
	{
		className_.set(className);
	}

	/**
	 * ���\�b�h�����擾����
	 * @return ���\�b�h��
	 */
	public static String getMethodName() 
	{
		return methodName_.get();
	}

	/**
	 * ���\�b�h�����Z�b�g����
	 * @param methodName ���\�b�h��
	 */
	public static void setMethodName(String methodName) 
	{
		methodName_.set(methodName);
	}

	/**
	 * �^�C�}�[��臒l���擾����
	 * @return �^�C�}�[��臒l
	 */
	public static Integer getThreshold() {
		return threshold_.get();
	}

	/**
	 * �^�C�}�[��臒l���Z�b�g����
	 * @param threshold �^�C�}�[��臒l
	 */
	public static void setThreshold(Integer threshold) {
		threshold_.set(threshold);
	}
}
