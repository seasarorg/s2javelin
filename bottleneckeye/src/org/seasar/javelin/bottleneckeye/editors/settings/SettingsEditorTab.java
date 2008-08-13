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

    /** 線のスタイルを選択するコンボボックス */
    private Combo             lineStyleCombo_;

    /** View画面のクラス１つに表示するメソッドの最大数 */
    private Text              maxMethodText_;

    /** "Reload" ボタン */
    private Button            reloadButton_;

    /** "Reset" ボタン */
    private Button            resetButton_;

    /** "Start" ボタン */
    private Button            startButton_;

    /** "Stop" ボタン */
    private Button            stopButton_;

    /** 最後に押されたStart/StopボタンがStartの場合True、Stopの場合False */
    private boolean           isLastStarted_;

    /**
     * コンストラクタ。関連するEditorを設定する。
     * @param multiPageEditor 親となるマルチページエディタ。
     * @param statsVisionEditor 通信部分を持つStatsVisionエディタ。
     */
    public SettingsEditorTab(MultiPageEditor multiPageEditor, StatsVisionEditor statsVisionEditor)
    {
        this.multiPageEditor_ = multiPageEditor;
        this.statsVisionEditor_ = statsVisionEditor;
        this.isLastStarted_ = true;
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

        createLabel(composite, "Max methods:");
        this.maxMethodText_ = createText(composite, 6, Long.toString(0));
        createSpacer(composite, 4);

        String[] values;
        values = new String[]{MultiPageEditor.MODE_TCP, MultiPageEditor.MODE_JMX};

        values =
                new String[]{MultiPageEditor.LINE_STYLE_NORMAL,
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

        Button printButton = createButton(composite, "Print", true);
        Button copyButton = createButton(composite, "Copy", true);
        // ------------------------------------------------------------

        this.hostText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String hostName = SettingsEditorTab.this.hostText_.getText();
                SettingsEditorTab.this.statsVisionEditor_.setHostName(hostName);
                SettingsEditorTab.this.statsVisionEditor_.setDirty(true);
            }
        });

        this.portText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String portText = SettingsEditorTab.this.portText_.getText();
                int port = Integer.parseInt(portText);
                SettingsEditorTab.this.statsVisionEditor_.setPortNum(port);
                SettingsEditorTab.this.statsVisionEditor_.setDirty(true);
            }
        });

        this.domainText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String domain = SettingsEditorTab.this.domainText_.getText();
                SettingsEditorTab.this.statsVisionEditor_.setDomain(domain);
                SettingsEditorTab.this.statsVisionEditor_.setDirty(true);
            }
        });

        this.warningText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String thresholdText = SettingsEditorTab.this.warningText_.getText();
                long threshold = Long.parseLong(thresholdText);
                SettingsEditorTab.this.statsVisionEditor_.setWarningThreshold(threshold);
                SettingsEditorTab.this.statsVisionEditor_.setDirty(true);
            }
        });

        this.alarmText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String thresholdText = SettingsEditorTab.this.alarmText_.getText();
                long threshold = Long.parseLong(thresholdText);
                SettingsEditorTab.this.statsVisionEditor_.setAlarmThreshold(threshold);
                SettingsEditorTab.this.statsVisionEditor_.setDirty(true);
            }
        });

        this.lineStyleCombo_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String lineStyle = SettingsEditorTab.this.lineStyleCombo_.getText();
                SettingsEditorTab.this.statsVisionEditor_.setLineStyle(lineStyle);
                SettingsEditorTab.this.statsVisionEditor_.setDirty(true);
            }
        });

        this.maxMethodText_.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                String maxMethodCountText = SettingsEditorTab.this.maxMethodText_.getText();
                long maxMethodCount = Long.parseLong(maxMethodCountText);
                SettingsEditorTab.this.statsVisionEditor_.setMaxMethodCount(maxMethodCount);
                SettingsEditorTab.this.statsVisionEditor_.setDirty(true);
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
                start();
            }
        });

        // ------------------------------------------------------------
        this.stopButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event)
            {
                stop();
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
     * View画面のクラス１つに表示するメソッドの最大数をセットする。
     *
     * @param maxMethodCount View画面のクラス１つに表示するメソッドの最大数
     */
    public void setMaxMethodCount(long maxMethodCount)
    {
        this.maxMethodText_.setText(String.valueOf(maxMethodCount));
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
        this.statsVisionEditor_.setMaxMethodCount(Long.valueOf(this.maxMethodText_.getText()));
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

    /**
     * 接続を開始する。
     */
    public void start()
    {
        notifyAllSettings();

        this.startButton_.setEnabled(false);
        this.stopButton_.setEnabled(false);
        this.hostText_.setEnabled(false);
        this.portText_.setEnabled(false);
        this.domainText_.setEnabled(false);
        this.warningText_.setEnabled(false);
        this.alarmText_.setEnabled(false);
        this.lineStyleCombo_.setEnabled(false);
        this.maxMethodText_.setEnabled(false);

        this.isLastStarted_ = true;

        this.multiPageEditor_.notifyStart();
    }

    /**
     * 接続を終了する。
     */
    private void stop()
    {
        this.startButton_.setEnabled(false);
        this.stopButton_.setEnabled(false);
        this.resetButton_.setEnabled(false);
        this.reloadButton_.setEnabled(false);
        this.hostText_.setEnabled(true);
        this.portText_.setEnabled(true);
        this.domainText_.setEnabled(true);
        this.warningText_.setEnabled(true);
        this.alarmText_.setEnabled(true);
        this.lineStyleCombo_.setEnabled(true);
        this.maxMethodText_.setEnabled(true);

        this.isLastStarted_ = false;

        this.multiPageEditor_.notifyStop();
    }

    /**
     * TCP接続が開始したことの通知を受けた際の動作
     * 最後にStartボタンが押下されていた場合、
     * startボタンを再度押下不可能にする。
     * stopボタン、resetボタン、reloadボタンを押下可能にする。
     * 最後にStopボタンが押下されていた場合はStopボタンの２重押下を防止するため、処理は行わない。
     */
    public void notifyCommunicateStart()
    {
        if (this.isLastStarted_)
        {
            if (this.stopButton_.isDisposed() == true)
            {
                return;
            }

            this.startButton_.setEnabled(false);
            this.stopButton_.setEnabled(true);
            boolean enabled = this.statsVisionEditor_.isConnected();
            this.resetButton_.setEnabled(enabled);
            this.reloadButton_.setEnabled(enabled);
        }
    }

    /**
     * TCP接続が終了したことの通知
     * startボタンを押下可能にする。
     * stopボタン、resetボタン、reloadボタンを押下不可能にする。
     */
    public void notifyCommunicateStop()
    {
        if (this.stopButton_.isDisposed() == true)
        {
            return;
        }

        this.stopButton_.setEnabled(false);
        this.startButton_.setEnabled(true);
        this.resetButton_.setEnabled(false);
        this.reloadButton_.setEnabled(false);
    }
}
