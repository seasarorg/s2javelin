package org.seasar.javelin.statsvision.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewFileWizard extends Wizard implements INewWizard
{
    private static final String TITLE = "New StatsVision File";

    private WizardNewFileCreationPage creationPage;

    private IStructuredSelection selection;

    private NewFileWizardPage modifyPage;
    
    public NewFileWizard()
    {
        setWindowTitle(TITLE);
    }

    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        this.selection = selection;
    }
    
    public void addPages()
    {
        creationPage = new WizardNewFileCreationPage("", selection);
        creationPage.setTitle("StatsVision");
        creationPage.setDescription("Create new StatsVision file.");
        creationPage.setFileExtension("jview");

        modifyPage = new NewFileWizardPage();

        addPage(creationPage);
        addPage(modifyPage);
    }
    
    @Override
    public boolean performFinish()
    {
        IFile file = creationPage.createNewFile();
        try
        {
            modifyPage.save(file);
        }
        catch (CoreException ex)
        {
            ex.printStackTrace();
        }
        
        return true;
    }
}
