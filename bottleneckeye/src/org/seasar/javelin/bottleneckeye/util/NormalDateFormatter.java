package org.seasar.javelin.bottleneckeye.util;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * ��ʓI�Ȍ`���̓��t�������long�^�̎����l�𑊌ݕϊ����郆�[�e�B���e�B�B
 * @author hayakawa
 */
public class NormalDateFormatter
{
    static final private String DATA_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss.SSS";
    static final private String DATA_WITHOUT_MILLIS_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss";

    /**
     * long�l�œn���ꂽ�����̒l���A"yyyy/MM/dd HH:mm:ss.SSS"
     * �Ƃ����`���̕�����ɕϊ�����B
     * 
     * @param time ����
     * @return �t�H�[�}�b�g���������̕�����
     */
    static public String format(long time)
    {
        return DateFormatUtils.format(time, DATA_FORMAT_PATTERN);
    }

    /**
     * long�l�œn���ꂽ�����̒l���A"yyyy/MM/dd HH:mm:ss"
     * �Ƃ����`���̕�����ɕϊ�����B
     * 
     * @param time ����
     * @return �t�H�[�}�b�g���������̕�����
     */
    static public String formatWithoutMillis(long time)
    {
        return DateFormatUtils.format(time, DATA_WITHOUT_MILLIS_FORMAT_PATTERN);
    }
}