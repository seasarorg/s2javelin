package org.seasar.javelin.util;

import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.S2JavelinConfig;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

public class StatsUtil
{

	public static long getElapsedTime(CallTreeNode node)
	{
	    long elapsedTime = node.getAccumulatedTime();
	    for (int index = 0; index < node.getChildren().size(); index++)
	    {
	    	CallTreeNode child = (CallTreeNode)  node.getChildren().get(index);
	        elapsedTime = elapsedTime - child.getAccumulatedTime();
	    }
	
	    return elapsedTime;
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
	
	    StringBuffer threadId = new StringBuffer();
	    threadId.append(currentThread.getName());
	    threadId.append("@" + currentThread.getClass().getName());
	    threadId.append("@" + StatsUtil.getObjectID(currentThread));
	
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
    
    //####################################################################
    //# �� �ȉ���JavelinLogUtil�ɂ��L�q���� �C������ꍇ�͓��ꂷ�邱�Ɓ� #
    //####################################################################
    private static final String   VALUE_HEADER        = "[";

    private static final String   VALUE_FOOTER        = "]";

    private static final String   KEY_VALUE_SEPARATOR = " = ";

    private static final String   VALUE_SEPARATOR     = " , ";

    private static final String   DEFAULT_VALUE       = "????";

    private static final String   NULL_VALUE          = "null";

    private static final String INNER_CLASS_SEPARATOR_CHAR = "$";

    //���o�͑ΏۂƂ���N���X�Q
    private static final String[] PRINT_CLASS         = {"Short", "Integer",
            "Long", "String", "Boolean", "Byte", "Character", "Float", "Double"};

    /**
     * Object�̏��o�͂��s��
     * ���͂��ꂽ�[�x���ɂ��킹�A�t�B�[���h��H�邩���̏�ŏo�͂��邩���肷��
     * 
     * @param object       �o�͑ΏۃI�u�W�F�N�g
     * @param detailDepth  �o�͐[�x
     * @param currentDepth ���ݐ[�x
     * @return             �o�͌���
     */
    public static String objectDetailPrinter(Object object, int detailDepth,
            int currentDepth)
    {

    	// String�̏ꍇ�͂��̂܂܏o�͂���B
        if(object instanceof String)
        {
            return (String)object;
        }

        if (object == null)
        {
            return NULL_VALUE;
        }
        
        // �擪�Ƀw�b�_�������ǉ�����B
        String detailString = toDetailString(object, detailDepth, currentDepth);
        if(currentDepth == 0)
        {
            StringBuffer detailBuffer = new StringBuffer();
        	detailBuffer.append(object.getClass().toString() + "@" + getObjectID(object)).append(":");
        	detailBuffer.append(detailString);
        	return detailBuffer.toString();
        }
        else
        {
        	return detailString;
        }
    }

	private static String toDetailString(Object object, int detailDepth, int currentDepth) {
		//���݂̊K�w�̐[�����ݒ�l�ȏ�̏ꍇ�AtoString�̌��ʂ�Ԃ�
        if (currentDepth >= detailDepth)
        {
        	return toStringPrinter(object);
        }

        //���o�͑ΏۂƂȂ�I�u�W�F�N�g�̏ꍇ�AtoString�̌��ʂ�Ԃ�
        if (isPrintTarget(object))
        {
            return toStringPrinter(object);
        }

        Class clazz = object.getClass();
        //�z��̏ꍇ�A�z��̊e�v�f�ɑ΂��Ĕ�����s��
        if (clazz.isArray())
        {
            return toStringArrayObject(object, detailDepth, currentDepth);
        }
        //�R���N�V�����̏ꍇ�A�z��ɕϊ����Ĕ�����s��
        if (object instanceof Collection)
        {
            Collection collectionObject = (Collection)object;
            return toStringArrayObject(collectionObject.toArray(), detailDepth,
                                       currentDepth);
        }
        //Map�̏ꍇ�͐�p�����ŏo�͂��s��
        if (object instanceof Map)
        {
            Map mapObject = (Map)object;
            return toStringMapObject(mapObject, detailDepth, currentDepth);
        }

        Field[] fields = clazz.getDeclaredFields();
        try
        {
            AccessibleObject.setAccessible(fields, true);
        }
        catch (SecurityException scex)
        {
            return toStringPrinter(object);
        }

        StringBuffer buf = new StringBuffer();
        buf.append(VALUE_HEADER);
        boolean separatorFlag = false;
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            if (accept(field))
            {
                if (separatorFlag)
                {
                    buf.append(VALUE_SEPARATOR);
                }
                String fieldName = field.getName();
                buf.append(fieldName).append(KEY_VALUE_SEPARATOR);

                Object fieldValue = null;
                try
                {
                    fieldValue = field.get(object);
                    buf.append(objectDetailPrinter(fieldValue, detailDepth,
                                                   currentDepth + 1));

                }
                //�G���[�����������ꍇ�̓f�t�H���g������Ƃ���
                catch (IllegalAccessException iaex)
                {
                    buf.append(DEFAULT_VALUE);
                }
                separatorFlag = true;
            }
        }
        buf.append(VALUE_FOOTER);
        return buf.toString();
	}

    /**
     * Map�̕�����o�͂��s���B
     * [key1 = value1 , key2 = value2 ..... keyn = valuen]�̌`���ŏo�͂���
     * 
     * @param mapObject    �ΏۂƂȂ�Map
     * @param detailDepth  �o�͐[�x
     * @param currentDepth ���ݐ[�x
     * @return             Map�̕�����\��
     */
    public static String toStringMapObject(Map mapObject, int detailDepth,
            int currentDepth)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(VALUE_HEADER);

        Object[] keys = mapObject.keySet().toArray();
        int length = keys.length;

        for (int i = 0; i < length; i++)
        {
            if (i > 0)
            {
                buf.append(VALUE_SEPARATOR);
            }
            
            Object item = mapObject.get(keys[i]);
            buf.append(toStringPrinter(keys[i])).append(KEY_VALUE_SEPARATOR);

            if (item == null)
            {
                buf.append(NULL_VALUE);
            }
            else
            {
                buf.append(objectDetailPrinter(item, detailDepth,
                                               currentDepth + 1));
            }
        }

        buf.append(VALUE_FOOTER);
        return buf.substring(0);
    }

    /**
     * �z��I�u�W�F�N�g�̃��O�o�͂��s��
     * 
     * @param array        �o�͑Ώہi�z��j
     * @param detailDepth  �o�͐[�x
     * @param currentDepth ���ݐ[�x
     * @return             �o�͕�����
     */
    public static String toStringArrayObject(Object array, int detailDepth,
            int currentDepth)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(VALUE_HEADER);
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++)
        {
            Object item = Array.get(array, i);
            if (i > 0)
            {
                buf.append(VALUE_SEPARATOR);
            }

            if (item == null)
            {
                buf.append(NULL_VALUE);
            }
            else
            {
                buf.append(objectDetailPrinter(item, detailDepth,
                                               currentDepth + 1));
            }
        }
        buf.append(VALUE_FOOTER);
        return buf.substring(0);
    }

    /**
     * ToString�̌��ʂ�Ԃ�
     * 
     * @param object �ϊ��Ώ�
     * @return       ToString�̌���
     */
    public static String toStringPrinter(Object object)
    {
        //toString�͗�O�𔭐������邱�Ƃ����邽�߁A��������
        //"????"�Ƃ����������Ԃ��悤�ɂ���B
        try
        {
            return object.toString();
        }
        catch (Throwable th)
        {
            return DEFAULT_VALUE;
        }
    }

    /**
     * field���o�͂��邩�A�̔��茋�ʂ�Ԃ�
     * 
     * @param field �N���X���̃t�B�[���h
     * @return      �o�͂��邩�ۂ�
     */
    public static boolean accept(Field field)
    {
        if (field.getName().indexOf(INNER_CLASS_SEPARATOR_CHAR) != -1)
        {
            return false;
        }
        if (Modifier.isTransient(field.getModifiers()))
        {
            return false;
        }
        if (Modifier.isStatic(field.getModifiers()))
        {
            return false;
        }
        return true;
    }

    /**
     * ���o�͑Ώۂ̃I�u�W�F�N�g��������s��
     * Primitive�^�A�������͂��̃��b�p�[�N���X���o�͑ΏۂƂȂ�
     * 
     * @param object ����Ώ�
     * @return       ���茋��
     */
    public static boolean isPrintTarget(Object object)
    {
        Class clazz = object.getClass();

        if (clazz.isPrimitive())
        {
            return true;
        }

        String className = clazz.getSimpleName();

        for (String target : PRINT_CLASS)
        {
            if (target.equals(className))
            {
                return true;
            }
        }

        return false;
    }
}
