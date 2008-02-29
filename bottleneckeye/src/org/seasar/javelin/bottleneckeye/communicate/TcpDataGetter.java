package org.seasar.javelin.bottleneckeye.communicate;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.seasar.javelin.bottleneckeye.editors.EditorTabInterface;
import org.seasar.javelin.bottleneckeye.editors.view.TcpStatsVisionEditor;

public class TcpDataGetter implements TelegramClientManager
{

    /**
     * �ʐM�\�P�b�g
     */
    private SocketChannel            socketChannel_  = null;

    /**
     * �o�͗�
     */
    private PrintStream              objPrintStream_ = null;

    /** ��M�����d����]������^�[�Q�b�g�I�u�W�F�N�g�̃��X�g */
    private List<EditorTabInterface> editorTabList_;

    private TelegramReader           telegramReader_;

    /** �z�X�g�� */
    private String                   hostName_;

    /** �|�[�g�ԍ� */
    private int                      portNumber_;

    public TcpDataGetter()
    {
        this.editorTabList_ = new ArrayList<EditorTabInterface>();
    }

    /**
     * �z�X�g�����Z�b�g����B
     *
     * @param hostName �z�X�g��
     */
    public void setHostName(String hostName)
    {
        this.hostName_ = hostName;
    }

    /**
     * �|�[�g�ԍ����Z�b�g����B
     *
     * @param portNumber �|�[�g�ԍ�
     */
    public void setPortNumber(int portNumber)
    {
        this.portNumber_ = portNumber;
    }

    /**
     * �T�[�o�ɐڑ�����B
     */
    public boolean open()
    {
        try
        {
            // �T�[�o�ɐڑ�����
            SocketAddress remote = new InetSocketAddress(this.hostName_, this.portNumber_);
            this.socketChannel_ = SocketChannel.open(remote);
            // �ڑ����̃��b�Z�[�W
            System.out.println("\n�T�[�o�ɐڑ����܂���:" + remote);

        }
        catch (UnknownHostException objUnknownHostException)
        {
            // �G���[���b�Z�[�W���o��
            System.out.println("�T�[�o�ւ̐ڑ��Ɏ��s���܂����B�T�[�o�A�h���X�A�|�[�g��ʂ�ɐݒ肵�Ă��邱�Ƃ��m�F���Ă��������B");
            return false;
        }
        catch (IOException objIOException)
        {
            // �G���[���b�Z�[�W���o��
            System.out.println("�T�[�o�ւ̐ڑ��Ɏ��s���܂����B�T�[�o�A�h���X�A�|�[�g���������ݒ肳��Ă��邱�Ƃ��m�F���Ă��������B");
            return false;
        }

        try
        {
            this.objPrintStream_ = new PrintStream(this.socketChannel_.socket().getOutputStream(),
                                                   true);
        }
        catch (IOException objIOException)
        {
            // �G���[���b�Z�[�W���o��
            objIOException.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * �T�[�o�ɐڑ�������
     */
    public void close()
    {
        if (this.telegramReader_ != null)
        {
            this.telegramReader_.setRunning(false);
        }

        // �g�p�����ʐM�Ώۂ��N���A����
        if (this.objPrintStream_ != null)
        {
            this.objPrintStream_.close();
            this.objPrintStream_ = null;
        }

        try
        {
            if (this.socketChannel_ != null)
            {
                this.socketChannel_.close();
                this.socketChannel_ = null;
            }

            System.out.println("�T�[�o�Ƃ̒ʐM���I�����܂����B");
        }
        catch (IOException objIOException)
        {
            // �G���[���o��
            objIOException.printStackTrace();
        }
    }

    /**
     * �T�[�o�ɏ�Ԏ擾�d���𑗂�
     */
    public void request()
    {
        // �����f�[�^�Ώۂ�����āA�f�[�^��ݒ肷��
        Header objHeader = new Header();
        objHeader.setByteTelegramKind(Common.BYTE_TELEGRAM_KIND_GET);
        objHeader.setByteRequestKind(Common.BYTE_REQUEST_KIND_REQUEST);

        // ������d���Ώۂɐݒ肷��
        Telegram objOutputTelegram = new Telegram();
        objOutputTelegram.setObjHeader(objHeader);

        sendTelegram(objOutputTelegram);
    }

    /**
     * �T�[�o�Ƀ��Z�b�g�d���𑗂�B
     */
    public void sendReset()
    {
        // �����f�[�^�Ώۂ�����āA�f�[�^��ݒ肷��
        Header objHeader = new Header();
        objHeader.setByteTelegramKind(Common.BYTE_TELEGRAM_KIND_RESET);
        objHeader.setByteRequestKind(Common.BYTE_REQUEST_KIND_REQUEST);

        // ������d���Ώۂɐݒ肷��
        Telegram objOutputTelegram = new Telegram();
        objOutputTelegram.setObjHeader(objHeader);

        sendTelegram(objOutputTelegram);
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
     * �T�[�o����̓d����M���J�n����B
     * @param editor TcpStatsVisionEditor
     */
    public void startRead()
    {
        this.telegramReader_ = new TelegramReader(this);
        for (EditorTabInterface editorTab : this.editorTabList_)
        {
            this.telegramReader_.addEditorTab(editorTab);
        }

        Thread readerThread = new Thread(this.telegramReader_, "StatsReaderThread");
        readerThread.start();
    }

    /**
     * �T�[�o�ɓd���𑗐M����B
     *
     * @param telegram �d��
     */
    public void sendTelegram(Telegram telegram)
    {
        byte[] byteOutputArray = TelegramUtil.createTelegram(telegram);

        try
        {
            if (this.objPrintStream_ != null)
            {
                this.objPrintStream_.write(byteOutputArray);
                this.objPrintStream_.flush();
                // �����I�����s��ꂽ�Ƃ��A�Đڑ����s��
                if (this.objPrintStream_.checkError())
                {
                    System.err.println("�ʐM�������I�����܂����B");
                    close();
                    startRead();
                }
            }
        }
        catch (IOException objIOException)
        {
            objIOException.printStackTrace();
            this.close();
        }
    }

    /**
     * �\�P�b�g�`���l�����擾����
     * @return �\�P�b�g�`���l��
     */
    public SocketChannel getChannel()
    {
        return this.socketChannel_;
    }
}
