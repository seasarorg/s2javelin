/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.view;

import org.eclipse.jface.action.Action;


/**
 * �N���X�}�������[�h����A�N�V�����B
 *
 * @author Sakamoto
 */
public class ClassReloadAction extends Action
{
    /** �N���X�}�\���G�f�B�^ */
    private StatsVisionEditor editor_;

    /**
     * �N���X�}�������[�h����A�N�V�����𐶐�����B
     *
     * @param editor �N���X�}�\���G�f�B�^
     */
    public ClassReloadAction(StatsVisionEditor editor)
    {
        this.editor_ = editor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {
        this.editor_.requestStatus();
    }

}
