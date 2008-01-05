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
 * TrapListenerテストクラス
 * @author tsukano
 */
public class TestTrapListener extends TestCase
{
    /**
     * Test対象のAlarmListenerを生成する
     * @return Test対象のAlarmListener
     */
    private AlarmListener createListener()
    {
        return new TrapListener();
    }
    
    //------------------------------------------------------------
    // Invocationパラメータの試験項目
    /**
     * EVENT_TIMEが入っていること。
     * PROCESS_NAME(notNull、空文字列、null)
     * CLASS_NAME(notNull、空文字列、null)
     * METHOD_NAME(notNull、空文字列、null)
     * COUNT(1、10、0)
     * MINIMUM(0、10)
     * MAXIMUM(0、10)
     * INTERVAL_LIST(1、10、0) = listの個数
     * THROWABLE_LIST(0、1、10) = listの個数
     * CALLER_SET(0個、1個、3個、claaName=null、methodName=null)
     * RECORD_THRESHOLD(0、10)
     * ALARM_THRESHOLD(0、10)
     * 
     * v1パラメータ
     * v2cパラメータ
     */
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * EVENT_TIMEパラメータがPDUに設定されていること
     * @throws Exception
     */
    public void testCreateVariableBindings_EVENT_TIME() throws Exception
    {
        // Invocation設定
        Invocation invocation
            = new Invocation("pid@host",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.1 = "));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(ProcessName : 正常[pid@host])
     * @throws Exception
     */
    public void testCreateVariableBindings_processName_normal() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.2 = pid@host;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(ProcessName : 準正常[空文字列])
     * @throws Exception
     */
    public void testCreateVariableBindings_processName_empty() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.2 = ;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(ProcessName : 準正常[null])
     * @throws Exception
     */
    public void testCreateVariableBindings_processName_null() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation(null,
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.2 = ;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(ClassName : 正常)
     * @throws Exception
     */
    public void testCreateVariableBindings_className_normal() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.5 = RootCallerName;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(ClassName : 準正常[空文字列])
     * @throws Exception
     */
    public void testCreateVariableBindings_className_empty() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.5 = ;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(ClassName : 準正常[null])
     * @throws Exception
     */
    public void testCreateVariableBindings_className_null() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         null, "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.5 = ;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(MethodName : 正常)
     * @throws Exception
     */
    public void testCreateVariableBindings_methodName_normal() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.6 = callerMethod;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(MethodName : 準正常[空文字列])
     * @throws Exception
     */
    public void testCreateVariableBindings_methodName_empty() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.6 = ;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(MethodName : 準正常[null])
     * @throws Exception
     */
    public void testCreateVariableBindings_methodName_null() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", null, 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.6 = ;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(Count : 正常[1])
     * @throws Exception
     */
    public void testCreateVariableBindings_count_1() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.9 = 1;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(Count : 正常[10])
     * @throws Exception
     */
    public void testCreateVariableBindings_count_10() throws Exception
    {
        // Invocation設定
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
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.9 = 10;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(Count : 準正常[0])
     * @throws Exception
     */
    public void testCreateVariableBindings_count_0() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.9 = 0;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(Minimum : 正常[0])
     * @throws Exception
     */
    public void testCreateVariableBindings_minimum_0() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(0);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.10 = 0;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(Minimum : 正常[10])
     * @throws Exception
     */
    public void testCreateVariableBindings_minimum_10() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        invocation.addInterval(20);

        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.10 = 10;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(Maximum : 正常[0])
     * @throws Exception
     */
    public void testCreateVariableBindings_maximum_0() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(0);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.11 = 0;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(Maximum : 正常[10])
     * @throws Exception
     */
    public void testCreateVariableBindings_maximum_10() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(5);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.11 = 10;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(Average : 正常[Interval 1個])
     * @throws Exception
     */
    public void testCreateVariableBindings_interval_1() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.12 = 10;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(Average : 正常[Interval 10個])
     * @throws Exception
     */
    public void testCreateVariableBindings_interval_10() throws Exception
    {
        // Invocation設定
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
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.12 = 20;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(Average : 準正常[Interval 0個])
     * @throws Exception
     */
    public void testCreateVariableBindings_interval_0() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.12 = 0;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(ThrowableCount : 正常[Throwable 0個])
     * @throws Exception
     */
    public void testCreateVariableBindings_throwableCount_0() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.13 = 0;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(ThrowableCount : 正常[Throwable 1個])
     * @throws Exception
     */
    public void testCreateVariableBindings_throwableCount_1() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addThrowable(new Throwable());
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.13 = 1;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(ThrowableCount : 正常[Throwable 10個])
     * @throws Exception
     */
    public void testCreateVariableBindings_throwableCount_10() throws Exception
    {
        // Invocation設定
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
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.13 = 10;"));
    }


    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(AllCallerInvocation : 正常[Caller 0個])
     * @throws Exception
     */
    public void testCreateVariableBindings_allCallerInvocation_0() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.14 = ;"));
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(AllCallerInvocation : 正常[Caller 1個])
     * @throws Exception
     */
    public void testCreateVariableBindings_allCallerInvocation_1() throws Exception
    {
        // Invocation設定
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
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.14 = CallerName1#callerMethod1;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(AllCallerInvocation : 正常[Caller 3個])
     * @throws Exception
     */
    public void testCreateVariableBindings_allCallerInvocation_3() throws Exception
    {
        // Invocation設定
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
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        // addCallerしたInvocationのリストは順不同で取得されるため
        // 内容を分割してassertする
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.14 = "));
        assertTrue(result.contains("CallerName1#callerMethod1"));
        assertTrue(result.contains("CallerName2#callerMethod2"));
        assertTrue(result.contains("CallerName3#callerMethod3"));
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(AllCallerInvocation : 準正常[classNameがnullのInvocation])
     * @throws Exception
     */
    public void testCreateVariableBindings_allCallerInvocation_className_null() throws Exception
    {
        // Invocation設定
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
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.14 = #callerMethod1;"));
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(AllCallerInvocation : 準正常[methodNameがnullのInvocation])
     * @throws Exception
     */
    public void testCreateVariableBindings_allCallerInvocation_methodName_null() throws Exception
    {
        // Invocation設定
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
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.14 = CallerName1#;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(RecordThreshold : 正常[0])
     * @throws Exception
     */
    public void testCreateVariableBindings_recordThreshold_0() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 0, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.15 = 0;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(RecordThreshold : 正常[10])
     * @throws Exception
     */
    public void testCreateVariableBindings_recordThreshold_10() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.15 = 10;"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(AlarmThreshold : 正常[0])
     * @throws Exception
     */
    public void testCreateVariableBindings_alarmThreshold_1() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 0);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.16 = 0]"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createVariableBindings(org.snmp4j.PDU, org.seasar.javelin.bean.Invocation)}
     *
     * Invocationパラメータ(AlarmThreshold : 正常[10])
     * @throws Exception
     */
    public void testCreateVariableBindings_alarmThreshold_10() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createVariableBindings", PDU.class, Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, new PDU(), invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("1.3.6.1.4.1.99999.1.1.1.16 = 10]"));
    }

    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#createV1Trap(org.seasar.javelin.bean.Invocation)}
     *
     * PDUの確認
     * @throws Exception
     */
    public void testCreateV1Trap() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createV1Trap", Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
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
     * PDUの確認
     * @throws Exception
     */
    public void testCreateV2cTrap() throws Exception
    {
        // Invocation設定
        Invocation invocation
        = new Invocation("pid@host",
                         new ObjectName("a:type=objName"),
                         new ObjectName("b:type=classObjName"),
                         "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
        Method method = listener.getClass().getDeclaredMethod("createV2cTrap", Invocation.class);
        method.setAccessible(true);
        
        // 試験対象メソッド呼び出し
        PDU pdu = (PDU) method.invoke(listener, invocation);
        
        // assert
        String result = pdu.toString();
        assertTrue(result.contains("TRAP"));
        assertTrue(result.contains("1.3.6.1.6.3.1.1.4.1.0 = 1.3.6.1.4.1.99999.1.1.2.10;"));
    }
    
    //------------------------------------------------------------
    // プロパティファイルの試験項目
    /**
     * javelin.snmp.sendTrap:true、false、(指定なし)
     * javelin.snmp.managers:1つ、2つ、不正アドレス1/2、不正アドレス2/2、(指定なし)
     * javelin.snmp.trapPort:162、1620、-1、(指定なし)
     * javelin.snmp.trapCommunity:public、xxx、(指定なし)
     * javelin.snmp.version:v1、v2c、xxx、(指定なし)
     */
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * localhostの162番ポートにコミュニティ名"public"でv2cのTrapを送信すること
     * プロパティ(javelin.snmp.sendTrap      : 正常[true])
     * プロパティ(javelin.snmp.managers      : 正常[localhost])
     * プロパティ(javelin.snmp.trapPort      : 正常[162])
     * プロパティ(javelin.snmp.trapCommunity : 正常[public])
     * プロパティ(javelin.snmp.version       : 正常[v2c])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_normal() throws Exception
    {
        // Config設定
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "localhost");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation設定
        Invocation invocation
            = new Invocation("normal",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
                
        // 試験対象メソッド呼び出し
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(パケットキャプチャ等のツールで確認)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * Trapを送信しないこと
     * プロパティ(javelin.snmp.sendTrap : 正常[false])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_sendTrap_false() throws Exception
    {
        // Config設定
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "false");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "localhost");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation設定
        Invocation invocation
            = new Invocation("sendTrap_false",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
                
        // 試験対象メソッド呼び出し
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(パケットキャプチャ等のツールで確認)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * host1,host2にTrapを送信すること
     * プロパティ(javelin.snmp.managers : 正常[host1,host2])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_managers_2() throws Exception
    {
        // Config設定
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "host1,host2");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation設定
        Invocation invocation
            = new Invocation("managers_2",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
                
        // 試験対象メソッド呼び出し
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(パケットキャプチャ等のツールで確認)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * 存在しないアドレスng1にTrapを送信しないこと。
     * host2にTrapを送信すること
     * プロパティ(javelin.snmp.managers : 正常[ng1,host2])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_managers_invalid1() throws Exception
    {
        // Config設定
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "ng1,host2");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation設定
        Invocation invocation
            = new Invocation("managers_invalid1",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
                
        // 試験対象メソッド呼び出し
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(パケットキャプチャ等のツールで確認)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * 存在しないアドレスng1,ng2にTrapを送信しないこと。
     * プロパティ(javelin.snmp.managers : 正常[ng1,ng2])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_managers_invalid2() throws Exception
    {
        // Config設定
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "ng1,ng2");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation設定
        Invocation invocation
            = new Invocation("managers_invalid2",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
                
        // 試験対象メソッド呼び出し
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(パケットキャプチャ等のツールで確認)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * 1620番ポートにTrapを送信すること
     * プロパティ(javelin.snmp.trapPort : 正常[1620])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_trapPort_1620() throws Exception
    {
        // Config設定
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "localhost");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "1620");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation設定
        Invocation invocation
            = new Invocation("trapPort_1620",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
                
        // 試験対象メソッド呼び出し
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(パケットキャプチャ等のツールで確認)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * Trapを送信しないこと
     * プロパティ(javelin.snmp.trapPort : 異常[-1])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_trapPort_minus1() throws Exception
    {
        // Config設定
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "localhost");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "-1");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation設定
        Invocation invocation
            = new Invocation("trapPort_minus1",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
                
        // 試験対象メソッド呼び出し
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(パケットキャプチャ等のツールで確認)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * コミュニティ名"xxx"でTrapを送信すること
     * プロパティ(javelin.snmp.trapCommunity : 正常[xxx])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_trapCommunity_xxx() throws Exception
    {
        // Config設定
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "host1");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "xxx");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v2c");

        // Invocation設定
        Invocation invocation
            = new Invocation("trapCommunity_xxx",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
                
        // 試験対象メソッド呼び出し
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(パケットキャプチャ等のツールで確認)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * v1のTrapを送信すること
     * プロパティ(javelin.snmp.version : 正常[v1])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_version_v1() throws Exception
    {
        // Config設定
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "host1");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v1");

        // Invocation設定
        Invocation invocation
            = new Invocation("version_v1",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
                
        // 試験対象メソッド呼び出し
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(パケットキャプチャ等のツールで確認)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * Trapを送信しないこと
     * プロパティ(javelin.snmp.version : 異常[xxx])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_version_xxx() throws Exception
    {
        // Config設定
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "host1");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "xxx");

        // Invocation設定
        Invocation invocation
            = new Invocation("version_xxx",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
                
        // 試験対象メソッド呼び出し
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(パケットキャプチャ等のツールで確認)
    }
    
    /**
     * {@link org.seasar.javelin.communicate.snmp.TrapListener#sendExceedThresholdAlarm(org.seasar.javelin.bean.Invocation)}
     *
     * Trapを送信しないこと
     * プロパティ(javelin.snmp.version : 異常[v3])
     * @throws Exception
     */
    public void testSendExceedThresholdAlarm_version_v3() throws Exception
    {
        // Config設定
        JavelinConfigUtil.getInstance().setString("javelin.snmp.sendTrap", "true");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.managers", "host1");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapPort", "162");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.trapCommunity", "public");
        JavelinConfigUtil.getInstance().setString("javelin.snmp.version", "v3");

        // Invocation設定
        Invocation invocation
            = new Invocation("version_v3",
                             new ObjectName("a:type=objName"),
                             new ObjectName("b:type=classObjName"),
                             "RootCallerName", "callerMethod", 10, 10, 10, 10);
        invocation.addInterval(10);
        
        // 試験対象AlarmListener準備
        AlarmListener listener = createListener();
                
        // 試験対象メソッド呼び出し
        listener.sendExceedThresholdAlarm(invocation);
        
        // assert(パケットキャプチャ等のツールで確認)
    }
}
