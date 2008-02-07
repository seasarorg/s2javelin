/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.timeseries;


/**
 * ヒープメモリ使用率を表示するビュー。
 *
 * @author sakamoto
 */
public class HeapMemoryTimeSeriesChartView extends AbstractTimeSeriesChartView
{

    /**
     * ヒープメモリ使用率を表示するビューを作成します。
     *
     * @param chart
     */
    public HeapMemoryTimeSeriesChartView()
    {
        super("ヒープ使用量", "時刻", "使用量(MB)");
    }

}
