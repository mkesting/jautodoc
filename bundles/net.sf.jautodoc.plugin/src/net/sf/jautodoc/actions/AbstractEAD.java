/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.Configuration;
import net.sf.jautodoc.preferences.ConfigurationManager;
import net.sf.jautodoc.preferences.OptionsDialog;
import net.sf.jautodoc.preview.CompilationUnitRefactoring;
import net.sf.jautodoc.preview.CompilationUnitRefactoringWizard;
import net.sf.jautodoc.source.SourceManipulator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.operations.TimeTriggeredProgressMonitorDialog;


/**
 * Abstract base class for editor action delegates.
 */
@SuppressWarnings("restriction")
public abstract class AbstractEAD implements IEditorActionDelegate {
    private static final String JAVADOC_DLG_ACTION = "net.sf.jautodoc.view.action.addJavadocDlg";
    private static final String  HEADER_DLG_ACTION = "net.sf.jautodoc.view.action.addHeaderDlg";

    protected String dialogTitle;
    protected boolean showDialog;
    protected IEditorPart editorPart;

    /**
     * Sets the show dialog flag. If true, the options dialog will be shown before running the action.
     *
     * @param showDialog the new show dialog flag
     * @param dialogTitle the dialog title
     */
    public void setShowDialog(boolean showDialog, String dialogTitle) {
        this.showDialog = showDialog;
        this.dialogTitle = dialogTitle;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
     */
    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        this.editorPart = targetEditor;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(final IAction action) {
        final IEditorInput editorInput = this.editorPart.getEditorInput();
        final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
        final ICompilationUnit compUnit = manager.getWorkingCopy(editorInput);

        if (compUnit.findPrimaryType() == null) {
            return; // ignore e.g. package-info.java
        }

        while (true) {
            final int rc = showOptionsDialog(action, compUnit);
            if (rc == IDialogConstants.CANCEL_ID) {
                return;
            }

            final boolean showPreview = (rc == OptionsDialog.PREVIEW_ID);
            final Configuration config = findConfiguration(compUnit);
            final SourceManipulator sm = createSourceManipulator(compUnit, config, showPreview);
            final IRunnableWithProgress task = createTask(sm, compUnit, config);

            if (runTask(task, sm, compUnit, showPreview) == IDialogConstants.OK_ID) {
                break;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    protected abstract void doRun(final SourceManipulator sm, final ICompilationUnit compUnit,
            final Configuration config, final IProgressMonitor monitor) throws Exception;

    protected IMember getSelectedMember(final ICompilationUnit compUnit) throws JavaModelException {
        final ITextSelection textSelection = getSelection();
        if (textSelection == null) {
            return null;
        }

        IJavaElement element = compUnit.getElementAt(textSelection.getOffset());
        if (element == null) {
            return null;
        }

        if (element instanceof IType) {
            final ISourceRange typeNameRange = ((IType)element).getNameRange();

            if (typeNameRange.getOffset() > textSelection.getOffset()) {
                element = null; // Name starts after current offset
            }
            else if ((typeNameRange.getOffset() + typeNameRange.getLength())
                    < textSelection.getOffset()) {
                element = null; // Name ends prior to offset
            }

            return (IMember)element;
        }

        if (!(element instanceof IField) &&
            !(element instanceof IMethod)) {
            element = null; // ignore initializer
        }

        return (IMember)element;
    }

    protected ITextSelection getSelection() {
        return editorPart != null && editorPart.getEditorSite().getSelectionProvider() != null ?
                (ITextSelection) editorPart.getEditorSite().getSelectionProvider().getSelection() : null;
    }

    private Shell getShell() {
        return editorPart.getSite().getShell();
    }

    private Configuration findConfiguration(final ICompilationUnit compUnit) {
        // cached configuration for this compilation unit?
        Configuration config = ConfigurationManager.getCachedConfiguration(compUnit);
        if (config == null) {
            // no -> use project/workspace settings
            config = ConfigurationManager.getConfiguration(compUnit);
        }
        return config;
    }

    private int showOptionsDialog(final IAction action, final ICompilationUnit compUnit) {
        if (showDialog || action != null
                && (JAVADOC_DLG_ACTION.equals(action.getId()) || HEADER_DLG_ACTION.equals(action.getId()))) {
            // open options dialog (sets a cached configuration for the current compilation unit)
            if (dialogTitle == null && action != null) {
                dialogTitle = JAVADOC_DLG_ACTION.equals(action.getId()) ? "Add Javadoc" : "Add Header";
            }

            final OptionsDialog dlg = new OptionsDialog(getShell(), dialogTitle, compUnit);
            return dlg.open();
        }
        return IDialogConstants.OK_ID;
    }

    private SourceManipulator createSourceManipulator(final ICompilationUnit compUnit, final Configuration config,
            final boolean showPreview) {
        final SourceManipulator sm = new SourceManipulator(compUnit, config);
        sm.setShowPreview(showPreview);
        return sm;
    }

    private IRunnableWithProgress createTask(final SourceManipulator sm, final ICompilationUnit compUnit,
            final Configuration config) {
        return new IRunnableWithProgress() {

            /* (non-Javadoc)
             * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
             */
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    doRun(sm, compUnit, config, monitor);
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } finally{
                    monitor.done();
                }
            }
        };
    }

    private IRunnableContext createRunnableContext(final boolean showPreview) {
        return showPreview ? new TimeTriggeredProgressMonitorDialog(getShell(), 800)
                : new ProgressMonitorDialog(getShell());
    }

    private int runTask(final IRunnableWithProgress task, final SourceManipulator sm, final ICompilationUnit compUnit,
            final boolean showPreview) {
        int rc = IDialogConstants.OK_ID;
        try {
            createRunnableContext(showPreview).run(false, true, task);
            if (showPreview) {
                rc = showPreview(compUnit, sm.getChanges(), sm.getChangeDescriptions());
            }
        } catch (InvocationTargetException e) {
            JAutodocPlugin.getDefault().handleException(getShell(), compUnit, e.getTargetException());
        } catch (InterruptedException e) {
            JAutodocPlugin.getDefault().handleException(compUnit, e);
        }
        return rc;
    }

    private int showPreview(final ICompilationUnit cu, final TextEdit te,
            final Map<TextEdit, String> changeDescriptions) throws InterruptedException {
        final CompilationUnitRefactoring refactoring = new CompilationUnitRefactoring(cu, te, changeDescriptions);
        final CompilationUnitRefactoringWizard wizard = new CompilationUnitRefactoringWizard(refactoring);
        final RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(wizard);
        return op.run(getShell(), "JAutodoc");
    }
}
