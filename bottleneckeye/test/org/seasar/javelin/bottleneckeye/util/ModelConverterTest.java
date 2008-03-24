package org.seasar.javelin.bottleneckeye.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.draw2d.geometry.Rectangle;
import org.seasar.javelin.bottleneckeye.editors.view.TcpStatsVisionEditor;
import org.seasar.javelin.bottleneckeye.model.AbstractConnectionModel;
import org.seasar.javelin.bottleneckeye.model.ArrowConnectionModel;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;
import org.seasar.javelin.bottleneckeye.model.InvocationModel;
import org.seasar.javelin.bottleneckeye.model.persistence.Component;
import org.seasar.javelin.bottleneckeye.model.persistence.Method;
import org.seasar.javelin.bottleneckeye.model.persistence.Relation;
import org.seasar.javelin.bottleneckeye.model.persistence.PersistenceModel;
import org.seasar.javelin.bottleneckeye.model.persistence.View;

/**
 * ModelConverterのテストクラス。
 *
 */
public class ModelConverterTest extends TestCase
{
    /**
     * toPersistenceModelのテストケース。
     */
    public void testToPersistenceModel()
    {
        // prepare
        TcpStatsVisionEditor editor = new TcpStatsVisionEditor();

        ComponentModel model1 = new ComponentModel();
        model1.setClassName("class1");
        model1.setConstraint(new Rectangle(11, 12, 13, 14));
        editor.getComponentMap().put("class1", model1);

        InvocationModel invocation1 = new InvocationModel();
        invocation1.setAverage(11);
        invocation1.setMaximum(12);
        invocation1.setMinimum(13);
        invocation1.setThrowableCount(14);
        invocation1.setWarningThreshold(15);
        invocation1.setAlarmThreshold(16);
        invocation1.setMethodName("method1");
        model1.addInvocation(invocation1);

        InvocationModel invocation2 = new InvocationModel();
        invocation2.setAverage(21);
        invocation2.setMaximum(22);
        invocation2.setMinimum(23);
        invocation2.setThrowableCount(24);
        invocation2.setWarningThreshold(25);
        invocation2.setAlarmThreshold(26);
        invocation2.setMethodName("method2");
        model1.addInvocation(invocation2);

        ComponentModel model2 = new ComponentModel();
        model2.setClassName("class2");
        model2.setConstraint(new Rectangle(21, 22, 23, 24));
        editor.getComponentMap().put("class2", model2);

        ArrowConnectionModel connection = new ArrowConnectionModel();
        connection.setSource(model1);
        connection.setTarget(model2);
        connection.attachSource();
        connection.attachTarget();

        // excute
//      PersistenceModel persistence = ModelConverter.toPersistenceModel(editor);
      PersistenceModel persistence = null;

        // assert
        Component component1 = persistence.getView().getComponents().get(0);
        assertEquals("class1", component1.getName());
        assertEquals(11, component1.getX());
        assertEquals(12, component1.getY());

        Method method1 = component1.getMethods().get(0);
        assertEquals(Long.valueOf(11), method1.getAverage());
        assertEquals(Long.valueOf(12), method1.getMaximum());
        assertEquals(Long.valueOf(13), method1.getMinimum());
        assertEquals(Long.valueOf(14), method1.getThrowableCount());
        assertEquals(Long.valueOf(15), method1.getWarningThreshold());
        assertEquals(Long.valueOf(16), method1.getAlarmThreshold());
        assertEquals("method1", method1.getName());

        Method method2 = component1.getMethods().get(1);
        assertEquals(Long.valueOf(21), method2.getAverage());
        assertEquals(Long.valueOf(22), method2.getMaximum());
        assertEquals(Long.valueOf(23), method2.getMinimum());
        assertEquals(Long.valueOf(24), method2.getThrowableCount());
        assertEquals(Long.valueOf(25), method2.getWarningThreshold());
        assertEquals(Long.valueOf(26), method2.getAlarmThreshold());
        assertEquals("method2", method2.getName());

        Component component2 = persistence.getView().getComponents().get(1);
        assertEquals("class2", component2.getName());
        assertEquals(21, component2.getX());
        assertEquals(22, component2.getY());

        Relation relation = persistence.getView().getRelations().get(0);
        assertEquals("class1", relation.getSourceName());
        assertEquals("class2", relation.getTargetName());
    }

    /**
     * toContentsModelのテストケース。
     */
    public void testToContentsModel()
    {
        // prepare
//        PersistenceModel persistence = new PersistenceModel();
//        View view = new View();
//        persistence.setView(view);
//
//        List<Component> components = new ArrayList<Component>();
//        view.setComponents(components);
//        List<Relation> relations = new ArrayList<Relation>();
//        view.setRelations(relations);
//
//        Component component1 = new Component();
//        component1.setName("class1");
//        component1.setX(11);
//        component1.setY(12);
//        List<Method> methods = new ArrayList<Method>();
//        component1.setMethods(methods);
//        components.add(component1);
//
//        Method method1 = new Method();
//        method1.setAverage(11L);
//        method1.setMaximum(12L);
//        method1.setMinimum(13L);
//        method1.setThrowableCount(14L);
//        method1.setWarningThreshold(15L);
//        method1.setAlarmThreshold(16L);
//        method1.setName("method1");
//        methods.add(method1);
//
//        Method method2 = new Method();
//        method2.setAverage(21L);
//        method2.setMaximum(22L);
//        method2.setMinimum(23L);
//        method2.setThrowableCount(24L);
//        method2.setWarningThreshold(25L);
//        method2.setAlarmThreshold(26L);
//        method2.setName("method2");
//        methods.add(method2);
//
//        Component component2 = new Component();
//        component2.setName("class2");
//        component2.setX(21);
//        component2.setY(22);
//        components.add(component2);
//
//        Relation relation = new Relation();
//        relation.setSourceName("class1");
//        relation.setTargetName("class2");
//        relations.add(relation);
//
//        // excute
//        ContentsModel contents = ModelConverter.toContentsModel(persistence);
//
//        // assert
//        ComponentModel model1 = contents.getChildren().get(0);
//        assertEquals("class1", model1.getClassName());
//        assertEquals(11, model1.getConstraint().x);
//        assertEquals(12, model1.getConstraint().y);
//
//        InvocationModel invocation1 = model1.getInvocationList().get(0);
//        assertEquals(11L, invocation1.getAverage());
//        assertEquals(12L, invocation1.getMaximum());
//        assertEquals(13L, invocation1.getMinimum());
//        assertEquals(14L, invocation1.getThrowableCount());
//        assertEquals(15L, invocation1.getWarningThreshold());
//        assertEquals(16L, invocation1.getAlarmThreshold());
//        assertEquals("method1", invocation1.getMethodName());
//
//        InvocationModel invocation2 = model1.getInvocationList().get(1);
//        assertEquals(21L, invocation2.getAverage());
//        assertEquals(22L, invocation2.getMaximum());
//        assertEquals(23L, invocation2.getMinimum());
//        assertEquals(24L, invocation2.getThrowableCount());
//        assertEquals(25L, invocation2.getWarningThreshold());
//        assertEquals(26L, invocation2.getAlarmThreshold());
//        assertEquals("method2", invocation2.getMethodName());
//
//        ComponentModel model2 = contents.getChildren().get(1);
//        assertEquals("class2", model2.getClassName());
//        assertEquals(21, model2.getConstraint().x);
//        assertEquals(22, model2.getConstraint().y);
//
//        AbstractConnectionModel connection =
//                contents.getChildren().get(0).getModelSourceConnections().get(0);
//        assertEquals(model1, connection.getSource());
//        assertEquals(model2, connection.getTarget());
    }

    /**
     * toContentsModelのテストケース。
     */
    public void testToContentsModel_blankPersistence()
    {
//        // prepare
//        PersistenceModel persistence = new PersistenceModel();
//
//        // excute
//        ContentsModel contents = ModelConverter.toContentsModel(persistence);
//
//        // assert
//        assertEquals(0, contents.getChildren().size());
    }
}
