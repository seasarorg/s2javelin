/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.timeseries;


/**
 * CPU使用率を表示するビュー。
 *
 * @author sakamoto
 */
public class CpuRateTimeSeriesChartView extends AbstractTimeSeriesChartView
{

    /**
     * CPU使用率を表示するビューを作成します。
     *
     * @param chart
     */
    public CpuRateTimeSeriesChartView()
    {
        super("CPU使用率", "時刻", "使用率(%)");
        setVerticalRange(0, 100);
    }

}
