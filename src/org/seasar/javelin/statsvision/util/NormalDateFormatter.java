package org.seasar.javelin.statsvision.util;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 一般的な形式の日付文字列とlong型の時刻値を相互変換するユーティリティ。
 * @author hayakawa
 */
public class NormalDateFormatter
{
    static final private String     DATA_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss.SSS";

    static private SimpleDateFormat formatter_          = new SimpleDateFormat(
                                                                               DATA_FORMAT_PATTERN);

    static private Date             tmpDateObject_      = new Date();
    
    /**
     * long値で渡された時刻の値を、"yyyy/MM/dd HH:mm:ss.SSS"
     * という形式の文字列に変換する。
     * 同期化していないため、複数スレッドからのアクセスに対する
     * 呼び出しがあった場合は、結果を保証しない。
     * 
     * @param time 時刻
     * @return フォーマットした時刻の文字列
     */
    static public String format(long time)
    {
        tmpDateObject_.setTime(time);
        return formatter_.format(tmpDateObject_);
    }
}