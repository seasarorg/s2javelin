/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.timeseries;


/**
 * ヒープ以外のメモリ使用率を表示するビュー。
 *
 * @author sakamoto
 */
public class NonHeapMemoryTimeSeriesChartView extends AbstractTimeSeriesChartView
{

    /**
     * ヒープ以外のメモリ使用率を表示するビューを作成します。
     *
     * @param chart
     */
    public NonHeapMemoryTimeSeriesChartView()
    {
        super("ヒープ以外使用量", "時刻", "使用量(MB)");
    }

}
