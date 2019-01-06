/*******************************************************************
 * Copyright (c) 2006 - 2014, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.cleanup;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.Configuration;
import net.sf.jautodoc.preferences.ConfigurationManager;
import net.sf.jautodoc.utils.LineDelimiterConverter;
import net.sf.jautodoc.utils.SourceUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.jdt.ui.cleanup.ICleanUpFix;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.ltk.core.refactoring.CategorizedTextEditGroup;
import org.eclipse.ltk.core.refactoring.GroupCategory;
import org.eclipse.ltk.core.refactoring.GroupCategorySet;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

/**
 * The header clean up fix.
 */
public class AddHeaderCleanUpFix implements ICleanUpFix {

    private final TextEdit textEdit;
    private final String description;
    private final ICompilationUnit compUnit;


    /**
     * Creates the header clean up fix.
     *
     * @param compUnit the related compilation unit
     * @param replaceHeader replace header flag
     * @return the clean up fix
     * @throws Exception an exception occurred
     */
    public static ICleanUpFix createCleanUp(final ICompilationUnit compUnit, final boolean replaceHeader)
            throws Exception {

        // no package or import statement -> can't distinguish between header and Javadoc of the first type.
        final ISourceReference packageOrImportReference = SourceUtils.getPackageOrImportReference(compUnit);
        if (packageOrImportReference == null) {
            return null;
        }

        final Document document = new Document(compUnit.getBuffer().getContents());
        final String lineDelimiter = TextUtilities.getDefaultLineDelimiter(document);

        final IScanner commentScanner = ToolFactory.createScanner(true, false, false, false);
        commentScanner.setSource(document.get().toCharArray());

        final Configuration config = ConfigurationManager.getConfiguration(compUnit);

        // find existing header
        final ISourceRange poiRange = packageOrImportReference.getSourceRange();
        final ISourceRange commentRange = SourceUtils.findCommentSourceRange(document, 0,
                poiRange.getOffset() + poiRange.getLength(), commentScanner, !config.isMultiCommentHeader());
        if (commentRange.getLength() > 0 && !replaceHeader) {
            return null;
        }

        final String existingHeader = document.get(commentRange.getOffset(), commentRange.getLength());

        // create new header
        String newHeader = JAutodocPlugin.getContext().getTemplateManager().evaluateTemplate(compUnit,
                config.getHeaderText(), "File Header", config.getProperties());
        newHeader = LineDelimiterConverter.convert(newHeader, lineDelimiter);

        if (existingHeader.length() == 0) {
            newHeader += lineDelimiter;
        }

        // create the fix if header changed
        if (!SourceUtils.isSameComment(existingHeader, newHeader)) {
            final TextEdit textEdit = (commentRange.getLength() == 0) ? new InsertEdit(commentRange.getOffset(), newHeader)
                    : new ReplaceEdit(commentRange.getOffset(), commentRange.getLength(), newHeader);

            final String description = (commentRange.getLength() == 0) ? "Add file header" : "Replace existing file header";

            return new AddHeaderCleanUpFix(compUnit, textEdit, description);
        }
        return null;
    }

    private AddHeaderCleanUpFix(final ICompilationUnit compUnit, final TextEdit textEdit, final String description) {
        this.compUnit = compUnit;
        this.textEdit = textEdit;
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public CompilationUnitChange createChange(final IProgressMonitor progressMonitor) throws CoreException {
        final CompilationUnitChange result= new CompilationUnitChange(description, compUnit);
        result.setEdit(textEdit);
        result.addTextEditGroup(new CategorizedTextEditGroup(description,
                new GroupCategorySet(new GroupCategory(description, description, description))));
        return result;
    }
}
