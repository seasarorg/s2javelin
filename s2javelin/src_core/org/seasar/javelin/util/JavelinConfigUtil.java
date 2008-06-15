package org.seasar.javelin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    /** Javelin�I�v�V�����L�[1 */
    private static final String JAVELIN_OPTION_KEY_1 = 
    	S2JavelinConfig.JAVELIN_PREFIX + "property";
    
    /** Javelin�I�v�V�����L�[2 */
    private static final String      JAVELIN_OPTION_KEY_2 = 
    	S2JavelinConfig.JAVELIN_PREFIX + "properties";

    /** �ݒ�t�@�C����1 */
    private String                   fileName1_;

    /** �ݒ�t�@�C����2 */
    private String                   fileName2_;
    
    /** Javelin �v���p�e�B */
    private Properties               properties_;

    /** Javelin�̐ݒ�I�u�W�F�N�g */
    private static JavelinConfigUtil configUtil_ = new JavelinConfigUtil();
    
    /** Javelin���sJar�t�@�C���̑��݃f�B���N�g�� */
    private String                   absoluteJarDirectory_;
    
    /** PropertyFile�̃p�X */
    private String                   propertyFilePath_;
    
    /** PropertyFile�̑��݃f�B���N�g�� */
    private String                   propertyFileDirectory_;

    /** Boolean�̒l��ێ�����B */
	private Map<String, Boolean> booleanMap_ = new HashMap<String, Boolean>();

	/** Long�̒l��ێ�����B */
	private Map<String, Long> longMap_ = new HashMap<String, Long>();

	/** Integer�̒l��ێ�����B */
	private Map<String, Integer> intMap_ = new HashMap<String, Integer>();
    
    
    /**
	 * Singleton
	 */
    private JavelinConfigUtil()
    {
        this.fileName1_ = System.getProperty(JAVELIN_OPTION_KEY_1);
        this.fileName2_ = System.getProperty(JAVELIN_OPTION_KEY_2);
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

        String fileName = null;
        
        if (this.fileName1_ != null)
        {
        	fileName = this.fileName1_;
        }
        else if(this.fileName2_ != null)
        {
        	fileName = this.fileName2_;
        }
        else
        {
            fileName = "../conf/javelin.properties";
        }
        
        this.propertyFilePath_ = convertRelPathFromJartoAbsPath(fileName);
        
        if (this.propertyFilePath_ != null)
        {
        	File file = null;
            try
            {
                file = new File(this.propertyFilePath_);
                if (!file.exists())
                {
                    System.err.println("�v���p�e�B�t�@�C�������݂��܂���B" + "(" + file.getAbsolutePath() + ")");
                	return;
                }
                FileInputStream stream = new FileInputStream(file);
                this.properties_.load(stream);
                stream.close();

                // �ݒ�t�@�C���i*.conf�j�̂���f�B���N�g�����擾����
                File optionFile = new File(this.propertyFilePath_);
                File optionPath = optionFile.getParentFile();
                if (optionPath != null)
                {
                    setPropertyFileDirectory(optionPath.getAbsolutePath());
                    this.properties_.setProperty(JAVELIN_OPTION_KEY_1, optionPath.getAbsolutePath());
                }
            }
            catch (IOException ex)
            {
            	if (file != null)
            	{
                    System.err.println("�v���p�e�B�t�@�C���̓ǂݍ��݂Ɏ��s���܂����B" + "(" + file.getAbsolutePath() + ")");
            	}
            }
        }
        else
        {
            System.err.println("�K�v�ȃv���p�e�B(-Djavelin.property)���w�肳��Ă��܂���B");
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
    	if(intMap_.containsKey(key) == false)
    	{
	        String value = getString(key, null);
	        Integer intValue = Integer.valueOf(defaultValue);
	        if (value != null)
	        {
	            try
	            {
	            	intValue = Integer.valueOf(value);
	            }
	            catch (NumberFormatException nfe)
	            {
	                System.out.println(key + "�ɕs���Ȓl�����͂���܂����B�f�t�H���g�l(" + intValue + ")���g�p���܂��B");
	                setInteger(key, intValue);
	            }
	        }
	        
	        intMap_.put(key, intValue);
    	}
    	
        return intMap_.get(key).intValue();
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
    	if(longMap_.containsKey(key) == false)
    	{
	        String value = getString(key, null);
	        Long longValue = Long.valueOf(defaultValue);
	        if (value != null)
	        {
	            try
	            {
	            	longValue = Long.valueOf(value);
	            }
	            catch (NumberFormatException nfe)
	            {
	                System.out.println(key + "�ɕs���Ȓl�����͂���܂����B�f�t�H���g�l(" + defaultValue + ")���g�p���܂��B");
	                setLong(key, longValue);
	            }
	        }
	        
            longMap_.put(key, longValue);
    	}
    	
        return longMap_.get(key).longValue();
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
    	if(booleanMap_.containsKey(key) == false)
    	{
	        String value = getString(key, null);
	
	        boolean result = defaultValue;
	        if (value != null)
	        {
	            if ("true".equals(value))
	            {
	            	result = true;
	            }
	            else if ("false".equals(value))
	            {
	            	result = false;
	            }
	            else
	            {
		            System.out.println(key + "�ɕs���Ȓl�����͂���܂����B�f�t�H���g�l(" + defaultValue + ")���g�p���܂��B");
		            setBoolean(key, result);
	            }
	        }
	        
	        booleanMap_.put(key, result);
    	}
    	
		return booleanMap_.get(key).booleanValue();
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
        return this.fileName1_;
    }
    
    /**
     * Javelin���sJar�t�@�C���̑��݃f�B���N�g����Ԃ��B
     * @return
     */
    public String getAbsoluteJarDirectory()
    {
        return this.absoluteJarDirectory_;
    }
    
    /**
     * Javelin���sJar�t�@�C���̑��݃f�B���N�g����ݒ肷��B
     * 
     * @param absoluteJarDirectory
     */
    public void setAbsoluteJarDirectory(String absoluteJarDirectory)
    {
        this.absoluteJarDirectory_ = absoluteJarDirectory;
    }
    
    /**
     * �v���p�e�B�t�@�C���̑��݃f�B���N�g����Ԃ��B
     * @return
     */
    public String getPropertyFileDirectory()
    {
        return this.propertyFileDirectory_;
    }
    
    /**
     * �v���p�e�B�t�@�C���̑��݃f�B���N�g����ݒ肷��B
     * 
     * @param propertyFileDirectory
     */
    public void setPropertyFileDirectory(String propertyFileDirectory)
    {
        this.propertyFileDirectory_ = propertyFileDirectory;
    }
    
    /**
     * �v���p�e�B�t�@�C���̃p�X��ݒ肷��B
     * 
     * @param propertyFilePath
     */
    public void setPropertyFilePath(String propertyFilePath)
    {
        this.propertyFilePath_ = propertyFilePath;
    }
    
    /**
     * �v���p�e�B�t�@�C���̃p�X��Ԃ��B
     * @return
     */
    public String getPropertyFilePath()
    {
        return this.propertyFilePath_;
    }
    
    /**
     * Javelin���sJar�t�@�C������̑��΃p�X��
     * ��΃p�X�ɕϊ�����B
     * 
     * @param relativePath Javelin���sJar�t�@�C������̑��΃p�X
     * @return ��΃p�X
     */
    public String convertRelPathFromJartoAbsPath(String relativePath)
    {
        if(relativePath == null)
        {
            return null;
        }

        File relativeFile = new File(relativePath);
        if(relativeFile.isAbsolute())
        {
            return relativePath;
        }        
        File targetPath = new File(this.absoluteJarDirectory_, relativePath);
        
        String canonicalPath;
        try
        {
            canonicalPath = targetPath.getCanonicalPath();
        }
        catch (IOException ioe)
        {
            return targetPath.getAbsolutePath();
        }
        
        return canonicalPath;
    }
    
    /**
     * �v���p�e�B�t�@�C������̑��΃p�X��
     * ��΃p�X�ɕϊ�����B
     * 
     * @param relativePath �v���p�e�B�t�@�C������̑��΃p�X
     * @return ��΃p�X
     */
    public String convertRelativePathtoAbsolutePath(String relativePath)
    {
        if(relativePath == null)
        {
            return null;
        }
        
        File relativeFile = new File(relativePath);
        if(relativeFile.isAbsolute())
        {
            return relativePath;
        }
        
        File targetPath = new File(this.propertyFileDirectory_, relativePath);
        
        String canonicalPath;
        try
        {
            canonicalPath = targetPath.getCanonicalPath();
        }
        catch (IOException ioe)
        {
            return targetPath.getAbsolutePath();
        }
        
        return canonicalPath;
    }

    public void update()
    {
    	this.longMap_ = new HashMap<String, Long>();
    	this.booleanMap_ = new HashMap<String, Boolean>();
    	this.intMap_ = new HashMap<String, Integer>();
    }
}
