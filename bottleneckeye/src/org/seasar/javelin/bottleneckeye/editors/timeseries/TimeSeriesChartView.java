/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.timeseries;

import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;

/**
 * ���n��f�[�^��\������r���[�̃C���^�t�F�[�X�B
 *
 * @author sakamoto
 */
public interface TimeSeriesChartView
{

    /**
     * JFreeChart �I�u�W�F�N�g��Ԃ��܂��B
     *
     * @return JFreeChart �I�u�W�F�N�g
     */
    public JFreeChart getChart();


    /**
     * �f�[�^��ǉ����܂��B
     *
     * @param second �f�[�^��������������
     * @param value �l
     */
    public void addValue(Second second, double value);


    /**
     * �O���t��ɂ���f�[�^����Ԃ��܂��B
     *
     * @return �f�[�^��
     */
    public int getDataCount();


    /**
     * �O���t�̕`��ő�l��Ԃ��܂��B
     *
     * @return �ő�l
     */
    public double getVerticalRangeMax();


    /**
     * �O���t�̕`��ŏ��l��Ԃ��܂��B
     *
     * @return �ŏ��l
     */
    public double getVerticalRangeMin();


    /**
     * �O���t�ɕ\������f�[�^�̍ő吔���Z�b�g���܂��B
     *
     * @param limit �f�[�^�̍ő吔
     */
    public void setDataCountLimit(int limit);


    /**
     * �O���t�̕`��͈͂��Z�b�g���܂��B
     *
     * @param minValue �ŏ��l
     * @param maxValue �ő�l
     */
    public void setVerticalRange(double minValue, double maxValue);

}
