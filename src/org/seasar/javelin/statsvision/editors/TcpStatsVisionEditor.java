package org.seasar.javelin.statsvision.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.seasar.javelin.statsvision.communicate.Body;
import org.seasar.javelin.statsvision.communicate.ResponseBody;
import org.seasar.javelin.statsvision.communicate.TcpDataGetter;
import org.seasar.javelin.statsvision.communicate.Telegram;
import org.seasar.javelin.statsvision.editpart.ComponentEditPart;
import org.seasar.javelin.statsvision.editpart.StatsVisionEditPartFactory;
import org.seasar.javelin.statsvision.model.ArrowConnectionModel;
import org.seasar.javelin.statsvision.model.ComponentModel;
import org.seasar.javelin.statsvision.model.ContentsModel;
import org.seasar.javelin.statsvision.model.InvocationModel;
import org.seasar.javelin.statsvision.model.MainCtrl;

public class TcpStatsVisionEditor extends GraphicalEditor implements StatsVisionEditor {
	private String hostName_ = "";

	private int portNum_ = 0;

	private String domain_ = "";

	public long warningThreshold_ = Long.MAX_VALUE;

	public long alarmThreshold_ = Long.MAX_VALUE;

	/**
	 * 受付電文データ格納先
	 */
	private byte[] byteInputAllDataArr = new byte[0];

	/**
	 * Listeningするとき、初期表示時使うEditPartを持つ
	 */
	public ComponentEditPart componentEditPart = null;

	/**
	 * 赤くブリンクメソッドの統計
	 */
	public static int intExceededThresholdMethodCounter = 0;

	// Componentモデル設定用
	Map<String, ComponentModel> componentMap = new HashMap<String, ComponentModel>();

	// Invocationモデル設定用
	// Map<String, InvocationModel> invocationMap = new HashMap<String,
	// InvocationModel>();

	TcpDataGetter tcpDataGetter;

	GraphicalViewer viewer;

	ContentsModel rootModel;

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#connect()
	 */
	public void connect() {
		try {
			if (tcpDataGetter != null) {
				disconnect();
			}
			tcpDataGetter = new TcpDataGetter();
			this.tcpDataGetter.setStatsJavelinEditor(this);

			// 表示用データを取得する
			tcpDataGetter.open();
			tcpDataGetter.startRead();

		} catch (Exception objException) {
			// 異常があったら、エラーメッセージを表示する
			objException.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#disconnect()
	 */
	public void disconnect() {
		tcpDataGetter.close();
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#initializeGraphicalViewer()
	 */
	public void initializeGraphicalViewer() {

		// Componentモデル設定用
		componentMap = new HashMap<String, ComponentModel>();
		// Invocationモデル設定用
		// invocationMap = new HashMap<String, InvocationModel>();

		// GEFビューを作る
		viewer = getGraphicalViewer();

		// 最上位のモデルの設定
		rootModel = new ContentsModel();
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#start()
	 */
	public void start() {
		// 接続
		connect();

		// 要求の送信
		tcpDataGetter.request();
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#stop()
	 */
	public void stop() {
		// 接続
		disconnect();
	}

	public void addResponseTelegramImpl(final Telegram telegram) {
		// 電文よりモデルを作成する。
		InvocationModel[] invocations = InvocationModel
				.createFromTelegram(telegram,this.alarmThreshold_, this.warningThreshold_);

		for (int index = 0; index < invocations.length; index++) {
			InvocationModel invocation;
			invocation = invocations[index];
			String strClassName = invocation.getClassName();
			ComponentModel target = getComponentModel(componentMap, rootModel,
					strClassName);

			// InvocationMapに、該当InvocationModelを設定する
			MainCtrl.getInstance().addInvocationModel(invocation);
			MainCtrl.getInstance().notifyDataChangeListener(invocation);

			// 呼出先に追加する
			target.addInvocation(invocation);
		}

		for (int index = 0; index < invocations.length; index++) {
			InvocationModel invocation;
			invocation = invocations[index];
			String strClassName = invocation.getClassName();
			ComponentModel target = getComponentModel(componentMap, rootModel,
					strClassName);

			// 全て呼び出す元を設定用
			Body[] bodies = telegram.getObjBody();
			// データをInvocationModelに設定する
			for (int i = 0; i < bodies.length; i++) {
				ResponseBody responseBody = (ResponseBody) bodies[i];
				String strItemName = responseBody.getStrItemName();
				String classMethodName = responseBody.getStrObjName();
				String bodyClassName = classMethodName.substring(0,
						classMethodName.lastIndexOf('.'));
				if (bodyClassName.equals(strClassName) == false) {
					continue;
				}

				// メソッドの呼び出し元 クラス名
				if (strItemName.equals("allCallerNames") == false) {
					continue;
				}

				Object[] objCallerNames = responseBody.getObjItemValueArr();
				for (int j = 0; j < objCallerNames.length; j++) {
					String callerName = (String) objCallerNames[j];
					addOneCaller(target, callerName);
				}
			}
		}

		layoutModel(componentMap);
		viewer.setContents(rootModel);

	}

	private void addOneCaller(ComponentModel target, String callerName) {
		// 呼出元に追加する
		ComponentModel source = getComponentModel(componentMap, rootModel,
				callerName);
		// NULL場合、継続
		if (source == null)
			return;

		// 矢量連続Modelを作る
		ArrowConnectionModel arrow = new ArrowConnectionModel();
		// 呼出元、矢量を追加する
		source.addSourceConnection(arrow);
		// 呼出先、矢量を追加する
		target.addTargetConnection(arrow);
		// 矢量に呼出元を設定する
		arrow.setSource(source);
		// 矢量に呼出先を設定する
		arrow.setTarget(target);
	}

	/**
	 * 状態取得応答電文受信処理。 SWTの描画スレッドに処理を委譲する。
	 * 
	 * @param telegram
	 */
	public void addResponseTelegram(final Telegram telegram) {
		if (telegram.getObjBody().length == 0) {
			return;
		}
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try
				{
					addResponseTelegramImpl(telegram);
				}
				catch(Throwable throwable)
				{
					throwable.printStackTrace();
				}
			}
		});
	}

	/**
	 * Listening表示
	 */
	public void listeningGraphicalViewer(Telegram telegram) {
		if (telegram.getObjBody().length == 0) {
			return;
		}

		// InvocationMapに、該当InvocationModelを設定する
		InvocationModel[] invocations = InvocationModel
				.createFromTelegram(telegram,this.alarmThreshold_, this.warningThreshold_);
		// TODO ハードコーディング
		InvocationModel invocation = invocations[0];
		MainCtrl.getInstance().addInvocationModel(invocation);
		MainCtrl.getInstance().notifyDataChangeListener(invocation);

		// 「クラス名、メソッド名」で赤くブリンクメソッドのキーを取得する
		StringBuffer strKeyTemp = new StringBuffer();
		strKeyTemp.append(invocation.getClassName());
		strKeyTemp.append(invocation.getMethodName());
		String strExceededThresholdMethodName = strKeyTemp.toString();
		
		String rootFlag = strExceededThresholdMethodName.substring(0, 5);
		if (rootFlag.endsWith("ROOT-")) {
			strExceededThresholdMethodName = strExceededThresholdMethodName.substring(5);
		}
		intExceededThresholdMethodCounter++;
		System.out.println("●赤くブリンク●べきなメソッド『"
				+ intExceededThresholdMethodCounter + "』は："
				+ strExceededThresholdMethodName);

		// 赤くブリンクで表示する
		if (componentEditPart != null) {
			try {
				componentEditPart
						.exceededThresholdAlarm(strExceededThresholdMethodName);
			} catch (NullPointerException nullPointerExp) {
				System.out.println("　★⇒　メソッド【" + strExceededThresholdMethodName + "】は表示しているメソッドではありません！");
			}			
		}

	}

	private ComponentModel getComponentModel(
			Map<String, ComponentModel> componentMap, ContentsModel rootModel,
			String strClassName) {
		ComponentModel target;
		if (componentMap.containsKey(strClassName) == false) {
			// クラス名を対応のComponentModelに設定する
			ComponentModel component = new ComponentModel();
			component.setClassName(strClassName);
			component.setConstraint(new Rectangle(0, 0, -1, -1));

			// 最上位のモデルに、該当ComponentModelを追加する
			rootModel.addChild(component);
			// componentMapに、該当componentModelを追加する
			componentMap.put(strClassName, component);
			target = component;
		} else {
			// 該当ComponentModelを呼び出し先に設定する
			target = componentMap.get(strClassName);
		}
		return target;
	}

	Map<ComponentModel, Integer> revRankMap = new HashMap<ComponentModel, Integer>();

	private void layoutModel(Map<String, ComponentModel> componentMap) {
		Map<Integer, List<ComponentModel>> rankMap = new HashMap<Integer, List<ComponentModel>>();
		
		// 先ず、ルートモデルのＲａｎｋを取る
		for (ComponentModel component : componentMap.values()) {			
			String rootFlag = component.getClassName().substring(0, 5);
			if (rootFlag.endsWith("ROOT-")) {
				component.setClassName(component.getClassName().substring(5));
				int rank = getRank(0, component);
				if (rankMap.containsKey(rank)) {
					rankMap.get(rank).add(component);
				} else {
					List<ComponentModel> list = new ArrayList<ComponentModel>();
					list.add(component);
					rankMap.put(rank, list);
				}
				revRankMap.put(component, rank);
			}
		}

		// 全てのモデルのＲａｎｋを取る
		for (ComponentModel component : componentMap.values()) {
			int rank = getRank(0, component);
			if ((rankMap.containsKey(rank)) && (!(rankMap.get(rank).contains(component)))) {
				rankMap.get(rank).add(component);
			} else {
				List<ComponentModel> list = new ArrayList<ComponentModel>();
				list.add(component);
				rankMap.put(rank, list);
			}
			revRankMap.put(component, rank);
		}

		for (int rank : rankMap.keySet()) {
			List<ComponentModel> list = rankMap.get(rank);
			int order = 32;
			for (ComponentModel component : list) {
				System.out.println("class:" + component.getClassName());
				System.out.println("rank:" + rank);
				component.getConstraint().x = rank * 240 + 32;
				component.getConstraint().y = order;
				order = order + component.getInvocationList().size() * 16;
				order = order + 32;
			}
		}
	}

	private int getRank(int rank, ComponentModel component) {
		
		// このモデルを接続先とするコネクションのリストを返す
		List<ArrowConnectionModel> list = component.getModelTargetConnections();
		
		// ルートモデルのＲａｎｋを取る
		if ((revRankMap.size() == 0)) {
			return rank;
		}

		if (revRankMap.containsKey(component)) {
			int currentRank = revRankMap.get(component);
			return currentRank;
		}

		int newRank = rank;
		
		// Ｒａｎｋもう取った呼び出す元から、一番大きいＲａｎｋ値を取って増える
		for (ArrowConnectionModel arrowModel : list) {
			
			if (!(revRankMap.size() == 0) && (!(revRankMap.containsKey(arrowModel.getSource())))) {
				continue;
			}

			if(arrowModel.getSource() == component) {
				continue;
			}
			int aRank = getRank(rank, arrowModel.getSource()) + 1;
			if (aRank > newRank) {
				newRank = aRank;
			}
		}

		return newRank;
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#doSaveAs()
	 */
	public void doSaveAs() {
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	public TcpStatsVisionEditor() {
		setEditDomain(new DefaultEditDomain(this));
	}

	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();

		GraphicalViewer viewer = getGraphicalViewer();
		// EditPartFactoryの作成と設定
		viewer.setEditPartFactory(new StatsVisionEditPartFactory(this));
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#setDomain(java.lang.String)
	 */
	public void setDomain(String domain) {
		domain_ = domain;
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#setHostName(java.lang.String)
	 */
	public void setHostName(String hostName) {
		hostName_ = hostName;
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#setPortNum(int)
	 */
	public void setPortNum(int portNum) {
		portNum_ = portNum;
	}

	// サーバIPを取得する
	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#getHostName()
	 */
	public String getHostName() {
		return hostName_;
	}

	// サーバPortを取得する
	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#getPortNum()
	 */
	public int getPortNum() {
		return portNum_;
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#setWarningThreshold(long)
	 */
	public void setWarningThreshold(long warningThreshold) {
		if (warningThreshold < 1)
			warningThreshold = Long.MAX_VALUE;
		warningThreshold_ = warningThreshold;
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#setAlarmThreshold(long)
	 */
	public void setAlarmThreshold(long alarmThreshold) {
		if (alarmThreshold < 1)
			alarmThreshold = Long.MAX_VALUE;
		alarmThreshold_ = alarmThreshold;
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#setBlnReload(boolean)
	 */
	public void setBlnReload(boolean blnReload) {
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#reset()
	 */
	public void reset() {
		tcpDataGetter.sendReset();
		tcpDataGetter.request();
	}

	public void setComponentEditPart(ComponentEditPart componentPart) {
		this.componentEditPart = componentPart;
	}
}
