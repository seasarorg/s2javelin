package org.seasar.javelin.statsvision.editors;

import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.seasar.javelin.statsvision.communicate.Body;
import org.seasar.javelin.statsvision.communicate.ResponseBody;
import org.seasar.javelin.statsvision.communicate.TcpDataGetter;
import org.seasar.javelin.statsvision.communicate.Telegram;
import org.seasar.javelin.statsvision.editpart.ComponentEditPart;
import org.seasar.javelin.statsvision.model.ArrowConnectionModel;
import org.seasar.javelin.statsvision.model.ComponentModel;
import org.seasar.javelin.statsvision.model.ContentsModel;
import org.seasar.javelin.statsvision.model.InvocationModel;
import org.seasar.javelin.statsvision.model.MainCtrl;

public class TcpStatsVisionEditor extends AbstractStatsVisionEditor<String> {
	
	/**
	 * Listeningするとき、初期表示時使うEditPartを持つ
	 */
	public ComponentEditPart componentEditPart = null;

	/**
	 * 赤くブリンクメソッドの統計
	 */
	public static int intExceededThresholdMethodCounter = 0;

	TcpDataGetter tcpDataGetter;

	GraphicalViewer viewer;

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
	public void initializeGraphicalViewer()
	{
		// GEFビューを作る
		viewer = getGraphicalViewer();

		// 最上位のモデルの設定
		rootModel = new ContentsModel();
        rootModel.setContentsName(getTitle());
		
		// 位置データの読み込み
		load();
		
        layoutModel(componentMap);
        
        viewer.setContents(rootModel);
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#start()
	 */
	public void start() {
		// 接続
		connect();

		// 要求の送信
		tcpDataGetter.request();
		
		setDirty(true);
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
			ComponentModel target = getComponentModel(
					getComponentMap(), rootModel, strClassName);

			// InvocationMapに、該当InvocationModelを設定する
			MainCtrl.getInstance().addInvocationModel(invocation);
			MainCtrl.getInstance().notifyDataChangeListener(invocation);

			// 呼出先に追加する
			target.addInvocation(invocation);
		}

		for (int index = 0; index < invocations.length; index++) {
			InvocationModel invocation = invocations[index];
			String          className  = invocation.getClassName();
			ComponentModel  target     = 
				getComponentModel(getComponentMap(), rootModel, className);

			// 全て呼び出す元を設定用
			Body[] bodies = telegram.getObjBody();
			// データをInvocationModelに設定する
			for (int i = 0; i < bodies.length; i++) {
				ResponseBody responseBody = (ResponseBody) bodies[i];
				String strItemName = responseBody.getStrItemName();
				String classMethodName = responseBody.getStrObjName();

				int methodIndex = classMethodName.lastIndexOf(InvocationModel.CLASSMETHOD_SEPARATOR);
				if (methodIndex >= 0)
				{
					String bodyClassName = classMethodName.substring(0, methodIndex);
					if (bodyClassName.equals(className) == false) {
						continue;
					}
				}

				// メソッドの呼び出し元 クラス名
				if (strItemName.equals("allCallerNames") == false) {
					continue;
				}

				Object[] objCallerNames = responseBody.getObjItemValueArr();
				for (int j = 0; j < objCallerNames.length; j++) {
					String callerName = (String) objCallerNames[j];
					
                    // 自分への呼び出しは無視する。
                    if(target.getClassName().equals(callerName) == false)
                    {
                        addOneCaller(target, callerName);
                    }
				}
			}
		}

		layoutModel(getComponentMap());
		viewer.setContents(rootModel);

	}

	private void addOneCaller(ComponentModel target, String callerName) {
		// 呼出元に追加する
		ComponentModel source = getComponentModel(
				getComponentMap(), rootModel, callerName);
		// NULL場合、継続
		if (source == null)
		{
			return;
		}

		List<ArrowConnectionModel> sourceList = 
			(List<ArrowConnectionModel>)source.getModelSourceConnections();
		for (ArrowConnectionModel model : sourceList)
		{
			if (target.equals(model.getTarget()))
			{
				return;
			}
		}
		
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
		StringBuilder strKeyTemp = new StringBuilder();
		strKeyTemp.append(invocation.getClassName());
		strKeyTemp.append(invocation.getMethodName());
		String strExceededThresholdMethodName = strKeyTemp.toString();
		
		String rootFlag = strExceededThresholdMethodName.substring(0, 5);
		if (rootFlag.endsWith("ROOT-")) {
			strExceededThresholdMethodName = strExceededThresholdMethodName.substring(5);
		}
		
		intExceededThresholdMethodCounter++;

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
			String strClassName)
	{
		ComponentModel target;
		
		if (componentMap.containsKey(strClassName) == false)
		{
			// コンポーネント情報がメモリ上に存在しない場合、新たに生成する。
			ComponentModel component = new ComponentModel();
			component.setClassName(strClassName);
			component.setConstraint(new Rectangle(0, 0, -1, -1));

			// 最上位のモデルに、該当ComponentModelを追加する
			rootModel.addChild(component);
			// componentMapに、該当componentModelを追加する
			componentMap.put(strClassName, component);
			
			target = component;
		}
		else
		{
			// 該当ComponentModelを呼び出し先に設定する
			target = componentMap.get(strClassName);
		}

		return target;
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

	@Override
	protected String getComponentKey(String className)
	{
		return className;
	}
}
