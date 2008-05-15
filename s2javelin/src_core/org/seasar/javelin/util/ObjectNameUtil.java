package org.seasar.javelin.util;

import java.util.ArrayList;
import java.util.List;

import org.seasar.javelin.S2JavelinConfig;
import org.seasar.javelin.bean.ComponentMBean;
import org.seasar.javelin.bean.InvocationMBean;

public class ObjectNameUtil
{
    private static final List<String> keyList__          = new ArrayList<String>();

    private static final List<String> valueList__        = new ArrayList<String>();

    static
    {
        addToMap("\\*", "#####ASTERISC#####");
        addToMap("\\?", "#####QUESTION#####");
        addToMap("=", "#####EQUAL#####");
        addToMap(":", "#####COLON#####");
        addToMap("\r", "#####CR#####");
        addToMap("\n", "#####LF#####");
        addToMap(",", "#####COMMA#####");
        addToMap("\\\"", "#####DOUBLEQUOTE#####");
        addToMap("\\\\", "#####BACKSLASH#####");
    }

    private static void addToMap(String key, String value)
    {
        keyList__.add(key);
        valueList__.add(value);
    }

    public static String encode(String decodedStr)
    {
        String encodedStr = decodedStr;
        for (int index = 0; index < keyList__.size(); index++)
        {
            String key = keyList__.get(index);
            String value = valueList__.get(index);
            encodedStr = encodedStr.replaceAll(key, value);
        }
        return encodedStr;
    }

    public static String decode(String encodedStr)
    {
        String decodedStr = encodedStr;
        for (int index = 0; index < keyList__.size(); index++)
        {
            String key = keyList__.get(index);
            String value = valueList__.get(index);
            decodedStr = decodedStr.replaceAll(value, key);
        }
        return decodedStr;
    }

	/**
	 * MBeanに登録する文字列を生成する
	 * 
	 * @param className
	 *            クラス名
	 * @param methodName
	 *            メソッド名
	 * @param config
	 *            設定ファイルから読み込んだ設定値
	 * @return 登録する文字列
	 */
	public static String createInvocationBeanName(String className, String methodName,
	        S2JavelinConfig config)
	{
        if(className == null || className.equals(""))
        {
            className = "unknown";
        }
        if(methodName == null || methodName.equals(""))
        {
            methodName = "unknown";
        }

        return config.getDomain() + ".invocation:type=" + InvocationMBean.class.getName()
	            + ",class=" + encode(className) + ",method="
	            + encode(methodName);
	}

	public static String createComponentBeanName(String className, S2JavelinConfig config)
	{
	    if(className == null || className.equals(""))
	    {
	        className = "unknown";
	    }
	    return config.getDomain() + ".component:type=" + ComponentMBean.class.getName() + ",class="
	            + encode(className);
	}

}
