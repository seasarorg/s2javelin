package org.seasar.javelin.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.seasar.javelin.SystemLogger;

/**
 * �p�����[�^�ڍ׉����s���N���X�B
 * 
 * @author kato
 * 
 */
public class DetailStringBuilder
{
    private static final int            DEFAULT_DEPTH              = 1;

    private static final String         VALUE_HEADER               = "[";

    private static final String         VALUE_FOOTER               = "]";

    private static final String         KEY_VALUE_SEPARATOR        = " = ";

    private static final String         VALUE_SEPARATOR            = " , ";

    private static final String         DEFAULT_VALUE              = "????";

    private static final String         NULL_VALUE                 = "null";

    private static final String         INNER_CLASS_SEPARATOR_CHAR = "$";

    private static final String         OBJECT_ID_SEPARATOR        = ":";

    private static final String         CLASS_NAME_SEPARATOR       = "@";

    // ���o�͑ΏۂƂ���N���X�Q
    private static Set<String>          PRINT_CLASS_SET            = new HashSet<String>();
    static
    {
        PRINT_CLASS_SET.add("Short");
        PRINT_CLASS_SET.add("Integer");
        PRINT_CLASS_SET.add("Long");
        PRINT_CLASS_SET.add("String");
        PRINT_CLASS_SET.add("Boolean");
        PRINT_CLASS_SET.add("Byte");
        PRINT_CLASS_SET.add("Character");
        PRINT_CLASS_SET.add("Float");
        PRINT_CLASS_SET.add("Double");
    }

    /**
     * Object�̏��o�͂��s�� 
     * ���͂��ꂽ�[�x���ɂ��킹�A�t�B�[���h��H�邩���̏�ŏo�͂��邩���肷��
     * 
     * @param object �o�͑ΏۃI�u�W�F�N�g
     * @return �o�͌���
     */
    public static String buildDetailString(Object object)
    {
        // �o�͐[�x1�ŌĂяo��
        String detailString = buildDetailString(object, DEFAULT_DEPTH);

        return detailString;
    }

    /**
     * Object�̏��o�͂��s�� 
     * ���͂��ꂽ�[�x���ɂ��킹�A�t�B�[���h��H�邩���̏�ŏo�͂��邩���肷��
     * 
     * @param object �o�͑ΏۃI�u�W�F�N�g
     * @param detailDepth �ݒ�[�x
     * @return �o�͌���
     */
    protected static String buildDetailString(Object object, int detailDepth)
    {
        // null�̏ꍇ��"null"�Əo�͂���B
        if (object == null)
        {
            return NULL_VALUE;
        }

        // String�̏ꍇ�͂��̂܂܏o�͂���B
        if (object instanceof String)
        {
            return (String)object;
        }

        //�擪�Ƀw�b�_�����������
        String detailString = toDetailString(object, detailDepth, 0);
        StringBuilder detailBuilder = new StringBuilder();
        detailBuilder.append(object.getClass().getName());
        detailBuilder.append(CLASS_NAME_SEPARATOR);
        detailBuilder.append(Integer.toHexString(System.identityHashCode(object)));
        detailBuilder.append(OBJECT_ID_SEPARATOR);
        detailBuilder.append(detailString);
        return detailBuilder.toString();

    }

    /**
     * Object�̏ڍו����񉻂��s�� 
     * ���͂��ꂽ�[�x���ɂ��킹�A�t�B�[���h��H�邩���̏�ŏo�͂��邩���肷��
     * 
     * @param object �o�͑ΏۃI�u�W�F�N�g
     * @param detailDepth  �o�͐[�x
     * @param currentDepth ���ݐ[�x
     * @return �o�͌���
     */
    protected static String toDetailString(Object object, int detailDepth, int currentDepth)
    {
        // ���݂̊K�w�̐[�����ݒ�l�ȏ�̏ꍇ�AtoString�̌��ʂ�Ԃ�
        if (currentDepth >= detailDepth)
        {
            return buildString(object);
        }

        // ���o�͑ΏۂƂȂ�I�u�W�F�N�g�̏ꍇ�AtoString�̌��ʂ�Ԃ�
        if (isPrintable(object))
        {
            return buildString(object);
        }

        Class<?> clazz = object.getClass();
        // �z��̏ꍇ�A�z���p�����ŏo�͂��s��
        if (clazz.isArray())
        {
            return toStringArrayObject(object, detailDepth, currentDepth);
        }
        // �R���N�V�����̏ꍇ�A�R���N�V������p�����ŏo�͂��s��
        if (object instanceof Collection)
        {
            Collection<?> collectionObject = (Collection<?>)object;
            return toStringCollectionObject(collectionObject, detailDepth, currentDepth);
        }
        // Map�̏ꍇ�AMap��p�����ŏo�͂��s��
        if (object instanceof Map<?, ?>)
        {
            Map<?, ?> mapObject = (Map<?, ?>)object;
            return toStringMapObject(mapObject, detailDepth, currentDepth);
        }

        Field[] fields = clazz.getDeclaredFields();
        try
        {
            AccessibleObject.setAccessible(fields, true);
        }
        catch (SecurityException scex)
        {
            return buildString(object);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(VALUE_HEADER);
        boolean separatorFlag = false;
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            
            boolean printableFlag = isPrintable(field);

            if (printableFlag)
            {
                if (separatorFlag)
                {
                    builder.append(VALUE_SEPARATOR);
                }
                String fieldName = field.getName();
                builder.append(fieldName).append(KEY_VALUE_SEPARATOR);

                Object fieldValue = null;
                try
                {
                    fieldValue = field.get(object);
                    builder.append(toDetailString(fieldValue, detailDepth, currentDepth + 1));

                }
                // �G���[�����������ꍇ�̓f�t�H���g������Ƃ���
                catch (IllegalAccessException iaex)
                {
                    builder.append(DEFAULT_VALUE);
                }
                separatorFlag = true;
            }
        }
        builder.append(VALUE_FOOTER);
        return builder.toString();
    }

    /**
     * Map�̕�����o�͂��s���B
     * [key1 = value1 , key2 = value2 ..... keyn = valuen]�̌`���ŏo�͂���
     * 
     * @param mapObject �ΏۂƂȂ�Map
     * @param detailDepth �o�͐[�x
     * @param currentDepth ���ݐ[�x
     * @return Map�̕�����\��
     */
    protected static String toStringMapObject(Map<?, ?> mapObject, int detailDepth, int currentDepth)
    {
        // �z��null�̎���"null"��Ԃ��B
        if (mapObject == null)
        {
            return NULL_VALUE;
        }
        
        StringBuilder builder = new StringBuilder();
        builder.append(VALUE_HEADER);

        Object[] keys = mapObject.keySet().toArray();
        int length = keys.length;

        for (int i = 0; i < length; i++)
        {
            if (i > 0)
            {
                builder.append(VALUE_SEPARATOR);
            }

            Object item = mapObject.get(keys[i]);
            builder.append(buildString(keys[i])).append(KEY_VALUE_SEPARATOR);

            if (item == null)
            {
                builder.append(NULL_VALUE);
            }
            else
            {
                builder.append(toDetailString(item, detailDepth, currentDepth + 1));
            }
        }

        builder.append(VALUE_FOOTER);
        return builder.toString();
    }

    /**
     * �z��I�u�W�F�N�g�̃��O�o�͂��s��
     * 
     * @param array �o�͑Ώہi�z��j
     * @param detailDepth �o�͐[�x
     * @param currentDepth ���ݐ[�x
     * @return �o�͕�����
     */
    protected static String toStringArrayObject(Object array, int detailDepth, int currentDepth)
    {
        // �z��null�̎���"null"��Ԃ��B
        if (array == null)
        {
            return NULL_VALUE;
        }
        
        StringBuilder builder = new StringBuilder();
        builder.append(VALUE_HEADER);
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++)
        {
            Object item = Array.get(array, i);
            if (i > 0)
            {
                builder.append(VALUE_SEPARATOR);
            }

            if (item == null)
            {
                builder.append(NULL_VALUE);
            }
            else
            {
                builder.append(toDetailString(item, detailDepth, currentDepth + 1));
            }
        }
        builder.append(VALUE_FOOTER);
        return builder.toString();
    }

    /**
     * �R���N�V�����I�u�W�F�N�g�̃��O�o�͂��s��
     * 
     * @param array �o�͑Ώہi�z��j
     * @param detailDepth �o�͐[�x
     * @param currentDepth ���ݐ[�x
     * @return �o�͕�����
     */
    protected static String toStringCollectionObject(Collection<?> collection, int detailDepth,
            int currentDepth)
    {
        // �R���N�V������null�̎���"null"��Ԃ��B
        if (collection == null)
        {
            return NULL_VALUE;
        }
        
        StringBuilder builder = new StringBuilder();
        builder.append(VALUE_HEADER);

        boolean separatorFlag = false;
        for (Object item : collection)
        {
            if (separatorFlag)
            {
                builder.append(VALUE_SEPARATOR);
            }

            if (item == null)
            {
                builder.append(NULL_VALUE);
            }
            else
            {
                builder.append(toDetailString(item, detailDepth, currentDepth + 1));
            }
            separatorFlag = true;
        }

        builder.append(VALUE_FOOTER);
        return builder.toString();
    }

    /**
     * ToString�̌��ʂ�Ԃ�
     * 
     * @param object �ϊ��Ώ�
     * @return ToString�̌���
     */
    public static String buildString(Object object)
    {
        // �I�u�W�F�N�g��null�̎���"null"��Ԃ��B
        if (object == null)
        {
            return NULL_VALUE;
        }

        // toString�͗�O�𔭐������邱�Ƃ����邽�߁A��������
        // "????"�Ƃ����������Ԃ��悤�ɂ���B
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
     * �o�͑Ώۂ̃I�u�W�F�N�g��������s��
     * 
     * @param object ����Ώ�
     * @return ���茋��
     */
    protected static boolean isPrintable(Object object)
    {
    	if(object == null)
    	{
    		return true;
    	}
    	
        // Field�I�u�W�F�N�g�̏ꍇ�A�C���i�[�N���X�Atransient�t�B�[���h�Astatic�t�B�[���h
        // �̎��ɂ͏o�͂��s��Ȃ��B
        if (object instanceof Field)
        {
            Field field = (Field)object;
            if (field.getType().getName().indexOf(INNER_CLASS_SEPARATOR_CHAR) != -1)
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

        // Field�I�u�W�F�N�g�ł͂Ȃ��ꍇ�A�v���~�e�B�u�^�܂��̓v���~�e�B�u�^�̃��b�p�[�N���X
        // �̎��ɂ͑��o�͑ΏۂƂ���B
        Class<?> clazz = object.getClass();

        if (clazz.isPrimitive())
        {
            return true;
        }

        try
        {
        String className = clazz.getSimpleName();
        if (PRINT_CLASS_SET.contains(className))
        {
            return true;
        }
        } catch(Exception ex)
        {
        	SystemLogger.getInstance().warn(ex);
        }
        return false;
    }

}
