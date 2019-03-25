/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preview;

import net.sf.jautodoc.JAutodocPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ContentStamp;
import org.eclipse.ltk.core.refactoring.UndoTextFileChange;
import org.eclipse.text.edits.UndoEdit;

/**
 * A {@link UndoTextFileChange} that operates on an {@link ICompilationUnit} in the workspace.
 * <p>
 * Adaptation of org.eclipse.jdt.core.refactoring.UndoCompilationUnitChange
 * </p>
 */
public class UndoCompilationUnitChange extends UndoTextFileChange {

    private final ICompilationUnit compUnit;

    public UndoCompilationUnitChange(final String name, final ICompilationUnit compUnit, final UndoEdit undo,
            final ContentStamp stampToRestore, final int saveMode) throws CoreException {
        super(name, getFile(compUnit), undo, stampToRestore, saveMode);
        this.compUnit = compUnit;
    }

    private static IFile getFile(final ICompilationUnit compUnit) throws CoreException {
        final IFile file = (IFile)compUnit.getResource();
        if (file == null) {
            String message = "Missing File: " + compUnit.getElementName();
            throw new CoreException(new Status(IStatus.ERROR, JAutodocPlugin.PLUGIN_ID, message));
        }
        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getModifiedElement() {
        return compUnit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Change createUndoChange(final UndoEdit edit, final ContentStamp stampToRestore) throws CoreException {
        return new UndoCompilationUnitChange(getName(), compUnit, edit, stampToRestore, getSaveMode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Change perform(final IProgressMonitor pm) throws CoreException {
        pm.beginTask("", 2); //$NON-NLS-1$
        compUnit.becomeWorkingCopy(new SubProgressMonitor(pm,1));
        try {
            return super.perform(new SubProgressMonitor(pm,1));
        } finally {
            compUnit.discardWorkingCopy();
        }
    }
}
