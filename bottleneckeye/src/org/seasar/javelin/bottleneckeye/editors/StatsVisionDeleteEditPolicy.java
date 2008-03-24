/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;

/**
 * �I�����ꂽ�N���X���폜���� EditPolicy �B
 *
 * @author Sakamoto
 */
public class StatsVisionDeleteEditPolicy extends ComponentEditPolicy
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected Command createDeleteCommand(GroupRequest request)
    {
        ContentsModel contentsModel = (ContentsModel)getHost().getParent().getModel();
        ComponentModel componentModel = (ComponentModel)getHost().getModel();
        return new DeleteClassCommand(contentsModel, componentModel);
    }

}
