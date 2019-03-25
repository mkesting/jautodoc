/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preview;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

public class CompilationUnitRefactoringWizard extends RefactoringWizard {

    public CompilationUnitRefactoringWizard(final Refactoring refactoring) {
        super(refactoring, DIALOG_BASED_USER_INTERFACE);
        setWindowTitle("JAutodoc Preview");
        setDefaultPageTitle("JAutodoc Preview");
    }

    @Override
    protected void addUserInputPages() {
        // empty
    }
}
