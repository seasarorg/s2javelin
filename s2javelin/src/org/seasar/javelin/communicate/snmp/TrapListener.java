package org.seasar.javelin.communicate.snmp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.seasar.javelin.JavelinErrorLogger;
import org.seasar.javelin.communicate.AlarmListener;
import org.seasar.javelin.bean.Invocation;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * 呼び出し情報を元にTrapを送信するクラス。
 * @author tsukano
 */
public class TrapListener implements AlarmListener
{
    /** SNMPTrapを送信するかどうか */
    private boolean sendType;
    /** マネージャリスト */
    private String[] managers;
    /** SNMP Trapポート番号 */
    private int trapPort;
    /** Trapコミュニティ名 */
    private String trapCommunity;
    /** SNMP Version */
    private String version;
    
    /**
     * SnmpConfigから設定情報を読み込み、TrapListenerを初期化する。
     */
    public TrapListener()
    {
        SnmpConfig config = new SnmpConfig();
        this.sendType = config.getSendTrap();
        this.managers = config.getManagers().split(",");
        this.trapPort = config.getTrapPort();
        this.trapCommunity = config.getTrapCommunity();
        this.version = config.getVersion();
    }

    /**
     * 呼び出し情報を元にTrapを送信する。
     * @param invocation 呼び出し情報を
     */
    public void sendExceedThresholdAlarm(Invocation invocation)
    {
        // SNMPTrapを送信しない設定の場合、何もしない
        if (this.sendType == false)
        {
            return;
        }
        
        // 各managerに送信する
        for (String manager : this.managers)
        {
            try
            {
                send(manager, invocation);
            }
            catch (Exception ex)
            {
                JavelinErrorLogger.getInstance().log("電文送信中に予期せぬエラーが発生しました。", ex);
            }
        }
    }
    
    /**
     * 送信先を指定してTrapを送信する
     * @param manager 送信先
     * @param invocation 呼び出し情報
     */
    private void send(String manager, Invocation invocation) throws IOException
    {
        if (SnmpConfig.VERSION_V1.equals(this.version) == true)
        {
            // v1の場合
            
            // Trapの送信先を設定する
            Target target = new CommunityTarget(new UdpAddress(manager + "/" + this.trapPort),
                                                new OctetString(this.trapCommunity));
            target.setVersion(SnmpConstants.version1);
            
            // Trapの内容を設定する
            PDU pdu = createV1Trap(invocation);
            
            // Trapを送信する
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            snmp.send(pdu, target);
        }
        else if (SnmpConfig.VERSION_V2C.equals(this.version) == true)
        {
            // v2cの場合
            
            // Trapの送信先を設定する
            Target target = new CommunityTarget(new UdpAddress(manager + "/" + this.trapPort),
                                                new OctetString(this.trapCommunity));
            target.setVersion(SnmpConstants.version2c);
            
            // Trapの内容を設定する
            PDU pdu = createV2cTrap(invocation);
            
            // Trapを送信する
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            snmp.send(pdu, target);
        }
        else if (SnmpConfig.VERSION_V3.equals(this.version) == true)
        {
            // v3の場合
            // TODO 未実装のため、Trap送信しない
            JavelinErrorLogger.getInstance().log(SnmpConfig.VERSION_KEY + "=" + this.version + "は未対応であるため、Trapを送信しませんでした。");
        }
        else
        {
            // versionが無効値のため、Trap送信しない
            JavelinErrorLogger.getInstance().log(SnmpConfig.VERSION_KEY + "=" + this.version + "は無効値であるため、Trapを送信しませんでした。");
        }
    }
    
    /**
     * 呼び出し情報を元に送信するTrapの内容を設定する(v1)
     * @param invocation 呼び出し情報
     * @return 送信Trap
     * @throws UnknownHostException 本プログラムが稼働しているホストのIPアドレスを取得できない場合
     */
    private PDU createV1Trap(Invocation invocation) throws UnknownHostException
    {
        // v1 Trapのヘッダを設定する
        PDUv1 pdu = new PDUv1();
        pdu.setType(PDU.V1TRAP);
        pdu.setEnterprise(new OID(SnmpConfig.ENTERPRISE_ID));
        pdu.setAgentAddress(new IpAddress(InetAddress.getLocalHost()));
        pdu.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
        pdu.setSpecificTrap(SnmpConfig.SPECIFIC_ID);
        
        // 詳細パラメータを設定する
        return createVariableBindings(pdu, invocation);
    }

    /**
     * 呼び出し情報を元に送信するTrapの内容を設定する(v2c)
     * @param invocation 呼び出し情報
     * @return 送信Trap
     */
    private PDU createV2cTrap(Invocation invocation)
    {
        // v2c Trapのヘッダを設定する
        PDU pdu = new PDU();
        pdu.setType(PDU.NOTIFICATION);
        
        // Trap OIDを設定する
        pdu.add(new VariableBinding(new OID(SnmpConstants.snmpTrapOID),
                                    new OID(SnmpConfig.OID_EXCEED_AlARM_THRESHOLD)));
        
        // 詳細パラメータを設定する
        return createVariableBindings(pdu, invocation);
    }

    /**
     * 呼び出し情報を元に送信するTrapの内容を設定する(v3)
     * @param invocation 呼び出し情報
     * @return 送信Trap
     */
/**
    private PDU createV3Trap(Invocation invocation)
    {
        // v3 Trapのヘッダを設定する
        PDU pdu = new ScopedPDU();
        pdu.setType(PDU.NOTIFICATION);
        
        // Trap OIDを設定する
        pdu.add(new VariableBinding(new OID(SnmpConstants.snmpTrapOID),
                                    new OID(SnmpConfig.OID_EXCEED_AlARM_THRESHOLD)));
        
        // 詳細パラメータを設定する
        return createVariableBindings(pdu, invocation);
    }
*/
    
    /**
     * 指定されたTrap情報に詳細パラメータを設定する
     * @param pdu Trap情報
     * @param invocation 呼び出し情報
     * @return 詳細パラメータを設定したTrap
     */
    private PDU createVariableBindings(PDU pdu, Invocation invocation)
    {
        // EventTime
        pdu.add(new VariableBinding(new OID(SnmpConfig.OID_EVENT_TIME),
                                    new Counter64(System.currentTimeMillis())));
        // ProcessName
        String processName = "";
        if (invocation.getProcessName() != null)
        {
            processName = invocation.getProcessName();
        }
        pdu.add(new VariableBinding(new OID(SnmpConfig.OID_PROCESS_NAME),
                                    new OctetString(processName)));
        // ClassName
        String className = "";
        if (invocation.getClassName() != null)
        {
            className = invocation.getClassName();
        }
        pdu.add(new VariableBinding(new OID(SnmpConfig.OID_CLASS_NAME),
                                    new OctetString(className)));
        // MethodName
        String methodName = "";
        if (invocation.getMethodName() != null)
        {
            methodName = invocation.getMethodName();
        }
        pdu.add(new VariableBinding(new OID(SnmpConfig.OID_METHOD_NAME),
                                    new OctetString(methodName)));
        // Count
        pdu.add(new VariableBinding(new OID(SnmpConfig.OID_COUNT),
                                    new Counter64(invocation.getCount())));
        // Minimum
        pdu.add(new VariableBinding(new OID(SnmpConfig.OID_MINIMUM),
                                    new Counter64(invocation.getMinimum())));
        // Maximum
        pdu.add(new VariableBinding(new OID(SnmpConfig.OID_MAXIMUM),
                                    new Counter64(invocation.getMaximum())));
        // IntervalList(Average)
        pdu.add(new VariableBinding(new OID(SnmpConfig.OID_INTERVAL_LIST),
                                    new Counter64(invocation.getAverage())));
        // ThrowableList(ThrowableCount)
        pdu.add(new VariableBinding(new OID(SnmpConfig.OID_THROWABLE_LIST),
                                    new Counter64(invocation.getThrowableCount())));
        // CallerSet(CallerSetString)
        pdu.add(new VariableBinding(new OID(SnmpConfig.OID_CALLER_SET),
                                    new OctetString(buildCallerSetString(invocation.getAllCallerInvocation()))));
        // RecordThreshold
        pdu.add(new VariableBinding(new OID(SnmpConfig.OID_RECORD_THRESHOLD),
                                    new Counter64(invocation.getRecordThreshold())));
        // AlarmThreshold()
        pdu.add(new VariableBinding(new OID(SnmpConfig.OID_ALARM_THRESHOLD),
                                    new Counter64(invocation.getAlarmThreshold())));
        return pdu;
    }
    
    /**
     * CallerとなるInvocationの配列を文字列化する。<br>
     * 各Invocationオブジェクトは、"クラス名#メソッド名"の形で文字列化する。
     * 配列の要素は","で区切りで並べる。
     * 
     * @param callerSet CallerとなるInvocation配列。
     * @return 各Invocationオブジェクトを文字列化した結果。
     */
    private String buildCallerSetString(Invocation[] callerSet)
    {
        StringBuilder buf = new StringBuilder();

        for (Invocation invocation : callerSet)
        {
            if (buf.length() > 0)
            {
                buf.append(",");
            }
            if (invocation.getClassName() != null)
            {
                buf.append(invocation.getClassName());
            }
            buf.append("#");
            if (invocation.getMethodName() != null)
            {
                buf.append(invocation.getMethodName());
            }
        }
        
        return buf.toString();
    }
}
