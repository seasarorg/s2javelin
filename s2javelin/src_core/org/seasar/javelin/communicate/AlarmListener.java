package org.seasar.javelin.communicate;

import org.seasar.javelin.CallTreeNode;

/**
 * �A���[����ʒm���鏈������������C���^�t�F�[�X�B
 */
public interface AlarmListener
{
    /**
     * �������l���߂̃A���[����ʒm����ۂɎg�p����B
     * 
     * @param node �������l���߂����Ăяo���̏��B
     */
    void sendExceedThresholdAlarm(CallTreeNode node);
    
    /**
     * ���[�g�m�[�h(�R�[���c���[�̒��_)�݂̂�Alarm�̑ΏۂƂ��邩�ǂ������w�肷��B
     * 
     * @return true�Ȃ�΃��[�g�m�[�h�݂̂�ΏۂƂ���Bfalse�Ȃ�ΑS�Ă�Alarm��ΏۂƂ���B
     */
    boolean isSendingRootOnly();
}
