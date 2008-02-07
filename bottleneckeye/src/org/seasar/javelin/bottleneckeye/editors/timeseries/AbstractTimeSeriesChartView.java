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
 * 時系列データを表示する JFreeChart の抽象クラス。
 *
 * @author sakamoto
 */
public abstract class AbstractTimeSeriesChartView implements TimeSeriesChartView
{

    /** JFreeChartオブジェクト */
    private JFreeChart chart_;

    /** 時系列データ */
    private TimeSeries timeSeries_;

    /** データの最大数 */
    private int dataCountLimit_;

    /** グラフの縦軸の最大値 */
    private double maxValue_;

    /** グラフの縦軸の最小値 */
    private double minValue_;


    /**
     * 時系列データを表示するJFreeChartを作成します。
     *
     * @param titleLabel タイトル文字列
     * @param timeAxisLabel 横軸の文字列
     * @param valueAxisLabel 縦軸の文字列
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
     * データを追加します。
     *
     * @param second データが発生した時刻
     * @param value 値
     */
    public void addValue(Second second, double value)
    {
        // グラフ上のデータ数が最大なら、古いデータを削除する
        if (this.timeSeries_.getItemCount() == this.dataCountLimit_)
        {
            this.timeSeries_.delete(0, 0);
        }

        this.timeSeries_.add(second, value);
    }


    /**
     * グラフ上にあるデータ数を返します。
     *
     * @return データ数
     */
    public int getDataCount()
    {
        return this.timeSeries_.getItemCount();
    }


    /**
     * JFreeChart オブジェクトを返します。
     *
     * @return JFreeChart オブジェクト
     */
    public JFreeChart getChart()
    {
        return this.chart_;
    }


    /**
     * グラフの描画最大値を返します。
     *
     * @return 最大値
     */
    public double getVerticalRangeMax()
    {
        return this.maxValue_;
    }


    /**
     * グラフの描画最小値を返します。
     *
     * @return 最小値
     */
    public double getVerticalRangeMin()
    {
        return this.minValue_;
    }


    /**
     * グラフに表示するデータの最大数をセットします。
     *
     * 0 以下の値をセットした場合は、無視されます。
     *
     * @param limit データの最大数
     */
    public void setDataCountLimit(int limit)
    {
        if (limit <= 0)
        {
            return;
        }

        this.dataCountLimit_ = limit;

        // 値を変更した結果、すでにグラフ上にあるデータの数が最大数よりも多くなった場合は、
        // あふれた分の古いデータを削除する。
        if (this.timeSeries_.getItemCount() > limit)
        {
            this.timeSeries_.delete(0, this.timeSeries_.getItemCount() - limit - 1);
        }
    }


    /**
     * グラフの描画範囲をセットします。
     *
     * @param minValue 最小値
     * @param maxValue 最大値
     */
    public void setVerticalRange(double minValue, double maxValue)
    {
        this.minValue_ = minValue;
        this.maxValue_ = maxValue;

        // 縦軸の範囲をセットする
        XYPlot xyPlot = this.chart_.getXYPlot();
        ValueAxis axis = xyPlot.getRangeAxis();
        axis.setAutoRange(false);
        axis.setRange(new Range(minValue, maxValue));
    }

}
