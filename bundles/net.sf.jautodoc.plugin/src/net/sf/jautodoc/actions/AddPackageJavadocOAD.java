/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.Configuration;
import net.sf.jautodoc.preferences.ConfigurationManager;
import net.sf.jautodoc.utils.NewFileOperation;
import net.sf.jautodoc.utils.UIHelper;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;


/**
 * Object action delegate for adding package Javadoc.
 */
public class AddPackageJavadocOAD implements IObjectActionDelegate {

    private IWorkbenchPage workbenchPage;


    /* (non-Javadoc)
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
        workbenchPage = targetPart.getSite().getPage();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(final IAction action) {
        final IPackageFragment[] packages = getPackages();
        if (packages == null || packages.length == 0) {
            return;
        }

        final String[] fileNames = getPackageDocFileNames(packages);
        final IFile[] files = getPackageDocFiles(packages,fileNames);
        final String[] contents = getInitialContents(packages, files);

        final NewFileOperation op = new NewFileOperation(files, contents);
        try {
            new ProgressMonitorDialog(getShell()).run(false, true, op);
            selectRevealAndOpen(files);
        }
        catch (InterruptedException e) {
            return;
        }
        catch (InvocationTargetException e) {
            JAutodocPlugin.getDefault().handleException(getShell(), e.getTargetException());
        }
        catch (PartInitException e) {
            JAutodocPlugin.getDefault().handleException(getShell(), e);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(final IAction action, final ISelection selection) {
    }

    private IPackageFragment[] getPackages() {
        IStructuredSelection selection = (IStructuredSelection)workbenchPage.getSelection();
        if (selection.isEmpty()
                || !(selection.getFirstElement() instanceof IPackageFragment)) {
            return null;
        }

        Set<IPackageFragment> packages = new HashSet<IPackageFragment>();
        Iterator<?> iter = selection.iterator();
        while (iter.hasNext()) {
            packages.add((IPackageFragment)iter.next());
        }

        return packages.toArray(new IPackageFragment[packages.size()]);
    }

    private String[] getPackageDocFileNames(final IPackageFragment[] packages) {
        final String[] fileNames = new String[packages.length];
        for (int i = 0; i < packages.length; ++i) {
            final IProject project = packages[i].getJavaProject().getProject();
            final Configuration config = ConfigurationManager.getConfiguration(project);
            fileNames[i] = config.isUsePackageInfo() ? "package-info.java" : "package.html";
        }
        return fileNames;
    }

    private IFile[] getPackageDocFiles(final IPackageFragment[] packages, final String[] fileNames) {
        final IFile[] files = new IFile[packages.length];
        for (int i = 0; i < packages.length; ++i) {
            final IPath pkgDocPath = packages[i].getResource().getFullPath().append(fileNames[i]);
            files[i] = createFileHandle(pkgDocPath);
        }
        return files;
    }

    private String[] getInitialContents(final IPackageFragment[] packages, final IFile[] files) {
        final String[] contents = new String[files.length];
        for (int i = 0; i < files.length; ++i) {
            contents[i] = getTemplateText(files[i].getProject(), packages[i]);
        }
        return contents;
    }

    private String getTemplateText(final IProject project, final IPackageFragment pkg) {
        final Configuration config = ConfigurationManager.getConfiguration(project);

        final String template = config.isUsePackageInfo() ? config.getPackageInfoText() : config.getPackageDocText();
        if (template == null || template.trim().length() == 0) {
            return null;
        }

        // apply template
        String text = template;
        try {
            text = JAutodocPlugin.getContext().getTemplateManager().evaluateTemplate(pkg, template, "Package Javadoc",
                    config.getProperties());
        } catch (Exception e) {
            JAutodocPlugin.getDefault().handleException(pkg, e);
        }
        return text;
    }

    private void selectRevealAndOpen(final IFile[] files) throws PartInitException {
        for (int i = 0; i < files.length; ++i) {
            UIHelper.selectAndReveal(files[i], workbenchPage.getWorkbenchWindow());
            UIHelper.openInEditor(files[i]);
        }
    }

    private Shell getShell() {
        return workbenchPage.getWorkbenchWindow().getShell();
    }

    private IFile createFileHandle(final IPath filePath) {
        return ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
    }
}
