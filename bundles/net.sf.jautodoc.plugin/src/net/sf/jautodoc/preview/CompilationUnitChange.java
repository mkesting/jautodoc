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
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.ContentStamp;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.UndoEdit;

/**
 * A {@link TextFileChange} that operates on an {@link ICompilationUnit} in the workspace.
 * <p>
 * Adaptation of org.eclipse.jdt.core.refactoring.CompilationUnitChange
 * </p>
 */
public class CompilationUnitChange extends TextFileChange {

    private final ICompilationUnit compUnit;

    /** The (optional) refactoring descriptor */
    private ChangeDescriptor descriptor;

    /**
     * Creates a new <code>CompilationUnitChange</code>.
     *
     * @param name the change's name, mainly used to render the change in the UI
     * @param cunit the compilation unit this change works on
     */
    public CompilationUnitChange(String name, ICompilationUnit compUnit) {
        super(name, getFile(compUnit));
        Assert.isNotNull(compUnit);
        this.compUnit = compUnit;
        setTextType("java"); //$NON-NLS-1$
    }

    private static IFile getFile(final ICompilationUnit compUnit) {
        return (IFile) compUnit.getResource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getModifiedElement(){
        return compUnit;
    }

    /**
     * Returns the compilation unit this change works on.
     *
     * @return the compilation unit this change works on
     */
    public ICompilationUnit getCompilationUnit() {
        return compUnit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IDocument acquireDocument(final IProgressMonitor pm) throws CoreException {
        pm.beginTask("", 2); //$NON-NLS-1$
        compUnit.becomeWorkingCopy(new SubProgressMonitor(pm, 1));
        return super.acquireDocument(new SubProgressMonitor(pm, 1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void releaseDocument(final IDocument document, final IProgressMonitor pm) throws CoreException {
        final boolean isModified = isDocumentModified();
        super.releaseDocument(document, pm);
        try {
            compUnit.discardWorkingCopy();
        } finally {
            if (isModified && !isDocumentAcquired()) {
                if (compUnit.isWorkingCopy()) {
                    compUnit.reconcile(
                            ICompilationUnit.NO_AST,
                            false /* don't force problem detection */,
                            null /* use primary owner */,
                            null /* no progress monitor */);
                }
                else {
                    compUnit.makeConsistent(pm);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Change createUndoChange(final UndoEdit edit, final ContentStamp stampToRestore) {
        try {
            return new UndoCompilationUnitChange(getName(), compUnit, edit, stampToRestore, getSaveMode());
        } catch (CoreException e) {
            JAutodocPlugin.getDefault().handleException(e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(final Class adapter) {
        if (ICompilationUnit.class.equals(adapter)) {
            return compUnit;
        }
        return super.getAdapter(adapter);
    }

    /**
     * Sets the refactoring descriptor for this change.
     *
     * @param descriptor the descriptor to set, or <code>null</code> to set no descriptor
     */
    public void setDescriptor(final ChangeDescriptor descriptor) {
        this.descriptor= descriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChangeDescriptor getDescriptor() {
        return descriptor;
    }
}
