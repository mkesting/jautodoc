/*******************************************************************
 * Copyright (c) 2006 - 2025, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.IMemberFilter;
import net.sf.jautodoc.source.SourceRange;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.corext.util.MethodOverrideTester;
import org.eclipse.jdt.internal.corext.util.SuperTypeHierarchyCache;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.IRegion;

/**
 * Various helper methods for source processing.
 */
@SuppressWarnings("restriction")
public final class SourceUtils {
    private static final Map<IJavaProject, List<String>> fieldPrefixes = new HashMap<IJavaProject, List<String>>();
    private static final Map<IJavaProject, List<String>> fieldSuffixes = new HashMap<IJavaProject, List<String>>();

    private static final FieldOptionChangeListener focListener = new FieldOptionChangeListener();

    private SourceUtils() {/* no instantiation */}

    public static ISourceReference getPackageOrImportReference(final ICompilationUnit compUnit) throws JavaModelException {
        if (compUnit.getPackageDeclarations().length > 0) {
            return compUnit.getPackageDeclarations()[0];
        }

        if (compUnit.getImports().length > 0) {
            return compUnit.getImports()[0];
        }
        return null;
    }

    public static void getMembers(final IType type, final List<IMember> members, final IMemberFilter filter) throws JavaModelException {
        getFieldMembers(type, members, filter);
        getMethodMembers(type, members, filter);
        getNestedTypeMembers(type, members, filter);
    }

    public static void getFieldMembers(final IType type, final List<IMember> members, final IMemberFilter filter)
            throws JavaModelException {
        final IField[] fields = type.getFields();
        for (int i = 0; i < fields.length; ++i) {
            final IField field = fields[i];
            if (isMatchingField(field, filter)) {
                members.add(field);
            }
        }
    }

    public static void getMethodMembers(final IType type, final List<IMember> members, final IMemberFilter filter)
            throws JavaModelException {
        final IMethod[] methods = type.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            final IMethod method = methods[i];
            if (isMatchingMethod(method, filter)) {
                members.add(method);
            }
        }
    }

    public static void getNestedTypeMembers(final IType type, final List<IMember> members, final IMemberFilter filter)
            throws JavaModelException {
        final IType[] types = type.getTypes();
        for (int i = 0; i < types.length; ++i) {
            final IType nestedType = types[i];
            if (hasMatchingVisibility(nestedType, filter)) {
                if (filter.isIncludeTypes()) {
                    members.add(nestedType);
                }
                getMembers(nestedType, members, filter);
            }
        }
    }

    public static void getSubpackages(final IPackageFragment pkgFragment, final Collection<IPackageFragment> subpackages)
            throws JavaModelException {
        final String packageName = pkgFragment.getElementName();
        final IJavaElement[] allPackages = ((IPackageFragmentRoot)pkgFragment.getParent()).getChildren();

        for (int i = 0; i < allPackages.length; i++) {
            final IPackageFragment otherPackage = (PackageFragment) allPackages[i];
            final String otherPackageName = otherPackage.getElementName();

            if (otherPackageName.length() > packageName.length() && otherPackageName.startsWith(packageName)) {
                subpackages.add(otherPackage);
            }
        }
    }

    public static boolean isMatchingType(final IType type, final IMemberFilter filter) throws JavaModelException {
        return filter.isIncludeTypes() && hasMatchingVisibility(type, filter) && !isGeneratedMember(type);
    }

    public static boolean isMatchingField(final IField field, final IMemberFilter filter) throws JavaModelException {
        return filter.isIncludeFields() && hasMatchingVisibility(field, filter) && !isGeneratedMember(field);
    }

    public static boolean isMatchingMethod(final IMethod method, final IMemberFilter filter) throws JavaModelException {
        if (method.getDeclaringType().isInterface() && Flags.isPackageDefault(method.getFlags())) {
            return filter.isIncludeMethods() && filter.isIncludePublic();
        }
        return filter.isIncludeMethods() && hasMatchingVisibility(method, filter) && !isGeneratedMember(method)
                && matchesGetterSetterFilter(method, filter) && matchesOverridingFilter(method, filter);
    }

    public static boolean isGeneratedMember(final IMember member) {
        try {
            // source is annotation only like Lombock @Getter, @Setter, @Data
            return Optional.ofNullable(member.getSource()).map(s -> s.matches("^@\\S*$")).orElse(false);
        } catch (JavaModelException e) {
            JAutodocPlugin.getDefault().handleException(member, e);
            return false;
        }
    }

    public static boolean isRecordComponent(final IMember member) {
        return member instanceof IField && Optional.ofNullable(member.getDeclaringType()).map(type -> {
            try {
                return type.isRecord() && type.getRecordComponent(member.getElementName()) != null;
            } catch (JavaModelException e) {
                JAutodocPlugin.getDefault().handleException(member, e);
                return false;
            }
        }).orElse(false);
    }

    public static boolean matchesOverridingFilter(final IMethod method, final IMemberFilter filter) {
        return !filter.isExcludeOverriding() || !isOverridingMethod(method);
    }

    public static boolean matchesGetterSetterFilter(final IMethod method, final IMemberFilter filter)
            throws JavaModelException {
        if (!filter.isGetterSetterOnly() && !filter.isExcludeGetterSetter()) {
            return true;
        }

        final boolean isGetterSetter = isGetterSetter(method);
        return filter.isGetterSetterOnly() && isGetterSetter || filter.isExcludeGetterSetter() && !isGetterSetter;
    }

    public static boolean isGetterSetter(final IMember member) throws JavaModelException {
        return (member instanceof IMethod) &&  isGetterSetter((IMethod) member, new StringBuffer());
    }

    public static boolean isGetterSetter(final IMethod method) throws JavaModelException {
        return isGetterSetter(method, new StringBuffer());
    }

    public static boolean isGetterSetter(final IMethod method, final StringBuffer fieldName) throws JavaModelException {
        final String name = method.getElementName();
        final String prefix = StringUtils.getPrefix(name);

        if ("set".equals(prefix) && name.length() > "set".length()
                && method.getParameterNames().length == 1
                && method.getReturnType().charAt(0) == Signature.C_VOID) {
            fieldName.append(StringUtils.firstToLower(name.substring("set".length())));
            return true;
        }

        if ("get".equals(prefix) && name.length() > "get".length()
                && method.getParameterNames().length == 0
                && method.getReturnType().charAt(0) != Signature.C_VOID) {
            fieldName.append(StringUtils.firstToLower(name.substring("get".length())));
            return true;
        }

        if ("is".equals(prefix) && name.length() > "is".length()
                && method.getParameterNames().length == 0
                && method.getReturnType().charAt(0) == Signature.C_BOOLEAN) {
            fieldName.append(StringUtils.firstToLower(name.substring("is".length())));
            return true;
        }

        return false;
    }

    public static IField getFieldOfGetterSetter(final IMethod method) throws JavaModelException {
        final StringBuffer fieldNameSb = new StringBuffer();
        if (!SourceUtils.isGetterSetter(method, fieldNameSb)) {
            return null;
        }
        return getField(method.getDeclaringType(), fieldNameSb.toString());
    }

    public static IField getField(final IType declaringType, final String fieldName) throws JavaModelException {
        for (final String prefix : getFieldPrefixes(declaringType.getJavaProject())) {
            for (final String suffix : getFieldSuffixes(declaringType.getJavaProject())) {
                final IField field = declaringType.getField(StringUtils.composeName(prefix, fieldName, suffix));
                if (field != null && field.exists()) {
                    return field;
                }
            }
        }
        return null;
    }

    public static boolean hasMatchingVisibility(final IMember member, final IMemberFilter filter) throws JavaModelException {
        final int flags = member.getFlags();
        if (filter.isIncludePublic()    && Flags.isPublic(flags) ||
                filter.isIncludeProtected() && Flags.isProtected(flags) ||
                filter.isIncludePackage()   && Flags.isPackageDefault(flags) ||
                filter.isIncludePrivate()   && Flags.isPrivate(flags)) {
            return true;
        }
        return false;
    }

    /**
     * Find source range of the first comment(s) in the given document range.
     *
     * @param document the document
     * @param offset the document range offset
     * @param length the document range length
     * @param scanner the document scanner
     * @param onlyFirstComment true to ignore multiple comments
     * @return the comment source range
     * @throws Exception an exception occured
     */
    public static ISourceRange findCommentSourceRange(final IDocument document, final int offset, final int length,
            final IScanner scanner, final boolean onlyFirstComment) throws Exception {

        scanner.resetTo(offset, offset + length - 1);

        int commentStart = -1;
        int commentEnd   = -1;
        boolean markdownComment = false;
        boolean singleLineComment = false;

        int token = scanner.getNextToken();
        while (Utils.isComment(token)) {
            if (commentStart < 0) {
                commentStart = scanner.getCurrentTokenStartPosition();
            }
            commentEnd = scanner.getCurrentTokenEndPosition();
            markdownComment = Utils.isMarkdownComment(token);
            singleLineComment = Utils.isSingleLineComment(token);

            if (onlyFirstComment) {
                break;
            }
            token = scanner.getNextToken();
        }

        if (commentStart < 0) {
            // no comment -> point to the beginning of the given range
            commentStart = offset;
            commentEnd = commentStart - 1;
        }
        else if (markdownComment || singleLineComment) {
            // exclude line delimiter from range
            String delim = document.getLineDelimiter(document.getLineOfOffset(commentEnd));
            if (delim != null) {
                commentEnd -= delim.length();
            }
        }
        return new SourceRange(commentStart, commentEnd - commentStart + 1);
    }

    /**
     * Find Javadoc source range for the given member.
     *
     * @param member the member
     * @param scanner the related source code scanner
     * @return the Javadoc source range
     * @throws JavaModelException failure in Java model
     * @throws InvalidInputException invalid scanner input
     */
    public static ISourceRange findJavadocSourceRange(final IMember member, final IScanner scanner)
            throws JavaModelException, InvalidInputException {

        final ISourceRange range = member.getSourceRange();
        final int offset = range.getOffset();
        final int length = range.getLength();

        scanner.resetTo(offset, offset + length - 1);

        int javadocStart = -1;
        int javadocEnd = -1;

        int otherdocStart = -1;
        int otherdocEnd = -1;

        int token = scanner.getNextToken();
        while (Utils.isComment(token)) {
            if (Utils.isJavadocComment(token) || Utils.isMarkdownComment(token)) {
                javadocStart = scanner.getCurrentTokenStartPosition();
                javadocEnd   = scanner.getCurrentTokenEndPosition();
                break;
            } else {
                otherdocStart = scanner.getCurrentTokenStartPosition();
                otherdocEnd   = scanner.getCurrentTokenEndPosition();
            }
            token = scanner.getNextToken();
        }

        if (javadocStart < 0) {
            // no comment -> point to start of member
            final int memberStart = scanner.getCurrentTokenStartPosition();
            if (otherdocEnd < 0) {
                javadocStart = memberStart;
            } else {
                // ... or to start of direct related (same line or line above) other comment
                final int lineOtherdocEnd = scanner.getLineNumber(otherdocEnd);
                final int lineMemberStart = scanner.getLineNumber(memberStart);
                if (lineMemberStart - lineOtherdocEnd > 1) {
                    javadocStart = memberStart;
                } else {
                    javadocStart = otherdocStart;
                }
            }
            javadocEnd = javadocStart - 1;
        } else {
            char[] currentTokenSource = scanner.getCurrentTokenSource();
            for (int i = currentTokenSource.length - 1; i >= 0
                    && (currentTokenSource[i] == '\r' || currentTokenSource[i] == '\n'); i--) {
                javadocEnd--;
            }
        }

        return new SourceRange(javadocStart, javadocEnd - javadocStart + 1);
    }

    public static boolean isSameComment(final String existingJavadoc, final String newJavadoc, final boolean checkCommentStyle) {
        if (existingJavadoc == null || existingJavadoc.length() == 0 || (checkCommentStyle
                && !StringUtils.startOf(existingJavadoc, 2).equals(StringUtils.startOf(newJavadoc, 2)))) {
            return false;
        }
        return getRawComment(existingJavadoc).equals(getRawComment(newJavadoc));
    }

    public static String getRawComment(final String javadoc) {
        String rawComment = "";
        if (javadoc != null) {
            // remove '/', '*' and whitespaces
            rawComment = javadoc.replaceAll("[/\\*\\s]", "");
        }
        return rawComment;
    }

    /**
     * Gets the inherited javadoc.
     *
     * @param lineSeparator the line separator
     * @param indent the indent
     * @param method the method
     * @return the inherited javadoc
     * @throws JavaModelException the java model exception
     */
    public static String getInheritedJavadoc(IMethod method, String indent, String lineSeparator)
            throws JavaModelException {
        String javadoc = null;
        try {
            final IMethod overridden = findOverriddenMethod(method);
            if (overridden != null) {
                javadoc = CodeGeneration.getMethodComment(method, overridden, lineSeparator);
                if (javadoc != null) {
                    javadoc = correctIndent(javadoc, indent, lineSeparator);
                }
            }
        } catch (Exception e) {
            JAutodocPlugin.getDefault().handleException(e);
        }
        return javadoc;
    }

    public static IMethod findOverriddenMethod(final IMethod method) {
        try {
            if (!method.isConstructor()) {
                final IType declaringType = method.getDeclaringType();
                final ITypeHierarchy hierarchy = SuperTypeHierarchyCache.getTypeHierarchy(declaringType);
                final MethodOverrideTester tester = new MethodOverrideTester(declaringType, hierarchy);
                return tester.findOverriddenMethod(method, true);
            }
        } catch (JavaModelException e) {
            JAutodocPlugin.getDefault().handleException(e);
        }
        return null;
    }

    public static boolean isOverridingMethod(final IMethod method) {
        return findOverriddenMethod(method) != null;
    }

    /**
     * Gets the type parameter names of the given type.
     *
     * @param type the type
     * @return the type parameter names
     * @throws JavaModelException failure in Java model
     */
    public static String[] getParameterNames(final IType type) throws JavaModelException {
        final ITypeParameter[] typeParameters = type.getTypeParameters();
        final IField[] recordComponents = type.getRecordComponents();
        final String[] parameterNames = new String[typeParameters.length + recordComponents.length];

        getTypeParameterNames(typeParameters, parameterNames);
        for (int i = 0; i < recordComponents.length; ++i) {
            parameterNames[typeParameters.length + i] = recordComponents[i].getElementName();
        }
        return parameterNames;
    }

    /**
     * Gets the parameter names of the given method, including type parameters.
     *
     * @param method the method
     * @return the parameter names
     * @throws JavaModelException failure in Java model
     */
    public static String[] getParameterNames(final IMethod method) throws JavaModelException {
        final ITypeParameter[] typeParameters = method.getTypeParameters();
        final String[] methodParamNames = method.getParameterNames();
        final String[] parameterNames = new String[typeParameters.length + methodParamNames.length];

        getTypeParameterNames(typeParameters, parameterNames);
        for (int i = 0; i < methodParamNames.length; ++i) {
            parameterNames[typeParameters.length + i] = methodParamNames[i];
        }
        return parameterNames;
    }

    /**
     * Gets the names of the given type parameters.
     *
     * @param typeParameters the type parameters
     * @param parameterNames the type parameter names
     * @return the type parameter names
     */
    public static void getTypeParameterNames(final ITypeParameter[] typeParameters, final String[] parameterNames) {
        for (int i = 0; i < typeParameters.length; ++i) {
            parameterNames[i] = "<" + typeParameters[i].getElementName() + ">";
        }
    }

    /**
     * Gets the exception types of the given method.
     *
     * @param method the method
     * @param docExceptionTypes the currently documented exception types
     * @return the exception types
     * @throws JavaModelException a java model exception occurred
     */
    public static String[] getExceptionTypes(final IMethod method, final Collection<String> docExceptionTypes)
            throws JavaModelException {
        final List<String> exceptionTypes = new ArrayList<String>();
        for (String exception : method.getExceptionTypes()) {
            exceptionTypes.add(Signature.getSignatureSimpleName(exception));
        }

        // add documented RuntimeExceptions
        for (final String docExceptionType : docExceptionTypes) {
            if (!exceptionTypes.contains(docExceptionType)
                    && isRuntimeException(method.getDeclaringType(), docExceptionType)) {
                exceptionTypes.add(docExceptionType);
            }
        }
        return exceptionTypes.toArray(new String[exceptionTypes.size()]);
    }

    /**
     * Checks if the exception with the given name is a runtime exception.
     *
     * @param primaryType check in the context of this type
     * @param exceptionName the exception name
     * @return true, if it is a runtime exception
     * @throws JavaModelException a java model exception occurred
     */
    public static boolean isRuntimeException(final IType primaryType, final String exceptionName)
            throws JavaModelException {
        final String[][] exceptionTypeNames = primaryType.resolveType(exceptionName);
        if (exceptionTypeNames == null || exceptionTypeNames.length == 0) {
            return false;
        }

        for (final String[] exceptionTypeName : exceptionTypeNames) {
            if (exceptionTypeName.length != 2) {
                continue;
            }

            final IType exceptionType = primaryType.getJavaProject().findType(exceptionTypeName[0], exceptionTypeName[1]);
            if (exceptionType == null) {
                continue;
            }

            if (RuntimeException.class.getName().equals(exceptionType.getFullyQualifiedName())) {
                return true;
            }

            final String superclassName = exceptionType.getSuperclassName();
            if (superclassName == null
                    || superclassName.equals(Exception.class.getName())
                    || superclassName.equals(Throwable.class.getName())
                    || superclassName.equals(Object.class.getName())) {
                return false;
            }

            if (isRuntimeException(exceptionType, superclassName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Correct indent.
     *
     * @param lineSeparator the line separator
     * @param indent the indent
     * @param comment the comment
     * @return the string
     * @throws BadLocationException the bad location exception
     */
    public static String correctIndent(final String comment, final String indent, final String lineSeparator)
            throws BadLocationException {
        final ILineTracker tracker = new DefaultLineTracker();
        tracker.set(comment);

        final int nLines = tracker.getNumberOfLines();
        if (nLines == 1) {
            return comment;
        }

        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < nLines; i++) {
            final IRegion region = tracker.getLineInformation(i);
            final int start = region.getOffset();
            final int end = start + region.getLength();
            String line = comment.substring(start, end);

            if (i == 0) {  // no indent for first line (contained in the formatted string)
                buf.append(line);
            } else { // no new line after last line
                buf.append(lineSeparator);
                buf.append(indent);
                buf.append(line);
            }
        }
        return buf.toString();
    }

    /**
     * Gets the indention string for the given member.
     *
     * @param document the related document
     * @param member the member
     * @return the indention string
     * @throws Exception an exception occured
     */
    public static String getIndentionString(final IDocument document, final IMember member) throws Exception {
        final StringBuffer indent = new StringBuffer();

        int pos = member.getSourceRange().getOffset();
        while (0 <= --pos) {
            char c = document.getChar(pos);
            if (c != '\t' && c != ' '){
                break;
            }
            indent.append(c);
        }
        return indent.toString();
    }

    /**
     * Gets the first type in the current compilation unit.
     *
     * @param compUnit the compilation unit
     * @return the first type
     * @throws JavaModelException failure in Java model
     */
    public static IType getFirstType(final ICompilationUnit compUnit) throws JavaModelException {
        final IType[] allTypes = compUnit.getAllTypes();
        return allTypes.length > 0 ? allTypes[0] : null;
    }

    /**
     * Sort members regarding to their occurence in the compilation unit.
     *
     * @param members the members
     * @return the sorted members
     * @throws JavaModelException failure in Java model
     */
    public static IMember[] sortMembers(final IMember[] members) throws JavaModelException {
        final Map<Integer, IMember> map = new TreeMap<Integer, IMember>(); // sorted map
        for (int i = 0; i < members.length; ++i) {
            final IMember member = members[i];
            map.put(Integer.valueOf(member.getSourceRange().getOffset()), member);
        }
        return map.values().toArray(new IMember[map.values().size()]);
    }

    // ------------------------------------------------------------------------
    // Helpers...
    // ------------------------------------------------------------------------

    private static List<String> getFieldPrefixes(final IJavaProject javaProject) {
        return getFieldOption(javaProject, fieldPrefixes, JavaCore.CODEASSIST_FIELD_PREFIXES);
    }

    private static List<String> getFieldSuffixes(final IJavaProject javaProject) {
        return getFieldOption(javaProject, fieldSuffixes, JavaCore.CODEASSIST_FIELD_SUFFIXES);
    }

    private static List<String> getFieldOption(final IJavaProject javaProject,
            final Map<IJavaProject, List<String>> fieldOptions, final String optionKey) {

        List<String> options = fieldOptions.get(javaProject);
        if (options == null) {
            options = new ArrayList<String>();
            options.add("");
            fieldOptions.put(javaProject, options);

            final String sPrefixes = javaProject.getOption(optionKey, true);
            if (sPrefixes != null && sPrefixes.length() > 0) {
                options.addAll(Arrays.asList(sPrefixes.split(",")));
            }
            addFieldOptionChangeListener(javaProject);
        }
        return options;
    }

    private static void addFieldOptionChangeListener(final IJavaProject javaProject) {
        InstanceScope.INSTANCE.getNode(JavaCore.PLUGIN_ID).addPreferenceChangeListener(focListener);
        new ProjectScope(javaProject.getProject()).getNode(JavaCore.PLUGIN_ID).addPreferenceChangeListener(focListener);
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    private static final class FieldOptionChangeListener implements IPreferenceChangeListener {
        @Override
        public void preferenceChange(PreferenceChangeEvent event) {
            if (event.getKey().equals(JavaCore.CODEASSIST_FIELD_PREFIXES)
                    || event.getKey().equals(JavaCore.CODEASSIST_FIELD_SUFFIXES)) {
                fieldPrefixes.clear();
                fieldSuffixes.clear();
            }
        }
    }
}
