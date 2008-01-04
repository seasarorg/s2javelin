// KeywordConverterFactory.java
package org.seasar.javelin.util;

/**
 * �������u�����邽�߂̃N���X�𐶐�����N���X�B</br>
 * 
 * @author tsukano
 */
public class KeywordConverterFactory
{
    /**
     * Prefix�ASurfix�Ȃ��ŃL�[���[�h�����̂܂ܒu������N���X�𐶐�����B</br>
     * 
     * @return �L�[���[�h�u���N���X
     */
    public static KeywordConverter createSimpleConverter()
    {
        return new KeywordConverter();
    }

    /**
     * {keyword}�`���̃L�[���[�h��u������N���X�𐶐�����B</br>
     * 
     * @return �L�[���[�h�u���N���X
     */
    public static KeywordConverter createBraceConverter()
    {
        return new KeywordConverter("{", "}");
    }

    /**
     * ${keyword}�`���̃L�[���[�h��u������N���X�𐶐�����B</br>
     * 
     * @return �L�[���[�h�u���N���X
     */
    public static KeywordConverter createDollarBraceConverter()
    {
        return new KeywordConverter("${", "}");
    }

    /**
     * [keyword]�`���̃L�[���[�h��u������N���X�𐶐�����B</br>
     * 
     * @return �L�[���[�h�u���N���X
     */
    public static KeywordConverter createBracketConverter()
    {
        return new KeywordConverter("[", "]");
    }

    /**
     * $[keyword]�`���̃L�[���[�h��u������N���X�𐶐�����B</br>
     * 
     * @return �L�[���[�h�u���N���X
     */
    public static KeywordConverter createDollarBracketConverter()
    {
        return new KeywordConverter("$[", "]");
    }

    /**
     * 'keyword'�`���̃L�[���[�h��u������N���X�𐶐�����B</br>
     * 
     * @return �L�[���[�h�u���N���X
     */
    public static KeywordConverter createSingleQouteConverter()
    {
        return new KeywordConverter("'", "'");
    }

    /**
     * "keyword"�`���̃L�[���[�h��u������N���X�𐶐�����B</br>
     * 
     * @return �L�[���[�h�u���N���X
     */
    public static KeywordConverter createDoubleQouteConverter()
    {
        return new KeywordConverter("\"", "\"");
    }
}
