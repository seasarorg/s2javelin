// KeywordConverter.java
package org.seasar.javelin.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * ������Ɋ܂܂��L�[���[�h��ϊ�����N���X�B</br>
 * �L�[���[�h��Prefix�ASuffix���w�肷�邱�Ƃ��ł���B
 * addConverter���\�b�h�𗘗p���ăL�[���[�h�̒u����������w�肵�A
 * convert���\�b�h�Œu������B
 * 
 * @author tsukano
 */
public class KeywordConverter
{
    /** �L�[���[�h��Prefix */
    private final String        keywordPrefix;

    /** �L�[���[�h��Suffix */
    private final String        keywordSuffix;

    /** �L�[���[�h��ϊ����镶������`�������X�g */
    private Map<String, String> converterMap = new LinkedHashMap<String, String>();

    /**
     * Prefix�ASuffix�Ȃ��̕ϊ��N���X�𐶐�����B</br>
     */
    public KeywordConverter()
    {
        this.keywordPrefix = "";
        this.keywordSuffix = "";
    }

    /**
     * Prefix�ASuffix���w�肵�ĕϊ��N���X�𐶐�����B</br>
     * 
     * @param keywordPrefix �L�[���[�h��Prefix
     * @param keywordSuffix �L�[���[�h��Suffix
     */
    public KeywordConverter(String keywordPrefix, String keywordSuffix)
    {
        this.keywordPrefix = keywordPrefix;
        this.keywordSuffix = keywordSuffix;
    }

    /**
     * �L�[���[�h�ƒu���������ǉ�����B</br>
     * 
     * @param keyword �L�[���[�h
     * @param convertedString �L�[���[�h�̒u��������
     */
    public void addConverter(String keyword, String convertedString)
    {
        converterMap.put(keywordPrefix + keyword + keywordSuffix,
                         convertedString);
    }
    
    /**
     * �L�[���[�h�ƒu���������ǉ�����B</br>
     * �u���������int�l��ݒ肷��ׂ̊ȈՃ��\�b�h�B
     * 
     * @param keyword �L�[���[�h
     * @param convertedValue �L�[���[�h�̒u��������(int�l)
     */
    public void addConverter(String keyword, int convertedValue)
    {
        addConverter(keyword, String.valueOf(convertedValue));
    }
    
    /**
     * �L�[���[�h�ƒu���������ǉ�����B</br>
     * �u���������long�l��ݒ肷��ׂ̊ȈՃ��\�b�h�B
     * 
     * @param keyword �L�[���[�h
     * @param convertedValue �L�[���[�h�̒u��������(long�l)
     */
    public void addConverter(String keyword, long convertedValue)
    {
        addConverter(keyword, String.valueOf(convertedValue));
    }
    
    /**
     * �L�[���[�h�ƒu���������ǉ�����B</br>
     * �u���������Object�̕������ݒ肷��ׂ̊ȈՃ��\�b�h�B
     * toString()���������Ă���Object�Ȃ�΂��̏o�͂Œu������B
     * 
     * @param keyword �L�[���[�h
     * @param convertedValue �L�[���[�h�̒u��������(Object)
     */
    public void addConverter(String keyword, Object convertedValue)
    {
        addConverter(keyword, String.valueOf(convertedValue));
    }
    
    /**
     * �o�^�����u��������ɃL�[���[�h��u������B</br>
     * 
     * @param source �u���O�̕�����
     * @return �u����̕�����
     */
    public String convert(String source)
    {
        String retValue = source;

        // �o�^���Ă�����𗘗p���Ēu������
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

        // �u����̕������Ԃ�
        return retValue;
    }
}
