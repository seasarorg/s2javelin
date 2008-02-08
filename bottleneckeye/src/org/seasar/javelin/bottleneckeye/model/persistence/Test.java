package org.seasar.javelin.bottleneckeye.model.persistence;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class Test {
	public static void main(String[] args) throws Exception {
		JAXBContext context = JAXBContext.newInstance(Root.class);
		Marshaller marshaller = context.createMarshaller();
		Unmarshaller unmarshaller = context.createUnmarshaller();

		Root root = new Root();
		List<Component> componentList = new ArrayList<Component>();
		root.setComponents(componentList);

		Component component1 = new Component();
		componentList.add(component1);

		component1.setName("Class1");
		component1.setX(10);
		component1.setY(20);

		List<Method> methodList = new ArrayList<Method>();
		Method method = new Method();
		method.setName("method1");
		methodList.add(method);

		method = new Method();
		method.setName("method2");
		methodList.add(method);

		component1.setMethods(methodList);

		Relation relation = new Relation();
		relation.setSourceName(component1.getName());
		relation.setTargetName(component1.getName());
		List<Relation> relations = new ArrayList<Relation>();
		relations.add(relation);
		root.setRelations(relations);

		StringWriter writer = new StringWriter();
		marshaller.marshal(root, writer);

		System.out.println(writer.toString());

	}
}
