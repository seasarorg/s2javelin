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
    private volatile boolean         isRunning_;

    /** �d����]������^�[�Q�b�g�I�u�W�F�N�g�̃��X�g */
    private List<EditorTabInterface> editorTabList_;

    private SocketChannel            channel_;

    /** �T�[�o������̃f�[�^��Head�p�ϐ� */
    private ByteBuffer               headerBuffer   = ByteBuffer.allocate(Header.HEADER_LENGTH);

    /**�@�ċN���pTcpStatsVisionEditor */
    private TcpDataGetter            tcpDataGetter_ = null;

    /** �O�ɑ������ʒm���ڑ��ʒm�Ȃ� <code>true</code> �A�ؒf�ʒm�Ȃ� <code>false</code> */
    private boolean                  isPrevNotifyConnect_;

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
        this.isPrevNotifyConnect_ = false;
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
            notifyCommunicateStart();
            this.channel_ = this.tcpDataGetter_.getChannel();
            if (this.channel_ == null)
            {
                sendDisconnectNotify();
                retry();
                continue;
            }
            sendConnectNotify();

            byte[] telegramBytes = null;
            try
            {
                telegramBytes = this.readTelegramBytes();
            }
            catch (IOException ioe)
            {
                // �ؒf���ꂽ
                sendDisconnectNotify();
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

        sendDisconnectNotify();
        notifyCommunicateStop();
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
        if (this.isRunning_ == false)
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

    /**
     * �ڑ����ꂽ���Ƃ��e�^�u�֒ʒm����B
     */
    private void sendConnectNotify()
    {
        synchronized (this)
        {
            if (this.isPrevNotifyConnect_ == false)
            {
                this.isPrevNotifyConnect_ = true;
                for (EditorTabInterface editorTab : this.editorTabList_)
                {
                    editorTab.connected();
                }
            }
        }
    }

    /**
     * �ؒf���ꂽ���Ƃ��e�^�u�֒ʒm����B
     */
    public void sendDisconnectNotify()
    {
        synchronized (this)
        {
            if (this.isPrevNotifyConnect_ == true)
            {
                this.isPrevNotifyConnect_ = false;
                for (EditorTabInterface editorTab : this.editorTabList_)
                {
                    editorTab.disconnected();
                }
            }
        }
    }

    /**
     * �ڑ����J�n���ꂽ���Ƃ��e�^�u�ɒʒm����
     */
    private void notifyCommunicateStart()
    {
        synchronized (this)
        {
            if (this.isPrevNotifyConnect_ == false)
            {
                this.isPrevNotifyConnect_ = true;
                for (EditorTabInterface editorTab : this.editorTabList_)
                {
                    editorTab.notifyCommunicateStart();
                }
            }
        }
    }

    /**
     * �ڑ����I���������Ƃ��e�^�u�ɒʒm����
     */
    private void notifyCommunicateStop()
    {
        synchronized (this)
        {
            if (this.isPrevNotifyConnect_ == false)
            {
                this.isPrevNotifyConnect_ = true;
                for (EditorTabInterface editorTab : this.editorTabList_)
                {
                    editorTab.notifyCommunicateStop();
                }
            }
        }
    }

}
