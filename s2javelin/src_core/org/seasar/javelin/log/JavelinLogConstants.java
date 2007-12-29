package org.seasar.javelin.log;

public interface JavelinLogConstants
{
    /**
     * 詳細情報取得キー:ThreadMXBean#getCurrentThreadCpuTimeパラメータ
     * 現在のスレッドの合計 CPU 時間をナノ秒単位で返します。
     */
    public static final String JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME          = "thread.currentThreadCpuTime";

    /**
     * 詳細情報取得キー:ThreadMXBean#getCurrentThreadCpuTimeパラメータの差分
     * 現在のスレッドの合計 CPU 時間の差分をナノ秒単位で返します。
     */
    public static final String JMXPARAM_THREAD_CURRENT_THREAD_CPU_TIME_DELTA    = "thread.currentThreadCpuTime.delta";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getCurrentThreadUserTimeパラメータ
     * 現在のスレッドがユーザモードで実行した CPU 時間 (ナノ秒単位) を返します。
     */
    public static final String JMXPARAM_THREAD_CURRENT_THREAD_USER_TIME         = "thread.currentThreadUserTime";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getCurrentThreadUserTimeパラメータの差分
     * 現在のスレッドがユーザモードで実行した CPU 時間 (ナノ秒単位) の差分を返します。
     */
    public static final String JMXPARAM_THREAD_CURRENT_THREAD_USER_TIME_DELTA   = "thread.currentThreadUserTime.delta";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getBlockedCountパラメータ
     * この ThreadInfo に関連するスレッドが、モニターに入るか、再入するのをブロックした合計回数を返します。
     */
    public static final String JMXPARAM_THREAD_THREADINFO_BLOCKED_COUNT         = "thread.threadInfo.blockedCount";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getBlockedCountパラメータの差分
     * この ThreadInfo に関連するスレッドが、モニターに入るか、再入するのをブロックした合計回数の差分を返します。
     */
    public static final String JMXPARAM_THREAD_THREADINFO_BLOCKED_COUNT_DELTA   = "thread.threadInfo.blockedCount.delta";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getBlockedTimeパラメータ
     * スレッドコンテンション監視が有効になってから、この ThreadInfo に関連するスレッドがモニターに入るか
     * 再入するのをブロックしたおよその累積経過時間 (ミリ秒単位) を返します。
     */
    public static final String JMXPARAM_THREAD_THREADINFO_BLOCKED_TIME          = "thread.threadInfo.blockedTime";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getBlockedTimeパラメータの差分
     * スレッドコンテンション監視が有効になってから、この ThreadInfo に関連するスレッドがモニターに入るか
     * 再入するのをブロックしたおよその累積経過時間 (ミリ秒単位) の差分を返します。
     */
    public static final String JMXPARAM_THREAD_THREADINFO_BLOCKED_TIME_DELTA    = "thread.threadInfo.blockedTime.delta";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getWaitedCountパラメータ
     * この ThreadInfo に関連するスレッドが通知を待機した合計回数を返します。
     */
    public static final String JMXPARAM_THREAD_THREADINFO_WAITED_COUNT          = "thread.threadInfo.waitedCount";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getWaitedCountパラメータの差分
     * この ThreadInfo に関連するスレッドが通知を待機した合計回数の差分を返します。
     */
    public static final String JMXPARAM_THREAD_THREADINFO_WAITED_COUNT_DELTA    = "thread.threadInfo.waitedCount.delta";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getWaitedTimeパラメータ
     * スレッドコンテンション監視が有効になってから、この ThreadInfo に関連するスレッドが通知を待機した
     * およその累積経過時間 (ミリ秒単位) を返します。
     */
    public static final String JMXPARAM_THREAD_THREADINFO_WAITED_TIME           = "thread.threadInfo.waitedTime";

    /** 
     * 詳細情報取得キー:ThreadMXBean#getThreadInfo#getWaitedTimeパラメータの差分
     * スレッドコンテンション監視が有効になってから、この ThreadInfo に関連するスレッドが通知を待機した
     * およその累積経過時間 (ミリ秒単位) の差分を返します。
     */
    public static final String JMXPARAM_THREAD_THREADINFO_WAITED_TIME_DELTA     = "thread.threadInfo.waitedTime.delta";

    /** 
     * 詳細情報取得キー:GarbageCollectorMXBean#getCollectionCountパラメータ
     *発生したコレクションの合計数を返します。
     */
    public static final String JMXPARAM_GARBAGECOLLECTOR_COLLECTION_COUNT       = "garbageCollector.collectionCount";

    /** 
     * 詳細情報取得キー:GarbageCollectorMXBean#getCollectionCountパラメータの差分
     * 発生したコレクションの合計数を返します。
     */
    public static final String JMXPARAM_GARBAGECOLLECTOR_COLLECTION_COUNT_DELTA = "garbageCollector.collectionCount.delta";

    /** 
     * 詳細情報取得キー:GarbageCollectorMXBean#getCollectionTimeパラメータ
     * コレクションのおよその累積経過時間 (ミリ秒単位) を返します。
     */
    public static final String JMXPARAM_GARBAGECOLLECTOR_COLLECTION_TIME        = "garbageCollector.collectionTime";

    /** 
     * 詳細情報取得キー:GarbageCollectorMXBean#getCollectionTimeパラメータの差分
     * コレクションのおよその累積経過時間 (ミリ秒単位) を返します。
     */
    public static final String JMXPARAM_GARBAGECOLLECTOR_COLLECTION_TIME_DELTA  = "garbageCollector.collectionTime.delta";

    /** 
     * 詳細情報取得キー:MemoryPoolMXBean#getPeakUsage#getUsageパラメータ
     * Java 仮想マシンが起動されてから、またはピークがリセットされてからの、このメモリプールのピークメモリ使用量を返します
     */
    public static final String JMXPARAM_MEMORYPOOL_PEAKUSAGE_USAGE              = "memoryPool.peakUsage.usage";

    /** 
     * 詳細情報取得キー:MemoryPoolMXBean#getPeakUsage#getUsageパラメータの差分
     * Java 仮想マシンが起動されてから、またはピークがリセットされてからの、このメモリプールのピークメモリ使用量を返します
     */
    public static final String JMXPARAM_MEMORYPOOL_PEAKUSAGE_USAGE_DELTA        = "memoryPool.peakUsage.usage.delta";

    /** 
     * 詳細情報取得キー:メソッドのTAT(ミリ秒単位)
     */
    public static final String EXTRAPARAM_DURATION                              = "duration";

    /** 例外生成タグ。*/
    public static final String JAVELIN_EXCEPTION                                = "<<javelin.Exception>>";

    /** スタックトレース出力の開始タグ。*/
    public static final String JAVELIN_STACKTRACE_START                         = "<<javelin.StackTrace_START>>";

    /** スタックトレース出力の終了タグ。*/
    public static final String JAVELIN_STACKTRACE_END                           = "<<javelin.StackTrace_END>>";

    /** フィールド値出力の開始タグ。*/
    public static final String JAVELIN_FIELDVALUE_START                         = "<<javelin.FieldValue_START>>";

    /** フィールド値出力の終了タグ。*/
    public static final String JAVELIN_FIELDVALUE_END                           = "<<javelin.FieldValue_END>>";

    /** 戻り値出力の開始タグ。*/
    public static final String JAVELIN_RETURN_START                             = "<<javelin.Return_START>>";

    /** 戻り値出力の終了タグ。*/
    public static final String JAVELIN_RETURN_END                               = "<<javelin.Return_END>>";

    /** 引数出力の開始タグ。*/
    public static final String JAVELIN_ARGS_START                               = "<<javelin.Args_START>>";

    /** 引数出力の終了タグ。*/
    public static final String JAVELIN_ARGS_END                                 = "<<javelin.Args_END>>";

    /** JMXにより取得したVMの状態出力の開始タグ。*/
    public static final String JAVELIN_JMXINFO_START                            = "<<javelin.JMXInfo_START>>";

    /** JMXにより取得したVMの状態出力の終了タグ。*/
    public static final String JAVELIN_JMXINFO_END                              = "<<javelin.JMXInfo_END>>";

    /** 追加情報出力の開始タグ。*/
    public static final String JAVELIN_EXTRAINFO_START                          = "<<javelin.ExtraInfo_START>>";

    /** 追加情報出力の終了タグ。*/
    public static final String JAVELIN_EXTRAINFO_END                            = "<<javelin.ExtraInfo_END>>";

    /** 動作ログ出力日時のフォーマット。*/
    public static final String DATE_PATTERN                                     = "yyyy/MM/dd HH:mm:ss.SSS";

}
