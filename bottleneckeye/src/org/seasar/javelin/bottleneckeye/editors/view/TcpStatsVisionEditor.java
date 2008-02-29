package org.seasar.javelin.bottleneckeye.editors.view;

import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.seasar.javelin.bottleneckeye.communicate.Body;
import org.seasar.javelin.bottleneckeye.communicate.ResponseBody;
import org.seasar.javelin.bottleneckeye.communicate.TcpDataGetter;
import org.seasar.javelin.bottleneckeye.communicate.Telegram;
import org.seasar.javelin.bottleneckeye.communicate.TelegramClientManager;
import org.seasar.javelin.bottleneckeye.editpart.ComponentEditPart;
import org.seasar.javelin.bottleneckeye.model.AbstractConnectionModel;
import org.seasar.javelin.bottleneckeye.model.ArrowConnectionModel;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ContentsModel;
import org.seasar.javelin.bottleneckeye.model.InvocationModel;
import org.seasar.javelin.bottleneckeye.model.MainCtrl;

/**
 * TCP�𗘗p����StatsVision�G�f�B�^�B
 * @author smg
 *
 */
public class TcpStatsVisionEditor extends AbstractStatsVisionEditor<String>
{
    /** �Ԃ��u�����N���\�b�h�̓��v */
    private static int        intExceededThresholdMethodCounter__ = 0;

    /** Listening����Ƃ��A�����\�����g��EditPart������ */
    private ComponentEditPart componentEditPart_                  = null;

    /** TCP�f�[�^�擾 */
    private TcpDataGetter     tcpDataGetter_                      = new TcpDataGetter();

    /** �ڑ���� */
    private boolean           isConnect_                          = false;

    /** �r���[�� */
    private GraphicalViewer   viewer_;

    /**
     * {@inheritDoc}
     */
    public void connect()
    {
        try
        {
            if (this.isConnect_ == true)
            {
                disconnect();
            }
            this.tcpDataGetter_.setHostName(getHostName());
            this.tcpDataGetter_.setPortNumber(getPortNum());

            // �\���p�f�[�^���擾����
            this.tcpDataGetter_.open();
            this.isConnect_ = true;

            this.tcpDataGetter_.startRead();

        }
        catch (Exception objException)
        {
            // �ُ킪��������A�G���[���b�Z�[�W��\������
            objException.printStackTrace();
        }

    }

    /**
     * {@inheritDoc}
     */
    public void disconnect()
    {
        this.tcpDataGetter_.close();
        this.isConnect_ = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeGraphicalViewer()
    {
        // GEF�r���[�����
        this.viewer_ = getGraphicalViewer();

        // �ŏ�ʂ̃��f���̐ݒ�
        this.rootModel = new ContentsModel();
        this.rootModel.setContentsName(getTitle());

        // �ʒu�f�[�^�̓ǂݍ���
        load();

        layoutModel(this.componentMap);

        this.viewer_.setContents(this.rootModel);
    }

    /**
     * {@inheritDoc}
     */
    public void start()
    {
        // �ڑ�
        connect();

        // �v���̑��M
        this.tcpDataGetter_.request();

        setDirty(true);
    }

    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        // �ڑ�
        disconnect();
    }

    /**
     * �d���̎�M�������s���B
     * @param telegram �d��
     */
    public void doAddResponseTelegram(final Telegram telegram)
    {
        // �d����胂�f�����쐬����B
        InvocationModel[] invocations = InvocationModel.createFromTelegram(telegram,
                                                                           this.alarmThreshold_,
                                                                           this.warningThreshold_);

        for (int index = 0; index < invocations.length; index++)
        {
            InvocationModel invocation;
            invocation = invocations[index];
            String strClassName = invocation.getClassName();
            ComponentModel target = getComponentModel(getComponentMap(), this.rootModel,
                                                      strClassName);

            // InvocationMap�ɁA�Y��InvocationModel��ݒ肷��
            MainCtrl.getInstance().addInvocationModel(invocation);
            MainCtrl.getInstance().notifyDataChangeListener(invocation);

            // �ďo��ɒǉ�����
            target.addInvocation(invocation);
        }

        for (int index = 0; index < invocations.length; index++)
        {
            InvocationModel invocation = invocations[index];
            String className = invocation.getClassName();
            ComponentModel target = getComponentModel(getComponentMap(), this.rootModel, className);

            // �S�ČĂяo������ݒ�p
            Body[] bodies = telegram.getObjBody();
            // �f�[�^��InvocationModel�ɐݒ肷��
            for (int i = 0; i < bodies.length; i++)
            {
                ResponseBody responseBody = (ResponseBody)bodies[i];
                String strItemName = responseBody.getStrItemName();
                String classMethodName = responseBody.getStrObjName();

                int methodIndex = classMethodName.lastIndexOf(InvocationModel.CLASSMETHOD_SEPARATOR);
                if (methodIndex >= 0)
                {
                    String bodyClassName = classMethodName.substring(0, methodIndex);
                    if (bodyClassName.equals(className) == false)
                    {
                        continue;
                    }
                }

                // ���\�b�h�̌Ăяo���� �N���X��
                if (strItemName.equals("allCallerNames") == false)
                {
                    continue;
                }

                Object[] objCallerNames = responseBody.getObjItemValueArr();
                for (int j = 0; j < objCallerNames.length; j++)
                {
                    String callerName = (String)objCallerNames[j];

                    // �����ւ̌Ăяo���͖�������B
                    if (target.getClassName().equals(callerName) == false)
                    {
                        addOneCaller(target, callerName);
                    }
                }
            }
        }

        layoutModel(getComponentMap());
        this.viewer_.setContents(this.rootModel);

    }

    /**
     * �Ăяo�����ɃR���|�[�l���g���f����ǉ�����B
     * @param target �R���|�[�l���g���f��
     * @param callerName �Ăяo�����̖��O
     */
    private void addOneCaller(ComponentModel target, String callerName)
    {
        // �ďo���ɒǉ�����
        ComponentModel source = getComponentModel(getComponentMap(), this.rootModel, callerName);

        // NULL�ꍇ�A�p��
        if (source == null)
        {
            return;
        }

        List<AbstractConnectionModel> sourceList = source.getModelSourceConnections();
        for (AbstractConnectionModel model : sourceList)
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
     * {@inheritDoc}<br>
     * ��Ԏ擾�����d����M�����B SWT�̕`��X���b�h�ɏ������Ϗ�����B
     */
    public void addResponseTelegram(final Telegram telegram)
    {
        if (telegram.getObjBody().length == 0)
        {
            return;
        }
        this.viewer_.getControl().getDisplay().asyncExec(new Runnable() {
            public void run()
            {
                try
                {
                    doAddResponseTelegram(telegram);
                }
                catch (Throwable throwable)
                {
                    throwable.printStackTrace();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void listeningGraphicalViewer(Telegram telegram)
    {
        if (telegram.getObjBody().length == 0)
        {
            return;
        }

        // InvocationMap�ɁA�Y��InvocationModel��ݒ肷��
        InvocationModel[] invocations = InvocationModel.createFromTelegram(telegram,
                                                                           this.alarmThreshold_,
                                                                           this.warningThreshold_);

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
        if (rootFlag.endsWith("ROOT-"))
        {
            strExceededThresholdMethodName = strExceededThresholdMethodName.substring(5);
        }

        intExceededThresholdMethodCounter__++;

        // �Ԃ��u�����N�ŕ\������
        if (this.componentEditPart_ != null)
        {
            try
            {
                this.componentEditPart_.exceededThresholdAlarm(strExceededThresholdMethodName);
            }
            catch (NullPointerException nullPointerExp)
            {
                System.out.println("�@���ˁ@���\�b�h�y" + strExceededThresholdMethodName
                        + "�z�͕\�����Ă��郁�\�b�h�ł͂���܂���I");
            }
        }

    }

    private ComponentModel getComponentModel(Map<String, ComponentModel> componentMap,
            ContentsModel rootModel, String strClassName)
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveAs()
    {
        // Do Nothing.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSaveAsAllowed()
    {
        return false;
    }

    /**
     * �R���X�g���N�^�B
     */
    public TcpStatsVisionEditor()
    {
        setEditDomain(new DefaultEditDomain(this));
    }

    /**
     * {@inheritDoc}
     */
    public void setBlnReload(boolean blnReload)
    {
        // Do Nothing.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset()
    {
        super.reset();
        this.tcpDataGetter_.sendReset();
        this.tcpDataGetter_.request();
    }

    /**
     * {@inheritDoc}
     */
    public void setComponentEditPart(ComponentEditPart componentPart)
    {
        this.componentEditPart_ = componentPart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getComponentKey(String className)
    {
        return className;
    }

    /**
     * {@inheritDoc}
     */
    public TelegramClientManager getTelegramClientManager()
    {
        return this.tcpDataGetter_;
    }

    /**
     * �T�[�o�ɓd���𑗐M����B
     *
     * @param telegram �d��
     */
    public void sendTelegram(Telegram telegram)
    {
        this.tcpDataGetter_.sendTelegram(telegram);
    }
}
