/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.timeseries;


/**
 * CPU�g�p����\������r���[�B
 *
 * @author sakamoto
 */
public class CpuRateTimeSeriesChartView extends AbstractTimeSeriesChartView
{

    /**
     * CPU�g�p����\������r���[���쐬���܂��B
     *
     * @param chart
     */
    public CpuRateTimeSeriesChartView()
    {
        super("CPU�g�p��", "����", "�g�p��(%)");
        setVerticalRange(0, 100);
    }

}
