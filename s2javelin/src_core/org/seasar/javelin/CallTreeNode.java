package org.seasar.javelin;

import java.util.ArrayList;
import java.util.List;

import org.seasar.javelin.bean.Invocation;

public class CallTreeNode
{
    private Invocation          invocation_;

    private String              returnValue_;

    private Throwable           throwable_;

    private long                throwTime_;

    private long                startTime_;

    private long                endTime_;

    private long                accumulatedTime_;

    String[]                    args_;

    private StackTraceElement[] stacktrace_;

    private CallTreeNode        parent_;

    private List<CallTreeNode>  children_ = new ArrayList<CallTreeNode>();

    private boolean             isFieldAccess_;

    public Invocation getInvocation()
    {
        return invocation_;
    }

    public void setInvocation(Invocation invocation)
    {
        invocation_ = invocation;
    }

    public String getReturnValue()
    {
        return returnValue_;
    }

    public void setReturnValue(String returnValue)
    {
        returnValue_ = returnValue;
    }

    public long getStartTime()
    {
        return startTime_;
    }

    public void setStartTime(long startTime)
    {
        startTime_ = startTime;
    }

    public long getEndTime()
    {
        return endTime_;
    }

    public void setEndTime(long endTime)
    {
        endTime_ = endTime;
        accumulatedTime_ = endTime_ - startTime_;
    }

    public long getAccumulatedTime()
    {
        return accumulatedTime_;
    }

    public StackTraceElement[] getStacktrace()
    {
        return stacktrace_;
    }

    public void setStacktrace(StackTraceElement[] stacktrace)
    {
        stacktrace_ = stacktrace;
    }

    public CallTreeNode getParent()
    {
        return parent_;
    }

    public void setParent(CallTreeNode parent)
    {
        parent_ = parent;
    }

    public List getChildren()
    {
        return children_;
    }

    public void addChild(CallTreeNode node)
    {
        children_.add(node);
        node.setParent(this);
    }

    public String[] getArgs()
    {
        return args_;
    }

    public void setArgs(String[] args)
    {
        args_ = args;
    }

    /**
     * このノードの親ノードを削除する。
     * 親ノードがない場合は何もしない。
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
     * @return フィールドアクセスならtrue、そうでなければfalseを返す。
     */
    public boolean isFieldAccess()
    {
        return isFieldAccess_;
    }

    /**
     * ノードがフィールドへのアクセスかどうかを示すフラグを取得する。
     * @param isFieldAccess フィールドアクセスならtrue、そうでなければfalse。
     */
    public void setFieldAccess(boolean isFieldAccess)
    {
        this.isFieldAccess_ = isFieldAccess;
    }

    public Throwable getThrowable()
    {
        return throwable_;
    }

    public void setThrowable(Throwable throwable)
    {
        throwable_ = throwable;
    }

    public long getThrowTime()
    {
        return throwTime_;
    }

    public void setThrowTime(long throwTime)
    {
        throwTime_ = throwTime;
    }
}
