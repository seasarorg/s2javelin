package org.seasar.javelin.bottleneckeye.editors.view;

import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DefaultEditDomain;
import org.seasar.javelin.bottleneckeye.communicate.Body;
import org.seasar.javelin.bottleneckeye.communicate.ResponseBody;
import org.seasar.javelin.bottleneckeye.communicate.TcpDataGetter;
import org.seasar.javelin.bottleneckeye.communicate.Telegram;
import org.seasar.javelin.bottleneckeye.communicate.TelegramClientManager;
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
    /** TCP�f�[�^�擾 */
    private TcpDataGetter tcpDataGetter_ = new TcpDataGetter();

    /**
     * {@inheritDoc}
     */
    public void connect()
    {
        try
        {
            if (this.tcpDataGetter_ == null)
            {
                // �T�[�o�ƈ�U�ؒf����B
                disconnect();
            }
            this.tcpDataGetter_.setHostName(getHostName());
            this.tcpDataGetter_.setPortNumber(getPortNum());

            // �T�[�o�ɐڑ�����B
            this.tcpDataGetter_.open();

            // �ǂݍ��݂��J�n����B
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
    public boolean isConnected()
    {
        return this.tcpDataGetter_.isConnected();
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect()
    {
        this.tcpDataGetter_.close();
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

    public void reload()
    {
        // �v���̑��M
        this.tcpDataGetter_.request();
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        // �ؒf
        disconnect();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        this.tcpDataGetter_.shutdown();
    }

    /**
     * �d���̎�M�������s���B
     * @param telegram �d��
     */
    public void doAddResponseTelegram(final Telegram telegram)
    {
        doAddResponseTelegramWithoutSetRootModel(telegram);
        getViewer().setContents(this.rootModel);
    }

    /**
     * �d���̎�M�������s���i rootModel �̃Z�b�g�͍s��Ȃ��j�B
     *
     * @param telegram �d��
     */
    private void doAddResponseTelegramWithoutSetRootModel(final Telegram telegram)
    {
        // �d����胂�f�����쐬����B
        InvocationModel[] invocations =
                InvocationModel.createFromTelegram(telegram, getAlarmThreshold(),
                                                   getWarningThreshold());

        for (int index = 0; index < invocations.length; index++)
        {
            InvocationModel invocation;
            invocation = invocations[index];
            String strClassName = invocation.getClassName();

            Map<String, ComponentModel> componentMap = getComponentMap();
            ComponentModel target =
                    getComponentModel(componentMap, this.rootModel, strClassName);

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

                int methodIndex =
                        classMethodName.lastIndexOf(InvocationModel.CLASSMETHOD_SEPARATOR);
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

        layoutModel();
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
        getViewer().getControl().getDisplay().asyncExec(new Runnable() {
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
            target = componentMap.get(strClassName);
            if (target.isDeleted() == true)
            {
                // ���[�U���폜�������f���𕜊�����
                target.setDeleted(false);
                rootModel.addChild(target);
            }
            else
            {
                // �Y��ComponentModel���Ăяo����ɐݒ肷��
                target = componentMap.get(strClassName);
            }
        }

        return target;
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
    public void requestStatus()
    {
        this.tcpDataGetter_.request();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void listeningGraphicalViewer(final Telegram telegram)
    {
        getViewer().getControl().getDisplay().asyncExec(new Runnable() {
            public void run()
            {
                try
                {
                    doAddResponseTelegramWithoutSetRootModel(telegram);
                }
                catch (Throwable throwable)
                {
                    throwable.printStackTrace();
                }
                getViewer().setContents(TcpStatsVisionEditor.this.rootModel);
                TcpStatsVisionEditor.super.listeningGraphicalViewer(telegram);
            }
        });
    }
}
