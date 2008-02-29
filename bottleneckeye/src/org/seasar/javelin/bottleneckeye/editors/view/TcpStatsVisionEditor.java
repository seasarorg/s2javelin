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
 * TCPを利用するStatsVisionエディタ。
 * @author smg
 *
 */
public class TcpStatsVisionEditor extends AbstractStatsVisionEditor<String>
{
    /** 赤くブリンクメソッドの統計 */
    private static int        intExceededThresholdMethodCounter__ = 0;

    /** Listeningするとき、初期表示時使うEditPartを持つ */
    private ComponentEditPart componentEditPart_                  = null;

    /** TCPデータ取得 */
    private TcpDataGetter     tcpDataGetter_                      = new TcpDataGetter();

    /** 接続状態 */
    private boolean           isConnect_                          = false;

    /** ビューワ */
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

            // 表示用データを取得する
            this.tcpDataGetter_.open();
            this.isConnect_ = true;

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
        // GEFビューを作る
        this.viewer_ = getGraphicalViewer();

        // 最上位のモデルの設定
        this.rootModel = new ContentsModel();
        this.rootModel.setContentsName(getTitle());

        // 位置データの読み込み
        load();

        layoutModel(this.componentMap);

        this.viewer_.setContents(this.rootModel);
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

    /**
     * {@inheritDoc}
     */
    public void stop()
    {
        // 接続
        disconnect();
    }

    /**
     * 電文の受信処理を行う。
     * @param telegram 電文
     */
    public void doAddResponseTelegram(final Telegram telegram)
    {
        // 電文よりモデルを作成する。
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

                int methodIndex = classMethodName.lastIndexOf(InvocationModel.CLASSMETHOD_SEPARATOR);
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

        layoutModel(getComponentMap());
        this.viewer_.setContents(this.rootModel);

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

        // InvocationMapに、該当InvocationModelを設定する
        InvocationModel[] invocations = InvocationModel.createFromTelegram(telegram,
                                                                           this.alarmThreshold_,
                                                                           this.warningThreshold_);

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
        if (rootFlag.endsWith("ROOT-"))
        {
            strExceededThresholdMethodName = strExceededThresholdMethodName.substring(5);
        }

        intExceededThresholdMethodCounter__++;

        // 赤くブリンクで表示する
        if (this.componentEditPart_ != null)
        {
            try
            {
                this.componentEditPart_.exceededThresholdAlarm(strExceededThresholdMethodName);
            }
            catch (NullPointerException nullPointerExp)
            {
                System.out.println("　★⇒　メソッド【" + strExceededThresholdMethodName
                        + "】は表示しているメソッドではありません！");
            }
        }

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
            // 該当ComponentModelを呼び出し先に設定する
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
     * サーバに電文を送信する。
     *
     * @param telegram 電文
     */
    public void sendTelegram(Telegram telegram)
    {
        this.tcpDataGetter_.sendTelegram(telegram);
    }
}
