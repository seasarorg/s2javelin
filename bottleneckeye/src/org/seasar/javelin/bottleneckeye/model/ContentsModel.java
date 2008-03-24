package org.seasar.javelin.bottleneckeye.model;

import java.util.ArrayList;
import java.util.List;

/**
 * StatsVision�̃��[�g�R���e���c���f���B
 * 
 * @author yamasaki
 */
public class ContentsModel extends AbstractModel
{
    
    /** �ύX�̎�ނ����ʂ��邽�߂̕�����B */
    public static final String P_CONTENTS_NAME = "_contents_name";

    /** �q���f���̕ύX�����ʂ��邽�߂̕�����B */
    public static final String P_CHILDREN = "_children";
    
    // �q���f���̃��X�g
	private List<ComponentModel> children_ = new ArrayList<ComponentModel>();

	// ���f���̖��O
	private String contentsName_;
	
	/**
	 * �R���X�g���N�^�B
	 */
    public ContentsModel()
    {
        super();
    }

    /**
     * ���f���̖��O���擾����B
     * 
     * @return ���f���̖��O�B
     */
    public String getContentsName()
    {
        return this.contentsName_;
    }

    /**
     * ���f���̖��O��ݒ肷��B
     * 
     * @param contentsName ���f���̖��O�B
     */
    public void setContentsName(String contentsName)
    {
        this.contentsName_ = contentsName;
        firePropertyChange(P_CONTENTS_NAME, null, this.contentsName_);
    }
    
    /**
	 * �q���f����ǉ�����B
	 * 
	 * @param child �ǉ�����q���f���B
	 */
	public void addChild(ComponentModel child)
	{
	    this.children_.add(child);
        firePropertyChange(P_CHILDREN, null, this.children_);
	}

	/**
	 * �q���f�����폜����B
	 *
	 * @param child �폜����q���f���B
	 */
	public void removeChild(ComponentModel child)
	{
	    this.children_.remove(child);
        firePropertyChange(P_CHILDREN, null, this.children_);
	}

	/**
	 * �q���f���̃��X�g���擾����B
	 * 
	 * @return �q���f���̃��X�g�B
	 */
	public List<ComponentModel> getChildren()
	{
		return this.children_;
	}
}
