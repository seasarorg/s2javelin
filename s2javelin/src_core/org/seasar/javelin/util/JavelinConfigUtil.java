package org.seasar.javelin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    /** Javelinオプションキー */
    private static final String      JAVELIN_OPTION_KEY = S2JavelinConfig.JAVELIN_PREFIX
                                                                + "property";

    /** 設定ファイル名 */
    private String                   fileName_;

    /** Javelin プロパティ */
    private Properties               properties_;

    /** Javelinの設定オブジェクト */
    private static JavelinConfigUtil configUtil_        = new JavelinConfigUtil();

    /**
     * Singleton
     */
    private JavelinConfigUtil()
    {
        this.fileName_ = System.getProperty(JAVELIN_OPTION_KEY);
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
        if (this.fileName_ != null)
        {
            try
            {
                File file = new File(this.fileName_);
                FileInputStream stream = new FileInputStream(file);
                this.properties_.load(stream);
                stream.close();

                // 設定ファイル（*.conf）のあるディレクトリを取得する
                File optionFile = new File(this.fileName_);
                File optionPath = optionFile.getParentFile();
                if (optionPath != null)
                {
                    this.properties_.setProperty(JAVELIN_OPTION_KEY, optionPath.getAbsolutePath());
                }
            }
            catch (IOException e)
            {
                System.err.println("オプションファイルの読み込みに失敗しました。" + "(" + this.fileName_ + ")");
            }
        }
        else
        {
            System.err.println("必要な（*.conf）ファイルが指定されていません。");
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
        String value = getString(key, null);
        if (value != null)
        {
            try
            {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException nfe)
            {
                System.out.println(key + "に不正な値が入力されました。デフォルト値(" + defaultValue + ")を使用します。");
                setInteger(key, defaultValue);
            }
        }
        return defaultValue;
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
        String value = getString(key, null);
        if (value != null)
        {
            try
            {
                return Long.parseLong(value);
            }
            catch (NumberFormatException nfe)
            {
                System.out.println(key + "に不正な値が入力されました。デフォルト値(" + defaultValue + ")を使用します。");
                setLong(key, defaultValue);
            }
        }
        return defaultValue;
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
            System.out.println(key + "に不正な値が入力されました。デフォルト値(" + defaultValue + ")を使用します。");
            setBoolean(key, defaultValue);
        }
        return defaultValue;
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
        return this.fileName_;
    }

}
