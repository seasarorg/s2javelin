package org.seasar.javelin;

/**
 * @author fujii
 */
public class S2JavelinCommonPool {
	/** クラス名 */
	private static ThreadLocal<String> className_ = new ThreadLocal<String>();
	
	/** メソッド名 */
	private static ThreadLocal<String> methodName_ = new ThreadLocal<String>();
	
	/** 閾値 */
	private static ThreadLocal<Integer> threshold_ = new ThreadLocal<Integer>();

	/** callTree */
	private static ThreadLocal<CallTree> callTree_ = new ThreadLocal<CallTree>();

	/**
	 * call tree を取得する
	 * @return calltree
	 */
	public static CallTree getCallTree() 
	{
		return callTree_.get();
	}

	/**
	 * call tree をセットする
	 * @param callTree
	 */
	public static void setCallTree(CallTree callTree) 
	{
		callTree_.set(callTree);
	}

	/**
	 * クラス名を取得する
	 * @return クラス名
	 */
	public static String getClassName() 
	{
		return className_.get();
	}

	/**
	 * クラス名をセットする
	 * @param className クラス名
	 */
	public static void setClassName(String className) 
	{
		className_.set(className);
	}

	/**
	 * メソッド名を取得する
	 * @return メソッド名
	 */
	public static String getMethodName() 
	{
		return methodName_.get();
	}

	/**
	 * メソッド名をセットする
	 * @param methodName メソッド名
	 */
	public static void setMethodName(String methodName) 
	{
		methodName_.set(methodName);
	}

	/**
	 * タイマーの閾値を取得する
	 * @return タイマーの閾値
	 */
	public static Integer getThreshold() {
		return threshold_.get();
	}

	/**
	 * タイマーの閾値をセットする
	 * @param threshold タイマーの閾値
	 */
	public static void setThreshold(Integer threshold) {
		threshold_.set(threshold);
	}
}
