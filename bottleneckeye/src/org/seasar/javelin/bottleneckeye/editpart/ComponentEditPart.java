package org.seasar.javelin.bottleneckeye.editpart;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Color;
import org.seasar.javelin.bottleneckeye.editors.EditPartWithListener;
import org.seasar.javelin.bottleneckeye.editors.StatsVisionDeleteEditPolicy;
import org.seasar.javelin.bottleneckeye.editors.view.AbstractStatsVisionEditor;
import org.seasar.javelin.bottleneckeye.model.AbstractConnectionModel;
import org.seasar.javelin.bottleneckeye.model.ComponentModel;
import org.seasar.javelin.bottleneckeye.model.ComponentType;
import org.seasar.javelin.bottleneckeye.model.InvocationModel;

/**
 * クラスを表す EditPart 。
 */
public class ComponentEditPart extends EditPartWithListener implements NodeEditPart
{

    /** クラスの背景色。 */
    private static final Color               YELLOW                  =
                                                                             new Color(null, 255,
                                                                                       255, 206);

    /** メジャー警告色。 */
    private static final Color               RED                     = ColorConstants.red;

    /** マイナー警告色。 */
    private static final Color               ORANGE                  = new Color(null, 224, 160, 0);

    /** 有効色（呼び出されているクラス／メソッド）。 */
    private static final Color               BLACK                   = ColorConstants.black;

    /** 無効色（呼び出されていないクラス／メソッド）。 */
    private static final Color               GRAY                    = ColorConstants.gray;

    /** コンポーネント名の最大長。これを越える部分は...で表示する。 */
    private static final int                 COMPONENTNAME_MAXLENGTH = 80;

    /** メソッド名の最大長。これを越える部分は...で表示する。 */
    private static final int                 METHODNAME_MAXLENGTH    = 80;

    /** コンポーネント名 */
    private String                           componentName_;

    /** StatsVisionEditor */
    private AbstractStatsVisionEditor<?>     statsVisionEditor_;

    /** ブリンク用 */
    private HashMap<String, Label>           invocationLabelMap_     = new HashMap<String, Label>();

    /** ブリンク用 */
    private HashMap<String, InvocationModel> invocationMap_          =
                                                                             new HashMap<String, InvocationModel>();

    /**
     * クラスの EditPart を生成する。
     *
     * @param statsVisionEditor エディタ
     */
    public ComponentEditPart(AbstractStatsVisionEditor<?> statsVisionEditor)
    {
        this.statsVisionEditor_ = statsVisionEditor;
    }

    /**
     * Viewを作成する。
     * @return View 
     */
    protected IFigure createFigure()
    {
        ComponentModel model = (ComponentModel)getModel();

        LineBorder border = new LineBorder();
        border.setColor(BLACK);

        Layer layer = new Layer();
        layer.setBorder(border);
        layer.setOpaque(true);
        layer.setBackgroundColor(YELLOW);
        layer.setLayoutManager(new ToolbarLayout());

        // コンポーネント名用ラベルの設定。
        Label label = new Label();

        // 背景色を黄色に
        label.setBackgroundColor(YELLOW);

        // 背景色を不透明に
        label.setOpaque(true);

        // 表示文字列の決定
        String className = model.getClassName();
        this.componentName_ = className;
        ComponentType componentType = model.getComponentType();
        String componentText = createComponentText(componentType, className);

        label.setForegroundColor(GRAY);
        for (InvocationModel invocation : model.getInvocationList())
        {
            if (invocation.getMaximum() >= 0)
            {
                label.setForegroundColor(BLACK);
            }
        }

        label.setText(toStr(componentText, COMPONENTNAME_MAXLENGTH));
        layer.add(label);

        CompartmentFigure figure = new CompartmentFigure();
        figure.setBackgroundColor(YELLOW);

        for (InvocationModel invocation : model.getInvocationList())
        {
            Label invocationLabel = new Label();
            invocationLabel.setOpaque(true);
            invocationLabel.setBackgroundColor(YELLOW);
            invocationLabel.setTextAlignment(PositionConstants.ALWAYS_LEFT);
            String methodName = invocation.getMethodName();
            String methodLabelText = createMethodLabelText(invocation);
            invocationLabel.setText(methodLabelText);
            if (invocation.getMaximum() > invocation.getAlarmThreshold())
            {
                invocationLabel.setForegroundColor(RED);
            }
            else if (invocation.getMaximum() > invocation.getWarningThreshold())
            {
                invocationLabel.setForegroundColor(ORANGE);
            }
            else if (invocation.getMaximum() < 0 && invocation.getAverage() == 0)
            {
                invocationLabel.setForegroundColor(GRAY);
            }
            else
            {
                invocationLabel.setForegroundColor(BLACK);
            }

            // 「クラス名、メソッド名」でキーを設定する
            StringBuffer strKeyTemp = new StringBuffer();
            //strKeyTemp.append(model.getClassName());
            strKeyTemp.append(methodName);
            String strKey = strKeyTemp.toString();

            this.invocationLabelMap_.put(strKey, invocationLabel);
            this.invocationMap_.put(strKey, invocation);

            figure.add(invocationLabel);
        }

        layer.add(figure);

        return layer;
    }

    /**
     * コンポーネントのテキストを作成する。
     * @param componentType タイプ：WEB、CLASS､DATABESE
     * @param className クラス名
     * @return コンポーネントのテキスト
     */
    private String createComponentText(ComponentType componentType, String className)
    {
        String componentText = "";
        if (componentType == ComponentType.WEB || componentType == ComponentType.DATABASE)
        {
            componentText = className;
        }
        else
        {
            int lastIndexOf = className.lastIndexOf(".");
            if (lastIndexOf >= 0)
            {
                componentText = className.substring(lastIndexOf + 1);
            }
            else
            {
                componentText = className;
            }
        }
        return componentText;
    }

    /**
     * コンポーネント名を取得する。
     *
     * @return コンポーネント名
     */
    public String getComponentName()
    {
        return this.componentName_;
    }

    /**
     * 指定されたメソッド名に対応するラベルを取得する。
     *
     * @param methodName メソッド名
     * @return ラベル。ラベルがない場合は <code>null</code>
     */
    public Label getMethodLabel(String methodName)
    {
        return this.invocationLabelMap_.get(methodName);
    }

    /**
     * 指定されたメソッド名に対応する Invocation を取得する。
     *
     * @param methodName メソッド名
     * @return Invocation 。見つからない場合は <code>null</code>
     */
    public InvocationModel getInvocationModel(String methodName)
    {
        return this.invocationMap_.get(methodName);
    }

    /**
     * メソッドのラベルに表示するテキストを作成する。
     * @param invocation Invocation
     * @return メソッドのラベル
     */
    private String createMethodLabelText(InvocationModel invocation)
    {
        String methodName;
        methodName = invocation.getMethodName();
        methodName = toStr(methodName, METHODNAME_MAXLENGTH);
        StringBuilder builder = new StringBuilder(methodName);

        builder.append("(");

        long max = invocation.getMaximum();
        if (max < 0)
        {
            builder.append("-");
            builder.append(":");
            builder.append("-");
        }
        else
        {
            builder.append(max);
            builder.append(":");
            long avg = invocation.getAverage();
            builder.append(avg);
        }

        builder.append(")");

        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public ConnectionAnchor getSourceConnectionAnchor(Request request)
    {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * {@inheritDoc}
     */
    public ConnectionAnchor getTargetConnectionAnchor(Request request)
    {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * {@inheritDoc}
     */
    public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection)
    {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * {@inheritDoc}
     */
    public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection)
    {
        return new ChopboxAnchor(getFigure());
    }

    /**
     * 更新
     */
    protected void refreshVisuals()
    {
        // 制約の取得
        Rectangle constraint = ((ComponentModel)getModel()).getConstraint();

        // Rectangleオブジェクトを制約としてビューに設定する
        // setLayoutConstraintメソッドは親EditPartから呼び出す
        ((GraphicalEditPart)getParent()).setLayoutConstraint(this, getFigure(), constraint);
    }

    /**
     * {@inheritDoc}
     */
    protected void createEditPolicies()
    {
        installEditPolicy(EditPolicy.COMPONENT_ROLE,
                          new StatsVisionDeleteEditPolicy(this.statsVisionEditor_));
    }

    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        // 変更の型がモデルの位置情報の変更を示すものかどうか
        if (evt.getPropertyName().equals(ComponentModel.P_CONSTRAINT))
        {
            refreshVisuals(); // ビューを更新する
        }
        else if (ComponentModel.P_SOURCE_CONNECTION.equals(evt.getPropertyName()))
        {
            refreshSourceConnections(); // コネクション・ソースの更新
        }
        else if (ComponentModel.P_TARGET_CONNECTION.equals(evt.getPropertyName()))
        {
            refreshTargetConnections(); // コネクション・ターゲットの更新
        }
        else if (ComponentModel.P_EXCEEDED_THRESHOLD_ALARM.equals(evt.getPropertyName()))
        {
            String methodName = (String)evt.getNewValue();
            this.statsVisionEditor_.exceededThresholdAlarm(this.componentName_, methodName, this);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected List<AbstractConnectionModel> getModelSourceConnections()
    {
        // このEditPartを接続元とするコネクション・モデルのリストを返す
        return ((ComponentModel)getModel()).getModelSourceConnections();
    }

    /**
     * {@inheritDoc}
     */
    protected List<? extends AbstractConnectionModel> getModelTargetConnections()
    {
        // このEditPartを接続先とするコネクション・モデルのリストを返す
        return ((ComponentModel)getModel()).getModelTargetConnections();
    }
    
    /**
     * 結果を表示する。
     * @param result 結果
     * @param length 長さ
     * @return 結果
     */
    public static String toStr(String result, int length)
    {
        // 引数がnullの場合は"null"を返す。
        if (result == null)
        {
            return null;
        }

        if (length == 0)
        {
            result = "";
        }
        else if (result.length() > length)
        {
            result = result.substring(0, length) + "...";
        }

        return result;
    }

    /**
     * 背景色を取得する。
     * @return 背景色
     */
    public Color getBgColor()
    {
        return YELLOW;
    }

    /**
     * ビューの背景色を取得する。
     * @param invocation Invocation
     * @return ビューの背景色
     */
    public Color getFgColor(InvocationModel invocation)
    {
        if (invocation.getMaximum() > invocation.getAlarmThreshold())
        {
            return RED;
        }
        else if (invocation.getMaximum() > invocation.getWarningThreshold())
        {
            return ORANGE;
        }
        else
        {
            return ColorConstants.black;
        }
    }
}
