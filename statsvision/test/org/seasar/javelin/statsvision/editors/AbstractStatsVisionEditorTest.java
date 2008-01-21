package org.seasar.javelin.statsvision.editors;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashMap;
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
		StringBuilder expect = new StringBuilder();
		expect.append("").append(LB);
		expect.append(0).append(LB);
		expect.append("").append(LB);
		expect.append(Long.MAX_VALUE).append(LB);
		expect.append(Long.MAX_VALUE).append(LB);
		expect.append("TCP").append(LB);
		expect.append("NORMAL").append(LB);

		// assert
		assertEquals(expect.toString(), editor.createContent());
	}

	public void testCreateContent1() {
		// create test data
		editor.setHostName("localhost");
		editor.setPortNum(18000);
		editor.setDomain("domain");
		editor.setWarningThreshold(1000);
		editor.setAlarmThreshold(5000);
		editor.setMode("JMX");
		editor.setLineStyle("SHORT");

		Map<Object, ComponentModel> map = new HashMap<Object, ComponentModel>();
		editor.setComponentMap(map);

		ComponentModel component1 = new ComponentModel();
		map.put(1, component1);
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

		// create expect data
		StringBuilder expect = new StringBuilder();
		expect.append("localhost").append(LB);
		expect.append(18000).append(LB);
		expect.append("domain").append(LB);
		expect.append(1000).append(LB);
		expect.append(5000).append(LB);
		expect.append("JMX").append(LB);
		expect.append("SHORT").append(LB);
		expect.append("Class1=10,20").append(LB);
		expect.append("<START-OF-METHOD>").append(LB);
		expect.append("100,500,10,5,1100,5500,method1-1").append(LB);
		expect.append("200,600,20,10,2200,6600,method1-2").append(LB);
		expect.append("<END-OF-METHOD>").append(LB);
		expect.append("<START-OF-RELATION>").append(LB);
		expect.append("Class2").append(LB);
		expect.append("<END-OF-RELATION>").append(LB);
		expect.append("Class2=20,40").append(LB);
		expect.append("<START-OF-METHOD>").append(LB);
		expect.append("<END-OF-METHOD>").append(LB);
		expect.append("<START-OF-RELATION>").append(LB);
		expect.append("<END-OF-RELATION>").append(LB);
		expect.append("Class2=20,40").append(LB);
		expect.append("Class1=10,20").append(LB);

		ArrowConnectionModel connection = new ArrowConnectionModel();
		component1.addSourceConnection(connection);

		ComponentModel component2 = new ComponentModel();
		map.put(2, component2);
		connection.setTarget(component2);
		component2.setClassName("Class2");
		component2.setConstraint(new Rectangle(20, 40, 60, 80));

		connection = new ArrowConnectionModel();
		component2.addTargetConnection(connection);
		connection.setTarget(component1);

		// assert
		assertEquals(expect.toString(), editor.createContent());
	}

	public void testLoadContent() throws Exception {
		// create test data
		editor.rootModel = new ContentsModel();
		editor.rootModel.setContentsName("Test Root Model");

		InputStream in = AbstractStatsVisionEditorTest.class
				.getResourceAsStream("AbstractStatsVisionEditorTest_testLoadContent1.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		try {
			editor.loadContent(reader);
		} finally {
			reader.close();
		}

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

		assertEquals("Class2", model2.getClassName());

		assertEquals(0, model2.getInvocationList().size());
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
