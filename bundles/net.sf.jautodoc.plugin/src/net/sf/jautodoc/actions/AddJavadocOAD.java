/*******************************************************************
 * Copyright (c) 2006 - 2023, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import net.sf.jautodoc.preferences.Configuration;
import net.sf.jautodoc.preferences.ConfigurationManager;
import net.sf.jautodoc.preferences.Constants;
import net.sf.jautodoc.source.JavadocFormatter;
import net.sf.jautodoc.source.SourceManipulator;
import net.sf.jautodoc.utils.SourceUtils;
import net.sf.jautodoc.utils.Utils;


/**
 * Object action delegate for adding Javadoc.
 */
public class AddJavadocOAD extends AbstractOAD {

    /* (non-Javadoc)
     * @see net.sf.jautodoc.actions.AbstractOAD#getTask(java.lang.Object[], java.lang.Object[])
     */
    protected ITask getTask(final Map<ICompilationUnit, List<IMember>> cus) {
        return new AddJavadocTask(cus);
    }

    /**
     * Task for adding Javadoc.
     */
    private class AddJavadocTask implements ITask {

        private Exception exception;
        private ICompilationUnit compUnit;

        private final Map<ICompilationUnit, List<IMember>> cus;


        /**
         * Instantiates a new add javadoc task.
         *
         * @param compUnits the compilation units
         * @param members the selected members
         */
        public AddJavadocTask(final Map<ICompilationUnit, List<IMember>> cus) {
            this.cus = cus;
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
                monitor.beginTask(Constants.TITLE_JDOC_TASK, cus.size());

                for (Map.Entry<ICompilationUnit, List<IMember>> entry : cus.entrySet()) {
                    compUnit = entry.getKey();
                    final List<IMember> members = entry.getValue();

                    monitor.subTask(compUnit.getElementName());
                    if (monitor.isCanceled()) {
                        break;
                    }

                    final Configuration config = ConfigurationManager.getConfiguration(compUnit);
                    if (config.isUseEclipseFormatter()) {
                        JavadocFormatter.getInstance().startFormatting(compUnit);
                    }

                    IEditorPart editor = Utils.findEditor(compUnit);
                    if (editor != null) {
                        editor.getEditorSite().getPage().bringToTop(editor);
                    }

                    final ICompilationUnit workingCopy = Utils.getWorkingCopy(compUnit, editor);
                    final SourceManipulator sm = new SourceManipulator(workingCopy, config);

                    if (members != null && members.size() > 0) {
                        final IMember[] wcMembers = getWorkingCopyMembers(workingCopy, members);
                        sm.addJavadoc(wcMembers, null);
                    }
                    else {
                        sm.addJavadoc(null);
                    }

                    if (editor == null) {
                        // not open in editor -> commit + discard
                        workingCopy.commitWorkingCopy(false, null);
                        workingCopy.discardWorkingCopy();
                    }
                    else if (members != null && members.size() > 0) {
                        final IMember member = members.get(0);
                        final int offset = member.getNameRange().getOffset();
                        final int length = member.getNameRange().getLength();
                        ((ITextEditor)editor).selectAndReveal(offset, length);
                    }

                    monitor.worked(1);
                }
            }
            catch (Exception e) {
                exception = e;
            }
            finally {
                JavadocFormatter.getInstance().stopFormatting();
                monitor.done();
            }
        }

        private IMember[] getWorkingCopyMembers(final ICompilationUnit workingCopy, final List<IMember> members) {
            final List<IMember> memberList = new ArrayList<IMember>();
            for (int i = 0; i < members.size(); ++i) {
                final IMember member = members.get(i);

                if (!isApplicableMember(member)) {
                    continue;
                }

                if (compUnit != member.getCompilationUnit()) {
                    continue;
                }

                if (workingCopy == member.getCompilationUnit()) {
                    memberList.add(member);
                    continue;
                }

                final IJavaElement[] elements = workingCopy.findElements(member);
                if (elements == null) {
                    continue;
                }

                for (int j = 0; j < elements.length; ++j) {
                    if (elements[j] instanceof IMember && isApplicableMember((IMember) elements[j])) {
                        memberList.add((IMember) elements[j]);
                    }
                }
            }
            return memberList.toArray(new IMember[memberList.size()]);
        }

        private boolean isApplicableMember(final IMember member) {
            return !SourceUtils.isGeneratedMember(member) && !SourceUtils.isRecordComponent(member);
        }
    }
}
