package org.seasar.javelin;

/**
 * S2StatsJavelinRecorderで以下の処理を行うかどうか判定するStrategyインタフｪｰｽ。</br>
 * <li>Javelinログ</li>
 * <li>アラーム通知</li>
 * 
 * @author tsukano
 */
public interface RecordStrategy
{
	/**
	 * Javelinログをファイルに出力するかどうか判定する。
	 * @param node
	 * @return true:出力する、false:出力しない
	 */
	public boolean judgeGenerateJaveinFile(CallTreeNode node);
	
	/**
	 * アラームを通知するかどうか判定する。
	 * @param node
	 * @return true:通知する、false:通知しない
	 */
	public boolean judgeSendExceedThresholdAlarm(CallTreeNode node);
}
