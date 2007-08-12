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
	 * ��t�d���f�[�^�i�[��
	 */
	private byte[] byteInputAllDataArr = new byte[0];

	/**
	 * Listening����Ƃ��A�����\�����g��EditPart������
	 */
	public ComponentEditPart componentEditPart = null;

	/**
	 * �Ԃ��u�����N���\�b�h�̓��v
	 */
	public static int intExceededThresholdMethodCounter = 0;

	// Component���f���ݒ�p
	Map<String, ComponentModel> componentMap = new HashMap<String, ComponentModel>();

	// Invocation���f���ݒ�p
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

			// �\���p�f�[�^���擾����
			tcpDataGetter.open();
			tcpDataGetter.startRead();

		} catch (Exception objException) {
			// �ُ킪��������A�G���[���b�Z�[�W��\������
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

		// Component���f���ݒ�p
		componentMap = new HashMap<String, ComponentModel>();
		// Invocation���f���ݒ�p
		// invocationMap = new HashMap<String, InvocationModel>();

		// GEF�r���[�����
		viewer = getGraphicalViewer();

		// �ŏ�ʂ̃��f���̐ݒ�
		rootModel = new ContentsModel();
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#start()
	 */
	public void start() {
		// �ڑ�
		connect();

		// �v���̑��M
		tcpDataGetter.request();
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#stop()
	 */
	public void stop() {
		// �ڑ�
		disconnect();
	}

	public void addResponseTelegramImpl(final Telegram telegram) {
		// �d����胂�f�����쐬����B
		InvocationModel[] invocations = InvocationModel
				.createFromTelegram(telegram,this.alarmThreshold_, this.warningThreshold_);

		for (int index = 0; index < invocations.length; index++) {
			InvocationModel invocation;
			invocation = invocations[index];
			String strClassName = invocation.getClassName();
			ComponentModel target = getComponentModel(componentMap, rootModel,
					strClassName);

			// InvocationMap�ɁA�Y��InvocationModel��ݒ肷��
			MainCtrl.getInstance().addInvocationModel(invocation);
			MainCtrl.getInstance().notifyDataChangeListener(invocation);

			// �ďo��ɒǉ�����
			target.addInvocation(invocation);
		}

		for (int index = 0; index < invocations.length; index++) {
			InvocationModel invocation;
			invocation = invocations[index];
			String strClassName = invocation.getClassName();
			ComponentModel target = getComponentModel(componentMap, rootModel,
					strClassName);

			// �S�ČĂяo������ݒ�p
			Body[] bodies = telegram.getObjBody();
			// �f�[�^��InvocationModel�ɐݒ肷��
			for (int i = 0; i < bodies.length; i++) {
				ResponseBody responseBody = (ResponseBody) bodies[i];
				String strItemName = responseBody.getStrItemName();
				String classMethodName = responseBody.getStrObjName();
				String bodyClassName = classMethodName.substring(0,
						classMethodName.lastIndexOf('.'));
				if (bodyClassName.equals(strClassName) == false) {
					continue;
				}

				// ���\�b�h�̌Ăяo���� �N���X��
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
		// �ďo���ɒǉ�����
		ComponentModel source = getComponentModel(componentMap, rootModel,
				callerName);
		// NULL�ꍇ�A�p��
		if (source == null)
			return;

		// ��ʘA��Model�����
		ArrowConnectionModel arrow = new ArrowConnectionModel();
		// �ďo���A��ʂ�ǉ�����
		source.addSourceConnection(arrow);
		// �ďo��A��ʂ�ǉ�����
		target.addTargetConnection(arrow);
		// ��ʂɌďo����ݒ肷��
		arrow.setSource(source);
		// ��ʂɌďo���ݒ肷��
		arrow.setTarget(target);
	}

	/**
	 * ��Ԏ擾�����d����M�����B SWT�̕`��X���b�h�ɏ������Ϗ�����B
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
	 * Listening�\��
	 */
	public void listeningGraphicalViewer(Telegram telegram) {
		if (telegram.getObjBody().length == 0) {
			return;
		}

		// InvocationMap�ɁA�Y��InvocationModel��ݒ肷��
		InvocationModel[] invocations = InvocationModel
				.createFromTelegram(telegram,this.alarmThreshold_, this.warningThreshold_);
		// TODO �n�[�h�R�[�f�B���O
		InvocationModel invocation = invocations[0];
		MainCtrl.getInstance().addInvocationModel(invocation);
		MainCtrl.getInstance().notifyDataChangeListener(invocation);

		// �u�N���X���A���\�b�h���v�ŐԂ��u�����N���\�b�h�̃L�[���擾����
		StringBuffer strKeyTemp = new StringBuffer();
		strKeyTemp.append(invocation.getClassName());
		strKeyTemp.append(invocation.getMethodName());
		String strExceededThresholdMethodName = strKeyTemp.toString();
		
		String rootFlag = strExceededThresholdMethodName.substring(0, 5);
		if (rootFlag.endsWith("ROOT-")) {
			strExceededThresholdMethodName = strExceededThresholdMethodName.substring(5);
		}
		intExceededThresholdMethodCounter++;
		System.out.println("���Ԃ��u�����N���ׂ��ȃ��\�b�h�w"
				+ intExceededThresholdMethodCounter + "�x�́F"
				+ strExceededThresholdMethodName);

		// �Ԃ��u�����N�ŕ\������
		if (componentEditPart != null) {
			try {
				componentEditPart
						.exceededThresholdAlarm(strExceededThresholdMethodName);
			} catch (NullPointerException nullPointerExp) {
				System.out.println("�@���ˁ@���\�b�h�y" + strExceededThresholdMethodName + "�z�͕\�����Ă��郁�\�b�h�ł͂���܂���I");
			}			
		}

	}

	private ComponentModel getComponentModel(
			Map<String, ComponentModel> componentMap, ContentsModel rootModel,
			String strClassName) {
		ComponentModel target;
		if (componentMap.containsKey(strClassName) == false) {
			// �N���X����Ή���ComponentModel�ɐݒ肷��
			ComponentModel component = new ComponentModel();
			component.setClassName(strClassName);
			component.setConstraint(new Rectangle(0, 0, -1, -1));

			// �ŏ�ʂ̃��f���ɁA�Y��ComponentModel��ǉ�����
			rootModel.addChild(component);
			// componentMap�ɁA�Y��componentModel��ǉ�����
			componentMap.put(strClassName, component);
			target = component;
		} else {
			// �Y��ComponentModel���Ăяo����ɐݒ肷��
			target = componentMap.get(strClassName);
		}
		return target;
	}

	Map<ComponentModel, Integer> revRankMap = new HashMap<ComponentModel, Integer>();

	private void layoutModel(Map<String, ComponentModel> componentMap) {
		Map<Integer, List<ComponentModel>> rankMap = new HashMap<Integer, List<ComponentModel>>();
		
		// �悸�A���[�g���f���̂q�����������
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

		// �S�Ẵ��f���̂q�����������
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
		
		// ���̃��f����ڑ���Ƃ���R�l�N�V�����̃��X�g��Ԃ�
		List<ArrowConnectionModel> list = component.getModelTargetConnections();
		
		// ���[�g���f���̂q�����������
		if ((revRankMap.size() == 0)) {
			return rank;
		}

		if (revRankMap.containsKey(component)) {
			int currentRank = revRankMap.get(component);
			return currentRank;
		}

		int newRank = rank;
		
		// �q����������������Ăяo��������A��ԑ傫���q�������l������đ�����
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
		// EditPartFactory�̍쐬�Ɛݒ�
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

	// �T�[�oIP���擾����
	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#getHostName()
	 */
	public String getHostName() {
		return hostName_;
	}

	// �T�[�oPort���擾����
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
