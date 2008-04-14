package org.seasar.javelin;

import org.seasar.javelin.log.JavelinLogCallback;
import org.seasar.javelin.util.JavelinConfigUtil;

/**
 * 呼び出し積算時間の最大値が更新された場合に、記録・通知を行うRecordStrategy。 ただし、不必要な記録・通知を防ぐため、更新回数が
 * javelin.maxAccumulatedTimeUpdate.ignoreUpdateCountで指定された 回数以下の場合は記録・通知を行わない。
 * 
 * @author tsukano
 */
public class S2MaxAccumulatedTimeUpdateRecordStrategy implements RecordStrategy
{
    /** 更新回数を無視する閾値 */
    private int                 ignoreUpdateCount_;

    /** 更新回数を無視する閾値を表すプロパティ名 */
    private static final String IGNOREUPDATECOUNT_KEY     =
                                                                  S2JavelinConfig.JAVELIN_PREFIX
                                                                          + "maxAccumulatedTimeUpdate.ignoreUpdateCount";

    /** 更新回数を無視する閾値のデフォルト */
    private static final int    DEFAULT_IGNOREUPDATECOUNT = 3;

    /**
     * プロパティからignoreUpdateCountを読み込む。
     */
    public S2MaxAccumulatedTimeUpdateRecordStrategy()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        this.ignoreUpdateCount_ =
                configUtil.getInteger(IGNOREUPDATECOUNT_KEY, DEFAULT_IGNOREUPDATECOUNT);
    }

    public boolean judgeGenerateJaveinFile(CallTreeNode node)
    {
        if (node.getAccumulatedTime() >= node.getInvocation().getMaxAccumulatedTime()
                && node.getInvocation().getMaxAccumulatedTimeUpdateCount() > this.ignoreUpdateCount_)
        {
            return true;
        }

        return false;
    }

    public boolean judgeSendExceedThresholdAlarm(CallTreeNode node)
    {
        return judgeGenerateJaveinFile(node);
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
}
