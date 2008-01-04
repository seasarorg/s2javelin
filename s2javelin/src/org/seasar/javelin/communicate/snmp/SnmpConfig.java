package org.seasar.javelin.communicate.snmp;

import org.seasar.javelin.S2JavelinConfig;
import org.seasar.javelin.util.JavelinConfigUtil;

/**
 * S2JavelinのSNMP設定を保持するクラス。
 */
public class SnmpConfig extends S2JavelinConfig
{
    /** JAVELIN_MIBのOID */
    public static final String OID_JAVELIN_MIB            = "1.3.6.1.4.1.99999.1.1";
    /** EXCEED_AlARM_THRESHOLDのENTERPRISE_ID */
    public static final String ENTERPRISE_ID              = OID_JAVELIN_MIB + ".2";
    /** EXCEED_AlARM_THRESHOLDのSPECIFIC_ID */
    public static final int    SPECIFIC_ID                = 10;
    /** EXCEED_AlARM_THRESHOLDのOID */
    public static final String OID_EXCEED_AlARM_THRESHOLD = ENTERPRISE_ID + "." + SPECIFIC_ID;

    /** JAVELIN_MIB_OBJECTSのOID */
    public static final String OID_JAVELIN_MIB_OBJECTS    = OID_JAVELIN_MIB + ".1";
    /** EVENT_TIMEのOID */
    public static final String OID_EVENT_TIME             = OID_JAVELIN_MIB_OBJECTS + ".1";
    /** PROCESS_NAMEのOID */
    public static final String OID_PROCESS_NAME           = OID_JAVELIN_MIB_OBJECTS + ".2";
    /** OBJ_NAMEのOID */
    public static final String OID_OBJ_NAME               = OID_JAVELIN_MIB_OBJECTS + ".3";
    /** CLASS_OBJ_NAMEのOID */
    public static final String OID_CLASS_OBJ_NAME         = OID_JAVELIN_MIB_OBJECTS + ".4";
    /** CLASS_NAMEのOID */
    public static final String OID_CLASS_NAME             = OID_JAVELIN_MIB_OBJECTS + ".5";
    /** METHOD_NAMEのOID */
    public static final String OID_METHOD_NAME            = OID_JAVELIN_MIB_OBJECTS + ".6";
    /** INTERVAL_MAXのOID */
    public static final String OID_INTERVAL_MAX           = OID_JAVELIN_MIB_OBJECTS + ".7";
    /** THROWABLE_MAXのOID */
    public static final String OID_THROWABLE_MAX          = OID_JAVELIN_MIB_OBJECTS + ".8";
    /** COUNTのOID */
    public static final String OID_COUNT                  = OID_JAVELIN_MIB_OBJECTS + ".9";
    /** MINIMUMのOID */
    public static final String OID_MINIMUM                = OID_JAVELIN_MIB_OBJECTS + ".10";
    /** MAXIMUMのOID */
    public static final String OID_MAXIMUM                = OID_JAVELIN_MIB_OBJECTS + ".11";
    /** INTERVAL_LISTのOID */
    public static final String OID_INTERVAL_LIST          = OID_JAVELIN_MIB_OBJECTS + ".12";
    /** THROWABLE_LISTのOID */
    public static final String OID_THROWABLE_LIST         = OID_JAVELIN_MIB_OBJECTS + ".13";
    /** CALLER_SETのOID */
    public static final String OID_CALLER_SET             = OID_JAVELIN_MIB_OBJECTS + ".14";
    /** RECORD_THRESHOLDのOID */
    public static final String OID_RECORD_THRESHOLD       = OID_JAVELIN_MIB_OBJECTS + ".15";
    /** ALARM_THRESHOLDのOID */
    public static final String OID_ALARM_THRESHOLD        = OID_JAVELIN_MIB_OBJECTS + ".16";
    
    /** SNMP系パラメータの接頭辞 */
    public static final String  SNMP_PREFIX              = JAVELIN_PREFIX + "snmp.";
    
    /** SNMPTrapを送信するかどうか(true=送信する、false=送信しない)。デフォルトはfalse(=送信しない) */
    public static final String  SEND_TRAP_KEY            = SNMP_PREFIX + "sendTrap";

    /** マネージャリスト。カンマ区切りで複数指定可能。デフォルトはlocalhost */
    public static final String  MANAGERS_KEY             = SNMP_PREFIX + "managers";

    /** SNMP Trapポート番号。デフォルトは162 */
    public static final String  TRAP_PORT_KEY            = SNMP_PREFIX + "trapPort";

    /** Trapコミュニティ名。デフォルトはpublic */
    public static final String  TRAP_COMMUNITY_KEY       = SNMP_PREFIX + "trapCommunity";

    /** SNMP Version。v2cのみ指定可能。デフォルトはv2c */
    public static final String  VERSION_KEY              = SNMP_PREFIX + "version";

    /** SNMPTrapを送信するかどうかのデフォルト値false(=送信しない) */
    private static final boolean DEFAULT_SEND_TRAP        = false;

    /** マネージャリストのデフォルト値localhost */
    private static final String  DEFAULT_MANAGERS         = "localhost";
    
    /** SNMP Trapポート番号のデフォルト値162 */
    private static final int     DEFAULT_TRAP_PORT        = 162;

    /** Trapコミュニティ名のデフォルト値public */
    private static final String  DEFAULT_TRAP_COMMUNITY   = "public";
    
    /** SNMP Version: v1 */
    public static final String  VERSION_V1               = "v1";

    /** SNMP Version: v2c */
    public static final String  VERSION_V2C              = "v2c";

    /** SNMP Version: v3 */
    public static final String  VERSION_V3               = "v3";

    /** SNMP Versionのデフォルト値v2c */
    private static final String  DEFAULT_VERSION          = VERSION_V2C;

    /**
     * SNMPTrapを送信するかどうかを返す。
     *
     * @return SNMPTrapを送信するかどうか(true=送信する、false=送信しない)
     */
    public boolean getSendTrap()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getBoolean(SEND_TRAP_KEY, DEFAULT_SEND_TRAP);
    }

    /**
     * マネージャリストを返す。
     *
     * @return マネージャリスト。カンマ区切りで複数指定可能
     */
    public String getManagers()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(MANAGERS_KEY, DEFAULT_MANAGERS);
    }

    /**
     * SNMP Trapポート番号を返す。
     *
     * @return SNMP Trapポート番号
     */
    public int getTrapPort()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getInteger(TRAP_PORT_KEY, DEFAULT_TRAP_PORT);
    }

    /**
     * Trapコミュニティ名を返す。
     *
     * @return Trapコミュニティ名
     */
    public String getTrapCommunity()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(TRAP_COMMUNITY_KEY, DEFAULT_TRAP_COMMUNITY);
    }

    /**
     * SNMP Versionを返す。
     *
     * @return SNMP Version
     */
    public String getVersion()
    {
        JavelinConfigUtil configUtil = JavelinConfigUtil.getInstance();
        return configUtil.getString(VERSION_KEY, DEFAULT_VERSION);
    }
}
