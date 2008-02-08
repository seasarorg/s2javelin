package org.seasar.javelin.bottleneckeye.model.persistence;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
public class Root {
	private List<Component> components;

	private List<Relation> relations;

	@XmlElementWrapper(name = "components")
	@XmlElement(name = "component")
	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}

	@XmlElementWrapper(name = "relations")
	@XmlElement(name = "relation")
	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}
}
