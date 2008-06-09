package org.seasar.javelin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.seasar.javelin.util.IOUtil;

/**
 * Javelin�̃V�X�e�����K�[�B<br>
 * 
 * @author eriguchi
 */
public class SystemLogger
{
    /** �G���[���O�o�͓����̃t�H�[�}�b�g�B */
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";

    /** ���s�����B */
    public static final String NEW_LINE    = System.getProperty("line.separator");

    /** �V�X�e�����O�t�@�C���̊g���q�B */
    private static final String EXTENTION   = ".log";

    /** �V�X�e�����O�t�@�C�����̃t�H�[�}�b�g(���t�t�H�[�}�b�g(�~��(sec)�܂ŕ\��) */
    private static final String LOG_FILE_FORMAT = "jvn_sys_{0,date,yyyy_MM_dd_HHmmss_SSS}" + EXTENTION;

    /** �V�X�e�����O�t�@�C���̍ő吔�B */
    private int                 systemLogNumMax_;

    /** �V�X�e�����O�t�@�C���̍ő�T�C�Y�B */
    private int                 systemLogSizeMax_;

    /** �V�X�e�����O�̃��O���x���B */
    private LogLevel            systemLogLevel_;

    /** JavelinErrorLogger�̃C���X�^���X�B */
    private static SystemLogger instance_   = new SystemLogger();

    /** �������񂾕������B */
    private long                writeCount_ = 0;

    /** �V�X�e�����O�t�@�C���̏o�͐�f�B���N�g���̃p�X�B */
    private String              logPath_;

    /** �V�X�e�����O�t�@�C���̃t�@�C�����B */
    private String              logFileName_;

    /**
     * �R���X�g���N�^�B
     * �C���X�^���X����1�ɐ������邽�߁Aprivate���\�b�h�Ƃ���B
     */
    private SystemLogger()
    {
    }

    /**
     * JavelinErrorLogger�C���X�^���X�擾�p���\�b�h�B
     * 
     * @return JavelinErrorLogger�C���X�^���X�B
     */
    public static SystemLogger getInstance()
    {
        return instance_;
    }

    /**
     * �V�X�e�����O�t�@�C�����𐶐�����B
     * �t�@�C�����̃t�H�[�}�b�g�͈ȉ��̂Ƃ���B<br>
     * jvn_sys_yyyyMMddHHmmssSSS.log
     * 
     * @return �V�X�e�����O�t�@�C�����B
     */
    private String createLogFileName()
    {
        return MessageFormat.format(LOG_FILE_FORMAT, new Date());
    }

    /**
     * ���O���b�Z�[�W���t�H�[�}�b�g����B
     * 
     * @param level�@���O���x���B
     * @param message ���O���b�Z�[�W�B
     * @param throwable ��O�B
     * @return �t�H�[�}�b�g�������O���b�Z�[�W�B
     */
    private String formatMessage(LogLevel level, String message, Throwable throwable)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String dateMessage = dateFormat.format(new Date());

        StringBuffer messageBuffer = new StringBuffer();

        messageBuffer.append(dateMessage);
        messageBuffer.append(" [");
        messageBuffer.append(level.getLevelStr());
        messageBuffer.append("] ");
        if (message != null)
        {
            messageBuffer.append("[Javelin] ");
            messageBuffer.append(message);
        }
        if (throwable != null)
        {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            throwable.printStackTrace(printWriter);
            String stackTrace = stringWriter.toString();

            messageBuffer.append(stackTrace);
        }
        messageBuffer.append(NEW_LINE);
        String buildMessage = messageBuffer.toString();
        return buildMessage;
    }

    /**
     * ���O���o�͂���B
     * @param message �G���[���b�Z�[�W�B
     * @param throwable ��O�B
     */
    private synchronized void log(LogLevel level, String message, Throwable throwable)
    {
        if (level.getLevel() < this.systemLogLevel_.getLevel())
        {
            return;
        }

        String formattedMessage = formatMessage(level, message, throwable);

        if (this.logPath_ == null)
        {
            System.err.println(formattedMessage);
            return;
        }

        if (logFileName_ == null)
        {
            logFileName_ = createLogFileName();
        }

        OutputStreamWriter writer = null;
        try
        {
            // �e�f�B���N�g�����쐬����B
            IOUtil.createDirs(logPath_);

            FileOutputStream fileOutputStream =
                    new FileOutputStream(logPath_ + File.separator + logFileName_, true);
            writer = new OutputStreamWriter(fileOutputStream);

            writer.write(formattedMessage);
            writeCount_ += formattedMessage.length();
        }
        catch (Exception ex)
        {
            // �o�͂ł��Ȃ������ꍇ�͕W���G���[�ɏo�͂���B�B
            System.err.println("Javelin���s�G���[�o�̓t�@�C���ւ̏������݂Ɏ��s���܂����B�W���G���[�o�͂��g�p���܂��B" + NEW_LINE
                    + "(javelin.error.log=" + this.logPath_ + File.separator + logFileName_ + ")");
            ex.printStackTrace();
            System.err.println(formattedMessage);
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.close();
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
        }

        // ���[�e�[�g���K�v�ȏꍇ�̓��[�e�[�g����B
        if (writeCount_ > systemLogSizeMax_)
        {
            File logFile = new File(this.logPath_ + File.separator + this.logFileName_);
            if (logFile.length() > systemLogSizeMax_)
            {
                logFileName_ = createLogFileName();
                IOUtil.removeLogFiles(systemLogNumMax_, this.logPath_, EXTENTION);
                writeCount_ = 0;
            }
        }
    }

    public void fatal(String message)
    {
        this.fatal(message, null);
    }

    public void fatal(Throwable throwable)
    {
        this.fatal(null, throwable);
    }

    public void fatal(String message, Throwable throwable)
    {
        this.log(LogLevel.FATAL, message, throwable);
    }

    public void error(String message)
    {
        this.error(message, null);
    }

    public void error(Throwable throwable)
    {
        this.error(null, throwable);
    }

    public void error(String message, Throwable throwable)
    {
        this.log(LogLevel.ERROR, message, throwable);
    }

    public void warn(String message)
    {
        this.warn(message, null);
    }

    public void warn(Throwable throwable)
    {
        this.warn(null, throwable);
    }

    public void warn(String message, Throwable throwable)
    {
        this.log(LogLevel.WARN, message, throwable);
    }

    public void info(String message)
    {
        this.info(message, null);
    }

    public void info(Throwable throwable)
    {
        this.info(null, throwable);
    }

    public void info(String message, Throwable throwable)
    {
        this.log(LogLevel.INFO, message, throwable);
    }

    public void debug(String message)
    {
        this.debug(message, null);
    }

    public void debug(Throwable throwable)
    {
        this.debug(null, throwable);
    }

    public void debug(String message, Throwable throwable)
    {
        this.log(LogLevel.DEBUG, message, throwable);
    }

    public boolean isDebugEnabled()
    {
        if (LogLevel.DEBUG.getLevel() < this.systemLogLevel_.getLevel())
        {
            return false;
        }
        
        return true;
    }
    
    /**
     * �V�X�e�����O������������B
     * 
     * @param options �N���I�v�V����
     */
    public static void initSystemLog(S2JavelinConfig config)
    {
        SystemLogger instance = getInstance();
        synchronized (instance)
        {
            instance.logPath_ = config.getSystemLog();
            instance.systemLogNumMax_ = config.getSystemLogNumMax();
            instance.systemLogSizeMax_ = config.getSystemLogSizeMax();
            instance.systemLogLevel_ = toLogLevel(config.getSystemLogLevel());
            
            // �N�����Ƀ��O�t�@�C������������ꍇ�͍폜����B
            IOUtil.removeLogFiles(instance.systemLogNumMax_, instance.logPath_, EXTENTION);
        }
    }

    /**
     * ���O���x���̕������LogLevel�C���X�^���X�ɕϊ�����B
     * 
     * @param logLevelStr ���O���x���̕�����B
     * @return LogLevel�B
     */
    private static LogLevel toLogLevel(String logLevelStr)
    {
        if ("DEBUG".equals(logLevelStr))
        {
            return LogLevel.DEBUG;
        }
        else if ("INFO".equals(logLevelStr))
        {
            return LogLevel.INFO;
        }
        else if ("WARN".equals(logLevelStr))
        {
            return LogLevel.WARN;
        }
        else if ("ERROR".equals(logLevelStr))
        {
            return LogLevel.ERROR;
        }
        else if ("FATAL".equals(logLevelStr))
        {
            return LogLevel.FATAL;
        }
        return LogLevel.WARN;
    }
}