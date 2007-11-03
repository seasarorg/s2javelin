package org.seasar.javelin.util;

import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.S2JavelinConfig;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

public class StatsUtil
{

	public static long getElapsedTime(CallTreeNode node)
	{
	    long elapsedTime = node.getAccumulatedTime();
	    for (int index = 0; index < node.getChildren().size(); index++)
	    {
	    	CallTreeNode child = (CallTreeNode)  node.getChildren().get(index);
	        elapsedTime = elapsedTime - child.getAccumulatedTime();
	    }
	
	    return elapsedTime;
	}

	/**
	 * スレッドを識別するための文字列を出力する。 
	 * フォーマット：スレッド名@スレッドクラス名@スレッドオブジェクトのID
	 * 
	 * @return スレッドを識別するための文字列
	 */
	public static String createThreadIDText( )
	{
	    Thread currentThread = Thread.currentThread();
	
	    StringBuffer threadId = new StringBuffer();
	    threadId.append(currentThread.getName());
	    threadId.append("@" + currentThread.getClass().getName());
	    threadId.append("@" + StatsUtil.getObjectID(currentThread));
	
	    return threadId.toString();
	}

	/**
	 * オブジェクトIDを16進形式の文字列として取得する。
	 * 
	 * @param object オブジェクトIDを取得オブジェクト。
	 * @return オブジェクトID。
	 */
	public static String getObjectID(Object object)
	{
	    // 引数がnullの場合は"null"を返す。
	    if (object == null)
	    {
	        return "null";
	    }
	
	    return Integer.toHexString(System.identityHashCode(object));
	}
	
	/**
	 * objectをtoStringで文字列に変換する。
	 * 
	 * toStringで例外が発生した場合は、
	 * 標準エラー出力にobjectのクラス名とスタックトレースを出力し、
	 * クラス名@オブジェクトIDを返す。
	 * 
	 * @param object
	 * @return
	 */
	public static String toStr(Object object) {
		// 引数がnullの場合は"null"を返す。
		if (object == null) {
			return "null";
		}

		String result;
		try {
			result = object.toString();
		} catch (Throwable th) {
			S2JavelinConfig config = new S2JavelinConfig();
			if(config.isDebug())
			{
				System.err.println("Javelin Exception "
						+ object.getClass().toString() + "#toString(): ");
				th.printStackTrace();
			}
			result = object.getClass().toString() + "@"
					+ StatsUtil.getObjectID(object);
		}
		return result;
	}


	/**
	 * objectをtoStringで文字列に変換、指定長で切る。
	 * 
	 * toStringで例外が発生した場合は、
	 * 標準エラー出力にobjectのクラス名とスタックトレースを出力し、
	 * クラス名@オブジェクトIDを返す。
	 * 指定長を超えている場合は指定長で切り、"..."を付与する。
	 * 
	 * @param object 文字列化対象オブジェクト
	 * @param length 文字列指定長
	 * @return
	 */
	public static String toStr(Object object, int length) {
		// 引数がnullの場合は"null"を返す。
		if (object == null) {
			return "null";
		}

		String result;
		try {
			result = object.toString();
			if(length == 0) {
				result = "";
			} else  if(result.length() > length) {
				result = result.substring(0, length) + "...";
			}
		} catch (Throwable th) {
			S2JavelinConfig config = new S2JavelinConfig();
			if(config.isDebug())
			{
				System.err.println("Javelin Exception "
						+ object.getClass().toString() + "#toString(): ");
				th.printStackTrace();
			}
			result = object.getClass().toString() + "@"
					+ StatsUtil.getObjectID(object);
		}
		
		return result;
	}
	
	/**
	 * バイト列をbyte[length]:FFFF...形式に変換、指定長で切る。
	 * 
	 * @param binary バイナリ
	 * @return
	 */
	public static String toStr(byte binary) {
		String hex = Integer.toHexString(((int)binary) & 0xFF).toUpperCase();
		String result = "byte[1]:" + "00".substring(hex.length()) + hex;
		return result;
	}
	
	/**
	 * バイト列をbyte[length]:FFFF...形式に変換(最大で先頭8バイトを16進出力)。
	 * 
	 * @param binary バイナリ
	 * @return
	 */
	public static String toStr(byte[] binary) {
		
		if(binary.length == 0) {
			return "byte[0]";
		}
		
		StringBuffer result = new StringBuffer("byte[");
		result.append(binary.length);
		result.append("]:");
		for(int count = 0; count < 8 && count < binary.length; count++) {
			String hex = Integer.toHexString(((int)binary[count]) & 0xFF).toUpperCase();
			result.append("00".substring(hex.length()) + hex);
		}
		if(binary.length > 8) {
			result.append("...");
		}
		return result.toString();
	}
    
    //####################################################################
    //# ↓ 以下はJavelinLogUtilにも記述あり 修正する場合は統一すること↓ #
    //####################################################################
    private static final String   VALUE_HEADER        = "[";

    private static final String   VALUE_FOOTER        = "]";

    private static final String   KEY_VALUE_SEPARATOR = " = ";

    private static final String   VALUE_SEPARATOR     = " , ";

    private static final String   DEFAULT_VALUE       = "????";

    private static final String   NULL_VALUE          = "null";

    private static final String INNER_CLASS_SEPARATOR_CHAR = "$";

    //即出力対象とするクラス群
    private static final String[] PRINT_CLASS         = {"Short", "Integer",
            "Long", "String", "Boolean", "Byte", "Character", "Float", "Double"};

    /**
     * Objectの情報出力を行う
     * 入力された深度情報にあわせ、フィールドを辿るかその場で出力するか判定する
     * 
     * @param object       出力対象オブジェクト
     * @param detailDepth  出力深度
     * @param currentDepth 現在深度
     * @return             出力結果
     */
    public static String objectDetailPrinter(Object object, int detailDepth,
            int currentDepth)
    {

    	// Stringの場合はそのまま出力する。
        if(object instanceof String)
        {
            return (String)object;
        }

        if (object == null)
        {
            return NULL_VALUE;
        }
        
        // 先頭にヘッダ文字列を追加する。
        String detailString = toDetailString(object, detailDepth, currentDepth);
        if(currentDepth == 0)
        {
            StringBuffer detailBuffer = new StringBuffer();
        	detailBuffer.append(object.getClass().toString() + "@" + getObjectID(object)).append(":");
        	detailBuffer.append(detailString);
        	return detailBuffer.toString();
        }
        else
        {
        	return detailString;
        }
    }

	private static String toDetailString(Object object, int detailDepth, int currentDepth) {
		//現在の階層の深さが設定値以上の場合、toStringの結果を返す
        if (currentDepth >= detailDepth)
        {
        	return toStringPrinter(object);
        }

        //即出力対象となるオブジェクトの場合、toStringの結果を返す
        if (isPrintTarget(object))
        {
            return toStringPrinter(object);
        }

        Class clazz = object.getClass();
        //配列の場合、配列の各要素に対して判定を行う
        if (clazz.isArray())
        {
            return toStringArrayObject(object, detailDepth, currentDepth);
        }
        //コレクションの場合、配列に変換して判定を行う
        if (object instanceof Collection)
        {
            Collection collectionObject = (Collection)object;
            return toStringArrayObject(collectionObject.toArray(), detailDepth,
                                       currentDepth);
        }
        //Mapの場合は専用処理で出力を行う
        if (object instanceof Map)
        {
            Map mapObject = (Map)object;
            return toStringMapObject(mapObject, detailDepth, currentDepth);
        }

        Field[] fields = clazz.getDeclaredFields();
        try
        {
            AccessibleObject.setAccessible(fields, true);
        }
        catch (SecurityException scex)
        {
            return toStringPrinter(object);
        }

        StringBuffer buf = new StringBuffer();
        buf.append(VALUE_HEADER);
        boolean separatorFlag = false;
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            if (accept(field))
            {
                if (separatorFlag)
                {
                    buf.append(VALUE_SEPARATOR);
                }
                String fieldName = field.getName();
                buf.append(fieldName).append(KEY_VALUE_SEPARATOR);

                Object fieldValue = null;
                try
                {
                    fieldValue = field.get(object);
                    buf.append(objectDetailPrinter(fieldValue, detailDepth,
                                                   currentDepth + 1));

                }
                //エラーが発生した場合はデフォルト文字列とする
                catch (IllegalAccessException iaex)
                {
                    buf.append(DEFAULT_VALUE);
                }
                separatorFlag = true;
            }
        }
        buf.append(VALUE_FOOTER);
        return buf.toString();
	}

    /**
     * Mapの文字列出力を行う。
     * [key1 = value1 , key2 = value2 ..... keyn = valuen]の形式で出力する
     * 
     * @param mapObject    対象となるMap
     * @param detailDepth  出力深度
     * @param currentDepth 現在深度
     * @return             Mapの文字列表現
     */
    public static String toStringMapObject(Map mapObject, int detailDepth,
            int currentDepth)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(VALUE_HEADER);

        Object[] keys = mapObject.keySet().toArray();
        int length = keys.length;

        for (int i = 0; i < length; i++)
        {
            if (i > 0)
            {
                buf.append(VALUE_SEPARATOR);
            }
            
            Object item = mapObject.get(keys[i]);
            buf.append(toStringPrinter(keys[i])).append(KEY_VALUE_SEPARATOR);

            if (item == null)
            {
                buf.append(NULL_VALUE);
            }
            else
            {
                buf.append(objectDetailPrinter(item, detailDepth,
                                               currentDepth + 1));
            }
        }

        buf.append(VALUE_FOOTER);
        return buf.substring(0);
    }

    /**
     * 配列オブジェクトのログ出力を行う
     * 
     * @param array        出力対象（配列）
     * @param detailDepth  出力深度
     * @param currentDepth 現在深度
     * @return             出力文字列
     */
    public static String toStringArrayObject(Object array, int detailDepth,
            int currentDepth)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(VALUE_HEADER);
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++)
        {
            Object item = Array.get(array, i);
            if (i > 0)
            {
                buf.append(VALUE_SEPARATOR);
            }

            if (item == null)
            {
                buf.append(NULL_VALUE);
            }
            else
            {
                buf.append(objectDetailPrinter(item, detailDepth,
                                               currentDepth + 1));
            }
        }
        buf.append(VALUE_FOOTER);
        return buf.substring(0);
    }

    /**
     * ToStringの結果を返す
     * 
     * @param object 変換対象
     * @return       ToStringの結果
     */
    public static String toStringPrinter(Object object)
    {
        //toStringは例外を発生させることがあるため、発生時は
        //"????"という文字列を返すようにする。
        try
        {
            return object.toString();
        }
        catch (Throwable th)
        {
            return DEFAULT_VALUE;
        }
    }

    /**
     * fieldを出力するか、の判定結果を返す
     * 
     * @param field クラス中のフィールド
     * @return      出力するか否か
     */
    public static boolean accept(Field field)
    {
        if (field.getName().indexOf(INNER_CLASS_SEPARATOR_CHAR) != -1)
        {
            return false;
        }
        if (Modifier.isTransient(field.getModifiers()))
        {
            return false;
        }
        if (Modifier.isStatic(field.getModifiers()))
        {
            return false;
        }
        return true;
    }

    /**
     * 即出力対象のオブジェクトか判定を行う
     * Primitive型、もしくはそのラッパークラスが出力対象となる
     * 
     * @param object 判定対象
     * @return       判定結果
     */
    public static boolean isPrintTarget(Object object)
    {
        Class clazz = object.getClass();

        if (clazz.isPrimitive())
        {
            return true;
        }

        String className = clazz.getSimpleName();

        for (String target : PRINT_CLASS)
        {
            if (target.equals(className))
            {
                return true;
            }
        }

        return false;
    }
}
