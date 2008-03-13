package org.seasar.javelin.bottleneckeye.util;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * 一般的な形式の日付文字列とlong型の時刻値を相互変換するユーティリティ。
 * @author hayakawa
 */
public class NormalDateFormatter
{
    static final private String DATA_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss.SSS";
    static final private String DATA_WITHOUT_MILLIS_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss";

    /**
     * long値で渡された時刻の値を、"yyyy/MM/dd HH:mm:ss.SSS"
     * という形式の文字列に変換する。
     * 
     * @param time 時刻
     * @return フォーマットした時刻の文字列
     */
    static public String format(long time)
    {
        return DateFormatUtils.format(time, DATA_FORMAT_PATTERN);
    }

    /**
     * long値で渡された時刻の値を、"yyyy/MM/dd HH:mm:ss"
     * という形式の文字列に変換する。
     * 
     * @param time 時刻
     * @return フォーマットした時刻の文字列
     */
    static public String formatWithoutMillis(long time)
    {
        return DateFormatUtils.format(time, DATA_WITHOUT_MILLIS_FORMAT_PATTERN);
    }
}