package org.seasar.javelin;

import org.seasar.javelin.log.JavelinLogCallback;

/**
 * S2StatsJavelinRecorder�ňȉ��̏������s�����ǂ������肷��Strategy�C���^�t����B</br>
 * <li>Javelin���O</li>
 * <li>�A���[���ʒm</li>
 * 
 * @author tsukano
 */
public interface RecordStrategy
{
	/**
	 * Javelin���O���t�@�C���ɏo�͂��邩�ǂ������肷��B
	 * @param node
	 * @return true:�o�͂���Afalse:�o�͂��Ȃ�
	 */
	public boolean judgeGenerateJaveinFile(CallTreeNode node);
	
	/**
	 * �A���[����ʒm���邩�ǂ������肷��B
	 * @param node
	 * @return true:�ʒm����Afalse:�ʒm���Ȃ�
	 */
	public boolean judgeSendExceedThresholdAlarm(CallTreeNode node);
	
	/**
	 * �R�[���o�b�N�I�u�W�F�N�g��Ԃ��B
	 * 
	 * @param node�@�m�[�h�B
	 * 
	 * @return �R�[���o�b�N�I�u�W�F�N�g�B
	 */
	public JavelinLogCallback createCallback(CallTreeNode node);

    
    /**
     * �R�[���o�b�N�I�u�W�F�N�g��Ԃ��B
     * 
     * @return �R�[���o�b�N�I�u�W�F�N�g�B
     */
    public JavelinLogCallback createCallback();
}
