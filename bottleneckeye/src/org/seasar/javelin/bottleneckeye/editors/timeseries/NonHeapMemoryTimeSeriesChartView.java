/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.timeseries;


/**
 * �q�[�v�ȊO�̃������g�p����\������r���[�B
 *
 * @author sakamoto
 */
public class NonHeapMemoryTimeSeriesChartView extends AbstractTimeSeriesChartView
{

    /**
     * �q�[�v�ȊO�̃������g�p����\������r���[���쐬���܂��B
     *
     * @param chart
     */
    public NonHeapMemoryTimeSeriesChartView()
    {
        super("�q�[�v�ȊO�g�p��", "����", "�g�p��(MB)");
    }

}
