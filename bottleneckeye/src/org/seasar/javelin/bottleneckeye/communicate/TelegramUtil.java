package org.seasar.javelin.bottleneckeye.communicate;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 基本的な共通機能を提供する
 */
public final class TelegramUtil extends Common {
	/**
	 * byte[] の前４位を取得して、int に転換する
	 */
	public static int arrHead4ToInt(byte[] byteSoruceArr) {
		// 返却する用
		int intResult = 0;

		// byte[] の前４位を取得する
		byte[] byteHeaderLengthArr = new byte[INT_BYTE_SWITCH_LENGTH];
		for (int i = 0; i < byteHeaderLengthArr.length; i++) {
			byteHeaderLengthArr[i] = byteSoruceArr[i];
		}

		// 変換クラスを使う
		ByteBuffer objByteBuffer = ByteBuffer.allocate(INT_BYTE_SWITCH_LENGTH);
		objByteBuffer = objByteBuffer.put(byteHeaderLengthArr);

		// int に転換する
		intResult = objByteBuffer.getInt(0);

		// 返却する
		return intResult;
	}

	/**
	 * byte[] の前８位を取得して、long に転換する
	 */
	public static long arrHead8Tolong(byte[] byteSoruceArr) {
		// 返却する用
		long lngResult = 0;

		// byte[] の前８位を取得する
		byte[] byteTempArr = new byte[LONG_BYTE_SWITCH_LENGTH];
		for (int i = 0; i < byteTempArr.length; i++) {
			byteTempArr[i] = byteSoruceArr[i];
		}

		// 変換クラスを使う
		ByteBuffer objByteBuffer = ByteBuffer.allocate(LONG_BYTE_SWITCH_LENGTH);
		objByteBuffer = objByteBuffer.put(byteTempArr);

		// long に転換する
		lngResult = objByteBuffer.getLong(0);

		// 返却する
		return lngResult;
	}

	/**
	 * String ⇒ byte[size,data] に転換する
	 */
	public static byte[] strToByteArr(String strBodyData) {
		// String ⇒ byte[data] に転換する
		byte[] byteBodyDataArr = strBodyData.getBytes();

		// 変換クラスを使って、String.length ⇒ byte[size] に転換する
		ByteBuffer objByteBuffer = ByteBuffer.allocate(INT_BYTE_SWITCH_LENGTH);
		objByteBuffer = objByteBuffer.putInt(byteBodyDataArr.length);

		// byte[size] に転換する
		byte[] byteBodyDataLengthArr = objByteBuffer.array();

		// byte[size] と byte[data] を連接する
		byteBodyDataArr = arrayAdd(byteBodyDataLengthArr, byteBodyDataArr);

		// 返却する
		return byteBodyDataArr;
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
	 * byte[size,data] ⇒ String に転換する
	 */
	public static String byteArrToStr(byte[] byteSoruceArr) {
		// 返却する用
		String strResult = "";

		// size を取得する
		int intbyteArrLength = arrHead4ToInt(byteSoruceArr);

		try {
			// byte[data] を String に変換する
			strResult = new String(byteSoruceArr, INT_BYTE_SWITCH_LENGTH,
					intbyteArrLength, "UTF-8");
		} catch (UnsupportedEncodingException objUnsupportedEncodingException) {
			// エラーメッセージを出す
			objUnsupportedEncodingException.printStackTrace();
		}

		// 返却する
		return strResult;
	}

	/**
	 * 電文は、object ⇒ byte[] に変換する
	 */
	public static byte[] createTelegram(Telegram objTelegram) {
		// 返却用
		byte[] byteOutputArr = null;

		// 頭部データを出力流に設定する
		byte[] byteHeadArr = new byte[6];
		byteHeadArr[TelegramUtil.INT_BYTE_SWITCH_LENGTH] = objTelegram
				.getObjHeader().getByteTelegramKind();
		byteHeadArr[TelegramUtil.INT_BYTE_SWITCH_LENGTH + 1] = objTelegram
				.getObjHeader().getByteRequestKind();

		// 本体データを作る
		byte[] byteBodyArr = null;

		if (objTelegram.getObjBody() != null) {
			for (int i = 0; i < objTelegram.getObjBody().length; i++) {
				// オブジェクト名 を byte[size,data] に転換する
				byte[] byteBodyObjNameArr = TelegramUtil
						.strToByteArr(((RequestBody) objTelegram.getObjBody()[i])
								.getStrObjName());
				// 項目名 を byte[size,data] に転換する
				byte[] byteBodyItemNameArr = TelegramUtil
						.strToByteArr(((RequestBody) objTelegram.getObjBody()[i])
								.getStrItemName());
				// オブジェクト名と項目名を連接する
				byte[] byteBodyTemp = TelegramUtil.arrayAdd(byteBodyObjNameArr,
						byteBodyItemNameArr);
				// 本体データに設定する
				byteBodyArr = TelegramUtil.arrayAdd(byteBodyArr, byteBodyTemp);
			}
		}

		// 頭部と本体を出力流に設定する
		byteOutputArr = TelegramUtil.arrayAdd(byteHeadArr, byteBodyArr);

		// 変換クラスを使う
		ByteBuffer objByteBuffer = ByteBuffer.allocate(INT_BYTE_SWITCH_LENGTH);
		objByteBuffer.putInt(byteOutputArr.length);

		// 電文総サイズを取得して、出力流の最初ところに設定する
		byte[] byteHeadSizeArr = objByteBuffer.array();
		for (int i = 0; i < byteHeadSizeArr.length; i++) {
			byteOutputArr[i] = byteHeadSizeArr[i];
		}

		// 返却する
		return byteOutputArr;
	}

	/**
	 * 電文は、byte[] ⇒ object に回復する
	 */
	public static Telegram recovryTelegram(byte[] byteTelegramArr) {
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

		List bodyList = new ArrayList();

		// 本体を取得する
		while (telegramBuffer.remaining() > 0) {
			// 一つ本体対象を作る
			ResponseBody body = new ResponseBody();

			// オブジェクト名設定
			body.setStrObjName(byteArrToStr(telegramBuffer));

			// 項目名設定
			body.setStrItemName(byteArrToStr(telegramBuffer));

			// 項目型設定
			body.setByteItemMode(telegramBuffer.get());

			// 繰り返し回数設定
			body.setIntLoopCount(telegramBuffer.getInt());

			// 説明設定
			Object[] values = new Object[body.getIntLoopCount()];
			for (int index = 0; index < values.length; index++) {
				if (body.getByteItemMode() == BYTE_ITEMMODE_KIND_STRING) {
					// 項目型がBYTE_ITEMMODE_KIND_STRINGの場合、String[] を取得する
					values[index] = byteArrToStr(telegramBuffer);
				} else {
					// 項目型がBYTE_ITEMMODE_KIND_STRING以外の場合、Long を取得する
					values[index] = Long.valueOf(telegramBuffer.getLong());
				}
			}

			body.setObjItemValueArr(values);

			bodyList.add(body);
		}

		// 本体リストを作る
		ResponseBody[] objResponseBodyArr = (ResponseBody[]) bodyList
				.toArray(new ResponseBody[0]);

		// 回復した頭部と本体を電文に設定する
		objTelegram.setObjHeader(objHeader);
		objTelegram.setObjBody(objResponseBodyArr);

		// 返却する
		return objTelegram;
	}

	/**
	 * 電文を出力する
	 */
	public static void printTelegram(int intCounter, Telegram objInputTelegram) {
		System.out.println("\n※※※※※ " + intCounter + "番目電文開始 ※※※※※");
		System.out.println("●頭部電文長度："
				+ objInputTelegram.getObjHeader().getIntSize());
		System.out.println("●頭部電文種別："
				+ objInputTelegram.getObjHeader().getByteTelegramKind());
		System.out.println("●頭部応答種別："
				+ objInputTelegram.getObjHeader().getByteRequestKind());

		for (int i = 0; i < objInputTelegram.getObjBody().length; i++) {
			System.out.println("------------------");
			System.out.println("○本体["
					+ i
					+ "]対象名　："
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getStrObjName());
			System.out.println("○本体["
					+ i
					+ "]項目名　："
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getStrItemName());
			System.out.println("○本体["
					+ i
					+ "]項目型　："
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getByteItemMode());
			System.out.println("○本体["
					+ i
					+ "]繰返回数："
					+ ((ResponseBody) objInputTelegram.getObjBody()[i])
							.getIntLoopCount());
			Object[] objArr = ((ResponseBody) objInputTelegram.getObjBody()[i])
					.getObjItemValueArr();
			for (int j = 0; j < objArr.length; j++) {
				System.out
						.println("○本体[" + i + "]説明　　：[" + j + "]" + objArr[j]);
			}
		}
		System.out.println("※※※※※ " + intCounter + "番目電文終了 ※※※※※");
	}
}