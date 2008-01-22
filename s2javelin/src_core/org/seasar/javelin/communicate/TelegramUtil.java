package org.seasar.javelin.communicate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.seasar.javelin.MBeanManager;
import org.seasar.javelin.bean.Component;
import org.seasar.javelin.bean.Invocation;
import org.seasar.javelin.communicate.entity.Header;
import org.seasar.javelin.communicate.entity.ResponseBody;
import org.seasar.javelin.communicate.entity.Telegram;


/**
 * ��{�I�ȋ��ʋ@�\��񋟂���
 */
public final class TelegramUtil extends Common {
	private static final String CLASSMETHOD_SEPARATOR = "###CLASSMETHOD_SEPARATOR###";
    private static final int TELEGRAM_ITEM_COUNT = 6;

	/**
	 * String �� byte[size,data] �ɓ]������
	 */
	public static byte[] strToByteArr(String strBodyData) {
		// String �� byte[data] �ɓ]������
		byte[] byteBodyDataArr;
		try {
			byteBodyDataArr = strBodyData.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new byte[0];
		}

		ByteBuffer buffer = ByteBuffer.allocate(byteBodyDataArr.length
				+ INT_BYTE_SWITCH_LENGTH);
		buffer.putInt(byteBodyDataArr.length);
		buffer.put(byteBodyDataArr);

		return buffer.array();
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
	 * �d���́Aobject �� byte[] �ɕϊ�����
	 */
	public static byte[] createTelegram(Telegram telegram) {
		// �����f�[�^���o�͗��ɐݒ肷��
		byte[] byteHeadArr = new byte[6];
		byteHeadArr[TelegramUtil.INT_BYTE_SWITCH_LENGTH] = telegram
				.getObjHeader().getByteTelegramKind();
		byteHeadArr[TelegramUtil.INT_BYTE_SWITCH_LENGTH + 1] = telegram
				.getObjHeader().getByteRequestKind();

		if (telegram.getObjBody() == null) {
			setLength(byteHeadArr);
			return byteHeadArr;
		}

		// �{�̃f�[�^�����
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			byteArrayOutputStream.write(byteHeadArr);

			for (int i = 0; i < telegram.getObjBody().length; i++) {
				ResponseBody responseBody = (ResponseBody) telegram
						.getObjBody()[i];
				// �I�u�W�F�N�g�� �� byte[size,data] �ɓ]������
				byte[] byteBodyObjNameArr = TelegramUtil
						.strToByteArr(responseBody.getStrObjName());
				byteArrayOutputStream.write(byteBodyObjNameArr);

				// ���ږ� �� byte[size,data] �ɓ]������
				byte[] byteBodyItemNameArr = TelegramUtil
						.strToByteArr(responseBody.getStrItemName());
				byteArrayOutputStream.write(byteBodyItemNameArr);

				// ���ڌ^ �� byte[size,data] �ɓ]������
				byte[] byteBodyItemModeArr = new byte[1];
				byteBodyItemModeArr[0] = responseBody.getByteItemMode();
				byteArrayOutputStream.write(byteBodyItemModeArr);

				// �J��Ԃ��� �� byte[size,data] �ɓ]������
				ByteBuffer objByteBufferBodyLoopCount = ByteBuffer
						.allocate(INT_BYTE_SWITCH_LENGTH);
				objByteBufferBodyLoopCount.putInt(responseBody
						.getIntLoopCount());
				byte[] byteBodyLoopCount = objByteBufferBodyLoopCount.array();
				byteArrayOutputStream.write(byteBodyLoopCount);

				// ���� �� byte[size,data] �ɓ]������
				Object[] objItemValueArrTemp = (responseBody)
						.getObjItemValueArr();
				if (objItemValueArrTemp != null) {
					for (int j = 0; j < objItemValueArrTemp.length; j++) {
						if (responseBody.getByteItemMode() == 6) {
							byte[] byteItemValueArr;
							byteItemValueArr = TelegramUtil
									.strToByteArr(((Invocation) objItemValueArrTemp[j])
											.getClassName());
							byteArrayOutputStream.write(byteItemValueArr);
						} else {
							ByteBuffer objByteBufferItemValue = ByteBuffer
									.allocate(LONG_BYTE_SWITCH_LENGTH);
							objByteBufferItemValue
									.putLong(((Long) objItemValueArrTemp[j])
											.longValue());
							byte[] byteItemValueArr = objByteBufferItemValue
									.array();
							byteArrayOutputStream.write(byteItemValueArr);
						}
					}

				}
			}
		} catch (IOException ioe) {
			// �������Ȃ��B
		}
		byte[] byteOutputArr = byteArrayOutputStream.toByteArray();
		setLength(byteOutputArr);

		// �ԋp����
		return byteOutputArr;
	}

	private static void setLength(byte[] byteOutputArr) {
		// �ϊ��N���X���g��
		ByteBuffer objByteBuffer = ByteBuffer.allocate(INT_BYTE_SWITCH_LENGTH);
		objByteBuffer.putInt(byteOutputArr.length);

		// �d�����T�C�Y���擾���āA�o�͗��̍ŏ��Ƃ���ɐݒ肷��
		byte[] byteHeadSizeArr = objByteBuffer.array();
		for (int i = 0; i < byteHeadSizeArr.length; i++) {
			byteOutputArr[i] = byteHeadSizeArr[i];
		}
	}

	/**
	 * �d���́Abyte[] �� object �ɉ񕜂��� TODO ERIGUCHI �S�d����ʂɂ͑Ή����Ă��Ȃ��B
	 */
	public static Telegram recoveryTelegram(byte[] byteTelegramArr) {
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

		// ��d���ɒu���čő�͘Z�{�̂�����
		ResponseBody[] objResponseBodyArrTemp = new ResponseBody[6];
		// �{�d���̒��Ŏ��ۖ{�̐����v
		int intBodyCounter = 0;

		// �{�̂��擾����
		while (telegramBuffer.remaining() > 0) {
			// ��{�̑Ώۂ����
			objResponseBodyArrTemp[intBodyCounter] = new ResponseBody();

			// �I�u�W�F�N�g���ݒ�
			objResponseBodyArrTemp[intBodyCounter]
					.setStrObjName(byteArrToStr(telegramBuffer));

			// ���ږ��ݒ�
			objResponseBodyArrTemp[intBodyCounter]
					.setStrItemName(byteArrToStr(telegramBuffer));

			// ���ڌ^�ݒ�
			objResponseBodyArrTemp[intBodyCounter]
					.setByteItemMode(telegramBuffer.get());

			// �J��Ԃ��񐔐ݒ�
			objResponseBodyArrTemp[intBodyCounter]
					.setIntLoopCount(telegramBuffer.getInt());

			// �����ݒ�
			Object[] values = new Object[objResponseBodyArrTemp[intBodyCounter]
					.getIntLoopCount()];
			for (int index = 0; index < values.length; index++) {
				if (objResponseBodyArrTemp[intBodyCounter].getByteItemMode() == BYTE_ITEMMODE_KIND_STRING) {
					// ���ڌ^��BYTE_ITEMMODE_KIND_STRING�̏ꍇ�AString[] ���擾����
					values[index] = byteArrToStr(telegramBuffer);
				} else {
					// ���ڌ^��BYTE_ITEMMODE_KIND_STRING�ȊO�̏ꍇ�ALong ���擾����
					values[index] = Long.valueOf(telegramBuffer.getLong());
				}
			}

			objResponseBodyArrTemp[intBodyCounter].setObjItemValueArr(values);
			// �{�̐����L�^����
			intBodyCounter++;
		}

		// �{�̃��X�g�����
		ResponseBody[] objResponseBodyArr = new ResponseBody[intBodyCounter];
		System.arraycopy(objResponseBodyArrTemp, 0, objResponseBodyArr, 0,
				intBodyCounter);

		// �񕜂��������Ɩ{�̂�d���ɐݒ肷��
		objTelegram.setObjHeader(objHeader);
		objTelegram.setObjBody(objResponseBodyArr);

		// �ԋp����
		return objTelegram;
	}

	/**
	 * �d�����o�͂���
	 */
	public static String printTelegram(int intCounter, Telegram objInputTelegram) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n***** " + intCounter + "�Ԗړd���J�n *****\n");
		buffer
				.append("#�w�b�_�d���� : "
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
			// ���ڐ�����u���郊�X�g
			Object[] objItemValueArr = null;
			int bodyIndex = index * TELEGRAM_ITEM_COUNT;
			// �Ăяo����
			bodies[bodyIndex + 0] = new ResponseBody();
			bodies[bodyIndex + 0].setStrObjName(strObjName.toString());
			bodies[bodyIndex + 0].setStrItemName("callCount");
			bodies[bodyIndex + 0]
					.setByteItemMode(Common.BYTE_ITEMMODE_KIND_8BYTE_INT);
			bodies[bodyIndex + 0].setIntLoopCount(Common.INT_LOOP_COUNT_SINGLE);
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getCount());
			bodies[bodyIndex + 0].setObjItemValueArr(objItemValueArr);
			// ���ώ���
			bodies[bodyIndex + 1] = new ResponseBody();
			bodies[bodyIndex + 1].setStrObjName(strObjName.toString());
			bodies[bodyIndex + 1].setStrItemName("averageInterval");
			bodies[bodyIndex + 1]
					.setByteItemMode(Common.BYTE_ITEMMODE_KIND_8BYTE_INT);
			bodies[bodyIndex + 1].setIntLoopCount(Common.INT_LOOP_COUNT_SINGLE);
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getAverage());
			bodies[bodyIndex + 1].setObjItemValueArr(objItemValueArr);
			// �ő又������
			bodies[bodyIndex + 2] = new ResponseBody();
			bodies[bodyIndex + 2].setStrObjName(strObjName.toString());
			bodies[bodyIndex + 2].setStrItemName("maximumInterval");
			bodies[bodyIndex + 2]
					.setByteItemMode(Common.BYTE_ITEMMODE_KIND_8BYTE_INT);
			bodies[2].setIntLoopCount(Common.INT_LOOP_COUNT_SINGLE);
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getMaximum());
			bodies[bodyIndex + 2].setObjItemValueArr(objItemValueArr);
			// �ŏ���������
			bodies[bodyIndex + 3] = new ResponseBody();
			bodies[bodyIndex + 3].setStrObjName(strObjName.toString());
			bodies[bodyIndex + 3].setStrItemName("minimumInterval");
			bodies[bodyIndex + 3]
					.setByteItemMode(Common.BYTE_ITEMMODE_KIND_8BYTE_INT);
			bodies[bodyIndex + 3].setIntLoopCount(Common.INT_LOOP_COUNT_SINGLE);
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getMinimum());
			bodies[bodyIndex + 3].setObjItemValueArr(objItemValueArr);
			// ��O������
			bodies[bodyIndex + 4] = new ResponseBody();
			bodies[bodyIndex + 4].setStrObjName(strObjName.toString());
			bodies[bodyIndex + 4].setStrItemName("throwableCount");
			bodies[bodyIndex + 4]
					.setByteItemMode(Common.BYTE_ITEMMODE_KIND_8BYTE_INT);
			bodies[bodyIndex + 4].setIntLoopCount(Common.INT_LOOP_COUNT_SINGLE);
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getThrowableCount());
			bodies[bodyIndex + 4].setObjItemValueArr(objItemValueArr);
			// ���\�b�h�̌Ăяo���� �N���X��
			bodies[bodyIndex + 5] = new ResponseBody();
			bodies[bodyIndex + 5].setStrObjName(strObjName.toString());
			bodies[bodyIndex + 5].setStrItemName("allCallerNames");
			bodies[bodyIndex + 5]
					.setByteItemMode(Common.BYTE_ITEMMODE_KIND_STRING);
			objItemValueArr = invocation.getAllCallerInvocation();
			bodies[bodyIndex + 5].setIntLoopCount(objItemValueArr.length);
			bodies[bodyIndex + 5].setObjItemValueArr(objItemValueArr);
		}

		// �d���I�u�W�F�N�g��ݒ肷��
		Telegram objTelegram = new Telegram();
		objTelegram.setObjHeader(objHeader);
		objTelegram.setObjBody(bodies);
		return objTelegram;
	}
}