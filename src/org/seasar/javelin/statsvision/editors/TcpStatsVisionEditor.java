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
	 * Listening����Ƃ��A�����\�����g��EditPart������
	 */
	public ComponentEditPart componentEditPart = null;

	/**
	 * �Ԃ��u�����N���\�b�h�̓��v
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
	public void initializeGraphicalViewer()
	{
		// GEF�r���[�����
		viewer = getGraphicalViewer();

		// �ŏ�ʂ̃��f���̐ݒ�
		rootModel = new ContentsModel();
        rootModel.setContentsName(getTitle());
		
		// �ʒu�f�[�^�̓ǂݍ���
		load();
		
        layoutModel(componentMap);
        
        viewer.setContents(rootModel);
	}

	/* (non-Javadoc)
	 * @see org.seasar.javelin.statsvision.editors.StatsVisionEditor#start()
	 */
	public void start() {
		// �ڑ�
		connect();

		// �v���̑��M
		tcpDataGetter.request();
		
		setDirty(true);
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
			ComponentModel target = getComponentModel(
					getComponentMap(), rootModel, strClassName);

			// InvocationMap�ɁA�Y��InvocationModel��ݒ肷��
			MainCtrl.getInstance().addInvocationModel(invocation);
			MainCtrl.getInstance().notifyDataChangeListener(invocation);

			// �ďo��ɒǉ�����
			target.addInvocation(invocation);
		}

		for (int index = 0; index < invocations.length; index++) {
			InvocationModel invocation = invocations[index];
			String          className  = invocation.getClassName();
			ComponentModel  target     = 
				getComponentModel(getComponentMap(), rootModel, className);

			// �S�ČĂяo������ݒ�p
			Body[] bodies = telegram.getObjBody();
			// �f�[�^��InvocationModel�ɐݒ肷��
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

				// ���\�b�h�̌Ăяo���� �N���X��
				if (strItemName.equals("allCallerNames") == false) {
					continue;
				}

				Object[] objCallerNames = responseBody.getObjItemValueArr();
				for (int j = 0; j < objCallerNames.length; j++) {
					String callerName = (String) objCallerNames[j];
					
                    // �����ւ̌Ăяo���͖�������B
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
		// �ďo���ɒǉ�����
		ComponentModel source = getComponentModel(
				getComponentMap(), rootModel, callerName);
		// NULL�ꍇ�A�p��
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
		StringBuilder strKeyTemp = new StringBuilder();
		strKeyTemp.append(invocation.getClassName());
		strKeyTemp.append(invocation.getMethodName());
		String strExceededThresholdMethodName = strKeyTemp.toString();
		
		String rootFlag = strExceededThresholdMethodName.substring(0, 5);
		if (rootFlag.endsWith("ROOT-")) {
			strExceededThresholdMethodName = strExceededThresholdMethodName.substring(5);
		}
		
		intExceededThresholdMethodCounter++;

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
			String strClassName)
	{
		ComponentModel target;
		
		if (componentMap.containsKey(strClassName) == false)
		{
			// �R���|�[�l���g��񂪃�������ɑ��݂��Ȃ��ꍇ�A�V���ɐ�������B
			ComponentModel component = new ComponentModel();
			component.setClassName(strClassName);
			component.setConstraint(new Rectangle(0, 0, -1, -1));

			// �ŏ�ʂ̃��f���ɁA�Y��ComponentModel��ǉ�����
			rootModel.addChild(component);
			// componentMap�ɁA�Y��componentModel��ǉ�����
			componentMap.put(strClassName, component);
			
			target = component;
		}
		else
		{
			// �Y��ComponentModel���Ăяo����ɐݒ肷��
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
