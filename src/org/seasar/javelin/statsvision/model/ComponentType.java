package org.seasar.javelin.statsvision.model;


public enum ComponentType {
	WEB, CLASS, DATABASE;

	public static ComponentType getComponentType(String className) {
		if (className.startsWith("/")) {
			return WEB;
		} else if (className.startsWith("jdbc:")) {
			return DATABASE;
		} else {
			return CLASS;
		}
	}

}
