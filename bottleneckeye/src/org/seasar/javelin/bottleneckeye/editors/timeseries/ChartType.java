/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.timeseries;

/**
 * 時系列グラフの種類。
 *
 * @author sakamoto
 */
public enum ChartType
{

    /** CPU使用率 */
    CPU_RATE,

    /** 物理メモリ使用量 */
    PHYSICAL_MEMORY,

    /** 仮想メモリ使用量 */
    VIRTUAL_MEMORY,

    /** 仮想マシンのメモリ使用量 */
    VIRTUAL_MACHINE_MEMORY,

    /** ヒープのメモリ使用量 */
    HEAP_MEMORY,

    /** ヒープ以外のメモリ使用量 */
    NONHEAP_MEMORY,

    /** スワップの使用量 */
    SWAP_MEMORY

}
