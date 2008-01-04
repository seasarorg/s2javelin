package org.seasar.javelin.communicate.snmp;

import org.seasar.javelin.S2JavelinConfig;
import org.seasar.javelin.util.JavelinConfigUtil;

/**
 * S2Javelin��SNMP�ݒ��ێ�����N���X�B
 */
public class SnmpConfig extends S2JavelinConfig
{
    /** JAVELIN_MIB��OID */
    public static final String OID_JAVELIN_MIB            = "1.3.6.1.4.1.99999.1.1";
    /** EXCEED_AlARM_THRESHOLD��ENTERPRISE_ID */
    public static final String ENTERPRISE_ID              = OID_JAVELIN_MIB + ".2";
    /** EXCEED_AlARM_THRESHOLD��SPECIFIC_ID */
    public static final int    SPECIFIC_ID                = 10;
    /** EXCEED_AlARM_THRESHOLD��OID */
    public static final String OID_EXCEED_AlARM_THRESHOLD = ENTERPRISE_ID + "." + SPECIFIC_ID;

    /** JAVELIN_MIB_OBJECTS��OID */
    public static final String OID_JAVELIN_MIB_OBJECTS    = OID_JAVELIN_MIB + ".1";
    /** EVENT_TIME��OID */
    public static final String OID_EVENT_TIME             = OID_JAVELIN_MIB_OBJECTS + ".1";
    /** PROCESS_NAME��OID */
    public static final String OID_PROCESS_NAME           = OID_JAVELIN_MIB_OBJECTS + ".2";
    /** OBJ_NAME��OID */
    public static final String OID_OBJ_NAME               = OID_JAVELIN_MIB_OBJECTS + ".3";
    /** CLASS_OBJ_NAME��OID */
    public static final String OID_CLASS_OBJ_NAME         = OID_JAVELIN_MIB_OBJECTS + ".4";
    /** CLASS_NAME��OID */
    public static final String OID_CLASS_NAME             = OID_JAVELIN_MIB_OBJECTS + ".5";
    /** METHOD_NAME��OID */
    public static final String OID_METHOD_NAME            = OID_JAVELIN_MIB_OBJECTS + ".6";
    /** INTERVAL_MAX��OID */
    public static final String OID_INTERVAL_MAX           = OID_JAVELIN_MIB_OBJECTS + ".7";
    /** THROWABLE_MAX��OID */
    public static final String OID_THROWABLE_MAX          = OID_JAVELIN_MIB_OBJECTS + ".8";
    /** COUNT��OID */
    public static final String OID_COUNT                  = OID_JAVELIN_MIB_OBJECTS + ".9";
    /** MINIMUM��OID */
    public static final String OID_MINIMUM                = OID_JAVELIN_MIB_OBJECTS + ".10";
    /** MAXIMUM��OID */
    public static final String OID_MAXIMUM                = OID_JAVELIN_MIB_OBJECTS + ".11";
    /** INTERVAL_LIST��OID */
    public static final String OID_INTERVAL_LIST          = OID_JAVELIN_MIB_OBJECTS + ".12";
    /** THROWABLE_LIST��OID */
    public static final String OID_THROWABLE_LIST         = OID_JAVELIN_MIB_OBJECTS + ".13";
    /** CALLER_SET��OID */
    public static final String OID_CALLER_SET             = OID_JAVELIN_MIB_OBJECTS + ".14";
    /** RECORD_THRESHOLD��OID */
    public static final String OID_RECORD_THRESHOLD       = OID_JAVELIN_MIB_OBJECTS + ".15";
    /** ALARM_THRESHOLD��OID */
    public static final String OID_ALARM_THRESHOLD        = OID_JAVELIN_MIB_OBJECTS + ".16";
    
    /** SNMP�n�p�����[�^�̐ړ��� */
    public static final String  SNMP_PREFIX              = JAVELIN_PREFIX + "snmp.";
    
    /** SNMPTrap�𑗐M���邩�ǂ���(true=���M����Afalse=���M���Ȃ�)�B�f�t�H���g��false(=���M���Ȃ�) */
    public static final String  SEND_TRAP_KEY            = SNMP_PREFIX + "sendTrap";

    /** �}�l�[�W�����X�g�B�J���}��؂�ŕ����w��\�B�f�t�H���g��localhost */
    public static final String  MANAGERS_KEY             = SNMP_PREFIX + "managers";

    /** SNMP Trap�|�[�g�ԍ��B�f�t�H���g��162 */
    public static final String  TRAP_PORT_KEY            = SNMP_PREFIX + "trapPort";

    /** Trap�R�~���j�e�B���B�f�t�H���g��public */
    public static final String  TRAP_COMMUNITY_KEY       = SNMP_PREFIX + "trapCommunity";

    /** SNMP Version�Bv2c�̂ݎw��\�B�f�t�H���g��v2c */
    public static final String  VERSION_KEY              = SNMP_PREFIX + "version";

    /** SNMPTrap�𑗐M���邩�ǂ����̃f�t�H���g�lfalse(=���M���Ȃ�) */
    private static final boolean DEFAULT_SEND_TRAP        = false;

    /** �}�l�[�W�����X�g�̃f�t�H���g�llocalhost */
    private static final String  DEFAULT_MANAGERS         = "localhost";
    
    /** SNMP Trap�|�[�g�ԍ��̃f�t�H���g�l162 */
    private static final int     DEFAULT_TRAP_PORT        = 162;

    /** Trap�R�~���j�e�B���̃f�t�H���g�lpublic */
    private static final String  DEFAULT_TRAP_COMMUNITY   = "public";
    
    /** SNMP Version: v1 */
    public static final String  VERSION_V1               = "v1";

    /** SNMP Version: v2c */
    public static final String  VERSION_V2C              = "v2c";

    /** SNMP Version: v3 */
    public static final String  VERSION_V3               = "v3";

    /** SNMP Version�̃f�t�H���g�lv2c */
    private static final String  DEFAULT_VERSION          = VERSION_V2C;

    /**
     * SNMPTrap�𑗐M���邩�ǂ�����Ԃ��B
     *
     * @return SNMPTrap�𑗐M���邩�ǂ���(true=���M����Afalse=���M���Ȃ�)
     */
    public boolean getSendTrap()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(SEND_TRAP_KEY, DEFAULT_SEND_TRAP);
    }

    /**
     * �}�l�[�W�����X�g��Ԃ��B
     *
     * @return �}�l�[�W�����X�g�B�J���}��؂�ŕ����w��\
     */
    public String getManagers()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(MANAGERS_KEY, DEFAULT_MANAGERS);
    }

    /**
     * SNMP Trap�|�[�g�ԍ���Ԃ��B
     *
     * @return SNMP Trap�|�[�g�ԍ�
     */
    public int getTrapPort()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(TRAP_PORT_KEY, DEFAULT_TRAP_PORT);
    }

    /**
     * Trap�R�~���j�e�B����Ԃ��B
     *
     * @return Trap�R�~���j�e�B��
     */
    public String getTrapCommunity()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(TRAP_COMMUNITY_KEY, DEFAULT_TRAP_COMMUNITY);
    }

    /**
     * SNMP Version��Ԃ��B
     *
     * @return SNMP Version
     */
    public String getVersion()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(VERSION_KEY, DEFAULT_VERSION);
    }
}
