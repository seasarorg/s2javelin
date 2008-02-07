/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.timeseries;

/**
 * 時系列データを表示するビューを作成する Factory クラス。
 *
 * @author sakamoto
 */
public class TimeSeriesChartViewFactory
{

    /**
     * 指定された種類の時系列ビューを作成します。
     *
     * @param chartType 時系列ビューの種類
     * @return 時系列ビュー
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
