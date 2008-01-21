package org.seasar.javelin.statsvision.editors;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.seasar.javelin.statsvision.communicate.Telegram;
import org.seasar.javelin.statsvision.editpart.ComponentEditPart;
import org.seasar.javelin.statsvision.model.ArrowConnectionModel;
import org.seasar.javelin.statsvision.model.ComponentModel;
import org.seasar.javelin.statsvision.model.ContentsModel;
import org.seasar.javelin.statsvision.model.InvocationModel;

public class AbstractStatsVisionEditorTest extends TestCase {
	private String LB = System.getProperty("line.separator");

	private Field pointMap;

	public AbstractStatsVisionEditorTest() throws NoSuchFieldException {
		pointMap = AbstractStatsVisionEditor.class.getDeclaredField("pointMap");
		pointMap.setAccessible(true);
	}

	public void testCreateContent_None() {
		// create expect data
		StringBuilder expected = new StringBuilder();
		expected.append("").append(LB);
		expected.append(0).append(LB);
		expected.append("").append(LB);
		expected.append(Long.MAX_VALUE).append(LB);
		expected.append(Long.MAX_VALUE).append(LB);
		expected.append("TCP").append(LB);
		expected.append("NORMAL").append(LB);

		// assert
		assertEquals(expected.toString(), editor.createContent());
	}

	public void testCreateContent1() {
		// create test data
		editor.setHostName("localhost");
		editor.setPortNum(18000);
		editor.setDomain("domain");
		editor.setWarningThreshold(1000);
		editor.setAlarmThreshold(5000);
		editor.setMode("JMX");
		editor.setLineStyle("MANHATTAN");

		Map<Object, ComponentModel> map = new LinkedHashMap<Object, ComponentModel>();
		editor.setComponentMap(map);

		ComponentModel component1 = new ComponentModel();
		map.put("Class1", component1);
		component1.setClassName("Class1");
		component1.setConstraint(new Rectangle(10, 20, 30, 40));

		InvocationModel invocation = new InvocationModel();
		invocation.setAverage(100);
		invocation.setMaximum(500);
		invocation.setMinimum(10);
		invocation.setThrowableCount(5);
		invocation.setWarningThreshold(1100);
		invocation.setAlarmThreshold(5500);
		invocation.setMethodName("method1-1");
		component1.addInvocation(invocation);

		invocation = new InvocationModel();
		invocation.setAverage(200);
		invocation.setMaximum(600);
		invocation.setMinimum(20);
		invocation.setThrowableCount(10);
		invocation.setWarningThreshold(2200);
		invocation.setAlarmThreshold(6600);
		invocation.setMethodName("method1-2");
		component1.addInvocation(invocation);

		ComponentModel component2 = new ComponentModel();
		map.put("Class2", component2);
		component2.setClassName("Class2");
		component2.setConstraint(new Rectangle(20, 40, 60, 80));

		ArrowConnectionModel connection = new ArrowConnectionModel();
		connection.setSource(component1);
		connection.setTarget(component2);
		component1.addSourceConnection(connection);
		component2.addTargetConnection(connection);

		// create expect data
		StringBuilder expected = new StringBuilder();
		expected.append("localhost").append(LB);
		expected.append(18000).append(LB);
		expected.append("domain").append(LB);
		expected.append(1000).append(LB);
		expected.append(5000).append(LB);
		expected.append("JMX").append(LB);
		expected.append("MANHATTAN").append(LB);
		expected.append("Class1=10,20").append(LB);
		expected.append("<START-OF-METHOD>").append(LB);
		expected.append("100,500,10,5,1100,5500,method1-1").append(LB);
		expected.append("200,600,20,10,2200,6600,method1-2").append(LB);
		expected.append("<END-OF-METHOD>").append(LB);
		expected.append("<START-OF-RELATION>").append(LB);
		expected.append("Class2").append(LB);
		expected.append("<END-OF-RELATION>").append(LB);
		expected.append("Class2=20,40").append(LB);
		expected.append("<START-OF-METHOD>").append(LB);
		expected.append("<END-OF-METHOD>").append(LB);
		expected.append("<START-OF-RELATION>").append(LB);
		expected.append("<END-OF-RELATION>").append(LB);

		// assert
		assertEquals(expected.toString(), editor.createContent());
	}

	public void testLoadContent() throws Exception {
		// create test data
		editor.rootModel = new ContentsModel();
		editor.rootModel.setContentsName("Test Root Model");

		InputStream in = AbstractStatsVisionEditorTest.class
				.getResourceAsStream("AbstractStatsVisionEditorTest_testLoadContent1.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		// execute
		try {
			editor.loadContent(reader);
		} finally {
			reader.close();
		}

		// assert
		assertEquals(2, editor.rootModel.getChildren().size());
		ComponentModel model1 = editor.rootModel.getChildren().get(0);
		ComponentModel model2 = editor.rootModel.getChildren().get(1);

		assertEquals("Class1", model1.getClassName());

		InvocationModel method = model1.getInvocationList().get(0);
		assertEquals("method1-1", method.getMethodName());
		assertEquals(100, method.getAverage());
		assertEquals(500, method.getMaximum());
		assertEquals(10, method.getMinimum());
		assertEquals(5, method.getThrowableCount());
		assertEquals(1100, method.getWarningThreshold());
		assertEquals(5500, method.getAlarmThreshold());

		method = model1.getInvocationList().get(1);
		assertEquals("method1-2", method.getMethodName());
		assertEquals(200, method.getAverage());
		assertEquals(600, method.getMaximum());
		assertEquals(20, method.getMinimum());
		assertEquals(10, method.getThrowableCount());
		assertEquals(2200, method.getWarningThreshold());
		assertEquals(6600, method.getAlarmThreshold());

		Map<Object, Point> pointMap = getPointMap(editor);
		assertEquals(10, pointMap.get("Class1").x);
		assertEquals(20, pointMap.get("Class1").y);

		ArrowConnectionModel arrow = (ArrowConnectionModel) model1
				.getModelSourceConnections().get(0);
		assertTrue(arrow == model2.getModelTargetConnections().get(0));

		assertTrue(arrow.getSource() == model1);
		assertTrue(arrow.getTarget() == model2);

		assertEquals(0, model2.getInvocationList().size());

		assertEquals("Class2", model2.getClassName());
		assertEquals(40, pointMap.get("Class2").x);
		assertEquals(100, pointMap.get("Class2").y);
	}

	private Map<Object, Point> getPointMap(
			AbstractStatsVisionEditor<Object> editor)
			throws IllegalArgumentException, IllegalAccessException {
		return (Map<Object, Point>) pointMap.get(editor);
	}

	private AbstractStatsVisionEditor<Object> editor = new AbstractStatsVisionEditor<Object>() {
		@Override
		protected Object getComponentKey(String className) {
			return className;
		}

		@Override
		public void initializeGraphicalViewer() {
		}

		public void addResponseTelegram(Telegram telegram) {
		}

		public void connect() {
		}

		public void disconnect() {
		}

		public void listeningGraphicalViewer(Telegram telegram) {
		}

		public void reset() {
		}

		public void setBlnReload(boolean blnReload) {
		}

		public void setComponentEditPart(ComponentEditPart componentPart) {
		}

		public void start() {
		}

		public void stop() {
		}
	};
}
