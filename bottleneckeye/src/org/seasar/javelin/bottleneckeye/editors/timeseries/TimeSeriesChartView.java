/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.timeseries;

import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;

/**
 * 時系列データを表示するビューのインタフェース。
 *
 * @author sakamoto
 */
public interface TimeSeriesChartView
{

    /**
     * JFreeChart オブジェクトを返します。
     *
     * @return JFreeChart オブジェクト
     */
    public JFreeChart getChart();


    /**
     * データを追加します。
     *
     * @param second データが発生した時刻
     * @param value 値
     */
    public void addValue(Second second, double value);


    /**
     * グラフ上にあるデータ数を返します。
     *
     * @return データ数
     */
    public int getDataCount();


    /**
     * グラフの描画最大値を返します。
     *
     * @return 最大値
     */
    public double getVerticalRangeMax();


    /**
     * グラフの描画最小値を返します。
     *
     * @return 最小値
     */
    public double getVerticalRangeMin();


    /**
     * グラフに表示するデータの最大数をセットします。
     *
     * @param limit データの最大数
     */
    public void setDataCountLimit(int limit);


    /**
     * グラフの描画範囲をセットします。
     *
     * @param minValue 最小値
     * @param maxValue 最大値
     */
    public void setVerticalRange(double minValue, double maxValue);

}
