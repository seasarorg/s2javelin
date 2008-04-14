/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.commands.Command;
import org.seasar.javelin.bottleneckeye.editors.view.AbstractStatsVisionEditor;
import org.seasar.javelin.bottleneckeye.model.AbstractConnectionModel;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;

/**
 * �N���X���폜����R�}���h�B
 *
 * @author Sakamoto
 */
public class DeleteClassCommand extends Command
{

    private ContentsModel contentsModel_;

    private ComponentModel componentModel_;

    private AbstractStatsVisionEditor<?> editor_;

    /** �폜�Ώۂ̃��f�����\�[�X�Ƃ���R�l�N�V�����̃��X�g */
    private List<AbstractConnectionModel> sourceConnections_ = new ArrayList<AbstractConnectionModel>();

    /** �폜�Ώۂ̃��f�����^�[�Q�b�g�Ƃ���R�l�N�V�����̃��X�g */
    private List<AbstractConnectionModel> targetConnections_ = new ArrayList<AbstractConnectionModel>();


    /**
     * �N���X���폜����R�}���h�𐶐�����B
     *
     * @param contentsModel �N���X������ ContentsModel
     * @param componentModel �N���X
     * @param editor �N���X�}�G�f�B�^
     */
    public DeleteClassCommand(ContentsModel contentsModel, ComponentModel componentModel,
    		AbstractStatsVisionEditor<?> editor)
    {
        this.contentsModel_ = contentsModel;
        this.componentModel_ = componentModel;
        this.editor_ = editor;
    }


    /**
     * {@inheritDoc}
     */
    public void execute()
    {
        // �R�l�N�V���������X�g�ɑޔ�������A���ꂼ��̃R�l�N�V�������O��
        this.sourceConnections_.addAll(this.componentModel_.getModelSourceConnections());
        this.targetConnections_.addAll(this.componentModel_.getModelTargetConnections());
        detachConnections(this.sourceConnections_);
        detachConnections(this.targetConnections_);

        // �R�l�N�V�������O������Ƀ��f�����폜����
        this.contentsModel_.removeChild(this.componentModel_);
        this.componentModel_.setDeleted(true);

        // �����̃}�b�v������폜����
        Map<?, ComponentModel> componentMap = this.editor_.getComponentMap();
        componentMap.remove(this.componentModel_.getClassName());
    }

    /**
     * {@inheritDoc}
     */
    public void undo()
    {
        this.contentsModel_.addChild(this.componentModel_);

        // �R�l�N�V���������ɖ߂�
        attachConnections(this.sourceConnections_);
        attachConnections(this.targetConnections_);
        this.sourceConnections_.clear();
        this.targetConnections_.clear();
    }

    /**
     * �w�肳�ꂽ���X�g���̂��ׂẴR�l�N�V�������O���B
     *
     * @param connections �R�l�N�V�������X�g
     */
    private void detachConnections(List<AbstractConnectionModel> connections)
    {
        for (AbstractConnectionModel connection : connections)
        {
            connection.detachSource();
            connection.detachTarget();
        }
    }

    /**
     * �w�肳�ꂽ���X�g���̂��ׂẴR�l�N�V�������q���B
     *
     * @param connections �R�l�N�V�������X�g 
     */
    private void attachConnections(List<AbstractConnectionModel> connections)
    {
        for (AbstractConnectionModel connection : connections)
        {
            connection.attachSource();
            connection.attachTarget();
        }
    }

}
