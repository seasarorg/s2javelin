package org.seasar.javelin;

import org.seasar.javelin.util.JavelinConfigUtil;

/**
 * S2StatsJavelin�̐ݒ��ێ�����N���X�B
 * @author eriguchi
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

    /** ���\�b�h���ώ��Ԃ��o�͂��邽�߂ɋL�^����Invocation���̃v���p�e�B */
    public static final String   INTERVALMAX_KEY             = JAVELIN_PREFIX + "intervalMax";

    /** ��O�̐����L�^���邽�߂�Invocation���̃v���p�e�B */
    public static final String   THROWABLEMAX_KEY            = JAVELIN_PREFIX + "throwableMax";

    /** �������ɕۑ�����臒l�̃v���p�e�B */
    public static final String   STATISTICSTHRESHOLD_KEY     = JAVELIN_PREFIX + "statistics"
                                                                     + "Threshold";

    /** �t�@�C���ɏo�͂���TAT��臒l�̃v���p�e�B */
    public static final String   RECORDTHRESHOLD_KEY         = JAVELIN_PREFIX + "recordThreshold";

    /** �A���[����ʒm����TAT��臒l�̃v���p�e�B */
    public static final String   ALARMTHRESHOLD_KEY          = JAVELIN_PREFIX + "alarmThreshold";

    /** Javelin���O���o�͂���t�@�C�����̃v���p�e�B */
    public static final String   JAVELINFILEDIR_KEY          = JAVELIN_PREFIX + "javelinFileDir";

    /** BottleneckEye�ɗ��p����h���C���̃v���p�e�B */
    public static final String   DOMAIN_KEY                  = JAVELIN_PREFIX + "domain";

    /** �X�^�b�N�g���[�X���o�͂��邩�ǂ��������肷��v���p�e�B */
    public static final String   LOG_STACKTRACE_KEY          = JAVELIN_PREFIX + "log.stacktrace";

    /** ���������o�͂��邩�ǂ��������肷��v���p�e�B */
    public static final String   LOG_ARGS_KEY                = JAVELIN_PREFIX + "log.args";

    /** JMXInfo���o�͂��邩�ǂ������肷��v���p�e�B */
    public static final String   LOG_MBEANINFO_KEY           = JAVELIN_PREFIX + "log.mbeaninfo";

    /** �[�_�ŁAJMXInfo���o�͂��邩�ǂ������肷��v���p�e�B */
    public static final String   LOG_MBEANINFO_ROOT_KEY      = JAVELIN_PREFIX + "log.mbeaninfo.root";

    /** �߂�l���o�͂��邩�ǂ��������肷��v���p�e�B */
    public static final String   LOG_RETURN_KEY              = JAVELIN_PREFIX + "log.return";

    /** �����̏ڍ׏����o�͂��邩�ǂ��������肷��v���p�e�B */
    public static final String   ARGS_DETAIL_KEY             = JAVELIN_PREFIX + "log.args.detail";

    /** �߂�l�̏ڍ׏����o�͂��邩�ǂ��������肷��v���p�e�B */
    public static final String   RETURN_DETAIL_KEY           = JAVELIN_PREFIX + "log.return.detail";

    /** �����̏ڍ׏��̐[����\���v���p�e�B */
    public static final String   ARGS_DETAIL_DEPTH_KEY       = JAVELIN_PREFIX + "log.args."
                                                                     + "detail.depth";

    /** �߂�l�̏ڍ׏��̐[����\���v���p�e�B */
    public static final String   RETURN_DETAIL_DEPTH_KEY     = JAVELIN_PREFIX + "log.return."
                                                                     + "detail.depth";

    /** �Ăяo�������s���̂Ƃ��ɐݒ肷�閼�O�̃v���p�e�B */
    public static final String   ROOTCALLERNAME_KEY          = JAVELIN_PREFIX + "rootCallerName";

    /** �ł��[���Ăяo���悪�s���̂Ƃ��ɐݒ肷�閼�O�̃v���p�e�B */
    public static final String   ENDCALLEENAME_KEY           = JAVELIN_PREFIX + "endCalleeName";

    /** �X���b�h�̖��̂̌�����@��\���v���p�e�B */
    public static final String   THREADMODEL_KEY             = JAVELIN_PREFIX + "threadModel";

    /** JMX��HTTPAdaptor�����J����|�[�g�ԍ���\���v���p�e�B */
    public static final String   HTTPPORT_KEY                = JAVELIN_PREFIX + "httpPort";

    /** StatsJavelin�̑҂������|�[�g�̃v���p�e�B�� */
    public static final String   ACCEPTPORT_KEY              = JAVELIN_PREFIX + "acceptPort";

    /** Javelin�̃��O�o��ON/OFF�ؑփt���O�̃v���p�e�B�� */
    public static final String   JAVELINENABLE_KEY           = JAVELIN_PREFIX + "javelinEnable";

    /** �����A�߂�l���̕����� */
    public static final String   STRINGLIMITLENGTH_KEY       = JAVELIN_PREFIX + "stringLimitLength";

    /** �G���[���O�t�@�C���̃v���p�e�B�� */
    public static final String   SYSTEMLOG_KEY               = JAVELIN_PREFIX + "system.log";

    /** ���p����AlarmListener�� */
    public static final String   ALARM_LISTENERS_KEY         = JAVELIN_PREFIX + "alarmListeners";

    /** JMX�ʐM�ɂ������J���s�����ǂ�����\���v���p�e�B�� */
    public static final String   RECORD_JMX_KEY              = JAVELIN_PREFIX + "record.jmx";

    /** jvn���O�t�@�C���̍ő吔��\���v���p�e�B�� */
    public static final String   LOG_JVN_MAX_KEY             = JAVELIN_PREFIX + "log.jvn.max";

    /** jvn���O�t�@�C�������k����zip�t�@�C���̍ő吔��\���v���p�e�B�� */
    public static final String   LOG_ZIP_MAX_KEY             = JAVELIN_PREFIX + "log.zip.max";

    /** �L�^��������N���X */
    public static final String   RECORDSTRATEGY_KEY          = JAVELIN_PREFIX + "recordStrategy";

    /** ���p����TelegramListener�� */
    public static final String   TELERAM_LISTENERS_KEY       = JAVELIN_PREFIX + "telegramListeners";

    /** Javelin�̃V�X�e�����O�̍ő�t�@�C�����̃L�[ */
    private static final String  SYSTEM_LOG_NUM_MAX_KEY      = JAVELIN_PREFIX
                                                                     + "system.log.num.max";

    /** Javelin�̃V�X�e�����O�̍ő�t�@�C���T�C�Y�̃L�[ */
    private static final String  SYSTEM_LOG_SIZE_MAX_KEY     = JAVELIN_PREFIX + "system.log."
                                                                     + "size.max";

    /** Javelin�̃V�X�e�����O�̃��O���x���̃L�[ */
    private static final String  SYSTEM_LOG_LEVEL_KEY        = JAVELIN_PREFIX + "system.log.level";

    /** MBeanManager���������V���A���C�Y����t�@�C���� */
    public static final String   SERIALIZE_FILE_KEY          = JAVELIN_PREFIX + "serializeFile";

    /** �ۑ�����CallTree���̃v���p�e�B */
    private static final String  CALL_TREE_MAX_KEY           = JAVELIN_PREFIX + "call.tree.max";

    /** �A�v���P�[�V�������s���̗�O���o�͂��邩�ǂ��������肷��v���p�e�B */
    public static final String   RECORD_EXCEPTION_KEY        = JAVELIN_PREFIX + "recordException";

    /** �A�v���P�[�V�������s���̗�O���o�͂��邩�ǂ��������肷��v���p�e�B */
    public static final String   ALARM_EXCEPTION_KEY         = JAVELIN_PREFIX + "alarmException";

    /** �ۑ�����CallTree���̃f�t�H���g�l */
    private static final int     DEFAULT_CALL_TREE_MAX       = 1000000;

    /** ���\�b�h���ώ��Ԃ��o�͂��邽�߂ɋL�^����Invocation���̃f�t�H���g�l */
    private static final int     DEFAULT_INTERVALMAX         = 500;

    /** ��O�̐����L�^���邽�߂�Invocation���̃f�t�H���g�l */
    private static final int     DEFAULT_THROWABLEMAX        = 500;

    /** �������ɕۑ�����臒l�̃v���p�e�B */
    private static final long    DEFAULT_STATISTICSTHRESHOLD = 0;

    /** �t�@�C���ɏo�͂���TAT��臒l�̃v���p�e�B */
    private static final long    DEFAULT_RECORDTHRESHOLD     = 5000;

    /** �A���[����ʒm����TAT��臒l�̃v���p�e�B */
    private static final long    DEFAULT_ALARMTHRESHOLD      = 5000;

    /** Javelin���O���o�͂���t�@�C�����̃v���p�e�B */
    private static final String  DEFAULT_JAVELINFILEDIR      = "../logs";

    /** BottleneckEye�ɗ��p����h���C���̃f�t�H���g�l */
    private static final String  DEFAULT_DOMAIN              = "default";

    /** �X�^�b�N�g���[�X���o�͂��邩�ǂ��������肷��f�t�H���g�l */
    private static final boolean DEFAULT_LOG_STACKTRACE      = false;

    /** ���������o�͂��邩�ǂ��������肷��f�t�H���g�l */
    private static final boolean DEFAULT_LOG_ARGS            = true;

    /** JMXInfo���o�͂��邩�ǂ������肷��f�t�H���g�l */
    private static final boolean DEFAULT_LOG_MBEANINFO       = true;

    /** JMXInfo���o�͂��邩�ǂ������肷��f�t�H���g�l */
    private static final boolean DEFAULT_LOG_MBEANINFO_ROOT  = true;

    /** �߂�l���o�͂��邩�ǂ��������肷��f�t�H���g�l */
    private static final boolean DEFAULT_LOG_RETURN          = true;

    /** �����̏ڍ׏����o�͂��邩�ǂ��������肷��f�t�H���g�l */
    private static final boolean DEFAULT_ARGS_DETAIL         = false;

    /** �߂�l�̏ڍ׏����o�͂��邩�ǂ��������肷��f�t�H���g�l */
    private static final boolean DEFAULT_RETURN_DETAIL       = false;

    /** �����̏ڍ׏��̐[���̃f�t�H���g�l */
    private static final int     DEFAULT_ARGS_DETAIL_DEPTH   = 1;

    /** �߂�l�̏ڍ׏��̐[���̃f�t�H���g�l */
    private static final int     DEFAULT_RETURN_DETAIL_DEPTH = 1;

    /** �Ăяo�������s���̂Ƃ��ɐݒ肷�閼�O�̃v���p�e�B */
    private static final String  DEFAULT_ROOTCALLERNAME      = "root";

    /** �ł��[���Ăяo���悪�s���̂Ƃ��ɐݒ肷�閼�O�̃v���p�e�B */
    private static final String  DEFAULT_ENDCALLEENAME       = "unknown";

    /** �X���b�h�̖��̂̌�����@��\���v���p�e�B */
    private static final int     DEFAULT_THREADMODEL         = 0;

    /** JMX��HTTPAdaptor�����J����|�[�g�ԍ���\���v���p�e�B */
    private static final int     DEFAULT_HTTPPORT            = 0;

    /** �����A�߂�l���̕����񒷂̃f�t�H���g�l */
    private static final int     DEFAULT_STRINGLIMITLENGTH   = 102400;

    /** Javelin���O���o�͂��邩�ǂ����̃f�t�H���g�ݒ� */
    public static final boolean  DEFAULT_JAVELINENABLE       = false;

    /** �҂������|�[�g�ԍ��̃f�t�H���g�l */
    public static final int      DEFAULT_ACCEPTPORT          = 18000;

    /** Javelin���s�G���[���b�Z�[�W�̏o�͐�p�X�̃f�t�H���g�l */
    public static final String   DEFAULT_SYSTEMLOG           = "../traces";

    /** �f�t�H���g�ŗ��p����AlarmListener�� */
    private static final String  DEFAULT_ALARM_LISTENERS     = "org.seasar.javelin.communicate.JmxListener";

    /** �f�t�H���g��JMX�ʐM�ɂ������J���s�����ǂ��� */
    private static final boolean DEFAULT_RECORD_JMX          = true;

    /** jvn���O�t�@�C���̍ő吔�̃f�t�H���g */
    private static final int     DEFAULT_LOG_JVN_MAX         = 256;

    /** jvn���O�t�@�C�������k����zip�t�@�C���̍ő吔�̃f�t�H���g */
    private static final int     DEFAULT_LOG_ZIP_MAX         = 256;

    /** �L�^��������N���X�̃f�t�H���g */
    public static final String   DEFAULT_RECORDSTRATEGY      = "org.seasar.javelin.S2DefaultRecordStrategy";

    /** �f�t�H���g�ŗ��p����TelegramListener�� */
    private static final String  DEFAULT_TELEGEAM_LISTENERS  = "org.seasar.javelin.communicate.GetRequestTelegramListener,"
                                                                     + "org.seasar.javelin.communicate.ResetRequestTelegramListener";

    /** Javelin�̃V�X�e�����O�̍ő�t�@�C�����̃f�t�H���g */
    private static final int     DEFAULT_SYSTEM_LOG_NUM_MAX  = 16;

    /** Javelin�̃V�X�e�����O�̍ő�t�@�C���T�C�Y�̃f�t�H���g */
    private static final int     DEFAULT_SYSTEM_LOG_SIZE_MAX = 1000000;

    /** MBeanManager���������V���A���C�Y����t�@�C�����̃f�t�H���g */
    public static final String   DEFAULT_SERIALIZE_FILE      = "../data/serialize.dat";

    /** Javelin�̃V�X�e�����O�̃��O���x���̃f�t�H���g */
    private static final String  DEFAULT_SYSTEM_LOG_LEVEL    = "INFO";

    /** �A�v���P�[�V�������s���̗�O���Ƀ��O�o�͂���f�t�H���g�l */
    private static final boolean DEFAULT_RECORD_EXCEPTION    = true;

    /** �A�v���P�[�V�������s���̗�O���ɃA���[���ʒm����f�t�H���g�l */
    private static final boolean DEFAULT_ALARM_EXCEPTION     = true;

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
     * @param isRecordJMX JMX�ʐM�ɂ������J���s���Ȃ�true
     */
    public void setRecordJMX(boolean isRecordJMX)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(RECORD_JMX_KEY, isRecordJMX);
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
        String relativePath = configUtil.getString(JAVELINFILEDIR_KEY, DEFAULT_JAVELINFILEDIR);
        return configUtil.convertRelativePathtoAbsolutePath(relativePath);
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
     * MBean�ɂ���Ď擾���������o�͂��邩�ǂ����̐ݒ��Ԃ��B
     *
     * @return MBean�ɂ���Ď擾���������o�͂���Ȃ�true
     */
    public boolean isLogMBeanInfo()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(LOG_MBEANINFO_KEY, DEFAULT_LOG_MBEANINFO);
    }

    /**
     * �[�_�ŁAMBean�ɂ���Ď擾���������o�͂��邩�ǂ����̐ݒ��Ԃ��B
     *
     * @return �[�_�ŁAMBean�ɂ���Ď擾���������o�͂���Ȃ�true
     */
    public boolean isLogMBeanInfoRoot()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(LOG_MBEANINFO_ROOT_KEY, DEFAULT_LOG_MBEANINFO_ROOT);
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
     * MBean�ɂ���Ď擾���������o�͂��邩�ǂ�����ݒ肷��B
     *
     * @param isLogMBeanInfo MBean�ɂ���Ď擾���������o�͂���Ȃ�true
     */
    public void setLogMBeanInfo(boolean isLogMBeanInfo)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_MBEANINFO_KEY, isLogMBeanInfo);
    }

    /**
     * MBean�ɂ���Ď擾�������i���[�g�m�[�h�j���o�͂��邩�ǂ�����ݒ肷��B
     *
     * @param isLogMBeanInfo MBean�ɂ���Ď擾���������o�͂���Ȃ�true
     */
    public void setLogMBeanInfoRoot(boolean isLogMBeanInfo)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(LOG_MBEANINFO_ROOT_KEY, isLogMBeanInfo);
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
     * �������ɕۑ�����臒l���擾����B
     * @return �������ɕۑ�����臒l
     */
    public long getStatisticsThreshold()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getLong(STATISTICSTHRESHOLD_KEY, DEFAULT_STATISTICSTHRESHOLD);
    }

    /**
     * �������ɕۑ�����臒l��ݒ肷��B
     * @param statisticsThreshold �������ɕۑ�����臒l
     */
    public void setStatisticsThreshold(long statisticsThreshold)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setLong(STATISTICSTHRESHOLD_KEY, statisticsThreshold);
    }

    /**
     * ���O�ɏo�͂���Args�̒�����臒l���擾����B
     * @return Args�̒�����臒l
     */
    public int getStringLimitLength()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(STRINGLIMITLENGTH_KEY, DEFAULT_STRINGLIMITLENGTH);
    }

    /**
     * ���O�ɏo�͂���Args�̒�����臒l��ݒ肷��B
     * @param stringLimitLength Args�̒�����臒l
     */
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
     * Javelin�V�X�e�����O�̏o�͐�f�B���N�g����Ԃ��B
     *
     * @return Javelin�V�X�e�����O�̏o�͐�f�B���N�g���B
     */
    public String getSystemLog()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        String relativePath = configUtil.getString(SYSTEMLOG_KEY, DEFAULT_SYSTEMLOG);
        return configUtil.convertRelativePathtoAbsolutePath(relativePath);
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

    /**
     * ���O�T�C�Y�̍ő�l���擾����B
     * @return ���O�T�C�Y�̍ő�l
     */
    public int getLogJvnMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(LOG_JVN_MAX_KEY, DEFAULT_LOG_JVN_MAX);
    }

    /**
     * Zip�����郍�O�̃t�@�C�������擾����B
     * @return Zip�����郍�O�̃t�@�C����
     */
    public int getLogZipMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(LOG_ZIP_MAX_KEY, DEFAULT_LOG_ZIP_MAX);
    }

    /**
     * ���O��Zip�����邩�ǂ�����Ԃ��B
     * @return true:���O��Zip������Afalse:���O��Zip�����Ȃ��B
     */
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

    /**
     * Javelin�̃V�X�e�����O�̍ő�t�@�C�������擾����B
     *
     * @return Javelin�̃V�X�e�����O�̍ő�t�@�C�����B
     */
    public int getSystemLogNumMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(SYSTEM_LOG_NUM_MAX_KEY, DEFAULT_SYSTEM_LOG_NUM_MAX);
    }

    /**
     * MBeanManager���������V���A���C�Y����t�@�C������Ԃ��B
     *
     * @return ���p����t�@�C����
     */
    public String getSerializeFile()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        String relativePath = configUtil.getString(SERIALIZE_FILE_KEY, DEFAULT_SERIALIZE_FILE);
        return configUtil.convertRelativePathtoAbsolutePath(relativePath);
    }

    /**
     * MBeanManager���������V���A���C�Y����t�@�C�������ݒ肳��Ă��邩�ǂ����𒲂ׂ�B
     *
     * @return ���p����t�@�C����
     */
    public boolean isSetSerializeFile()
    {
        return isKeyExist(SERIALIZE_FILE_KEY);
    }

    /**
     * Javelin�̃V�X�e�����O�̍ő�t�@�C���T�C�Y���擾����B
     *
     * @return Javelin�̃V�X�e�����O�̍ő�t�@�C���T�C�Y�B
     */
    public int getSystemLogSizeMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(SYSTEM_LOG_SIZE_MAX_KEY, DEFAULT_SYSTEM_LOG_SIZE_MAX);
    }

    /**
     * �V�X�e�����O�̃��x�����擾����B
     * @return �V�X�e�����O�̃��x��
     */
    public String getSystemLogLevel()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(SYSTEM_LOG_LEVEL_KEY, DEFAULT_SYSTEM_LOG_LEVEL);
    }

    /**
     * CallTree�̍ő�l���擾����B
     * @return CallTree�̍ő�l
     */
    public int getCallTreeMax()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(CALL_TREE_MAX_KEY, DEFAULT_CALL_TREE_MAX);
    }

    /**
     * CallTree�̍ő�l��ݒ肷��B
     * @param callTreeMax CallTree�̍ő�l
     */
    public void setCallTreeMax(int callTreeMax)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setInteger(CALL_TREE_MAX_KEY, callTreeMax);
    }

    /**
     * �A�v���P�[�V�������s���ɗ�O�����O�ɏo�͂��邩�ǂ����B
     * @return true:���O�ɏo�́Afalse:���O�ɏo�͂��Ȃ��B
     */
    public boolean isRecordException()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(RECORD_EXCEPTION_KEY, DEFAULT_RECORD_EXCEPTION);
    }

    /**
     * �A�v���P�[�V�������s���ɗ�O�����O�ɏo�͂��邩�ǂ����ݒ肷��B
     *
     * @param isRecordException ��O�����O�ɏo�͂���Ȃ�true
     */
    public void setRecordException(boolean isRecordException)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(RECORD_EXCEPTION_KEY, isRecordException);
    }

    /**
     * �A�v���P�[�V�������s���ɗ�O���A���[���ʒm���邩�ǂ����B
     * @return true:�A���[���ʒm����Afalse:�A���[���ʒm���Ȃ��B
     */
    public boolean isAlarmException()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(ALARM_EXCEPTION_KEY, DEFAULT_ALARM_EXCEPTION);
    }

    /**
     * �A�v���P�[�V�������s���ɗ�O���A���[���ʒm���邩�ǂ����ݒ肷��B
     *
     * @param isAlarmException ��O���A���[���ʒm����Ȃ�true
     */
    public void setAlarmException(boolean isAlarmException)
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        configUtil.setBoolean(ALARM_EXCEPTION_KEY, isAlarmException);
    }

}
