/**
 * 
 */
package org.seasar.javelin.bottleneckeye.editors.view;

import org.eclipse.jface.action.Action;
import org.seasar.javelin.bottleneckeye.communicate.Common;
import org.seasar.javelin.bottleneckeye.communicate.Header;
import org.seasar.javelin.bottleneckeye.communicate.Telegram;


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
        // �����f�[�^�Ώۂ�����āA�f�[�^��ݒ肷��
        Header objHeader = new Header();
        objHeader.setByteTelegramKind(Common.BYTE_TELEGRAM_KIND_GET);
        objHeader.setByteRequestKind(Common.BYTE_REQUEST_KIND_REQUEST);

        // ������d���Ώۂɐݒ肷��
        Telegram objOutputTelegram = new Telegram();
        objOutputTelegram.setObjHeader(objHeader);

        ((TcpStatsVisionEditor)this.editor_).sendTelegram(objOutputTelegram);
    }

}
