package org.seasar.javelin;

import org.seasar.javelin.util.JavelinConfigUtil;

/**
 * S2StatsJavelin�̐ݒ��ێ�����N���X�B
 */
public class S2JavelinConfig
{

    /** �X���b�h���f���̒l�F�X���b�hID */
    public static final int      TM_THREAD_ID                = 1;

    /** �X���b�h���f���̒l�F�X���b�h�� */
    public static final int      TM_THREAD_NAME              = 2;

    /** �X���b�h���f���̒l�F */
    public static final int      TM_CONTEXT_PATH             = 3;

    /** Javelin�n�p�����[�^�̐ړ��� */
    public static final String   JAVELIN_PREFIX              = "javelin.";

    public static final String  INTERVALMAX_KEY             = JAVELIN_PREFIX + "intervalMax";

    public static final String  THROWABLEMAX_KEY            = JAVELIN_PREFIX + "throwableMax";

    public static final String  STATISTICSTHRESHOLD_KEY     = JAVELIN_PREFIX
                                                                     + "statisticsThreshold";

    public static final String  RECORDTHRESHOLD_KEY         = JAVELIN_PREFIX + "recordThreshold";

    public static final String  ALARMTHRESHOLD_KEY          = JAVELIN_PREFIX + "alarmThreshold";

    public static final String  JAVELINFILEDIR_KEY          = JAVELIN_PREFIX + "javelinFileDir";

    public static final String  DOMAIN_KEY                  = JAVELIN_PREFIX + "domain";

    public static final String  LOG_STACKTRACE_KEY          = JAVELIN_PREFIX + "log.stacktrace";

    public static final String  LOG_ARGS_KEY                = JAVELIN_PREFIX + "log.args";

    public static final String  LOG_JMXNFO_KEY              = JAVELIN_PREFIX + "log.jmxinfo";

    public static final String  LOG_RETURN_KEY              = JAVELIN_PREFIX + "log.return";

    public static final String  ARGS_DETAIL_KEY             = JAVELIN_PREFIX + "log.args.detail";

    public static final String  RETURN_DETAIL_KEY           = JAVELIN_PREFIX + "log.return.detail";

    public static final String  ARGS_DETAIL_DEPTH_KEY       = JAVELIN_PREFIX
                                                                     + "log.args.detail.depth";

    public static final String  RETURN_DETAIL_DEPTH_KEY     = JAVELIN_PREFIX
                                                                     + "log.return.detail.depth";

    public static final String  ROOTCALLERNAME_KEY          = JAVELIN_PREFIX + "rootCallerName";

    public static final String  ENDCALLEENAME_KEY           = JAVELIN_PREFIX + "endCalleeName";

    public static final String  THREADMODEL_KEY             = JAVELIN_PREFIX + "threadModel";

    public static final String  HTTPPORT_KEY                = JAVELIN_PREFIX + "httpPort";

    public static final String  DEBUG_KEY                   = JAVELIN_PREFIX + "debug";

    /** StatsJavelin�̑҂������|�[�g�̃v���p�e�B�� */
    public static final String   ACCEPTPORT_KEY              = JAVELIN_PREFIX + "acceptPort";

    /** Javelin�̃��O�o��ON/OFF�ؑփt���O�̃v���p�e�B�� */
    public static final String  JAVELINENABLE_KEY           = JAVELIN_PREFIX + "javelinEnable";

    /** �����A�߂�l���̕����� */
    public static final String  STRINGLIMITLENGTH_KEY       = JAVELIN_PREFIX + "stringLimitLength";

    /** �G���[���O�t�@�C���̃v���p�e�B�� */
    public static final String   ERRORLOG_KEY                = JAVELIN_PREFIX + "error.log";

    /** ���p����AlarmListener�� */
    public static final String  ALARM_LISTENERS_KEY         = JAVELIN_PREFIX + "alarmListeners";

    /** JMX�ʐM�ɂ������J���s�����ǂ�����\���v���p�e�B�� */
    public static final String  RECORD_JMX_KEY              = JAVELIN_PREFIX + "record.jmx";

    /** jvn���O�t�@�C���̍ő吔��\���v���p�e�B�� */
    public static final String  LOG_JVN_MAX_KEY             = JAVELIN_PREFIX + "log.jvn.max";

    /** jvn���O�t�@�C�������k����zip�t�@�C���̍ő吔��\���v���p�e�B�� */
    public static final String  LOG_ZIP_MAX_KEY             = JAVELIN_PREFIX + "log.zip.max";

    /** �L�^��������N���X */
    public static final String  RECORDSTRATEGY_KEY         = JAVELIN_PREFIX + "recordStrategy";

    /** ���p����TelegramListener�� */
    public static final String  TELERAM_LISTENERS_KEY         = JAVELIN_PREFIX + "telegramListeners";

    private static final int     DEFAULT_INTERVALMAX         = 1000;

    private static final int     DEFAULT_THROWABLEMAX        = 1000;

    private static final long    DEFAULT_STATISTICSTHRESHOLD = 0;

    private static final long    DEFAULT_RECORDTHRESHOLD     = 0;

    private static final long    DEFAULT_ALARMTHRESHOLD      = 1000;

    private static final String  DEFAULT_JAVELINFILEDIR      = System.getProperty("java.io.tmpdir");

    private static final String  DEFAULT_DOMAIN              = "org.seasar.javelin.jmx.default";

    private static final boolean DEFAULT_LOG_STACKTRACE      = false;

    private static final boolean DEFAULT_LOG_ARGS            = true;

    private static final boolean DEFAULT_LOG_JMXINFO         = true;

    private static final boolean DEFAULT_LOG_RETURN          = true;

    private static final boolean DEFAULT_ARGS_DETAIL         = false;

    private static final boolean DEFAULT_RETURN_DETAIL       = false;

    private static final int     DEFAULT_ARGS_DETAIL_DEPTH   = 1;

    private static final int     DEFAULT_RETURN_DETAIL_DEPTH = 1;

    private static final String  DEFAULT_ROOTCALLERNAME      = "unknown";

    private static final String  DEFAULT_ENDCALLEENAME       = "unknown";

    private static final int     DEFAULT_THREADMODEL         = 1;

    private static final int     DEFAULT_HTTPPORT            = 0;

    private static final boolean DEFAULT_DEBUG               = false;

    private static final int     DEFAULT_STRINGLIMITLENGTH   = 1024;

    /** Javelin���O���o�͂��邩�ǂ����̃f�t�H���g�ݒ� */
    public static final boolean  DEFAULT_JAVELINENABLE       = false;

    /** �҂������|�[�g�ԍ��̃f�t�H���g�l */
    public static final int      DEFAULT_ACCEPTPORT          = 32000;

    /** Javelin���s�G���[���b�Z�[�W�̏o�͐�p�X�̃f�t�H���g�l */
    public static final String   DEFAULT_ERRORLOG            = "log/error.log";

    /** �f�t�H���g�ŗ��p����AlarmListener�� */
    private static final String  DEFAULT_ALARM_LISTENERS     = "org.seasar.javelin.communicate.JmxListener";

    /** �f�t�H���g��JMX�ʐM�ɂ������J���s�����ǂ��� */
    private static final boolean DEFAULT_RECORD_JMX          = true;

    /** jvn���O�t�@�C���̍ő吔�̃f�t�H���g */
    private static final int     DEFAULT_LOG_JVN_MAX         = 256;

    /** jvn���O�t�@�C�������k����zip�t�@�C���̍ő吔�̃f�t�H���g */
    private static final int     DEFAULT_LOG_ZIP_MAX         = 256;

    /** �L�^��������N���X�̃f�t�H���g */
    private static final String  DEFAULT_RECORDSTRATEGY      = "org.seasar.javelin.DefaultRecordStrategy";
    
    /** �f�t�H���g�ŗ��p����TelegramListener�� */
    private static final String  DEFAULT_TELEGEAM_LISTENERS
        = "org.seasar.javelin.communicate.GetRequestTelegramListener,"
    	+ "org.seasar.javelin.communicate.ResetRequestTelegramListener";

    /**
     * S2StatsJavelin�̐ݒ��ێ�����I�u�W�F�N�g���쐬����B
     */
    public S2JavelinConfig()
    {
        // �������Ȃ�
    }

    /**
     * �Ăяo�������L�^����ۂ�臒l��Ԃ��B
     *
     * @return 臒l�i�~���b�j
     */
    public long getAlarmThreshold()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getLong(ALARMTHRESHOLD_KEY, DEFAULT_ALARMTHRESHOLD);
    }

    /**
     * �Ăяo�������L�^����ۂ�臒l���ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetAlarmThreshold()
    {
        return isKeyExist(ALARMTHRESHOLD_KEY);
    }

    /**
     * �Ăяo�������L�^����ۂ�臒l��Ԃ��B
     *
     * @param alarmThreshold 臒l�i�~���b�j
     */
    public void setAlarmThreshold(long alarmThreshold)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setLong(ALARMTHRESHOLD_KEY, alarmThreshold);
    }

    /**
     * JMX�ʐM�ɂ������J���s�����ǂ����̐ݒ��Ԃ��B
     *
     * @return JMX�ʐM�ɂ������J���s���Ȃ�true
     */
    public boolean isRecordJMX()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(RECORD_JMX_KEY, DEFAULT_RECORD_JMX);
    }

    /**
     * JMX�ʐM�ɂ������J���s�����ǂ�����ݒ肷��B
     *
     * @param  JMX�ʐM�ɂ������J���s���Ȃ�true
     */
    public void setRecordJMX(boolean isLogStacktrace)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_STACKTRACE_KEY, isLogStacktrace);
    }

    /**
     * �h���C������Ԃ��B
     *
     * @return �h���C����
     */
    public String getDomain()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(DOMAIN_KEY, DEFAULT_DOMAIN);
    }

    /**
     * �h���C�������ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetDomain()
    {
        return isKeyExist(DOMAIN_KEY);
    }

    /**
     * �h���C�������Z�b�g����B
     *
     * @param domain �h���C����
     */
    public void setDomain(String domain)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setString(DOMAIN_KEY, domain);
    }

    /**
     * �Ăяo�������L�^����ő匏����Ԃ��B
     *
     * @return ����
     */
    public int getIntervalMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(INTERVALMAX_KEY, DEFAULT_INTERVALMAX);
    }

    /**
     * �Ăяo�������L�^����ő匏�����ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetIntervalMax()
    {
        return isKeyExist(INTERVALMAX_KEY);
    }

    /**
     * �Ăяo�������L�^����ő匏�����Z�b�g����B
     *
     * @param intervalMax ����
     */
    public void setIntervalMax(int intervalMax)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(INTERVALMAX_KEY, intervalMax);
    }

    /**
     * Javelin���O�t�@�C���̏o�͐���擾����B
     *
     * @return �o�͐�p�X
     */
    public String getJavelinFileDir()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(JAVELINFILEDIR_KEY, DEFAULT_JAVELINFILEDIR);
    }

    /**
     * Javelin���O�t�@�C���̏o�͐悪�ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetJavelinFileDir()
    {
        return isKeyExist(JAVELINFILEDIR_KEY);
    }

    /**
     * Javelin���O�t�@�C���̏o�͐���Z�b�g����B
     *
     * @param javelinFileDir �o�͐�p�X
     */
    public void setJavelinFileDir(String javelinFileDir)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setString(JAVELINFILEDIR_KEY, javelinFileDir);
    }

    /**
     * �Ăяo�������L�^����ۂ�臒l
     *
     * @return 臒l�i�~���b�j
     */
    public long getRecordThreshold()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getLong(RECORDTHRESHOLD_KEY, DEFAULT_RECORDTHRESHOLD);
    }

    /**
     * �Ăяo�������L�^����ۂ�臒l���ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetRecordThreshold()
    {
        return isKeyExist(RECORDTHRESHOLD_KEY);
    }

    /**
     * �Ăяo�������L�^����ۂ�臒l���Z�b�g����B
     *
     * @param recordThreshold 臒l
     */
    public void setRecordThreshold(long recordThreshold)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setLong(RECORDTHRESHOLD_KEY, recordThreshold);
    }

    /**
     * ��O�̔����������L�^����ő匏����Ԃ��B
     *
     * @return ����
     */
    public int getThrowableMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(THROWABLEMAX_KEY, DEFAULT_THROWABLEMAX);
    }

    /**
     * �Ăяo�������L�^����ۂ�臒l���ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetThrowableMax()
    {
        return isKeyExist(THROWABLEMAX_KEY);
    }

    /**
     * ��O�̔����������L�^����ő匏�����Z�b�g����B
     *
     * @param throwableMax ����
     */
    public void setThrowableMax(int throwableMax)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(THROWABLEMAX_KEY, throwableMax);
    }

    /**
     * �Ăяo����ɂ��閼�̂�Ԃ��B
     *
     * @return ����
     */
    public String getEndCalleeName()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(ENDCALLEENAME_KEY, DEFAULT_ENDCALLEENAME);
    }

    /**
     * �Ăяo����ɂ��閼�̂��ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetEndCalleeName()
    {
        return isKeyExist(ENDCALLEENAME_KEY);
    }

    /**
     * �Ăяo����ɂ��閼�̂��Z�b�g����B
     *
     * @param endCalleeName ����
     */
    public void setEndCalleeName(String endCalleeName)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setString(ENDCALLEENAME_KEY, endCalleeName);
    }

    /**
     * �Ăяo�����ɂ��閼�̂�Ԃ��B
     *
     * @return ����
     */
    public String getRootCallerName()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(ROOTCALLERNAME_KEY, DEFAULT_ROOTCALLERNAME);
    }

    /**
     * �Ăяo�����ɂ��閼�̂��ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetRootCallerName()
    {
        return isKeyExist(ROOTCALLERNAME_KEY);
    }

    /**
     * �Ăяo�����ɂ��閼�̂��Z�b�g����B
     *
     * @param rootCallerName ����
     */
    public void setRootCallerName(String rootCallerName)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setString(ROOTCALLERNAME_KEY, rootCallerName);
    }

    /**
     * �X�^�b�N�g���[�X���o�͂��邩�ǂ����̐ݒ��Ԃ��B
     *
     * @return �X�^�b�N�g���[�X���o�͂���Ȃ�true
     */
    public boolean isLogStacktrace()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(LOG_STACKTRACE_KEY, DEFAULT_LOG_STACKTRACE);
    }

    /**
     * �X�^�b�N�g���[�X���o�͂��邩�ǂ������ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetLogStacktrace()
    {
        return isKeyExist(LOG_STACKTRACE_KEY);
    }

    /**
     * �X�^�b�N�g���[�X���o�͂��邩�ǂ�����ݒ肷��B
     *
     * @param isLogStacktrace �X�^�b�N�g���[�X���o�͂���Ȃ�true
     */
    public void setLogStacktrace(boolean isLogStacktrace)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_STACKTRACE_KEY, isLogStacktrace);
    }

    /**
     * �������o�͂��邩�ǂ����̐ݒ��Ԃ��B
     *
     * @return �������o�͂���Ȃ�true
     */
    public boolean isLogArgs()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(LOG_ARGS_KEY, DEFAULT_LOG_ARGS);
    }

    /**
     * JMX�ɂ���Ď擾���������o�͂��邩�ǂ����̐ݒ��Ԃ��B
     *
     * @return JMX�ɂ���Ď擾���������o�͂���Ȃ�true
     */
    public boolean isLogJmxInfo()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(LOG_JMXNFO_KEY, DEFAULT_LOG_JMXINFO);
    }

    /**
     * �������o�͂��邩�ǂ������ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetLogArgs()
    {
        return isKeyExist(LOG_ARGS_KEY);
    }

    /**
     * �������o�͂��邩�ǂ�����ݒ肷��B
     *
     * @param isLogArgs �������o�͂���Ȃ�true
     */
    public void setLogArgs(boolean isLogArgs)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_ARGS_KEY, isLogArgs);
    }

    /**
     * JMX�ɂ���Ď擾���������o�͂��邩�ǂ�����ݒ肷��B
     *
     * @param isLogJmxInfo JMX�ɂ���Ď擾���������o�͂���Ȃ�true
     */
    public void setLogJmxInfo(boolean isLogJmxInfo)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_JMXNFO_KEY, isLogJmxInfo);
    }

    /**
     * �߂�l���o�͂��邩�ǂ����̐ݒ��Ԃ��B
     *
     * @return �߂�l���o�͂���Ȃ�true
     */
    public boolean isLogReturn()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(LOG_RETURN_KEY, DEFAULT_LOG_RETURN);
    }

    /**
     * �߂�l���o�͂��邩�ǂ������ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetLogReturn()
    {
        return isKeyExist(LOG_RETURN_KEY);
    }

    /**
     * �߂�l���o�͂��邩�ǂ�����ݒ肷��B
     *
     * @param isLogReturn �߂�l���o�͂���Ȃ�true
     */
    public void setLogReturn(boolean isLogReturn)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_RETURN_KEY, isLogReturn);
    }

    /**
     * �����̏ڍׂ��o�͂��邩�ǂ����̐ݒ��Ԃ��B
     *
     * @return �����̏ڍׂ��o�͂���Ȃ�true
     */
    public boolean isArgsDetail()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(ARGS_DETAIL_KEY, DEFAULT_ARGS_DETAIL);
    }

    /**
     * �����̏ڍׂ��o�͂��邩�ǂ������ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetArgsDetail()
    {
        return isKeyExist(ARGS_DETAIL_KEY);
    }

    /**
     * �����̏ڍׂ��o�͂��邩�ǂ�����ݒ肷��B
     *
     * @param isArgsDetail �����̏ڍׂ��o�͂���Ȃ�true
     */
    public void setArgsDetail(boolean isArgsDetail)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(ARGS_DETAIL_KEY, isArgsDetail);
    }

    /**
     * �߂�l�̏ڍׂ��o�͂��邩�ǂ����̐ݒ��Ԃ��B
     *
     * @return �߂�l�̏ڍׂ��o�͂���Ȃ�true
     */
    public boolean isReturnDetail()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(RETURN_DETAIL_KEY, DEFAULT_RETURN_DETAIL);
    }

    /**
     * �߂�l�̏ڍׂ��o�͂��邩�ǂ������ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetReturnDetail()
    {
        return isKeyExist(RETURN_DETAIL_KEY);
    }

    /**
     * �߂�l�̏ڍׂ��o�͂��邩�ǂ�����ݒ肷��B
     *
     * @param isReturnDetail �߂�l�̏ڍׂ��o�͂���Ȃ�true
     */
    public void setReturnDetail(boolean isReturnDetail)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(RETURN_DETAIL_KEY, isReturnDetail);
    }

    /**
     * �����̏ڍׂ��o�͂���K�w���̐ݒ��Ԃ��B
     *
     * @return �����̏ڍׂ��o�͂���K�w��
     */
    public int getArgsDetailDepth()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(ARGS_DETAIL_DEPTH_KEY, DEFAULT_ARGS_DETAIL_DEPTH);
    }

    /**
     * �����̏ڍׂ��o�͂���K�w�����ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetArgsDetailDepth()
    {
        return isKeyExist(ARGS_DETAIL_DEPTH_KEY);
    }

    /**
     * �����̏ڍׂ��o�͂���K�w����ݒ肷��B
     *
     * @param detailDepth �����̏ڍׂ��o�͂���K�w��
     */
    public void setArgsDetailDepth(int detailDepth)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(ARGS_DETAIL_DEPTH_KEY, detailDepth);
    }

    /**
     * �߂�l�̏ڍׂ��o�͂���K�w���̐ݒ��Ԃ��B
     *
     * @return �ڍׂ��o�͂���K�w��
     */
    public int getReturnDetailDepth()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(RETURN_DETAIL_DEPTH_KEY, DEFAULT_RETURN_DETAIL_DEPTH);
    }

    /**
     * �߂�l�̏ڍׂ��o�͂���K�w�����ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetReturnDetailDepth()
    {
        return isKeyExist(RETURN_DETAIL_DEPTH_KEY);
    }

    /**
     * �߂�l�̏ڍׂ��o�͂���K�w����ݒ肷��B
     *
     * @param returnDetailDepth �߂�l�̏ڍׂ��o�͂���K�w��
     */
    public void setReturnDetailDepth(int returnDetailDepth)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(RETURN_DETAIL_DEPTH_KEY, returnDetailDepth);
    }

    /**
     * �X���b�h���f����Ԃ��B
     *
     * @return �X���b�h���f��
     */
    public int getThreadModel()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(THREADMODEL_KEY, DEFAULT_THREADMODEL);
    }

    /**
     * �X���b�h���f�����ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetThreadModel()
    {
        return isKeyExist(THREADMODEL_KEY);
    }

    /**
     * �X���b�h���f�����Z�b�g����B
     *
     * @param threadModel �X���b�h���f��
     */
    public void setThreadModel(int threadModel)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(THREADMODEL_KEY, threadModel);
    }

    /**
     * HTTP�|�[�g��Ԃ��B
     *
     * @return HTTP�|�[�g
     */
    public int getHttpPort()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(HTTPPORT_KEY, DEFAULT_HTTPPORT);
    }

    /**
     * HTTP�|�[�g���ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetHttpPort()
    {
        return isKeyExist(HTTPPORT_KEY);
    }

    /**
     * HTTP�|�[�g���Z�b�g����
     *
     * @param httpPort HTTP�|�[�g
     */
    public void setHttpPort(int httpPort)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(HTTPPORT_KEY, httpPort);
    }

    /**
     * �L�[�ɑΉ�����l���Z�b�g����Ă��邩�ǂ����𒲂ׂ�B
     *
     * @param key �L�[
     * @return �l���Z�b�g����Ă����true
     */
    private boolean isKeyExist(String key)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.isKeyExist(key);
    }

    /**
     * Debug���[�h���ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetDebug()
    {
        return isKeyExist(DEBUG_KEY);
    }

    /**
     * Debug���[�h���Z�b�g����
     *
     * @param isDebug Debug���[�h
     */
    public void setDebug(boolean isDebug)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(DEBUG_KEY, isDebug);
    }

    /**
     * Debug���[�h��Ԃ��B
     *
     * @return Debug���[�h
     */
    public boolean isDebug()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        boolean result = DEFAULT_DEBUG;
        try
        {
            result = configUtil.getBoolean(DEBUG_KEY, DEFAULT_DEBUG);
        }
        catch (NumberFormatException nfe)
        {
            result = DEFAULT_DEBUG;
            this.setDebug(result);
        }
        return result;
    }

    public long getStatisticsThreshold()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getLong(STATISTICSTHRESHOLD_KEY, DEFAULT_STATISTICSTHRESHOLD);
    }

    public void setStatisticsThreshold(long statisticsThreshold)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setLong(STATISTICSTHRESHOLD_KEY, statisticsThreshold);
    }

    public int getStringLimitLength()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(STRINGLIMITLENGTH_KEY, DEFAULT_STRINGLIMITLENGTH);
    }

    public void setStringLimitLength(int stringLimitLength)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setLong(STRINGLIMITLENGTH_KEY, stringLimitLength);
    }

    /**
     * Javelin���O���o�͂��邩�ǂ����̐ݒ��Ԃ��B
     *
     * @return ���O���o�͂���Ȃ�true
     */
    public boolean isJavelinEnable()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(JAVELINENABLE_KEY, DEFAULT_JAVELINENABLE);
    }

    /**
     * �҂������|�[�g�ԍ���Ԃ��B
     *
     * @return �|�[�g�ԍ�
     */
    public int getAcceptPort()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(ACCEPTPORT_KEY, DEFAULT_ACCEPTPORT);
    }

    /**
     * Javelin���s�G���[���b�Z�[�W�̏o�͐�p�X��Ԃ��B
     *
     * @return �p�X
     */
    public String getErrorLog()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(ERRORLOG_KEY, DEFAULT_ERRORLOG);
    }

    /**
     * ���p����AlarmListener����Ԃ��B
     * ","��؂�ŕ����w�肷�邱�Ƃ��ł���B
     *
     * @return ���p����AlarmListener��
     */
    public String getAlarmListeners()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(ALARM_LISTENERS_KEY, DEFAULT_ALARM_LISTENERS);
    }

    /**
     * ���p����AlarmListener�����ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetAlarmListeners()
    {
        return isKeyExist(ALARM_LISTENERS_KEY);
    }

    /**
     * ���p����AlarmListener�����Z�b�g����B
     * ","��؂�ŕ����w�肷�邱�Ƃ��ł���B
     *
     * @param alarmListeners ���p����AlarmListener��
     */
    public void setAlarmListeners(String alarmListeners)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setString(ALARM_LISTENERS_KEY, alarmListeners);
    }

    public int getLogJvnMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(LOG_JVN_MAX_KEY, DEFAULT_LOG_JVN_MAX);
    }

    public int getLogZipMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(LOG_ZIP_MAX_KEY, DEFAULT_LOG_ZIP_MAX);
    }

    public boolean isLogZipMax()
    {
        return isKeyExist(LOG_ZIP_MAX_KEY);
    }
    
    /**
     * �L�^��������N���X����Ԃ�
     *
     * @return �N���X��
     */
    public String getRecordStrategy()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(RECORDSTRATEGY_KEY, DEFAULT_RECORDSTRATEGY);
    }
    
    /**
     * �L�^��������N���X�����ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isRecordStrategy()
    {
        return isKeyExist(RECORDSTRATEGY_KEY);
    }
    
    /**
     * ���p����TelegramListener����Ԃ��B
     * ","��؂�ŕ����w�肷�邱�Ƃ��ł���B
     *
     * @return ���p����TelegramListener��
     */
    public String getTelegramListeners()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(TELERAM_LISTENERS_KEY, DEFAULT_TELEGEAM_LISTENERS);
    }

    /**
     * ���p����TelegramListener�����ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return �ݒ肳��Ă����true
     */
    public boolean isSetTelegramListener()
    {
        return isKeyExist(TELERAM_LISTENERS_KEY);
    }
}
