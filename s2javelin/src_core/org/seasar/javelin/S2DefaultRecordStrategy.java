package org.seasar.javelin;

import org.seasar.javelin.log.JavelinLogCallback;

/**
 * TAT�̒l���ݒ肵��臒l���z���Ă���ꍇ�A���O�t�@�C���o�́A�A���[���ʒm���s���B
 * @author eriguchi
 *
 */
public class S2DefaultRecordStrategy implements RecordStrategy
{
    /**
     * Javelin���O���t�@�C���ɏo�͂��ǂ������肷��</br>
     * AccumulatedTime��javelin.recordThreshold�ɐݒ肵���l�ȏ�̂Ƃ��ɏo�͂���B
     * @param node CallTreeNode
     * @return true:���O�t�@�C���o�͂��s���Afalse�F���O�t�@�C���o�͂��s��Ȃ��B
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
     * �A���[����ʒm���邩�ǂ������肷��</br>
     * AccumulatedTime��javelin.alarmThreshold�ɐݒ肵���l�ȏ�̂Ƃ��ɏo�͂���B
     * @param node CallTreeNode
     * @return true:�A���[���ʒm���s���Afalse�F�A���[���ʒm���s��Ȃ��B
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
     * �������Ȃ��B
     * @param node CallTreeNode
     * @return null
     */
    public JavelinLogCallback createCallback(CallTreeNode node)
    {
        // Do Nothing
        return null;
    }

    /**
     * �������Ȃ��B
     * @return null
     */
    public JavelinLogCallback createCallback()
    {
        // Do Nothing
        return null;
    }
}
