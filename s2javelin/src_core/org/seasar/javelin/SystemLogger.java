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
 * Javelinのシステムロガー。<br>
 * 
 * @author eriguchi
 */
public class SystemLogger
{
    /** エラーログ出力日時のフォーマット。 */
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";

    /** 改行文字。 */
    public static final String NEW_LINE    = System.getProperty("line.separator");

    /** システムログファイルの拡張子。 */
    private static final String EXTENTION   = ".log";

    /** システムログファイル名のフォーマット(日付フォーマット(ミリ(sec)まで表示) */
    private static final String LOG_FILE_FORMAT = "jvn_sys_{0,date,yyyy_MM_dd_HHmmss_SSS}" + EXTENTION;

    /** システムログファイルの最大数。 */
    private int                 systemLogNumMax_;

    /** システムログファイルの最大サイズ。 */
    private int                 systemLogSizeMax_;

    /** システムログのログレベル。 */
    private LogLevel            systemLogLevel_;

    /** JavelinErrorLoggerのインスタンス。 */
    private static SystemLogger instance_   = new SystemLogger();

    /** 書き込んだ文字数。 */
    private long                writeCount_ = 0;

    /** システムログファイルの出力先ディレクトリのパス。 */
    private String              logPath_;

    /** システムログファイルのファイル名。 */
    private String              logFileName_;

    /**
     * コンストラクタ。
     * インスタンス数を1つに制限するため、privateメソッドとする。
     */
    private SystemLogger()
    {
    }

    /**
     * JavelinErrorLoggerインスタンス取得用メソッド。
     * 
     * @return JavelinErrorLoggerインスタンス。
     */
    public static SystemLogger getInstance()
    {
        return instance_;
    }

    /**
     * システムログファイル名を生成する。
     * ファイル名のフォーマットは以下のとおり。<br>
     * jvn_sys_yyyyMMddHHmmssSSS.log
     * 
     * @return システムログファイル名。
     */
    private String createLogFileName()
    {
        return MessageFormat.format(LOG_FILE_FORMAT, new Date());
    }

    /**
     * ログメッセージをフォーマットする。
     * 
     * @param level　ログレベル。
     * @param message ログメッセージ。
     * @param throwable 例外。
     * @return フォーマットしたログメッセージ。
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
     * ログを出力する。
     * @param message エラーメッセージ。
     * @param throwable 例外。
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
            // 親ディレクトリを作成する。
            IOUtil.createDirs(logPath_);

            FileOutputStream fileOutputStream =
                    new FileOutputStream(logPath_ + File.separator + logFileName_, true);
            writer = new OutputStreamWriter(fileOutputStream);

            writer.write(formattedMessage);
            writeCount_ += formattedMessage.length();
        }
        catch (Exception ex)
        {
            // 出力できなかった場合は標準エラーに出力する。。
            System.err.println("Javelin実行エラー出力ファイルへの書き込みに失敗しました。標準エラー出力を使用します。" + NEW_LINE
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

        // ローテートが必要な場合はローテートする。
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
     * システムログを初期化する。
     * 
     * @param options 起動オプション
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
            
            // 起動時にログファイルが多数ある場合は削除する。
            IOUtil.removeLogFiles(instance.systemLogNumMax_, instance.logPath_, EXTENTION);
        }
    }

    /**
     * ログレベルの文字列をLogLevelインスタンスに変換する。
     * 
     * @param logLevelStr ログレベルの文字列。
     * @return LogLevel。
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