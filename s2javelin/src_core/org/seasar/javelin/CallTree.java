package org.seasar.javelin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.seasar.javelin.util.StatsUtil;

/**
 * Javelin���O�o�͗p�ɃR�[���X�^�b�N���L�^���邽�߂́A�c���[�N���X�B
 * 
 * @author yamasaki
 */
public class CallTree
{
    /** CallTree�m�[�h */
    private CallTreeNode        rootNode_;

    /** ThreadID */
    private String              threadID_;

    /** ���\�b�h�Ăяo���̃��[�g�m�[�h�ɂ��閼�O�B */
    private String              rootCallerName_  = "unknown";

    /** ���\�b�h�Ăяo���̃G���h�m�[�h�̖��O������ł��Ȃ��ꍇ�ɂ��閼�O�B */
    private String              endCalleeName_   = "unknown";

    /** ��O�̌��� */
    private Throwable           cause_;

    /**  */
    private List<Callback>      callbackList_    = new ArrayList<Callback>();

    /**  */
    private Map<String, Object> flagMap_         = new HashMap<String, Object>();

    /**  */
    private Map<String, Object> loggingValueMap_ = new TreeMap<String, Object>();

    /**
     * �R���X�g���N�^�B �X���b�hID��ݒ肷��B�G
     */
    public CallTree()
    {
        this.threadID_ = StatsUtil.createThreadIDText();
    }

    /**
     * ���[�g�m�[�h���擾����B
     * 
     * @return ���[�g�m�[�h
     */
    public CallTreeNode getRootNode()
    {
        return this.rootNode_;
    }

    /**
     * ���[�g�m�[�h��ݒ肷��B
     * 
     * @param rootNode ���[�g�m�[�h
     */
    public void setRootNode(CallTreeNode rootNode)
    {
        this.rootNode_ = rootNode;
    }

    /**
     * ThreadID���擾����B
     * 
     * @return ThreadID
     */
    public String getThreadID()
    {
        return this.threadID_;
    }

    /**
     * ThreadID��ݒ肷��B
     * 
     * @param threadID �X���b�hID
     */
    public void setThreadID(String threadID)
    {
        this.threadID_ = threadID;
    }

    /**
     * �G���h�m�[�h���擾����B
     * 
     * @return �G���h�m�[�h
     */
    public String getEndCalleeName()
    {
        return this.endCalleeName_;
    }

    /**
     * �G���h�m�[�h��ݒ肷��B
     * 
     * @param endCalleeName �G���h�m�[�h
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
     * �Ăяo�����̃��[�g�m�[�h�����擾����B
     * 
     * @return �Ăяo�����̃��[�g�m�[�h��
     */
    public String getRootCallerName()
    {
        return this.rootCallerName_;
    }

    /**
     * �Ăяo�����̃��[�g�m�[�h����ݒ肷��B
     * 
     * @param rootCallerName �Ăяo�����̃��[�g�m�[�h���B
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
     * CallBack��ǉ�����B
     * @param callback CallBack
     */
    public void addCallback(Callback callback)
    {
        this.callbackList_.add(callback);
    }

    /**
     * CallBack�����s����B
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
     * �t���O��ݒ肷��B
     * @param flag �t���O
     * @param value �l
     * @return �t���O
     */
    public boolean setFlag(String flag, Object value)
    {
        return (this.flagMap_.put(flag, value) != null);
    }

    /**
     * �t���O���擾����B
     * @param flag �t���O
     * @return �t���O
     */
    public Object getFlag(String flag)
    {
        return this.flagMap_.get(flag);
    }

    /**
     * flag��Map�ɓo�^����Ă��邩�Ԃ��B
     * @param flag �t���O
     * @return true:�L�[��Map�ɓo�^����Ă���Afalse:�L�[��Map�ɓo�^����Ă��Ȃ��B
     */
    public boolean containsFlag(String flag)
    {
        return this.flagMap_.containsKey(flag);
    }

    /**
     * �t���O�̒l��Map���珜�O����B
     * @param flag �t���O
     * @return true:���O�����Afalse:���O����Ȃ��B
     */
    public boolean removeFlag(String flag)
    {
        return (this.flagMap_.remove(flag) != null);
    }

    /**
     * ���O�l��ݒ肷��B
     * @param key �L�[
     * @param value �l
     */
    public void setLoggingValue(String key, Object value)
    {
        this.loggingValueMap_.put(key, value);
    }

    /**
     * Map����L�[���擾����B
     * @return  �L�[�z��
     */
    public String[] getLoggingKeys()
    {
        Set<String> keySet = this.loggingValueMap_.keySet();
        String[] keys = keySet.toArray(new String[keySet.size()]);

        return keys;
    }

    /**
     * Map����L�[�ɑΉ�����l���擾����B
     * @param key �L�[
     * @return �L�[�̒l
     */
    public Object getLoggingValue(String key)
    {
        return this.loggingValueMap_.get(key);
    }

    /**
     * ��O�̌������擾����B
     * @return ��O�̌���
     */
    public Throwable getCause()
    {
        return this.cause_;
    }

    /**
     * ��O�̌�����ݒ肷��B
     * @param cause ��O�̌���
     */
    public void setCause(Throwable cause)
    {
        this.cause_ = cause;
    }
}
