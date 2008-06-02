package org.seasar.javelin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.seasar.javelin.bean.Invocation;

/**
 * ���\�b�h�Ăяo�����
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

    /** �߂�l */
    private String              returnValue_;

    /** ��O */
    private Throwable           throwable_;

    /** ��O������ */
    private long                throwTime_;

    /** �J�n���� */
    private long                startTime_;

    /** �I������ */
    private long                endTime_;

    /** �ݐώ��� */
    private long                accumulatedTime_;

    /** CPU���� */
    private long                cpuTime_;

    /** ���[�U���� */
    private long                userTime_;

    /** �J�n����VM�̃X�e�[�^�X */
    private VMStatus            startVmStatus_;

    /** �I������VM�̃X�e�[�^�X */
    private VMStatus            endVmStatus_;

    /** ���� */
    private String[]            args_;

    /** �X�^�b�N�g���[�X */
    private StackTraceElement[] stacktrace_;

    /** CallTreeNode�̐e�m�[�h */
    private CallTreeNode        parent_;

    /** CallTreeNode�̎q�m�[�h */
    private List<CallTreeNode>  children_ = new ArrayList<CallTreeNode>();

    /** �t�B�[���h�A�N�Z�X */
    private boolean             isFieldAccess_;

    /**  */
    private Map<String, Object> loggingValueMap_ = new TreeMap<String, Object>();

    /**
     * InvoCation
     * invocation���擾����B
     * @return Invocation
     */
    public Invocation getInvocation()
    {
        return this.invocation_;
    }

    /**
     * Invocation��ݒ肷��B
     * @param invocation Invocation
     */
    public void setInvocation(Invocation invocation)
    {
        this.invocation_ = invocation;
    }

    /**
     * �߂�l���擾����B
     * @return �߂�l
     */
    public String getReturnValue()
    {
        return this.returnValue_;
    }

    /**
     * �߂�l��ݒ肷��B
     * @param returnValue �߂�l
     */
    public void setReturnValue(String returnValue)
    {
        this.returnValue_ = returnValue;
    }

    /**
     * ���\�b�h�J�n�������擾����B
     * @return ���\�b�h�J�n����
     */
    public long getStartTime()
    {
        return this.startTime_;
    }

    /**
     * ���\�b�h�J�n�������擾����B
     * @param startTime ���\�b�h�J�n����
     */
    public void setStartTime(long startTime)
    {
        this.startTime_ = startTime;
    }

    /**
     * ���\�b�h�̏I���������擾����B
     * @return ���\�b�h�̏I������
     */
    public long getEndTime()
    {
        return this.endTime_;
    }

    /**
     * ���\�b�h�̏I��������ݒ肷��B
     * @param endTime ���\�b�h�̏I������
     */
    public void setEndTime(long endTime)
    {
        this.endTime_ = endTime;
        this.accumulatedTime_ = this.endTime_ - this.startTime_;
        this.invocation_.setAccumulatedTime(this.accumulatedTime_);
    }

    /**
     * �ݐώ��Ԃ��擾����B
     * @return �ݐώ���
     */
    public long getAccumulatedTime()
    {
        return this.accumulatedTime_;
    }

    /**
     * CPU���Ԃ��擾���B��
     * @param cpuTime CPU����
     */
    public void setCpuTime(long cpuTime)
    {
        this.cpuTime_ = cpuTime;
    }

    /**
     * CPU���Ԃ��擾����B
     * @return CPU����
     */
    public long getCpuTime()
    {
        return this.cpuTime_;
    }

    /**
     * StackTrace���擾����B
     * @return StackTrace
     */
    public StackTraceElement[] getStacktrace()
    {
        return this.stacktrace_;
    }

    /**
     * StackTrace��ݒ肷��B
     * @param stacktrace StackTrace
     */
    public void setStacktrace(StackTraceElement[] stacktrace)
    {
        this.stacktrace_ = stacktrace;
    }

    /**
     * CallTreeNode�̐e���擾����B
     * @return CallTreeNode�̐e
     */
    public CallTreeNode getParent()
    {
        return this.parent_;
    }

    /**
     * CallTreeNode�̐e��ݒ肷��B
     * @param parent CallTreeNode�̐e
     */
    public void setParent(CallTreeNode parent)
    {
        this.parent_ = parent;
    }

    /**
     * CallTreeNOde�̎q���擾����B
     * @return CallTreeNode�̎q
     */
    public List<CallTreeNode> getChildren()
    {
        return this.children_;
    }

    /**
     * CallTreeNOde�̎q��ݒ肷��B
     * @param node CallTreeNOde�̎q
     */
    public void addChild(CallTreeNode node)
    {
        this.children_.add(node);
        node.setParent(this);
    }

    /**
     * �������擾����B
     * @return ����
     */
    public String[] getArgs()
    {
        return this.args_;
    }

    /**
     * ������ݒ肷��B
     * @param args ����
     */
    public void setArgs(String[] args)
    {
        this.args_ = args;
    }

    /**
     * ���̃m�[�h�̐e�m�[�h���폜����B �e�m�[�h���Ȃ��ꍇ�͉������Ȃ��B
     * 
     * @param tree �c���[
     */
    public void removeParent(CallTree tree)
    {
        CallTreeNode parent = getParent();
        if (parent != null)
        {
            // �e�̐e������΁A�e�̐e�̒��Ɏ���������B
            // �e�̐e���Ȃ���΁A�������c���[�̃��[�g�ɂȂ�B
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
     * ���̃m�[�h���c���[����폜����B
     * 
     * @return ���̃m�[�h�̐e
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
     * �w�肳�ꂽ�m�[�h�����Ԗڂ̎q�m�[�h���𒲂ׂ�B
     * 
     * @param node �m�[�h
     * @return �m�[�h�̔ԍ��B�q�m�[�h�łȂ���� -1
     */
    private int getChildIndex(CallTreeNode node)
    {
        return this.children_.indexOf(node);
    }

    /**
     * �m�[�h���t�B�[���h�ւ̃A�N�Z�X���ǂ����������t���O���擾����B
     * 
     * @return �t�B�[���h�A�N�Z�X�Ȃ�true�A�����łȂ����false��Ԃ��B
     */
    public boolean isFieldAccess()
    {
        return this.isFieldAccess_;
    }

    /**
     * �m�[�h���t�B�[���h�ւ̃A�N�Z�X���ǂ����������t���O���擾����B
     * 
     * @param isFieldAccess �t�B�[���h�A�N�Z�X�Ȃ�true�A�����łȂ����false�B
     */
    public void setFieldAccess(boolean isFieldAccess)
    {
        this.isFieldAccess_ = isFieldAccess;
    }

    /**
     * ��O���擾����B
     * @return ��O
     */
    public Throwable getThrowable()
    {
        return this.throwable_;
    }

    /**
     * ��O��ݒ肷��B
     * @param throwable ��O
     */
    public void setThrowable(Throwable throwable)
    {
        this.throwable_ = throwable;
    }

    /**
     * ��O�����񐔂��擾����B
     * @return ��O������
     */
    public long getThrowTime()
    {
        return this.throwTime_;
    }

    /**
     * ��O�����񐔂�ݒ肷��B
     * @param throwTime ��O�����񐔁B
     */
    public void setThrowTime(long throwTime)
    {
        this.throwTime_ = throwTime;
    }

    /**
     * VM�̃X�e�[�^�X���擾����B
     * @return VM�̃X�e�[�^�X
     */
    public VMStatus getEndVmStatus()
    {
        return this.endVmStatus_;
    }

    /**
     * VM�̃X�e�[�^�X��ݒ肷��B
     * @return VM�̃X�e�[�^�X
     */
    public VMStatus getStartVmStatus()
    {
        return this.startVmStatus_;
    }

    /**
     * �I������VM�̃X�e�[�^�X�ݒ肷��B
     * @param endVmStatus �I������VM�̃X�e�[�^�X
     */
    public void setEndVmStatus(VMStatus endVmStatus)
    {
        this.endVmStatus_ = endVmStatus;
    }

    /**
     * �J�n����VM�̃X�e�[�^�X�ݒ肷��B
     * @param startVmStatus �J�n����VM�̃X�e�[�^�X
     */
    public void setStartVmStatus(VMStatus startVmStatus)
    {
        this.startVmStatus_ = startVmStatus;
    }

    /**
     * ���[�U���Ԃ��擾����B
     * @return ���[�U����
     */
    public long getUserTime()
    {
        return this.userTime_;
    }

    /**
     * ���[�U���Ԃ�ݒ肷��B
     * @param userTime ���[�U����
     */
    public void setUserTime(long userTime)
    {
        this.userTime_ = userTime;
    }


    /**
     * ���O�l��ݒ肷��B
     * 
     * @param key �L�[
     * @param value �l
     */
    public void setLoggingValue(String key, Object value)
    {
        this.loggingValueMap_.put(key, value);
    }

    /**
     * Map����L�[���擾����B
     * 
     * @return �L�[�z��
     */
    public String[] getLoggingKeys()
    {
        Set<String> keySet = this.loggingValueMap_.keySet();
        String[] keys = keySet.toArray(new String[keySet.size()]);

        return keys;
    }

    /**
     * Map����L�[�ɑΉ�����l���擾����B
     * 
     * @param key �L�[
     * @return �L�[�̒l
     */
    public Object getLoggingValue(String key)
    {
        return this.loggingValueMap_.get(key);
    }
}
