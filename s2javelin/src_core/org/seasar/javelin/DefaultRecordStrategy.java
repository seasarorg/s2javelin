package org.seasar.javelin;

public class DefaultRecordStrategy implements RecordStrategy
{
	/**
	 * Javelin���O���t�@�C���ɏo�͂��ǂ������肷��</br>
	 * AccumulatedTime��javelin.recordThreshold�ɐݒ肵���l�ȏ�̂Ƃ��ɏo�͂���B
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
	 * �A���[����ʒm���邩�ǂ������肷��</br>
	 * AccumulatedTime��javelin.alarmThreshold�ɐݒ肵���l�ȏ�̂Ƃ��ɏo�͂���B
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
