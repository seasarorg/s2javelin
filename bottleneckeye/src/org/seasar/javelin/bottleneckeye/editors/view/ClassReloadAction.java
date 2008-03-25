/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.view;

import org.eclipse.jface.action.Action;


/**
 * クラス図をリロードするアクション。
 *
 * @author Sakamoto
 */
public class ClassReloadAction extends Action
{
    /** クラス図表示エディタ */
    private StatsVisionEditor editor_;

    /**
     * クラス図をリロードするアクションを生成する。
     *
     * @param editor クラス図表示エディタ
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
