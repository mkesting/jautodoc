/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import net.sf.jautodoc.preferences.Constants;
import net.sf.jautodoc.source.SourceManipulator;
import net.sf.jautodoc.utils.Utils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;


/**
 * Object action delegate for adding file headers.
 */
public class AddHeaderOAD extends AbstractOAD {

    /* (non-Javadoc)
     * @see net.sf.jautodoc.actions.AbstractOAD#getTask(java.lang.Object[], java.lang.Object[])
     */
    protected ITask getTask(final Map<ICompilationUnit, List<IMember>> cus) {
        return new AddHeaderTask(cus.keySet().toArray(new ICompilationUnit[cus.size()]));
    }

    /**
     * Task for adding file headers.
     */
    private class AddHeaderTask implements ITask {

        private ICompilationUnit compUnit;

        private ICompilationUnit[] compUnits;
        private Exception exception;


        /**
         * Instantiates a new add header task.
         *
         * @param compUnits the compilation units
         */
        public AddHeaderTask(ICompilationUnit[] compUnits) {
            this.compUnits = compUnits;
        }

        /* (non-Javadoc)
         * @see net.sf.jautodoc.actions.AbstractOAD.ITask#getCompilationUnit()
         */
        public ICompilationUnit getCompilationUnit() {
            return compUnit;
        }

        /* (non-Javadoc)
         * @see net.sf.jautodoc.actions.AbstractOAD.ITask#checkSuccess()
         */
        public void checkSuccess() throws Exception {
            if (exception != null) {
                throw exception;
            }
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException {

            try {
                monitor.beginTask(Constants.TITLE_HEADER_TASK, compUnits.length);

                for (int i = 0; i < compUnits.length; i++) {
                    compUnit = compUnits[i];
                    monitor.subTask(compUnit.getElementName());
                    if (monitor.isCanceled()) {
                        break;
                    }

                    ITextSelection selection = null;

                    IEditorPart editor = Utils.findEditor(compUnit);
                    if (editor != null) {
                        editor.getEditorSite().getPage().bringToTop(editor);
                        selection = (ITextSelection) editor.getEditorSite()
                                .getSelectionProvider().getSelection();
                    }

                    ICompilationUnit workingCopy = Utils.getWorkingCopy(compUnit, editor);
                    SourceManipulator sm = new SourceManipulator(workingCopy);
                    sm.setForceAddHeader(true);
                    if (selection != null) {
                        sm.setCursorPosition(selection.getOffset());
                    }
                    sm.addJavadoc(new IMember[0], null);

                    if (editor == null) {
                        // not open in editor -> commit + discard
                        workingCopy.commitWorkingCopy(false, null);
                        workingCopy.discardWorkingCopy();
                    }
                    else if (editor != null) {
                        ((ITextEditor) editor).selectAndReveal(sm.getCursorPosition(), 0);
                    }

                    monitor.worked(1);
                }
            }
            catch (Exception e) {
                exception = e;
            }
            finally {
                monitor.done();
            }
        }
    }
}
