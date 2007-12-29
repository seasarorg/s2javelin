package org.seasar.javelin.log;

public interface JavelinLogConstants
{
    /**
     * �ڍ׏��擾�L�[:ThreadMXBean#getCurrentThreadCpuTime�p�����[�^
     * ���݂̃X���b�h�̍��v CPU ���Ԃ��i�m�b�P�ʂŕԂ��܂��B
     */
    public static final String JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME          = "thread.currentThreadCpuTime";

    /**
     * �ڍ׏��擾�L�[:ThreadMXBean#getCurrentThreadCpuTime�p�����[�^�̍���
     * ���݂̃X���b�h�̍��v CPU ���Ԃ̍������i�m�b�P�ʂŕԂ��܂��B
     */
    public static final String JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME_DELTA    = "thread.currentThreadCpuTime.delta";

    /** 
     * �ڍ׏��擾�L�[:ThreadMXBean#getCurrentThreadUserTime�p�����[�^
     * ���݂̃X���b�h�����[�U���[�h�Ŏ��s���� CPU ���� (�i�m�b�P��) ��Ԃ��܂��B
     */
    public static final String JMXPARAM_THREAD_CURRENT_THREAD_USER_TIME         = "thread.currentThreadUserTime";

    /** 
     * �ڍ׏��擾�L�[:ThreadMXBean#getCurrentThreadUserTime�p�����[�^�̍���
     * ���݂̃X���b�h�����[�U���[�h�Ŏ��s���� CPU ���� (�i�m�b�P��) �̍�����Ԃ��܂��B
     */
    public static final String JMXPARAM_THREAD_CURRENT_THREAD_USER_TIME_DELTA   = "thread.currentThreadUserTime.delta";

    /** 
     * �ڍ׏��擾�L�[:ThreadMXBean#getThreadInfo#getBlockedCount�p�����[�^
     * ���� ThreadInfo �Ɋ֘A����X���b�h���A���j�^�[�ɓ��邩�A�ē�����̂��u���b�N�������v�񐔂�Ԃ��܂��B
     */
    public static final String JMXPARAM_THREAD_THREADINFO_BLOCKED_COUNT         = "thread.threadInfo.blockedCount";

    /** 
     * �ڍ׏��擾�L�[:ThreadMXBean#getThreadInfo#getBlockedCount�p�����[�^�̍���
     * ���� ThreadInfo �Ɋ֘A����X���b�h���A���j�^�[�ɓ��邩�A�ē�����̂��u���b�N�������v�񐔂̍�����Ԃ��܂��B
     */
    public static final String JMXPARAM_THREAD_THREADINFO_BLOCKED_COUNT_DELTA   = "thread.threadInfo.blockedCount.delta";

    /** 
     * �ڍ׏��擾�L�[:ThreadMXBean#getThreadInfo#getBlockedTime�p�����[�^
     * �X���b�h�R���e���V�����Ď����L���ɂȂ��Ă���A���� ThreadInfo �Ɋ֘A����X���b�h�����j�^�[�ɓ��邩
     * �ē�����̂��u���b�N�������悻�̗ݐόo�ߎ��� (�~���b�P��) ��Ԃ��܂��B
     */
    public static final String JMXPARAM_THREAD_THREADINFO_BLOCKED_TIME          = "thread.threadInfo.blockedTime";

    /** 
     * �ڍ׏��擾�L�[:ThreadMXBean#getThreadInfo#getBlockedTime�p�����[�^�̍���
     * �X���b�h�R���e���V�����Ď����L���ɂȂ��Ă���A���� ThreadInfo �Ɋ֘A����X���b�h�����j�^�[�ɓ��邩
     * �ē�����̂��u���b�N�������悻�̗ݐόo�ߎ��� (�~���b�P��) �̍�����Ԃ��܂��B
     */
    public static final String JMXPARAM_THREAD_THREADINFO_BLOCKED_TIME_DELTA    = "thread.threadInfo.blockedTime.delta";

    /** 
     * �ڍ׏��擾�L�[:ThreadMXBean#getThreadInfo#getWaitedCount�p�����[�^
     * ���� ThreadInfo �Ɋ֘A����X���b�h���ʒm��ҋ@�������v�񐔂�Ԃ��܂��B
     */
    public static final String JMXPARAM_THREAD_THREADINFO_WAITED_COUNT          = "thread.threadInfo.waitedCount";

    /** 
     * �ڍ׏��擾�L�[:ThreadMXBean#getThreadInfo#getWaitedCount�p�����[�^�̍���
     * ���� ThreadInfo �Ɋ֘A����X���b�h���ʒm��ҋ@�������v�񐔂̍�����Ԃ��܂��B
     */
    public static final String JMXPARAM_THREAD_THREADINFO_WAITED_COUNT_DELTA    = "thread.threadInfo.waitedCount.delta";

    /** 
     * �ڍ׏��擾�L�[:ThreadMXBean#getThreadInfo#getWaitedTime�p�����[�^
     * �X���b�h�R���e���V�����Ď����L���ɂȂ��Ă���A���� ThreadInfo �Ɋ֘A����X���b�h���ʒm��ҋ@����
     * ���悻�̗ݐόo�ߎ��� (�~���b�P��) ��Ԃ��܂��B
     */
    public static final String JMXPARAM_THREAD_THREADINFO_WAITED_TIME           = "thread.threadInfo.waitedTime";

    /** 
     * �ڍ׏��擾�L�[:ThreadMXBean#getThreadInfo#getWaitedTime�p�����[�^�̍���
     * �X���b�h�R���e���V�����Ď����L���ɂȂ��Ă���A���� ThreadInfo �Ɋ֘A����X���b�h���ʒm��ҋ@����
     * ���悻�̗ݐόo�ߎ��� (�~���b�P��) �̍�����Ԃ��܂��B
     */
    public static final String JMXPARAM_THREAD_THREADINFO_WAITED_TIME_DELTA     = "thread.threadInfo.waitedTime.delta";

    /** 
     * �ڍ׏��擾�L�[:GarbageCollectorMXBean#getCollectionCount�p�����[�^
     *���������R���N�V�����̍��v����Ԃ��܂��B
     */
    public static final String JMXPARAM_GARBAGECOLLECTOR_COLLECTION_COUNT       = "garbageCollector.collectionCount";

    /** 
     * �ڍ׏��擾�L�[:GarbageCollectorMXBean#getCollectionCount�p�����[�^�̍���
     * ���������R���N�V�����̍��v����Ԃ��܂��B
     */
    public static final String JMXPARAM_GARBAGECOLLECTOR_COLLECTION_COUNT_DELTA = "garbageCollector.collectionCount.delta";

    /** 
     * �ڍ׏��擾�L�[:GarbageCollectorMXBean#getCollectionTime�p�����[�^
     * �R���N�V�����̂��悻�̗ݐόo�ߎ��� (�~���b�P��) ��Ԃ��܂��B
     */
    public static final String JMXPARAM_GARBAGECOLLECTOR_COLLECTION_TIME        = "garbageCollector.collectionTime";

    /** 
     * �ڍ׏��擾�L�[:GarbageCollectorMXBean#getCollectionTime�p�����[�^�̍���
     * �R���N�V�����̂��悻�̗ݐόo�ߎ��� (�~���b�P��) ��Ԃ��܂��B
     */
    public static final String JMXPARAM_GARBAGECOLLECTOR_COLLECTION_TIME_DELTA  = "garbageCollector.collectionTime.delta";

    /** 
     * �ڍ׏��擾�L�[:MemoryPoolMXBean#getPeakUsage#getUsage�p�����[�^
     * Java ���z�}�V�����N������Ă���A�܂��̓s�[�N�����Z�b�g����Ă���́A���̃������v�[���̃s�[�N�������g�p�ʂ�Ԃ��܂�
     */
    public static final String JMXPARAM_MEMORYPOOL_PEAKUSAGE_USAGE              = "memoryPool.peakUsage.usage";

    /** 
     * �ڍ׏��擾�L�[:MemoryPoolMXBean#getPeakUsage#getUsage�p�����[�^�̍���
     * Java ���z�}�V�����N������Ă���A�܂��̓s�[�N�����Z�b�g����Ă���́A���̃������v�[���̃s�[�N�������g�p�ʂ�Ԃ��܂�
     */
    public static final String JMXPARAM_MEMORYPOOL_PEAKUSAGE_USAGE_DELTA        = "memoryPool.peakUsage.usage.delta";

    /** 
     * �ڍ׏��擾�L�[:���\�b�h��TAT(�~���b�P��)
     */
    public static final String EXTRAPARAM_DURATION                              = "duration";

    /** ��O�����^�O�B*/
    public static final String JAVELIN_EXCEPTION                                = "<<javelin.Exception>>";

    /** �X�^�b�N�g���[�X�o�͂̊J�n�^�O�B*/
    public static final String JAVELIN_STACKTRACE_START                         = "<<javelin.StackTrace_START>>";

    /** �X�^�b�N�g���[�X�o�͂̏I���^�O�B*/
    public static final String JAVELIN_STACKTRACE_END                           = "<<javelin.StackTrace_END>>";

    /** �t�B�[���h�l�o�͂̊J�n�^�O�B*/
    public static final String JAVELIN_FIELDVALUE_START                         = "<<javelin.FieldValue_START>>";

    /** �t�B�[���h�l�o�͂̏I���^�O�B*/
    public static final String JAVELIN_FIELDVALUE_END                           = "<<javelin.FieldValue_END>>";

    /** �߂�l�o�͂̊J�n�^�O�B*/
    public static final String JAVELIN_RETURN_START                             = "<<javelin.Return_START>>";

    /** �߂�l�o�͂̏I���^�O�B*/
    public static final String JAVELIN_RETURN_END                               = "<<javelin.Return_END>>";

    /** �����o�͂̊J�n�^�O�B*/
    public static final String JAVELIN_ARGS_START                               = "<<javelin.Args_START>>";

    /** �����o�͂̏I���^�O�B*/
    public static final String JAVELIN_ARGS_END                                 = "<<javelin.Args_END>>";

    /** JMX�ɂ��擾����VM�̏�ԏo�͂̊J�n�^�O�B*/
    public static final String JAVELIN_JMXINFO_START                            = "<<javelin.JMXInfo_START>>";

    /** JMX�ɂ��擾����VM�̏�ԏo�͂̏I���^�O�B*/
    public static final String JAVELIN_JMXINFO_END                              = "<<javelin.JMXInfo_END>>";

    /** �ǉ����o�͂̊J�n�^�O�B*/
    public static final String JAVELIN_EXTRAINFO_START                          = "<<javelin.ExtraInfo_START>>";

    /** �ǉ����o�͂̏I���^�O�B*/
    public static final String JAVELIN_EXTRAINFO_END                            = "<<javelin.ExtraInfo_END>>";

    /** ���샍�O�o�͓����̃t�H�[�}�b�g�B*/
    public static final String DATE_PATTERN                                     = "yyyy/MM/dd HH:mm:ss.SSS";

}
