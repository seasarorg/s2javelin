package org.seasar.javelin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.seasar.javelin.S2JavelinConfig;

/**
 * Javelin�S�̂̐ݒ��ێ�����N���X�B(Singleton)
 *
 * �l�����߂Ď擾���悤�Ƃ����Ƃ��igetter���\�b�h�����߂ČĂ񂾂Ƃ��j��
 * �t�@�C������ݒ�����[�h����B
 *
 * @author sakamoto
 */
public class JavelinConfigUtil
{
    /** Javelin�I�v�V�����L�[ */
    private static final String      JAVELIN_OPTION_KEY = S2JavelinConfig.JAVELIN_PREFIX
                                                                + "property";

    /** �ݒ�t�@�C���� */
    private String                   fileName_;

    /** Javelin �v���p�e�B */
    private Properties               properties_;

    /** Javelin�̐ݒ�I�u�W�F�N�g */
    private static JavelinConfigUtil configUtil_        = new JavelinConfigUtil();

    /**
     * Singleton
     */
    private JavelinConfigUtil()
    {
        this.fileName_ = System.getProperty(JAVELIN_OPTION_KEY);
    }

    /**
     * JavelinConfigUtil�̃C���X�^���X��Ԃ��B
     *
     * @return �C���X�^���X(Singleton)
     */
    public static JavelinConfigUtil getInstance()
    {
        return configUtil_;
    }

    /**
     * �ݒ�t�@�C����ǂݍ��ށB
     */
    private void load()
    {
        this.properties_ = new Properties();
        if (this.fileName_ != null)
        {
            try
            {
                File file = new File(this.fileName_);
                FileInputStream stream = new FileInputStream(file);
                this.properties_.load(stream);
                stream.close();

                // �ݒ�t�@�C���i*.conf�j�̂���f�B���N�g�����擾����
                File optionFile = new File(this.fileName_);
                File optionPath = optionFile.getParentFile();
                if (optionPath != null)
                {
                    this.properties_.setProperty(JAVELIN_OPTION_KEY, optionPath.getAbsolutePath());
                }
            }
            catch (IOException e)
            {
                System.err.println("�I�v�V�����t�@�C���̓ǂݍ��݂Ɏ��s���܂����B" + "(" + this.fileName_ + ")");
            }
        }
        else
        {
            System.err.println("�K�v�ȁi*.conf�j�t�@�C�����w�肳��Ă��܂���B");
        }
    }

    /**
     * �w�肳�ꂽ�L�[�ɑΉ����镶�����Ԃ��B
     * �����ݒ肪�s���Ă��Ȃ��Ƃ��ɂ́A�f�t�H���g�l��Ԃ��B
     *
     * @param key �L�[
     * @param defaultValue �f�t�H���g�l
     * @return �l
     */
    public String getString(String key, String defaultValue)
    {
        synchronized (this)
        {
            if (this.properties_ == null)
            {
                load();
            }
        }
        String value = this.properties_.getProperty(key);
        if (value == null)
        {
            value = defaultValue;
        }
        return value;
    }

    /**
     * �w�肳�ꂽ�L�[�ɑΉ����鐔�l��Ԃ��B
     * �����ݒ肪�s���Ă��Ȃ��Ƃ��A���͕s���Ȓl�����͂���Ă���Ƃ��A
     * �f�t�H���g�l��Ԃ��B
     *
     * @param key �L�[
     * @param defaultValue �f�t�H���g�l
     * @return �l
     */
    public int getInteger(String key, int defaultValue)
    {
        String value = getString(key, null);
        if (value != null)
        {
            try
            {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException nfe)
            {
                System.out.println(key + "�ɕs���Ȓl�����͂���܂����B�f�t�H���g�l(" + defaultValue + ")���g�p���܂��B");
                setInteger(key, defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * �w�肳�ꂽ�L�[�ɑΉ����鐔�l��Ԃ��B
     * �����ݒ肪�s���Ă��Ȃ��Ƃ��A���͕s���Ȓl�����͂���Ă���Ƃ��A
     * �f�t�H���g�l��Ԃ��B
     *
     * @param key �L�[
     * @param defaultValue �f�t�H���g�l
     * @return �l
     */
    public long getLong(String key, long defaultValue)
    {
        String value = getString(key, null);
        if (value != null)
        {
            try
            {
                return Long.parseLong(value);
            }
            catch (NumberFormatException nfe)
            {
                System.out.println(key + "�ɕs���Ȓl�����͂���܂����B�f�t�H���g�l(" + defaultValue + ")���g�p���܂��B");
                setLong(key, defaultValue);
            }
        }
        return defaultValue;
    }

    /**
     * �w�肳�ꂽ�L�[�ɑΉ�����Boolean�l��Ԃ��B
     * �����ݒ肪�s���Ă��Ȃ��Ƃ��A���͕s���Ȓl�����͂���Ă���Ƃ��A
     * �f�t�H���g�l��Ԃ��B
     *
     * @param key �L�[
     * @param defaultValue �f�t�H���g�l
     * @return �l
     */
    public boolean getBoolean(String key, boolean defaultValue)
    {
        String value = getString(key, null);
        if (value != null)
        {
            if ("true".equals(value))
            {
                return true;
            }
            if ("false".equals(value))
            {
                return false;
            }
            System.out.println(key + "�ɕs���Ȓl�����͂���܂����B�f�t�H���g�l(" + defaultValue + ")���g�p���܂��B");
            setBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    /**
     * �w�肳�ꂽ�L�[�ɕ�������Z�b�g���܂��B
     *
     * @param key �L�[
     * @param value �l
     */
    public void setString(String key, String value)
    {
        synchronized (this)
        {
            if (this.properties_ == null)
            {
                load();
            }
        }
        this.properties_.setProperty(key, value);
    }

    /**
     * �w�肳�ꂽ�L�[�ɐ��l���Z�b�g���܂��B
     *
     * @param key �L�[
     * @param value �l
     */
    public void setInteger(String key, int value)
    {
        String valueString = String.valueOf(value);
        setString(key, valueString);
    }

    /**
     * �w�肳�ꂽ�L�[�ɐ��l���Z�b�g���܂��B
     *
     * @param key �L�[
     * @param value �l
     */
    public void setLong(String key, long value)
    {
        String valueString = String.valueOf(value);
        setString(key, valueString);
    }

    /**
     * �w�肳�ꂽ�L�[��boolean�l���Z�b�g���܂��B
     *
     * @param key �L�[
     * @param value �l
     */
    public void setBoolean(String key, boolean value)
    {
        String valueString = String.valueOf(value);
        setString(key, valueString);
    }

    /**
     * �w�肳�ꂽ�L�[���ݒ�ɑ��݂��邩�ǂ����𒲂ׂ܂��B
     *
     * @param key �L�[
     * @return true:���݂���Afalse�F���݂��Ȃ��B
     */
    public boolean isKeyExist(String key)
    {
        synchronized (this)
        {
            if (this.properties_ == null)
            {
                load();
            }
        }
        return this.properties_.containsKey(key);
    }

    /**
     * Config�t�@�C������Ԃ��B
     *
     * @return �t�@�C����
     */
    public String getFileName()
    {
        return this.fileName_;
    }

}
