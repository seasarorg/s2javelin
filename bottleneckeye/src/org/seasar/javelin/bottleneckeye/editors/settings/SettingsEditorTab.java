package org.seasar.javelin.bottleneckeye.editors.settings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.seasar.javelin.bottleneckeye.editors.MultiPageEditor;
import org.seasar.javelin.bottleneckeye.editors.view.StatsVisionEditor;
import org.seasar.javelin.bottleneckeye.model.MainCtrl;

/**
 * 設定タブ。
 *
 * @author Sakamoto
 */
public class SettingsEditorTab
{
    // TODO: 通信モジュールを切り出す。
    /** 親のMultiPageEditor */
    private MultiPageEditor   multiPageEditor_;

    /** StatsVisionEditor */
    private StatsVisionEditor statsVisionEditor_;

    /** ホスト名を入力するテキストフィールド */
    private Text              hostText_;

    /** ポート番号を入力するテキストフィールド */
    private Text              portText_;

    /** ドメインを入力するテキストフィールド */
    private Text              domainText_;

    /** 警告閾値を入力するテキストフィールド */
    private Text              warningText_;

    /** アラーム閾値を入力するテキストフィールド */
    private Text              alarmText_;

    /** 通信モードを選択するコンボボックス */
    private Combo             modeCombo_;

    /** 線のスタイルを選択するコンボボックス */
    private Combo             lineStyleCombo_;

    /** "Reload" ボタン */
    private Button            reloadButton_;

    /** "Reset" ボタン */
    private Button            resetButton_;

    /** "Start" ボタン */
    private Button            startButton_;

    /** "Stop" ボタン */
    private Button            stopButton_;

    /**
     * コンストラクタ。関連するEditorを設定する。
     * @param multiPageEditor 親となるマルチページエディタ。
     * @param statsVisionEditor 通信部分を持つStatsVisionエディタ。
     */
    public SettingsEditorTab(MultiPageEditor multiPageEditor, StatsVisionEditor statsVisionEditor)
    {
        this.multiPageEditor_ = multiPageEditor;
        this.statsVisionEditor_ = statsVisionEditor;
    }

    /**
     * タブの中身を作成する（コンポジット）。
     *
     * @param container 親コンポジット
     * @param editorPart タブを生成するエディタ
     * @return 画面インスタンス
     */
    public Composite createComposite(Composite container, MultiPageEditorPart editorPart)
    {
        Composite composite = new Composite(container, SWT.NONE);
        GridLayout layout = new GridLayout(12, true);
        composite.setLayout(layout);

        createLabel(composite, "Host:");
        this.hostText_ = createText(composite, 6, "");
        createSpacer(composite, 4);

        createLabel(composite, "Port:");
        this.portText_ = createText(composite, 6, Integer.toString(0));
        createSpacer(composite, 4);

        createLabel(composite, "Domain:");
        this.domainText_ = createText(composite, 10, "");

        createLabel(composite, "Warning:");
        this.warningText_ = createText(composite, 6, Long.toString(0));
        createSpacer(composite, 4);

        createLabel(composite, "Alarm:");
        this.alarmText_ = createText(composite, 6, Long.toString(0));
        createSpacer(composite, 4);

        String[] values;
        values = new String[]{MultiPageEditor.MODE_TCP, MultiPageEditor.MODE_JMX};

        createLabel(composite, "Mode:");
        this.modeCombo_ = createCombo(composite, 4, values, MultiPageEditor.MODE_TCP);
        createSpacer(composite, 6);

        values = new String[]{MultiPageEditor.LINE_STYLE_NORMAL,
                MultiPageEditor.LINE_STYLE_SHORTEST, MultiPageEditor.LINE_STYLE_FAN,
                MultiPageEditor.LINE_STYLE_MANHATTAN};

        createLabel(composite, "Style:");
        this.lineStyleCombo_ = createCombo(composite, 4, values, "");
        createSpacer(composite, 6);

        this.reloadButton_ = createButton(composite, "Reload", false);
        this.resetButton_ = createButton(composite, "Reset", false);
        this.startButton_ = createButton(composite, "Start", true);
        this.stopButton_ = createButton(composite, "Stop", false);
        createSpacer(composite, 4);

        final Button printButton = createButton(composite, "Print", true);
        final Button copyButton = createButton(composite, "Copy", true);
        // ------------------------------------------------------------

        this.hostText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String hostName = SettingsEditorTab.this.hostText_.getText();
                SettingsEditorTab.this.statsVisionEditor_.setHostName(hostName);
            }
        });

        this.portText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String portText = SettingsEditorTab.this.portText_.getText();
                int port = Integer.parseInt(portText);
                SettingsEditorTab.this.statsVisionEditor_.setPortNum(port);
            }
        });

        this.domainText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String domain = SettingsEditorTab.this.domainText_.getText();
                SettingsEditorTab.this.statsVisionEditor_.setDomain(domain);
            }
        });

        this.warningText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String thresholdText = SettingsEditorTab.this.warningText_.getText();
                long threshold = Long.parseLong(thresholdText);
                SettingsEditorTab.this.statsVisionEditor_.setWarningThreshold(threshold);
            }
        });

        this.alarmText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String thresholdText = SettingsEditorTab.this.alarmText_.getText();
                long threshold = Long.parseLong(thresholdText);
                SettingsEditorTab.this.statsVisionEditor_.setAlarmThreshold(threshold);
            }
        });

        this.modeCombo_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String mode = SettingsEditorTab.this.modeCombo_.getText();
                SettingsEditorTab.this.statsVisionEditor_.setMode(mode);
            }
        });

        this.lineStyleCombo_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String lineStyle = SettingsEditorTab.this.lineStyleCombo_.getText();
                SettingsEditorTab.this.statsVisionEditor_.setLineStyle(lineStyle);
            }
        });

        // ------------------------------------------------------------
        this.reloadButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                notifyAllSettings();
                SettingsEditorTab.this.multiPageEditor_.notifyReload();
            }
        });

        // ------------------------------------------------------------
        this.resetButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                try
                {
                    notifyAllSettings();
                    SettingsEditorTab.this.multiPageEditor_.notifyReset();
                }
                catch (Exception ex)
                {
                    // ignore
                    ex.printStackTrace();
                }
            }
        });

        // ------------------------------------------------------------
        this.startButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                notifyAllSettings();

                SettingsEditorTab.this.startButton_.setEnabled(false);
                SettingsEditorTab.this.stopButton_.setEnabled(true);
                SettingsEditorTab.this.resetButton_.setEnabled(true);
                SettingsEditorTab.this.reloadButton_.setEnabled(true);
                SettingsEditorTab.this.hostText_.setEnabled(false);
                SettingsEditorTab.this.portText_.setEnabled(false);
                SettingsEditorTab.this.domainText_.setEnabled(false);
                SettingsEditorTab.this.warningText_.setEnabled(false);
                SettingsEditorTab.this.alarmText_.setEnabled(false);
                SettingsEditorTab.this.modeCombo_.setEnabled(false);
                SettingsEditorTab.this.lineStyleCombo_.setEnabled(false);

                SettingsEditorTab.this.multiPageEditor_.notifyStart();
            }
        });

        // ------------------------------------------------------------
        this.stopButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                SettingsEditorTab.this.startButton_.setEnabled(true);
                SettingsEditorTab.this.stopButton_.setEnabled(false);
                SettingsEditorTab.this.resetButton_.setEnabled(false);
                SettingsEditorTab.this.reloadButton_.setEnabled(false);
                SettingsEditorTab.this.hostText_.setEnabled(true);
                SettingsEditorTab.this.portText_.setEnabled(true);
                SettingsEditorTab.this.domainText_.setEnabled(true);
                SettingsEditorTab.this.warningText_.setEnabled(true);
                SettingsEditorTab.this.alarmText_.setEnabled(true);
                SettingsEditorTab.this.modeCombo_.setEnabled(true);
                SettingsEditorTab.this.lineStyleCombo_.setEnabled(true);

                SettingsEditorTab.this.multiPageEditor_.notifyStop();
            }
        });

        // ------------------------------------------------------------
        printButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                SettingsEditorTab.this.multiPageEditor_.notifyPrint();
            }
        });

        // ------------------------------------------------------------
        copyButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                SettingsEditorTab.this.multiPageEditor_.notifyCopy();
            }
        });

        return composite;
    }

    /**
     * タブの名前を返す。
     *
     * @return タブ名
     */
    public String getName()
    {
        return "Settings";
    }

    /**
     * アラーム閾値をセットする。
     *
     * @param threshold 閾値
     */
    public void setAlarmThreshold(long threshold)
    {
        this.alarmText_.setText(String.valueOf(threshold));
    }

    /**
     * ドメインをセットする。
     *
     * @param domain ドメイン
     */
    public void setDomain(String domain)
    {
        this.domainText_.setText(domain);
    }

    /**
     * ホスト名をセットする。
     *
     * @param hostName ホスト名
     */
    public void setHostName(String hostName)
    {
        this.hostText_.setText(hostName);
    }

    /**
     * 線の種類をセットする。
     *
     * @param lineStyle 線の種類
     */
    public void setLineStyle(String lineStyle)
    {
        this.lineStyleCombo_.setText(lineStyle);
    }

    /**
     * 通信モードをセットする。
     *
     * @param mode 通信モード
     */
    public void setMode(String mode)
    {
        this.modeCombo_.setText(mode);
    }

    /**
     * ポート番号をセットする。
     *
     * @param port ポート番号
     */
    public void setPortNum(int port)
    {
        this.portText_.setText(String.valueOf(port));
    }

    /**
     * 警告閾値をセットする。
     *
     * @param threshold 閾値
     */
    public void setWarningThreshold(long threshold)
    {
        this.warningText_.setText(String.valueOf(threshold));
    }

    /**
     * 設定をビューに通知する。
     */
    private void notifyAllSettings()
    {
        this.statsVisionEditor_.setAlarmThreshold(Long.valueOf(this.alarmText_.getText()));
        this.statsVisionEditor_.setDomain(this.domainText_.getText());
        this.statsVisionEditor_.setHostName(this.hostText_.getText());
        this.statsVisionEditor_.setLineStyle(this.lineStyleCombo_.getText());
        this.statsVisionEditor_.setMode(this.modeCombo_.getText());
        this.statsVisionEditor_.setPortNum(Integer.valueOf(this.portText_.getText()));
        this.statsVisionEditor_.setWarningThreshold(Long.valueOf(this.warningText_.getText()));
    }

    /**
     * 設定画面用のラベルを生成する。
     *
     * @param composite ラベルを挿入するコンポジット
     * @param text ラベルテキスト
     * @return ラベル
     */
    private Label createLabel(Composite composite, String text)
    {
        GridData grid = new GridData(GridData.BEGINNING);
        grid.horizontalSpan = 2;

        Label label = new Label(composite, SWT.NONE);
        label.setText(text);
        label.setLayoutData(grid);

        return label;
    }

    /**
     * 設定画面用のテキストフィールドを生成する。
     *
     * @param composite テキストフィールドを挿入するコンポジット
     * @param span 幅
     * @param value 初期テキスト
     * @return テキストボックス
     */
    private Text createText(Composite composite, int span, String value)
    {
        GridData grid = new GridData(GridData.BEGINNING);
        grid.horizontalSpan = span;
        grid.horizontalAlignment = GridData.FILL;

        Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
        text.setText(value);
        text.setLayoutData(grid);

        return text;
    }

    /**
     * 設定画面用のボタンを生成する。
     *
     * @param composite ボタンを挿入するコンポジット
     * @param text ボタンのテキスト
     * @param isEnabled ボタンを有効にする場合は <code>true</code>
     * @return ボタン
     */
    private Button createButton(Composite composite, String text, boolean isEnabled)
    {
        GridData grid = new GridData(GridData.BEGINNING);
        grid.horizontalSpan = 2;
        grid.horizontalAlignment = GridData.FILL;

        Button button = new Button(composite, SWT.NONE);
        button.setEnabled(isEnabled);
        button.setLayoutData(grid);
        button.setText(text);

        return button;
    }

    /**
     * 設定画面用のコンボボックスを生成する。
     *
     * @param composite コンボボックスを挿入するコンポジット
     * @param span スパン
     * @param values 項目値
     * @param value 選択する項目
     * @return コンボボックス
     */
    private Combo createCombo(Composite composite, int span, String[] values, String value)
    {
        GridData grid = new GridData(GridData.BEGINNING);
        grid.horizontalSpan = span;
        grid.horizontalAlignment = GridData.FILL;

        Combo combo = new Combo(composite, SWT.READ_ONLY);
        combo.setLayoutData(grid);

        for (String item : values)
        {
            combo.add(item);
        }
        combo.setText(value);

        return combo;
    }

    /**
     * 設定画面用のスペーサを生成する。
     *
     * @param composite スペーサを挿入するコンポジット
     * @param space スペースの大きさ
     */
    private void createSpacer(Composite composite, int space)
    {
        GridData spacerGrid;
        spacerGrid = new GridData(GridData.BEGINNING);
        spacerGrid.horizontalSpan = space;

        Label spacerLabel;
        spacerLabel = new Label(composite, SWT.NONE);
        spacerLabel.setLayoutData(spacerGrid);
    }
}
