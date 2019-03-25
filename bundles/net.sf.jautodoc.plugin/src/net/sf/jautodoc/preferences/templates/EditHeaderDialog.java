/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences.templates;

import java.util.Map;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.Constants;
import net.sf.jautodoc.templates.contentassist.ITemplateContentAssistant;
import net.sf.jautodoc.utils.Utils;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.swt.widgets.Shell;


/**
 * Dialog for editing a file header template.
 */
public class EditHeaderDialog extends AbstractEditTextDialog implements Constants {

    /**
     * Instantiates a new edit header dialog.
     *
     * @param parentShell the parent shell
     * @param headerText the header text
     * @param properties the properties to use
     */
    public EditHeaderDialog(final Shell parentShell, final String headerText, final Map<String, String> properties) {
        super(parentShell, headerText, properties);
    }

    /**
     * {@inheritDoc}
     *
     * Check for valid comment(s).
     */
    protected boolean validateEvaluatedText(final String evaluatedText) {
        final IScanner commentScanner = ToolFactory.createScanner(true, false, false, false);
        commentScanner.setSource(evaluatedText.toCharArray());

        try {
            int token = commentScanner.getNextToken();
            while (Utils.isComment(token)) {
                token = commentScanner.getNextToken();
            }

            if (token == ITerminalSymbols.TokenNameEOF) {
                return true;
            }
        } catch (InvalidInputException e) {/* ignore */}

        setError(HEADER_ERROR_MSG);
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ITemplateContentAssistant[] getTemplateContentAssistants() {
        return JAutodocPlugin.getContext().getHeaderTemplateContentAssistants(properties);
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.preferences.templates.AbstractEditTextDialog#getTitle()
     */
    protected String getTitle() {
        return HEADER_TITLE;
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.preferences.templates.AbstractEditTextDialog#getHint()
     */
    protected String getHint() {
        return HEADER_HINT;
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.preferences.templates.AbstractEditTextDialog#getExportFileName()
     */
    protected String getExportFileName() {
        return "jautodoc_header.txt";
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.preferences.templates.AbstractEditTextDialog#getFilterExtensions()
     */
    protected String[] getFilterExtensions() {
        return new String[] {"*.txt", "*.*"};
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.preferences.templates.AbstractEditTextDialog#getLineOffset()
     */
    protected int getLineOffset() {
        return 1;
    }
}
