// KeywordConverterFactory.java
package org.seasar.javelin.util;

/**
 * 文字列を置換するためのクラスを生成するクラス。</br>
 * 
 * @author tsukano
 */
public class KeywordConverterFactory
{
    /**
     * Prefix、Surfixなしでキーワードをそのまま置換するクラスを生成する。</br>
     * 
     * @return キーワード置換クラス
     */
    public static KeywordConverter createSimpleConverter()
    {
        return new KeywordConverter();
    }

    /**
     * {keyword}形式のキーワードを置換するクラスを生成する。</br>
     * 
     * @return キーワード置換クラス
     */
    public static KeywordConverter createBraceConverter()
    {
        return new KeywordConverter("{", "}");
    }

    /**
     * ${keyword}形式のキーワードを置換するクラスを生成する。</br>
     * 
     * @return キーワード置換クラス
     */
    public static KeywordConverter createDollarBraceConverter()
    {
        return new KeywordConverter("${", "}");
    }

    /**
     * [keyword]形式のキーワードを置換するクラスを生成する。</br>
     * 
     * @return キーワード置換クラス
     */
    public static KeywordConverter createBracketConverter()
    {
        return new KeywordConverter("[", "]");
    }

    /**
     * $[keyword]形式のキーワードを置換するクラスを生成する。</br>
     * 
     * @return キーワード置換クラス
     */
    public static KeywordConverter createDollarBracketConverter()
    {
        return new KeywordConverter("$[", "]");
    }

    /**
     * 'keyword'形式のキーワードを置換するクラスを生成する。</br>
     * 
     * @return キーワード置換クラス
     */
    public static KeywordConverter createSingleQouteConverter()
    {
        return new KeywordConverter("'", "'");
    }

    /**
     * "keyword"形式のキーワードを置換するクラスを生成する。</br>
     * 
     * @return キーワード置換クラス
     */
    public static KeywordConverter createDoubleQouteConverter()
    {
        return new KeywordConverter("\"", "\"");
    }
}
