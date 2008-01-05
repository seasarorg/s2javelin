// KeywordConverter.java
package org.seasar.javelin.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 文字列に含まれるキーワードを変換するクラス。</br>
 * キーワードのPrefix、Suffixを指定することができる。
 * addConverterメソッドを利用してキーワードの置換文字列を指定し、
 * convertメソッドで置換する。
 * 
 * @author tsukano
 */
public class KeywordConverter
{
    /** キーワードのPrefix */
    private final String        keywordPrefix;

    /** キーワードのSuffix */
    private final String        keywordSuffix;

    /** キーワードを変換する文字列を定義したリスト */
    private Map<String, String> converterMap = new LinkedHashMap<String, String>();

    /**
     * Prefix、Suffixなしの変換クラスを生成する。</br>
     */
    public KeywordConverter()
    {
        this.keywordPrefix = "";
        this.keywordSuffix = "";
    }

    /**
     * Prefix、Suffixを指定して変換クラスを生成する。</br>
     * 
     * @param keywordPrefix キーワードのPrefix
     * @param keywordSuffix キーワードのSuffix
     */
    public KeywordConverter(String keywordPrefix, String keywordSuffix)
    {
        this.keywordPrefix = keywordPrefix;
        this.keywordSuffix = keywordSuffix;
    }

    /**
     * キーワードと置換文字列を追加する。</br>
     * 
     * @param keyword キーワード
     * @param convertedString キーワードの置換文字列
     */
    public void addConverter(String keyword, String convertedString)
    {
        converterMap.put(keywordPrefix + keyword + keywordSuffix,
                         convertedString);
    }
    
    /**
     * キーワードと置換文字列を追加する。</br>
     * 置換文字列にint値を設定する為の簡易メソッド。
     * 
     * @param keyword キーワード
     * @param convertedValue キーワードの置換文字列(int値)
     */
    public void addConverter(String keyword, int convertedValue)
    {
        addConverter(keyword, String.valueOf(convertedValue));
    }
    
    /**
     * キーワードと置換文字列を追加する。</br>
     * 置換文字列にlong値を設定する為の簡易メソッド。
     * 
     * @param keyword キーワード
     * @param convertedValue キーワードの置換文字列(long値)
     */
    public void addConverter(String keyword, long convertedValue)
    {
        addConverter(keyword, String.valueOf(convertedValue));
    }
    
    /**
     * キーワードと置換文字列を追加する。</br>
     * 置換文字列にObjectの文字列を設定する為の簡易メソッド。
     * toString()を実装しているObjectならばその出力で置換する。
     * 
     * @param keyword キーワード
     * @param convertedValue キーワードの置換文字列(Object)
     */
    public void addConverter(String keyword, Object convertedValue)
    {
        addConverter(keyword, String.valueOf(convertedValue));
    }
    
    /**
     * 登録した置換文字列にキーワードを置換する。</br>
     * 
     * @param source 置換前の文字列
     * @return 置換後の文字列
     */
    public String convert(String source)
    {
        String retValue = source;

        // 登録してある情報を利用して置換する
        Set<Map.Entry<String, String>> entries = converterMap.entrySet();
        for (Map.Entry<String, String> entry : entries)
        {
            if (entry.getValue() == null)
            {
                retValue = retValue.replace(entry.getKey(), "null");
            }
            else
            {
                retValue = retValue.replace(entry.getKey(), entry.getValue());
            }
        }

        // 置換後の文字列を返す
        return retValue;
    }
}
