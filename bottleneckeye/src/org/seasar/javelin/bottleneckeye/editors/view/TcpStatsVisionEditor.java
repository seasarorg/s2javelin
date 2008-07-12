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
 * TCPを利用するStatsVisionエディタ。
 * @author smg
 *
 */
public class TcpStatsVisionEditor extends AbstractStatsVisionEditor<String>
{
    /** TCPデータ取得 */
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
                // サーバと一旦切断する。
                disconnect();
            }
            this.tcpDataGetter_.setHostName(getHostName());
            this.tcpDataGetter_.setPortNumber(getPortNum());

            // サーバに接続する。
            this.tcpDataGetter_.open();

            // 読み込みを開始する。
            this.tcpDataGetter_.startRead();
        }
        catch (Exception objException)
        {
            // 異常があったら、エラーメッセージを表示する
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
        // 接続
        connect();

        // 要求の送信
        this.tcpDataGetter_.request();

        setDirty(true);
    }

    public void reload()
    {
        // 要求の送信
        this.tcpDataGetter_.request();
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        // 切断
        disconnect();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        this.tcpDataGetter_.shutdown();
    }

    /**
     * 電文の受信処理を行う。
     * @param telegram 電文
     */
    public void doAddResponseTelegram(final Telegram telegram)
    {
        doAddResponseTelegramWithoutSetRootModel(telegram);
        getViewer().setContents(this.rootModel);
    }

    /**
     * 電文の受信処理を行う（ rootModel のセットは行わない）。
     *
     * @param telegram 電文
     */
    private void doAddResponseTelegramWithoutSetRootModel(final Telegram telegram)
    {
        // 電文よりモデルを作成する。
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

            // InvocationMapに、該当InvocationModelを設定する
            MainCtrl.getInstance().addInvocationModel(invocation);
            MainCtrl.getInstance().notifyDataChangeListener(invocation);

            // 呼出先に追加する
            target.addInvocation(invocation);
        }

        for (int index = 0; index < invocations.length; index++)
        {
            InvocationModel invocation = invocations[index];
            String className = invocation.getClassName();
            ComponentModel target = getComponentModel(getComponentMap(), this.rootModel, className);

            // 全て呼び出す元を設定用
            Body[] bodies = telegram.getObjBody();
            // データをInvocationModelに設定する
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

                // メソッドの呼び出し元 クラス名
                if (strItemName.equals("allCallerNames") == false)
                {
                    continue;
                }

                Object[] objCallerNames = responseBody.getObjItemValueArr();
                for (int j = 0; j < objCallerNames.length; j++)
                {
                    String callerName = (String)objCallerNames[j];

                    // 自分への呼び出しは無視する。
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
     * 呼び出し元にコンポーネントモデルを追加する。
     * @param target コンポーネントモデル
     * @param callerName 呼び出し元の名前
     */
    private void addOneCaller(ComponentModel target, String callerName)
    {
        // 呼出元に追加する
        ComponentModel source = getComponentModel(getComponentMap(), this.rootModel, callerName);

        // NULL場合、継続
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
     * {@inheritDoc}<br>
     * 状態取得応答電文受信処理。 SWTの描画スレッドに処理を委譲する。
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
            target = componentMap.get(strClassName);
            if (target.isDeleted() == true)
            {
                // ユーザが削除したモデルを復活する
                target.setDeleted(false);
                rootModel.addChild(target);
            }
            else
            {
                // 該当ComponentModelを呼び出し先に設定する
                target = componentMap.get(strClassName);
            }
        }

        return target;
    }

    /**
     * コンストラクタ。
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
     * サーバに電文を送信する。
     *
     * @param telegram 電文
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
