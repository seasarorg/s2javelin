package org.seasar.javelin.communicate.smtp;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.management.ObjectName;

import junit.framework.TestCase;

import org.seasar.javelin.bean.Invocation;

/**
 * SMTPListenerクラスのテストケース。<br/>
 * ※このテストケースを実行するとメールが送信されます。
 * 
 * @author AKIBA Makoto
 */
public class SMTPListenerTest extends TestCase
{
    /** 注：実行前にメールアドレスを適切なアドレスに変更してください。 */
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
        
        // SMTPConfigの初期化(ファイルの読み込み)
        System.setProperty("javelin.property", "conf/javelin.properties");
        config_ = new SMTPConfig();
        config_.isSendMail();
    }
    
    //------------------------------------------------------------
    // sendExceedThresholdAlarm()
    
    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)} のためのテスト・メソッド。
     * 
     * 01: Invocationがnull
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
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)} のためのテスト・メソッド。
     * 
     * 02: コンフィグの設定値(javelin.sendMail : false)
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
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)} のためのテスト・メソッド。
     * 
     * 03: コンフィグの設定値(javelin.sendMail : true)
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
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} のためのテスト・メソッド。
     * 
     * 01: コンフィグの設定値(javelin.server : 正常)
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
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} のためのテスト・メソッド。
     * 
     * 02: コンフィグの設定値(javelin.server : 異常[パラメータ指定無し])
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
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} のためのテスト・メソッド。
     * 
     * 03: コンフィグの設定値(javelin.server : 異常[空のパラメータ])
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
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} のためのテスト・メソッド。
     * 
     * 04: コンフィグの設定値(javelin.server : 異常[存在しないサーバ])
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
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} のためのテスト・メソッド。
     * 
     * 06: コンフィグの設定値(javelin.from : 正常[@無し])
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
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} のためのテスト・メソッド。
     * 
     * 07: コンフィグの設定値(javelin.from : 正常[全角文字を含む])
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
            config_.setMailFrom("メール送信元");
            config_.setMailTo(new String[] { "javelin-test-to@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            Address[] fmAddrs = msg.getFrom();
            Address[] toAddrs = msg.getRecipients(Message.RecipientType.TO);
            Address[] rtAddrs = msg.getReplyTo();
            
            assertEquals(1, fmAddrs.length);
            assertEquals("メール送信元", fmAddrs[0].toString());
            assertEquals(1, toAddrs.length);
            assertEquals("javelin-test-to@smg.co.jp", toAddrs[0].toString());
            assertEquals(1, rtAddrs.length);
            assertEquals("メール送信元", rtAddrs[0].toString());
            assertEquals("Javelin alart", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }
    
    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)} のためのテスト・メソッド。
     * 
     * 08: コンフィグの設定値(javelin.from : 準正常[パラメータ指定無し])
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
     * 09: コンフィグの設定値(javelin.from : 異常[空のパラメータ])
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
     * 11: コンフィグの設定値(javelin.to : 正常[単一:@無し])
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
     * 12: コンフィグの設定値(javelin.to : 正常[複数:全てOK])
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
     * 13: コンフィグの設定値(javelin.to : 正常[全角文字を含む])
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
            config_.setMailTo(new String[] { "メール送信先@smg.co.jp" });
            config_.setMailEncoding("iso-2022-jp");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            Address[] fmAddrs = msg.getFrom();
            Address[] toAddrs = msg.getRecipients(Message.RecipientType.TO);
            Address[] rtAddrs = msg.getReplyTo();
            
            assertEquals(1, fmAddrs.length);
            assertEquals("javelin-test-fm@smg.co.jp", fmAddrs[0].toString());
            assertEquals(1, toAddrs.length);
            assertEquals("メール送信先@smg.co.jp", toAddrs[0].toString());
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
     * 14: コンフィグの設定値(javelin.to : 準正常[パラメータ指定無し])
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
     * 15: コンフィグの設定値(javelin.to : 異常[空のパラメータ])
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
     * 19: コンフィグの設定値(javelin.template : 正常[置換パラメータ無し])
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
     * 20: コンフィグの設定値(javelin.template : 正常[置換パラメータ有り])
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
     * 21: コンフィグの設定値(javelin.template : 正常[Subjectに置換パラメータ有り])
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
     * 22: コンフィグの設定値(javelin.template : 準正常[パラメータ指定無し])
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
     * 23: コンフィグの設定値(javelin.template : 準正常[空のパラメータ])
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
     * 24: コンフィグの設定値(javelin.template : 準正常[存在しないファイル])
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
     * 25: コンフィグの設定値(javelin.template : 準正常[空のファイル])
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
     * 27: コンフィグの設定値(javelin.subject : 正常[半角記号を含む])
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
     * 28: コンフィグの設定値(javelin.subject : 正常[全角文字を含む])
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
            config_.setMailSubject("[javelin] テスト testCreateMailMessage_config28");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_empty.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("[javelin] テスト testCreateMailMessage_config28", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 29: コンフィグの設定値(javelin.subject : 準正常[空のパラメータ])
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
     * 30: コンフィグの設定値(javelin.subject : 準正常[パラメータ指定無し])
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
     * 31: コンフィグの設定値(javelin.encoding : 正常)
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
            config_.setMailSubject("[javelin] テスト testCreateMailMessage_config31");
            config_.setMailTemplate("test/org/seasar/javelin/communicate/smtp/testCreateMailMessage_config_eucjp.txt");
            
            SMTPListener listener = new SMTPListener();
            MimeMessage msg = listener.createMailMessage(config_, invocation);
            
            assertEquals("[javelin] テスト testCreateMailMessage_config31", msg.getSubject());
        }
        catch (Exception exception)
        {
            fail(exception.toString());
        }
    }

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 32: コンフィグの設定値(javelin.encoding : 異常[パラメータ指定無し])
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
            config_.setMailSubject("[javelin] テスト－～ testCreateMailMessage_config32");
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
     * 33: コンフィグの設定値(javelin.encoding : 異常[空のパラメータ])
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
            config_.setMailSubject("[javelin] テスト－～ testCreateMailMessage_config28");
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
     * 34: コンフィグの設定値(javelin.encoding : 異常[存在しないエンコーディング名])
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
            config_.setMailSubject("[javelin] テスト－～ testCreateMailMessage_config28");
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
    // createMailMessage() : Invocationパラメータ

    /**
     * {@link org.seasar.javelin.communicate.smtp.SMTPListener#createMailMessage(org.seasar.javelin.communicate.smtp.SMTPConfig, org.seasar.javelin.bean.Invocation)}
     *
     * 01: Invocationパラメータ(ProcessName : 正常)
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
     * 02: Invocationパラメータ(ProcessName/ClassName/MethodName : 準正常[空文字列])
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
     * 03: Invocationパラメータ(ProcessName : 準正常[null])
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
     * 13: Invocationパラメータ(Average : 正常[Interval 2個])
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
     * 16: Invocationパラメータ(ThrowableCount : 正常[Throwable 2個])
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
     * 19: Invocationパラメータ(AllCallerInvocation : 正常[Caller 2個])
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
     * 20: Invocationパラメータ(AllCallerInvocation : 準正常[Invocationのクラス名またはメソッド名がnull])
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
