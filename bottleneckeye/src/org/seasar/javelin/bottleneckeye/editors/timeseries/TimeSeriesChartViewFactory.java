/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.timeseries;

/**
 * ���n��f�[�^��\������r���[���쐬���� Factory �N���X�B
 *
 * @author sakamoto
 */
public class TimeSeriesChartViewFactory
{

    /**
     * �w�肳�ꂽ��ނ̎��n��r���[���쐬���܂��B
     *
     * @param chartType ���n��r���[�̎��
     * @return ���n��r���[
     */
    public static TimeSeriesChartView createTimeSeriesChartView(ChartType chartType)
    {
        switch (chartType)
        {
        case CPU_RATE:
            return new CpuRateTimeSeriesChartView();
        case HEAP_MEMORY:
            return new HeapMemoryTimeSeriesChartView();
        case NONHEAP_MEMORY:
            return new NonHeapMemoryTimeSeriesChartView();
        default:
            return null;
        }
    }

}
