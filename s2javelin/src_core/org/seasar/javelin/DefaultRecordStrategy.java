package org.seasar.javelin;

import org.seasar.javelin.log.JavelinLogCallback;

public class DefaultRecordStrategy implements RecordStrategy
{
	/**
	 * Javelinログをファイルに出力かどうか判定する</br>
	 * AccumulatedTimeがjavelin.recordThresholdに設定した値以上のときに出力する。
	 */
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

	/**
	 * 何もしない。
	 */
    public JavelinLogCallback createCallback(CallTreeNode node)
    {
        // Do Nothing
        return null;
    }
}
