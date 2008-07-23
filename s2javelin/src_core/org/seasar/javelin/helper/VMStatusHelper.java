package org.seasar.javelin.helper;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;

import org.seasar.javelin.S2JavelinConfig;
import org.seasar.javelin.VMStatus;

/**
 * JMXからVMの状態を取得し、記録するために使用する。
 * 
 * 以下のMXBeanから、プロパティを取得する。<br>
 * <br>
 * <table border="1" cellspacing="0">
 * <tr>
 *  <th>MXBean名称</th>
 *  <th>プロパティ</th>
 *  <th>備考</th>
 * </tr>
 * <tr>
 *  <td>ThreadMXBean</td>
 *  <td>
 *      currentThreadCpuTime<br>
 *      currentThreadUserTime<br>
 *      threadInfo.blockedCount<br>
 *      threadInfo.blockedTime<br>
 *      threadInfo.waitedCount<br>
 *      threadInfo.waitedTime<br>
 *  </td>
 *  <td>&nbsp;</td>
 * </tr>
 * <tr>
 *  <td>GarbageCollectorMXBean</td>
 *  <td>
 *      collectionCount<br>
 *      collectionTime<br>
 *  </td>
 *  <td>GarbageCollectorが複数ある場合は、全てのGarbageCollectorの値の合計を使用する。&nbsp;</td>
 * </tr>
 * <tr>
 *  <td>MemoryPoolMXBean</td>
 *  <td>
 *      peakUsage.usage<br>
 *  </td>
 *  <td>
 *      MemoryPoolが複数ある場合は、全てのMemoryPoolの値の合計を使用する。<br>
 *      ピーク使用量のリセットは、CallTreeの情報がThreadLocalにない場合に実施する。&nbsp;
 *  </td>
 * </tr>
 * </table>
 * 
 * @author eriguchi
 *
 */
public class VMStatusHelper
{
    private static RuntimeMXBean                 runtimeMBean_                = ManagementFactory.getRuntimeMXBean();

	private ThreadMXBean                 threadMBean                 = ManagementFactory.getThreadMXBean();

    private List<GarbageCollectorMXBean> garbageCollectorMXBeanList_ = ManagementFactory.getGarbageCollectorMXBeans();

    private List<MemoryPoolMXBean>       memoryPoolMXBeanList_       = ManagementFactory.getMemoryPoolMXBeans();

    private S2JavelinConfig              javelinConfig               = new S2JavelinConfig();

    /**
     * 初期化を行う。
     * スレッドのCPU時間、待ち時間をJMXで取得するためのフラグをtrueにする。
     */
    public void init()
    {
        if(this.threadMBean.isThreadContentionMonitoringEnabled())
        {
            this.threadMBean.setThreadContentionMonitoringEnabled(true);
        }
        if(this.threadMBean.isThreadCpuTimeEnabled())
        {
            this.threadMBean.setThreadCpuTimeEnabled(true);
        }
    }

    /**
     * MemoryPoolMXBeanすべてに対し、ピークメモリ使用量をリセットする。
     *
     */
    public void resetPeakMemoryUsage()
    {
        if (false == this.javelinConfig.isLogMBeanInfo())
        {
            return;
        }

        for (MemoryPoolMXBean memoryPoolMXBean : this.memoryPoolMXBeanList_)
        {
            memoryPoolMXBean.resetPeakUsage();
        }
    }

    /**
     * JMXからVMの状態を取得し、VMStatusオブジェクトを生成する。<br>
     * 
     * @return JMXからVMの状態を取得した、VMStatusオブジェクト。
     */
    public VMStatus createVMStatus()
    {

        if (false == this.javelinConfig.isLogMBeanInfo())
        {
            return new VMStatus();
        }

        return createVMStatusForce();
    }

    /**
     * JMXからVMの状態を取得し、VMStatusオブジェクトを生成する。<br>
     * 
     * @return JMXからVMの状態を取得した、VMStatusオブジェクト。
     */
    public VMStatus createVMStatusForce()
    {
        VMStatus vmStatus = new VMStatus();
        vmStatus.setCpuTime(this.threadMBean.getCurrentThreadCpuTime());
        vmStatus.setUserTime(this.threadMBean.getCurrentThreadUserTime());
        long threadId = Thread.currentThread().getId();
        if(threadId != 0)
        {
            ThreadInfo threadInfo = this.threadMBean.getThreadInfo(threadId);
            vmStatus.setBlockedTime(threadInfo.getBlockedTime());
            vmStatus.setBlockedCount(threadInfo.getBlockedCount());
            vmStatus.setWaitedTime(threadInfo.getWaitedTime());
            vmStatus.setWaitedCount(threadInfo.getWaitedCount());
        }

        long collectionCount = 0;
        long collectionTime = 0;
        for (GarbageCollectorMXBean garbageCollectorMXBean : this.garbageCollectorMXBeanList_)
        {
            collectionCount += garbageCollectorMXBean.getCollectionCount();
            collectionTime += garbageCollectorMXBean.getCollectionTime();
        }

        vmStatus.setCollectionCount(collectionCount);
        vmStatus.setCollectionTime(collectionTime);

        long peakMemoryUsage = 0;
        // この値は現在利用していないためコメントアウトする。
//        for (MemoryPoolMXBean memoryPoolMXBean : this.memoryPoolMXBeanList_)
//        {
//            MemoryUsage peakUsage = memoryPoolMXBean.getPeakUsage();
//            if (peakUsage != null)
//            {
//                peakMemoryUsage += peakUsage.getUsed();
//            }
//        }
        vmStatus.setPeakMamoryUsage(peakMemoryUsage);

        return vmStatus;
    }

    
    public static String getProcessName()
    {
    	return runtimeMBean_.getName();
    }
}
