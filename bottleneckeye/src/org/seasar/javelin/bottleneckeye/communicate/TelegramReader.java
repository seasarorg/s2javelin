package org.seasar.javelin.bottleneckeye.communicate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.seasar.javelin.bottleneckeye.editors.EditorTabInterface;
import org.seasar.javelin.bottleneckeye.editors.view.TcpStatsVisionEditor;

public class TelegramReader implements Runnable
{
    private boolean                  isRunning_;

    /** �d����]������^�[�Q�b�g�I�u�W�F�N�g�̃��X�g */
    private List<EditorTabInterface> editorTabList_;

    private SocketChannel            channel_;

    /** �T�[�o������̃f�[�^��Head�p�ϐ� */
    private ByteBuffer               headerBuffer   = ByteBuffer.allocate(Header.HEADER_LENGTH);

    /**�@�ċN���pTcpStatsVisionEditor */
    private TcpDataGetter            tcpDataGetter_ = null;

    /** ���g���C���� */
    private static final int         RETRY_INTERVAL = 10000;

    /**
     * �d������M����I�u�W�F�N�g���쐬����B
     *
     * @param tcpDataGetter StatsVisionEditor
     * @param channel �`���l��
     */
    public TelegramReader(TcpDataGetter tcpDataGetter)
    {
        this.isRunning_ = false;
        this.editorTabList_ = new ArrayList<EditorTabInterface>();
        this.tcpDataGetter_ = tcpDataGetter;
    }

    /**
     * ��M�����d����]������I�u�W�F�N�g���Z�b�g����B
     *
     * @param editorTab �]����I�u�W�F�N�g
     */
    public void addEditorTab(EditorTabInterface editorTab)
    {
        this.editorTabList_.add(editorTab);
    }

    /**
     * �d����M���[�v�B
     */
    public void run()
    {
        this.isRunning_ = true;
        while (this.isRunning_)
        {
            this.channel_ = this.tcpDataGetter_.getChannel();
            if(this.channel_ == null)
            {
                retry();
                continue;
            }
            byte[] telegramBytes = null;
            try
            {
                telegramBytes = this.readTelegramBytes();
            }
            catch (IOException ioe)
            {
                this.isRunning_ = false;
                break;
            }
            Telegram telegram = TelegramUtil.recoveryTelegram(telegramBytes);
            boolean isProcess = false;
            for (EditorTabInterface editorTab : this.editorTabList_)
            {
                isProcess |= editorTab.receiveTelegram(telegram);
            }
            if (isProcess == false)
            {
                // TODO ���O�o��
                int requestKind = telegram.getObjHeader().getByteRequestKind();
                System.out.println("����`�̗v��������ʂ���M���܂����B[" + requestKind + "]");
            }

            int telegramKind = telegram.getObjHeader().getByteTelegramKind();
            int requestKind = telegram.getObjHeader().getByteRequestKind();

            /*            if (Common.BYTE_TELEGRAM_KIND_ALERT == telegramKind)
                        {
                            // �ʒm��M�������s��
                            statsJavelinEditor_.listeningGraphicalViewer(telegram);
                        }
                        else if (Common.BYTE_TELEGRAM_KIND_GET == telegramKind
                                && Common.BYTE_REQUEST_KIND_RESPONSE == requestKind)
                        {
                            // ������M�������s���B
                            statsJavelinEditor_.addResponseTelegram(telegram);
                            System.out.println("�d����M[" + requestKind + "]");
                        }
                        else
                        {
                            // TODO ���O�o��
                            System.out.println("����`�̗v��������ʂ���M���܂����B[" + requestKind + "]");
                        }*/
        }

    }

    /**
     * �T�[�o����f�[�^��ǂݍ���
     *
     * @return ��M�����f�[�^
     * @throws IOException
     */
    public byte[] readTelegramBytes()
        throws IOException
    {

        int readCount = 0;
        while (readCount < Header.HEADER_LENGTH)
        {
            int count = channel_.read(headerBuffer);
            if (count < 0)
            {
                throw new IOException();
            }
            else
            {
                readCount += count;
            }
        }

        headerBuffer.rewind();
        int telegramLength = headerBuffer.getInt();

        // �w�b�_�������Ȃ��ꍇ�͂��̂܂ܕԂ��B
        if (telegramLength <= Header.HEADER_LENGTH)
        {
            headerBuffer.rewind();
            return headerBuffer.array();
        }

        readCount = 0;
        ByteBuffer bodyBuffer = ByteBuffer.allocate(telegramLength);
        bodyBuffer.put(headerBuffer.array());

        while (bodyBuffer.remaining() > 0)
        {
            channel_.read(bodyBuffer);
        }

        headerBuffer.rewind();
        return bodyBuffer.array();
    }

    public void setRunning(boolean isRunning)
    {
        this.isRunning_ = isRunning;
    }

    /**
     * ���g���C����B
     */
    private void retry()
    {
        System.out.println(RETRY_INTERVAL / 1000 + "�b��ɍĐڑ����܂��B");
        try
        {
            Thread.sleep(RETRY_INTERVAL);
        }
        catch (InterruptedException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (this.isRunning_)
            {
                this.tcpDataGetter_.open();
            }
        }
    }
}
