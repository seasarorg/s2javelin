package org.seasar.javelin.bottleneckeye.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationListener;

import org.seasar.javelin.bottleneckeye.communicate.Body;
import org.seasar.javelin.bottleneckeye.communicate.ResponseBody;
import org.seasar.javelin.bottleneckeye.communicate.Telegram;

public class InvocationModel implements Comparable, NotificationListener
{
    private static final int    INDEX_METHODNAME       = 1;

    private static final int    INDEX_CLASSNAME        = 0;

    public static final String CLASSMETHOD_SEPARATOR  = "###CLASSMETHOD_SEPARATOR###";

    private ComponentModel      component_;

    private String              targetAddress_;

    private String              className_;

    private String              methodName_;

    private Date                date_;

    /** メソッドの呼び出し回数。 */
    private long                count_;

    /** メソッドの最短処理時間（単位:ミリ秒）。 */
    private long                minimum_;

    /** メソッドの最長処理時間（単位:ミリ秒）。 */
    private long                maximum_;

    /** メソッドの平均処理時間（単位:ミリ秒）。 */
    private long                average_;

    /** メソッドの最短CPU処理時間（単位:ミリ秒）。 */
    private long                cpuMinimum_;

    /** メソッドの最長CPU処理時間（単位:ミリ秒）。 */
    private long                cpuMaximum_;

    /** メソッドの平均CPU処理時間（単位:ミリ秒）。 */
    private long                cpuAverage_;


    /** メソッドの最短CPU処理時間（単位:ミリ秒）。 */
    private long                userMinimum_;

    /** メソッドの最長CPU処理時間（単位:ミリ秒）。 */
    private long                userMaximum_;

    /** メソッドの平均CPU処理時間（単位:ミリ秒）。 */
    private long                userAverage_;
    
    
    /** メソッド内での例外発生回数。 */
    private long                throwableCount_;

    /** メソッド内で発生した例外の履歴。 */
    private List<Throwable>     throwableList_;

    /**  */
    private long                warningThreshold_      = Long.MAX_VALUE;

    /**  */
    private long                alarmThreshold_        = Long.MAX_VALUE;

    /** 閾値判定用の定数文字列 */
    private static final String EXCEED_THRESHOLD_ALARM = "Alarm:EXCEED_THRESHOLD";

    public String getClassName()
    {
        return className_;
    }

    public void setClassName(String className_)
    {
        this.className_ = className_;
    }

    public String getTargetAddress()
    {
        return targetAddress_;
    }

    public void setTargetAddress(String targetAddress_)
    {
        this.targetAddress_ = targetAddress_;
    }

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

    public long getCpuMinimum()
    {
        return this.cpuMinimum_;
    }

    public void setCpuMinimum(long cpuMinimum)
    {
        this.cpuMinimum_ = cpuMinimum;
    }

    public long getCpuMaximum()
    {
        return this.cpuMaximum_;
    }

    public void setCpuMaximum(long cpuMaximum)
    {
        this.cpuMaximum_ = cpuMaximum;
    }

    public long getCpuAverage()
    {
        return this.cpuAverage_;
    }

    public void setCpuAverage(long cpuAverage)
    {
        this.cpuAverage_ = cpuAverage;
    }


    public long getUserMinimum()
    {
        return this.userMinimum_;
    }

    public void setUserMinimum(long userMinimum)
    {
        this.userMinimum_ = userMinimum;
    }

    public long getUserMaximum()
    {
        return this.userMaximum_;
    }

    public void setUserMaximum(long userMaximum)
    {
        this.userMaximum_ = userMaximum;
    }

    public long getUserAverage()
    {
        return this.userAverage_;
    }

    public void setUserAverage(long userAverage)
    {
        this.userAverage_ = userAverage;
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

    public Date getDate()
    {
        return date_;
    }

    public void setDate(Date date)
    {
        this.date_ = date;
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

    public void handleNotification(Notification notification, Object handback)
    {
        if (notification instanceof AttributeChangeNotification)
        {
            String alarmMsg = ((AttributeChangeNotification)notification).getMessage();
            if (EXCEED_THRESHOLD_ALARM.equals(alarmMsg) == true)
            {
                handleExceedThresholdAlarm((AttributeChangeNotification)notification);
            }
        }

    }

    private void handleExceedThresholdAlarm(AttributeChangeNotification notification)
    {
        setAverage((Long)notification.getOldValue());
        setMaximum((Long)notification.getNewValue());
        this.component_.setExceededThresholdAlarm(this.methodName_);
    }

    public static InvocationModel[] createFromTelegram(Telegram telegram, long alarmThreshold,
            long warningThreshold)
    {
        Map invocationMap = new HashMap();

        Body[] objBody = telegram.getObjBody();
        for (int index = 0; index < objBody.length; index++)
        {
            ResponseBody responseBody = (ResponseBody)objBody[index];

            if (invocationMap.containsKey(responseBody.getStrObjName()) == false)
            {
                // 取得した後、一つInvocationModelを作って、データを設定する
                InvocationModel invocation = new InvocationModel();
                invocation.setDate(new Date());
                // 対象名より、クラス名、メソッド名を取得する
                String strClassMethodName = (responseBody).getStrObjName();
                String[] strClassMethodNameArr = strClassMethodName.split(CLASSMETHOD_SEPARATOR);
                String strClassName = "unknown";
                String strMethodName = "unknown";
                if (strClassMethodNameArr.length > INDEX_METHODNAME)
                {
                    strClassName = strClassMethodNameArr[INDEX_CLASSNAME];
                    strMethodName = strClassMethodNameArr[INDEX_METHODNAME];
                }
                invocation.setClassName(strClassName);
                invocation.setMethodName(strMethodName);

                invocationMap.put(responseBody.getStrObjName(), invocation);
            }

            InvocationModel invocation = (InvocationModel)invocationMap.get(responseBody.getStrObjName());

            // 全て呼び出す元を設定用
            // データをInvocationModelに設定する
            String strItemName = ((ResponseBody)objBody[index]).getStrItemName();
            Object[] objTempArr = ((ResponseBody)objBody[index]).getObjItemValueArr();

            // 説明の配列長が0であればスキップする
            if (objTempArr.length == 0)
            {
                continue;
            }
            
            // 説明の配列の先頭要素がLongでなければスキップする
            if (objTempArr[0] instanceof Long == false)
            {
                continue;
            }
            
            // 呼び出し回数
            if ("callCount".equals(strItemName))
                invocation.setCount((Long)objTempArr[0]);
            // 平均時間
            if ("averageInterval".equals(strItemName))
                invocation.setAverage((Long)objTempArr[0]);
            // 最大処理時間
            if ("maximumInterval".equals(strItemName))
                invocation.setMaximum((Long)objTempArr[0]);
            // 最小処理時間
            if ("minimumInterval".equals(strItemName))
                invocation.setMinimum((Long)objTempArr[0]);
            // 平均CPU時間
            if ("averageCpuInterval".equals(strItemName))
                invocation.setCpuAverage((Long)objTempArr[0]/1000000);
            // 最大CPU時間
            if ("maximumCpuInterval".equals(strItemName))
                invocation.setCpuMaximum((Long)objTempArr[0]/1000000);
            // 最小USER時間
            if ("minimumCpuInterval".equals(strItemName))
                invocation.setCpuMinimum((Long)objTempArr[0]/1000000);
            // 平均USER時間
            if ("averageUserInterval".equals(strItemName))
                invocation.setUserAverage((Long)objTempArr[0]/1000000);
            // 最大USER時間
            if ("maximumUserInterval".equals(strItemName))
                invocation.setUserMaximum((Long)objTempArr[0]/1000000);
            // 最小USER時間
            if ("minimumUserInterval".equals(strItemName))
                invocation.setUserMinimum((Long)objTempArr[0]/1000000);
            // 例外発生回数
            if ("throwableCount".equals(strItemName))
                invocation.setThrowableCount((Long)objTempArr[0]);
            // メソッドの呼び出し元 クラス名については未実装
            if ("allCallerNames".equals(strItemName))
            {
                // 未実装
            }
            invocation.setAlarmThreshold(alarmThreshold);
            invocation.setWarningThreshold(warningThreshold);
        }

        return (InvocationModel[])invocationMap.values().toArray(new InvocationModel[0]);
    }
}
