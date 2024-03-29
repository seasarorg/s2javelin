package org.seasar.javelin.bottleneckeye.communicate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 基本的な共通機能を提供する
 */
public final class TelegramUtil extends Common
{

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

    /** 電文長の最大。 */
    public static final int TELEGRAM_MAX = 100 * 1024 * 1024;

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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ByteBuffer headerBuffer = ByteBuffer.allocate(TELEGRAM_HEADER_LENGTH);
        headerBuffer.putInt(0);
        headerBuffer.put(header.getByteTelegramKind());
        headerBuffer.put(header.getByteRequestKind());
        try
        {
            byteArrayOutputStream.write(headerBuffer.array());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        
        if (objTelegram.getObjBody() != null)
        {
            for (Body body : objTelegram.getObjBody())
            {
                if(byteArrayOutputStream.size() > TELEGRAM_MAX)
                {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    break;
                }
                
                try
                {
                    byte[] bytesObjName = stringToByteArray(body.getStrObjName());
                    byte[] bytesItemName = stringToByteArray(body.getStrItemName());
                    // 本体データに設定する
                    byteArrayOutputStream.write(bytesObjName);
                    byteArrayOutputStream.write(bytesItemName);

                    Body responseBody = body;
                    byte itemType = responseBody.getByteItemMode();
                    int loopCount = responseBody.getIntLoopCount();
                    byte[] itemModeArray = new byte[]{itemType};
                    byte[] loopCountArray = intToByteArray(loopCount);
                    byteArrayOutputStream.write(itemModeArray);
                    byteArrayOutputStream.write(loopCountArray);

                    for (int index = 0; index < loopCount; index++)
                    {
                        Object obj = responseBody.getObjItemValueArr()[index];
                        byte[] value = null;
                        switch (itemType)
                        {
                        case Body.ITEMTYPE_BYTE:
                            value = new byte[]{((Byte)obj).byteValue()};
                            break;
                        case Body.ITEMTYPE_INT16:
                            value = shortToByteArray((Short)obj);
                            break;
                        case Body.ITEMTYPE_INT32:
                            value = intToByteArray((Integer)obj);
                            break;
                        case Body.ITEMTYPE_INT64:
                            value = longToByteArray((Long)obj);
                            break;
                        case Body.ITEMTYPE_FLOAT:
                            value = floatToByteArray((Float)obj);
                            break;
                        case Body.ITEMTYPE_DOUBLE:
                            value = doubleToByteArray((Double)obj);
                            break;
                        case Body.ITEMTYPE_STRING:
                            value = stringToByteArray((String)obj);
                            break;
                        default:
                            return null;
                        }
                        byteArrayOutputStream.write(value);
                    }
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    continue;
                }
            }
        }

        byte[] bytesBody = byteArrayOutputStream.toByteArray();
        int telegramLength = bytesBody.length;
        ByteBuffer outputBuffer = ByteBuffer.wrap(bytesBody);

        // ヘッダを変換する
        outputBuffer.rewind();
        outputBuffer.putInt(telegramLength);
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
                    case Body.ITEMTYPE_BYTE:
                        values[index] = telegramBuffer.get();
                        break;
                    case Body.ITEMTYPE_INT16:
                        values[index] = telegramBuffer.getShort();
                        break;
                    case Body.ITEMTYPE_INT32:
                        values[index] = telegramBuffer.getInt();
                        break;
                    case Body.ITEMTYPE_INT64:
                        values[index] = telegramBuffer.getLong();
                        break;
                    case Body.ITEMTYPE_FLOAT:
                        values[index] = telegramBuffer.getFloat();
                        break;
                    case Body.ITEMTYPE_DOUBLE:
                        values[index] = telegramBuffer.getDouble();
                        break;
                    case Body.ITEMTYPE_STRING:
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
        header.setByteTelegramKind(telegramKind);
        header.setByteRequestKind(requestKind);

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

}
