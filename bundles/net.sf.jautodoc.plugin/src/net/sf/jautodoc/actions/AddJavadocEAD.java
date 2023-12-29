/*******************************************************************
 * Copyright (c) 2006 - 2023, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.text.ITextSelection;

import net.sf.jautodoc.preferences.Configuration;
import net.sf.jautodoc.source.JavadocFormatter;
import net.sf.jautodoc.source.SourceManipulator;
import net.sf.jautodoc.utils.SourceUtils;


/**
 * Editor action delegate for adding Javadoc.
 */
public class AddJavadocEAD extends AbstractEAD {

    @Override
    protected void doRun(final SourceManipulator sm, final ICompilationUnit compUnit, final Configuration config,
            final IProgressMonitor monitor) throws Exception {
        try {
            if (config.isUseEclipseFormatter()) {
                JavadocFormatter.getInstance().startFormatting(compUnit);
            }

            final IMember member = getSelectedMember(compUnit);
            if (member != null && !SourceUtils.isGeneratedMember(member)) {
                if (SourceUtils.isRecordComponent(member)) {
                    sm.addJavadoc(new IMember[] { member.getDeclaringType() }, monitor);
                } else {
                    sm.addJavadoc(new IMember[] { member }, monitor);
                }
            } else {
                final ITextSelection textSelection = getSelection();
                if (textSelection != null) {
                    sm.setCursorPosition(textSelection.getOffset());
                }
                sm.addJavadoc(monitor);
            }
        } finally {
            if (config.isUseEclipseFormatter()) {
                JavadocFormatter.getInstance().stopFormatting();
            }
        }
    }
}
