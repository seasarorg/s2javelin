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
 * Javelin�����o�����A�������l���߂��󂯂�SMTP�Ń��[���ʒm�𑗐M���郊�X�i�[�B
 * 
 * @author AKIBA Makoto
 */
public class SMTPListener implements AlarmListener
{
    /** ���s�����B */
    private static final String LS                       = System.getProperty("line.separator");

    /** �v���Z�X�����擾����ׂ̒u���L�[���[�h�B */
    /** JavaMail�ɓn��SMTP�T�[�o�̃L�[���B */
    private static final String SMTP_HOST_KEY            = "mail.smtp.host";

    /** ���[���[��\�����O�B */
    private static final String X_MAILER                 = "Javelin Mail Sender.";

    private static final String KEYWORD_PROCESS_NAME     = "processName";

    /** �N���X�����擾����ׂ̒u���L�[���[�h�B */
    private static final String KEYWORD_CLASS_NAME       = "className";

    /** ���\�b�h�����擾����ׂ̒u���L�[���[�h�B */
    private static final String KEYWORD_METHOD_NAME      = "methodName";

    /** �Ăяo���񐔂��擾����ׂ̒u���L�[���[�h�B */
    private static final String KEYWORD_COUNT            = "count";

    /** �ŏ����s���Ԃ��擾����ׂ̒u���L�[���[�h�B */
    private static final String KEYWORD_MINIMUM          = "minimum";

    /** �ő���s���Ԃ��擾����ׂ̒u���L�[���[�h�B */
    private static final String KEYWORD_MAXIMUM          = "maximum";

    /** ���ώ��s���Ԃ��擾����ׂ̒u���L�[���[�h�B */
    private static final String KEYWORD_AVERAGE          = "average";

    /** ��O�����񐔂��擾����ׂ̒u���L�[���[�h�B */
    private static final String KEYWORD_THROWABLE_COUNT  = "throwableCount";

    /** ���O�L�^�������l���擾����ׂ̒u���L�[���[�h�B */
    private static final String KEYWORD_RECORD_THRESHOLD = "recordThreshold";

    /** �A���[���������l���擾����ׂ̒u���L�[���[�h�B */
    private static final String KEYWORD_ALARM_THRESHOLD  = "alarmThreshold";

    /** �Ăяo������Invocation�����擾����ׂ̒u���L�[���[�h�B */
    private static final String KEYWORD_CALLER_SET       = "callerSet";

    /** �e���v���[�g����A���������擾����ׂ̒u���L�[���[�h�B */
    private static final String KEYWORD_DATE             = "date";

    /** �e���v���[�g����A�����������擾����ׂ̒u���L�[���[�h�B */
    private static final String KEYWORD_TIME             = "time";

    /** �e���v���[�g����A��������(�~���b�܂�)���擾����ׂ̒u���L�[���[�h�B */
    private static final String KEYWORD_TIMEMILLIS       = "timeMillis";

    /** ���ݎ�������t�ɕϊ�����t�H�[�}�b�^�B(�����͊܂܂Ȃ�) */
    private SimpleDateFormat    dateFormatter_           = new SimpleDateFormat("yyyy/MM/dd");

    /** ���ݎ����������ɕϊ�����t�H�[�}�b�^�B(���t�͊܂܂Ȃ�) */
    private SimpleDateFormat    timeFormatter_           = new SimpleDateFormat("HH:mm:ss");

    /** ���ݎ���������(�~���b�܂�)�ɕϊ�����t�H�[�}�b�^�B(���t�͊܂܂Ȃ�) */
    private SimpleDateFormat    millisFormatter_         = new SimpleDateFormat("HH:mm:ss.SSS");

    /** ���t�^�����𕶎��񉻂��邽�߂�Temporary�������̈�B */
    private Date                date_                    = new Date();

    /**
     * �������l���߂������������[���ʒm����B
     * 
     * @param invocation �������l���߂����Ăяo���̏��B
     * @see org.seasar.javelin.communicate.AlarmListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)
     */
    public void sendExceedThresholdAlarm(Invocation invocation)
    {
        SMTPConfig config = new SMTPConfig();
        boolean sendMail = config.isSendMail();
        if (sendMail == false)
        {
            // ���[�����M���Ȃ��ꍇ�͉��������ɏI������
            return;
        }

        try
        {
            // ���[���I�u�W�F�N�g���쐬����
            MimeMessage message = createMailMessage(config, invocation);
            
            // ���[���𑗐M����
            Transport.send(message);
        }
        catch (MessagingException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Mail�I�u�W�F�N�g���쐬����B
     * 
     * @return �쐬�������b�Z�[�W�B
     * @throws MessagingException ���b�Z�[�W�쐬���ɃG���[�����������ꍇ�B
     */
    private MimeMessage createMailMessage(SMTPConfig config, Invocation invocation)
        throws MessagingException
    {
        // JavaMail�ɓn���v���p�e�B��ݒ肷��
        Properties props = System.getProperties();
        props.setProperty(SMTP_HOST_KEY, config.getSmtpServer());

        // ���[���T�[�o�ɑ΂���Z�b�V�����L�[�ƂȂ�I�u�W�F�N�g���쐬����
        Session session = Session.getDefaultInstance(props);

        // MIME���b�Z�[�W���쐬����
        MimeMessage message = new MimeMessage(session);
        
        // ���b�Z�[�W�̃v���p�e�B��ݒ肷��
        // :�w�b�_
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
     * �ݒ�l��Subject��ǂݍ��݁A������u������B
     * 
     * @param config �ݒ�l�B
     * @param invocation �Ăяo����Invocation�I�u�W�F�N�g�B
     * @return �ϊ���̃��[��Subject�B
     */
    private String createSubject(SMTPConfig config, Invocation invocation)
    {
        String subjectTemplate = config.getMailSubject();
        String subject = convertTemplate(subjectTemplate, invocation);
        return subject;
    }

    /**
     * ���[���e���v���[�g��ǂݍ��݁A������u������B
     * 
     * @param config �ݒ�l�B
     * @param invocation �Ăяo����Invocation�I�u�W�F�N�g�B
     * @return �ϊ���̃��[���{���B
     * @throws IOException �e���v���[�g�̓ǂݍ��݂Ɏ��s�����ꍇ�B
     */
    private String createBody(SMTPConfig config, Invocation invocation)
        throws IOException
    {
        // �{�����e���v���[�g����ǂݍ���
        String bodyTemplate = readTemplate(config.getMailTemplate());
        String body = convertTemplate(bodyTemplate, invocation);
        return body;
    }
    
    /**
     * �e���v���[�g�ɑ΂��ĕ�����u�������{����B
     * 
     * @param template �e���v���[�g������B
     * @param invocation �Ăяo����Invocation�I�u�W�F�N�g�B
     * @return �ϊ���̕�����B
     */
    private String convertTemplate(String template, Invocation invocation)
    {
        // ������u�����s��
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
     * �G���[���̃f�t�H���g���b�Z�[�W��������쐬����B
     * 
     * @param invocation �Ăяo����Invocation�I�u�W�F�N�g�B
     * @return �쐬�������b�Z�[�W������B
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
     * Caller�ƂȂ�Invocation�̔z��𕶎��񉻂���B<br>
     * �eInvocation�I�u�W�F�N�g�́A"�N���X��#���\�b�h��"�̌`�ŕ����񉻂���B
     * 
     * @param callerSet Caller�ƂȂ�Invocation�z��B
     * @return �eInvocation�I�u�W�F�N�g�𕶎��񉻂������ʁB
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
     * �t�@�C����S�ēǂݍ��݁A�P���String�I�u�W�F�N�g�Ƃ��ĕԂ��B
     * �e���s��String�I�u�W�F�N�g���ɑ}�������B
     * 
     * @param filePath �ǂݍ��ރe���v���[�g�̃p�X�B
     * @return �ǂݍ��񂾕�����B
     * @throws IOException �t�@�C���̓ǂݍ��݂Ɏ��s�����ꍇ�B
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
