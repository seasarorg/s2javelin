package org.seasar.javelin.bottleneckeye.wizard;

import java.io.ByteArrayInputStream;
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

public class NewFileWizardPage extends WizardPage
{
    private static final String TITLE = "Bottleneck Eye";
    private static final String DESC  = "Modify values.";
    
    private Text hostNameText;
    private Text portText;
    private Text domainText;
    private Text warnText;
    private Text alarmText;
    private Combo modeCombo;
    private Combo lineStyleCombo;

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

        ModifyListener listener = new ModifyListener(){
            public void modifyText(ModifyEvent e)
            {
                doValidate();
            }
        };
        
        Label label;
        label = new Label(composite, SWT.NULL);
        label.setText("Host:");
        hostNameText = new Text(composite, SWT.BORDER);
        hostNameText.setText("localhost");
        hostNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        hostNameText.addModifyListener(listener);

        label = new Label(composite, SWT.NULL);
        label.setText("Port:");
        portText = new Text(composite, SWT.BORDER);
        portText.setText("18000");
        portText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        portText.addModifyListener(listener);

        label = new Label(composite, SWT.NULL);
        label.setText("Domain:");
        domainText = new Text(composite, SWT.BORDER);
        domainText.setText("default");
        domainText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        domainText.addModifyListener(listener);

        label = new Label(composite, SWT.NULL);
        label.setText("Warning:");
        warnText = new Text(composite, SWT.BORDER);
        warnText.setText("300");
        warnText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        warnText.addModifyListener(listener);

        label = new Label(composite, SWT.NULL);
        label.setText("Alarm:");
        alarmText = new Text(composite, SWT.BORDER);
        alarmText.setText("500");
        alarmText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        alarmText.addModifyListener(listener);
        
        label = new Label(composite, SWT.NULL);
        label.setText("Mode:");
        modeCombo = new Combo(composite, SWT.READ_ONLY);
        modeCombo.add("TCP");
        modeCombo.add("JMX");
        modeCombo.setText("TCP");

        label = new Label(composite, SWT.NULL);
        label.setText("Style:");
        lineStyleCombo = new Combo(composite, SWT.READ_ONLY);
        lineStyleCombo.add("NORMAL");
        lineStyleCombo.add("SHORTEST");
        lineStyleCombo.add("FAN");
        lineStyleCombo.add("MANHATTAN");
        lineStyleCombo.setText("NORMAL");

        doValidate();
        setControl(composite);
    }
    
    private void doValidate()
    {
        if (hostNameText.getText().trim().length() == 0)
        {
            setErrorMessage("Host cannot be empty.");
            setPageComplete(false);
            return;
        }
        
        try
        {
            int port = Integer.parseInt(portText.getText());
            if (port < 1 || port > 65535)
            {
                setErrorMessage("Port must be from 1 to 65535.");
                setPageComplete(false);
                return;
            }
        }
        catch(NumberFormatException nfEx)
        {
            setErrorMessage("Port must be from 1 to 65535.");
            setPageComplete(false);
            return;
        }
        
        if (domainText.getText().trim().length() == 0)
        {
            setErrorMessage("Domain cannot be empty.");
            setPageComplete(false);
            return;
        }
        
        try
        {
            long warn = Long.parseLong(warnText.getText());
            if (warn < 1)
            {
                setErrorMessage("Warn must be from 1 to max long value.");
                setPageComplete(false);
                return;
            }
        }
        catch(NumberFormatException nfEx)
        {
            setErrorMessage("Warn must be from 1 to max long value.");
            setPageComplete(false);
            return;
        }
        
        try
        {
            long alarm = Long.parseLong(alarmText.getText());
            if (alarm < 1)
            {
                setErrorMessage("Alarm must be from 1 to max long value.");
                setPageComplete(false);
                return;
            }
        }
        catch(NumberFormatException nfEx)
        {
            setErrorMessage("Alarm must be from 1 to max long value.");
            setPageComplete(false);
            return;
        }
        
        setErrorMessage(null);
        setPageComplete(true);
    }

    public void save(IFile file) throws CoreException
    {
        StringBuilder builder = new StringBuilder(1024);
        String lineSeparator = System.getProperty("line.separator");

        StringBuilder data = new StringBuilder(1024);
        data.append(hostNameText.getText()).append(lineSeparator);
        data.append(portText.getText()).append(lineSeparator);
        data.append(domainText.getText()).append(lineSeparator);
        data.append(warnText.getText()).append(lineSeparator);
        data.append(alarmText.getText()).append(lineSeparator);
        data.append(modeCombo.getText()).append(lineSeparator);
        data.append(lineStyleCombo.getText()).append(lineSeparator);
        
        InputStream stream = new ByteArrayInputStream(data.toString().getBytes());
        file.setContents(stream, true, false, null);
    }
}
