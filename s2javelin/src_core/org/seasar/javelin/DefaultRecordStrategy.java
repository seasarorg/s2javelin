package org.seasar.javelin;

public class DefaultRecordStrategy implements RecordStrategy
{
	/**
	 * Javelinログをファイルに出力かどうか判定する</br>
	 * AccumulatedTimeがjavelin.recordThresholdに設定した値以上のときに出力する。
	 */
	@Override
	public boolean judgeGenerateJaveinFile(CallTreeNode node)
	{
		if (node.getAccumulatedTime() > node.getInvocation().getRecordThreshold())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * アラームを通知するかどうか判定する</br>
	 * AccumulatedTimeがjavelin.alarmThresholdに設定した値以上のときに出力する。
	 */
	@Override
	public boolean judgeSendExceedThresholdAlarm(CallTreeNode node)
	{
		if (node.getAccumulatedTime() >= node.getInvocation().getAlarmThreshold())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
