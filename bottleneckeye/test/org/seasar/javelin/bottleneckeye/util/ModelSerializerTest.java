package org.seasar.javelin.bottleneckeye.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.seasar.javelin.bottleneckeye.model.persistence.Component;
import org.seasar.javelin.bottleneckeye.model.persistence.Method;
import org.seasar.javelin.bottleneckeye.model.persistence.Relation;
import org.seasar.javelin.bottleneckeye.model.persistence.PersistenceModel;
import org.seasar.javelin.bottleneckeye.model.persistence.Settings;
import org.seasar.javelin.bottleneckeye.model.persistence.View;

/**
 * ModelSerializerのテストクラス。
 * @author smg
 *
 */
public class ModelSerializerTest extends TestCase
{
    /**
     * serializeのテストケース
     * @throws Exception
     */
    public void testSerialize()
        throws Exception
    {
        PersistenceModel persistence = new PersistenceModel();

        Settings settings = new Settings();
        persistence.setSettings(settings);
        View view = new View();
        persistence.setView(view);

        List<Component> componentList = new ArrayList<Component>();
        view.setComponents(componentList);
        List<Relation> relations = new ArrayList<Relation>();
        view.setRelations(relations);

        settings.setHostName("host1");
        settings.setPortNum(10000);
        settings.setDomain("domain1");
        settings.setWarningThreshold(1L);
        settings.setAlarmThreshold(2L);
        settings.setMode("mode1");
        settings.setLineStyle("style1");

        Component component1 = new Component();
        component1.setName("Class1");
        component1.setX(11);
        component1.setY(12);
        List<Method> methodList = new ArrayList<Method>();
        component1.setMethods(methodList);
        componentList.add(component1);

        Method method = new Method();
        method.setName("method1");
        methodList.add(method);

        method = new Method();
        method.setName("method2");
        methodList.add(method);

        Component component2 = new Component();
        component2.setName("Class2");
        component2.setX(21);
        component2.setY(22);
        componentList.add(component2);

        Relation relation = new Relation();
        relation.setSourceName(component1.getName());
        relation.setTargetName(component2.getName());
        relations.add(relation);

        String result = new String(ModelSerializer.serialize(persistence));

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><bottleneckEye>"
                + "<settings warningThreshold=\"1\" portNum=\"10000\" mode=\"mode1\""
                + " lineStyle=\"style1\" hostName=\"host1\" domain=\"domain1\""
                + " alarmThreshold=\"2\"/>"
                + "<view><components><component y=\"12\" x=\"11\" name=\"Class1\">"
                + "<method name=\"method1\"/><method name=\"method2\"/></component>"
                + "<component y=\"22\" x=\"21\" name=\"Class2\"/></components>"
                + "<relations><relation targetName=\"Class2\" sourceName=\"Class1\"/></relations>"
                + "</view></bottleneckEye>", result);
        System.out.println(result);
    }

    /**
     * deserializeのテストケース
     * @throws Exception
     */
    public void testDeserialize()
        throws Exception
    {
        String xml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><bottleneckEye>"
                        + "<settings warningThreshold=\"1\" portNum=\"10000\" mode=\"mode1\""
                        + " lineStyle=\"style1\" hostName=\"host1\" domain=\"domain1\""
                        + " alarmThreshold=\"2\"/>"
                        + "<view><components><component y=\"12\" x=\"11\" name=\"Class1\">"
                        + "<method name=\"method1\"/><method name=\"method2\"/></component>"
                        + "<component y=\"22\" x=\"21\" name=\"Class2\"/></components>"
                        + "<relations><relation targetName=\"Class2\" sourceName=\"Class1\"/></relations>"
                        + "</view></bottleneckEye>";
        InputStream in = new ByteArrayInputStream(xml.getBytes());

        PersistenceModel persistence = ModelSerializer.deserialize(in);

        Settings settings = persistence.getSettings();
        assertEquals("host1", settings.getHostName());
        assertEquals(Integer.valueOf(10000), settings.getPortNum());
        assertEquals("domain1", settings.getDomain());
        assertEquals(Long.valueOf(1), settings.getWarningThreshold());
        assertEquals(Long.valueOf(2), settings.getAlarmThreshold());
        assertEquals("mode1", settings.getMode());
        assertEquals("style1", settings.getLineStyle());

        Component component1 = persistence.getView().getComponents().get(0);
        assertEquals("Class1", component1.getName());
        assertEquals(11, component1.getX());
        assertEquals(12, component1.getY());

        Method method1 = component1.getMethods().get(0);
        assertEquals("method1", method1.getName());

        Method method2 = component1.getMethods().get(1);
        assertEquals("method2", method2.getName());

        Component component2 = persistence.getView().getComponents().get(1);
        assertEquals("Class2", component2.getName());
        assertEquals(21, component2.getX());
        assertEquals(22, component2.getY());

        Relation relation = persistence.getView().getRelations().get(0);
        assertEquals("Class1", relation.getSourceName());
        assertEquals("Class2", relation.getTargetName());
    }
}
