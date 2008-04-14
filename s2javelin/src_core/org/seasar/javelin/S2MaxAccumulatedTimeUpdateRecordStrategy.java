package org.seasar.javelin;

import org.seasar.javelin.log.JavelinLogCallback;
import org.seasar.javelin.util.JavelinConfigUtil;

/**
 * �Ăяo���ώZ���Ԃ̍ő�l���X�V���ꂽ�ꍇ�ɁA�L�^�E�ʒm���s��RecordStrategy�B �������A�s�K�v�ȋL�^�E�ʒm��h�����߁A�X�V�񐔂�
 * javelin.maxAccumulatedTimeUpdate.ignoreUpdateCount�Ŏw�肳�ꂽ �񐔈ȉ��̏ꍇ�͋L�^�E�ʒm���s��Ȃ��B
 * 
 * @author tsukano
 */
public class S2MaxAccumulatedTimeUpdateRecordStrategy implements RecordStrategy
{
    /** �X�V�񐔂𖳎�����臒l */
    private int                 ignoreUpdateCount_;

    /** �X�V�񐔂𖳎�����臒l��\���v���p�e�B�� */
    private static final String IGNOREUPDATECOUNT_KEY     =
                                                                  S2JavelinConfig.JAVELIN_PREFIX
                                                                          + "maxAccumulatedTimeUpdate.ignoreUpdateCount";

    /** �X�V�񐔂𖳎�����臒l�̃f�t�H���g */
    private static final int    DEFAULT_IGNOREUPDATECOUNT = 3;

    /**
     * �v���p�e�B����ignoreUpdateCount��ǂݍ��ށB
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
     * �������Ȃ��B
     * @param node CallTreeNode
     * @return null
     */
    public JavelinLogCallback createCallback(CallTreeNode node)
    {
        // Do Nothing
        return null;
    }
}
