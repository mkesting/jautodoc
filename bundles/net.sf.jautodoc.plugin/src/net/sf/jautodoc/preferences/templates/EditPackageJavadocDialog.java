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

import org.eclipse.swt.widgets.Shell;


/**
 * Dialog for editing a Package Javadoc Template.
 */
public class EditPackageJavadocDialog  extends AbstractEditTextDialog implements Constants {

    private final boolean usePackageInfo;

    /**
     * Instantiates a new dialog for editing a Package Javadoc
     * Template.
     *
     * @param parentShell the parent shell
     * @param packageDocText the Package Javadoc Template
     * @param properties the properties to use
     * @param usePackageInfo use package-info.java
     */
    public EditPackageJavadocDialog(final Shell parentShell, final String packageDocText,
            final Map<String, String> properties, final boolean usePackageInfo) {
        super(parentShell, packageDocText, properties);
        this.usePackageInfo = usePackageInfo;
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.preferences.templates.AbstractEditTextDialog#validateEvaluatedText(java.lang.String)
     */
    protected boolean validateEvaluatedText(final String evaluatedText) {
        // no validation
        return true;
    }

    @Override
    protected ITemplateContentAssistant[] getTemplateContentAssistants() {
        return JAutodocPlugin.getContext().getPackageDocTemplateContentAssistants(properties);
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.preferences.templates.AbstractEditTextDialog#getTitle()
     */
    protected String getTitle() {
        return PKG_DOC_TITLE;
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.preferences.templates.AbstractEditTextDialog#getHint()
     */
    protected String getHint() {
        return PKG_DOC_HINT;
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.preferences.templates.AbstractEditTextDialog#getExportFileName()
     */
    protected String getExportFileName() {
        return usePackageInfo ? "package-info.java" : "package.html";
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.preferences.templates.AbstractEditTextDialog#getFilterExtensions()
     */
    protected String[] getFilterExtensions() {
        return usePackageInfo ? new String[] {"*.java", "*.*"} : new String[] {"*.html", "*.*"};
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.preferences.templates.AbstractEditTextDialog#getLineOffset()
     */
    protected int getLineOffset() {
        return 2;
    }
}
