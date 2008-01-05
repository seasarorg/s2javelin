// SMTPConfig.java
package org.seasar.javelin.communicate.smtp;

import org.seasar.javelin.S2JavelinConfig;
import org.seasar.javelin.util.JavelinConfigUtil;

/**
 * SMTPメール通知機能で使用する設定を読み込むConfigクラス。
 * 
 * @author AKIBA Makoto
 */
public class SMTPConfig extends S2JavelinConfig
{
    /** SMTPメール通知機能関連設定項目の接頭辞。 */
    private static final String  SMTP_PREFIX       = JAVELIN_PREFIX + "smtp.";

    /** メール通知を送信するかどうかを指定する設定項目名。 */
    private static final String  SEND_MAIL_KEY     = SMTP_PREFIX + "sendMail";

    /** メールサーバを指定する設定項目名。 */
    private static final String  SMTP_SERVER_KEY   = SMTP_PREFIX + "server";

    /** 送信元メールアドレスを指定する設定項目名。 */
    private static final String  SMTP_FROM_KEY     = SMTP_PREFIX + "from";

    /** 送信先メールアドレスを指定する設定項目名。 */
    private static final String  SMTP_TO_KEY       = SMTP_PREFIX + "to";

    /** メールテンプレートを指定する設定項目名。 */
    private static final String  SMTP_TEMPLATE_KEY = SMTP_PREFIX + "template";

    /** メールのエンコーディングを指定する設定項目名。 */
    private static final String  SMTP_ENCODING_KEY = SMTP_PREFIX + "encoding";

    /** メールのSubjectを指定する設定項目名。 */
    private static final String  SMTP_SUBJECT_KEY  = SMTP_PREFIX + "subject";
    
    /** メールのエンコーディング設定デフォルト値。 */
    private static final String  SMTP_ENCODING_DEF = "ISO2022JP";

    /** メール通知を送信するかどうかのデフォルト値。 */
    private static final boolean SEND_MAIL_DEF     = false;

    /** メールサーバのデフォルト値。 */
    private static final String  SMTP_SERVER_DEF   = "localhost";

    /** 送信元メールアドレス設定デフォルト値。 */
    private static final String  SMTP_FROM_DEF     = "root@localhost";

    /** 送信先メールアドレス設定デフォルト値。 */
    private static final String  SMTP_TO_DEF       = "root@localhost";

    /** メールテンプレートのデフォルト値。 */
    private static final String  SMTP_TEMPLATE_DEF = "default_mail.template";
    
    /** メールSubjectのデフォルト値。 */
    private static final String  SMTP_SUBJECT_DEF  = "Javelin alart";

    /**
     * Configクラスを生成する。
     */
    public SMTPConfig()
    {
        super();
    }

    /**
     * メール通知を送信するかどうかを取得する。
     * 
     * @return true=送信する/false=送信しない。
     */
    public boolean isSendMail()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        boolean sendMail = util.getBoolean(SEND_MAIL_KEY, SEND_MAIL_DEF);
        return sendMail;
    }

    /**
     * @param sendMail 設定する sendMail
     */
    public void setSendMail(boolean sendMail)
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        util.setBoolean(SEND_MAIL_KEY, sendMail);
    }

    /**
     * SMTPサーバ名を取得する。
     * 
     * @return SMTPサーバ。
     */
    public String getSmtpServer()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        String server = util.getString(SMTP_SERVER_KEY, SMTP_SERVER_DEF);
        return server;
    }

    /**
     * @param server 設定する server
     */
    public void setSmtpServer(String server)
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        if (server == null)
        {
            util.setString(SMTP_SERVER_KEY, "");
        }
        else
        {
            util.setString(SMTP_SERVER_KEY, server);
        }
    }

    /**
     * 送信元メールアドレスを取得する。
     * 
     * @return 送信元メールアドレス。
     */
    public String getMailFrom()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        String from = util.getString(SMTP_FROM_KEY, SMTP_FROM_DEF);
        return from;
    }

    /**
     * @param from 設定する from
     */
    public void setMailFrom(String from)
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        if (from == null)
        {
            util.setString(SMTP_FROM_KEY, "");
        }
        else
        {
            util.setString(SMTP_FROM_KEY, from);
        }
    }

    /**
     * 送信先メールアドレスを取得する。
     * 
     * @return 送信先メールアドレス。カンマ区切りで分割した文字列配列。
     */
    public String[] getMailTo()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        String toLine = util.getString(SMTP_TO_KEY, SMTP_TO_DEF);
        String[] to = toLine.split(",");
        return to;
    }

    /**
     * @param to 設定する to
     */
    public void setMailTo(String[] to)
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        if (to == null)
        {
            util.setString(SMTP_TO_KEY, "");
            return;
        }
        
        StringBuilder buf = new StringBuilder();
        for (int index = 0; index < to.length; index++)
        {
            if (index > 0)
            {
                buf.append(",");
            }
            buf.append(to[index]);
        }
        
        util.setString(SMTP_TO_KEY, buf.toString());
    }

    /**
     * メールテンプレートファイル名を取得する。
     * 
     * @return メールテンプレートファイル名。
     */
    public String getMailTemplate()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        String template = util.getString(SMTP_TEMPLATE_KEY, SMTP_TEMPLATE_DEF);
        return template;
    }

    /**
     * @param template 設定する template
     */
    public void setMailTemplate(String template)
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        if (template == null)
        {
            util.setString(SMTP_TEMPLATE_KEY, "");
        }
        else
        {
            util.setString(SMTP_TEMPLATE_KEY, template);
        }
    }

    /**
     * メールのエンコード名を取得する。
     * 
     * @return メールのエンコード名。
     */
    public String getMailEncoding()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        String encoding = util.getString(SMTP_ENCODING_KEY, SMTP_ENCODING_DEF);
        return encoding;
    }

    /**
     * @param encoding 設定する encoding
     */
    public void setMailEncoding(String encoding)
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        if (encoding == null)
        {
            util.setString(SMTP_ENCODING_KEY, "");
        }
        else
        {
            util.setString(SMTP_ENCODING_KEY, encoding);
        }
    }
    
    /**
     * メールのSubjectを取得する。
     * 
     * @return メールSubject。
     */
    public String getMailSubject()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        String subject = util.getString(SMTP_SUBJECT_KEY, SMTP_SUBJECT_DEF);
        return subject;
    }

    /**
     * @param subject メールSubject。
     */
    public void setMailSubject(String subject)
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        if (subject == null)
        {
            util.setString(SMTP_SUBJECT_KEY, "");
        }
        else
        {
            util.setString(SMTP_SUBJECT_KEY, subject);
        }
    }
}
