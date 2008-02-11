package org.seasar.javelin.communicate;


/**
 * ��{�I�ȋ��ʋ@�\��񋟂���
 */
public class Common 
{
	/**
	 * int �� byte[]�@�ϊ����ɑΉ��̃o�C�g��
	 */
	public static final int INT_BYTE_SWITCH_LENGTH = 4;
    
	/**
	 * long �� byte[]�@�ϊ����ɑΉ��̃o�C�g��
	 */
	public static final int LONG_BYTE_SWITCH_LENGTH = 8;

	/**
	 * �d�����(�A���[��)
	 */
	public static final byte BYTE_TELEGRAM_KIND_ALERT = 0;

	/**
	 * �d�����(��Ԏ擾)
	 */
	public static final byte BYTE_TELEGRAM_KIND_GET = 1;
	
	/**
	 * �d�����(���Z�b�g)
	 */
	public static final byte BYTE_TELEGRAM_KIND_RESET = 2;


	/**
	 * �d����ʁi���\�[�X�ʒm�j
	 */
    public static final byte BYTE_TELEGRAM_KIND_RESOURCENOTIFY = 3;

    /**
     * �d����ʁi�ݒ�ύX�j
     */
    public static final byte BYTE_TELEGRAM_KIND_CONFIGCHANGE = 4;

    /**
     * �d����ʁi�@�\�Ăяo���j
     */
    public static final byte BYTE_TELEGRAM_KIND_FUNCTIONCALL = 5;
    
    /**
	 * �d�����(JVN���O�o�͒ʒm)
	 */
	public static final byte BYTE_TELEGRAM_KIND_JVN_FILE = 6;

    /** �d�����(�T�[�o�v���p�e�B�擾) */
	public static final byte BYTE_TELEGRAM_KIND_GET_PROPERTY = 7;

    /** �d�����(�T�[�o�v���p�e�B�X�V) */
	public static final byte BYTE_TELEGRAM_KIND_UPDATE_PROPERTY = 8;
    
    /**
     * �d�����(JVN���O�o�͒ʒm)
     */
    public static final byte BYTE_TELEGRAM_KIND_JVN_FILE_LIST = 9;
    
	/**
	 * �v���������(�ʒm)
	 */
	public static final byte BYTE_REQUEST_KIND_NOTIFY = 0;


	/**
	 * �v���������(�v��)
	 */
	public static final byte BYTE_REQUEST_KIND_REQUEST = 1;

	/**
	 * �v���������(����)
	 */
	public static final byte BYTE_REQUEST_KIND_RESPONSE = 2;

	
	public static final byte BYTE_ITEMMODE_KIND_8BYTE_INT = 3;
	
	public static final byte BYTE_ITEMMODE_KIND_STRING = 6;
	
	public static final int INT_LOOP_COUNT_SINGLE = 1;

}