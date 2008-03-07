package org.seasar.javelin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Javelin�̎��s���G���[�̃��K�[�N���X�B<br>
 * 
 * Singleton�p�^�[����p���ăC���X�^���X������ɐ������Ă���B
 * @author eriguchi
 */
public class JavelinErrorLogger
{
    /**
     * ���s�����B
     */
    private static final String           NEW_LINE    = System.getProperty("line.separator");

    /**
     * �G���[���O�o�͓����̃t�H�[�}�b�g�B
     */
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";

    /**
     * JavelinErrorLogger�̃C���X�^���X�B
     */
    private static JavelinErrorLogger     instance_   = new JavelinErrorLogger();

    /**
     * �G���[�̏o�͐�B
     */
    private PrintStream                   errorOut_;

    /**
     * JavelinErrorLogger�C���X�^���X�擾�p���\�b�h�B
     * 
     * @return JavelinErrorLogger�C���X�^���X�B
     */
    public static JavelinErrorLogger getInstance()
    {
        return instance_;
    }

    /**
     * �G���[���O�t�@�C�����I�[�v������B
     * @param logFileName �G���[���O�t�@�C�����B
     * @throws FileNotFoundException �G���[���O�t�@�C����������Ȃ��ꍇ�B
     */
    public void open(String logFileName)
        throws FileNotFoundException
    {
        if (this.errorOut_ != null)
        {
            this.close();
        }

        // �t�@�C������null�̏ꍇ�͉������Ȃ��B
        if (logFileName == null)
        {
            return;
        }
        FileOutputStream fileOutputStream = new FileOutputStream(logFileName, true);
        PrintStream logStream = new PrintStream(fileOutputStream);
        this.errorOut_ = logStream;
    }

    /**
     * �G���[���O�t�@�C�����N���[�Y����B
     */
    public void close()
    {
        if (this.errorOut_ != System.err)
        {
            this.errorOut_.close();
        }
    }

    /**
     * �G���[���O���o�͂���B
     * @param message �G���[���b�Z�[�W�B
     * @param throwable ��O�B
     */
    public void log(String message, Throwable throwable)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String dateMessage = dateFormat.format(new Date());

        this.errorOut_.print(dateMessage);
        if (message != null)
        {
            this.errorOut_.print(" [Javelin] ");
            this.errorOut_.println(message);
        }
        if (throwable != null)
        {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            throwable.printStackTrace(printWriter);
            String stackTrace = stringWriter.toString();
            this.errorOut_.print(stackTrace);
        }
    }

    /**
     * �G���[���O���o�͂���B
     * @param message �G���[���b�Z�[�W�B
     */
    public void log(String message)
    {
        log(message, null);
    }

    /**
     * �G���[���O���o�͂���B
     * @param throwable ��O�B
     */
    public void log(Throwable throwable)
    {
        log(null, throwable);
    }

    /**
     * �R���X�g���N�^�B
     * �C���X�^���X����1�ɐ������邽�߁Aprivate���\�b�h�Ƃ���B
     */
    private JavelinErrorLogger()
    {
        this.errorOut_ = System.err;
    }

    /**
     * �G���[���O������������B
     * 
     * @param options �N���I�v�V����
     */
    public static void initErrorLog(S2JavelinConfig config)
    {
        // �G���[���K�t�@�C�������擾����B
        String errorFileName = config.getErrorLog();

        // �G���[���O�t�@�C�������w�肳��Ă��Ȃ��ꍇ�́A���������s��Ȃ��B
        if (errorFileName == null)
        {
            return;
        }

        File errorFile = new File(errorFileName);
        File parent = errorFile.getAbsoluteFile().getParentFile();
        if (parent == null || parent.exists() == false)
        {
            // Javelin���s�G���[�o�̓t�@�C���f�B���N�g�������݂��Ȃ��ꍇ�A�W���G���[�o�͂ɃG���[���o�͂��A
            // �ȍ~�̃G���[���O�o�͐��W���G���[�o�͂Ƃ���B
            logErrorLogNotFound(errorFileName);
        }
        else if (errorFile.exists() == true && errorFile.canWrite() == false)
        {
            // Javelin���s�G���[�o�̓t�@�C���ւ̏������݌������Ȃ��ꍇ�A�W���G���[�o�͂ɃG���[���o�͂��A
            // �ȍ~�̃G���[���O�o�͐��W���G���[�o�͂Ƃ���B
            logErrorLogCannotWrite(errorFileName);
        }
        else
        {
            try
            {
                // �G���[���O�̏o�͐�t�@�C�����J���B
                JavelinErrorLogger.getInstance().open(errorFileName);
            }
            catch (FileNotFoundException fnfe)
            {
                // Javelin���s�G���[�o�̓t�@�C���f�B���N�g�������݂��Ȃ��ꍇ�A�W���G���[�o�͂ɃG���[���o�͂��A
                // �ȍ~�̃G���[���O�o�͐��W���G���[�o�͂Ƃ���B
                logErrorLogNotFound(errorFileName);
            }
        }
    }

    /**
     * Javelin���s�G���[�o�̓t�@�C���ւ̏������݌������Ȃ��ꍇ�ɁA�G���[���O�o�͂��s���B
     * 
     * @param errorFileName
     *            �G���[�o�̓t�@�C���B
     */
    private static void logErrorLogCannotWrite(String errorFileName)
    {
        JavelinErrorLogger.getInstance().log(
                                             "Javelin���s�G���[�o�̓t�@�C���ւ̏������݌���������܂���B�W���G���[�o�͂��g�p���܂��B"
                                                     + NEW_LINE + "(javelin.error.log="
                                                     + errorFileName + ")");
    }

    /**
     * Javelin���s�G���[�o�̓t�@�C���f�B���N�g�������݂��Ȃ��ꍇ�ɁA�G���[���O�o�͂��s���B
     * 
     * @param errorFileName
     *            �G���[�o�̓t�@�C���B
     */
    private static void logErrorLogNotFound(String errorFileName)
    {
        JavelinErrorLogger.getInstance().log(
                                             "Javelin���s�G���[�o�̓t�@�C���f�B���N�g�������݂��܂���B�W���G���[�o�͂��g�p���܂��B"
                                                     + NEW_LINE + "(javelin.error.log="
                                                     + errorFileName + ")");
    }

}