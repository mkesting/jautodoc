/*******************************************************************
 * Copyright (c) 2006 - 2023, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.source;

import java.util.HashMap;
import java.util.Map;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.Configuration;
import net.sf.jautodoc.preferences.Constants;
import net.sf.jautodoc.utils.LineDelimiterConverter;
import net.sf.jautodoc.utils.SourceUtils;
import net.sf.jautodoc.utils.TextEditHelper;
import net.sf.jautodoc.utils.Utils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;


/**
 * Class for source code manipulation.
 */
public class SourceManipulator extends AbstractSourceProcessor {
    private String lineDelimiter;
    private MultiTextEdit textEdit;
    private Map<TextEdit, String> changeDescriptions;

    private int cursorPosition;
    private int cursorOffset;
    private boolean showPreview;
    private boolean forceAddHeader;

    /**
     * Instantiates a new source manipulator.
     *
     * @param compUnit the compilation unit
     */
    public SourceManipulator(ICompilationUnit compUnit) {
        this(compUnit, null);
    }

    /**
     * Instantiates a new source manipulator.
     *
     * @param compUnit the compilation unit
     * @param config the configuration to use
     */
    public SourceManipulator(ICompilationUnit compUnit, Configuration  config) {
        super(compUnit, config);
    }

    /**
     * Adds javadoc for all members of the assigned compilation unit.
     *
     * @param monitor the progress monitor
     * @throws Exception exception occured
     */
    public void addJavadoc(IProgressMonitor monitor) throws Exception {
        doProcessing(config, monitor);
    }

    /**
     * Adds javadoc to the given members.
     *
     * @param members the members
     * @param monitor the progress monitor
     * @throws Exception exception occured
     */
    public void addJavadoc(IMember[] members, IProgressMonitor monitor) throws Exception {
        doProcessing(members, monitor);
    }

    /**
     * Sets the current cursor position.
     *
     * @param cursorPosition the current cursor position
     */
    public void setCursorPosition(int cursorPosition) {
        this.cursorOffset   = 0;
        this.cursorPosition = cursorPosition;
    }

    /**
     * Gets the new cursor position.
     *
     * @return the new cursor position
     */
    public int getCursorPosition() {
        return cursorPosition + cursorOffset;
    }

    /**
     * Sets a flag to force file header creation independent
     * of the configuration settings.
     *
     * @param forceAddHeader true, if header should be added.
     */
    public void setForceAddHeader(boolean forceAddHeader) {
        this.forceAddHeader = forceAddHeader;
    }

    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
    }

    public MultiTextEdit getChanges() {
        return textEdit;
    }

    public Map<TextEdit, String> getChangeDescriptions() {
        return changeDescriptions;
    }

    @Override
    protected void startProcessing() {
        textEdit = new MultiTextEdit();
        changeDescriptions = new HashMap<TextEdit, String>();
        lineDelimiter = TextUtilities.getDefaultLineDelimiter(document);
    }

    @Override
    protected void processFileHeader() throws Exception {
        addFileHeader();
    }

    @Override
    protected void processTodoForAutodoc(final IMember[] members) throws Exception {
        if (config.isAddTodoForAutodoc() && members.length > 1) {
            addTodoForAutoDoc();
        }
    }

    @Override
    protected void processMember(final IMember member) throws Exception {
        addJavadoc(member);
    }

    @Override
    protected void stopProcessing() throws Exception {
        if (!showPreview && applyTextEdits()) {
            compUnit.getBuffer().setContents(document.get());
            compUnit.reconcile(ICompilationUnit.NO_AST, false, null, null);
        }
    }

    @Override
    protected String getTaskName() {
        return Constants.TITLE_JDOC_TASK;
    }

    private void addJavadoc(final IMember member) throws Exception {
        final ISourceRange docRange = SourceUtils.findJavadocSourceRange(member, commentScanner);

        JavadocInfo jdi = new JavadocInfo();

        // parse existing javadoc
        String existingJavadoc = "";
        if (docRange.getLength() > 0
                && (config.isKeepExistingJavadoc() || config.isCompleteExistingJavadoc())
                && (!config.isGetterSetterFromField() || !config.isGetterSetterFromFieldReplace()
                        || !SourceUtils.isGetterSetter(member))) {
            existingJavadoc = document.get(docRange.getOffset(), docRange.getLength());
            jdi.parseJavadoc(existingJavadoc);
            if (config.isKeepExistingJavadoc() && !jdi.isEmpty()) {
                return;
            }
        }

        // create/complete javadoc
        boolean inherited = false;
        String newJavadoc = "";

        final String indent = SourceUtils.getIndentionString(document, member);

        if (member instanceof IType) {
            if (config.isCreateDummyComment()) {
                jdi = javadocCreator.applyTemplate(member, jdi);
            }
            newJavadoc = javadocCreator.createJavadoc((IType) member, indent, lineDelimiter, jdi,
                    document, commentScanner);
        }
        else if (member instanceof IField) {
            if (config.isCreateDummyComment()) {
                jdi = javadocCreator.applyTemplate(member, jdi);
            }
            newJavadoc = javadocCreator.createJavadoc((IField)member, indent, lineDelimiter, jdi);
        }
        else if (member instanceof IMethod) {
            // check for inherited doc
            if (jdi.isEmpty()) { // keep existing
                newJavadoc = SourceUtils.getInheritedJavadoc((IMethod)member, indent, lineDelimiter);
                inherited = (newJavadoc != null && newJavadoc.length() > 0);
            }
            else if (jdi.isInheritDoc()) {
                return; // nothing to do
            }

            if (!inherited) {
                if (config.isGetterSetterFromField()) {
                    newJavadoc = javadocCreator.createMethodJavadocFromField((IMethod) member, indent, lineDelimiter,
                            jdi, document, commentScanner);
                }

                if (newJavadoc == null || newJavadoc.length() == 0) {
                    if (config.isCreateDummyComment()) {
                        jdi = javadocCreator.applyTemplate(member, jdi);
                    }
                    newJavadoc = javadocCreator.createJavadoc((IMethod) member, indent, lineDelimiter, jdi, document,
                            commentScanner);
                }
            }
        }

        // keep existing format
        if (SourceUtils.isSameComment(existingJavadoc, newJavadoc)) {
            return;
        }

        // format
        if (config.isUseEclipseFormatter() && !inherited) {
            newJavadoc = JavadocFormatter.getInstance().format(newJavadoc, indent, lineDelimiter);
        }

        // add empty line in front
        if (Utils.needsLeadingEmptyLine(document, member, docRange)) {
            newJavadoc  = lineDelimiter + indent + newJavadoc;
        }

        // for new javadoc add new line
        if (docRange.getLength() == 0) {
            newJavadoc += lineDelimiter + indent;
        }

        // add to buffer
        if ((docRange.getLength() == 0 || !config.isKeepExistingJavadoc()) &&
                Utils.needsReplacement(document, member, docRange, newJavadoc, inherited)) {
            doReplacement(docRange, newJavadoc, (docRange.getLength() == 0 ? "Add Javadoc" : (config
                    .isCompleteExistingJavadoc() ? "Complete existing Javadoc" : "Replace existing Javadoc")));
        }
    }

    private void addFileHeader() throws Exception {
        if (!config.isAddHeader() && !forceAddHeader) {
            return;
        }

        // no package or import statement -> can't distinguish between header and Javadoc of the first type.
        final ISourceReference element = SourceUtils.getPackageOrImportReference(compUnit);
        if (element == null) {
            return;
        }

        final ISourceRange range = element.getSourceRange();
        final ISourceRange commentRange = SourceUtils.findCommentSourceRange(document, 0, range.getOffset()
                + range.getLength(), commentScanner, !config.isMultiCommentHeader());
        if (commentRange.getLength() > 0 && !config.isReplaceHeader()) {
            return;
        }

        final String existingHeader = document.get(commentRange.getOffset(), commentRange.getLength());

        String newHeader = JAutodocPlugin.getContext().getTemplateManager().evaluateTemplate(compUnit,
                config.getHeaderText(), "File Header", config.getProperties());
        newHeader = LineDelimiterConverter.convert(newHeader, lineDelimiter);

        if (existingHeader.length() == 0) {
            newHeader += lineDelimiter;
        }

        if (!SourceUtils.isSameComment(existingHeader, newHeader)) {
            doReplacement(commentRange, newHeader, existingHeader.length() == 0 ? "Add file header"
                    : "Replace existing file header");
        }
    }

    private void addTodoForAutoDoc() throws Exception {
        final IType type = SourceUtils.getFirstType(compUnit);
        if (type == null) {
            return;
        }

        final ISourceRange range = type.getSourceRange();
        final int start = range.getOffset();
        final int length = range.getLength();

        final String source = document.get(start, length);
        if (!source.startsWith(Constants.TODO_FOR_AUTODOC)) {
            doReplacement(new SourceRange(start, 0), Constants.TODO_FOR_AUTODOC + lineDelimiter,
                    "Add ToDo for auto-generated Javadoc");
        }
    }

    private void doReplacement(final ISourceRange range, String replacement, final String description) throws Exception {
        replacement = LineDelimiterConverter.convert(replacement, lineDelimiter);

        final TextEdit te = (range.getLength() == 0) ? new InsertEdit(range.getOffset(), replacement)
                : new ReplaceEdit(range.getOffset(), range.getLength(), replacement);

        textEdit.addChild(te);
        changeDescriptions.put(te, description);

        if (cursorPosition >= range.getOffset()) {
            cursorOffset += replacement.length() - range.getLength();
        }
    }

    private boolean applyTextEdits() throws Exception {
        if (textEdit.hasChildren()) {
            new TextEditHelper(document, textEdit).apply();
            return true;
        }
        return false;
    }
}
