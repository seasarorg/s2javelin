package org.seasar.javelin;

import org.seasar.javelin.log.JavelinLogCallback;

/**
 * TATの値が設定した閾値を越えている場合、ログファイル出力、アラーム通知を行う。
 * @author eriguchi
 *
 */
public class S2DefaultRecordStrategy implements RecordStrategy
{
    /**
     * Javelinログをファイルに出力かどうか判定する</br>
     * AccumulatedTimeがjavelin.recordThresholdに設定した値以上のときに出力する。
     * @param node CallTreeNode
     * @return true:ログファイル出力を行う、false：ログファイル出力を行わない。
     */
    public boolean judgeGenerateJaveinFile(CallTreeNode node)
    {
        if (node.getAccumulatedTime() > node.getInvocation().getRecordThreshold())
        {
            return true;
        }
        return false;
    }

    /**
     * アラームを通知するかどうか判定する</br>
     * AccumulatedTimeがjavelin.alarmThresholdに設定した値以上のときに出力する。
     * @param node CallTreeNode
     * @return true:アラーム通知を行う、false：アラーム通知を行わない。
     */
    public boolean judgeSendExceedThresholdAlarm(CallTreeNode node)
    {
        if (node.getAccumulatedTime() >= node.getInvocation().getAlarmThreshold())
        {
            return true;
        }
        return false;
    }

    /**
     * 何もしない。
     * @param node CallTreeNode
     * @return null
     */
    public JavelinLogCallback createCallback(CallTreeNode node)
    {
        // Do Nothing
        return null;
    }

    /**
     * 何もしない。
     * @return null
     */
    public JavelinLogCallback createCallback()
    {
        // Do Nothing
        return null;
    }
}
