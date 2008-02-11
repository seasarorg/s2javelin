package org.seasar.javelin;

import java.util.HashMap;
import java.util.Map;

public class SystemStatusManager
{
	private static Map<String, Object> statusMap_ =
		new HashMap<String, Object>();
	
	public static void setValue(String key, Object value)
	{
		statusMap_.put(key, value);
	}

	public static Object getValue(String key)
	{
		return statusMap_.get(key);
	}
}
