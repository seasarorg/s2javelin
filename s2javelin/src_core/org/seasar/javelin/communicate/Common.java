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