package org.seasar.javelin.bottleneckeye.communicate;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.seasar.javelin.bottleneckeye.editors.EditorTabInterface;

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

    private ExecutorService          writeExecutor_  = createThreadPoolExecutor();

    /** �z�X�g�� */
    private String                   hostName_;

    /** �|�[�g�ԍ� */
    private int                      portNumber_;

    /** �ڑ���� */
    private boolean                  isConnect_      = false;

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
    public void open()
    {
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                if (TcpDataGetter.this.isConnect_ == true)
                {
                    return;
                }

                try
                {
                    // �T�[�o�ɐڑ�����
                    SocketAddress remote =
                            new InetSocketAddress(TcpDataGetter.this.hostName_,
                                                  TcpDataGetter.this.portNumber_);
                    TcpDataGetter.this.socketChannel_ = SocketChannel.open(remote);
                    // �ڑ����̃��b�Z�[�W
                    System.out.println("\n�T�[�o�ɐڑ����܂���:" + remote);
                    TcpDataGetter.this.isConnect_ = true;
                }
                catch (UnknownHostException objUnknownHostException)
                {
                    // �G���[���b�Z�[�W���o��
                    System.out.println("�T�[�o�ւ̐ڑ��Ɏ��s���܂����B�T�[�o�A�h���X�A�|�[�g��ʂ�ɐݒ肵�Ă��邱�Ƃ��m�F���Ă��������B");
                    return;
                    //            return false;
                }
                catch (IOException objIOException)
                {
                    // �G���[���b�Z�[�W���o��
                    System.out.println("�T�[�o�ւ̐ڑ��Ɏ��s���܂����B�T�[�o�A�h���X�A�|�[�g���������ݒ肳��Ă��邱�Ƃ��m�F���Ă��������B");
                    return;
                    //            return false;
                }

                try
                {
                    TcpDataGetter.this.objPrintStream_ =
                            new PrintStream(
                                            TcpDataGetter.this.socketChannel_.socket().getOutputStream(),
                                            true);
                }
                catch (IOException objIOException)
                {
                    // �G���[���b�Z�[�W���o��
                    objIOException.printStackTrace();
                    return;
                    //            return false;
                }

                //        return true;
            }
        });
    }

    private ThreadPoolExecutor createThreadPoolExecutor()
    {
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
                                          public Thread newThread(Runnable r)
                                          {
                                              String name =
                                                      "BeyeWriterThread-"
                                                              + TcpDataGetter.this.hostName_ + ":"
                                                              + TcpDataGetter.this.portNumber_;
                                              return new Thread(r, name);
                                          }
                                      }, new ThreadPoolExecutor.DiscardPolicy());
    }

    /**
     * �T�[�o�ɐڑ�������
     */
    public void close()
    {
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                if (TcpDataGetter.this.telegramReader_ != null)
                {
                    TcpDataGetter.this.telegramReader_.setRunning(false);
                }

                if (TcpDataGetter.this.isConnect_ == false)
                {
                    return;
                }

                // �g�p�����ʐM�Ώۂ��N���A����
                if (TcpDataGetter.this.objPrintStream_ != null)
                {
                    TcpDataGetter.this.objPrintStream_.close();
                    TcpDataGetter.this.objPrintStream_ = null;
                }

                try
                {
                    if (TcpDataGetter.this.socketChannel_ != null)
                    {
                        TcpDataGetter.this.socketChannel_.close();
                        TcpDataGetter.this.socketChannel_ = null;
                    }

                    System.out.println("�T�[�o�Ƃ̒ʐM���I�����܂����B");
                    TcpDataGetter.this.isConnect_ = false;
                    TcpDataGetter.this.telegramReader_.sendDisconnectNotify();
                }
                catch (IOException objIOException)
                {
                    // �G���[���o��
                    objIOException.printStackTrace();
                }
            }
        });
    }

    public void shutdown()
    {
        this.telegramReader_.setRunning(false);
        this.close();
        this.writeExecutor_.shutdown();
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
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                TcpDataGetter.this.telegramReader_ = new TelegramReader(TcpDataGetter.this);
                for (EditorTabInterface editorTab : TcpDataGetter.this.editorTabList_)
                {
                    TcpDataGetter.this.telegramReader_.addEditorTab(editorTab);
                }

                Thread readerThread =
                        new Thread(TcpDataGetter.this.telegramReader_, "BeyeReaderThread-"
                                + TcpDataGetter.this.hostName_ + ":"
                                + TcpDataGetter.this.portNumber_);

                readerThread.start();
            }
        });
    }

    /**
     * �T�[�o�ɓd���𑗐M����B
     *
     * @param telegram �d��
     */
    public void sendTelegram(final Telegram telegram)
    {
        this.writeExecutor_.execute(new Runnable() {
            public void run()
            {
                byte[] byteOutputArray = TelegramUtil.createTelegram(telegram);

                try
                {
                    if (TcpDataGetter.this.objPrintStream_ != null)
                    {
                        TcpDataGetter.this.objPrintStream_.write(byteOutputArray);
                        TcpDataGetter.this.objPrintStream_.flush();
                        // �����I�����s��ꂽ�Ƃ��A�Đڑ����s��
                        if (TcpDataGetter.this.objPrintStream_.checkError())
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
                    TcpDataGetter.this.close();
                }
            }
        });
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
