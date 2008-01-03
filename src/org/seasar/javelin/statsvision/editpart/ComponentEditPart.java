package org.seasar.javelin.statsvision.editpart;

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
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.seasar.javelin.statsvision.editors.EditPartWithListener;
import org.seasar.javelin.statsvision.model.ComponentModel;
import org.seasar.javelin.statsvision.model.InvocationModel;

public class ComponentEditPart
    extends EditPartWithListener implements NodeEditPart
{
    /** クラスの背景色。 */
    private static final Color YELLOW = new Color(null, 255, 255, 206);

    /** メジャー警告色。 */
    private static final Color RED    = ColorConstants.red;

    /** マイナー警告色。 */
    private static final Color ORANGE = new Color(null, 224, 160, 0);

    /** 有効色（呼び出されているクラス／メソッド）。 */
    private static final Color BLACK   = ColorConstants.black;
    
    /** 無効色（呼び出されていないクラス／メソッド）。 */
    private static final Color GRAY   = ColorConstants.gray;

    /** コンポーネント名の最大長。これを越える部分は...で表示する。 */
    private static final int COMPONENTNAME_MAXLENGTH = 80;

    /** メソッド名の最大長。これを越える部分は...で表示する。 */
    private static final int METHODNAME_MAXLENGTH    = 80;

    private static HashMap<String, Label> invocationLabelMap
        = new HashMap<String, Label>();

    private static HashMap<String, InvocationModel> invocationMap
        = new HashMap<String, InvocationModel>();

    protected IFigure createFigure()
    {
        ComponentModel model = (ComponentModel)getModel();

        LineBorder border = new LineBorder();
        border.setColor(BLACK);
        
        Layer      layer  = new Layer();
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
        String className     = model.getClassName();
        String componentName = "";
        if (className.indexOf("/") > -1 || className.indexOf("@") > -1)
        {
            componentName = className;
        }
        else
        {
            componentName = className.substring(className.lastIndexOf(".") + 1);
        }
        
        label.setForegroundColor(GRAY);
        for (InvocationModel invocation : model.getInvocationList())
        {
            if (invocation.getMaximum() >= 0)
            {
                label.setForegroundColor(BLACK);
            }
        }
        
        label.setText(toStr(componentName, COMPONENTNAME_MAXLENGTH));
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
            else if (invocation.getMaximum() < 0
                    && invocation.getAverage() == 0)
            {
                invocationLabel.setForegroundColor(GRAY);
            }
            else
            {
                invocationLabel.setForegroundColor(BLACK);
            }

            // 「クラス名、メソッド名」でキーを設定する
            StringBuffer strKeyTemp = new StringBuffer();
            strKeyTemp.append(model.getClassName());
            strKeyTemp.append(methodName);
            String strKey = strKeyTemp.toString();

            invocationLabelMap.put(strKey, invocationLabel);
            invocationMap.put(strKey, invocation);

            figure.add(invocationLabel);
        }

        layer.add(figure);

        return layer;
    }

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

    public ConnectionAnchor getSourceConnectionAnchor(Request request)
    {
        return new ChopboxAnchor(getFigure());
    }

    public ConnectionAnchor getTargetConnectionAnchor(Request request)
    {
        return new ChopboxAnchor(getFigure());
    }

    public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection)
    {
        return new ChopboxAnchor(getFigure());
    }

    public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection)
    {
        return new ChopboxAnchor(getFigure());
    }

    protected void refreshVisuals()
    {
        // 制約の取得
        Rectangle constraint = ((ComponentModel)getModel()).getConstraint();

        // Rectangleオブジェクトを制約としてビューに設定する
        // setLayoutConstraintメソッドは親EditPartから呼び出す
        ((GraphicalEditPart)getParent()).setLayoutConstraint(this, getFigure(), constraint);
    }

    protected void createEditPolicies()
    {}

    public void propertyChange(PropertyChangeEvent evt)
    {
        // 変更の型がモデルの位置情報の変更を示すものかどうか
        if (evt.getPropertyName().equals(ComponentModel.P_CONSTRAINT))
        {
            refreshVisuals(); // ビューを更新する
        }
        else if (evt.getPropertyName().equals(ComponentModel.P_SOURCE_CONNECTION))
        {
            refreshSourceConnections(); // コネクション・ソースの更新
        }
        else if (evt.getPropertyName().equals(ComponentModel.P_TARGET_CONNECTION))
        {
            refreshTargetConnections(); // コネクション・ターゲットの更新
        }
        else if (evt.getPropertyName().equals(ComponentModel.P_EXCEEDED_THRESHOLD_ALARM))
        {
            String methodName = (String)evt.getNewValue();
            exceededThresholdAlarm(methodName);
        }
    }

    protected List getModelSourceConnections()
    {
        // このEditPartを接続元とするコネクション・モデルのリストを返す
        return ((ComponentModel)getModel()).getModelSourceConnections();
    }

    protected List getModelTargetConnections()
    {
        // このEditPartを接続先とするコネクション・モデルのリストを返す
        return ((ComponentModel)getModel()).getModelTargetConnections();
    }

    public void exceededThresholdAlarm(String classmethodName)
    {
        Label label = invocationLabelMap.get(classmethodName);

        InvocationModel invocation = invocationMap.get(classmethodName);

        EditPartViewer viewer = getViewer();
        if (viewer == null)
        {
            // TODO ERIGUCHI ログ
            return;
        }
        Control control = viewer.getControl();
        if (control == null)
        {
            // TODO ERIGUCHI ログ
            return;
        }
        Display display = control.getDisplay();

        String methodLabelText = createMethodLabelText(invocation);

        display.asyncExec(new LabelUpdateJob(label, methodLabelText));

        Runnable blinkJob = new Blinker(display, label, ColorConstants.black, RED,
                                        getFgColor(invocation), getBgColor(invocation));
        Thread blinker = new Thread(blinkJob);
        blinker.start();

        ((ComponentModel)getModel()).setExceededThresholdAlarm(null);
    }

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

    private Color getBgColor(InvocationModel invocation)
    {
        return YELLOW;
    }

    private Color getFgColor(InvocationModel invocation)
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
