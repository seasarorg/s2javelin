package org.seasar.javelin;

import java.io.IOException;
import java.io.PrintStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * S2JavelinFilter
 * 
 * @author eriguchi
 * 
 */
public class S2JavelinFilter implements Filter
{

    /** �����̐�. */
    private static final int    ARGS_NUM                = 6;

    /** �h���C�� */
    private static final String PNAME_DOMAIN            = "domain";

    /** ���������ɒ~�ς���(���\�b�h����)�Ăяo�����̌��� */
    private static final String PNAME_INTERVAL_MAX      = "intervalMax";

    /** ���������ɒ~�ς���(���\�b�h����)��O�������̌��� */
    private static final String PNAME_THROWABLE_MAX     = "throwableMax";

    /** �t�@�C���o�͂��s���^�[���E�A���E���h�E�^�C����臒l(�P��:�~���b) */
    private static final String PNAME_RECORD_THRESHOLD  = "recordThreshold";

    /** BottleneckEye�ւ̒ʒm���s��CPU�^�C����臒l(�P��:�~���b) */
    private static final String PNAME_ALARM_THRESHOLD   = "alarmThreshold";

    /** jvn�t�@�C�����o�͂���f�B���N�g���ւ̃p�X�B */
    private static final String PNAME_JAVELIN_DIR       = "javelinFileDir";

    /** ���\�b�h�Ăяo���̈��������o�͂���B */
    private static final String PNAME_IS_LOG_ARGS       = "log.args";

    /** ���\�b�h�Ăяo���̖߂�l�����o�͂���B */
    private static final String PNAME_IS_LOG_RETURN     = "log.return";

    /** ���\�b�h�Ăяo���̎��̃X�^�b�N�g���[�X���o�͂���B */
    private static final String PNAME_IS_LOG_STACKTRACE = "log.stacktrace";

    /** ���\�b�h�Ăяo���̃��[�g�m�[�h�ɂ��閼�O�B */
    private static final String PNAME_ROOT_CALLER_NAME  = "rootCallerName";

    /** ���\�b�h�Ăяo���̃G���h�m�[�h�̖��O������ł��Ȃ��ꍇ�ɂ��閼�O�B */
    private static final String PNAME_END_CALLEE_NAME   = "endCalleeName";

    /**
     * �X���b�h�̖��̂̌�����@ 0:�X���b�h��@�X���b�hID(�X���b�h�N���X��@�I�u�W�F�N�gID) 1:�X���b�hID 2:�X���b�h��
     * 3:���\�b�h���iServlet�t�B���^�g�ݍ��ݎ��ɂ̓T�[�u���b�g�p�X�ƂȂ�B�j
     */
    private static final String PNAME_THREAD_MODEL      = "threadModel";

    /** S2Javelin�̐ݒ�l */
    private S2JavelinConfig     config_;

    /**
     * �������B
     * 
     * @param config web.xml�̐ݒ�
     * @throws ServletException
     */
    public void init(FilterConfig config)
    {
        this.config_ = new S2JavelinConfig();

        String domain = getInitParameter(config, PNAME_DOMAIN);
        if (this.config_.isSetDomain() == false && domain != null && domain.trim().length() > 0)
        {
            this.config_.setDomain(domain);
        }

        // web.xml�����ݒ�t�@�C���i*.conf�j�̕���D�悷��
        if (this.config_.isSetAlarmThreshold() == false
                && isParameterIntegerValue(config, PNAME_INTERVAL_MAX))
        {
            this.config_.setIntervalMax(getInitParameterInteger(config, PNAME_INTERVAL_MAX));
        }
        if (this.config_.isSetThrowableMax() == false
                && isParameterIntegerValue(config, PNAME_THROWABLE_MAX))
        {
            this.config_.setThrowableMax(getInitParameterInteger(config, PNAME_THROWABLE_MAX));
        }
        if (this.config_.isSetRecordThreshold() == false
                && isParameterIntegerValue(config, PNAME_RECORD_THRESHOLD))
        {
            this.config_.setRecordThreshold(getInitParameterInteger(config, PNAME_RECORD_THRESHOLD));
        }
        if (this.config_.isSetAlarmThreshold() == false
                && isParameterIntegerValue(config, PNAME_ALARM_THRESHOLD))
        {
            this.config_.setAlarmThreshold(getInitParameterInteger(config, PNAME_ALARM_THRESHOLD));
        }
        if (this.config_.isSetJavelinFileDir() == false
                && isParameterStringValue(config, PNAME_JAVELIN_DIR))
        {
            String javelinFileDir = getInitParameter(config, PNAME_JAVELIN_DIR);
            if (javelinFileDir.trim().length() > 0)
            {
                this.config_.setJavelinFileDir(javelinFileDir);
            }
        }
        if (this.config_.isSetLogArgs() == false
                && isParameterBooleanValue(config, PNAME_IS_LOG_ARGS))
        {
            this.config_.setLogArgs(getInitParameterBoolean(config, PNAME_IS_LOG_ARGS));
        }
        if (this.config_.isSetLogReturn() == false
                && isParameterBooleanValue(config, PNAME_IS_LOG_RETURN))
        {
            this.config_.setLogReturn(getInitParameterBoolean(config, PNAME_IS_LOG_RETURN));
        }
        if (this.config_.isSetLogStacktrace() == false
                && isParameterBooleanValue(config, PNAME_IS_LOG_STACKTRACE))
        {
            this.config_.setLogStacktrace(getInitParameterBoolean(config, PNAME_IS_LOG_STACKTRACE));
        }
        if (this.config_.isSetRootCallerName() == false
                && isParameterBooleanValue(config, PNAME_ROOT_CALLER_NAME))
        {
            this.config_.setRootCallerName(getInitParameter(config, PNAME_ROOT_CALLER_NAME));
        }
        if (this.config_.isSetEndCalleeName() == false
                && isParameterBooleanValue(config, PNAME_END_CALLEE_NAME))
        {
            this.config_.setEndCalleeName(getInitParameter(config, PNAME_END_CALLEE_NAME));
        }
        if (this.config_.isSetThreadModel() == false
                && isParameterIntegerValue(config, PNAME_THREAD_MODEL))
        {
            this.config_.setThreadModel(getInitParameterInteger(config, PNAME_THREAD_MODEL));
        }

        synchronized (S2StatsJavelinRecorder.class)
        {
            S2StatsJavelinRecorder.javelinInit(this.config_);
        }

        printConfigValue();
    }

    /**
     * �w�肳�ꂽ�I�v�V�����̒l�������񂩂ǂ����𒲂ׂ�B
     * 
     * @param config �t�B���^�ݒ�
     * @param name �I�v�V������
     * @return �l��������Ȃ�true
     */
    private boolean isParameterStringValue(FilterConfig config, String name)
    {
        String configValue = config.getInitParameter(name);
        return (configValue != null);
    }

    /**
     * �w�肳�ꂽ�I�v�V�����̒l�����l���ǂ����𒲂ׂ�B
     * 
     * @param config �t�B���^�ݒ�
     * @param name �I�v�V������
     * @return �l�����l�Ȃ�true
     */
    private boolean isParameterIntegerValue(FilterConfig config, String name)
    {
        String configValue = config.getInitParameter(name);
        if (configValue == null)
        {
            return false;
        }
        try
        {
            Integer.parseInt(configValue);
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
        return true;
    }

    /**
     * �w�肳�ꂽ�I�v�V�����̒l���u�[���l���ǂ����𒲂ׂ�B
     * 
     * @param config �t�B���^�ݒ�
     * @param name �I�v�V������
     * @return �l���u�[���l�Ȃ�true
     */
    private boolean isParameterBooleanValue(FilterConfig config, String name)
    {
        String configValue = config.getInitParameter(name);
        // 
        if (configValue != null)
        {
            return true;
        }
        return false;
    }

    /**
     * �p�����[�^���擾���Aint�^�ɂ��ĕԂ��B
     * @param config Javelin�̐ݒ�l
     * @param pname �p�����[�^��
     * @return �p�����[�^
     */
    private int getInitParameterInteger(FilterConfig config, String pname)
    {
        String configValue = config.getInitParameter(pname);
        return Integer.parseInt(configValue);
    }

    /**
     * �p�����[�^���擾���Aboolean�^�ɂ��ĕԂ��B
     * @param config Javelin�̐ݒ�l
     * @param pname �p�����[�^��
     * @return �p�����[�^
     */
    private boolean getInitParameterBoolean(FilterConfig config, String pname)
    {
        String configValue = config.getInitParameter(pname);
        return Boolean.valueOf(configValue);
    }

    /**
     * �T�[�u���b�g�t�B���^�̐ݒ肩��A�w�肳�ꂽ�p�����[�^�̒l���擾����B
     * 
     * @param config �T�[�u���b�g�t�B���^�̐ݒ�B
     * @param pname �擾�������p�����[�^�̖��́B
     * @return �p�����[�^�̒l�B
     */
    private String getInitParameter(FilterConfig config, String pname)
    {
        String value = config.getInitParameter(pname);
        return value;
    }

    /**
     * �t�B���^�����O�����B �T�[�u���b�g�̏������Ԃ��v�����A�e�폈�����s���B
     * 
     * @param request ���N�G�X�g
     * @param response ���X�|���X
     * @param chain �t�B���^�[�`�F�C��
     * @throws IOException ���o�͗�O
     * @throws ServletException �T�[�u���b�g��O
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException,
            ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse)response;

        try
        {
            // ThreadLocal����擾����CallTree�ɑ΂��ČĂяo���\����ۑ�����B
            CallTree callTree = S2JavelinCommonPool.getCallTree();
            if (callTree != null)
            {
                S2StatsJavelinRecorder.initCallTree(callTree);
            }

            /**
             * ThreadLocal����N���X���A���\�b�h�����Ăяo���B �N���X���A���\�b�h�������݂��Ȃ��ꍇ�A Http
             * Request�̃R���e�L�X�g�p�X�A�T�[�u���b�g�p�X���Ăяo���B
             */
            String contextPath = S2JavelinCommonPool.getClassName();
            String servletPath = S2JavelinCommonPool.getMethodName();
            if (contextPath == null || servletPath == null)
            {
                servletPath = httpRequest.getPathInfo();
                if (servletPath == null)
                {
                    contextPath = httpRequest.getContextPath();
                    servletPath = httpRequest.getServletPath();
                }
                else
                {
                    contextPath = httpRequest.getContextPath() + httpRequest.getServletPath();
                }

                contextPath = httpRequest.getContextPath();
                servletPath = httpRequest.getServletPath();
            }

            Object[] args = null;
            if (this.config_.isLogArgs())
            {
                args = new Object[ARGS_NUM];
                args[0] = httpRequest.getRemoteHost();
                args[1] = httpRequest.getRemotePort();
                args[2] = contextPath;
                args[3] = servletPath;
                args[4] = httpRequest.getMethod();
                args[5] = httpRequest.getParameterMap();
            }

            StackTraceElement[] stacktrace = null;
            if (this.config_.isLogStacktrace())
            {
                stacktrace = Thread.currentThread().getStackTrace();
            }

            S2StatsJavelinRecorder.preProcess(contextPath, servletPath, args, stacktrace,
                                              this.config_);
            // S2StatsJavelinRecorder.preProcessField(contextPath, servletPath,
            // config_);
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }

        try
        {
            // ==================================================
            // ���\�b�h�Ăяo���B
            chain.doFilter(request, response);
            // ==================================================
        }
        catch (IOException ex)
        {
            // S2StatsJavelinRecorder.postProcessField(this.config_);
            S2StatsJavelinRecorder.postProcess(ex, this.config_);
            throw ex;
        }
        catch (ServletException ex)
        {
            // S2StatsJavelinRecorder.postProcessField(this.config_);
            S2StatsJavelinRecorder.postProcess(ex, this.config_);
            throw ex;
        }
        catch (RuntimeException ex)
        {
            // S2StatsJavelinRecorder.postProcessField(this.config_);
            S2StatsJavelinRecorder.postProcess(ex, this.config_);
            throw ex;
        }
        catch (Error error)
        {
            // S2StatsJavelinRecorder.postProcessField(this.config_);
            S2StatsJavelinRecorder.postProcess(error, this.config_);
            throw error;
        }

        try
        {
            Object returnValue = null;
            if (this.config_.isLogReturn())
            {
                returnValue = createReturnValue(httpResponse);
            }

            // S2StatsJavelinRecorder.postProcessField(this.config_);
            S2StatsJavelinRecorder.postProcess(returnValue, this.config_);
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }
    }

    /**
     * HttpServletResponse����A�߂�l�𐶐�����B
     * 
     * @param response �T�[�u���b�g���X�|���X�B
     * @return �߂�l�B
     */
    private Object createReturnValue(HttpServletResponse response)
    {
        String returnValue;
        returnValue = response.getContentType();
        return returnValue;
    }

    /**
     * {non-javadoc}
     */
    public void destroy()
    {
        // �������Ȃ�
    }

    /**
     * �ݒ�l��W���o�͂ɏo�͂���B
     */
    private void printConfigValue()
    {
        PrintStream out = System.out;
        out.println(">>>> Properties related with S2JavelinFilter");
        out.println("\tjavelin.intervalMax               : " + this.config_.getIntervalMax());
        out.println("\tjavelin.throwableMax              : " + this.config_.getThrowableMax());
        out.println("\tjavelin.statisticsThreshold       : " + this.config_.getStatisticsThreshold());
        out.println("\tjavelin.recordThreshold           : " + this.config_.getRecordThreshold());
        out.println("\tjavelin.alarmThreshold            : " + this.config_.getAlarmThreshold());
        out.println("\tjavelin.javelinFileDir            : " + this.config_.getJavelinFileDir());
        out.println("\tjavelin.domain                    : " + this.config_.getDomain());
        out.println("\tjavelin.recordException           : " + this.config_.isRecordException());
        out.println("\tjavelin.alarmException            : " + this.config_.isAlarmException());
        out.println("\tjavelin.log.stacktrace            : " + this.config_.isLogStacktrace());
        out.println("\tjavelin.log.args                  : " + this.config_.isLogArgs());
        out.println("\tjavelin.log.jmxinfo               : " + this.config_.isLogMBeanInfo());
        out.println("\tjavelin.log.jmxinfo.root          : " + this.config_.isLogMBeanInfoRoot());
        out.println("\tjavelin.log.return                : " + this.config_.isLogReturn());
        out.println("\tjavelin.log.return.detail         : " + this.config_.isReturnDetail());
        out.println("\tjavelin.log.return.detail.depth   : " + this.config_.getReturnDetailDepth());
        out.println("\tjavelin.log.args.detail           : " + this.config_.isArgsDetail());
        out.println("\tjavelin.log.args.detail.depth     : " + this.config_.getArgsDetailDepth());
        out.println("\tjavelin.rootCallerName            : " + this.config_.getRootCallerName());
        out.println("\tjavelin.endCalleeName             : " + this.config_.getEndCalleeName());
        out.println("\tjavelin.threadModel               : " + this.config_.getThreadModel());
        out.println("\tjavelin.acceptPort                : " + this.config_.getAcceptPort());
        out.println("\tjavelin.stringLimitLength         : " + this.config_.getStringLimitLength());
        out.println("\tjavelin.system.log                : " + this.config_.getSystemLog());
        out.println("\tjavelin.log.jvn.max               : " + this.config_.getLogJvnMax());
        out.println("\tjavelin.log.zip.max               : " + this.config_.getLogZipMax());
        out.println("\tjavelin.system.log.num.max        : " + this.config_.getSystemLogNumMax());
        out.println("\tjavelin.system.log.size.max       : " + this.config_.getSystemLogSizeMax());
        out.println("\tjavelin.system.log.level          : " + this.config_.getSystemLogLevel());
        out.println("\tjavelin.call.tree.max             : " + this.config_.getCallTreeMax());
        out.println("\tjavelin.record.jmx                : " + this.config_.isRecordJMX());
        out.println("\tjavelin.recordStrategy            : " + this.config_.getRecordStrategy());
        out.println("\tjavelin.alarmListeners            : " + this.config_.getAlarmListeners());
        out.println("\tjavelin.telegramListeners         : " + this.config_.getTelegramListeners());
        out.println("\tjavelin.serializeFile             : " + this.config_.getSerializeFile());
        out.println("\tjavelin.record.invocation.num.max : " + this.config_.getRecordInvocationMax());
        out.println("<<<<");
    }
}
