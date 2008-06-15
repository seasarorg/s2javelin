package org.seasar.javelin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.seasar.javelin.S2JavelinConfig;

/**
 * Javelin全体の設定を保持するクラス。(Singleton)
 *
 * 値を初めて取得しようとしたとき（getterメソッドを初めて呼んだとき）に
 * ファイルから設定をロードする。
 *
 * @author sakamoto
 */
public class JavelinConfigUtil
{
    /** Javelinオプションキー1 */
    private static final String JAVELIN_OPTION_KEY_1 = 
    	S2JavelinConfig.JAVELIN_PREFIX + "property";
    
    /** Javelinオプションキー2 */
    private static final String      JAVELIN_OPTION_KEY_2 = 
    	S2JavelinConfig.JAVELIN_PREFIX + "properties";

    /** 設定ファイル名1 */
    private String                   fileName1_;

    /** 設定ファイル名2 */
    private String                   fileName2_;
    
    /** Javelin プロパティ */
    private Properties               properties_;

    /** Javelinの設定オブジェクト */
    private static JavelinConfigUtil configUtil_ = new JavelinConfigUtil();
    
    /** Javelin実行Jarファイルの存在ディレクトリ */
    private String                   absoluteJarDirectory_;
    
    /** PropertyFileのパス */
    private String                   propertyFilePath_;
    
    /** PropertyFileの存在ディレクトリ */
    private String                   propertyFileDirectory_;

    /** Booleanの値を保持する。 */
	private Map<String, Boolean> booleanMap_ = new HashMap<String, Boolean>();

	/** Longの値を保持する。 */
	private Map<String, Long> longMap_ = new HashMap<String, Long>();

	/** Integerの値を保持する。 */
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
     * JavelinConfigUtilのインスタンスを返す。
     *
     * @return インスタンス(Singleton)
     */
    public static JavelinConfigUtil getInstance()
    {
        return configUtil_;
    }

    /**
     * 設定ファイルを読み込む。
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
                    System.err.println("プロパティファイルが存在しません。" + "(" + file.getAbsolutePath() + ")");
                	return;
                }
                FileInputStream stream = new FileInputStream(file);
                this.properties_.load(stream);
                stream.close();

                // 設定ファイル（*.conf）のあるディレクトリを取得する
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
                    System.err.println("プロパティファイルの読み込みに失敗しました。" + "(" + file.getAbsolutePath() + ")");
            	}
            }
        }
        else
        {
            System.err.println("必要なプロパティ(-Djavelin.property)が指定されていません。");
        }
    }

    /**
     * 指定されたキーに対応する文字列を返す。
     * 初期設定が行われていないときには、デフォルト値を返す。
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return 値
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
     * 指定されたキーに対応する数値を返す。
     * 初期設定が行われていないとき、又は不正な値が入力されているとき、
     * デフォルト値を返す。
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return 値
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
	                System.out.println(key + "に不正な値が入力されました。デフォルト値(" + intValue + ")を使用します。");
	                setInteger(key, intValue);
	            }
	        }
	        
	        intMap_.put(key, intValue);
    	}
    	
        return intMap_.get(key).intValue();
   }

    /**
     * 指定されたキーに対応する数値を返す。
     * 初期設定が行われていないとき、又は不正な値が入力されているとき、
     * デフォルト値を返す。
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return 値
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
	                System.out.println(key + "に不正な値が入力されました。デフォルト値(" + defaultValue + ")を使用します。");
	                setLong(key, longValue);
	            }
	        }
	        
            longMap_.put(key, longValue);
    	}
    	
        return longMap_.get(key).longValue();
    }

    /**
     * 指定されたキーに対応するBoolean値を返す。
     * 初期設定が行われていないとき、又は不正な値が入力されているとき、
     * デフォルト値を返す。
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return 値
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
		            System.out.println(key + "に不正な値が入力されました。デフォルト値(" + defaultValue + ")を使用します。");
		            setBoolean(key, result);
	            }
	        }
	        
	        booleanMap_.put(key, result);
    	}
    	
		return booleanMap_.get(key).booleanValue();
    }

    /**
     * 指定されたキーに文字列をセットします。
     *
     * @param key キー
     * @param value 値
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
     * 指定されたキーに数値をセットします。
     *
     * @param key キー
     * @param value 値
     */
    public void setInteger(String key, int value)
    {
        String valueString = String.valueOf(value);
        setString(key, valueString);
    }

    /**
     * 指定されたキーに数値をセットします。
     *
     * @param key キー
     * @param value 値
     */
    public void setLong(String key, long value)
    {
        String valueString = String.valueOf(value);
        setString(key, valueString);
    }

    /**
     * 指定されたキーにboolean値をセットします。
     *
     * @param key キー
     * @param value 値
     */
    public void setBoolean(String key, boolean value)
    {
        String valueString = String.valueOf(value);
        setString(key, valueString);
    }

    /**
     * 指定されたキーが設定に存在するかどうかを調べます。
     *
     * @param key キー
     * @return true:存在する、false：存在しない。
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
     * Configファイル名を返す。
     *
     * @return ファイル名
     */
    public String getFileName()
    {
        return this.fileName1_;
    }
    
    /**
     * Javelin実行Jarファイルの存在ディレクトリを返す。
     * @return
     */
    public String getAbsoluteJarDirectory()
    {
        return this.absoluteJarDirectory_;
    }
    
    /**
     * Javelin実行Jarファイルの存在ディレクトリを設定する。
     * 
     * @param absoluteJarDirectory
     */
    public void setAbsoluteJarDirectory(String absoluteJarDirectory)
    {
        this.absoluteJarDirectory_ = absoluteJarDirectory;
    }
    
    /**
     * プロパティファイルの存在ディレクトリを返す。
     * @return
     */
    public String getPropertyFileDirectory()
    {
        return this.propertyFileDirectory_;
    }
    
    /**
     * プロパティファイルの存在ディレクトリを設定する。
     * 
     * @param propertyFileDirectory
     */
    public void setPropertyFileDirectory(String propertyFileDirectory)
    {
        this.propertyFileDirectory_ = propertyFileDirectory;
    }
    
    /**
     * プロパティファイルのパスを設定する。
     * 
     * @param propertyFilePath
     */
    public void setPropertyFilePath(String propertyFilePath)
    {
        this.propertyFilePath_ = propertyFilePath;
    }
    
    /**
     * プロパティファイルのパスを返す。
     * @return
     */
    public String getPropertyFilePath()
    {
        return this.propertyFilePath_;
    }
    
    /**
     * Javelin実行Jarファイルからの相対パスを
     * 絶対パスに変換する。
     * 
     * @param relativePath Javelin実行Jarファイルからの相対パス
     * @return 絶対パス
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
     * プロパティファイルからの相対パスを
     * 絶対パスに変換する。
     * 
     * @param relativePath プロパティファイルからの相対パス
     * @return 絶対パス
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
