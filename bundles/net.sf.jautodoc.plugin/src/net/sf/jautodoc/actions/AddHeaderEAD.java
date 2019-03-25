/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.actions;

import net.sf.jautodoc.preferences.Configuration;
import net.sf.jautodoc.source.SourceManipulator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.text.ITextSelection;


/**
 * Editor action delegate for adding file headers.
 */
public class AddHeaderEAD extends AbstractEAD {

    @Override
    protected void doRun(final SourceManipulator sm, final ICompilationUnit compUnit, final Configuration config,
            final IProgressMonitor monitor) throws Exception {
        final ITextSelection textSelection = getSelection();
        if (textSelection != null) {
            sm.setCursorPosition(textSelection.getOffset());
        }
        sm.setForceAddHeader(true);
        sm.addJavadoc(new IMember[0], monitor);
        //((ITextEditor) editorPart).selectAndReveal(sm.getCursorPosition(), 0);
    }
}
