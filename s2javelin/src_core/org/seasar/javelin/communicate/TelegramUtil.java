package org.seasar.javelin.communicate;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.seasar.javelin.MBeanManager;
import org.seasar.javelin.bean.Component;
import org.seasar.javelin.bean.Invocation;
import org.seasar.javelin.communicate.entity.Body;
import org.seasar.javelin.communicate.entity.Header;
import org.seasar.javelin.communicate.entity.RequestBody;
import org.seasar.javelin.communicate.entity.ResponseBody;
import org.seasar.javelin.communicate.entity.Telegram;

/**
 * 基本的な共通機能を提供する
 */
public final class TelegramUtil extends Common {
	private static final String CLASSMETHOD_SEPARATOR = "###CLASSMETHOD_SEPARATOR###";
	private static final int TELEGRAM_ITEM_COUNT = 12;



    /** shortからbyte配列への変換時に必要なバイト数 */
    private static final int SHORT_BYTE_SWITCH_LENGTH  = 2;

    /** intからbyte配列への変換時に必要なバイト数 */
    private static final int INT_BYTE_SWITCH_LENGTH    = 4;

    /** longからbyte配列への変換時に必要なバイト数 */
    private static final int LONG_BYTE_SWITCH_LENGTH   = 8;

    /** floatからbyte配列への変換時に必要なバイト数 */
    private static final int FLOAT_BYTE_SWITCH_LENGTH  = 4;

    /** doubleからbyte配列への変換時に必要なバイト数 */
    private static final int DOUBLE_BYTE_SWITCH_LENGTH = 8;

    /** ヘッダの長さ */
    private static final int TELEGRAM_HEADER_LENGTH = 6;
    private static final byte ITEMTYPE_BYTE = 0;
    private static final byte ITEMTYPE_INT16 = 1;
    private static final byte ITEMTYPE_INT32 = 2;
    private static final byte ITEMTYPE_INT64 = 3;
    private static final byte ITEMTYPE_FLOAT = 4;
    private static final byte ITEMTYPE_DOUBLE = 5;
    private static final byte ITEMTYPE_STRING = 6;


    /** 改行文字。 */
    public static final String NEW_LINE    = System.getProperty("line.separator");

    /**
     * 文字列をバイト配列（４バイト文字列長＋UTF8）に変換する。
     *
     * @param text 文字列
     * @return バイト配列
     */
    private static byte[] stringToByteArray(String text)
    {
        byte[] textArray = text.getBytes();
        byte[] lengthArray = intToByteArray(textArray.length);

        // 文字列長と文字列を結合する
        return combineTwoByteArray(lengthArray, textArray);
    }

    /**
     * 4バイトの文字列長＋UTF8のバイト配列から文字列を作成する。
     *
     * @param buffer バイト配列
     * @return 文字列
     */
    private static String byteArrayToString(ByteBuffer buffer)
    {
        String strResult = "";

        // 文字列長を取得する
        int intbyteArrLength = buffer.getInt();

        try
        {
            byte[] byteSoruceArr = new byte[intbyteArrLength];
            buffer.get(byteSoruceArr);
            strResult = new String(byteSoruceArr, 0, intbyteArrLength, "UTF-8");
        }
        catch (UnsupportedEncodingException uee)
        {
            // 何もしない
        }

        return strResult;
    }

    /**
     * ２バイト符号付整数をバイト配列に変換する。
     *
     * @param value 値
     * @return バイト配列
     */
    private static byte[] shortToByteArray(short value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(SHORT_BYTE_SWITCH_LENGTH);
        buffer.putShort(value);
        return buffer.array();
    }

    /**
     * ４バイト符号付整数をバイト配列に変換する。
     *
     * @param value 値
     * @return バイト配列
     */
    private static byte[] intToByteArray(int value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(INT_BYTE_SWITCH_LENGTH);
        buffer.putInt(value);
        return buffer.array();
    }

    /**
     * ８バイト符号付整数をバイト配列に変換する。
     *
     * @param value 値
     * @return バイト配列
     */
    private static byte[] longToByteArray(long value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(LONG_BYTE_SWITCH_LENGTH);
        buffer.putLong(value);
        return buffer.array();
    }

    /**
     * ４バイト符号付小数をバイト配列に変換する。
     *
     * @param value 値
     * @return バイト配列
     */
    private static byte[] floatToByteArray(float value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(FLOAT_BYTE_SWITCH_LENGTH);
        buffer.putFloat(value);
        return buffer.array();
    }

    /**
     * ８バイト符号付小数をバイト配列に変換する。
     *
     * @param value 値
     * @return バイト配列
     */
    private static byte[] doubleToByteArray(double value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(DOUBLE_BYTE_SWITCH_LENGTH);
        buffer.putDouble(value);
        return buffer.array();
    }

    /**
     * 電文オブジェクトをバイト配列に変換する。
     *
     * @param objTelegram 電文オブジェクト
     * @return バイト配列
     */
    public static byte[] createTelegram(Telegram objTelegram)
    {
        Header header = objTelegram.getObjHeader();

        // 本体データを作る
        byte[] bytesBody = null;

        if (objTelegram.getObjBody() != null)
        {
            for (Body body : objTelegram.getObjBody())
            {
                byte[] bytesObjName = stringToByteArray(body.getStrObjName());
                byte[] bytesItemName = stringToByteArray(body.getStrItemName());
                byte[] bytesBodyNames = combineTwoByteArray(bytesObjName, bytesItemName);
                // 本体データに設定する
                bytesBody = combineTwoByteArray(bytesBody, bytesBodyNames);

                // 項目型、ループ回数、値を変換する
                Body responseBody = body;
                byte itemType = responseBody.getByteItemMode();
                int loopCount = responseBody.getIntLoopCount();
                byte[] itemModeArray = new byte[]{itemType};
                byte[] loopCountArray = intToByteArray(loopCount);
                bytesBody = combineTwoByteArray(bytesBody, itemModeArray);
                bytesBody = combineTwoByteArray(bytesBody, loopCountArray);
                for (int index = 0; index < loopCount; index++)
                {
                    Object obj = responseBody.getObjItemValueArr()[index];
                    byte[] value = null;
                    if(obj == null)
                    {
                        continue;
                    }
                    switch (itemType)
                    {
                        case ITEMTYPE_BYTE:
                            value = new byte[]{ ((Byte)obj).byteValue() };
                            break;
                        case ITEMTYPE_INT16:
                            value = shortToByteArray((Short)obj);
                            break;
                        case ITEMTYPE_INT32:
                            value = intToByteArray((Integer)obj);
                            break;
                        case ITEMTYPE_INT64:
                            value = longToByteArray((Long)obj);
                            break;
                        case ITEMTYPE_FLOAT:
                            value = floatToByteArray((Float)obj);
                            break;
                        case ITEMTYPE_DOUBLE:
                            value = doubleToByteArray((Double)obj);
                            break;
                        case ITEMTYPE_STRING:
                            value = stringToByteArray((String)obj);
                            break;
                        default:
                            return null;
                    }
                    bytesBody = combineTwoByteArray(bytesBody, value);
                }
            }
        }

        int telegramLength = TELEGRAM_HEADER_LENGTH;
        if (bytesBody != null)
        {
            telegramLength += bytesBody.length;
        }

        ByteBuffer outputBuffer = ByteBuffer.allocate(telegramLength);

        // ヘッダを変換する
        outputBuffer.putInt(telegramLength);
        outputBuffer.put(header.getByteTelegramKind());
        outputBuffer.put(header.getByteRequestKind());

        if (bytesBody != null)
        {
            outputBuffer.put(bytesBody);
        }

        return outputBuffer.array();
    }

    /**
     * バイト配列を電文オブジェクトに変換する。
     *
     * @param byteTelegramArr バイト配列
     * @return 電文オブジェクト
     */
    public static Telegram recoveryTelegram(byte[] byteTelegramArr)
    {
        // 返却する用
        Telegram objTelegram = new Telegram();

        if (byteTelegramArr == null)
        {
            return null;
        }

        ByteBuffer telegramBuffer = ByteBuffer.wrap(byteTelegramArr);

        // まず、Header分を取得する
        Header objHeader = new Header();
        objHeader.setIntSize(telegramBuffer.getInt());
        objHeader.setByteTelegramKind(telegramBuffer.get());
        objHeader.setByteRequestKind(telegramBuffer.get());

        boolean isResponseBody = (objHeader.getByteRequestKind() == Common.BYTE_REQUEST_KIND_RESPONSE
                    || objHeader.getByteRequestKind() == Common.BYTE_REQUEST_KIND_NOTIFY);

        List<Body> bodyList = new ArrayList<Body>();

        // 本体を取得する
        while (telegramBuffer.remaining() > 0)
        {
            Body body;
            String objectName = byteArrayToString(telegramBuffer);
            String itemName = byteArrayToString(telegramBuffer);
            if (isResponseBody)
            {
                body = new ResponseBody();
            }
            else
            {
                body = new RequestBody();
            }


            // 項目型設定
            body.setByteItemMode(telegramBuffer.get());

            // 繰り返し回数設定
            body.setIntLoopCount(telegramBuffer.getInt());

            // 値設定
            Object[] values = new Object[body.getIntLoopCount()];
            for (int index = 0; index < values.length; index++)
            {
                switch (body.getByteItemMode())
                {
                    case ITEMTYPE_BYTE:
                        values[index] = telegramBuffer.get();
                        break;
                    case ITEMTYPE_INT16:
                        values[index] = telegramBuffer.getShort();
                        break;
                    case ITEMTYPE_INT32:
                        values[index] = telegramBuffer.getInt();
                        break;
                    case ITEMTYPE_INT64:
                        values[index] = telegramBuffer.getLong();
                        break;
                    case ITEMTYPE_FLOAT:
                        values[index] = telegramBuffer.getFloat();
                        break;
                    case ITEMTYPE_DOUBLE:
                        values[index] = telegramBuffer.getDouble();
                        break;
                    case ITEMTYPE_STRING:
                        values[index] = byteArrayToString(telegramBuffer);
                        break;
                    default:
                        return null;
                }
            }
            body.setObjItemValueArr(values);
            
            body.setStrObjName(objectName);
            body.setStrItemName(itemName);
            bodyList.add(body);
        }

        // 本体リストを作る
        Body[] objBodyArr;
        if (isResponseBody)
        {
            objBodyArr = bodyList.toArray(new ResponseBody[bodyList.size()]);
        }
        else
        {
            objBodyArr = bodyList.toArray(new Body[bodyList.size()]);
        }

        // 回復したヘッダと本体を電文に設定する
        objTelegram.setObjHeader(objHeader);
        objTelegram.setObjBody(objBodyArr);

        return objTelegram;
    }

    /**
     * 指定された種類の電文を作成する。
     *
     * @param telegramKind 電文種別
     * @param requestKind 要求応答種別
     * @param objectName オブジェクト名
     * @param itemName 項目名
     * @param itemType 項目型
     * @param value 値
     * @return 電文
     */
    public static Telegram createSingleTelegram(byte telegramKind, byte requestKind,
            String objectName, String itemName, byte itemType, Object value)
    {
        Header header = new Header();
        header.setByteTelegramKind(Common.BYTE_TELEGRAM_KIND_CONFIGCHANGE);
        header.setByteRequestKind(Common.BYTE_REQUEST_KIND_NOTIFY);

        ResponseBody responseBody = createSingleResponseBody(objectName, itemName, itemType, value);

        Telegram telegram = new Telegram();
        telegram.setObjHeader(header);
        telegram.setObjBody(new Body[]{responseBody});
        return telegram;
    }

    /**
     * 指定された種類の応答を作成する。
     *
     * @param objectName オブジェクト名
     * @param itemName 項目名
     * @param itemType 項目型
     * @param value 値
     * @return 応答
     */
    public static ResponseBody createSingleResponseBody(String objectName, String itemName,
            byte itemType, Object value)
    {
        ResponseBody responseBody = new ResponseBody();
        responseBody.setStrObjName(objectName);
        responseBody.setStrItemName(itemName);
        responseBody.setByteItemMode(itemType);
        Object[] itemValues;
        if (value instanceof List)
        {
            List valueList = (List)value;
            itemValues = new Object[valueList.size()];
            for (int index = 0; index < valueList.size(); index++)
            {
                itemValues[index] = valueList.get(index);
            }
        }
        else
        {
            itemValues = new Object[]{value};
        }            
        responseBody.setIntLoopCount(itemValues.length);
        responseBody.setObjItemValueArr(itemValues);
        return responseBody;
    }

    /**
     * ２つのバイト配列を結合する。
     *
     * @param bytesFirst 最初のバイト配列
     * @param bytesSecond 後ろにつなげるバイト配列
     * @return ２つのバイト配列をつなげたバイトは零つ
     */
    private static byte[] combineTwoByteArray(byte[] bytesFirst, byte[] bytesSecond)
    {
        // 返却用
        byte[] bytesResult = null;

        int byteBeforeArrLength = 0;
        int byteAfterArrLength = 0;

        // 前分　byte[]　のサイズを取得
        if (bytesFirst != null)
        {
            byteBeforeArrLength = bytesFirst.length;
        }

        // 後分　byte[]　のサイズを取得
        if (bytesSecond != null)
        {
            byteAfterArrLength = bytesSecond.length;
        }

        // 返却用　byte[]　を作る
        if (byteBeforeArrLength + byteAfterArrLength > 0)
        {
            bytesResult = new byte[byteBeforeArrLength + byteAfterArrLength];
        }

        // 前分　byte[]　を返却用　byte[]　に設定する
        if (byteBeforeArrLength > 0)
        {
            System.arraycopy(bytesFirst, 0, bytesResult, 0, byteBeforeArrLength);
        }

        // 後分　byte[]　を返却用　byte[]　に設定する
        if (byteAfterArrLength > 0)
        {
            System.arraycopy(bytesSecond, 0, bytesResult, byteBeforeArrLength, byteAfterArrLength);
        }

        // 返却する
        return bytesResult;
    }

	public static byte[] createAll() {
		// 電文データを取る
		Component[] objComponentArr = MBeanManager.getAllComponents();
		List invocationList = new ArrayList();

		// 電文数を統計する
		for (int i = 0; i < objComponentArr.length; i++) {
			invocationList.addAll(Arrays.asList((Object[]) objComponentArr[i]
					.getAllInvocation()));
		}

		Telegram objTelegram = TelegramUtil.create(invocationList,
				Common.BYTE_TELEGRAM_KIND_GET,
				Common.BYTE_REQUEST_KIND_RESPONSE);

		// 電文は、object ⇒ byte[] に変換する
		byte[] result = createTelegram(objTelegram);

		// 返却する
		return result;
	}

	static Telegram create(List invocations, byte telegramKind, byte requestKind) {
		// 電文頭部を作る【とりあえず、電文長を設定しない】
		Header objHeader = new Header();
		objHeader.setByteRequestKind(requestKind);
		objHeader.setByteTelegramKind(telegramKind);

		// 電文本体を作る
		ResponseBody[] bodies = new ResponseBody[invocations.size()
				* TELEGRAM_ITEM_COUNT];

		for (int index = 0; index < invocations.size(); index++) {
			Invocation invocation = (Invocation) invocations.get(index);

			// オブジェクト名を取得する
			StringBuffer strObjName = new StringBuffer();
			strObjName.append(invocation.getClassName());
			strObjName.append(CLASSMETHOD_SEPARATOR);
			strObjName.append(invocation.getMethodName());
			String objName = strObjName.toString();

			// 項目説明を置けるリスト
			Object[] objItemValueArr = null;
			int bodyIndex = index * TELEGRAM_ITEM_COUNT;
			// 呼び出し回数
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getCount());
			bodies[bodyIndex + 0] = createResponseBody(objName, "callCount",
					Common.BYTE_ITEMMODE_KIND_8BYTE_INT, objItemValueArr);

			// 平均時間
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getAverage());
			bodies[bodyIndex + 1] = createResponseBody(objName,
					"averageInterval", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
					objItemValueArr);

			// 最大処理時間
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getMaximum());
			bodies[bodyIndex + 2] = createResponseBody(objName,
					"maximumInterval", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
					objItemValueArr);

			// 最小処理時間
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getMinimum());
			bodies[bodyIndex + 3] = createResponseBody(objName,
					"minimumInterval", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
					objItemValueArr);

            // CPU平均時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getCpuAverage());
            bodies[bodyIndex + 4] = createResponseBody(objName,
                    "averageCpuInterval", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
                    objItemValueArr);

            // CPU最大処理時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getCpuMaximum());
            bodies[bodyIndex + 5] = createResponseBody(objName,
                    "maximumCpuInterval", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
                    objItemValueArr);

            // CPU最小処理時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getCpuMinimum());
            bodies[bodyIndex + 6] = createResponseBody(objName,
                    "minimumCpuInterval", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
                    objItemValueArr);
            
            // User平均時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getUserAverage());
            bodies[bodyIndex + 7] = createResponseBody(objName,
                    "averageUserInterval", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
                    objItemValueArr);

            // User最大処理時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getUserMaximum());
            bodies[bodyIndex + 8] = createResponseBody(objName,
                    "maximumUserInterval", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
                    objItemValueArr);

            // User最小処理時間
            objItemValueArr = new Long[1];
            objItemValueArr[0] = Long.valueOf(invocation.getUserMinimum());
            bodies[bodyIndex + 9] = createResponseBody(objName,
                    "minimumUserInterval", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
                    objItemValueArr);

            // 例外発生回数
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getThrowableCount());
			bodies[bodyIndex + 10] = createResponseBody(objName,
					"throwableCount", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
					objItemValueArr);

			// メソッドの呼び出し元 クラス名
            Invocation[] callerInvocations = invocation.getAllCallerInvocation();
            String[] callerNames = new String[callerInvocations.length];
            for (int callerIndex = 0; callerIndex < callerInvocations.length; callerIndex++)
            {
                callerNames[callerIndex] = callerInvocations[callerIndex].getClassName();
            }
			
			bodies[bodyIndex + 11] = createResponseBody(objName,
					"allCallerNames", Common.BYTE_ITEMMODE_KIND_STRING,
					callerNames);

		}

		// 電文オブジェクトを設定する
		Telegram objTelegram = new Telegram();
		objTelegram.setObjHeader(objHeader);
		objTelegram.setObjBody(bodies);
		return objTelegram;
	}

	/**
	 * ReponseBodyを作成する。
	 * 
	 * @param objName
	 * @param itemName
	 * @param itemMode
	 * @param objItemValueArr
	 * @return ReponseBody。
	 */
	public static ResponseBody createResponseBody(String objName,
			String itemName, byte itemMode, Object[] objItemValueArr) {
		ResponseBody body = new ResponseBody();
		body.setStrObjName(objName);
		body.setStrItemName(itemName);
		body.setByteItemMode(itemMode);
		body.setIntLoopCount(objItemValueArr.length);
		body.setObjItemValueArr(objItemValueArr);

		return body;
	}

	public static String toPrintStr(Telegram telegram)
    {
        StringBuffer receivedBuffer = new StringBuffer();
	    
        Header header = telegram.getObjHeader();
        byte telegramKind = header.getByteTelegramKind();
        byte requestKind = header.getByteRequestKind();
        int length = header.getIntSize();

        receivedBuffer.append(NEW_LINE);
        receivedBuffer.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        receivedBuffer.append(NEW_LINE);

        receivedBuffer.append("電文種別      :[" + telegramKind + "]");
        receivedBuffer.append(NEW_LINE);
        receivedBuffer.append("要求応答種別  :[" + requestKind + "]");
        receivedBuffer.append(NEW_LINE);
        receivedBuffer.append("電文長        :[" + length + "]");
        receivedBuffer.append(NEW_LINE);

        Body[] objBody = telegram.getObjBody();

        receivedBuffer.append("オブジェクト名\t項目名\t項目型\t繰り返し回数\t項目値");
        receivedBuffer.append(NEW_LINE);
        for (Body body : objBody)
        {
            String objName = body.getStrObjName();
            String itemName = body.getStrItemName();
            String itemMode = "";
            String loopCount = "";
            String itemValue = "";

            if (body instanceof ResponseBody)
            {
                ResponseBody responseBody = (ResponseBody)body;
                itemMode = "[" + responseBody.getByteItemMode() + "]";
                loopCount = "[" + responseBody.getIntLoopCount() + "]";

                Object[] objArr = responseBody.getObjItemValueArr();
                for (Object obj : objArr)
                {
                    itemValue += "[" + obj + "]";
                }
            }
            receivedBuffer.append(objName);
            receivedBuffer.append("\t\t");
            receivedBuffer.append(itemName);
            receivedBuffer.append("\t");
            receivedBuffer.append(itemMode);
            receivedBuffer.append("\t");
            receivedBuffer.append(loopCount);
            receivedBuffer.append("\t");
            receivedBuffer.append(itemValue);
            receivedBuffer.append(NEW_LINE);
        }

        receivedBuffer.append("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        receivedBuffer.append(NEW_LINE);

        String receivedStr = receivedBuffer.toString();
        
        return receivedStr;
    }
}
