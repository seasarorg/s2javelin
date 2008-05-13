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

    /** ���\�b�h�̌Ăяo���񐔁B */
    private long                count_;

    /** ���\�b�h�̍ŒZ�������ԁi�P��:�~���b�j�B */
    private long                minimum_;

    /** ���\�b�h�̍Œ��������ԁi�P��:�~���b�j�B */
    private long                maximum_;

    /** ���\�b�h�̕��Ϗ������ԁi�P��:�~���b�j�B */
    private long                average_;

    /** ���\�b�h�̍ŒZCPU�������ԁi�P��:�~���b�j�B */
    private long                cpuMinimum_;

    /** ���\�b�h�̍Œ�CPU�������ԁi�P��:�~���b�j�B */
    private long                cpuMaximum_;

    /** ���\�b�h�̕���CPU�������ԁi�P��:�~���b�j�B */
    private long                cpuAverage_;


    /** ���\�b�h�̍ŒZCPU�������ԁi�P��:�~���b�j�B */
    private long                userMinimum_;

    /** ���\�b�h�̍Œ�CPU�������ԁi�P��:�~���b�j�B */
    private long                userMaximum_;

    /** ���\�b�h�̕���CPU�������ԁi�P��:�~���b�j�B */
    private long                userAverage_;
    
    
    /** ���\�b�h���ł̗�O�����񐔁B */
    private long                throwableCount_;

    /** ���\�b�h���Ŕ���������O�̗����B */
    private List<Throwable>     throwableList_;

    /**  */
    private long                warningThreshold_      = Long.MAX_VALUE;

    /**  */
    private long                alarmThreshold_        = Long.MAX_VALUE;

    /** 臒l����p�̒萔������ */
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
        builder.append("�Ăяo����=");
        builder.append(getCount());
        builder.append(", ���Ϗ�������=");
        builder.append(getAverage());
        builder.append(", �ő又������=");
        builder.append(getMaximum());
        builder.append(", �ŏ���������=");
        builder.append(getMinimum());
        builder.append(", ��O������=");
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
                // �擾������A���InvocationModel������āA�f�[�^��ݒ肷��
                InvocationModel invocation = new InvocationModel();
                invocation.setDate(new Date());
                // �Ώۖ����A�N���X���A���\�b�h�����擾����
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

            // �S�ČĂяo������ݒ�p
            // �f�[�^��InvocationModel�ɐݒ肷��
            String strItemName = ((ResponseBody)objBody[index]).getStrItemName();
            Object[] objTempArr = ((ResponseBody)objBody[index]).getObjItemValueArr();

            // �����̔z�񒷂�0�ł���΃X�L�b�v����
            if (objTempArr.length == 0)
            {
                continue;
            }
            
            // �����̔z��̐擪�v�f��Long�łȂ���΃X�L�b�v����
            if (objTempArr[0] instanceof Long == false)
            {
                continue;
            }
            
            // �Ăяo����
            if ("callCount".equals(strItemName))
                invocation.setCount((Long)objTempArr[0]);
            // ���ώ���
            if ("averageInterval".equals(strItemName))
                invocation.setAverage((Long)objTempArr[0]);
            // �ő又������
            if ("maximumInterval".equals(strItemName))
                invocation.setMaximum((Long)objTempArr[0]);
            // �ŏ���������
            if ("minimumInterval".equals(strItemName))
                invocation.setMinimum((Long)objTempArr[0]);
            // ����CPU����
            if ("averageCpuInterval".equals(strItemName))
                invocation.setCpuAverage((Long)objTempArr[0]/1000000);
            // �ő�CPU����
            if ("maximumCpuInterval".equals(strItemName))
                invocation.setCpuMaximum((Long)objTempArr[0]/1000000);
            // �ŏ�USER����
            if ("minimumCpuInterval".equals(strItemName))
                invocation.setCpuMinimum((Long)objTempArr[0]/1000000);
            // ����USER����
            if ("averageUserInterval".equals(strItemName))
                invocation.setUserAverage((Long)objTempArr[0]/1000000);
            // �ő�USER����
            if ("maximumUserInterval".equals(strItemName))
                invocation.setUserMaximum((Long)objTempArr[0]/1000000);
            // �ŏ�USER����
            if ("minimumUserInterval".equals(strItemName))
                invocation.setUserMinimum((Long)objTempArr[0]/1000000);
            // ��O������
            if ("throwableCount".equals(strItemName))
                invocation.setThrowableCount((Long)objTempArr[0]);
            // ���\�b�h�̌Ăяo���� �N���X���ɂ��Ă͖�����
            if ("allCallerNames".equals(strItemName))
            {
                // ������
            }
            invocation.setAlarmThreshold(alarmThreshold);
            invocation.setWarningThreshold(warningThreshold);
        }

        return (InvocationModel[])invocationMap.values().toArray(new InvocationModel[0]);
    }
}
