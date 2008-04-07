package org.seasar.javelin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.seasar.javelin.util.StatsUtil;

/**
 * Javelinログ出力用にコールスタックを記録するための、ツリークラス。
 * 
 * @author yamasaki
 */
public class CallTree
{
    /** CallTreeノード */
    private CallTreeNode        rootNode_;

    /** ThreadID */
    private String              threadID_;

    /** メソッド呼び出しのルートノードにつける名前。 */
    private String              rootCallerName_  = "unknown";

    /** メソッド呼び出しのエンドノードの名前が決定できない場合につける名前。 */
    private String              endCalleeName_   = "unknown";

    /** 例外の原因 */
    private Throwable           cause_;

    /**  */
    private List<Callback>      callbackList_    = new ArrayList<Callback>();

    /**  */
    private Map<String, Object> flagMap_         = new HashMap<String, Object>();

    /**  */
    private Map<String, Object> loggingValueMap_ = new TreeMap<String, Object>();

    /**
     * コンストラクタ。 スレッドIDを設定する。；
     */
    public CallTree()
    {
        this.threadID_ = StatsUtil.createThreadIDText();
    }

    /**
     * ルートノードを取得する。
     * 
     * @return ルートノード
     */
    public CallTreeNode getRootNode()
    {
        return this.rootNode_;
    }

    /**
     * ルートノードを設定する。
     * 
     * @param rootNode ルートノード
     */
    public void setRootNode(CallTreeNode rootNode)
    {
        this.rootNode_ = rootNode;
    }

    /**
     * ThreadIDを取得する。
     * 
     * @return ThreadID
     */
    public String getThreadID()
    {
        return this.threadID_;
    }

    /**
     * ThreadIDを設定する。
     * 
     * @param threadID スレッドID
     */
    public void setThreadID(String threadID)
    {
        this.threadID_ = threadID;
    }

    /**
     * エンドノードを取得する。
     * 
     * @return エンドノード
     */
    public String getEndCalleeName()
    {
        return this.endCalleeName_;
    }

    /**
     * エンドノードを設定する。
     * 
     * @param endCalleeName エンドノード
     */
    public void setEndCalleeName(String endCalleeName)
    {
        if (endCalleeName == null)
        {
            return;
        }
        this.endCalleeName_ = endCalleeName;
    }

    /**
     * 呼び出し元のルートノード名を取得する。
     * 
     * @return 呼び出し元のルートノード名
     */
    public String getRootCallerName()
    {
        return this.rootCallerName_;
    }

    /**
     * 呼び出し元のルートノード名を設定する。
     * 
     * @param rootCallerName 呼び出し元のルートノード名。
     */
    public void setRootCallerName(String rootCallerName)
    {
        if (rootCallerName == null)
        {
            return;
        }
        this.rootCallerName_ = rootCallerName;
    }

    /**
     * CallBackを追加する。
     * @param callback CallBack
     */
    public void addCallback(Callback callback)
    {
        this.callbackList_.add(callback);
    }

    /**
     * CallBackを実行する。
     */
    public void executeCallback()
    {
        for (Callback callback : this.callbackList_)
        {
            try
            {
                callback.execute();
            }
            catch (Exception ex)
            {
                SystemLogger.getInstance().warn(ex);
            }
        }

        this.callbackList_.clear();
    }

    /**
     * フラグを設定する。
     * @param flag フラグ
     * @param value 値
     * @return フラグ
     */
    public boolean setFlag(String flag, Object value)
    {
        return (this.flagMap_.put(flag, value) != null);
    }

    /**
     * フラグを取得する。
     * @param flag フラグ
     * @return フラグ
     */
    public Object getFlag(String flag)
    {
        return this.flagMap_.get(flag);
    }

    /**
     * flagがMapに登録されているか返す。
     * @param flag フラグ
     * @return true:キーがMapに登録されている、false:キーがMapに登録されていない。
     */
    public boolean containsFlag(String flag)
    {
        return this.flagMap_.containsKey(flag);
    }

    /**
     * フラグの値をMapから除外する。
     * @param flag フラグ
     * @return true:除外される、false:除外されない。
     */
    public boolean removeFlag(String flag)
    {
        return (this.flagMap_.remove(flag) != null);
    }

    /**
     * ログ値を設定する。
     * @param key キー
     * @param value 値
     */
    public void setLoggingValue(String key, Object value)
    {
        this.loggingValueMap_.put(key, value);
    }

    /**
     * Mapからキーを取得する。
     * @return  キー配列
     */
    public String[] getLoggingKeys()
    {
        Set<String> keySet = this.loggingValueMap_.keySet();
        String[] keys = keySet.toArray(new String[keySet.size()]);

        return keys;
    }

    /**
     * Mapからキーに対応する値を取得する。
     * @param key キー
     * @return キーの値
     */
    public Object getLoggingValue(String key)
    {
        return this.loggingValueMap_.get(key);
    }

    /**
     * 例外の原因を取得する。
     * @return 例外の原因
     */
    public Throwable getCause()
    {
        return this.cause_;
    }

    /**
     * 例外の原因を設定する。
     * @param cause 例外の原因
     */
    public void setCause(Throwable cause)
    {
        this.cause_ = cause;
    }
}
