/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preview;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;

public class CompilationUnitRefactoring extends Refactoring {

    private final CompilationUnitChange change;

    public CompilationUnitRefactoring(final ICompilationUnit compUnit, final TextEdit textEdit,
            final Map<TextEdit, String> changeDescriptions) {

        this.change = new CompilationUnitChange(compUnit.getElementName(), compUnit);
        this.change.setEdit(textEdit);
        this.change.setSaveMode(textEdit.hasChildren() ? TextFileChange.LEAVE_DIRTY : TextFileChange.KEEP_SAVE_STATE);

        for (final TextEdit te : textEdit.getChildren()) {
            final String description = changeDescriptions.get(te);
            this.change.addTextEditGroup(new TextEditGroup(description, te));
        }
    }

    @Override
    public String getName() {
        return change.getCompilationUnit().getElementName();
    }

    @Override
    public Change createChange(final IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return change;
    }

    @Override
    public RefactoringStatus checkInitialConditions(final IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        return new RefactoringStatus(); // OK
    }

    @Override
    public RefactoringStatus checkFinalConditions(final IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        return new RefactoringStatus(); // OK
    }
}
