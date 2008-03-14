package org.seasar.javelin.util;

import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.S2JavelinConfig;
import org.seasar.javelin.bean.InvocationInterval;


public class StatsUtil
{

    public static InvocationInterval getElapsedTime(CallTreeNode node)
    {
        InvocationInterval interval = new InvocationInterval();
        
        long elapsedTime = node.getAccumulatedTime();
        long elapsedCpuTime = node.getCpuTime();
        long elapsedUserTime = node.getUserTime();
        for (int index = 0; index < node.getChildren().size(); index++)
        {
            CallTreeNode child = (CallTreeNode) node.getChildren().get(index);
            elapsedTime = elapsedTime - child.getAccumulatedTime();
            elapsedCpuTime = elapsedCpuTime - child.getCpuTime();
            elapsedUserTime = elapsedUserTime - child.getUserTime();
        }
    
        interval.setInterval(elapsedTime);
        interval.setCpuInterval(elapsedCpuTime);
        interval.setUserInterval(elapsedUserTime);
        
        return interval;
    }

	/**
	 * �X���b�h�����ʂ��邽�߂̕�������o�͂���B 
	 * �t�H�[�}�b�g�F�X���b�h��@�X���b�h�N���X��@�X���b�h�I�u�W�F�N�g��ID
	 * 
	 * @return �X���b�h�����ʂ��邽�߂̕�����
	 */
	public static String createThreadIDText( )
	{
	    Thread currentThread = Thread.currentThread();
	
	    StringBuilder threadId = new StringBuilder();
        threadId.append(currentThread.getName());
        threadId.append("(");
        threadId.append(currentThread.getId());
        threadId.append(")");
	    threadId.append("@" + currentThread.getClass().getName());
        threadId.append("(");
	    threadId.append(StatsUtil.getObjectID(currentThread));
        threadId.append(")");
	
	    return threadId.toString();
	}

	/**
	 * �I�u�W�F�N�gID��16�i�`���̕�����Ƃ��Ď擾����B
	 * 
	 * @param object �I�u�W�F�N�gID���擾�I�u�W�F�N�g�B
	 * @return �I�u�W�F�N�gID�B
	 */
	public static String getObjectID(Object object)
	{
	    // ������null�̏ꍇ��"null"��Ԃ��B
	    if (object == null)
	    {
	        return "null";
	    }
	
	    return Integer.toHexString(System.identityHashCode(object));
	}
	
	/**
	 * object��toString�ŕ�����ɕϊ�����B
	 * 
	 * toString�ŗ�O�����������ꍇ�́A
	 * �W���G���[�o�͂�object�̃N���X���ƃX�^�b�N�g���[�X���o�͂��A
	 * �N���X��@�I�u�W�F�N�gID��Ԃ��B
	 * 
	 * @param object
	 * @return
	 */
	public static String toStr(Object object) {
		// ������null�̏ꍇ��"null"��Ԃ��B
		if (object == null) {
			return "null";
		}

		String result;
		try {
			result = object.toString();
		} catch (Throwable th) {
			S2JavelinConfig config = new S2JavelinConfig();
			if(config.isDebug())
			{
				System.err.println("Javelin Exception "
						+ object.getClass().toString() + "#toString(): ");
				th.printStackTrace();
			}
			result = object.getClass().toString() + "@"
					+ StatsUtil.getObjectID(object);
		}
		return result;
	}


	/**
	 * object��toString�ŕ�����ɕϊ��A�w�蒷�Ő؂�B
	 * 
	 * toString�ŗ�O�����������ꍇ�́A
	 * �W���G���[�o�͂�object�̃N���X���ƃX�^�b�N�g���[�X���o�͂��A
	 * �N���X��@�I�u�W�F�N�gID��Ԃ��B
	 * �w�蒷�𒴂��Ă���ꍇ�͎w�蒷�Ő؂�A"..."��t�^����B
	 * 
	 * @param object �����񉻑ΏۃI�u�W�F�N�g
	 * @param length ������w�蒷
	 * @return
	 */
	public static String toStr(Object object, int length) {
		// ������null�̏ꍇ��"null"��Ԃ��B
		if (object == null) {
			return "null";
		}

		String result;
		try {
			result = object.toString();
			if(length == 0) {
				result = "";
			} else  if(result.length() > length) {
				result = result.substring(0, length) + "...";
			}
		} catch (Throwable th) {
			S2JavelinConfig config = new S2JavelinConfig();
			if(config.isDebug())
			{
				System.err.println("Javelin Exception "
						+ object.getClass().toString() + "#toString(): ");
				th.printStackTrace();
			}
			result = object.getClass().toString() + "@"
					+ StatsUtil.getObjectID(object);
		}
		
		return result;
	}
	
	/**
	 * �o�C�g���byte[length]:FFFF...�`���ɕϊ��A�w�蒷�Ő؂�B
	 * 
	 * @param binary �o�C�i��
	 * @return
	 */
	public static String toStr(byte binary) {
		String hex = Integer.toHexString(((int)binary) & 0xFF).toUpperCase();
		String result = "byte[1]:" + "00".substring(hex.length()) + hex;
		return result;
	}
	
	/**
	 * �o�C�g���byte[length]:FFFF...�`���ɕϊ�(�ő�Ő擪8�o�C�g��16�i�o��)�B
	 * 
	 * @param binary �o�C�i��
	 * @return
	 */
	public static String toStr(byte[] binary) {
		
		if(binary.length == 0) {
			return "byte[0]";
		}
		
		StringBuffer result = new StringBuffer("byte[");
		result.append(binary.length);
		result.append("]:");
		for(int count = 0; count < 8 && count < binary.length; count++) {
			String hex = Integer.toHexString(((int)binary[count]) & 0xFF).toUpperCase();
			result.append("00".substring(hex.length()) + hex);
		}
		if(binary.length > 8) {
			result.append("...");
		}
		return result.toString();
	}
    

    /**
     * Object�̏��o�͂��s��
     * �o�͐[�x�ɂ��킹�A�t�B�[���h��H�邩���̏�ŏo�͂��邩���肷��
     * 
     * @param object       �o�͑ΏۃI�u�W�F�N�g
     * @param detailDepth  �o�͐[�x
     * @return             �o�͌���
     */
    public static String buildDetailString(Object object, int detailDepth)
    {
    	return DetailStringBuilder.buildDetailString(object, detailDepth);
    }
    
    /**
     * ToString�̌��ʂ�Ԃ�
     * 
     * @param object �ϊ��Ώ�
     * @return       ToString�̌���
     */
    public static String buildString(Object object)
    {
        //toString�͗�O�𔭐������邱�Ƃ����邽�߁A��������
        //"????"�Ƃ����������Ԃ��悤�ɂ���B
    	return DetailStringBuilder.buildString(object);
    }
}
