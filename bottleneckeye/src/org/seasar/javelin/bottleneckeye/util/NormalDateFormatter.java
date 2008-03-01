package org.seasar.javelin.bottleneckeye.util;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ��ʓI�Ȍ`���̓��t�������long�^�̎����l�𑊌ݕϊ����郆�[�e�B���e�B�B
 * @author hayakawa
 */
public class NormalDateFormatter
{
    static final private String DATA_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss.SSS";
    
    /**
     * long�l�œn���ꂽ�����̒l���A"yyyy/MM/dd HH:mm:ss.SSS"
     * �Ƃ����`���̕�����ɕϊ�����B
     * ���������Ă��Ȃ����߁A�����X���b�h����̃A�N�Z�X�ɑ΂���
     * �Ăяo�����������ꍇ�́A���ʂ�ۏ؂��Ȃ��B
     * 
     * @param time ����
     * @return �t�H�[�}�b�g���������̕�����
     */
    static public String format(long time)
    {
        Date tmpDateObject = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(DATA_FORMAT_PATTERN);
        tmpDateObject.setTime(time);
        return formatter.format(tmpDateObject);
    }
}