package org.seasar.javelin.communicate.smtp;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.management.ObjectName;

import junit.framework.TestCase;

import org.seasar.javelin.bean.Invocation;

/**
 * SMTPListener�N���X�̃e�X�g�P�[�X�B<br/>
 * �����̃e�X�g�P�[�X�����s����ƃ��[�������M����܂��B
 * 
 * @author AKIBA Makoto
 */
public class SMTPListenerTest extends TestCase
{
    /** ���F���s�O�Ƀ��[���A�h���X��K�؂ȃA�h���X�ɕύX���Ă��������B */
    private static final String MAIL_TO = "javelin@example.com";

    private static final String LS = System.getProperty("line.separator");
    
    private SMTPConfig config_;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
        
        // SMTPConfig�̏�����(�t�@�C���̓ǂݍ���)
        System.setProperty("javelin.property", "conf/javelin.properties");
        config_ = new SMTPConfig();
        config_.isSendMail();
    }
    
    //------------------------------------------------------------
    // sendExceedThresholdAlarm()
    
    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)} �̂��߂̃e�X�g�E���\�b�h�B
     * 
     * 01: Invocation��null
     */
    public void testSendExceedThresholdAlarm01()
    {
        config_.setSendMail(true);
        config_.setSmtpServer("castor.smg.co.jp");
        config_.setMailTo(new String[] { MAIL_TO });
        
        SMTPListener listener = new SMTPListener();
        listener.sendExceedThresholdAlarm(null);
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)} �̂��߂̃e�X�g�E���\�b�h�B
     * 
     * 02: �R���t�B�O�̐ݒ�l(javelin.sendMail : false)
     */
    public void testSendExceedThresholdAlarm02()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testSendExceedThresholdAlarm02",
                                                   1, 2, 3, 4);
            
            config_.setSendMail(false);
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailTo(new String[] { MAIL_TO });
            
            SMTPListener listener = new SMTPListener();
            listener.sendExceedThresholdAlarm(invocation);
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)} �̂��߂̃e�X�g�E���\�b�h�B
     * 
     * 03: �R���t�B�O�̐ݒ�l(javelin.sendMail : true)
     */
    public void testSendExceedThresholdAlarm03()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testSendExceedThresholdAlarm03",
                                                   1, 2, 3, 4);
            
            config_.setSendMail(true);
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom(MAIL_TO);
            config_.setMailTo(new String[] { MAIL_TO });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            listener.sendExceedThresholdAlarm(invocation);
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    //------------------------------------------------------------
    // createMailMessage() : config
    
    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} �̂��߂̃e�X�g�E���\�b�h�B
     * 
     * 01: �R���t�B�O�̐ݒ�l(javelin.server : ����)
     */
    public void testCreateMailMessage_config01()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config01",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            Address[] fmAddrs = msg.getFrom();
            Address[] toAddrs = msg.getRecipients(Message.RecipientType.TO);
            Address[] rtAddrs = msg.getReplyTo();
            
            assertEquals(1, fmAddrs.length);
            assertEquals("javelin-test-fm@smg.co.jp", fmAddrs[0].toString());
            assertEquals(1, toAddrs.length);
            assertEquals("javelin-test-to@smg.co.jp", toAddrs[0].toString());
            assertEquals(1, rtAddrs.length);
            assertEquals("javelin-test-fm@smg.co.jp", rtAddrs[0].toString());
            assertEquals("Javelin alart", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }
    
    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} �̂��߂̃e�X�g�E���\�b�h�B
     * 
     * 02: �R���t�B�O�̐ݒ�l(javelin.server : �ُ�[�p�����[�^�w�薳��])
     */
    public void testCreateMailMessage_config02()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config02",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer(null);
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            listener.createMailMessage(config_, invocation);
            
            fail("test expected to occur an exception.");
        }
        catch (MessagingException exception)
        {
        }
        catch (Exception exception)
        {
            fail("test expected to occur a MessagingException. but acturally " + exception.getClass().getName());
        }
    }
    
    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} �̂��߂̃e�X�g�E���\�b�h�B
     * 
     * 03: �R���t�B�O�̐ݒ�l(javelin.server : �ُ�[��̃p�����[�^])
     */
    public void testCreateMailMessage_config03()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config03",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            listener.createMailMessage(config_, invocation);
            
            fail("test expected to occur an exception.");
        }
        catch (MessagingException exception)
        {
        }
        catch (Exception exception)
        {
            fail("test expected to occur a MessagingException. but acturally " + exception.getClass().getName());
        }
    }
    
    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} �̂��߂̃e�X�g�E���\�b�h�B
     * 
     * 04: �R���t�B�O�̐ݒ�l(javelin.server : �ُ�[���݂��Ȃ��T�[�o])
     */
    public void testCreateMailMessage_config04()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config04",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("no-use.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            Address[] fmAddrs = msg.getFrom();
            Address[] toAddrs = msg.getRecipients(Message.RecipientType.TO);
            Address[] rtAddrs = msg.getReplyTo();
            
            assertEquals(1, fmAddrs.length);
            assertEquals("javelin-test-fm@smg.co.jp", fmAddrs[0].toString());
            assertEquals(1, toAddrs.length);
            assertEquals("javelin-test-to@smg.co.jp", toAddrs[0].toString());
            assertEquals(1, rtAddrs.length);
            assertEquals("javelin-test-fm@smg.co.jp", rtAddrs[0].toString());
            assertEquals("Javelin alart", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }
    
    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} �̂��߂̃e�X�g�E���\�b�h�B
     * 
     * 06: �R���t�B�O�̐ݒ�l(javelin.from : ����[@����])
     */
    public void testCreateMailMessage_config06()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config06",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            Address[] fmAddrs = msg.getFrom();
            Address[] toAddrs = msg.getRecipients(Message.RecipientType.TO);
            Address[] rtAddrs = msg.getReplyTo();
            
            assertEquals(1, fmAddrs.length);
            assertEquals("javelin-test", fmAddrs[0].toString());
            assertEquals(1, toAddrs.length);
            assertEquals("javelin-test-to@smg.co.jp", toAddrs[0].toString());
            assertEquals(1, rtAddrs.length);
            assertEquals("javelin-test", rtAddrs[0].toString());
            assertEquals("Javelin alart", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }
    
    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} �̂��߂̃e�X�g�E���\�b�h�B
     * 
     * 07: �R���t�B�O�̐ݒ�l(javelin.from : ����[�S�p�������܂�])
     */
    public void testCreateMailMessage_config07()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config07",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("���[�����M��");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            Address[] fmAddrs = msg.getFrom();
            Address[] toAddrs = msg.getRecipients(Message.RecipientType.TO);
            Address[] rtAddrs = msg.getReplyTo();
            
            assertEquals(1, fmAddrs.length);
            assertEquals("���[�����M��", fmAddrs[0].toString());
            assertEquals(1, toAddrs.length);
            assertEquals("javelin-test-to@smg.co.jp", toAddrs[0].toString());
            assertEquals(1, rtAddrs.length);
            assertEquals("���[�����M��", rtAddrs[0].toString());
            assertEquals("Javelin alart", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }
    
    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} �̂��߂̃e�X�g�E���\�b�h�B
     * 
     * 08: �R���t�B�O�̐ݒ�l(javelin.from : ������[�p�����[�^�w�薳��])
     */
    public void testCreateMailMessage_config08()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config08",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom(null);
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            listener.createMailMessage(config_, invocation);
            
            fail("test expected to occur an exception.");
        }
        catch (MessagingException exception)
        {
        }
        catch (Exception exception)
        {
            fail("test expected to occur a MessagingException. but acturally " + exception.getClass().getName());
        }
    }
    
    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 09: �R���t�B�O�̐ݒ�l(javelin.from : �ُ�[��̃p�����[�^])
     */
    public void testCreateMailMessage_config09()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config09",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            listener.createMailMessage(config_, invocation);
            
            fail("test expected to occur an exception.");
        }
        catch (MessagingException exception)
        {
        }
        catch (Exception exception)
        {
            fail("test expected to occur a MessagingException. but acturally " + exception.getClass().getName());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 11: �R���t�B�O�̐ݒ�l(javelin.to : ����[�P��:@����])
     */
    public void testCreateMailMessage_config11()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config11",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            Address[] fmAddrs = msg.getFrom();
            Address[] toAddrs = msg.getRecipients(Message.RecipientType.TO);
            Address[] rtAddrs = msg.getReplyTo();
            
            assertEquals(1, fmAddrs.length);
            assertEquals("javelin-test-fm@smg.co.jp", fmAddrs[0].toString());
            assertEquals(1, toAddrs.length);
            assertEquals("javelin-test-to", toAddrs[0].toString());
            assertEquals(1, rtAddrs.length);
            assertEquals("javelin-test-fm@smg.co.jp", rtAddrs[0].toString());
            assertEquals("Javelin alart", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 12: �R���t�B�O�̐ݒ�l(javelin.to : ����[����:�S��OK])
     */
    public void testCreateMailMessage_config12()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config12",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to1@smg.co.jp",
                                             "javelin-test-to2@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            Address[] fmAddrs = msg.getFrom();
            Address[] toAddrs = msg.getRecipients(Message.RecipientType.TO);
            Address[] rtAddrs = msg.getReplyTo();
            
            assertEquals(1, fmAddrs.length);
            assertEquals("javelin-test-fm@smg.co.jp", fmAddrs[0].toString());
            assertEquals(2, toAddrs.length);
            assertEquals("javelin-test-to1@smg.co.jp", toAddrs[0].toString());
            assertEquals("javelin-test-to2@smg.co.jp", toAddrs[1].toString());
            assertEquals(1, rtAddrs.length);
            assertEquals("javelin-test-fm@smg.co.jp", rtAddrs[0].toString());
            assertEquals("Javelin alart", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 13: �R���t�B�O�̐ݒ�l(javelin.to : ����[�S�p�������܂�])
     */
    public void testCreateMailMessage_config13()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config13",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "���[�����M��@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            Address[] fmAddrs = msg.getFrom();
            Address[] toAddrs = msg.getRecipients(Message.RecipientType.TO);
            Address[] rtAddrs = msg.getReplyTo();
            
            assertEquals(1, fmAddrs.length);
            assertEquals("javelin-test-fm@smg.co.jp", fmAddrs[0].toString());
            assertEquals(1, toAddrs.length);
            assertEquals("���[�����M��@smg.co.jp", toAddrs[0].toString());
            assertEquals(1, rtAddrs.length);
            assertEquals("javelin-test-fm@smg.co.jp", rtAddrs[0].toString());
            assertEquals("Javelin alart", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 14: �R���t�B�O�̐ݒ�l(javelin.to : ������[�p�����[�^�w�薳��])
     */
    public void testCreateMailMessage_config14()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config14",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(null);
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            listener.createMailMessage(config_, invocation);
            
            fail("test expected to occur an exception.");
        }
        catch (MessagingException exception)
        {
        }
        catch (Exception exception)
        {
            fail("test expected to occur a MessagingException. but acturally " + exception.getClass().getName());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 15: �R���t�B�O�̐ݒ�l(javelin.to : �ُ�[��̃p�����[�^])
     */
    public void testCreateMailMessage_config15()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config15",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            listener.createMailMessage(config_, invocation);
            
            fail("test expected to occur an exception.");
        }
        catch (MessagingException exception)
        {
        }
        catch (Exception exception)
        {
            fail("test expected to occur a MessagingException. but acturally " + exception.getClass().getName());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 19: �R���t�B�O�̐ݒ�l(javelin.template : ����[�u���p�����[�^����])
     */
    public void testCreateMailMessage_config19()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config19",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailSubject("[javelin] testCreateMailMessage_config19");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_no-param.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("[javelin] testCreateMailMessage_config19", msg.getSubject());
            String content = (String) msg.getContent();
            assertEquals("this is a test message." + LS, content);
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 20: �R���t�B�O�̐ݒ�l(javelin.template : ����[�u���p�����[�^�L��])
     */
    public void testCreateMailMessage_config20()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config20",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailSubject("[javelin] testCreateMailMessage_config20");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_param.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("[javelin] testCreateMailMessage_config20", msg.getSubject());
            String content = (String) msg.getContent();
            assertEquals("this is a test message." + LS + "ProcessName=junit" + LS, content);
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 21: �R���t�B�O�̐ݒ�l(javelin.template : ����[Subject�ɒu���p�����[�^�L��])
     */
    public void testCreateMailMessage_config21()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config21",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailSubject("[javelin] ${methodName}");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_param.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("[javelin] testCreateMailMessage_config21", msg.getSubject());
            String content = (String) msg.getContent();
            assertEquals("this is a test message." + LS + "ProcessName=junit" + LS, content);
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 22: �R���t�B�O�̐ݒ�l(javelin.template : ������[�p�����[�^�w�薳��])
     */
    public void testCreateMailMessage_config22()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config22",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailSubject("[javelin] testCreateMailMessage_config22");
            config_.setMailTemplate(null);
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("Javelin alart", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 23: �R���t�B�O�̐ݒ�l(javelin.template : ������[��̃p�����[�^])
     */
    public void testCreateMailMessage_config23()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config23",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailSubject("[javelin] testCreateMailMessage_config23");
            config_.setMailTemplate("");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("Javelin alart", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 24: �R���t�B�O�̐ݒ�l(javelin.template : ������[���݂��Ȃ��t�@�C��])
     */
    public void testCreateMailMessage_config24()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config24",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailSubject("[javelin] testCreateMailMessage_config24");
            config_.setMailTemplate("a_file_in_nowhere.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("Javelin alart", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 25: �R���t�B�O�̐ݒ�l(javelin.template : ������[��̃t�@�C��])
     */
    public void testCreateMailMessage_config25()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config25",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailSubject("[javelin] testCreateMailMessage_config25");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_empty.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("[javelin] testCreateMailMessage_config25", msg.getSubject());
            String content = (String) msg.getContent();
            assertEquals("", content);
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 27: �R���t�B�O�̐ݒ�l(javelin.subject : ����[���p�L�����܂�])
     */
    public void testCreateMailMessage_config27()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config27",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailSubject("[javelin] !\"#$%&'()-=^~\\|@`[{}];+:*,<>. testCreateMailMessage_config27");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_empty.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("[javelin] !\"#$%&'()-=^~\\|@`[{}];+:*,<>. testCreateMailMessage_config27", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 28: �R���t�B�O�̐ݒ�l(javelin.subject : ����[�S�p�������܂�])
     */
    public void testCreateMailMessage_config28()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config28",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailSubject("[javelin] �e�X�g testCreateMailMessage_config28");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_empty.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("[javelin] �e�X�g testCreateMailMessage_config28", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 29: �R���t�B�O�̐ݒ�l(javelin.subject : ������[��̃p�����[�^])
     */
    public void testCreateMailMessage_config29()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config29",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailSubject("");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_empty.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 30: �R���t�B�O�̐ݒ�l(javelin.subject : ������[�p�����[�^�w�薳��])
     */
    public void testCreateMailMessage_config30()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config30",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailSubject(null);
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_empty.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("", msg.getSubject());
        }
        catch (MessagingException exception)
        {
        }
        catch (Exception exception)
        {
            fail("test expected to occur a MessagingException. but acturally " + exception.getClass().getName());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 31: �R���t�B�O�̐ݒ�l(javelin.encoding : ����)
     */
    public void testCreateMailMessage_config31()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config31",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("EUC-JP");
            config_.setMailSubject("[javelin] �e�X�g testCreateMailMessage_config31");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_eucjp.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("[javelin] �e�X�g testCreateMailMessage_config31", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 32: �R���t�B�O�̐ݒ�l(javelin.encoding : �ُ�[�p�����[�^�w�薳��])
     */
    public void testCreateMailMessage_config32()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config32",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding(null);
            config_.setMailSubject("[javelin] �e�X�g�|�` testCreateMailMessage_config32");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_empty.txt");
            
            SMTPListener listener = new SMTPListener();
            listener.createMailMessage(config_, invocation);
            
            fail("test expected to occur an exception.");
        }
        catch (MessagingException exception)
        {
        }
        catch (Exception exception)
        {
            fail("test expected to occur a MessagingException. but acturally " + exception.getClass().getName());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 33: �R���t�B�O�̐ݒ�l(javelin.encoding : �ُ�[��̃p�����[�^])
     */
    public void testCreateMailMessage_config33()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config33",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("");
            config_.setMailSubject("[javelin] �e�X�g�|�` testCreateMailMessage_config28");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_empty.txt");
            
            SMTPListener listener = new SMTPListener();
            listener.createMailMessage(config_, invocation);
            
            fail("test expected to occur an exception.");
        }
        catch (MessagingException exception)
        {
        }
        catch (Exception exception)
        {
            fail("test expected to occur a MessagingException. but acturally " + exception.getClass().getName());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 34: �R���t�B�O�̐ݒ�l(javelin.encoding : �ُ�[���݂��Ȃ��G���R�[�f�B���O��])
     */
    public void testCreateMailMessage_config34()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_config34",
                                                   1, 2, 3, 4);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("unknown");
            config_.setMailSubject("[javelin] �e�X�g�|�` testCreateMailMessage_config28");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_empty.txt");
            
            SMTPListener listener = new SMTPListener();
            listener.createMailMessage(config_, invocation);
            
            fail("test expected to occur an exception.");
        }
        catch (MessagingException exception)
        {
        }
        catch (Exception exception)
        {
            fail("test expected to occur a MessagingException. but acturally " + exception.getClass().getName());
        }

    }

    //------------------------------------------------------------
    // createMailMessage() : Invocation�p�����[�^

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 01: Invocation�p�����[�^(ProcessName : ����)
     */
    public void testCreateMailMessage_invocation01()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_invocation01",
                                                   1, 2, 3, 4);
            
            invocation.addInterval(10);
            invocation.addThrowable(new Exception());
            invocation.addCaller(invocation);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_invocation01.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            String content = (String) msg.getContent();
            assertEquals("ProcessName=junit" + LS
                         + "ClassName=" + SMTPListenerTest.class.getName() + LS
                         + "MethodName=testCreateMailMessage_invocation01" + LS
                         + "Count=1" + LS
                         + "Minimum=10" + LS
                         + "Maximum=10" + LS
                         + "Average=10" + LS
                         + "ThrowableCount=1" + LS
                         + "Caller={org.seasar.javelin.communicate.smtp.SMTPListenerTest#testCreateMailMessage_invocation01" + LS
                         + "}" + LS
                         + "RecordThreshold=3" + LS
                         + "AlarmThreshold=4" + LS
                         , content);
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 02: Invocation�p�����[�^(ProcessName/ClassName/MethodName : ������[�󕶎���])
     */
    public void testCreateMailMessage_invocation02()
    {
        try
        {
            Invocation invocation = new Invocation("",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   "",
                                                   "",
                                                   1, 2, 300, 400);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_invocation02.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            String content = (String) msg.getContent();
            assertEquals("ProcessName=" + LS
                         + "ClassName=" + LS
                         + "MethodName=" + LS
                         + "Count=0" + LS
                         + "Minimum=-1" + LS
                         + "Maximum=-1" + LS
                         + "Average=0" + LS
                         + "ThrowableCount=0" + LS
                         + "Caller={" + LS
                         + "}" + LS
                         + "RecordThreshold=300" + LS
                         + "AlarmThreshold=400" + LS
                         , content);
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 03: Invocation�p�����[�^(ProcessName : ������[null])
     */
    public void testCreateMailMessage_invocation03()
    {
        try
        {
            Invocation invocation = new Invocation(null,
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   null,
                                                   null,
                                                   1, 2, 300, 400);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_invocation03.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            String content = (String) msg.getContent();
            assertEquals("ProcessName=null" + LS
                         + "ClassName=null" + LS
                         + "MethodName=null" + LS
                         + "Count=0" + LS
                         + "Minimum=-1" + LS
                         + "Maximum=-1" + LS
                         + "Average=0" + LS
                         + "ThrowableCount=0" + LS
                         + "Caller={" + LS
                         + "}" + LS
                         + "RecordThreshold=300" + LS
                         + "AlarmThreshold=400" + LS
                         , content);
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 13: Invocation�p�����[�^(Average : ����[Interval 2��])
     */
    public void testCreateMailMessage_invocation13()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_invocation13",
                                                   100, 200, 300, 400);
            
            invocation.addInterval(10);
            invocation.addInterval(20);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_invocation13.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            String content = (String) msg.getContent();
            assertEquals("ProcessName=junit" + LS
                         + "ClassName=" + SMTPListenerTest.class.getName() + LS
                         + "MethodName=testCreateMailMessage_invocation13" + LS
                         + "Count=2" + LS
                         + "Minimum=10" + LS
                         + "Maximum=20" + LS
                         + "Average=15" + LS
                         + "ThrowableCount=0" + LS
                         + "Caller={" + LS
                         + "}" + LS
                         + "RecordThreshold=300" + LS
                         + "AlarmThreshold=400" + LS
                         , content);
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 16: Invocation�p�����[�^(ThrowableCount : ����[Throwable 2��])
     */
    public void testCreateMailMessage_invocation16()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_invocation16",
                                                   100, 200, 300, 400);
            
            invocation.addThrowable(new Exception("No.1"));
            invocation.addThrowable(new Exception("No.2"));
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_invocation16.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            String content = (String) msg.getContent();
            assertEquals("ProcessName=junit" + LS
                         + "ClassName=" + SMTPListenerTest.class.getName() + LS
                         + "MethodName=testCreateMailMessage_invocation16" + LS
                         + "Count=0" + LS
                         + "Minimum=-1" + LS
                         + "Maximum=-1" + LS
                         + "Average=0" + LS
                         + "ThrowableCount=2" + LS
                         + "Caller={" + LS
                         + "}" + LS
                         + "RecordThreshold=300" + LS
                         + "AlarmThreshold=400" + LS
                         , content);
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 19: Invocation�p�����[�^(AllCallerInvocation : ����[Caller 2��])
     */
    public void testCreateMailMessage_invocation19()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_invocation19",
                                                   100, 200, 300, 400);
            
            Invocation a_invocation1 =
                new Invocation("junit",
                               new ObjectName("org.seasar.javelin:type=objName"),
                               new ObjectName("org.seasar.javelin:type=classObjName"),
                               SMTPListenerTest.class.getName(),
                               "testCreateMailMessage_invocation19_1",
                               100, 200, 300, 400);
            Invocation a_invocation2 =
                new Invocation("junit",
                               new ObjectName("org.seasar.javelin:type=objName"),
                               new ObjectName("org.seasar.javelin:type=classObjName"),
                               SMTPListenerTest.class.getName(),
                               "testCreateMailMessage_invocation19_2",
                               100, 200, 300, 400);
            
            invocation.addCaller(a_invocation1);
            invocation.addCaller(a_invocation2);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_invocation19.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            String content = (String) msg.getContent();
            assertEquals("ProcessName=junit" + LS
                         + "ClassName=" + SMTPListenerTest.class.getName() + LS
                         + "MethodName=testCreateMailMessage_invocation19" + LS
                         + "Count=0" + LS
                         + "Minimum=-1" + LS
                         + "Maximum=-1" + LS
                         + "Average=0" + LS
                         + "ThrowableCount=0" + LS
                         + "Caller={org.seasar.javelin.communicate.smtp.SMTPListenerTest#testCreateMailMessage_invocation19_2," + LS
                         + "org.seasar.javelin.communicate.smtp.SMTPListenerTest#testCreateMailMessage_invocation19_1" + LS
                         + "}" + LS
                         + "RecordThreshold=300" + LS
                         + "AlarmThreshold=400" + LS
                         , content);
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 20: Invocation�p�����[�^(AllCallerInvocation : ������[Invocation�̃N���X���܂��̓��\�b�h����null])
     */
    public void testCreateMailMessage_invocation20()
    {
        try
        {
            Invocation invocation = new Invocation("junit",
                                                   new ObjectName("org.seasar.javelin:type=objName"),
                                                   new ObjectName("org.seasar.javelin:type=classObjName"),
                                                   SMTPListenerTest.class.getName(),
                                                   "testCreateMailMessage_invocation20",
                                                   100, 200, 300, 400);
            
            Invocation a_invocation1 =
                new Invocation("junit",
                               new ObjectName("org.seasar.javelin:type=objName"),
                               new ObjectName("org.seasar.javelin:type=classObjName"),
                               null,
                               "testCreateMailMessage_invocation20_1",
                               100, 200, 300, 400);
            Invocation a_invocation2 =
                new Invocation("junit",
                               new ObjectName("org.seasar.javelin:type=objName"),
                               new ObjectName("org.seasar.javelin:type=classObjName"),
                               SMTPListenerTest.class.getName(),
                               null,
                               100, 200, 300, 400);
            
            invocation.addCaller(a_invocation1);
            invocation.addCaller(a_invocation2);
            
            config_.setSmtpServer("castor.smg.co.jp");
            config_.setMailFrom("javelin-test-fm@smg.co.jp");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_invocation20.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            String content = (String) msg.getContent();
            assertEquals("ProcessName=junit" + LS
                         + "ClassName=" + SMTPListenerTest.class.getName() + LS
                         + "MethodName=testCreateMailMessage_invocation20" + LS
                         + "Count=0" + LS
                         + "Minimum=-1" + LS
                         + "Maximum=-1" + LS
                         + "Average=0" + LS
                         + "ThrowableCount=0" + LS
                         + "Caller={"
                         + "null#testCreateMailMessage_invocation20_1," + LS
                         + "org.seasar.javelin.communicate.smtp.SMTPListenerTest#null" + LS
                         + "}" + LS
                         + "RecordThreshold=300" + LS
                         + "AlarmThreshold=400" + LS
                         , content);
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }
}
