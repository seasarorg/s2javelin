package org.seasar.javelin.bottleneckeye.editors.timeseries;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * ���n��f�[�^��\������ JFreeChart �̒��ۃN���X�B
 *
 * @author sakamoto
 */
public abstract class AbstractTimeSeriesChartView implements TimeSeriesChartView
{

    /** JFreeChart�I�u�W�F�N�g */
    private JFreeChart chart_;

    /** ���n��f�[�^ */
    private TimeSeries timeSeries_;

    /** �f�[�^�̍ő吔 */
    private int dataCountLimit_;

    /** �O���t�̏c���̍ő�l */
    private double maxValue_;

    /** �O���t�̏c���̍ŏ��l */
    private double minValue_;


    /**
     * ���n��f�[�^��\������JFreeChart���쐬���܂��B
     *
     * @param titleLabel �^�C�g��������
     * @param timeAxisLabel �����̕�����
     * @param valueAxisLabel �c���̕�����
     */
    public AbstractTimeSeriesChartView(String titleLabel, String timeAxisLabel, String valueAxisLabel)
    {
        this.minValue_ = 0.0;
        this.maxValue_ = 1.0;
        this.dataCountLimit_ = 200;

        this.timeSeries_ = new TimeSeries("Series", "domain", "range", Second.class);
        TimeSeriesCollection collection = new TimeSeriesCollection();
        collection.addSeries(this.timeSeries_);
        this.chart_ = ChartFactory.createTimeSeriesChart(
                titleLabel, timeAxisLabel, valueAxisLabel, collection, true, true, false);

        this.chart_.removeLegend();
    }


    /**
     * �f�[�^��ǉ����܂��B
     *
     * @param second �f�[�^��������������
     * @param value �l
     */
    public void addValue(Second second, double value)
    {
        // �O���t��̃f�[�^�����ő�Ȃ�A�Â��f�[�^���폜����
        if (this.timeSeries_.getItemCount() == this.dataCountLimit_)
        {
            this.timeSeries_.delete(0, 0);
        }

        this.timeSeries_.add(second, value);
    }


    /**
     * �O���t��ɂ���f�[�^����Ԃ��܂��B
     *
     * @return �f�[�^��
     */
    public int getDataCount()
    {
        return this.timeSeries_.getItemCount();
    }


    /**
     * JFreeChart �I�u�W�F�N�g��Ԃ��܂��B
     *
     * @return JFreeChart �I�u�W�F�N�g
     */
    public JFreeChart getChart()
    {
        return this.chart_;
    }


    /**
     * �O���t�̕`��ő�l��Ԃ��܂��B
     *
     * @return �ő�l
     */
    public double getVerticalRangeMax()
    {
        return this.maxValue_;
    }


    /**
     * �O���t�̕`��ŏ��l��Ԃ��܂��B
     *
     * @return �ŏ��l
     */
    public double getVerticalRangeMin()
    {
        return this.minValue_;
    }


    /**
     * �O���t�ɕ\������f�[�^�̍ő吔���Z�b�g���܂��B
     *
     * 0 �ȉ��̒l���Z�b�g�����ꍇ�́A��������܂��B
     *
     * @param limit �f�[�^�̍ő吔
     */
    public void setDataCountLimit(int limit)
    {
        if (limit <= 0)
        {
            return;
        }

        this.dataCountLimit_ = limit;

        // �l��ύX�������ʁA���łɃO���t��ɂ���f�[�^�̐����ő吔���������Ȃ����ꍇ�́A
        // ���ӂꂽ���̌Â��f�[�^���폜����B
        if (this.timeSeries_.getItemCount() > limit)
        {
            this.timeSeries_.delete(0, this.timeSeries_.getItemCount() - limit - 1);
        }
    }


    /**
     * �O���t�̕`��͈͂��Z�b�g���܂��B
     *
     * @param minValue �ŏ��l
     * @param maxValue �ő�l
     */
    public void setVerticalRange(double minValue, double maxValue)
    {
        this.minValue_ = minValue;
        this.maxValue_ = maxValue;

        // �c���͈̔͂��Z�b�g����
        XYPlot xyPlot = this.chart_.getXYPlot();
        ValueAxis axis = xyPlot.getRangeAxis();
        axis.setAutoRange(false);
        axis.setRange(new Range(minValue, maxValue));
    }

}
