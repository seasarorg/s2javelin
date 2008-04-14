/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.seasar.javelin.bottleneckeye.editors.view.AbstractStatsVisionEditor;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;

/**
 * 選択されたクラスを削除する EditPolicy 。
 *
 * @author Sakamoto
 */
public class StatsVisionDeleteEditPolicy extends ComponentEditPolicy
{

	private AbstractStatsVisionEditor<?> editor_;

	/**
	 * 選択されたクラスを削除する EditPolicy を生成する。
	 *
	 * @param editor クラス図エディタ
	 */
	public StatsVisionDeleteEditPolicy(AbstractStatsVisionEditor<?> editor)
	{
		this.editor_ = editor;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createDeleteCommand(GroupRequest request)
    {
        ContentsModel contentsModel = (ContentsModel)getHost().getParent().getModel();
        ComponentModel componentModel = (ComponentModel)getHost().getModel();
        return new DeleteClassCommand(contentsModel, componentModel, this.editor_);
    }

}
