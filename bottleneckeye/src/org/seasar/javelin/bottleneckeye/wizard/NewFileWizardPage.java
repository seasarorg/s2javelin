package org.seasar.javelin.bottleneckeye.wizard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.seasar.javelin.bottleneckeye.model.persistence.PersistenceModel;
import org.seasar.javelin.bottleneckeye.model.persistence.Settings;
import org.seasar.javelin.bottleneckeye.util.ModelSerializer;

/**
 * Bottleneck Eye�t�@�C���̐V�K�쐬�E�B�U�[�h�B
 */
public class NewFileWizardPage extends WizardPage
{
    private static final String TITLE = "Bottleneck Eye";

    private static final String DESC  = "Modify values.";

    private Text                hostNameText;

    private Text                portText;

    private Text                domainText;

    private Text                warnText;

    private Text                alarmText;

    private Combo               modeCombo;

    private Combo               lineStyleCombo;

    /**
     * �R���X�g���N�^�B
     */
    public NewFileWizardPage()
    {
        super("Create new Bottleneck Eye file.");
        setTitle(TITLE);
        setDescription(DESC);
    }

    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout(2, false));

        ModifyListener listener = new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                doValidate();
            }
        };

        Label label;
        label = new Label(composite, SWT.NULL);
        label.setText("Host:");
        this.hostNameText = new Text(composite, SWT.BORDER);
        this.hostNameText.setText("localhost");
        this.hostNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.hostNameText.addModifyListener(listener);

        label = new Label(composite, SWT.NULL);
        label.setText("Port:");
        this.portText = new Text(composite, SWT.BORDER);
        this.portText.setText("18000");
        this.portText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.portText.addModifyListener(listener);

        label = new Label(composite, SWT.NULL);
        label.setText("Domain:");
        this.domainText = new Text(composite, SWT.BORDER);
        this.domainText.setText("default");
        this.domainText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.domainText.addModifyListener(listener);

        label = new Label(composite, SWT.NULL);
        label.setText("Warning:");
        this.warnText = new Text(composite, SWT.BORDER);
        this.warnText.setText("300");
        this.warnText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.warnText.addModifyListener(listener);

        label = new Label(composite, SWT.NULL);
        label.setText("Alarm:");
        this.alarmText = new Text(composite, SWT.BORDER);
        this.alarmText.setText("500");
        this.alarmText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.alarmText.addModifyListener(listener);

        label = new Label(composite, SWT.NULL);
        label.setText("Mode:");
        this.modeCombo = new Combo(composite, SWT.READ_ONLY);
        this.modeCombo.add("TCP");
        this.modeCombo.add("JMX");
        this.modeCombo.setText("TCP");

        label = new Label(composite, SWT.NULL);
        label.setText("Style:");
        this.lineStyleCombo = new Combo(composite, SWT.READ_ONLY);
        this.lineStyleCombo.add("NORMAL");
        this.lineStyleCombo.add("SHORTEST");
        this.lineStyleCombo.add("FAN");
        this.lineStyleCombo.add("MANHATTAN");
        this.lineStyleCombo.setText("NORMAL");

        doValidate();
        setControl(composite);
    }

    private void doValidate()
    {
        if (this.hostNameText.getText().trim().length() == 0)
        {
            setErrorMessage("Host cannot be empty.");
            setPageComplete(false);
            return;
        }

        try
        {
            int port = Integer.parseInt(this.portText.getText());
            if (port < 1 || port > 65535)
            {
                setErrorMessage("Port must be from 1 to 65535.");
                setPageComplete(false);
                return;
            }
        }
        catch (NumberFormatException nfEx)
        {
            setErrorMessage("Port must be from 1 to 65535.");
            setPageComplete(false);
            return;
        }

        if (this.domainText.getText().trim().length() == 0)
        {
            setErrorMessage("Domain cannot be empty.");
            setPageComplete(false);
            return;
        }

        try
        {
            long warn = Long.parseLong(this.warnText.getText());
            if (warn < 1)
            {
                setErrorMessage("Warn must be from 1 to max long value.");
                setPageComplete(false);
                return;
            }
        }
        catch (NumberFormatException nfEx)
        {
            setErrorMessage("Warn must be from 1 to max long value.");
            setPageComplete(false);
            return;
        }

        try
        {
            long alarm = Long.parseLong(this.alarmText.getText());
            if (alarm < 1)
            {
                setErrorMessage("Alarm must be from 1 to max long value.");
                setPageComplete(false);
                return;
            }
        }
        catch (NumberFormatException nfEx)
        {
            setErrorMessage("Alarm must be from 1 to max long value.");
            setPageComplete(false);
            return;
        }

        setErrorMessage(null);
        setPageComplete(true);
    }

    /**
     * �ۑ�����B
     * @param file �t�@�C��
     * @throws CoreException
     */
    public void save(IFile file)
        throws CoreException
    {
        PersistenceModel root = new PersistenceModel();
        Settings settings = new Settings();
        root.setSettings(settings);

        settings.setHostName(this.hostNameText.getText());
        settings.setPortNum(convertToInteger(this.portText.getText()));
        settings.setDomain(this.domainText.getText());
        settings.setWarningThreshold(convertToLong(this.warnText.getText()));
        settings.setAlarmThreshold(convertToLong(this.alarmText.getText()));
        settings.setMode((this.modeCombo.getText()));
        settings.setLineStyle((this.lineStyleCombo.getText()));

        byte[] data = null;
        try
        {
            data = ModelSerializer.serialize(root);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        InputStream stream = new ByteArrayInputStream(data);
        try
        {
            file.setContents(stream, true, false, null);
        }
        finally
        {
            try
            {
                stream.close();
            }
            catch (IOException ex)
            {
                // ignore
                ex.printStackTrace();
            }
        }
    }

    /**
     * �������Integer�ɕϊ�����B
     * @param str ������
     * @return Integer�̒l�B������null��Integer�ɕϊ��ł��Ȃ�����null��Ԃ��B
     */
    public Integer convertToInteger(String str)
    {
        if (str == null)
        {
            return null;
        }

        try
        {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException ex)
        {
            return null;
        }
    }

    /**
     * �������Long�ɕϊ�����B
     * @param str ������
     * @return Long�̒l�B������null��Long�ɕϊ��ł��Ȃ�����null��Ԃ��B
     */
    public Long convertToLong(String str)
    {
        if (str == null)
        {
            return null;
        }

        try
        {
            return Long.parseLong(str);
        }
        catch (NumberFormatException ex)
        {
            return null;
        }
    }
}
