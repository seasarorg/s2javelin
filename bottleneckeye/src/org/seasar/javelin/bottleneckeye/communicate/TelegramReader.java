package org.seasar.javelin.bottleneckeye.communicate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.seasar.javelin.bottleneckeye.editors.EditorTabInterface;

/**
 * �d������M����N���X�B
 */
public class TelegramReader implements Runnable
{
    private static final int SO_TIMEOUT = 10000;

    private volatile boolean         isRunning_;

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
            if (this.tcpDataGetter_.isStart() == false)
            {
                try
                {
                    Thread.sleep(RETRY_INTERVAL);
                }
                catch (InterruptedException ex)
                {
                    // interrupt����B
                }
                continue;
            }
            this.channel_ = this.tcpDataGetter_.getChannel();
            this.tcpDataGetter_.notifyCommunicateStart();

            if (this.channel_ == null)
            {
                this.tcpDataGetter_.sendDisconnectNotify();
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
                // �ؒf���ꂽ
                this.tcpDataGetter_.sendDisconnectNotify();
                retry();
                continue;
            }
            Telegram telegram = TelegramUtil.recoveryTelegram(telegramBytes);

            if (telegram == null)
            {
                System.out.println("��M�����d���̓ǂݍ��݂Ɏ��s���܂����B");
                continue;
            }

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
        this.channel_.socket().setSoTimeout(0);
        int readCount = 0;
        while (readCount < Header.HEADER_LENGTH)
        {
            int count = this.channel_.read(this.headerBuffer);
            if (count < 0)
            {
                throw new IOException();
            }

            readCount += count;
        }
        
        this.headerBuffer.rewind();
        int telegramLength = this.headerBuffer.getInt();

        // �w�b�_�������Ȃ��ꍇ�͂��̂܂ܕԂ��B
        if (telegramLength <= Header.HEADER_LENGTH)
        {
            this.headerBuffer.rewind();
            return this.headerBuffer.array();
        }

        readCount = 0;
        ByteBuffer bodyBuffer = ByteBuffer.allocate(telegramLength);
        bodyBuffer.put(this.headerBuffer.array());

        this.channel_.socket().setSoTimeout(SO_TIMEOUT);
        while (bodyBuffer.remaining() > 0)
        {
            this.channel_.read(bodyBuffer);
        }

        this.headerBuffer.rewind();
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
        if (this.isRunning_ == false || this.tcpDataGetter_.isStart() == false)
        {
            return;
        }

        try
        {
            Thread.sleep(RETRY_INTERVAL);
        }
        catch (InterruptedException ex)
        {
            // �ؒf����interrupt����B
        }

        if (this.isRunning_)
        {
            this.tcpDataGetter_.open();
        }
    }


}
