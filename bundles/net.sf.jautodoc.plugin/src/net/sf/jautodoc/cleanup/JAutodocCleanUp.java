/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.cleanup;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.Constants;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.cleanup.CleanUpContext;
import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.cleanup.CleanUpRequirements;
import org.eclipse.jdt.ui.cleanup.ICleanUp;
import org.eclipse.jdt.ui.cleanup.ICleanUpFix;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * The JAutodoc cleanup contribution class.
 */
public class JAutodocCleanUp implements ICleanUp {

    private CleanUpOptions options;


    /** {@inheritDoc} */
    @Override
    public void setOptions(final CleanUpOptions options) {
        this.options = options;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getStepDescriptions() {
        List<String> descriptions = new ArrayList<>(2);

        if (options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION)) {
            descriptions.add(options.isEnabled(Constants.CLEANUP_REP_HEADER_OPTION)
                    ? Constants.CLEANUP_REP_HEADER_STEP_LABEL
                    : Constants.CLEANUP_ADD_HEADER_STEP_LABEL);
        }

        if (options.isEnabled(Constants.CLEANUP_JAVADOC_OPTION)) {
            descriptions.add(Constants.CLEANUP_JAVADOC_STEP_LABEL);
        }

        return descriptions.toArray(new String[descriptions.size()]);
    }

    /** {@inheritDoc} */
    @Override
    public CleanUpRequirements getRequirements() {
        return new CleanUpRequirements(false, false, false, null);
    }

    /** {@inheritDoc} */
    @Override
    public RefactoringStatus checkPreConditions(final IJavaProject project, final ICompilationUnit[] compilationUnits,
            final IProgressMonitor monitor) throws CoreException {
        return new RefactoringStatus();
    }

    /** {@inheritDoc} */
    @Override
    public ICleanUpFix createFix(final CleanUpContext context) throws CoreException {
        if (options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION)
                || options.isEnabled(Constants.CLEANUP_JAVADOC_OPTION)) {

            final ICompilationUnit compUnit = context.getCompilationUnit();
            try {
                return new JAutodocCleanUpFix(compUnit,
                        options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION),
                        options.isEnabled(Constants.CLEANUP_REP_HEADER_OPTION),
                        options.isEnabled(Constants.CLEANUP_JAVADOC_OPTION));
            } catch (Exception e) {
                JAutodocPlugin.getDefault().handleException(compUnit, e);
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public RefactoringStatus checkPostConditions(final IProgressMonitor monitor) throws CoreException {
        return new RefactoringStatus();
    }
}
