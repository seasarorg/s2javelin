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
 * クラスを削除するコマンド。
 *
 * @author Sakamoto
 */
public class DeleteClassCommand extends Command
{

    private ContentsModel contentsModel_;

    private ComponentModel componentModel_;

    private AbstractStatsVisionEditor<?> editor_;

    /** 削除対象のモデルをソースとするコネクションのリスト */
    private List<AbstractConnectionModel> sourceConnections_ = new ArrayList<AbstractConnectionModel>();

    /** 削除対象のモデルをターゲットとするコネクションのリスト */
    private List<AbstractConnectionModel> targetConnections_ = new ArrayList<AbstractConnectionModel>();


    /**
     * クラスを削除するコマンドを生成する。
     *
     * @param contentsModel クラスを持つ ContentsModel
     * @param componentModel クラス
     * @param editor クラス図エディタ
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
        // コネクションをリストに退避した後、それぞれのコネクションを外す
        this.sourceConnections_.addAll(this.componentModel_.getModelSourceConnections());
        this.targetConnections_.addAll(this.componentModel_.getModelTargetConnections());
        detachConnections(this.sourceConnections_);
        detachConnections(this.targetConnections_);

        // コネクションを外した後にモデルを削除する
        this.contentsModel_.removeChild(this.componentModel_);
        this.componentModel_.setDeleted(true);

        // 内部のマップからも削除する
        Map<?, ComponentModel> componentMap = this.editor_.getComponentMap();
        componentMap.remove(this.componentModel_.getClassName());
    }

    /**
     * {@inheritDoc}
     */
    public void undo()
    {
        this.contentsModel_.addChild(this.componentModel_);

        // コネクションを元に戻す
        attachConnections(this.sourceConnections_);
        attachConnections(this.targetConnections_);
        this.sourceConnections_.clear();
        this.targetConnections_.clear();
    }

    /**
     * 指定されたリスト内のすべてのコネクションを外す。
     *
     * @param connections コネクションリスト
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
     * 指定されたリスト内のすべてのコネクションを繋ぐ。
     *
     * @param connections コネクションリスト 
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
