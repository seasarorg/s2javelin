package org.seasar.javelin.statsvision.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewFileWizard extends Wizard implements INewWizard
{

    public NewFileWizard()
    {
        // TODO Auto-generated constructor stub
    }

    public void addPages()
    {
        addPage(new NewFileWizardPage());
    }
    
    @Override
    public boolean performFinish()
    {
        // TODO Auto-generated method stub
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        // TODO Auto-generated method stub

    }

}
