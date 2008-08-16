package org.seasar.javelin.bottleneckeye.communicate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * ��{�I�ȋ��ʋ@�\��񋟂���
 */
public final class TelegramUtil extends Common
{

    /** short����byte�z��ւ̕ϊ����ɕK�v�ȃo�C�g�� */
    private static final int SHORT_BYTE_SWITCH_LENGTH  = 2;

    /** int����byte�z��ւ̕ϊ����ɕK�v�ȃo�C�g�� */
    private static final int INT_BYTE_SWITCH_LENGTH    = 4;

    /** long����byte�z��ւ̕ϊ����ɕK�v�ȃo�C�g�� */
    private static final int LONG_BYTE_SWITCH_LENGTH   = 8;

    /** float����byte�z��ւ̕ϊ����ɕK�v�ȃo�C�g�� */
    private static final int FLOAT_BYTE_SWITCH_LENGTH  = 4;

    /** double����byte�z��ւ̕ϊ����ɕK�v�ȃo�C�g�� */
    private static final int DOUBLE_BYTE_SWITCH_LENGTH = 8;

    /** �w�b�_�̒��� */
    private static final int TELEGRAM_HEADER_LENGTH = 6;

    /** �d�����̍ő�B */
    public static final int TELEGRAM_MAX = 100 * 1024 * 1024;

    /**
     * ��������o�C�g�z��i�S�o�C�g�����񒷁{UTF8�j�ɕϊ�����B
     *
     * @param text ������
     * @return �o�C�g�z��
     */
    private static byte[] stringToByteArray(String text)
    {
        byte[] textArray = text.getBytes();
        byte[] lengthArray = intToByteArray(textArray.length);

        // �����񒷂ƕ��������������
        return combineTwoByteArray(lengthArray, textArray);
    }

    /**
     * 4�o�C�g�̕����񒷁{UTF8�̃o�C�g�z�񂩂當������쐬����B
     *
     * @param buffer �o�C�g�z��
     * @return ������
     */
    private static String byteArrayToString(ByteBuffer buffer)
    {
        String strResult = "";

        // �����񒷂��擾����
        int intbyteArrLength = buffer.getInt();

        try
        {
            byte[] byteSoruceArr = new byte[intbyteArrLength];
            buffer.get(byteSoruceArr);
            strResult = new String(byteSoruceArr, 0, intbyteArrLength, "UTF-8");
        }
        catch (UnsupportedEncodingException uee)
        {
            // �������Ȃ�
        }

        return strResult;
    }

    /**
     * �Q�o�C�g�����t�������o�C�g�z��ɕϊ�����B
     *
     * @param value �l
     * @return �o�C�g�z��
     */
    private static byte[] shortToByteArray(short value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(SHORT_BYTE_SWITCH_LENGTH);
        buffer.putShort(value);
        return buffer.array();
    }

    /**
     * �S�o�C�g�����t�������o�C�g�z��ɕϊ�����B
     *
     * @param value �l
     * @return �o�C�g�z��
     */
    private static byte[] intToByteArray(int value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(INT_BYTE_SWITCH_LENGTH);
        buffer.putInt(value);
        return buffer.array();
    }

    /**
     * �W�o�C�g�����t�������o�C�g�z��ɕϊ�����B
     *
     * @param value �l
     * @return �o�C�g�z��
     */
    private static byte[] longToByteArray(long value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(LONG_BYTE_SWITCH_LENGTH);
        buffer.putLong(value);
        return buffer.array();
    }

    /**
     * �S�o�C�g�����t�������o�C�g�z��ɕϊ�����B
     *
     * @param value �l
     * @return �o�C�g�z��
     */
    private static byte[] floatToByteArray(float value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(FLOAT_BYTE_SWITCH_LENGTH);
        buffer.putFloat(value);
        return buffer.array();
    }

    /**
     * �W�o�C�g�����t�������o�C�g�z��ɕϊ�����B
     *
     * @param value �l
     * @return �o�C�g�z��
     */
    private static byte[] doubleToByteArray(double value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(DOUBLE_BYTE_SWITCH_LENGTH);
        buffer.putDouble(value);
        return buffer.array();
    }

    /**
     * �d���I�u�W�F�N�g���o�C�g�z��ɕϊ�����B
     *
     * @param objTelegram �d���I�u�W�F�N�g
     * @return �o�C�g�z��
     */
    public static byte[] createTelegram(Telegram objTelegram)
    {
        Header header = objTelegram.getObjHeader();

        // �{�̃f�[�^�����
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
                    // �{�̃f�[�^�ɐݒ肷��
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

        // �w�b�_��ϊ�����
        outputBuffer.rewind();
        outputBuffer.putInt(telegramLength);
        return outputBuffer.array();
    }
    
    /**
     * �o�C�g�z���d���I�u�W�F�N�g�ɕϊ�����B
     *
     * @param byteTelegramArr �o�C�g�z��
     * @return �d���I�u�W�F�N�g
     */
    public static Telegram recoveryTelegram(byte[] byteTelegramArr)
    {
        // �ԋp����p
        Telegram objTelegram = new Telegram();

        if (byteTelegramArr == null)
        {
            return null;
        }

        ByteBuffer telegramBuffer = ByteBuffer.wrap(byteTelegramArr);

        // �܂��AHeader�����擾����
        Header objHeader = new Header();
        objHeader.setIntSize(telegramBuffer.getInt());
        objHeader.setByteTelegramKind(telegramBuffer.get());
        objHeader.setByteRequestKind(telegramBuffer.get());

        boolean isResponseBody = (objHeader.getByteRequestKind() == Common.BYTE_REQUEST_KIND_RESPONSE
                    || objHeader.getByteRequestKind() == Common.BYTE_REQUEST_KIND_NOTIFY);

        List<Body> bodyList = new ArrayList<Body>();

        // �{�̂��擾����
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


            // ���ڌ^�ݒ�
            body.setByteItemMode(telegramBuffer.get());

            // �J��Ԃ��񐔐ݒ�
            body.setIntLoopCount(telegramBuffer.getInt());

            // �l�ݒ�
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

        // �{�̃��X�g�����
        Body[] objBodyArr;
        if (isResponseBody)
        {
            objBodyArr = bodyList.toArray(new ResponseBody[bodyList.size()]);
        }
        else
        {
            objBodyArr = bodyList.toArray(new Body[bodyList.size()]);
        }

        // �񕜂����w�b�_�Ɩ{�̂�d���ɐݒ肷��
        objTelegram.setObjHeader(objHeader);
        objTelegram.setObjBody(objBodyArr);

        return objTelegram;
    }

    /**
     * �w�肳�ꂽ��ނ̓d�����쐬����B
     *
     * @param telegramKind �d�����
     * @param requestKind �v���������
     * @param objectName �I�u�W�F�N�g��
     * @param itemName ���ږ�
     * @param itemType ���ڌ^
     * @param value �l
     * @return �d��
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
     * �w�肳�ꂽ��ނ̉������쐬����B
     *
     * @param objectName �I�u�W�F�N�g��
     * @param itemName ���ږ�
     * @param itemType ���ڌ^
     * @param value �l
     * @return ����
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
     * �Q�̃o�C�g�z�����������B
     *
     * @param bytesFirst �ŏ��̃o�C�g�z��
     * @param bytesSecond ���ɂȂ���o�C�g�z��
     * @return �Q�̃o�C�g�z����Ȃ����o�C�g�͗��
     */
    private static byte[] combineTwoByteArray(byte[] bytesFirst, byte[] bytesSecond)
    {
        // �ԋp�p
        byte[] bytesResult = null;

        int byteBeforeArrLength = 0;
        int byteAfterArrLength = 0;

        // �O���@byte[]�@�̃T�C�Y���擾
        if (bytesFirst != null)
        {
            byteBeforeArrLength = bytesFirst.length;
        }

        // �㕪�@byte[]�@�̃T�C�Y���擾
        if (bytesSecond != null)
        {
            byteAfterArrLength = bytesSecond.length;
        }

        // �ԋp�p�@byte[]�@�����
        if (byteBeforeArrLength + byteAfterArrLength > 0)
        {
            bytesResult = new byte[byteBeforeArrLength + byteAfterArrLength];
        }

        // �O���@byte[]�@��ԋp�p�@byte[]�@�ɐݒ肷��
        if (byteBeforeArrLength > 0)
        {
            System.arraycopy(bytesFirst, 0, bytesResult, 0, byteBeforeArrLength);
        }

        // �㕪�@byte[]�@��ԋp�p�@byte[]�@�ɐݒ肷��
        if (byteAfterArrLength > 0)
        {
            System.arraycopy(bytesSecond, 0, bytesResult, byteBeforeArrLength, byteAfterArrLength);
        }

        // �ԋp����
        return bytesResult;
    }

}
