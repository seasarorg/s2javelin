package org.seasar.javelin.bottleneckeye.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.seasar.javelin.bottleneckeye.model.ArrowConnectionModel;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;
import org.seasar.javelin.bottleneckeye.model.InvocationModel;
import org.seasar.javelin.bottleneckeye.model.persistence.Component;
import org.seasar.javelin.bottleneckeye.model.persistence.Method;
import org.seasar.javelin.bottleneckeye.model.persistence.Relation;
import org.seasar.javelin.bottleneckeye.model.persistence.Root;

public class ModelConverter
{
    /**
     * ContentsModelを、永続化モデル(Root)に変換する。
     * @param origRoot ContentsModel
     * @return 永続化モデル(Root)
     */
    public static Root toPersistenceModel(ContentsModel origRoot)
    {
        if (origRoot == null)
        {
            return null;
        }

        Root root = new Root();

        List<ComponentModel> origComponentList = origRoot.getChildren();
        List<Component> components = toComponents(origComponentList);
        root.setComponents(components);

        List<Relation> relations = toRelations(origComponentList);
        root.setRelations(relations);

        return root;
    }

    /**
     * ComponentModelのリストを、永続化モデル(Component)のリストに変換する。
     * @param origComponentList ComponentModelのリスト
     * @return 永続化モデル(Component)のリスト
     */
    private static List<Component> toComponents(List<ComponentModel> origComponentList)
    {
        List<Component> components = new ArrayList<Component>();
        for (ComponentModel origComponent : origComponentList)
        {
            Component component = toComponent(origComponent);
            components.add(component);

            List<Method> methodList = toMethodList(origComponent.getInvocationList());
            component.setMethods(methodList);
        }
        return components;
    }

    /**
     * ComponentModelを、永続化モデル(Component)に変換する。
     * @param origComponent ComponentModel
     * @return 永続化モデル(Component)
     */
    private static Component toComponent(ComponentModel origComponent)
    {
        Component component = new Component();

        component.setName(origComponent.getClassName());
        component.setX(origComponent.getConstraint().x);
        component.setY(origComponent.getConstraint().y);

        return component;
    }

    /**
     * InvocationModelのリストを、永続化モデル(Method)のリストに変換する。
     * @param invocationModelList InvocationModelのリスト
     * @return 永続化モデル(Method)のリスト
     */
    protected static List<Method> toMethodList(List<InvocationModel> invocationModelList)
    {
        ArrayList<Method> methodList = new ArrayList<Method>();

        for (InvocationModel invocation : invocationModelList)
        {
            Method method = toModel(invocation);
            methodList.add(method);
        }

        return methodList;
    }

    /**
     * InvocationModelを、永続化モデル(Method)に変換する。
     * @param invocationModel InvocationModel
     * @return 永続化モデル(Method)
     */
    private static Method toModel(InvocationModel invocation)
    {
        Method method = new Method();

        method.setName(invocation.getClassName());
        method.setAverage(invocation.getAverage());
        method.setMaximum(invocation.getMaximum());
        method.setMinimum(invocation.getMinimum());
        method.setThrowableCount(invocation.getThrowableCount());
        method.setWarningThreshold(invocation.getWarningThreshold());
        method.setAlarmThreshold(invocation.getAlarmThreshold());

        return method;
    }

    /**
     * ComponentModelのリストを、永続化モデル(Relation)のリストに変換する。
     * @param origComponentList ComponentModelのリスト
     * @return 永続化モデル(Relation)のリスト
     */
    private static List<Relation> toRelations(List<ComponentModel> origComponentList)
    {
        List<Relation> relations = new ArrayList<Relation>();

        for (ComponentModel origComponent : origComponentList)
        {
            List<?> origs = origComponent.getModelSourceConnections();
            for (Object obj : origs)
            {
                ArrowConnectionModel connectionModel = (ArrowConnectionModel)obj;
                ComponentModel targetModel = connectionModel.getTarget();

                Relation relation = new Relation();

                relation.setSourceName(origComponent.getClassName());
                relation.setTargetName(targetModel.getClassName());

                relations.add(relation);
            }
        }

        return relations;
    }

    /**
     * 永続化モデル(Root)を、Contentsモデルに変換する。
     * @param origRoot 永続化モデル(Root)
     * @return Contentsモデル
     */
    public ContentsModel toContentsModel(Root origRoot)
    {
        if (origRoot == null)
        {
            return null;
        }

        Map<String, ComponentModel> tempComponentMap = new HashMap<String, ComponentModel>();

        ContentsModel root = new ContentsModel();

        List<Component> origComponents = origRoot.getComponents();
        for (Component origComponent : origComponents)
        {
            String name = origComponent.getName();

            ComponentModel component = createNewComponent(root, name);
            tempComponentMap.put(name, component);

            List<Method> origMethods = origComponent.getMethods();
            for (Method method : origMethods)
            {
                InvocationModel invocation = toInvocationModel(method);
                component.addInvocation(invocation);
            }
        }

        List<Relation> origRelations = origRoot.getRelations();
        for (Relation origRelation : origRelations)
        {
            ArrowConnectionModel connection = new ArrowConnectionModel();

            String sourceName = origRelation.getSourceName();
            ComponentModel sourceComponent = tempComponentMap.get(sourceName);
            if (sourceComponent == null)
            {
                sourceComponent = createNewComponent(root, sourceName);
            }

            connection.setSource(sourceComponent);
            sourceComponent.addSourceConnection(connection);

            String targetName = origRelation.getTargetName();
            ComponentModel targetComponent = tempComponentMap.get(targetName);
            if (targetComponent == null)
            {
                targetComponent = createNewComponent(root, targetName);
            }

            connection.setTarget(targetComponent);
            targetComponent.addTargetConnection(connection);
        }

        return root;
    }

    /**
     * 永続化モデル(Method)を、InvocationModelに変換する。
     * @param method 永続化モデル(Method)
     * @return InvocationModel
     */
    private InvocationModel toInvocationModel(Method method)
    {
        InvocationModel invocation = new InvocationModel();

        invocation.setMethodName(method.getName());
        invocation.setAverage(method.getAverage());
        invocation.setMaximum(method.getMaximum());
        invocation.setMinimum(method.getMinimum());
        invocation.setThrowableCount(method.getThrowableCount());
        invocation.setWarningThreshold(method.getWarningThreshold());
        invocation.setAlarmThreshold(method.getAlarmThreshold());

        return invocation;
    }

    /**
     * 新規にコンポーネントモデルを作成する。
     * @param root コンポーネントモデルを追加する親モデル
     * @param name コンポーネント名
     * @return コンポーネントモデル
     */
    private ComponentModel createNewComponent(ContentsModel root, String name)
    {
        ComponentModel component = new ComponentModel();
        component.setClassName(name);
        component.setConstraint(new Rectangle(0, 0, -1, -1));
        root.addChild(component);

        return component;
    }

    /**
     * コンストラクタ。呼び出し禁止。
     */
    private ModelConverter()
    {
        // Do Nothing.
    }
}
