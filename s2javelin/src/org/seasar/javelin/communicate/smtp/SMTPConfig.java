// SMTPConfig.java
package org.seasar.javelin.communicate.smtp;

import org.seasar.javelin.S2JavelinConfig;
import org.seasar.javelin.util.JavelinConfigUtil;

/**
 * SMTP���[���ʒm�@�\�Ŏg�p����ݒ��ǂݍ���Config�N���X�B
 * 
 * @author AKIBA Makoto
 */
public class SMTPConfig extends S2JavelinConfig
{
    /** SMTP���[���ʒm�@�\�֘A�ݒ荀�ڂ̐ړ����B */
    private static final String  SMTP_PREFIX       = JAVELIN_PREFIX + "smtp.";

    /** ���[���ʒm�𑗐M���邩�ǂ������w�肷��ݒ荀�ږ��B */
    private static final String  SEND_MAIL_KEY     = SMTP_PREFIX + "sendMail";

    /** ���[���T�[�o���w�肷��ݒ荀�ږ��B */
    private static final String  SMTP_SERVER_KEY   = SMTP_PREFIX + "server";

    /** ���M�����[���A�h���X���w�肷��ݒ荀�ږ��B */
    private static final String  SMTP_FROM_KEY     = SMTP_PREFIX + "from";

    /** ���M�惁�[���A�h���X���w�肷��ݒ荀�ږ��B */
    private static final String  SMTP_TO_KEY       = SMTP_PREFIX + "to";

    /** ���[���e���v���[�g���w�肷��ݒ荀�ږ��B */
    private static final String  SMTP_TEMPLATE_KEY = SMTP_PREFIX + "template";

    /** ���[���̃G���R�[�f�B���O���w�肷��ݒ荀�ږ��B */
    private static final String  SMTP_ENCODING_KEY = SMTP_PREFIX + "encoding";

    /** ���[����Subject���w�肷��ݒ荀�ږ��B */
    private static final String  SMTP_SUBJECT_KEY  = SMTP_PREFIX + "subject";
    
    /** ���[���̃G���R�[�f�B���O�ݒ�f�t�H���g�l�B */
    private static final String  SMTP_ENCODING_DEF = "ISO2022JP";

    /** ���[���ʒm�𑗐M���邩�ǂ����̃f�t�H���g�l�B */
    private static final boolean SEND_MAIL_DEF     = false;

    /** ���[���T�[�o�̃f�t�H���g�l�B */
    private static final String  SMTP_SERVER_DEF   = "localhost";

    /** ���M�����[���A�h���X�ݒ�f�t�H���g�l�B */
    private static final String  SMTP_FROM_DEF     = "root@localhost";

    /** ���M�惁�[���A�h���X�ݒ�f�t�H���g�l�B */
    private static final String  SMTP_TO_DEF       = "root@localhost";

    /** ���[���e���v���[�g�̃f�t�H���g�l�B */
    private static final String  SMTP_TEMPLATE_DEF = "default_mail.template";
    
    /** ���[��Subject�̃f�t�H���g�l�B */
    private static final String  SMTP_SUBJECT_DEF  = "Javelin alart";

    /**
     * Config�N���X�𐶐�����B
     */
    public SMTPConfig()
    {
        super();
    }

    /**
     * ���[���ʒm�𑗐M���邩�ǂ������擾����B
     * 
     * @return true=���M����/false=���M���Ȃ��B
     */
    public boolean isSendMail()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        boolean sendMail = util.getBoolean(SEND_MAIL_KEY, SEND_MAIL_DEF);
        return sendMail;
    }

    /**
     * @param sendMail �ݒ肷�� sendMail
     */
    public void setSendMail(boolean sendMail)
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        util.setBoolean(SEND_MAIL_KEY, sendMail);
    }

    /**
     * SMTP�T�[�o�����擾����B
     * 
     * @return SMTP�T�[�o�B
     */
    public String getSmtpServer()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        String server = util.getString(SMTP_SERVER_KEY, SMTP_SERVER_DEF);
        return server;
    }

    /**
     * @param server �ݒ肷�� server
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
     * ���M�����[���A�h���X���擾����B
     * 
     * @return ���M�����[���A�h���X�B
     */
    public String getMailFrom()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        String from = util.getString(SMTP_FROM_KEY, SMTP_FROM_DEF);
        return from;
    }

    /**
     * @param from �ݒ肷�� from
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
     * ���M�惁�[���A�h���X���擾����B
     * 
     * @return ���M�惁�[���A�h���X�B�J���}��؂�ŕ�������������z��B
     */
    public String[] getMailTo()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        String toLine = util.getString(SMTP_TO_KEY, SMTP_TO_DEF);
        String[] to = toLine.split(",");
        return to;
    }

    /**
     * @param to �ݒ肷�� to
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
     * ���[���e���v���[�g�t�@�C�������擾����B
     * 
     * @return ���[���e���v���[�g�t�@�C�����B
     */
    public String getMailTemplate()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        String template = util.getString(SMTP_TEMPLATE_KEY, SMTP_TEMPLATE_DEF);
        return template;
    }

    /**
     * @param template �ݒ肷�� template
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
     * ���[���̃G���R�[�h�����擾����B
     * 
     * @return ���[���̃G���R�[�h���B
     */
    public String getMailEncoding()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        String encoding = util.getString(SMTP_ENCODING_KEY, SMTP_ENCODING_DEF);
        return encoding;
    }

    /**
     * @param encoding �ݒ肷�� encoding
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
     * ���[����Subject���擾����B
     * 
     * @return ���[��Subject�B
     */
    public String getMailSubject()
    {
        JavelinConfigUtil util = JavelinConfigUtil.getInstance();
        String subject = util.getString(SMTP_SUBJECT_KEY, SMTP_SUBJECT_DEF);
        return subject;
    }

    /**
     * @param subject ���[��Subject�B
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
