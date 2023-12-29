/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.source;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.Configuration;
import net.sf.jautodoc.preferences.Constants;
import net.sf.jautodoc.preferences.GetSetFromFieldReplacement;
import net.sf.jautodoc.preferences.replacements.Replacement;
import net.sf.jautodoc.source.JavadocTag.TagComparator;
import net.sf.jautodoc.utils.SourceUtils;
import net.sf.jautodoc.utils.StringUtils;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jface.text.IDocument;


/**
 * Creates Javadoc for the given elements.
 */
public class JavadocCreator {
    private static final String[] EMPTY_ARR = new String[0];

    private final Configuration config;

    /**
     * Instantiates a new Javadoc creator.
     *
     * @param config the configuration to use
     */
    public JavadocCreator(final Configuration config) {
        this.config = config;
    }

    /**
     * Creates Javadoc for the given type.
     *
     * @param type the type
     * @param indent the indent string
     * @param lineSeparator the line separator
     * @param jdi the Javadoc info
     * @param document the document
     * @param scanner the scanner
     * @return the resulting Javadoc string
     * @throws JavaModelException failure in Java model
     */
    public String createJavadoc(final IType type, final String indent, final String lineSeparator,
            final JavadocInfo jdi, final IDocument document, final IScanner scanner) throws JavaModelException {

        final List<String> text = jdi.getComment();
        if (text.isEmpty()) {
            if (config.isCreateDummyComment()) {
                if (type.isClass()) {
                    text.add(Constants.JDOC_CLASS + " " + type.getElementName() + Constants.DOT);
                }
                else if (type.isInterface()) {
                    text.add(Constants.JDOC_INTERFACE + " " + type.getElementName() + Constants.DOT);
                }
                else if (type.isEnum()) {
                    text.add(Constants.JDOC_ENUM + " " + type.getElementName() + Constants.DOT);
                }
                else {
                    text.add(Constants.JDOC_THE_UPPER + " " + type.getElementName() + Constants.DOT);
                }
            }
            else {
                text.add("");
            }
        }
        else {
            checkForDot(text);
        }

        createJavadocForTypeParams(jdi.getParamDoc(), type, type.getTypeParameters());
        if (type.isRecord()) {
            createJavadocForRecordComponents(jdi.getParamDoc(), type, document, scanner);
        }
        return createJavadocString(indent, lineSeparator, jdi, SourceUtils.getParameterNames(type));
    }

    /**
     * Creates Javadoc for the given field.
     *
     * @param field the field
     * @param indent the indent string
     * @param lineSeparator the line separator
     * @param jdi the Javadoc info
     * @return the resulting Javadoc string
     * @throws JavaModelException failure in Java model
     */
    public String createJavadoc(final IField field, final String indent, final String lineSeparator,
            final JavadocInfo jdi) throws JavaModelException {

        final List<String> text = jdi.getComment();
        if (text.isEmpty()) {
            if (config.isCreateDummyComment()) {
                int flags = field.getFlags();
                if (Flags.isStatic(flags) && Flags.isFinal(flags)) {
                    text.add(Constants.JDOC_CONSTANT + " " + field.getElementName() + Constants.DOT);
                }
                else {
                    String comment = CommentManager.createComment(config, field.getElementName(),
                            CommentManager.FIELD, true, true);
                    if (!comment.startsWith(Constants.JDOC_THE_UPPER) && !comment.startsWith(Constants.JDOC_THE_LOWER)) {
                        comment = Constants.JDOC_THE_UPPER + " " + comment;
                    }
                    else {
                        // starts with 'the'
                        comment = StringUtils.firstToUpper(comment);
                    }
                    text.add(comment + Constants.DOT);
                }
            }
            else {
                text.add("");
            }
        }
        else {
            checkForDot(text);
        }

        return createFieldJavadocString(indent, lineSeparator, jdi);
    }

    /**
     * Creates method javadoc of getter/setter from field.
     *
     * @param method the getter/setter method
     * @param indent the indent string
     * @param lineSeparator the line separator
     * @param methodJdi the existing method Javadoc info
     * @param document the document
     * @param scanner the scanner
     * @return the resulting Javadoc string or empty if not applicable
     * @throws Exception failure in Java model
     */
    public String createMethodJavadocFromField(final IMethod method, final String indent, final String lineSeparator,
            final JavadocInfo methodJdi, final IDocument document, final IScanner scanner) throws Exception {
        final IField field = SourceUtils.getFieldOfGetterSetter(method);
        if (field == null || !field.exists()) {
            return "";
        }

        final JavadocInfo fieldJdi = new JavadocInfo();
        final ISourceRange docRange = SourceUtils.findJavadocSourceRange(field, scanner);

        fieldJdi.parseJavadoc(document.get(docRange.getOffset(), docRange.getLength()));
        if (fieldJdi.getComment().isEmpty()) {
            return "";
        }
        return createJavadocFromField(method, indent, lineSeparator, methodJdi, fieldJdi);
    }

    private String createJavadocFromField(final IMethod method, final String indent, final String lineSeparator,
            final JavadocInfo methodJdi, final JavadocInfo fieldJdi) throws JavaModelException {

        if (fieldJdi.getComment().isEmpty()) {
            return "";
        }

        // append all lines
        final StringBuilder text = new StringBuilder(fieldJdi.getComment().get(0).trim());
        for (int i = 1; i < fieldJdi.getComment().size(); ++i) {
            text.append(" " + fieldJdi.getComment().get(i).trim());
        }

        // get text up to first dot
        final int dotIdx = text.indexOf(Constants.DOT);
        final String firstSentence = text.substring(0, dotIdx >= 0 ? dotIdx : text.length());
        final String fieldComment = config.isGetterSetterFromFieldFirst() ? firstSentence : text.toString();
        if (fieldComment.length() == 0) {
            return "";
        }

        final String methodName = method.getElementName();
        final String prefix = StringUtils.getPrefix(methodName);
        if (!"get".equals(prefix) && !"set".equals(prefix) && !"is".equals(prefix)) {
            return "";
        }

        // create comment (i.e. Get the <field comment>)
        final String[] commentStart = config.getReplacementManager().doReplacements(
                new String[] {prefix}, Replacement.SCOPE_METHOD);

        final GetSetFromFieldReplacement replacement = findGetSetFromFieldReplacement(fieldComment);
        final String comment = prepareFieldCommentForJavadocFromField(fieldComment, replacement);

        if (methodJdi.getComment().isEmpty()) {
            final String methodComment = StringUtils.firstToUpper(commentStart[0]) + " " + comment + Constants.DOT;
            methodJdi.getComment().add(methodComment.replaceAll("\\.\\.", "."));
        } else {
            checkForDot(methodJdi.getComment());
        }

        // parameter comment for setters - return comment for getter
        if ("set".equals(prefix) && method.getParameterNames().length > 0) {
            final String paramName = method.getParameterNames()[0];

            JavadocTag paramTag = methodJdi.getParamDoc().get(paramName);
            if (paramTag == null) {
                paramTag = new JavadocTag(JavadocTag.TAG_TYPE_PARAM, paramName);
                methodJdi.getParamDoc().put(paramName, paramTag);
            }

            final List<String> paramComment = paramTag.getComments();
            if (paramComment.isEmpty()) {
                final String separator = replacement.getParameterPrefix().isEmpty() ? "" : " ";
                paramComment.add(replacement.getParameterPrefix()
                        + separator + prepareFieldCommentForJavadocFromField(firstSentence, replacement));
            }
        } else if (methodJdi.getReturnDoc().isEmpty()) {
            final String separator = replacement.getReturnPrefix().isEmpty() ? "" : " ";
            methodJdi.getReturnDoc().add(replacement.getReturnPrefix()
                    + separator + prepareFieldCommentForJavadocFromField(firstSentence, replacement));
        }

        return createJavadocString(indent, lineSeparator, methodJdi, SourceUtils.getParameterNames(method));
    }

    private String prepareFieldCommentForJavadocFromField(final String fieldComment, final GetSetFromFieldReplacement replacement) {
        if (replacement.getFieldPrefix().isEmpty()) {
            return StringUtils.firstToLower(fieldComment);
        }
        return fieldComment.replaceFirst(createFieldPrefixPattern(replacement.getFieldPrefix()), "");
    }

    private GetSetFromFieldReplacement findGetSetFromFieldReplacement(final String fieldComment) {
        GetSetFromFieldReplacement result = null;

        for (final GetSetFromFieldReplacement replacement : config.getGetSetFromFieldReplacements()) {
            if (result == null && replacement.getFieldPrefix().isEmpty()) {
                result = replacement;
                continue;
            }

            if (StringUtils.startsWith(fieldComment, createFieldPrefixPattern(replacement.getFieldPrefix()))) {
                result = replacement;
                break;
            }
        }
        return result == null ? GetSetFromFieldReplacement.EMPTY : result;
    }

    private String createFieldPrefixPattern(final String fieldPrefix) {
        return String.format("(?i)%s\\s+", fieldPrefix); // case-insensitive and terminated by whitespace
    }

    /**
     * Creates Javadoc for the given method.
     *
     * @param method the method
     * @param indent the indent string
     * @param lineSeparator the line separator
     * @param jdi the Javadoc info
     * @param document the document
     * @param scanner the scanner
     * @return the resulting Javadoc string
     * @throws Exception failure in Java model
     */
    public String createJavadoc(final IMethod method, final String indent, final String lineSeparator,
            final JavadocInfo jdi, final IDocument document, final IScanner scanner) throws Exception {

        final List<String> text = jdi.getComment();
        if (text.isEmpty()) {
            if (config.isCreateDummyComment()) {
                if (method.isConstructor()) {
                    text.add(Constants.JDOC_CONSTRUCTOR);
                }
                else if (method.isMainMethod()) {
                    text.add(Constants.JDOC_MAIN);
                }
                else {
                    String comment = CommentManager.createComment(config, method.getElementName(),
                            CommentManager.METHOD, true, true, CommentManager.FIRST_TO_UPPER);
                    text.add(comment + Constants.DOT);
                }
            }
            else {
                text.add("");
            }
        }
        else {
            checkForDot(text);
        }

        final String[] parameterNames = SourceUtils.getParameterNames(method);
        final String[] exceptionTypes = SourceUtils.getExceptionTypes(method, jdi.getThrowsDoc().keySet());

        createJavadocForTypeParams(jdi.getParamDoc(), method, method.getTypeParameters());
        createJavadocForParameters(jdi.getParamDoc(), method, document, scanner);
        createJavadocForReturn(jdi.getReturnDoc(), method);
        createJavadocForExceptions(jdi.getThrowsDoc(), method, exceptionTypes);

        return createJavadocString(indent, lineSeparator, jdi, parameterNames, exceptionTypes);
    }

    /**
     * Apply Javadoc template to the given member.
     *
     * @param member the member
     * @param jdi the Javadoc info
     * @return the resulting javadoc info
     * @throws Exception
     */
    public JavadocInfo applyTemplate(final IMember member, final JavadocInfo jdi) throws Exception {
        final JavadocInfo templateJdi = new JavadocInfo();
        final String text = JAutodocPlugin.getContext().getTemplateManager().applyTemplate(member,
                config.getProperties());
        if (text != null && text.length() > 0) {
            templateJdi.parseJavadoc(text);
        }
        return jdi.isEmpty() ? templateJdi : jdi.merge(templateJdi);
    }

    /**
     * Creates javadoc for the given type parameters.
     *
     * @param paramDoc the parameter doc
     * @param member the related method or generic type
     * @param typeParameters the type parameters
     * @throws JavaModelException failure in Java model
     */
    private void createJavadocForTypeParams(final Map<String, JavadocTag> paramDoc, final IMember member,
            final ITypeParameter[] typeParameters) throws JavaModelException {

        final String[] parameterNames = new String[typeParameters.length];
        SourceUtils.getTypeParameterNames(typeParameters, parameterNames);

        for (final String paramName : parameterNames) {
            JavadocTag paramTag = paramDoc.get(paramName);
            if (paramTag == null) {
                paramTag = new JavadocTag(JavadocTag.TAG_TYPE_PARAM, paramName);
                paramDoc.put(paramName, paramTag);
            }

            final List<String> comments = paramTag.getComments();
            if (!comments.isEmpty()) {
                continue;
            }

            if (!config.isCreateDummyComment()) {
                comments.add("");
                continue;
            }

            // try to apply a template
            applyParameterTemplate(member, "GenericType", paramName, comments);
            if (!comments.isEmpty()) {
                continue;
            }

            // no template -> create dummy doc
            String comment = "the generic type";
            if ("<E>".equals(paramName)) {
                comment = "the element type";
            }
            else if ("<K>".equals(paramName)) {
                comment = "the key type";
            }
            else if ("<N>".equals(paramName)) {
                comment = "the number type";
            }
            else if ("<V>".equals(paramName)) {
                comment = "the value type";
            }
            comments.add(comment);
        }
    }

    /**
     * Creates Javadoc for parameters of the given method.
     *
     * @param paramDoc the existing parameter Javadoc
     * @param method the method
     * @param document the document
     * @param scanner the scanner
     * @throws Exception failure in Java model
     */
    private void createJavadocForParameters(Map<String, JavadocTag> paramDoc, IMethod method, IDocument document,
            IScanner scanner) throws Exception {

        final String[] parameterNames = method.getParameterNames();
        final String[] parameterTypes = method.getParameterTypes();

        for (int i = 0; i < parameterNames.length; ++i) {

            JavadocTag paramTag = paramDoc.get(parameterNames[i]);
            if (paramTag == null) {
                paramTag = new JavadocTag(JavadocTag.TAG_TYPE_PARAM, parameterNames[i]);
                paramDoc.put(parameterNames[i], paramTag);
            }

            final List<String> comments = paramTag.getComments();
            if (!comments.isEmpty()) {
                continue;
            }

            if (!config.isCreateDummyComment()) {
                comments.add("");
                continue;
            }

            if (method.isConstructor() && config.isGetterSetterFromField()) {
                lookupParameterFromField(method, parameterTypes[i], parameterNames[i], comments, document, scanner);
                if (!comments.isEmpty()) {
                    continue;
                }
            }

            // try to apply a template
            applyParameterTemplate(method, parameterTypes[i], parameterNames[i], comments);
            if (!comments.isEmpty()) {
                continue;
            }

            // no template -> create dummy doc
            String comment = "";
            String methodName = method.getElementName();
            if (parameterNames.length == 1 && methodName.startsWith("set") && methodName.length() > "set".length()) {
                // for setXXX-method create comment from method name
                comment = CommentManager.createComment(config, methodName.substring("set".length()),
                        CommentManager.METHOD, true, true);

                // first char to lower, if it does not starts with two upper
                // case letters
                if (comment.length() > 1 && !Character.isUpperCase(comment.charAt(1))) {
                    comment = StringUtils.firstToLower(comment);
                }
            }
            else {
                comment = CommentManager
                        .createComment(config, parameterNames[i], CommentManager.PARAMETER, true, true);
            }

            if (!comment.startsWith(Constants.JDOC_THE_UPPER) && !comment.startsWith(Constants.JDOC_THE_LOWER)) {
                comment = Constants.JDOC_THE_LOWER + " " + comment;
            }

            comments.add(comment);
        }
    }

    /**
     * Creates Javadoc for components of the given record type.
     *
     * @param paramDoc the existing parameter Javadoc
     * @param type the record type
     * @param document the document
     * @param scanner the scanner
     * @throws JavaModelException failure in Java model
     */
    private void createJavadocForRecordComponents(Map<String, JavadocTag> paramDoc, IType type, IDocument document,
            IScanner scanner) throws JavaModelException {

        IField[] recordComponents = type.getRecordComponents();

        for (int i = 0; i < recordComponents.length; ++i) {

            JavadocTag paramTag = paramDoc.get(recordComponents[i].getElementName());
            if (paramTag == null) {
                paramTag = new JavadocTag(JavadocTag.TAG_TYPE_PARAM, recordComponents[i].getElementName());
                paramDoc.put(recordComponents[i].getElementName(), paramTag);
            }

            final List<String> comments = paramTag.getComments();
            if (!comments.isEmpty()) {
                continue;
            }

            if (!config.isCreateDummyComment()) {
                comments.add("");
                continue;
            }

            // try to apply a template
            applyParameterTemplate(type, recordComponents[i].getTypeSignature(), recordComponents[i].getElementName(), comments);
            if (!comments.isEmpty()) {
                continue;
            }

            // no template -> create dummy doc
            String comment = CommentManager
                    .createComment(config, recordComponents[i].getElementName(), CommentManager.PARAMETER, true, true);

            if (!comment.startsWith(Constants.JDOC_THE_UPPER) && !comment.startsWith(Constants.JDOC_THE_LOWER)) {
                comment = Constants.JDOC_THE_LOWER + " " + comment;
            }

            comments.add(comment);
        }
    }

    /**
     * Try to apply a parameter template.
     *
     * @param member the related method or generic type
     * @param type the parameter type
     * @param name the parameter name
     * @param comments the resulting comments
     */
    private void applyParameterTemplate(final IMember member, final String type, final String name,
            final List<String> comments) {
        try {
            final String text = JAutodocPlugin.getContext().getTemplateManager().applyParameterTemplate(
                    member, type, name, config.getProperties());

            if (text != null && text.length() > 0) {
                final JavadocInfo jdi = new JavadocInfo();
                jdi.parseJavadoc(text);

                final JavadocTag paramTag = jdi.getParamDoc().get(name);
                if (paramTag != null) {
                    comments.addAll(paramTag.getComments());
                }
            }
        } catch (Exception e) {
            JAutodocPlugin.getDefault().handleException(e);
        }
    }

    /**
     * Try to lookup parameter comment from related field.
     *
     * @param method the related method
     * @param type the parameter type
     * @param name the parameter name
     * @param comments the resulting comments
     * @param document the document
     * @param scanner the scanner
     * @throws Exception failure in Java model
     */
    private void lookupParameterFromField(IMethod method, String type, String name, List<String> comments,
            IDocument document, IScanner scanner) throws Exception {

        IField field = SourceUtils.getField(method.getDeclaringType(), name);

        if (field != null && field.exists() && field.getTypeSignature().equals(type)) {

            JavadocInfo fieldJdi = new JavadocInfo();
            ISourceRange docRange = SourceUtils.findJavadocSourceRange(field, scanner);

            fieldJdi.parseJavadoc(document.get(docRange.getOffset(), docRange.getLength()));

            comments.addAll(fieldJdi.getComment());
            if (!comments.isEmpty()) {
                comments.set(0, StringUtils.firstToLower(comments.get(0).trim()));
            }
        }
    }

    /**
     * Creates Javadoc for the return value of the given method.
     *
     * @param method the method
     * @param returnDoc the existing return Javadoc
     * @throws JavaModelException failure in Java model
     */
    private void createJavadocForReturn(final List<String> returnDoc, final IMethod method) throws JavaModelException {
        if (!returnDoc.isEmpty()) {
            return;
        }

        String returnType = Signature.getSignatureSimpleName(method.getReturnType());
        if ("void".equals(returnType)) {
            return;
        }

        if (config.isCreateDummyComment()) {
            String prefix = Constants.JDOC_THE_LOWER;
            String elementName = returnType;

            String methodName = method.getElementName();
            if (methodName.startsWith("get") && methodName.length() > "get".length()) {
                // for getXXX-method create comment from method name
                elementName = methodName.substring("get".length());
            }
            else if (returnType.equalsIgnoreCase("boolean")) {
                // for boolean create comment from method name
                // and start with "true, if..."
                prefix = Constants.JDOC_TRUE_IF;
                elementName = methodName;
            }

            String comment = CommentManager.createComment(config, elementName, CommentManager.RETURN, true, true);

            // first char to lower, if it does not starts with two upper case
            // letters
            if (comment.length() > 1 && !Character.isUpperCase(comment.charAt(1))) {
                comment = StringUtils.firstToLower(comment);
            }

            returnDoc.add(prefix + " " + comment);
        }
        else {
            returnDoc.add("");
        }
    }

    /**
     * Creates Javadoc for the exceptions of the given method.
     *
     * @param throwsDoc the existing exception Javadoc
     * @param method the method
     * @param exceptionTypes the exception types
     * @throws JavaModelException failure in Java model
     */
    private void createJavadocForExceptions(final Map<String, JavadocTag> throwsDoc, final IMethod method,
            final String[] exceptionTypes) throws JavaModelException {

        for (final String exceptionType : exceptionTypes) {

            JavadocTag throwsTag = throwsDoc.get(exceptionType);
            if (throwsTag == null) {
                throwsTag = new JavadocTag(JavadocTag.TAG_TYPE_THROWS, exceptionType);
                throwsDoc.put(exceptionType, throwsTag);
            }

            final List<String> comments = throwsTag.getComments();
            if (!comments.isEmpty()) {
                continue;
            }

            if (!config.isCreateDummyComment()) {
                comments.add("");
                continue;
            }

            // try to apply a template
            applyExceptionTemplate(method, exceptionType, comments);
            if (!comments.isEmpty()) {
                continue;
            }

            // no template -> create dummy doc
            // create comment from exception type
            String comment = CommentManager.createComment(config, exceptionType, CommentManager.EXCEPTION, true, false);

            // first char to lower, if it doesn't start with two upper case
            // letters
            if (comment.length() > 1 && !Character.isUpperCase(comment.charAt(1))) {
                comment = StringUtils.firstToLower(comment);
            }

            comments.add(Constants.JDOC_THE_LOWER + " " + comment);
        }
    }

    /**
     * Apply exception Javadoc template.
     *
     * @param method the method
     * @param name the name of the exception
     * @param comments the resulting Javadoc comments
     */
    private void applyExceptionTemplate(final IMethod method, final String name, final List<String> comments) {
        try {
            String text = JAutodocPlugin.getContext().getTemplateManager().applyExceptionTemplate(method, name,
                    config.getProperties());

            if (text != null && text.length() > 0) {
                final JavadocInfo jdi = new JavadocInfo();
                jdi.parseJavadoc(text);

                final JavadocTag throwsTag = jdi.getThrowsDoc().get(name);
                if (throwsTag != null) {
                    comments.addAll(throwsTag.getComments());
                }
            }
        } catch (Exception e) {
            JAutodocPlugin.getDefault().handleException(e);
        }
    }

    /**
     * Checks for a dot in the given Javadoc comment and adds one, if needed.
     *
     * @param comment the Javadoc comment
     */
    private void checkForDot(final List<String> comment) {
        if (comment == null || comment.isEmpty()) {
            return;
        }

        boolean found = false;
        for (int i = 0; i < comment.size(); ++i) {
            if (comment.get(i).indexOf(Constants.DOT) >= 0) {
                found = true;
                break;
            }
        }

        if (!found) {
            String last = comment.get(comment.size() - 1);
            if (!last.trim().equals("")) {
                comment.remove(comment.size() - 1);
                comment.add(last + Constants.DOT);
            }
        }
    }

    /**
     * Creates a field Javadoc string.
     *
     * @param indent the indent string
     * @param lineSeparator the line separator
     * @param jdi the Javadoc info
     * @return the resulting Javadoc string
     * @throws JavaModelException failure in Java model
     */
    @SuppressWarnings("unchecked")
    private String createFieldJavadocString(final String indent, final String lineSeparator, final JavadocInfo jdi)
            throws JavaModelException {
        // start
        final StringBuilder javadoc = new StringBuilder("/**" + getFieldJavadocSeparator(lineSeparator, jdi.hasTags()));

        // text
        for (final String text : jdi.getComment()) {
            if (!config.isSingleLineComment() || jdi.hasTags()) {
                startNewLine(javadoc, indent);
            }
            javadoc.append(text);
            javadoc.append(getFieldJavadocSeparator(lineSeparator, jdi.hasTags()));
        }

        // the rest (maybe some tags for external tools)
        if (jdi.hasTags()) {
            addEmptyLine(javadoc, indent, lineSeparator);
        }

        final List<JavadocTag> tagComments = jdi.getAllTagComments(EMPTY_ARR, EMPTY_ARR);
        Collections.sort(tagComments, new TagComparator(config.getTagOrder(), Collections.EMPTY_LIST, Collections.EMPTY_LIST));

        for (final JavadocTag javadocTag : tagComments) {
            javadocTag.addToJavadocString(javadoc, indent, getFieldJavadocSeparator(lineSeparator, jdi.hasTags()));
        }

        // the end
        if (!config.isSingleLineComment() || jdi.hasTags()) {
            closeJavadoc(javadoc, indent);
        }
        else {
            javadoc.append("*/");
        }

        return javadoc.toString();
    }

    /**
     * Gets the field Javadoc separator. Whitespace for single line field comments, line separator otherwise.
     *
     * @param lineSeparator the line separator
     * @return the field Javadoc separator
     */
    private String getFieldJavadocSeparator(final String lineSeparator, final boolean hasTags) {
        return config.isSingleLineComment() && !hasTags ? " " : lineSeparator;
    }

    private String createJavadocString(final String indent, final String lineSeparator, final JavadocInfo jdi,
            final String[] parameterNames) throws JavaModelException {
        return createJavadocString(indent, lineSeparator, jdi, parameterNames, EMPTY_ARR);
    }

    private String createJavadocString(final String indent, final String lineSeparator, final JavadocInfo jdi,
            final String[] parameterNames, final String[] exceptionTypes)  throws JavaModelException {

        final StringBuilder javadoc = startJavadoc(lineSeparator);

        // text
        for (String text : jdi.getComment()) {
            startNewLine(javadoc, indent);
            javadoc.append((String) text);
            javadoc.append(lineSeparator);
        }

        final List<JavadocTag> tagComments = jdi.getAllTagComments(parameterNames, exceptionTypes);
        Collections.sort(tagComments, new TagComparator(
                config.getTagOrder(), Arrays.asList(parameterNames), Arrays.asList(exceptionTypes)));

        if (!tagComments.isEmpty()) {
            addEmptyLine(javadoc, indent, lineSeparator);
        }

        for (final JavadocTag javadocTag : tagComments) {
            javadocTag.addToJavadocString(javadoc, indent, lineSeparator);
        }

        // and the end
        closeJavadoc(javadoc, indent);

        return javadoc.toString();
    }

    private StringBuilder startJavadoc(final String lineSeparator) {
        final StringBuilder javadoc = new StringBuilder("/**" + lineSeparator);
        return javadoc;
    }

    private void closeJavadoc(final StringBuilder javadoc, final String indent) {
        javadoc.append(indent);
        javadoc.append(" */");
    }

    private void startNewLine(final StringBuilder javadoc, final String indent) {
        javadoc.append(indent);
        javadoc.append(" * ");
    }

    private void addEmptyLine(final StringBuilder javadoc, final String indent, final String lineSeparator) {
        javadoc.append(indent);
        javadoc.append(" *");
        javadoc.append(lineSeparator);
    }
}
