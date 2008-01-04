// SMTPListener.java
package org.seasar.javelin.communicate.smtp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.seasar.javelin.bean.Invocation;
import org.seasar.javelin.communicate.AlarmListener;
import org.seasar.javelin.util.KeywordConverter;
import org.seasar.javelin.util.KeywordConverterFactory;

/**
 * Javelinが検出した、しきい値超過を受けてSMTPでメール通知を送信するリスナー。
 * 
 * @author AKIBA Makoto
 */
public class SMTPListener implements AlarmListener
{
    /** 改行文字。 */
    private static final String LS                       = System.getProperty("line.separator");

    /** プロセス名を取得する為の置換キーワード。 */
    /** JavaMailに渡すSMTPサーバのキー名。 */
    private static final String SMTP_HOST_KEY            = "mail.smtp.host";

    /** メーラーを表す名前。 */
    private static final String X_MAILER                 = "Javelin Mail Sender.";

    private static final String KEYWORD_PROCESS_NAME     = "processName";

    /** クラス名を取得する為の置換キーワード。 */
    private static final String KEYWORD_CLASS_NAME       = "className";

    /** メソッド名を取得する為の置換キーワード。 */
    private static final String KEYWORD_METHOD_NAME      = "methodName";

    /** 呼び出し回数を取得する為の置換キーワード。 */
    private static final String KEYWORD_COUNT            = "count";

    /** 最小実行時間を取得する為の置換キーワード。 */
    private static final String KEYWORD_MINIMUM          = "minimum";

    /** 最大実行時間を取得する為の置換キーワード。 */
    private static final String KEYWORD_MAXIMUM          = "maximum";

    /** 平均実行時間を取得する為の置換キーワード。 */
    private static final String KEYWORD_AVERAGE          = "average";

    /** 例外発生回数を取得する為の置換キーワード。 */
    private static final String KEYWORD_THROWABLE_COUNT  = "throwableCount";

    /** ログ記録しきい値を取得する為の置換キーワード。 */
    private static final String KEYWORD_RECORD_THRESHOLD = "recordThreshold";

    /** アラームしきい値を取得する為の置換キーワード。 */
    private static final String KEYWORD_ALARM_THRESHOLD  = "alarmThreshold";

    /** 呼び出し元のInvocation情報を取得する為の置換キーワード。 */
    private static final String KEYWORD_CALLER_SET       = "callerSet";

    /** テンプレートから、発生日を取得する為の置換キーワード。 */
    private static final String KEYWORD_DATE             = "date";

    /** テンプレートから、発生時刻を取得する為の置換キーワード。 */
    private static final String KEYWORD_TIME             = "time";

    /** テンプレートから、発生時刻(ミリ秒まで)を取得する為の置換キーワード。 */
    private static final String KEYWORD_TIMEMILLIS       = "timeMillis";

    /** 現在時刻を日付に変換するフォーマッタ。(時刻は含まない) */
    private SimpleDateFormat    dateFormatter_           = new SimpleDateFormat("yyyy/MM/dd");

    /** 現在時刻を時刻に変換するフォーマッタ。(日付は含まない) */
    private SimpleDateFormat    timeFormatter_           = new SimpleDateFormat("HH:mm:ss");

    /** 現在時刻を時刻(ミリ秒まで)に変換するフォーマッタ。(日付は含まない) */
    private SimpleDateFormat    millisFormatter_         = new SimpleDateFormat("HH:mm:ss.SSS");

    /** 日付／時刻を文字列化するためのTemporaryメモリ領域。 */
    private Date                date_                    = new Date();

    /**
     * しきい値超過した処理をメール通知する。
     * 
     * @param invocation しきい値超過した呼び出しの情報。
     * @see org.seasar.javelin.communicate.AlarmListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)
     */
    public void sendExceedThresholdAlarm(Invocation invocation)
    {
        SMTPConfig config = new SMTPConfig();
        boolean sendMail = config.isSendMail();
        if (sendMail == false)
        {
            // メール送信しない場合は何もせずに終了する
            return;
        }

        try
        {
            // メールオブジェクトを作成する
            MimeMessage message = createMailMessage(config, invocation);
            
            // メールを送信する
            Transport.send(message);
        }
        catch (MessagingException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Mailオブジェクトを作成する。
     * 
     * @return 作成したメッセージ。
     * @throws MessagingException メッセージ作成中にエラーが発生した場合。
     */
    private MimeMessage createMailMessage(SMTPConfig config, Invocation invocation)
        throws MessagingException
    {
        // JavaMailに渡すプロパティを設定する
        Properties props = System.getProperties();
        props.setProperty(SMTP_HOST_KEY, config.getSmtpServer());

        // メールサーバに対するセッションキーとなるオブジェクトを作成する
        Session session = Session.getDefaultInstance(props);

        // MIMEメッセージを作成する
        MimeMessage message = new MimeMessage(session);
        
        // メッセージのプロパティを設定する
        // :ヘッダ
        date_.setTime(System.currentTimeMillis());
        String encoding = config.getMailEncoding();
        message.setHeader("X-Mailer", X_MAILER);
        message.setHeader("Content-Type", "text/plain; charset=\"" + encoding + "\"");
        message.setSentDate(date_);
        
        // :FROM
        String from = config.getMailFrom();
        InternetAddress fromAddr = new InternetAddress(from);
        message.setFrom(fromAddr);
        
        // :TO
        String[] recipients = config.getMailTo();
        for (String toStr : recipients)
        {
            InternetAddress toAddr = new InternetAddress(toStr);
            message.addRecipient(Message.RecipientType.TO, toAddr);
        }

        String body;
        try
        {
            body = createBody(config, invocation);
            
            String subject = createSubject(config, invocation);
            message.setSubject(subject, encoding);
        }
        catch (IOException exception)
        {
            message.setSubject("Javelin alart");
            body = "*** Failed read mail template: " + config.getMailTemplate() + " ***"
                   + LS + LS
                   + createDefaultBody(invocation);
        }
        
        message.setText(body, encoding);

        return message;
    }
    
    /**
     * 設定値のSubjectを読み込み、文字列置換する。
     * 
     * @param config 設定値。
     * @param invocation 呼び出し元Invocationオブジェクト。
     * @return 変換後のメールSubject。
     */
    private String createSubject(SMTPConfig config, Invocation invocation)
    {
        String subjectTemplate = config.getMailSubject();
        String subject = convertTemplate(subjectTemplate, invocation);
        return subject;
    }

    /**
     * メールテンプレートを読み込み、文字列置換する。
     * 
     * @param config 設定値。
     * @param invocation 呼び出し元Invocationオブジェクト。
     * @return 変換後のメール本文。
     * @throws IOException テンプレートの読み込みに失敗した場合。
     */
    private String createBody(SMTPConfig config, Invocation invocation)
        throws IOException
    {
        // 本文をテンプレートから読み込む
        String bodyTemplate = readTemplate(config.getMailTemplate());
        String body = convertTemplate(bodyTemplate, invocation);
        return body;
    }
    
    /**
     * テンプレートに対して文字列置換を実施する。
     * 
     * @param template テンプレート文字列。
     * @param invocation 呼び出し元Invocationオブジェクト。
     * @return 変換後の文字列。
     */
    private String convertTemplate(String template, Invocation invocation)
    {
        // 文字列置換を行う
        KeywordConverter conv = KeywordConverterFactory.createDollarBraceConverter();
        conv.addConverter(KEYWORD_CLASS_NAME, invocation.getClassName());
        conv.addConverter(KEYWORD_METHOD_NAME, invocation.getMethodName());
        conv.addConverter(KEYWORD_COUNT, invocation.getCount());
        conv.addConverter(KEYWORD_MINIMUM, invocation.getMinimum());
        conv.addConverter(KEYWORD_MAXIMUM, invocation.getMaximum());
        conv.addConverter(KEYWORD_AVERAGE, invocation.getAverage());
        conv.addConverter(KEYWORD_THROWABLE_COUNT, invocation.getThrowableCount());
        conv.addConverter(KEYWORD_RECORD_THRESHOLD, invocation.getRecordThreshold());
        conv.addConverter(KEYWORD_ALARM_THRESHOLD, invocation.getAlarmThreshold());
        conv.addConverter(KEYWORD_PROCESS_NAME, invocation.getProcessName());
        //conv.addConverter(KEYWORD_PROCESS_NAME, "ProcessName");
        conv.addConverter(KEYWORD_CALLER_SET,
                          buildCallerSetString(invocation.getAllCallerInvocation()));
        conv.addConverter(KEYWORD_DATE, dateFormatter_.format(date_));
        conv.addConverter(KEYWORD_TIME, timeFormatter_.format(date_));
        conv.addConverter(KEYWORD_TIMEMILLIS, millisFormatter_.format(date_));
        
        return conv.convert(template);
    }
    
    /**
     * エラー時のデフォルトメッセージ文字列を作成する。
     * 
     * @param invocation 呼び出し元Invocationオブジェクト。
     * @return 作成したメッセージ文字列。
     */
    private String createDefaultBody(Invocation invocation)
    {
        StringBuilder buf = new StringBuilder();
        
        buf.append(KEYWORD_DATE             + " = " + dateFormatter_.format(date_) + " ");
        buf.append(millisFormatter_.format(date_) + LS);
        buf.append(KEYWORD_PROCESS_NAME     + " = " + invocation.getProcessName() + "\n");
        //buf.append(KEYWORD_PROCESS_NAME     + " = " + "ProcessName" + LS);
        buf.append(KEYWORD_CLASS_NAME       + " = " + invocation.getClassName() + LS);
        buf.append(KEYWORD_METHOD_NAME      + " = " + invocation.getMethodName() + LS);
        buf.append(LS);
        buf.append(KEYWORD_COUNT            + " = " + invocation.getCount() + LS);
        buf.append(KEYWORD_MINIMUM          + " = " + invocation.getMinimum() + LS);
        buf.append(KEYWORD_MAXIMUM          + " = " + invocation.getMaximum() + LS);
        buf.append(KEYWORD_AVERAGE          + " = " + invocation.getAverage() + LS);
        buf.append(KEYWORD_THROWABLE_COUNT  + " = " + invocation.getThrowableCount() + LS);
        buf.append(LS);
        buf.append(KEYWORD_CALLER_SET       + " = {" + LS);
        buf.append(buildCallerSetString(invocation.getAllCallerInvocation()));
        buf.append("}");
        buf.append(LS);
        buf.append(KEYWORD_RECORD_THRESHOLD + " = " + invocation.getRecordThreshold() + LS);
        buf.append(KEYWORD_ALARM_THRESHOLD  + " = " + invocation.getAlarmThreshold() + LS);
        
        return buf.toString();
    }
    
    /**
     * CallerとなるInvocationの配列を文字列化する。<br>
     * 各Invocationオブジェクトは、"クラス名#メソッド名"の形で文字列化する。
     * 
     * @param callerSet CallerとなるInvocation配列。
     * @return 各Invocationオブジェクトを文字列化した結果。
     */
    private String buildCallerSetString(Invocation[] callerSet)
    {
        StringBuilder buf = new StringBuilder();

        for (Invocation invocation : callerSet)
        {
            if (buf.length() > 0)
            {
                buf.append("," + LS);
            }
            buf.append(invocation.getClassName());
            buf.append("#");
            buf.append(invocation.getMethodName());
        }
        
        return buf.toString();
    }

    /**
     * ファイルを全て読み込み、単一のStringオブジェクトとして返す。
     * 各改行はStringオブジェクト中に挿入される。
     * 
     * @param filePath 読み込むテンプレートのパス。
     * @return 読み込んだ文字列。
     * @throws IOException ファイルの読み込みに失敗した場合。
     */
    private String readTemplate(String filePath)
        throws IOException
    {
        StringBuilder template = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        while (true)
        {
            String line = br.readLine();
            if (line == null)
            {
                break;
            }

            template.append(line);
            template.append(LS);
        }

        return template.toString();
    }
}
