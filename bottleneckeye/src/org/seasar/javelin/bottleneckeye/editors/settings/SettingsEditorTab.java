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
 * �ݒ�^�u�B
 *
 * @author Sakamoto
 */
public class SettingsEditorTab
{
    // TODO: �ʐM���W���[����؂�o���B
    /** �e��MultiPageEditor */
    private MultiPageEditor   multiPageEditor_;

    /** StatsVisionEditor */
    private StatsVisionEditor statsVisionEditor_;

    /** �z�X�g������͂���e�L�X�g�t�B�[���h */
    private Text              hostText_;

    /** �|�[�g�ԍ�����͂���e�L�X�g�t�B�[���h */
    private Text              portText_;

    /** �h���C������͂���e�L�X�g�t�B�[���h */
    private Text              domainText_;

    /** �x��臒l����͂���e�L�X�g�t�B�[���h */
    private Text              warningText_;

    /** �A���[��臒l����͂���e�L�X�g�t�B�[���h */
    private Text              alarmText_;

    /** �ʐM���[�h��I������R���{�{�b�N�X */
    private Combo             modeCombo_;

    /** ���̃X�^�C����I������R���{�{�b�N�X */
    private Combo             lineStyleCombo_;

    /** "Reload" �{�^�� */
    private Button            reloadButton_;

    /** "Reset" �{�^�� */
    private Button            resetButton_;

    /** "Start" �{�^�� */
    private Button            startButton_;

    /** "Stop" �{�^�� */
    private Button            stopButton_;

    /**
     * �R���X�g���N�^�B�֘A����Editor��ݒ肷��B
     * @param multiPageEditor �e�ƂȂ�}���`�y�[�W�G�f�B�^�B
     * @param statsVisionEditor �ʐM����������StatsVision�G�f�B�^�B
     */
    public SettingsEditorTab(MultiPageEditor multiPageEditor, StatsVisionEditor statsVisionEditor)
    {
        this.multiPageEditor_ = multiPageEditor;
        this.statsVisionEditor_ = statsVisionEditor;
    }

    /**
     * �^�u�̒��g���쐬����i�R���|�W�b�g�j�B
     *
     * @param container �e�R���|�W�b�g
     * @param editorPart �^�u�𐶐�����G�f�B�^
     * @return ��ʃC���X�^���X
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
     * �^�u�̖��O��Ԃ��B
     *
     * @return �^�u��
     */
    public String getName()
    {
        return "Settings";
    }

    /**
     * �A���[��臒l���Z�b�g����B
     *
     * @param threshold 臒l
     */
    public void setAlarmThreshold(long threshold)
    {
        this.alarmText_.setText(String.valueOf(threshold));
    }

    /**
     * �h���C�����Z�b�g����B
     *
     * @param domain �h���C��
     */
    public void setDomain(String domain)
    {
        this.domainText_.setText(domain);
    }

    /**
     * �z�X�g�����Z�b�g����B
     *
     * @param hostName �z�X�g��
     */
    public void setHostName(String hostName)
    {
        this.hostText_.setText(hostName);
    }

    /**
     * ���̎�ނ��Z�b�g����B
     *
     * @param lineStyle ���̎��
     */
    public void setLineStyle(String lineStyle)
    {
        this.lineStyleCombo_.setText(lineStyle);
    }

    /**
     * �ʐM���[�h���Z�b�g����B
     *
     * @param mode �ʐM���[�h
     */
    public void setMode(String mode)
    {
        this.modeCombo_.setText(mode);
    }

    /**
     * �|�[�g�ԍ����Z�b�g����B
     *
     * @param port �|�[�g�ԍ�
     */
    public void setPortNum(int port)
    {
        this.portText_.setText(String.valueOf(port));
    }

    /**
     * �x��臒l���Z�b�g����B
     *
     * @param threshold 臒l
     */
    public void setWarningThreshold(long threshold)
    {
        this.warningText_.setText(String.valueOf(threshold));
    }

    /**
     * �ݒ���r���[�ɒʒm����B
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
     * �ݒ��ʗp�̃��x���𐶐�����B
     *
     * @param composite ���x����}������R���|�W�b�g
     * @param text ���x���e�L�X�g
     * @return ���x��
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
     * �ݒ��ʗp�̃e�L�X�g�t�B�[���h�𐶐�����B
     *
     * @param composite �e�L�X�g�t�B�[���h��}������R���|�W�b�g
     * @param span ��
     * @param value �����e�L�X�g
     * @return �e�L�X�g�{�b�N�X
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
     * �ݒ��ʗp�̃{�^���𐶐�����B
     *
     * @param composite �{�^����}������R���|�W�b�g
     * @param text �{�^���̃e�L�X�g
     * @param isEnabled �{�^����L���ɂ���ꍇ�� <code>true</code>
     * @return �{�^��
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
     * �ݒ��ʗp�̃R���{�{�b�N�X�𐶐�����B
     *
     * @param composite �R���{�{�b�N�X��}������R���|�W�b�g
     * @param span �X�p��
     * @param values ���ڒl
     * @param value �I�����鍀��
     * @return �R���{�{�b�N�X
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
     * �ݒ��ʗp�̃X�y�[�T�𐶐�����B
     *
     * @param composite �X�y�[�T��}������R���|�W�b�g
     * @param space �X�y�[�X�̑傫��
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
