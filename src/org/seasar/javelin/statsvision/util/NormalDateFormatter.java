package org.seasar.javelin.statsvision.util;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ��ʓI�Ȍ`���̓��t�������long�^�̎����l�𑊌ݕϊ����郆�[�e�B���e�B�B
 * @author hayakawa
 */
public class NormalDateFormatter
{
    static final private String     DATA_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss.SSS";

    static private SimpleDateFormat formatter_          = new SimpleDateFormat(
                                                                               DATA_FORMAT_PATTERN);

    static private Date             tmpDateObject_      = new Date();
    
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
        tmpDateObject_.setTime(time);
        return formatter_.format(tmpDateObject_);
    }
}