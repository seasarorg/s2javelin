package org.seasar.javelin.jmx;

public class CallTree
{
    private CallTreeNode rootNode_;

    private String       threadID_;

    public CallTree()
    {
        this.threadID_ = createThreadIDText();
    }

    public CallTreeNode getRootNode( )
    {
        return rootNode_;
    }

    public void setRootNode(CallTreeNode rootNode)
    {
        rootNode_ = rootNode;
    }
    
    public String getThreadID()
    {
        return this.threadID_;
    }

    /**
     * �X���b�h�����ʂ��邽�߂̕�������o�͂���B 
     * �t�H�[�}�b�g�F�X���b�h��@�X���b�h�N���X��@�X���b�h�I�u�W�F�N�g��ID
     * 
     * @return �X���b�h�����ʂ��邽�߂̕�����
     */
    private String createThreadIDText( )
    {
        Thread currentThread = Thread.currentThread();

        StringBuffer threadId = new StringBuffer();
        threadId.append(currentThread.getName());
        threadId.append("@" + currentThread.getClass().getName());
        threadId.append("@" + getObjectID(currentThread));

        return threadId.toString();
    }

    /**
     * �I�u�W�F�N�gID��16�i�`���̕�����Ƃ��Ď擾����B
     * 
     * @param object �I�u�W�F�N�gID���擾�I�u�W�F�N�g�B
     * @return �I�u�W�F�N�gID�B
     */
    private String getObjectID(Object object)
    {
        // ������null�̏ꍇ��"null"��Ԃ��B
        if (object == null)
        {
            return "null";
        }

        return Integer.toHexString(System.identityHashCode(object));
    }
}
