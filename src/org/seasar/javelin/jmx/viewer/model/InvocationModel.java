package org.seasar.javelin.jmx.viewer.model;

import java.util.List;

public class InvocationModel implements Comparable
{
	private ComponentModel component_;
	private String methodName_;
	
	/** メソッドの呼び出し回数。 */
	private long count_;
	
	/** メソッドの最短処理時間（単位:ミリ秒）。 */
	private long minimum_;
	
	/** メソッドの最長処理時間（単位:ミリ秒）。 */
	private long maximum_;
	
	/** メソッドの平均処理時間（単位:ミリ秒）。 */
	private long average_;
	
	/** メソッド内での例外発生回数。 */
	private long throwableCount_;
	
	/** メソッド内で発生した例外の履歴。 */
	private List<Throwable> throwableList_;

	/**  */
	private long warningThreshold_ = Long.MAX_VALUE;
	
	/**  */
	private long alarmThreshold_   = Long.MAX_VALUE;

	public long getAverage()
	{
		return average_;
	}

	public void setAverage(long average)
	{
		average_ = average;
	}

	public long getCount()
	{
		return count_;
	}

	public void setCount(long count)
	{
		count_ = count;
	}

	public long getMaximum()
	{
		return maximum_;
	}

	public void setMaximum(long maximum)
	{
		maximum_ = maximum;
	}

	public long getMinimum()
	{
		return minimum_;
	}

	public void setMinimum(long minimum)
	{
		minimum_ = minimum;
	}

	public long getThrowableCount()
	{
		return throwableCount_;
	}

	public void setThrowableCount(long throwableCount)
	{
		throwableCount_ = throwableCount;
	}

	public List<Throwable> getThrowableList()
	{
		return throwableList_;
	}

	public void setThrowableList(List<Throwable> throwableList)
	{
		throwableList_ = throwableList;
	}

	public ComponentModel getComponent()
	{
		return component_;
	}
	
	public void setComponent(ComponentModel component)
	{
		component_ = component;
	}

	public String getMethodName()
	{
		return methodName_;
	}

	public void setMethodName(String methodName)
	{
		methodName_ = methodName;
	}

	public long getWarningThreshold()
	{
		return warningThreshold_;
	}

	public void setWarningThreshold(long warningThreshold)
	{
		warningThreshold_ = warningThreshold;
	}

	public long getAlarmThreshold()
	{
		return alarmThreshold_;
	}

	public void setAlarmThreshold(long alarmThreshold)
	{
		alarmThreshold_ = alarmThreshold;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("呼び出し回数=");
		builder.append(getCount());
		builder.append(", 平均処理時間=");
		builder.append(getAverage());
		builder.append(", 最大処理時間=");
		builder.append(getMaximum());
		builder.append(", 最小処理時間=");
		builder.append(getMinimum());
		builder.append(", 例外発生回数=");
		builder.append(getThrowableCount());
		
		return builder.toString();
	}

	public int compareTo(Object arg0)
	{
		if (arg0 instanceof InvocationModel)
		{
			InvocationModel target = (InvocationModel)arg0;
			return this.getMethodName().compareTo(target.getMethodName());
		}
		
		return 0;
	}
}
