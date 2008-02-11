package org.seasar.javelin.communicate;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.seasar.javelin.MBeanManager;
import org.seasar.javelin.bean.Component;
import org.seasar.javelin.bean.Invocation;
import org.seasar.javelin.bottleneckeye.communicate.Common;
import org.seasar.javelin.communicate.entity.Body;
import org.seasar.javelin.communicate.entity.Header;
import org.seasar.javelin.communicate.entity.ResponseBody;
import org.seasar.javelin.communicate.entity.Telegram;

/**
 * ��{�I�ȋ��ʋ@�\��񋟂���
 */
public final class TelegramUtil extends Common {
	private static final String CLASSMETHOD_SEPARATOR = "###CLASSMETHOD_SEPARATOR###";
	private static final int TELEGRAM_ITEM_COUNT = 6;



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
    private static final byte ITEMTYPE_BYTE = 0;
    private static final byte ITEMTYPE_INT16 = 1;
    private static final byte ITEMTYPE_INT32 = 2;
    private static final byte ITEMTYPE_INT64 = 3;
    private static final byte ITEMTYPE_FLOAT = 4;
    private static final byte ITEMTYPE_DOUBLE = 5;
    private static final byte ITEMTYPE_STRING = 6;


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
        byte[] bytesBody = null;

        if (objTelegram.getObjBody() != null)
        {
            for (Body body : objTelegram.getObjBody())
            {
                byte[] bytesObjName = stringToByteArray(body.getStrObjName());
                byte[] bytesItemName = stringToByteArray(body.getStrItemName());
                byte[] bytesBodyNames = combineTwoByteArray(bytesObjName, bytesItemName);
                // �{�̃f�[�^�ɐݒ肷��
                bytesBody = combineTwoByteArray(bytesBody, bytesBodyNames);

                if (header.getByteRequestKind() == Common.BYTE_REQUEST_KIND_RESPONSE)
                {
                    // �����f�[�^�Ȃ�A���ڌ^�A���[�v�񐔁A�l��ϊ�����
                    ResponseBody responseBody = (ResponseBody)body;
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
        }

        int telegramLength = TELEGRAM_HEADER_LENGTH;
        if (bytesBody != null)
        {
            telegramLength += bytesBody.length;
        }

        ByteBuffer outputBuffer = ByteBuffer.allocate(telegramLength);

        // �w�b�_��ϊ�����
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

        List<Body> bodyList = new ArrayList<Body>();

        // �{�̂��擾����
        while (telegramBuffer.remaining() > 0)
        {
            // ��{�̑Ώۂ����
            ResponseBody body = new ResponseBody();

            // �I�u�W�F�N�g���ݒ�
            body.setStrObjName(byteArrayToString(telegramBuffer));

            // ���ږ��ݒ�
            body.setStrItemName(byteArrayToString(telegramBuffer));

            if (objHeader.getByteRequestKind() == Common.BYTE_REQUEST_KIND_RESPONSE
                    || objHeader.getByteRequestKind() == Common.BYTE_REQUEST_KIND_NOTIFY)
            {
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
                bodyList.add(body);
            }
        }

        // �{�̃��X�g�����
        Body[] objBodyArr = bodyList.toArray(new Body[0]);

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
        header.setByteTelegramKind(Common.BYTE_TELEGRAM_KIND_CONFIGCHANGE);
        header.setByteRequestKind(Common.BYTE_REQUEST_KIND_NOTIFY);

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
        responseBody.setIntLoopCount(1);
        responseBody.setObjItemValueArr(new Object[]{value});
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

	/**
	 * �d�����o�͂���
	 */
	public static String printTelegram(int intCounter, Telegram objInputTelegram) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n***** " + intCounter + "�Ԗړd���J�n *****\n");
		buffer.append("#�w�b�_�d���� : "
				+ objInputTelegram.getObjHeader().getIntSize());
		buffer.append("\n");
		buffer.append("#�w�b�_�d����� : "
				+ objInputTelegram.getObjHeader().getByteTelegramKind());
		buffer.append("\n");
		buffer.append("#�w�b�_������� : "
				+ objInputTelegram.getObjHeader().getByteRequestKind());
		buffer.append("\n");

		for (int i = 0; i < objInputTelegram.getObjBody().length; i++) {
			buffer.append("------------------");
			buffer.append("\n");
			buffer.append("--�{��["
					+ i
					+ "]�Ώۖ��@ : "
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getStrObjName());
			buffer.append("\n");
			buffer.append("--�{��["
					+ i
					+ "]���ږ��@ : "
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getStrItemName());
			buffer.append("\n");
			buffer.append("--�{��["
					+ i
					+ "]���ڌ^�@ : "
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getByteItemMode());
			buffer.append("\n");
			buffer.append("--�{��["
					+ i
					+ "]�J�ԉ� : "
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getIntLoopCount());
			buffer.append("\n");
			Object[] objArr = ((ResponseBody) objInputTelegram.getObjBody()[i])
					.getObjItemValueArr();
			for (int j = 0; j < objArr.length; j++) {
				buffer.append("--�{��[" + i + "]�����@�@ : [" + j + "]" + objArr[j]);
				buffer.append("\n");
			}
		}
		buffer.append("***** " + intCounter + "�Ԗړd���I�� *****");
		buffer.append("\n");

		return buffer.toString();
	}

	public static byte[] createAll() {
		// �d���f�[�^�����
		Component[] objComponentArr = MBeanManager.getAllComponents();
		List invocationList = new ArrayList();

		// �d�����𓝌v����
		for (int i = 0; i < objComponentArr.length; i++) {
			invocationList.addAll(Arrays.asList((Object[]) objComponentArr[i]
					.getAllInvocation()));
		}

		Telegram objTelegram = TelegramUtil.create(invocationList,
				Common.BYTE_TELEGRAM_KIND_GET,
				Common.BYTE_REQUEST_KIND_RESPONSE);

		// �d���́Aobject �� byte[] �ɕϊ�����
		byte[] result = createTelegram(objTelegram);

		// �ԋp����
		return result;
	}

	static Telegram create(List invocations, byte telegramKind, byte requestKind) {
		// �d�����������y�Ƃ肠�����A�d������ݒ肵�Ȃ��z
		Header objHeader = new Header();
		objHeader.setByteRequestKind(requestKind);
		objHeader.setByteTelegramKind(telegramKind);

		// �d���{�̂����
		ResponseBody[] bodies = new ResponseBody[invocations.size()
				* TELEGRAM_ITEM_COUNT];

		for (int index = 0; index < invocations.size(); index++) {
			Invocation invocation = (Invocation) invocations.get(index);

			// �I�u�W�F�N�g�����擾����
			StringBuffer strObjName = new StringBuffer();
			strObjName.append(invocation.getClassName());
			strObjName.append(CLASSMETHOD_SEPARATOR);
			strObjName.append(invocation.getMethodName());
			String objName = strObjName.toString();

			// ���ڐ�����u���郊�X�g
			Object[] objItemValueArr = null;
			int bodyIndex = index * TELEGRAM_ITEM_COUNT;
			// �Ăяo����
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getCount());
			bodies[bodyIndex + 0] = createResponseBody(objName, "callCount",
					Common.BYTE_ITEMMODE_KIND_8BYTE_INT, objItemValueArr);

			// ���ώ���
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getAverage());
			bodies[bodyIndex + 1] = createResponseBody(objName,
					"averageInterval", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
					objItemValueArr);

			// �ő又������
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getMaximum());
			bodies[bodyIndex + 2] = createResponseBody(objName,
					"maximumInterval", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
					objItemValueArr);

			// �ŏ���������
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getMinimum());
			bodies[bodyIndex + 3] = createResponseBody(objName,
					"minimumInterval", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
					objItemValueArr);

			// ��O������
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getThrowableCount());
			bodies[bodyIndex + 4] = createResponseBody(objName,
					"throwableCount", Common.BYTE_ITEMMODE_KIND_8BYTE_INT,
					objItemValueArr);

			// ���\�b�h�̌Ăяo���� �N���X��
			objItemValueArr = invocation.getAllCallerInvocation();
			bodies[bodyIndex + 5] = createResponseBody(objName,
					"allCallerNames", Common.BYTE_ITEMMODE_KIND_STRING,
					objItemValueArr);

		}

		// �d���I�u�W�F�N�g��ݒ肷��
		Telegram objTelegram = new Telegram();
		objTelegram.setObjHeader(objHeader);
		objTelegram.setObjBody(bodies);
		return objTelegram;
	}

	private static ResponseBody createResponseBody(String objName,
			String itemName, byte itemMode, Object[] objItemValueArr) {
		ResponseBody body = new ResponseBody();
		body.setStrObjName(objName);
		body.setStrItemName(itemName);
		body.setByteItemMode(itemMode);
		body.setIntLoopCount(objItemValueArr.length);
		body.setObjItemValueArr(objItemValueArr);

		return body;
	}

	public static Telegram createJvnLogTelegram(byte telegramKind, String jvnFileName,
			String jvnFileContent) {
		// �d���w�b�_�����
		Header objHeader = new Header();
		objHeader.setByteRequestKind(Common.BYTE_TELEGRAM_KIND_JVN_FILE);
		objHeader.setByteTelegramKind(telegramKind);

		// �d���{�̂����
		ResponseBody bodyJvnFileName = createResponseBody("jvnFile",
				"jvnFileName", Common.BYTE_ITEMMODE_KIND_STRING,
				new String[] { jvnFileName });

		ResponseBody bodyJvnFileContent = createResponseBody("jvnFile",
				"jvnFileContent", Common.BYTE_ITEMMODE_KIND_STRING,
				new String[] { jvnFileContent });

		// �d���I�u�W�F�N�g��ݒ肷��
		Telegram objTelegram = new Telegram();
		objTelegram.setObjHeader(objHeader);
		objTelegram.setObjBody(new ResponseBody[] { // 
				bodyJvnFileName, bodyJvnFileContent //
				});

		return objTelegram;
	}
}