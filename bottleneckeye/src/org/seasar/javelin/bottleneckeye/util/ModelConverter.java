package org.seasar.javelin.bottleneckeye.util;

import java.util.ArrayList;
import java.util.Collection;
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
import org.seasar.javelin.bottleneckeye.model.persistence.View;

/**
 * モデルのコンバータ。
 *
 */
public class ModelConverter
{
    /**
     * ComponentModelのリストを、永続化モデル(View)に変換する。
     * @param origComponentList ComponentModelのリスト
     * @return 永続化モデル(View)
     */
    public static View toView(Collection<ComponentModel> origComponentList)
    {
        View view = new View();

        List<Component> components = ModelConverter.toComponents(origComponentList);
        view.setComponents(components);

        List<Relation> relations = ModelConverter.toRelations(origComponentList);
        view.setRelations(relations);

        return view;
    }

    /**
     * ComponentModelのリストを、永続化モデル(Component)のリストに変換する。
     * @param origComponentList ComponentModelのリスト
     * @return 永続化モデル(Component)のリスト
     */
    public static List<Component> toComponents(Collection<ComponentModel> origComponentList)
    {
        List<Component> components = new ArrayList<Component>();

        if (origComponentList == null)
        {
            return components;
        }

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
    private static List<Method> toMethodList(List<InvocationModel> invocationModelList)
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

        method.setName(invocation.getMethodName());
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
    public static List<Relation> toRelations(Collection<ComponentModel> origComponentList)
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
     * 永続化モデル(View)を、Contentsモデルに変換する。
     * @param view 永続化モデル(View)
     * @return Contentsモデル
     */
    public static ContentsModel toContentsModel(View view)
    {
        if (view == null)
        {
            return null;
        }
        
        ContentsModel contents = new ContentsModel();
        if (view.getComponents() == null)
        {
            return contents;
        }

        List<Component> origComponents = view.getComponents();
        Map<String, ComponentModel> tempComponentMap = new HashMap<String, ComponentModel>();

        for (Component origComponent : origComponents)
        {
            String name = origComponent.getName();

            ComponentModel component = createNewComponent(contents, name);
            component.getConstraint().x = origComponent.getX();
            component.getConstraint().y = origComponent.getY();
            tempComponentMap.put(name, component);

            List<Method> origMethods = origComponent.getMethods();

            if (origMethods == null)
            {
                continue;
            }

            for (Method method : origMethods)
            {
                InvocationModel invocation = toInvocationModel(method);
                component.addInvocation(invocation);
            }
        }

        List<Relation> origRelations = view.getRelations();

        if (origRelations == null)
        {
            return contents;
        }

        for (Relation origRelation : origRelations)
        {
            ArrowConnectionModel connection = new ArrowConnectionModel();

            String sourceName = origRelation.getSourceName();
            ComponentModel sourceComponent = tempComponentMap.get(sourceName);
            if (sourceComponent == null)
            {
                sourceComponent = createNewComponent(contents, sourceName);
            }

            connection.setSource(sourceComponent);
            sourceComponent.addSourceConnection(connection);

            String targetName = origRelation.getTargetName();
            ComponentModel targetComponent = tempComponentMap.get(targetName);
            if (targetComponent == null)
            {
                targetComponent = createNewComponent(contents, targetName);
            }

            connection.setTarget(targetComponent);
            targetComponent.addTargetConnection(connection);
        }

        return contents;
    }

    /**
     * 永続化モデル(Method)を、InvocationModelに変換する。
     * @param method 永続化モデル(Method)
     * @return InvocationModel
     */
    private static InvocationModel toInvocationModel(Method method)
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
     * @param contents コンポーネントモデルを追加する親モデル
     * @param name コンポーネント名
     * @return コンポーネントモデル
     */
    private static ComponentModel createNewComponent(ContentsModel contents, String name)
    {
        ComponentModel component = new ComponentModel();
        component.setClassName(name);
        component.setConstraint(new Rectangle(0, 0, -1, -1));
        contents.addChild(component);

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
