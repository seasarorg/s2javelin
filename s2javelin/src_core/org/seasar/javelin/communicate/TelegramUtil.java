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
 * 基本的な共通機能を提供する
 */
public final class TelegramUtil extends Common {
	private static final String CLASSMETHOD_SEPARATOR = "###CLASSMETHOD_SEPARATOR###";
    private static final int TELEGRAM_ITEM_COUNT = 6;

	/**
	 * String ⇒ byte[size,data] に転換する
	 */
	public static byte[] strToByteArr(String strBodyData) {
		// String ⇒ byte[data] に転換する
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
	 * byte[size,data] ⇒ String に転換する
	 */
	public static String byteArrToStr(ByteBuffer buffer) {
		// 返却する用
		String strResult = "";

		// size を取得する
		int intbyteArrLength = buffer.getInt();

		try {
			byte[] byteSoruceArr = new byte[intbyteArrLength];
			buffer.get(byteSoruceArr);

			// byte[data] を String に変換する
			strResult = new String(byteSoruceArr, 0, intbyteArrLength, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			return "";
		}

		// 返却する
		return strResult;
	}

	/**
	 * 電文は、object ⇒ byte[] に変換する
	 */
	public static byte[] createTelegram(Telegram telegram) {
		// 頭部データを出力流に設定する
		byte[] byteHeadArr = new byte[6];
		byteHeadArr[TelegramUtil.INT_BYTE_SWITCH_LENGTH] = telegram
				.getObjHeader().getByteTelegramKind();
		byteHeadArr[TelegramUtil.INT_BYTE_SWITCH_LENGTH + 1] = telegram
				.getObjHeader().getByteRequestKind();

		if (telegram.getObjBody() == null) {
			setLength(byteHeadArr);
			return byteHeadArr;
		}

		// 本体データを作る
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			byteArrayOutputStream.write(byteHeadArr);

			for (int i = 0; i < telegram.getObjBody().length; i++) {
				ResponseBody responseBody = (ResponseBody) telegram
						.getObjBody()[i];
				// オブジェクト名 を byte[size,data] に転換する
				byte[] byteBodyObjNameArr = TelegramUtil
						.strToByteArr(responseBody.getStrObjName());
				byteArrayOutputStream.write(byteBodyObjNameArr);

				// 項目名 を byte[size,data] に転換する
				byte[] byteBodyItemNameArr = TelegramUtil
						.strToByteArr(responseBody.getStrItemName());
				byteArrayOutputStream.write(byteBodyItemNameArr);

				// 項目型 を byte[size,data] に転換する
				byte[] byteBodyItemModeArr = new byte[1];
				byteBodyItemModeArr[0] = responseBody.getByteItemMode();
				byteArrayOutputStream.write(byteBodyItemModeArr);

				// 繰り返し回数 を byte[size,data] に転換する
				ByteBuffer objByteBufferBodyLoopCount = ByteBuffer
						.allocate(INT_BYTE_SWITCH_LENGTH);
				objByteBufferBodyLoopCount.putInt(responseBody
						.getIntLoopCount());
				byte[] byteBodyLoopCount = objByteBufferBodyLoopCount.array();
				byteArrayOutputStream.write(byteBodyLoopCount);

				// 説明 を byte[size,data] に転換する
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
			// 発生しない。
		}
		byte[] byteOutputArr = byteArrayOutputStream.toByteArray();
		setLength(byteOutputArr);

		// 返却する
		return byteOutputArr;
	}

	private static void setLength(byte[] byteOutputArr) {
		// 変換クラスを使う
		ByteBuffer objByteBuffer = ByteBuffer.allocate(INT_BYTE_SWITCH_LENGTH);
		objByteBuffer.putInt(byteOutputArr.length);

		// 電文総サイズを取得して、出力流の最初ところに設定する
		byte[] byteHeadSizeArr = objByteBuffer.array();
		for (int i = 0; i < byteHeadSizeArr.length; i++) {
			byteOutputArr[i] = byteHeadSizeArr[i];
		}
	}

	/**
	 * 電文は、byte[] ⇒ object に回復する TODO ERIGUCHI 全電文種別には対応していない。
	 */
	public static Telegram recoveryTelegram(byte[] byteTelegramArr) {
		// 返却する用
		Telegram objTelegram = new Telegram();

		if (byteTelegramArr == null)
			return null;

		ByteBuffer telegramBuffer = ByteBuffer.wrap(byteTelegramArr);

		// まず、Header分が取得する
		Header objHeader = new Header();
		objHeader.setIntSize(telegramBuffer.getInt());
		objHeader.setByteTelegramKind(telegramBuffer.get());
		objHeader.setByteRequestKind(telegramBuffer.get());

		// 一つ電文に置いて最大は六つ本体がある
		ResponseBody[] objResponseBodyArrTemp = new ResponseBody[6];
		// 本電文の中で実際本体数統計
		int intBodyCounter = 0;

		// 本体を取得する
		while (telegramBuffer.remaining() > 0) {
			// 一つ本体対象を作る
			objResponseBodyArrTemp[intBodyCounter] = new ResponseBody();

			// オブジェクト名設定
			objResponseBodyArrTemp[intBodyCounter]
					.setStrObjName(byteArrToStr(telegramBuffer));

			// 項目名設定
			objResponseBodyArrTemp[intBodyCounter]
					.setStrItemName(byteArrToStr(telegramBuffer));

			// 項目型設定
			objResponseBodyArrTemp[intBodyCounter]
					.setByteItemMode(telegramBuffer.get());

			// 繰り返し回数設定
			objResponseBodyArrTemp[intBodyCounter]
					.setIntLoopCount(telegramBuffer.getInt());

			// 説明設定
			Object[] values = new Object[objResponseBodyArrTemp[intBodyCounter]
					.getIntLoopCount()];
			for (int index = 0; index < values.length; index++) {
				if (objResponseBodyArrTemp[intBodyCounter].getByteItemMode() == BYTE_ITEMMODE_KIND_STRING) {
					// 項目型がBYTE_ITEMMODE_KIND_STRINGの場合、String[] を取得する
					values[index] = byteArrToStr(telegramBuffer);
				} else {
					// 項目型がBYTE_ITEMMODE_KIND_STRING以外の場合、Long を取得する
					values[index] = Long.valueOf(telegramBuffer.getLong());
				}
			}

			objResponseBodyArrTemp[intBodyCounter].setObjItemValueArr(values);
			// 本体数を記録する
			intBodyCounter++;
		}

		// 本体リストを作る
		ResponseBody[] objResponseBodyArr = new ResponseBody[intBodyCounter];
		System.arraycopy(objResponseBodyArrTemp, 0, objResponseBodyArr, 0,
				intBodyCounter);

		// 回復した頭部と本体を電文に設定する
		objTelegram.setObjHeader(objHeader);
		objTelegram.setObjBody(objResponseBodyArr);

		// 返却する
		return objTelegram;
	}

	/**
	 * 電文を出力する
	 */
	public static String printTelegram(int intCounter, Telegram objInputTelegram) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n***** " + intCounter + "番目電文開始 *****\n");
		buffer
				.append("#ヘッダ電文長 : "
						+ objInputTelegram.getObjHeader().getIntSize());
		buffer.append("\n");
		buffer.append("#ヘッダ電文種別 : "
				+ objInputTelegram.getObjHeader().getByteTelegramKind());
		buffer.append("\n");
		buffer.append("#ヘッダ応答種別 : "
				+ objInputTelegram.getObjHeader().getByteRequestKind());
		buffer.append("\n");

		for (int i = 0; i < objInputTelegram.getObjBody().length; i++) {
			buffer.append("------------------");
			buffer.append("\n");
			buffer.append("--本体["
					+ i
					+ "]対象名　 : "
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getStrObjName());
			buffer.append("\n");
			buffer.append("--本体["
					+ i
					+ "]項目名　 : "
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getStrItemName());
			buffer.append("\n");
			buffer.append("--本体["
					+ i
					+ "]項目型　 : "
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getByteItemMode());
			buffer.append("\n");
			buffer.append("--本体["
					+ i
					+ "]繰返回数 : "
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getIntLoopCount());
			buffer.append("\n");
			Object[] objArr = ((ResponseBody) objInputTelegram.getObjBody()[i])
					.getObjItemValueArr();
			for (int j = 0; j < objArr.length; j++) {
				buffer.append("--本体[" + i + "]説明　　 : [" + j + "]" + objArr[j]);
				buffer.append("\n");
			}
		}
		buffer.append("***** " + intCounter + "番目電文終了 *****");
		buffer.append("\n");

		return buffer.toString();
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
			// 項目説明を置けるリスト
			Object[] objItemValueArr = null;
			int bodyIndex = index * TELEGRAM_ITEM_COUNT;
			// 呼び出し回数
			bodies[bodyIndex + 0] = new ResponseBody();
			bodies[bodyIndex + 0].setStrObjName(strObjName.toString());
			bodies[bodyIndex + 0].setStrItemName("callCount");
			bodies[bodyIndex + 0]
					.setByteItemMode(Common.BYTE_ITEMMODE_KIND_8BYTE_INT);
			bodies[bodyIndex + 0].setIntLoopCount(Common.INT_LOOP_COUNT_SINGLE);
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getCount());
			bodies[bodyIndex + 0].setObjItemValueArr(objItemValueArr);
			// 平均時間
			bodies[bodyIndex + 1] = new ResponseBody();
			bodies[bodyIndex + 1].setStrObjName(strObjName.toString());
			bodies[bodyIndex + 1].setStrItemName("averageInterval");
			bodies[bodyIndex + 1]
					.setByteItemMode(Common.BYTE_ITEMMODE_KIND_8BYTE_INT);
			bodies[bodyIndex + 1].setIntLoopCount(Common.INT_LOOP_COUNT_SINGLE);
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getAverage());
			bodies[bodyIndex + 1].setObjItemValueArr(objItemValueArr);
			// 最大処理時間
			bodies[bodyIndex + 2] = new ResponseBody();
			bodies[bodyIndex + 2].setStrObjName(strObjName.toString());
			bodies[bodyIndex + 2].setStrItemName("maximumInterval");
			bodies[bodyIndex + 2]
					.setByteItemMode(Common.BYTE_ITEMMODE_KIND_8BYTE_INT);
			bodies[2].setIntLoopCount(Common.INT_LOOP_COUNT_SINGLE);
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getMaximum());
			bodies[bodyIndex + 2].setObjItemValueArr(objItemValueArr);
			// 最小処理時間
			bodies[bodyIndex + 3] = new ResponseBody();
			bodies[bodyIndex + 3].setStrObjName(strObjName.toString());
			bodies[bodyIndex + 3].setStrItemName("minimumInterval");
			bodies[bodyIndex + 3]
					.setByteItemMode(Common.BYTE_ITEMMODE_KIND_8BYTE_INT);
			bodies[bodyIndex + 3].setIntLoopCount(Common.INT_LOOP_COUNT_SINGLE);
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getMinimum());
			bodies[bodyIndex + 3].setObjItemValueArr(objItemValueArr);
			// 例外発生回数
			bodies[bodyIndex + 4] = new ResponseBody();
			bodies[bodyIndex + 4].setStrObjName(strObjName.toString());
			bodies[bodyIndex + 4].setStrItemName("throwableCount");
			bodies[bodyIndex + 4]
					.setByteItemMode(Common.BYTE_ITEMMODE_KIND_8BYTE_INT);
			bodies[bodyIndex + 4].setIntLoopCount(Common.INT_LOOP_COUNT_SINGLE);
			objItemValueArr = new Long[1];
			objItemValueArr[0] = Long.valueOf(invocation.getThrowableCount());
			bodies[bodyIndex + 4].setObjItemValueArr(objItemValueArr);
			// メソッドの呼び出し元 クラス名
			bodies[bodyIndex + 5] = new ResponseBody();
			bodies[bodyIndex + 5].setStrObjName(strObjName.toString());
			bodies[bodyIndex + 5].setStrItemName("allCallerNames");
			bodies[bodyIndex + 5]
					.setByteItemMode(Common.BYTE_ITEMMODE_KIND_STRING);
			objItemValueArr = invocation.getAllCallerInvocation();
			bodies[bodyIndex + 5].setIntLoopCount(objItemValueArr.length);
			bodies[bodyIndex + 5].setObjItemValueArr(objItemValueArr);
		}

		// 電文オブジェクトを設定する
		Telegram objTelegram = new Telegram();
		objTelegram.setObjHeader(objHeader);
		objTelegram.setObjBody(bodies);
		return objTelegram;
	}
}