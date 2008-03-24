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
 * ���f���̃R���o�[�^�B
 *
 */
public class ModelConverter
{
    /**
     * ComponentModel�̃��X�g���A�i�������f��(View)�ɕϊ�����B
     * @param origComponentList ComponentModel�̃��X�g
     * @return �i�������f��(View)
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
     * ComponentModel�̃��X�g���A�i�������f��(Component)�̃��X�g�ɕϊ�����B
     * @param origComponentList ComponentModel�̃��X�g
     * @return �i�������f��(Component)�̃��X�g
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
     * ComponentModel���A�i�������f��(Component)�ɕϊ�����B
     * @param origComponent ComponentModel
     * @return �i�������f��(Component)
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
     * InvocationModel�̃��X�g���A�i�������f��(Method)�̃��X�g�ɕϊ�����B
     * @param invocationModelList InvocationModel�̃��X�g
     * @return �i�������f��(Method)�̃��X�g
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
     * InvocationModel���A�i�������f��(Method)�ɕϊ�����B
     * @param invocationModel InvocationModel
     * @return �i�������f��(Method)
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
     * ComponentModel�̃��X�g���A�i�������f��(Relation)�̃��X�g�ɕϊ�����B
     * @param origComponentList ComponentModel�̃��X�g
     * @return �i�������f��(Relation)�̃��X�g
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
     * �i�������f��(View)���AContents���f���ɕϊ�����B
     * @param view �i�������f��(View)
     * @return Contents���f��
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
     * �i�������f��(Method)���AInvocationModel�ɕϊ�����B
     * @param method �i�������f��(Method)
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
     * �V�K�ɃR���|�[�l���g���f�����쐬����B
     * @param contents �R���|�[�l���g���f����ǉ�����e���f��
     * @param name �R���|�[�l���g��
     * @return �R���|�[�l���g���f��
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
     * �R���X�g���N�^�B�Ăяo���֎~�B
     */
    private ModelConverter()
    {
        // Do Nothing.
    }
}
