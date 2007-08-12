package org.seasar.javelin.statsvision.communicate;

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
	 * �d�����
	 */
	public static byte BYTE_TELEGRAM_KIND_ALERT = 0;

	/**
	 * �d�����
	 */
	public static byte BYTE_TELEGRAM_KIND_GET = 1;
	
	/**
	 * �d�����(���Z�b�g)
	 */
	public static byte BYTE_TELEGRAM_KIND_RESET = 2;
	
	/**
	 * �v���������(�ʒm)
	 */
	public static byte BYTE_REQUEST_KIND_NOTIFY = 0;


	/**
	 * �v���������(�v��)
	 */
	public static byte BYTE_REQUEST_KIND_REQUEST = 1;

	/**
	 * �v���������(����)
	 */
	public static byte BYTE_REQUEST_KIND_RESPONSE = 2;


	public static final byte BYTE_ITEMMODE_KIND_8BYTE_INT = 3;
	
	public static final byte BYTE_ITEMMODE_KIND_STRING = 6;
	
	public static final int INT_LOOP_COUNT_SINGLE = 1;
	/**
	 * ���byte[]�@���@���byte[]�@�ɓZ�߂�
	 */
    public static byte[] arrayAdd(byte[] byteBeforeArr,byte[] byteAfterArr)
    {
    	// �ԋp�p
	    byte[] byteResultArr = null;
	    
	    int byteBeforeArrLength = 0;
	    int byteAfterArrLength = 0;
	    
	    // �O���@byte[]�@�̃T�C�Y���擾
	    if (byteBeforeArr != null)
	    	byteBeforeArrLength = byteBeforeArr.length;
	    
	    // �㕪�@byte[]�@�̃T�C�Y���擾
	    if (byteAfterArr != null)
	    	byteAfterArrLength = byteAfterArr.length;
	    
	    // �ԋp�p�@byte[]�@�����
	    if (byteBeforeArrLength + byteAfterArrLength > 0)
	    	byteResultArr = new byte[byteBeforeArrLength + byteAfterArrLength];
	    
	    // �O���@byte[]�@��ԋp�p�@byte[]�@�ɐݒ肷��
	    if (byteBeforeArrLength > 0)
	    	System.arraycopy(byteBeforeArr,0,byteResultArr,0,byteBeforeArrLength);
	    
	    // �㕪�@byte[]�@��ԋp�p�@byte[]�@�ɐݒ肷��
	    if (byteAfterArrLength > 0)
	    	System.arraycopy(byteAfterArr,0,byteResultArr,byteBeforeArrLength,byteAfterArrLength);
	    
	    // �ԋp����
	    return byteResultArr;
    }
    
	/**
	 * byte[] ����A�ꕪ�f�[�^������
	 */
    public static byte[] arrayDel(byte[] byteSoruceArr,int intDelCount)
    {
    	// �ԋp�p
    	byte[] byteResultArr = new byte[byteSoruceArr.length - intDelCount];
    	
    	// �O�̈ꕪ�f�[�^������
    	System.arraycopy(byteSoruceArr,intDelCount,byteResultArr,0,byteResultArr.length);
    	
    	// �ԋp����
    	return byteResultArr;
    }

}