/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.timeseries;

/**
 * ���n��O���t�̎�ށB
 *
 * @author sakamoto
 */
public enum ChartType
{

    /** CPU�g�p�� */
    CPU_RATE,

    /** �����������g�p�� */
    PHYSICAL_MEMORY,

    /** ���z�������g�p�� */
    VIRTUAL_MEMORY,

    /** ���z�}�V���̃������g�p�� */
    VIRTUAL_MACHINE_MEMORY,

    /** �q�[�v�̃������g�p�� */
    HEAP_MEMORY,

    /** �q�[�v�ȊO�̃������g�p�� */
    NONHEAP_MEMORY,

    /** �X���b�v�̎g�p�� */
    SWAP_MEMORY

}
