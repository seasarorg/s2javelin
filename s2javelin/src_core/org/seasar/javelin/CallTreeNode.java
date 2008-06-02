package org.seasar.javelin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.seasar.javelin.bean.Invocation;

/**
 * メソッド呼び出し情報
 * @author eriguchi
 *
 */
public class CallTreeNode
{
    public CallTreeNode()
    {
        
    }
    
    /**  */
    private Invocation          invocation_;

    /** 戻り値 */
    private String              returnValue_;

    /** 例外 */
    private Throwable           throwable_;

    /** 例外発生回数 */
    private long                throwTime_;

    /** 開始時刻 */
    private long                startTime_;

    /** 終了時刻 */
    private long                endTime_;

    /** 累積時間 */
    private long                accumulatedTime_;

    /** CPU時間 */
    private long                cpuTime_;

    /** ユーザ時間 */
    private long                userTime_;

    /** 開始時のVMのステータス */
    private VMStatus            startVmStatus_;

    /** 終了時のVMのステータス */
    private VMStatus            endVmStatus_;

    /** 引数 */
    private String[]            args_;

    /** スタックトレース */
    private StackTraceElement[] stacktrace_;

    /** CallTreeNodeの親ノード */
    private CallTreeNode        parent_;

    /** CallTreeNodeの子ノード */
    private List<CallTreeNode>  children_ = new ArrayList<CallTreeNode>();

    /** フィールドアクセス */
    private boolean             isFieldAccess_;

    /**  */
    private Map<String, Object> loggingValueMap_ = new TreeMap<String, Object>();

    /**
     * InvoCation
     * invocationを取得する。
     * @return Invocation
     */
    public Invocation getInvocation()
    {
        return this.invocation_;
    }

    /**
     * Invocationを設定する。
     * @param invocation Invocation
     */
    public void setInvocation(Invocation invocation)
    {
        this.invocation_ = invocation;
    }

    /**
     * 戻り値を取得する。
     * @return 戻り値
     */
    public String getReturnValue()
    {
        return this.returnValue_;
    }

    /**
     * 戻り値を設定する。
     * @param returnValue 戻り値
     */
    public void setReturnValue(String returnValue)
    {
        this.returnValue_ = returnValue;
    }

    /**
     * メソッド開始時刻を取得する。
     * @return メソッド開始時刻
     */
    public long getStartTime()
    {
        return this.startTime_;
    }

    /**
     * メソッド開始時刻を取得する。
     * @param startTime メソッド開始時刻
     */
    public void setStartTime(long startTime)
    {
        this.startTime_ = startTime;
    }

    /**
     * メソッドの終了時刻を取得する。
     * @return メソッドの終了時刻
     */
    public long getEndTime()
    {
        return this.endTime_;
    }

    /**
     * メソッドの終了時刻を設定する。
     * @param endTime メソッドの終了時刻
     */
    public void setEndTime(long endTime)
    {
        this.endTime_ = endTime;
        this.accumulatedTime_ = this.endTime_ - this.startTime_;
        this.invocation_.setAccumulatedTime(this.accumulatedTime_);
    }

    /**
     * 累積時間を取得する。
     * @return 累積時間
     */
    public long getAccumulatedTime()
    {
        return this.accumulatedTime_;
    }

    /**
     * CPU時間を取得す。る
     * @param cpuTime CPU時間
     */
    public void setCpuTime(long cpuTime)
    {
        this.cpuTime_ = cpuTime;
    }

    /**
     * CPU時間を取得する。
     * @return CPU時間
     */
    public long getCpuTime()
    {
        return this.cpuTime_;
    }

    /**
     * StackTraceを取得する。
     * @return StackTrace
     */
    public StackTraceElement[] getStacktrace()
    {
        return this.stacktrace_;
    }

    /**
     * StackTraceを設定する。
     * @param stacktrace StackTrace
     */
    public void setStacktrace(StackTraceElement[] stacktrace)
    {
        this.stacktrace_ = stacktrace;
    }

    /**
     * CallTreeNodeの親を取得する。
     * @return CallTreeNodeの親
     */
    public CallTreeNode getParent()
    {
        return this.parent_;
    }

    /**
     * CallTreeNodeの親を設定する。
     * @param parent CallTreeNodeの親
     */
    public void setParent(CallTreeNode parent)
    {
        this.parent_ = parent;
    }

    /**
     * CallTreeNOdeの子を取得する。
     * @return CallTreeNodeの子
     */
    public List<CallTreeNode> getChildren()
    {
        return this.children_;
    }

    /**
     * CallTreeNOdeの子を設定する。
     * @param node CallTreeNOdeの子
     */
    public void addChild(CallTreeNode node)
    {
        this.children_.add(node);
        node.setParent(this);
    }

    /**
     * 引数を取得する。
     * @return 引数
     */
    public String[] getArgs()
    {
        return this.args_;
    }

    /**
     * 引数を設定する。
     * @param args 引数
     */
    public void setArgs(String[] args)
    {
        this.args_ = args;
    }

    /**
     * このノードの親ノードを削除する。 親ノードがない場合は何もしない。
     * 
     * @param tree ツリー
     */
    public void removeParent(CallTree tree)
    {
        CallTreeNode parent = getParent();
        if (parent != null)
        {
            // 親の親があれば、親の親の中に自分を入れる。
            // 親の親がなければ、自分がツリーのルートになる。
            CallTreeNode grandParent = parent.getParent();
            if (grandParent != null)
            {
                int childIndex = grandParent.getChildIndex(parent);
                if (childIndex != -1)
                {
                    grandParent.children_.set(childIndex, this);
                    setParent(grandParent);
                }
            }
            else
            {
                tree.setRootNode(this);
                setParent(null);
            }
        }
    }

    /**
     * このノードをツリーから削除する。
     * 
     * @return このノードの親
     */
    public CallTreeNode remove()
    {
        CallTreeNode parent = getParent();
        if (parent != null)
        {
            parent.children_.remove(this);
        }
        return parent;
    }

    /**
     * 指定されたノードが何番目の子ノードかを調べる。
     * 
     * @param node ノード
     * @return ノードの番号。子ノードでなければ -1
     */
    private int getChildIndex(CallTreeNode node)
    {
        return this.children_.indexOf(node);
    }

    /**
     * ノードがフィールドへのアクセスかどうかを示すフラグを取得する。
     * 
     * @return フィールドアクセスならtrue、そうでなければfalseを返す。
     */
    public boolean isFieldAccess()
    {
        return this.isFieldAccess_;
    }

    /**
     * ノードがフィールドへのアクセスかどうかを示すフラグを取得する。
     * 
     * @param isFieldAccess フィールドアクセスならtrue、そうでなければfalse。
     */
    public void setFieldAccess(boolean isFieldAccess)
    {
        this.isFieldAccess_ = isFieldAccess;
    }

    /**
     * 例外を取得する。
     * @return 例外
     */
    public Throwable getThrowable()
    {
        return this.throwable_;
    }

    /**
     * 例外を設定する。
     * @param throwable 例外
     */
    public void setThrowable(Throwable throwable)
    {
        this.throwable_ = throwable;
    }

    /**
     * 例外発生回数を取得する。
     * @return 例外発生回数
     */
    public long getThrowTime()
    {
        return this.throwTime_;
    }

    /**
     * 例外発生回数を設定する。
     * @param throwTime 例外発生回数。
     */
    public void setThrowTime(long throwTime)
    {
        this.throwTime_ = throwTime;
    }

    /**
     * VMのステータスを取得する。
     * @return VMのステータス
     */
    public VMStatus getEndVmStatus()
    {
        return this.endVmStatus_;
    }

    /**
     * VMのステータスを設定する。
     * @return VMのステータス
     */
    public VMStatus getStartVmStatus()
    {
        return this.startVmStatus_;
    }

    /**
     * 終了時のVMのステータス設定する。
     * @param endVmStatus 終了時のVMのステータス
     */
    public void setEndVmStatus(VMStatus endVmStatus)
    {
        this.endVmStatus_ = endVmStatus;
    }

    /**
     * 開始時のVMのステータス設定する。
     * @param startVmStatus 開始時のVMのステータス
     */
    public void setStartVmStatus(VMStatus startVmStatus)
    {
        this.startVmStatus_ = startVmStatus;
    }

    /**
     * ユーザ時間を取得する。
     * @return ユーザ時間
     */
    public long getUserTime()
    {
        return this.userTime_;
    }

    /**
     * ユーザ時間を設定する。
     * @param userTime ユーザ時間
     */
    public void setUserTime(long userTime)
    {
        this.userTime_ = userTime;
    }


    /**
     * ログ値を設定する。
     * 
     * @param key キー
     * @param value 値
     */
    public void setLoggingValue(String key, Object value)
    {
        this.loggingValueMap_.put(key, value);
    }

    /**
     * Mapからキーを取得する。
     * 
     * @return キー配列
     */
    public String[] getLoggingKeys()
    {
        Set<String> keySet = this.loggingValueMap_.keySet();
        String[] keys = keySet.toArray(new String[keySet.size()]);

        return keys;
    }

    /**
     * Mapからキーに対応する値を取得する。
     * 
     * @param key キー
     * @return キーの値
     */
    public Object getLoggingValue(String key)
    {
        return this.loggingValueMap_.get(key);
    }
}
