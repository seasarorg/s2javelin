package org.seasar.javelin.communicate.snmp;

import java.lang.reflect.Method;
import java.net.InetAddress;

import javax.management.ObjectName;

import junit.framework.TestCase;

import org.seasar.javelin.bean.Invocation;
import org.seasar.javelin.communicate.AlarmListener;
import org.seasar.javelin.communicate.snmp.TrapListener;
import org.seasar.javelin.util.JavelinConfigUtil;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;

/**
 * TrapListener�e�X�g�N���X
 * @author tsukano
 */
public class TestTrapListener extends TestCase
{
    /**
     * Test�Ώۂ�AlarmListener�𐶐�����
     * @return Test�Ώۂ�AlarmListener
     */
    private AlarmListener createListener()
    {
        return new TrapListener();
    }
    
    //------------------------------------------------------------
    // Invocation�p�����[�^�̎�������
    /**
     * EVENT_TIME�������Ă��邱�ƁB
     * PROCESS_NAME(notNull�A�󕶎���Anull)
     * CLASS_NAME(notNull�A�󕶎���Anull)
     * METHOD_NAME(notNull�A�󕶎���Anull)
     * COUNT(1�A10�A0)
     * MINIMUM(0�A10)
     * MAXIMUM(0�A10)
     * INTERVAL_LIST(1�A10�A0) = list�̌�
     * THROWABLE_LIST(0�A1�A10) = list�̌�
     * CALLER_SET(0�A1�A3�AclaaName=null�AmethodName=null)
     * RECORD_THRESHOLD(0�A10)
     * ALARM_THRESHOLD(0�A10)
     * 
     * v1�p�����[�^
     * v2c�p�����[�^
     */
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * EVENT_TIME�p�����[�^��PDU�ɐݒ肳��Ă��邱��
     * @throws Exception
     */
    public void testCreateVariableBindings_EVENT_TIME() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
            = new Invocation("pid@host",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.1 = "));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(ProcessName : ����[pid@host])
     * @throws Exception
     */
    public void testCreateVariableBindings_processName_normal() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.2 = pid@host;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(ProcessName : ������[�󕶎���])
     * @throws Exception
     */
    public void testCreateVariableBindings_processName_empty() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.2 = ;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(ProcessName : ������[null])
     * @throws Exception
     */
    public void testCreateVariableBindings_processName_null() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation(null,
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.2 = ;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(ClassName : ����)
     * @throws Exception
     */
    public void testCreateVariableBindings_className_normal() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.5 = RootCallerName;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(ClassName : ������[�󕶎���])
     * @throws Exception
     */
    public void testCreateVariableBindings_className_empty() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.5 = ;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(ClassName : ������[null])
     * @throws Exception
     */
    public void testCreateVariableBindings_className_null() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         null, "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.5 = ;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(MethodName : ����)
     * @throws Exception
     */
    public void testCreateVariableBindings_methodName_normal() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.6 = callerMethod;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(MethodName : ������[�󕶎���])
     * @throws Exception
     */
    public void testCreateVariableBindings_methodName_empty() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.6 = ;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(MethodName : ������[null])
     * @throws Exception
     */
    public void testCreateVariableBindings_methodName_null() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", null, 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.6 = ;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(Count : ����[1])
     * @throws Exception
     */
    public void testCreateVariableBindings_count_1() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.9 = 1;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(Count : ����[10])
     * @throws Exception
     */
    public void testCreateVariableBindings_count_10() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        invocation.addInterval(10);
        invocation.addInterval(10);
        invocation.addInterval(10);
        invocation.addInterval(10);
        invocation.addInterval(10);
        invocation.addInterval(10);
        invocation.addInterval(10);
        invocation.addInterval(10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.9 = 10;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(Count : ������[0])
     * @throws Exception
     */
    public void testCreateVariableBindings_count_0() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.9 = 0;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(Minimum : ����[0])
     * @throws Exception
     */
    public void testCreateVariableBindings_minimum_0() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(0);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.10 = 0;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(Minimum : ����[10])
     * @throws Exception
     */
    public void testCreateVariableBindings_minimum_10() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        invocation.addInterval(20);

        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.10 = 10;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(Maximum : ����[0])
     * @throws Exception
     */
    public void testCreateVariableBindings_maximum_0() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(0);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.11 = 0;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(Maximum : ����[10])
     * @throws Exception
     */
    public void testCreateVariableBindings_maximum_10() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(5);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.11 = 10;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(Average : ����[Interval 1��])
     * @throws Exception
     */
    public void testCreateVariableBindings_interval_1() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.12 = 10;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(Average : ����[Interval 10��])
     * @throws Exception
     */
    public void testCreateVariableBindings_interval_10() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        invocation.addInterval(10);
        invocation.addInterval(10);
        invocation.addInterval(10);
        invocation.addInterval(10);
        invocation.addInterval(30);
        invocation.addInterval(30);
        invocation.addInterval(30);
        invocation.addInterval(30);
        invocation.addInterval(30);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.12 = 20;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(Average : ������[Interval 0��])
     * @throws Exception
     */
    public void testCreateVariableBindings_interval_0() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.12 = 0;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(ThrowableCount : ����[Throwable 0��])
     * @throws Exception
     */
    public void testCreateVariableBindings_throwableCount_0() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.13 = 0;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(ThrowableCount : ����[Throwable 1��])
     * @throws Exception
     */
    public void testCreateVariableBindings_throwableCount_1() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addThrowable(new Throwable());
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.13 = 1;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(ThrowableCount : ����[Throwable 10��])
     * @throws Exception
     */
    public void testCreateVariableBindings_throwableCount_10() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addThrowable(new Throwable());
        invocation.addThrowable(new Throwable());
        invocation.addThrowable(new Throwable());
        invocation.addThrowable(new Throwable());
        invocation.addThrowable(new Throwable());
        invocation.addThrowable(new Throwable());
        invocation.addThrowable(new Throwable());
        invocation.addThrowable(new Throwable());
        invocation.addThrowable(new Throwable());
        invocation.addThrowable(new Throwable());
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.13 = 10;"));
    }


    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(AllCallerInvocation : ����[Caller 0��])
     * @throws Exception
     */
    public void testCreateVariableBindings_allCallerInvocation_0() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.14 = ;"));
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(AllCallerInvocation : ����[Caller 1��])
     * @throws Exception
     */
    public void testCreateVariableBindings_allCallerInvocation_1() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        Invocation invocation1
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "CallerName1", "callerMethod1", 10, 10, 10, 10);
        invocation.addCaller(invocation1);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.14 = CallerName1#callerMethod1;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(AllCallerInvocation : ����[Caller 3��])
     * @throws Exception
     */
    public void testCreateVariableBindings_allCallerInvocation_3() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        Invocation invocation1
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "CallerName1", "callerMethod1", 10, 10, 10, 10);
        invocation.addCaller(invocation1);
        Invocation invocation2
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "CallerName2", "callerMethod2", 10, 10, 10, 10);
        invocation.addCaller(invocation2);
        Invocation invocation3
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "CallerName3", "callerMethod3", 10, 10, 10, 10);
        invocation.addCaller(invocation3);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        // addCaller����Invocation�̃��X�g�͏��s���Ŏ擾����邽��
        // ���e�𕪊�����assert����
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.14 = "));
        assertTrue(result.contains("CallerName1#callerMethod1"));
        assertTrue(result.contains("CallerName2#callerMethod2"));
        assertTrue(result.contains("CallerName3#callerMethod3"));
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(AllCallerInvocation : ������[className��null��Invocation])
     * @throws Exception
     */
    public void testCreateVariableBindings_allCallerInvocation_className_null() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        Invocation invocation1
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         null, "callerMethod1", 10, 10, 10, 10);
        invocation.addCaller(invocation1);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.14 = #callerMethod1;"));
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(AllCallerInvocation : ������[methodName��null��Invocation])
     * @throws Exception
     */
    public void testCreateVariableBindings_allCallerInvocation_methodName_null() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        Invocation invocation1
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "CallerName1", null, 10, 10, 10, 10);
        invocation.addCaller(invocation1);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.14 = CallerName1#;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(RecordThreshold : ����[0])
     * @throws Exception
     */
    public void testCreateVariableBindings_recordThreshold_0() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 0, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.15 = 0;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(RecordThreshold : ����[10])
     * @throws Exception
     */
    public void testCreateVariableBindings_recordThreshold_10() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.15 = 10;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(AlarmThreshold : ����[0])
     * @throws Exception
     */
    public void testCreateVariableBindings_alarmThreshold_1() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 0);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.16 = 0]"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocation�p�����[�^(AlarmThreshold : ����[10])
     * @throws Exception
     */
    public void testCreateVariableBindings_alarmThreshold_10() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.16 = 10]"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createV1Trap(org.seasar.javelin.bean.Invocation)}
     *
     * PDU�̊m�F
     * @throws Exception
     */
    public void testCreateV1Trap() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createV1Trap", Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDUv1 pdu = (PDUv1) method.invoke(listener, invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("V1TRAP"));
        assertTrue(result.contains("enterprise=1.3.6.1.4.1.99999.1.1.2"));
        assertTrue(result.contains("genericTrap=6"));
        assertTrue(result.contains("specificTrap=10"));
        assertEquals(pdu.getAgentAddress().toString(),
                     InetAddress.getLocalHost().getHostAddress());
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createV2cTrap(org.seasar.javelin.bean.Invocation)}
     *
     * PDU�̊m�F
     * @throws Exception
     */
    public void testCreateV2cTrap() throws Exception
    {
        // Invocation�ݒ�
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createV2cTrap", Invocation.class);
        method.setAccessible(true);
        
        // �����Ώۃ��\�b�h�Ăяo��
        PDU pdu = (PDU) method.invoke(listener, invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("TRAP"));
        assertTrue(result.contains("1.3.6.1.6.3.1.1.4.1.0 = 1.3.6.1.4.1.99999.1.1.2.10;"));
    }
    
    //------------------------------------------------------------
    // �v���p�e�B�t�@�C���̎�������
    /**
     * javelin.snmp.sendTrap:true�Afalse�A(�w��Ȃ�)
     * javelin.snmp.managers:1�A2�A�s���A�h���X1/2�A�s���A�h���X2/2�A(�w��Ȃ�)
     * javelin.snmp.trapPort:162�A1620�A-1�A(�w��Ȃ�)
     * javelin.snmp.trapCommunity:public�Axxx�A(�w��Ȃ�)
     * javelin.snmp.version:v1�Av2c�Axxx�A(�w��Ȃ�)
     */
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * localhost��162�ԃ|�[�g�ɃR�~���j�e�B��"public"��v2c��Trap�𑗐M���邱��
     * �v���p�e�B(javelin.snmp.sendTrap      : ����[true])
     * �v���p�e�B(javelin.snmp.managers      : ����[localhost])
     * �v���p�e�B(javelin.snmp.trapPort      : ����[162])
     * �v���p�e�B(javelin.snmp.trapCommunity : ����[public])
     * �v���p�e�B(javelin.snmp.version       : ����[v2c])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_normal() throws Exception
    {
        // Config�ݒ�
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "localhost");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation�ݒ�
        Invocation invocation
            = new Invocation("normal",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
                
        // �����Ώۃ��\�b�h�Ăяo��
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(�p�P�b�g�L���v�`�����̃c�[���Ŋm�F)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * Trap�𑗐M���Ȃ�����
     * �v���p�e�B(javelin.snmp.sendTrap : ����[false])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_sendTrap_false() throws Exception
    {
        // Config�ݒ�
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "false");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "localhost");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation�ݒ�
        Invocation invocation
            = new Invocation("sendTrap_false",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
                
        // �����Ώۃ��\�b�h�Ăяo��
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(�p�P�b�g�L���v�`�����̃c�[���Ŋm�F)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * host1,host2��Trap�𑗐M���邱��
     * �v���p�e�B(javelin.snmp.managers : ����[host1,host2])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_managers_2() throws Exception
    {
        // Config�ݒ�
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "host1,host2");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation�ݒ�
        Invocation invocation
            = new Invocation("managers_2",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
                
        // �����Ώۃ��\�b�h�Ăяo��
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(�p�P�b�g�L���v�`�����̃c�[���Ŋm�F)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * ���݂��Ȃ��A�h���Xng1��Trap�𑗐M���Ȃ����ƁB
     * host2��Trap�𑗐M���邱��
     * �v���p�e�B(javelin.snmp.managers : ����[ng1,host2])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_managers_invalid1() throws Exception
    {
        // Config�ݒ�
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "ng1,host2");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation�ݒ�
        Invocation invocation
            = new Invocation("managers_invalid1",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
                
        // �����Ώۃ��\�b�h�Ăяo��
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(�p�P�b�g�L���v�`�����̃c�[���Ŋm�F)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * ���݂��Ȃ��A�h���Xng1,ng2��Trap�𑗐M���Ȃ����ƁB
     * �v���p�e�B(javelin.snmp.managers : ����[ng1,ng2])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_managers_invalid2() throws Exception
    {
        // Config�ݒ�
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "ng1,ng2");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation�ݒ�
        Invocation invocation
            = new Invocation("managers_invalid2",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
                
        // �����Ώۃ��\�b�h�Ăяo��
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(�p�P�b�g�L���v�`�����̃c�[���Ŋm�F)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * 1620�ԃ|�[�g��Trap�𑗐M���邱��
     * �v���p�e�B(javelin.snmp.trapPort : ����[1620])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_trapPort_1620() throws Exception
    {
        // Config�ݒ�
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "localhost");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "1620");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation�ݒ�
        Invocation invocation
            = new Invocation("trapPort_1620",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
                
        // �����Ώۃ��\�b�h�Ăяo��
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(�p�P�b�g�L���v�`�����̃c�[���Ŋm�F)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * Trap�𑗐M���Ȃ�����
     * �v���p�e�B(javelin.snmp.trapPort : �ُ�[-1])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_trapPort_minus1() throws Exception
    {
        // Config�ݒ�
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "localhost");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "-1");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation�ݒ�
        Invocation invocation
            = new Invocation("trapPort_minus1",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
                
        // �����Ώۃ��\�b�h�Ăяo��
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(�p�P�b�g�L���v�`�����̃c�[���Ŋm�F)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * �R�~���j�e�B��"xxx"��Trap�𑗐M���邱��
     * �v���p�e�B(javelin.snmp.trapCommunity : ����[xxx])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_trapCommunity_xxx() throws Exception
    {
        // Config�ݒ�
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "host1");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "xxx");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation�ݒ�
        Invocation invocation
            = new Invocation("trapCommunity_xxx",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
                
        // �����Ώۃ��\�b�h�Ăяo��
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(�p�P�b�g�L���v�`�����̃c�[���Ŋm�F)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * v1��Trap�𑗐M���邱��
     * �v���p�e�B(javelin.snmp.version : ����[v1])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_version_v1() throws Exception
    {
        // Config�ݒ�
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "host1");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v1");

        // Invocation�ݒ�
        Invocation invocation
            = new Invocation("version_v1",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
                
        // �����Ώۃ��\�b�h�Ăяo��
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(�p�P�b�g�L���v�`�����̃c�[���Ŋm�F)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * Trap�𑗐M���Ȃ�����
     * �v���p�e�B(javelin.snmp.version : �ُ�[xxx])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_version_xxx() throws Exception
    {
        // Config�ݒ�
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "host1");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "xxx");

        // Invocation�ݒ�
        Invocation invocation
            = new Invocation("version_xxx",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
                
        // �����Ώۃ��\�b�h�Ăяo��
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(�p�P�b�g�L���v�`�����̃c�[���Ŋm�F)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * Trap�𑗐M���Ȃ�����
     * �v���p�e�B(javelin.snmp.version : �ُ�[v3])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_version_v3() throws Exception
    {
        // Config�ݒ�
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "host1");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v3");

        // Invocation�ݒ�
        Invocation invocation
            = new Invocation("version_v3",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // �����Ώ�AlarmListener����
        AlarmListener listener = createListener();
                
        // �����Ώۃ��\�b�h�Ăяo��
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(�p�P�b�g�L���v�`�����̃c�[���Ŋm�F)
    }
}
