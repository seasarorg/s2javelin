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
 * Javelinの実行時エラーのロガークラス。<br>
 * 
 * Singletonパターンを用いてインスタンス数を一つに制限している。
 * @author eriguchi
 */
public class JavelinErrorLogger
{
    /**
     * 改行文字。
     */
    private static final String           NEW_LINE    = System.getProperty("line.separator");

    /**
     * エラーログ出力日時のフォーマット。
     */
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS";

    /**
     * JavelinErrorLoggerのインスタンス。
     */
    private static JavelinErrorLogger     instance_   = new JavelinErrorLogger();

    /**
     * エラーの出力先。
     */
    private PrintStream                   errorOut_;

    /**
     * JavelinErrorLoggerインスタンス取得用メソッド。
     * 
     * @return JavelinErrorLoggerインスタンス。
     */
    public static JavelinErrorLogger getInstance()
    {
        return instance_;
    }

    /**
     * エラーログファイルをオープンする。
     * @param logFileName エラーログファイル名。
     * @throws FileNotFoundException エラーログファイルが見つからない場合。
     */
    public void open(String logFileName)
        throws FileNotFoundException
    {
        if (this.errorOut_ != null)
        {
            this.close();
        }

        // ファイル名がnullの場合は何もしない。
        if (logFileName == null)
        {
            return;
        }
        FileOutputStream fileOutputStream = new FileOutputStream(logFileName, true);
        PrintStream logStream = new PrintStream(fileOutputStream);
        this.errorOut_ = logStream;
    }

    /**
     * エラーログファイルをクローズする。
     */
    public void close()
    {
        if (this.errorOut_ != System.err)
        {
            this.errorOut_.close();
        }
    }

    /**
     * エラーログを出力する。
     * @param message エラーメッセージ。
     * @param throwable 例外。
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
     * エラーログを出力する。
     * @param message エラーメッセージ。
     */
    public void log(String message)
    {
        log(message, null);
    }

    /**
     * エラーログを出力する。
     * @param throwable 例外。
     */
    public void log(Throwable throwable)
    {
        log(null, throwable);
    }

    /**
     * コンストラクタ。
     * インスタンス数を1つに制限するため、privateメソッドとする。
     */
    private JavelinErrorLogger()
    {
        this.errorOut_ = System.err;
    }

    /**
     * エラーログを初期化する。
     * 
     * @param options 起動オプション
     */
    public static void initErrorLog(S2JavelinConfig config)
    {
        // エラーロガファイル名を取得する。
        String errorFileName = config.getErrorLog();

        // エラーログファイル名が指定されていない場合は、初期化を行わない。
        if (errorFileName == null)
        {
            return;
        }

        File errorFile = new File(errorFileName);
        File parent = errorFile.getAbsoluteFile().getParentFile();
        if (parent == null || parent.exists() == false)
        {
            // Javelin実行エラー出力ファイルディレクトリが存在しない場合、標準エラー出力にエラーを出力し、
            // 以降のエラーログ出力先を標準エラー出力とする。
            logErrorLogNotFound(errorFileName);
        }
        else if (errorFile.exists() == true && errorFile.canWrite() == false)
        {
            // Javelin実行エラー出力ファイルへの書き込み権限がない場合、標準エラー出力にエラーを出力し、
            // 以降のエラーログ出力先を標準エラー出力とする。
            logErrorLogCannotWrite(errorFileName);
        }
        else
        {
            try
            {
                // エラーログの出力先ファイルを開く。
                JavelinErrorLogger.getInstance().open(errorFileName);
            }
            catch (FileNotFoundException fnfe)
            {
                // Javelin実行エラー出力ファイルディレクトリが存在しない場合、標準エラー出力にエラーを出力し、
                // 以降のエラーログ出力先を標準エラー出力とする。
                logErrorLogNotFound(errorFileName);
            }
        }
    }

    /**
     * Javelin実行エラー出力ファイルへの書き込み権限がない場合に、エラーログ出力を行う。
     * 
     * @param errorFileName
     *            エラー出力ファイル。
     */
    private static void logErrorLogCannotWrite(String errorFileName)
    {
        JavelinErrorLogger.getInstance().log(
                                             "Javelin実行エラー出力ファイルへの書き込み権限がありません。標準エラー出力を使用します。"
                                                     + NEW_LINE + "(javelin.error.log="
                                                     + errorFileName + ")");
    }

    /**
     * Javelin実行エラー出力ファイルディレクトリが存在しない場合に、エラーログ出力を行う。
     * 
     * @param errorFileName
     *            エラー出力ファイル。
     */
    private static void logErrorLogNotFound(String errorFileName)
    {
        JavelinErrorLogger.getInstance().log(
                                             "Javelin実行エラー出力ファイルディレクトリが存在しません。標準エラー出力を使用します。"
                                                     + NEW_LINE + "(javelin.error.log="
                                                     + errorFileName + ")");
    }

}