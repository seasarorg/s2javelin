package org.seasar.javelin.bottleneckeye.communicate;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * ��{�I�ȋ��ʋ@�\��񋟂���
 */
public final class TelegramUtil extends Common {
	/**
	 * byte[] �̑O�S�ʂ��擾���āAint �ɓ]������
	 */
	public static int arrHead4ToInt(byte[] byteSoruceArr) {
		// �ԋp����p
		int intResult = 0;

		// byte[] �̑O�S�ʂ��擾����
		byte[] byteHeaderLengthArr = new byte[INT_BYTE_SWITCH_LENGTH];
		for (int i = 0; i < byteHeaderLengthArr.length; i++) {
			byteHeaderLengthArr[i] = byteSoruceArr[i];
		}

		// �ϊ��N���X���g��
		ByteBuffer objByteBuffer = ByteBuffer.allocate(INT_BYTE_SWITCH_LENGTH);
		objByteBuffer = objByteBuffer.put(byteHeaderLengthArr);

		// int �ɓ]������
		intResult = objByteBuffer.getInt(0);

		// �ԋp����
		return intResult;
	}

	/**
	 * byte[] �̑O�W�ʂ��擾���āAlong �ɓ]������
	 */
	public static long arrHead8Tolong(byte[] byteSoruceArr) {
		// �ԋp����p
		long lngResult = 0;

		// byte[] �̑O�W�ʂ��擾����
		byte[] byteTempArr = new byte[LONG_BYTE_SWITCH_LENGTH];
		for (int i = 0; i < byteTempArr.length; i++) {
			byteTempArr[i] = byteSoruceArr[i];
		}

		// �ϊ��N���X���g��
		ByteBuffer objByteBuffer = ByteBuffer.allocate(LONG_BYTE_SWITCH_LENGTH);
		objByteBuffer = objByteBuffer.put(byteTempArr);

		// long �ɓ]������
		lngResult = objByteBuffer.getLong(0);

		// �ԋp����
		return lngResult;
	}

	/**
	 * String �� byte[size,data] �ɓ]������
	 */
	public static byte[] strToByteArr(String strBodyData) {
		// String �� byte[data] �ɓ]������
		byte[] byteBodyDataArr = strBodyData.getBytes();

		// �ϊ��N���X���g���āAString.length �� byte[size] �ɓ]������
		ByteBuffer objByteBuffer = ByteBuffer.allocate(INT_BYTE_SWITCH_LENGTH);
		objByteBuffer = objByteBuffer.putInt(byteBodyDataArr.length);

		// byte[size] �ɓ]������
		byte[] byteBodyDataLengthArr = objByteBuffer.array();

		// byte[size] �� byte[data] ��A�ڂ���
		byteBodyDataArr = arrayAdd(byteBodyDataLengthArr, byteBodyDataArr);

		// �ԋp����
		return byteBodyDataArr;
	}

	/**
	 * byte[size,data] �� String �ɓ]������
	 */
	public static String byteArrToStr(ByteBuffer buffer) {
		// �ԋp����p
		String strResult = "";

		// size ���擾����
		int intbyteArrLength = buffer.getInt();

		try {
			byte[] byteSoruceArr = new byte[intbyteArrLength];
			buffer.get(byteSoruceArr);

			// byte[data] �� String �ɕϊ�����
			strResult = new String(byteSoruceArr, 0, intbyteArrLength, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			return "";
		}

		// �ԋp����
		return strResult;
	}

	/**
	 * byte[size,data] �� String �ɓ]������
	 */
	public static String byteArrToStr(byte[] byteSoruceArr) {
		// �ԋp����p
		String strResult = "";

		// size ���擾����
		int intbyteArrLength = arrHead4ToInt(byteSoruceArr);

		try {
			// byte[data] �� String �ɕϊ�����
			strResult = new String(byteSoruceArr, INT_BYTE_SWITCH_LENGTH,
					intbyteArrLength, "UTF-8");
		} catch (UnsupportedEncodingException objUnsupportedEncodingException) {
			// �G���[���b�Z�[�W���o��
			objUnsupportedEncodingException.printStackTrace();
		}

		// �ԋp����
		return strResult;
	}

	/**
	 * �d���́Aobject �� byte[] �ɕϊ�����
	 */
	public static byte[] createTelegram(Telegram objTelegram) {
		// �ԋp�p
		byte[] byteOutputArr = null;

		// �����f�[�^���o�͗��ɐݒ肷��
		byte[] byteHeadArr = new byte[6];
		byteHeadArr[TelegramUtil.INT_BYTE_SWITCH_LENGTH] = objTelegram
				.getObjHeader().getByteTelegramKind();
		byteHeadArr[TelegramUtil.INT_BYTE_SWITCH_LENGTH + 1] = objTelegram
				.getObjHeader().getByteRequestKind();

		// �{�̃f�[�^�����
		byte[] byteBodyArr = null;

		if (objTelegram.getObjBody() != null) {
			for (int i = 0; i < objTelegram.getObjBody().length; i++) {
				// �I�u�W�F�N�g�� �� byte[size,data] �ɓ]������
				byte[] byteBodyObjNameArr = TelegramUtil
						.strToByteArr(((RequestBody) objTelegram.getObjBody()[i])
								.getStrObjName());
				// ���ږ� �� byte[size,data] �ɓ]������
				byte[] byteBodyItemNameArr = TelegramUtil
						.strToByteArr(((RequestBody) objTelegram.getObjBody()[i])
								.getStrItemName());
				// �I�u�W�F�N�g���ƍ��ږ���A�ڂ���
				byte[] byteBodyTemp = TelegramUtil.arrayAdd(byteBodyObjNameArr,
						byteBodyItemNameArr);
				// �{�̃f�[�^�ɐݒ肷��
				byteBodyArr = TelegramUtil.arrayAdd(byteBodyArr, byteBodyTemp);
			}
		}

		// �����Ɩ{�̂��o�͗��ɐݒ肷��
		byteOutputArr = TelegramUtil.arrayAdd(byteHeadArr, byteBodyArr);

		// �ϊ��N���X���g��
		ByteBuffer objByteBuffer = ByteBuffer.allocate(INT_BYTE_SWITCH_LENGTH);
		objByteBuffer.putInt(byteOutputArr.length);

		// �d�����T�C�Y���擾���āA�o�͗��̍ŏ��Ƃ���ɐݒ肷��
		byte[] byteHeadSizeArr = objByteBuffer.array();
		for (int i = 0; i < byteHeadSizeArr.length; i++) {
			byteOutputArr[i] = byteHeadSizeArr[i];
		}

		// �ԋp����
		return byteOutputArr;
	}

	/**
	 * �d���́Abyte[] �� object �ɉ񕜂���
	 */
	public static Telegram recovryTelegram(byte[] byteTelegramArr) {
		// �ԋp����p
		Telegram objTelegram = new Telegram();

		if (byteTelegramArr == null)
			return null;

		ByteBuffer telegramBuffer = ByteBuffer.wrap(byteTelegramArr);

		// �܂��AHeader�����擾����
		Header objHeader = new Header();
		objHeader.setIntSize(telegramBuffer.getInt());
		objHeader.setByteTelegramKind(telegramBuffer.get());
		objHeader.setByteRequestKind(telegramBuffer.get());

		List bodyList = new ArrayList();

		// �{�̂��擾����
		while (telegramBuffer.remaining() > 0) {
			// ��{�̑Ώۂ����
			ResponseBody body = new ResponseBody();

			// �I�u�W�F�N�g���ݒ�
			body.setStrObjName(byteArrToStr(telegramBuffer));

			// ���ږ��ݒ�
			body.setStrItemName(byteArrToStr(telegramBuffer));

			// ���ڌ^�ݒ�
			body.setByteItemMode(telegramBuffer.get());

			// �J��Ԃ��񐔐ݒ�
			body.setIntLoopCount(telegramBuffer.getInt());

			// �����ݒ�
			Object[] values = new Object[body.getIntLoopCount()];
			for (int index = 0; index < values.length; index++) {
				if (body.getByteItemMode() == BYTE_ITEMMODE_KIND_STRING) {
					// ���ڌ^��BYTE_ITEMMODE_KIND_STRING�̏ꍇ�AString[] ���擾����
					values[index] = byteArrToStr(telegramBuffer);
				} else {
					// ���ڌ^��BYTE_ITEMMODE_KIND_STRING�ȊO�̏ꍇ�ALong ���擾����
					values[index] = Long.valueOf(telegramBuffer.getLong());
				}
			}

			body.setObjItemValueArr(values);

			bodyList.add(body);
		}

		// �{�̃��X�g�����
		ResponseBody[] objResponseBodyArr = (ResponseBody[]) bodyList
				.toArray(new ResponseBody[0]);

		// �񕜂��������Ɩ{�̂�d���ɐݒ肷��
		objTelegram.setObjHeader(objHeader);
		objTelegram.setObjBody(objResponseBodyArr);

		// �ԋp����
		return objTelegram;
	}

	/**
	 * �d�����o�͂���
	 */
	public static void printTelegram(int intCounter, Telegram objInputTelegram) {
		System.out.println("\n���������� " + intCounter + "�Ԗړd���J�n ����������");
		System.out.println("�������d�����x�F"
				+ objInputTelegram.getObjHeader().getIntSize());
		System.out.println("�������d����ʁF"
				+ objInputTelegram.getObjHeader().getByteTelegramKind());
		System.out.println("������������ʁF"
				+ objInputTelegram.getObjHeader().getByteRequestKind());

		for (int i = 0; i < objInputTelegram.getObjBody().length; i++) {
			System.out.println("------------------");
			System.out.println("���{��["
					+ i
					+ "]�Ώۖ��@�F"
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getStrObjName());
			System.out.println("���{��["
					+ i
					+ "]���ږ��@�F"
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getStrItemName());
			System.out.println("���{��["
					+ i
					+ "]���ڌ^�@�F"
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getByteItemMode());
			System.out.println("���{��["
					+ i
					+ "]�J�ԉ񐔁F"
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getIntLoopCount());
			Object[] objArr = ((ResponseBody) objInputTelegram.getObjBody()[i])
					.getObjItemValueArr();
			for (int j = 0; j < objArr.length; j++) {
				System.out
						.println("���{��[" + i + "]�����@�@�F[" + j + "]" + objArr[j]);
			}
		}
		System.out.println("���������� " + intCounter + "�Ԗړd���I�� ����������");
	}
}