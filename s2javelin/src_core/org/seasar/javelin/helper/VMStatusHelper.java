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
 * JMX����VM�̏�Ԃ��擾���A�L�^���邽�߂Ɏg�p����B
 * 
 * �ȉ���MXBean����A�v���p�e�B���擾����B<br>
 * <br>
 * <table border="1" cellspacing="0">
 * <tr>
 *  <th>MXBean����</th>
 *  <th>�v���p�e�B</th>
 *  <th>���l</th>
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
 *  <td>GarbageCollector����������ꍇ�́A�S�Ă�GarbageCollector�̒l�̍��v���g�p����B&nbsp;</td>
 * </tr>
 * <tr>
 *  <td>MemoryPoolMXBean</td>
 *  <td>
 *      peakUsage.usage<br>
 *  </td>
 *  <td>
 *      MemoryPool����������ꍇ�́A�S�Ă�MemoryPool�̒l�̍��v���g�p����B<br>
 *      �s�[�N�g�p�ʂ̃��Z�b�g�́ACallTree�̏��ThreadLocal�ɂȂ��ꍇ�Ɏ��{����B&nbsp;
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
     * ���������s���B
     * �X���b�h��CPU���ԁA�҂����Ԃ�JMX�Ŏ擾���邽�߂̃t���O��true�ɂ���B
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
     * MemoryPoolMXBean���ׂĂɑ΂��A�s�[�N�������g�p�ʂ����Z�b�g����B
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
     * JMX����VM�̏�Ԃ��擾���AVMStatus�I�u�W�F�N�g�𐶐�����B<br>
     * 
     * @return JMX����VM�̏�Ԃ��擾�����AVMStatus�I�u�W�F�N�g�B
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
     * JMX����VM�̏�Ԃ��擾���AVMStatus�I�u�W�F�N�g�𐶐�����B<br>
     * 
     * @return JMX����VM�̏�Ԃ��擾�����AVMStatus�I�u�W�F�N�g�B
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
        // ���̒l�͌��ݗ��p���Ă��Ȃ����߃R�����g�A�E�g����B
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
