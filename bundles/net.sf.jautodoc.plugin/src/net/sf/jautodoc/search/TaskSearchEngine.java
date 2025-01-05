/*******************************************************************
 * Copyright (c) 2006 - 2025, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import java.util.HashSet;
import java.util.Set;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.Constants;
import net.sf.jautodoc.search.TaskSearchMatch.FindingId;
import net.sf.jautodoc.search.TaskSearchPattern.MissingTag;
import net.sf.jautodoc.source.AbstractSourceProcessor;
import net.sf.jautodoc.source.JavadocInfo;
import net.sf.jautodoc.source.JavadocTag;
import net.sf.jautodoc.utils.SourceUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.search.ui.text.Match;

/**
 * Processes a given compilation unit according to the search pattern and adds all findings to the
 * current search result.
 */
public class TaskSearchEngine extends AbstractSourceProcessor {
    private final TaskSearchResult searchResult;
    private final TaskSearchPattern searchPattern;


    public TaskSearchEngine(final ICompilationUnit compUnit, final TaskSearchPattern searchPattern,
            final TaskSearchResult searchResult) {
        super(compUnit);
        this.searchResult = searchResult;
        this.searchPattern = searchPattern;
    }

    public void search(IProgressMonitor monitor) throws Exception {
        doProcessing(searchPattern, monitor);
    }

    @Override
    protected void startProcessing() throws Exception {
        searchResult.removeMatches(searchResult.getMatches(compUnit));
    }

    @Override
    protected void processFileHeader() throws Exception {
        if (!searchPattern.isMissingHeader() && !searchPattern.isOutdatedHeader()) {
            return;
        }

        final ISourceReference element = SourceUtils.getPackageOrImportReference(compUnit);
        if (element == null) {
            return;
        }

        final ISourceRange range = element.getSourceRange();
        final ISourceRange commentRange = SourceUtils.findCommentSourceRange(document,
                0, range.getOffset() + range.getLength(), commentScanner, !config.isMultiCommentHeader());
        if (commentRange.getLength() == 0) {
            if (searchPattern.isMissingHeader()) {
                addMatch(compUnit, 0, 0, FindingId.MISSING_HEADER, "Missing file header");
            }
            return;
        }

        if (!searchPattern.isOutdatedHeader()) {
            return;
        }

        final int offset = commentRange.getOffset();
        final int length = commentRange.getLength();

        final String existingHeader = document.get(offset, length);
        final String newHeader = JAutodocPlugin.getContext().getTemplateManager().evaluateTemplate(compUnit,
                config.getHeaderText(), "File Header", config.getProperties());

        if (!SourceUtils.isSameComment(existingHeader, newHeader, false)) {
            addMatch(compUnit, offset, length, FindingId.OUTDATED_HEADER, "Outdated file header");
        }
    }

    @Override
    protected void processTodoForAutodoc(IMember[] members) throws Exception {
        if (!searchPattern.isTodoForGenerated()) {
            return;
        }

        final IType type = SourceUtils.getFirstType(compUnit);
        if (type == null) {
            return;
        }

        final ISourceRange range = type.getSourceRange();
        final int offset = range.getOffset();
        final int length = range.getLength();

        final String source = document.get(offset, length);
        if (source.startsWith(Constants.TODO_FOR_AUTODOC)) {
            addMatch(compUnit, offset, Constants.TODO_FOR_AUTODOC.length(), FindingId.TODO_FOR_AUTODOC,
                    "ToDo for generated Javadoc");
        }
    }

    @Override
    protected void processMember(final IMember member) throws Exception {
        searchResult.removeMatches(searchResult.getMatches(member));

        if (!needsJavadocCheck(member)) {
            return;
        }

        final ISourceRange docRange = SourceUtils.findJavadocSourceRange(member, commentScanner);
        final String existingJavadoc = document.get(docRange.getOffset(), docRange.getLength());

        final JavadocInfo jdi = new JavadocInfo();
        jdi.parseJavadoc(existingJavadoc);

        if (searchPattern.isMissingJavadoc() && !jdi.hasComment()) {
            addMatch(member, FindingId.MISSING_JAVADOC, "Missing Javadoc comment");
        }

        if (jdi.isEmpty()) {
            return; // only one finding, if there is nothing.
        }

        if (searchPattern.isMissingPeriods() && jdi.hasComment() && !jdi.containsPeriod()) {
            addMatch(member, FindingId.MISSING_PERIOD, "Missing period on first sentence");
        }

        if (searchPattern.isGeneratedJavadoc()) {
            processGeneratedJavadoc(member, existingJavadoc);
        }

        if (searchPattern.isSearchMissingTags()) {
            processSearchMissingTags(member, jdi);
        }

        if (member instanceof IType) {
            processTypeJavadoc((IType) member, jdi);
        }

        if (member instanceof IMethod) {
            processMethodJavadoc((IMethod) member, jdi);
        }
    }

    @Override
    protected void stopProcessing() throws Exception {
        // nothing to do
    }

    @Override
    protected String getTaskName() {
        return "JAutodoc Search";
    }

    private void processTypeJavadoc(final IType type, final JavadocInfo jdi) throws JavaModelException {
        if (searchPattern.isMissingParamTag()) {
            processParameterTags(type, jdi);
        }
    }

    private void processMethodJavadoc(final IMethod method, final JavadocInfo jdi) throws JavaModelException {
        if (searchPattern.isMissingParamTag()) {
            processParameterTags(method, jdi);
        }

        if (searchPattern.isMissingReturnTag()) {
            processReturnTag(method, jdi);
        }

        if (searchPattern.isMissingThrowsTag()) {
            processThrowsTags(method, jdi);
        }
    }

    private void processGeneratedJavadoc(final IMember member, final String existingJavadoc)
            throws Exception {

        String generatedJavadoc = "";
        if (member instanceof IType) {
            final JavadocInfo emptyJdi = new JavadocInfo();
            final JavadocInfo generatedJdi = javadocCreator.applyTemplate(member, emptyJdi);
            generatedJavadoc = javadocCreator.createJavadoc((IType) member, "", "\n", generatedJdi, document, commentScanner);
        }
        else if (member instanceof IField) {
            final JavadocInfo emptyJdi = new JavadocInfo();
            final JavadocInfo generatedJdi = javadocCreator.applyTemplate(member, emptyJdi);
            generatedJavadoc = javadocCreator.createJavadoc((IField) member, "", "\n", generatedJdi);
        }
        else if (member instanceof IMethod) {
            generatedJavadoc = processGeneratedMethodJavadoc((IMethod) member);
        }

        if (!isEmpty(generatedJavadoc) && SourceUtils.isSameComment(existingJavadoc, generatedJavadoc, false)) {
            addMatch(member, FindingId.GENERATED_JAVADOC, "Generated Javadoc");
        }
    }

    private String processGeneratedMethodJavadoc(final IMethod method) throws Exception {
        String generatedJavadoc = "";
        if (config.isGetterSetterFromField()) {
            final JavadocInfo emptyJdi = new JavadocInfo();
            generatedJavadoc = javadocCreator.createMethodJavadocFromField(method, "", "\n", emptyJdi,
                    document, commentScanner);
        }

        if (isEmpty(generatedJavadoc)) {
            final JavadocInfo emptyJdi = new JavadocInfo();
            final JavadocInfo generatedJdi = javadocCreator.applyTemplate(method, emptyJdi);
            generatedJavadoc = javadocCreator.createJavadoc(method, "", "\n", generatedJdi, document, commentScanner);
        }
        return generatedJavadoc;
    }

    private void processParameterTags(final IType type, final JavadocInfo jdi) throws JavaModelException {
        final Set<String> parameters = processMissingParameterTags(type, jdi);
        processInvalidParameterTags(type, parameters, jdi);
    }

    private void processParameterTags(final IMethod method, final JavadocInfo jdi) throws JavaModelException {
        final Set<String> parameters = processMissingParameterTags(method, jdi);
        processInvalidParameterTags(method, parameters, jdi);
    }

    private Set<String> processMissingParameterTags(final IType type, final JavadocInfo jdi) throws JavaModelException {
        return processMissingParameterTags(type, SourceUtils.getParameterNames(type), jdi);
    }

    private Set<String> processMissingParameterTags(final IMethod method, final JavadocInfo jdi) throws JavaModelException {
        return processMissingParameterTags(method, SourceUtils.getParameterNames(method), jdi);
    }

    private Set<String> processMissingParameterTags(final IMember member, final String[] parameterNames,
            final JavadocInfo jdi) throws JavaModelException {

        final Set<String> validParameters = new HashSet<String>();

        for (String parameter : parameterNames) {
            final JavadocTag paramTag = jdi.getParamDoc().get(parameter);
            if (paramTag == null || paramTag.getComments().isEmpty()) {
                addMatch(member, FindingId.MISSING_PARAM, "Missing @param tag for '" + parameter + "'");
            }
            validParameters.add(parameter);
        }
        return validParameters;
    }

    private void processInvalidParameterTags(final IMember member, final Set<String> validParameters, final JavadocInfo jdi)
            throws JavaModelException {

        for (String parameter : jdi.getParamDoc().keySet()) {
            if (!validParameters.contains(parameter)) {
                addMatch(member, FindingId.INVALID_PARAM, "Invalid @param tag: " + parameter);
            }
        }
    }

    private void processReturnTag(final IMethod method, final JavadocInfo jdi) throws JavaModelException {
        if (jdi.getReturnDoc().isEmpty()
                && !"void".equals(Signature.getSignatureSimpleName(method.getReturnType()))) {
            addMatch(method, FindingId.MISSING_RETURN, "Missing @return tag");
        }
    }

    private void processThrowsTags(final IMethod method, final JavadocInfo jdi) throws JavaModelException {
        final Set<String> validExceptionTypes = processMissingThrowsTags(method, jdi);
        processInvalidThrowsTags(method, validExceptionTypes, jdi);
    }

    private Set<String> processMissingThrowsTags(final IMethod method, final JavadocInfo jdi) throws JavaModelException {
        final Set<String> validExceptionTypes = new HashSet<String>();

        for (String exceptionTypeSignature : method.getExceptionTypes()) {
            final String exceptionType = Signature.getSignatureSimpleName(exceptionTypeSignature);

            final JavadocTag throwsTag = jdi.getThrowsDoc().get(exceptionType);
            if (throwsTag == null || throwsTag.getComments().isEmpty()) {
                addMatch(method, FindingId.MISSING_THROWS, "Missing @throws tag for '" + exceptionType + "'");
            }
            validExceptionTypes.add(exceptionType);
        }
        return validExceptionTypes;
    }

    private void processInvalidThrowsTags(final IMethod method, final Set<String> validExceptionTypes,
            final JavadocInfo jdi) throws JavaModelException {

        for (String exceptionType : jdi.getThrowsDoc().keySet()) {
            if (!validExceptionTypes.contains(exceptionType)) {
                addMatch(method, FindingId.INVALID_THROWS, "Invalid @throws tag: " + exceptionType);
            }
        }
    }

    private void processSearchMissingTags(final IMember member, final JavadocInfo jdi) throws JavaModelException {
        for (final MissingTag missingTag : searchPattern.getMissingTags()) {

            if (!missingTag.isOnlyEmpty() && !hasTag( missingTag.getName(), jdi)) {
                addMatch(member, FindingId.MISSING_TAG, "Missing tag: " + missingTag.getName());
            }
            else if (!missingTag.isOnlyMissing()) {
                for (final JavadocTag javadocTag : jdi.getOtherDoc()) {
                    if (javadocTag.getTypeName().equals(missingTag.getName()) && javadocTag.getComments().size() == 0) {
                        addMatch(member, FindingId.MISSING_TAG, "Missing text on tag: " + missingTag.getName());
                    }
                }
            }
        }
    }

    private boolean hasTag(final String tagTypeName, final JavadocInfo jdi) {
        boolean exists = false;
        for (final JavadocTag currentTag : jdi.getOtherDoc()) {
            if (currentTag.getTypeName().equals(tagTypeName)) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    private void addMatch(final IMember member, final FindingId id, final String message) throws JavaModelException {
        final ISourceRange sr = member.getNameRange();
        addMatch(member, sr.getOffset(), sr.getLength(), id, message);
    }

    private void addMatch(final IJavaElement element, final int offset, final int length,
            final FindingId id, final String message) {

        boolean matchExists = false;

        final Match[] matches = searchResult.getMatches(element);
        for (Match match : matches) {
            if (match.getOffset() == offset && match.getLength() == length) {
                final TaskSearchMatch taskSearchMatch = (TaskSearchMatch)match;
                taskSearchMatch.addFinding(id, message);
                matchExists = true;
                break;
            }
        }

        if (!matchExists) {
            searchResult.addMatch(new TaskSearchMatch(element, offset, length, id, message));
        }
    }

    private boolean needsJavadocCheck(final IMember member) throws JavaModelException {
        if (member instanceof IType) {
            return SourceUtils.isMatchingType((IType) member, searchPattern) && (searchPattern.isMissingJavadoc()
                    || searchPattern.isMissingPeriods()
                    || searchPattern.isGeneratedJavadoc()
                    || searchPattern.isSearchMissingTags() && searchPattern.getMissingTags().length > 0);
        }

        if (member instanceof IField) {
            return SourceUtils.isMatchingField((IField) member, searchPattern) && (searchPattern.isMissingJavadoc()
                    || searchPattern.isMissingPeriods()
                    || searchPattern.isGeneratedJavadoc()
                    || searchPattern.isSearchMissingTags() && searchPattern.getMissingTags().length > 0);
        }

        if (member instanceof IMethod) {
            return SourceUtils.isMatchingMethod((IMethod)member, searchPattern) && (searchPattern.isMissingJavadoc()
                || searchPattern.isMissingPeriods()
                || searchPattern.isMissingParamTag()
                || searchPattern.isMissingReturnTag()
                || searchPattern.isMissingThrowsTag()
                || searchPattern.isGeneratedJavadoc()
                || searchPattern.isSearchMissingTags() && searchPattern.getMissingTags().length > 0);
        }

        return false;
    }

    private boolean isEmpty(final String string) {
        return string == null || string.trim().length() == 0;
    }
}
