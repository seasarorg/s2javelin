package org.seasar.javelin.util;

import java.util.ArrayList;
import java.util.List;

public class ObjectNameUtil
{
    private static final String       INVALID_OBJECTNAME = "[\\*\\?=:,\n\r]";

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

}
