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
 * �Ăяo����������Trap�𑗐M����N���X�B
 * @author tsukano
 */
public class TrapListener implements AlarmListener
{
    /** SNMPTrap�𑗐M���邩�ǂ��� */
    private boolean sendType;
    /** �}�l�[�W�����X�g */
    private String[] managers;
    /** SNMP Trap�|�[�g�ԍ� */
    private int trapPort;
    /** Trap�R�~���j�e�B�� */
    private String trapCommunity;
    /** SNMP Version */
    private String version;
    
    /**
     * SnmpConfig����ݒ����ǂݍ��݁ATrapListener������������B
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
     * �Ăяo����������Trap�𑗐M����B
     * @param invocation �Ăяo������
     */
    public void sendExceedThresholdAlarm(Invocation invocation)
    {
        // SNMPTrap�𑗐M���Ȃ��ݒ�̏ꍇ�A�������Ȃ�
        if (this.sendType == false)
        {
            return;
        }
        
        // �emanager�ɑ��M����
        for (String manager : this.managers)
        {
            try
            {
                send(manager, invocation);
            }
            catch (Exception ex)
            {
                JavelinErrorLogger.getInstance().log("�d�����M���ɗ\�����ʃG���[���������܂����B", ex);
            }
        }
    }
    
    /**
     * ���M����w�肵��Trap�𑗐M����
     * @param manager ���M��
     * @param invocation �Ăяo�����
     */
    private void send(String manager, Invocation invocation) throws IOException
    {
        if (SnmpConfig.VERSION_V1.equals(this.version) == true)
        {
            // v1�̏ꍇ
            
            // Trap�̑��M���ݒ肷��
            Target target = new CommunityTarget(new UdpAddress(manager + "/" + this.trapPort),
                                                new OctetString(this.trapCommunity));
            target.setVersion(SnmpConstants.version1);
            
            // Trap�̓��e��ݒ肷��
            PDU pdu = createV1Trap(invocation);
            
            // Trap�𑗐M����
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            snmp.send(pdu, target);
        }
        else if (SnmpConfig.VERSION_V2C.equals(this.version) == true)
        {
            // v2c�̏ꍇ
            
            // Trap�̑��M���ݒ肷��
            Target target = new CommunityTarget(new UdpAddress(manager + "/" + this.trapPort),
                                                new OctetString(this.trapCommunity));
            target.setVersion(SnmpConstants.version2c);
            
            // Trap�̓��e��ݒ肷��
            PDU pdu = createV2cTrap(invocation);
            
            // Trap�𑗐M����
            TransportMapping transport = new DefaultUdpTransportMapping();
            Snmp snmp = new Snmp(transport);
            snmp.send(pdu, target);
        }
        else if (SnmpConfig.VERSION_V3.equals(this.version) == true)
        {
            // v3�̏ꍇ
            // TODO �������̂��߁ATrap���M���Ȃ�
            JavelinErrorLogger.getInstance().log(SnmpConfig.VERSION_KEY + "=" + this.version + "�͖��Ή��ł��邽�߁ATrap�𑗐M���܂���ł����B");
        }
        else
        {
            // version�������l�̂��߁ATrap���M���Ȃ�
            JavelinErrorLogger.getInstance().log(SnmpConfig.VERSION_KEY + "=" + this.version + "�͖����l�ł��邽�߁ATrap�𑗐M���܂���ł����B");
        }
    }
    
    /**
     * �Ăяo���������ɑ��M����Trap�̓��e��ݒ肷��(v1)
     * @param invocation �Ăяo�����
     * @return ���MTrap
     * @throws UnknownHostException �{�v���O�������ғ����Ă���z�X�g��IP�A�h���X���擾�ł��Ȃ��ꍇ
     */
    private PDU createV1Trap(Invocation invocation) throws UnknownHostException
    {
        // v1 Trap�̃w�b�_��ݒ肷��
        PDUv1 pdu = new PDUv1();
        pdu.setType(PDU.V1TRAP);
        pdu.setEnterprise(new OID(SnmpConfig.ENTERPRISE_ID));
        pdu.setAgentAddress(new IpAddress(InetAddress.getLocalHost()));
        pdu.setGenericTrap(PDUv1.ENTERPRISE_SPECIFIC);
        pdu.setSpecificTrap(SnmpConfig.SPECIFIC_ID);
        
        // �ڍ׃p�����[�^��ݒ肷��
        return createVariableBindings(pdu, invocation);
    }

    /**
     * �Ăяo���������ɑ��M����Trap�̓��e��ݒ肷��(v2c)
     * @param invocation �Ăяo�����
     * @return ���MTrap
     */
    private PDU createV2cTrap(Invocation invocation)
    {
        // v2c Trap�̃w�b�_��ݒ肷��
        PDU pdu = new PDU();
        pdu.setType(PDU.NOTIFICATION);
        
        // Trap OID��ݒ肷��
        pdu.add(new VariableBinding(new OID(SnmpConstants.snmpTrapOID),
                                    new OID(SnmpConfig.OID_EXCEED_AlARM_THRESHOLD)));
        
        // �ڍ׃p�����[�^��ݒ肷��
        return createVariableBindings(pdu, invocation);
    }

    /**
     * �Ăяo���������ɑ��M����Trap�̓��e��ݒ肷��(v3)
     * @param invocation �Ăяo�����
     * @return ���MTrap
     */
/**
    private PDU createV3Trap(Invocation invocation)
    {
        // v3 Trap�̃w�b�_��ݒ肷��
        PDU pdu = new ScopedPDU();
        pdu.setType(PDU.NOTIFICATION);
        
        // Trap OID��ݒ肷��
        pdu.add(new VariableBinding(new OID(SnmpConstants.snmpTrapOID),
                                    new OID(SnmpConfig.OID_EXCEED_AlARM_THRESHOLD)));
        
        // �ڍ׃p�����[�^��ݒ肷��
        return createVariableBindings(pdu, invocation);
    }
*/
    
    /**
     * �w�肳�ꂽTrap���ɏڍ׃p�����[�^��ݒ肷��
     * @param pdu Trap���
     * @param invocation �Ăяo�����
     * @return �ڍ׃p�����[�^��ݒ肵��Trap
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
     * Caller�ƂȂ�Invocation�̔z��𕶎��񉻂���B<br>
     * �eInvocation�I�u�W�F�N�g�́A"�N���X��#���\�b�h��"�̌`�ŕ����񉻂���B
     * �z��̗v�f��","�ŋ�؂�ŕ��ׂ�B
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
