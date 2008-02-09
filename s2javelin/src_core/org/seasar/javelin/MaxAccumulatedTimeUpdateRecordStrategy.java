package org.seasar.javelin;

public class MaxAccumulatedTimeUpdateRecordStrategy implements RecordStrategy
{
	private int ignoreUpdateCount = 3;
	
	@Override
	public boolean judgeGenerateJaveinFile(CallTreeNode node)
	{
		if (   node.getAccumulatedTime() >= node.getMaxAccumulatedTime()
			&& node.getMaxAccumulatedTimeUpdateCount() > ignoreUpdateCount)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean judgeSendExceedThresholdAlarm(CallTreeNode node)
	{
		return judgeGenerateJaveinFile(node);
	}
}
