/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.jautodoc.preferences.replacements.Replacement;
import net.sf.jautodoc.preferences.replacements.ReplacementManager;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;


/**
 * Contains a Workspace or Project specific configuration.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration implements IMemberFilter, IPropertyChangeListener, Constants {

    @XmlTransient
    private PreferenceStore prefStore;

    @XmlTransient
    private IProject project;

    @XmlTransient
    private boolean readonly = true;

    private boolean completeExistingJavadoc = true;
    private boolean keepExistingJavadoc     = false;
    private boolean replaceExistingJavadoc  = false;

    private boolean includePublic    = true;
    private boolean includeProtected = true;
    private boolean includePackage   = true;
    private boolean includePrivate   = true;

    private boolean includeTypes        = true;
    private boolean includeFields       = true;
    private boolean includeMethods      = true;
    private boolean getterSetterOnly    = true;
    private boolean excludeGetterSetter = true;
    private boolean excludeOverriding   = false;

    private boolean addTodoForAutodoc     = true;
    private boolean createDummyComment    = true;
    private boolean singleLineComment     = true;
    private boolean useEclipseFormatter   = false;
    private boolean getterSetterFromField = false;
    private boolean includeSubPackages    = false;

    private boolean getterSetterFromFieldFirst   = true;
    private boolean getterSetterFromFieldReplace = false;

    private boolean addHeader          = false;
    private boolean replaceHeader      = false;
    private boolean multiCommentHeader = false;
    private boolean usePackageInfo     = false;

    @XmlTransient
    private String headerText      = "";

    @XmlTransient
    private String packageDocText  = "";

    @XmlTransient
    private String packageInfoText = "";

    @XmlTransient
    private List<String> tagOrder = new ArrayList<String>();

    @XmlTransient
    private Map<String, String> properties = new HashMap<String, String>();

    @XmlTransient
    private Set<GetSetFromFieldReplacement> getSetFromFieldReplacements = new TreeSet<GetSetFromFieldReplacement>();

    @XmlTransient
    private ReplacementManager replacementManager;


    /**
     * Instantiates a new configuration. Required for JAXB.
     */
    public Configuration() {
        this.readonly = false;
    }

    /**
     * Instantiates a new configuration. The new instance is
     * created readonly, but is kept in sync with the underlying
     * preference store.
     *
     * @param prefStore the underlying preference store
     */
    public Configuration(PreferenceStore prefStore) {
        this(prefStore, null);
    }

    /**
     * Instantiates a new configuration. The new instance is
     * created readonly, but is kept in sync with the underlying
     * preference store.
     *
     * @param prefStore the underlying preference store
     * @param project the related project
     */
    public Configuration(PreferenceStore prefStore, IProject project) {
        initialize(prefStore, project);
    }

    /**
     * Instantiates a new configuration from the given configuration.
     * The new instance is writeable and not connected to an
     * preference store.
     *
     * @param c the configuration to read from
     */
    public Configuration(Configuration c) {
        this.readonly = false;

        this.completeExistingJavadoc = c.completeExistingJavadoc;
        this.keepExistingJavadoc     = c.keepExistingJavadoc;
        this.replaceExistingJavadoc  = c.replaceExistingJavadoc;

        this.includePublic    = c.includePublic;
        this.includeProtected = c.includeProtected;
        this.includePackage   = c.includePackage;
        this.includePrivate   = c.includePrivate;

        this.includeTypes        = c.includeTypes;
        this.includeFields       = c.includeFields;
        this.includeMethods      = c.includeMethods;
        this.getterSetterOnly    = c.getterSetterOnly;
        this.excludeGetterSetter = c.excludeGetterSetter;
        this.excludeOverriding   = c.excludeOverriding;

        this.addTodoForAutodoc     = c.addTodoForAutodoc;
        this.createDummyComment    = c.createDummyComment;
        this.singleLineComment     = c.singleLineComment;
        this.useEclipseFormatter   = c.useEclipseFormatter;
        this.getterSetterFromField = c.getterSetterFromField;
        this.includeSubPackages    = c.includeSubPackages;

        this.getterSetterFromFieldFirst   = c.getterSetterFromFieldFirst;
        this.getterSetterFromFieldReplace = c.getterSetterFromFieldReplace;

        this.addHeader          = c.addHeader;
        this.replaceHeader      = c.replaceHeader;
        this.multiCommentHeader = c.multiCommentHeader;

        this.headerText      = new String(c.headerText);
        this.packageDocText  = new String(c.packageDocText);
        this.packageInfoText = new String(c.packageInfoText);

        this.tagOrder.addAll(c.tagOrder);
        this.properties.putAll(c.properties);
        this.getSetFromFieldReplacements.addAll(c.getSetFromFieldReplacements);

        this.replacementManager = new ReplacementManager(c.replacementManager);
    }

    /**
     * Gets the underlying preference store.
     *
     * @return the preference store
     */
    public PreferenceStore getPreferenceStore() {
        return prefStore;
    }

    public boolean isAddHeader() {
        return addHeader;
    }

    public void setAddHeader(boolean addHeader) {
        checkReadonly();
        this.addHeader = addHeader;
    }

    public boolean isAddTodoForAutodoc() {
        return addTodoForAutodoc;
    }

    public void setAddTodoForAutodoc(boolean addTodoForAutodoc) {
        checkReadonly();
        this.addTodoForAutodoc = addTodoForAutodoc;
    }

    public boolean isIncludeFields() {
        return includeFields;
    }

    public void setCommentFields(boolean commentFields) {
        checkReadonly();
        this.includeFields = commentFields;
    }

    public boolean isIncludeMethods() {
        return includeMethods;
    }

    public void setCommentMethods(boolean commentMethods) {
        checkReadonly();
        this.includeMethods = commentMethods;
    }

    public boolean isIncludeTypes() {
        return includeTypes;
    }

    public void setCommentTypes(boolean commentTypes) {
        checkReadonly();
        this.includeTypes = commentTypes;
    }

    public boolean isCompleteExistingJavadoc() {
        return completeExistingJavadoc;
    }

    public void setCompleteExistingJavadoc(boolean completeExistingJavadoc) {
        checkReadonly();
        this.completeExistingJavadoc = completeExistingJavadoc;
    }

    public boolean isCreateDummyComment() {
        return createDummyComment;
    }

    public void setCreateDummyComment(boolean createDummyComment) {
        checkReadonly();
        this.createDummyComment = createDummyComment;
    }

    public boolean isGetterSetterOnly() {
        return getterSetterOnly;
    }

    public void setGetterSetterOnly(boolean getterSetterOnly) {
        checkReadonly();
        this.getterSetterOnly = getterSetterOnly;
    }

    public boolean isExcludeGetterSetter() {
        return excludeGetterSetter;
    }

    public void setExcludeGetterSetter(boolean excludeGetterSetter) {
        checkReadonly();
        this.excludeGetterSetter = excludeGetterSetter;
    }

    public boolean isExcludeOverriding() {
        return excludeOverriding;
    }

    public void setExcludeOverriding(boolean excludeOverriding) {
        checkReadonly();
        this.excludeOverriding = excludeOverriding;
    }

    public boolean isKeepExistingJavadoc() {
        return keepExistingJavadoc;
    }

    public void setKeepExistingJavadoc(boolean keepExistingJavadoc) {
        checkReadonly();
        this.keepExistingJavadoc = keepExistingJavadoc;
    }

    public boolean isReplaceExistingJavadoc() {
        return replaceExistingJavadoc;
    }

    public void setReplaceExistingJavadoc(boolean replaceExistingJavadoc) {
        checkReadonly();
        this.replaceExistingJavadoc = replaceExistingJavadoc;
    }

    public boolean isReplaceHeader() {
        return replaceHeader;
    }

    public void setReplaceHeader(boolean replaceHeader) {
        checkReadonly();
        this.replaceHeader = replaceHeader;
    }

    public boolean isMultiCommentHeader() {
        return multiCommentHeader;
    }

    public void setMultiCommentHeader(boolean multiCommentHeader) {
        checkReadonly();
        this.multiCommentHeader = multiCommentHeader;
    }

    public boolean isUsePackageInfo() {
        return usePackageInfo;
    }

    public void setUsePackageInfo(boolean usePackageInfo) {
        checkReadonly();
        this.usePackageInfo = usePackageInfo;
    }

    public boolean isSingleLineComment() {
        return singleLineComment;
    }

    public void setSingleLineComment(boolean singleLineComment) {
        checkReadonly();
        this.singleLineComment = singleLineComment;
    }

    public boolean isUseEclipseFormatter() {
        return useEclipseFormatter;
    }

    public void setUseEclipseFormatter(boolean useEclipseFormatter) {
        checkReadonly();
        this.useEclipseFormatter = useEclipseFormatter;
    }

    public boolean isGetterSetterFromField() {
        return getterSetterFromField;
    }

    public void setGetterSetterFromField(boolean getterSetterFromField) {
        checkReadonly();
        this.getterSetterFromField = getterSetterFromField;
    }

    public boolean isGetterSetterFromFieldFirst() {
        return getterSetterFromFieldFirst;
    }

    public void setGetterSetterFromFieldFirst(boolean getterSetterFromFieldFirst) {
        checkReadonly();
        this.getterSetterFromFieldFirst = getterSetterFromFieldFirst;
    }

    public boolean isGetterSetterFromFieldReplace() {
        return getterSetterFromFieldReplace;
    }

    public void setGetterSetterFromFieldReplace(boolean getterSetterFromFieldReplace) {
        checkReadonly();
        this.getterSetterFromFieldReplace = getterSetterFromFieldReplace;
    }

    public boolean isIncludeSubPackages() {
        return includeSubPackages;
    }

    public void setIncludeSubPackages(boolean includeSubPackages) {
        checkReadonly();
        this.includeSubPackages = includeSubPackages;
    }

    public boolean isIncludePackage() {
        return includePackage;
    }

    public void setVisibilityPackage(boolean visibilityPackage) {
        checkReadonly();
        this.includePackage = visibilityPackage;
    }

    public boolean isIncludePrivate() {
        return includePrivate;
    }

    public void setVisibilityPrivate(boolean visibilityPrivate) {
        checkReadonly();
        this.includePrivate = visibilityPrivate;
    }

    public boolean isIncludeProtected() {
        return includeProtected;
    }

    public void setVisibilityProtected(boolean visibilityProtected) {
        checkReadonly();
        this.includeProtected = visibilityProtected;
    }

    public boolean isIncludePublic() {
        return includePublic;
    }

    public void setVisibilityPublic(boolean visibilityPublic) {
        checkReadonly();
        this.includePublic = visibilityPublic;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        checkReadonly();
        this.headerText = headerText;
    }

    public String getPackageDocText() {
        return packageDocText;
    }

    public void setPackageDocText(String packageDocText) {
        checkReadonly();
        this.packageDocText = packageDocText;
    }

    public String getPackageInfoText() {
        return packageInfoText;
    }

    public void setPackageInfoText(String packageInfoText) {
        checkReadonly();
        this.packageInfoText = packageInfoText;
    }

    public List<String> getTagOrder() {
        return Collections.unmodifiableList(tagOrder);
    }

    public void setTagOrder(List<String> tagOrder) {
        checkReadonly();
        this.tagOrder.clear();
        this.tagOrder.addAll(tagOrder);
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public void setProperties(Map<String, String> properties) {
        checkReadonly();
        this.properties.clear();
        this.properties.putAll(properties);
    }

    public Set<GetSetFromFieldReplacement> getGetSetFromFieldReplacements() {
        return Collections.unmodifiableSet(getSetFromFieldReplacements);
    }

    public void setGetSetFromFieldReplacements(Set<GetSetFromFieldReplacement> getSetFromFieldReplacements) {
        checkReadonly();
        this.getSetFromFieldReplacements.clear();
        this.getSetFromFieldReplacements.addAll(getSetFromFieldReplacements);
    }

    public ReplacementManager getReplacementManager() {
        return replacementManager;
    }

    /**
     * Initialize values from preference store.
     *
     * @param prefStore the preference store
     */
    private void initialize(PreferenceStore prefStore, IProject project) {
        this.prefStore = prefStore;
        this.project = project;

        initReplacements();
        initMode();

        tagOrder.addAll(prefStore.getTagOrder());
        properties.putAll(prefStore.getProperties());
        getSetFromFieldReplacements.addAll(prefStore.getGetSetFromFieldReplacements());

        includePublic    = prefStore.getBoolean(VISIBILITY_PUBLIC);
        includeProtected = prefStore.getBoolean(VISIBILITY_PROTECTED);
        includePackage   = prefStore.getBoolean(VISIBILITY_PACKAGE);
        includePrivate   = prefStore.getBoolean(VISIBILITY_PRIVATE);

        includeTypes        = prefStore.getBoolean(FILTER_TYPES);
        includeFields       = prefStore.getBoolean(FILTER_FIELDS);
        includeMethods      = prefStore.getBoolean(FILTER_METHODS);
        getterSetterOnly    = prefStore.getBoolean(FILTER_GETSET);
        excludeGetterSetter = prefStore.getBoolean(FILTER_EXCLGETSET);
        excludeOverriding   = prefStore.getBoolean(FILTER_EXCLOVERRID);

        addTodoForAutodoc     = prefStore.getBoolean(ADD_TODO);
        createDummyComment    = prefStore.getBoolean(CREATE_DUMMY_DOC);
        singleLineComment     = prefStore.getBoolean(SINGLE_LINE);
        useEclipseFormatter   = prefStore.getBoolean(USE_FORMATTER);
        getterSetterFromField = prefStore.getBoolean(GET_SET_FROM_FIELD);
        includeSubPackages    = prefStore.getBoolean(INCLUDE_SUBPACKAGES);
        addHeader             = prefStore.getBoolean(ADD_HEADER);
        replaceHeader         = prefStore.getBoolean(REPLACE_HEADER);
        multiCommentHeader    = prefStore.getBoolean(MULTI_HEADER);
        usePackageInfo        = prefStore.getBoolean(USE_PKG_INFO);

        getterSetterFromFieldFirst   = prefStore.getBoolean(GET_SET_FROM_FIELD_FIRST);
        getterSetterFromFieldReplace = prefStore.getBoolean(GET_SET_FROM_FIELD_REPLACE);

        headerText      = prefStore.getString(HEADER_TEXT);
        packageDocText  = prefStore.getString(PKG_DOC_TEXT);
        packageInfoText = prefStore.getString(PKG_INFO_TEXT);

        prefStore.addPropertyChangeListener(this);
        initGlobalPreferenceChangeListener();
    }

    private void initReplacements() {
        final List<Replacement> replacements = getDefaultFieldPrefixReplacements();
        replacements.addAll(Arrays.asList(prefStore.getReplacements()));
        replacementManager = new ReplacementManager(replacements.toArray(new Replacement[replacements.size()]));
    }

    private void initMode() {
        String mode = prefStore.getString(MODE);
        if (MODE_COMPLETE.equals(mode)) {
            completeExistingJavadoc = true;
            keepExistingJavadoc     = false;
            replaceExistingJavadoc  = false;
        }
        else if (MODE_KEEP.equals(mode)) {
            completeExistingJavadoc = false;
            keepExistingJavadoc     = true;
            replaceExistingJavadoc  = false;
        }
        else if (MODE_REPLACE.equals(mode)) {
            completeExistingJavadoc = false;
            keepExistingJavadoc     = false;
            replaceExistingJavadoc  = true;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().startsWith(REPLACEMENTS)) {
            initReplacements();
        }
        else if (MODE.equals(event.getProperty())) {
            initMode();
        }
        else if (TAG_ORDER.equals(event.getProperty())) {
            tagOrder.clear();
            tagOrder.addAll(prefStore.getTagOrder());
        }
        else if (event.getProperty().startsWith(PROPERTIES)) {
            properties.clear();
            properties.putAll(prefStore.getProperties());
        }
        else if (GET_SET_FROM_FIELD_REPLACEMENTS.equals(event.getProperty())) {
            getSetFromFieldReplacements.clear();
            getSetFromFieldReplacements.addAll(prefStore.getGetSetFromFieldReplacements());
        }
        else if (VISIBILITY_PUBLIC.equals(event.getProperty())) {
            includePublic = prefStore.getBoolean(VISIBILITY_PUBLIC);
        }
        else if (VISIBILITY_PROTECTED.equals(event.getProperty())) {
            includeProtected = prefStore.getBoolean(VISIBILITY_PROTECTED);
        }
        else if (VISIBILITY_PACKAGE.equals(event.getProperty())) {
            includePackage = prefStore.getBoolean(VISIBILITY_PACKAGE);
        }
        else if (VISIBILITY_PRIVATE.equals(event.getProperty())) {
            includePrivate = prefStore.getBoolean(VISIBILITY_PRIVATE);
        }
        else if (FILTER_TYPES.equals(event.getProperty())) {
            includeTypes = prefStore.getBoolean(FILTER_TYPES);
        }
        else if (FILTER_FIELDS.equals(event.getProperty())) {
            includeFields = prefStore.getBoolean(FILTER_FIELDS);
        }
        else if (FILTER_METHODS.equals(event.getProperty())) {
            includeMethods = prefStore.getBoolean(FILTER_METHODS);
        }
        else if (FILTER_GETSET.equals(event.getProperty())) {
            getterSetterOnly = prefStore.getBoolean(FILTER_GETSET);
        }
        else if (FILTER_EXCLGETSET.equals(event.getProperty())) {
            excludeGetterSetter = prefStore.getBoolean(FILTER_EXCLGETSET);
        }
        else if (FILTER_EXCLOVERRID.equals(event.getProperty())) {
            excludeOverriding = prefStore.getBoolean(FILTER_EXCLOVERRID);
        }
        else if (ADD_TODO.equals(event.getProperty())) {
            addTodoForAutodoc  = prefStore.getBoolean(ADD_TODO);
        }
        else if (CREATE_DUMMY_DOC.equals(event.getProperty())) {
            createDummyComment = prefStore.getBoolean(CREATE_DUMMY_DOC);
        }
        else if (SINGLE_LINE.equals(event.getProperty())) {
            singleLineComment = prefStore.getBoolean(SINGLE_LINE);
        }
        else if (USE_FORMATTER.equals(event.getProperty())) {
            useEclipseFormatter = prefStore.getBoolean(USE_FORMATTER);
        }
        else if (GET_SET_FROM_FIELD.equals(event.getProperty())) {
            getterSetterFromField = prefStore.getBoolean(GET_SET_FROM_FIELD);
        }
        else if (GET_SET_FROM_FIELD_FIRST.equals(event.getProperty())) {
            getterSetterFromFieldFirst = prefStore.getBoolean(GET_SET_FROM_FIELD_FIRST);
        }
        else if (GET_SET_FROM_FIELD_REPLACE.equals(event.getProperty())) {
            getterSetterFromFieldReplace = prefStore.getBoolean(GET_SET_FROM_FIELD_REPLACE);
        }
        else if (INCLUDE_SUBPACKAGES.equals(event.getProperty())) {
            includeSubPackages = prefStore.getBoolean(INCLUDE_SUBPACKAGES);
        }
        else if (ADD_HEADER.equals(event.getProperty())) {
            addHeader = prefStore.getBoolean(ADD_HEADER);
        }
        else if (REPLACE_HEADER.equals(event.getProperty())) {
            replaceHeader = prefStore.getBoolean(REPLACE_HEADER);
        }
        else if (MULTI_HEADER.equals(event.getProperty())) {
            multiCommentHeader = prefStore.getBoolean(MULTI_HEADER);
        }
        else if (USE_PKG_INFO.equals(event.getProperty())) {
            usePackageInfo = prefStore.getBoolean(USE_PKG_INFO);
        }
        else if (HEADER_TEXT.equals(event.getProperty())) {
            headerText = prefStore.getString(HEADER_TEXT);
        }
        else if (PKG_DOC_TEXT.equals(event.getProperty())) {
            packageDocText = prefStore.getString(PKG_DOC_TEXT);
        }
        else if (PKG_INFO_TEXT.equals(event.getProperty())) {
            packageInfoText = prefStore.getString(PKG_INFO_TEXT);
        }
    }

    private void checkReadonly() {
        if (readonly) {
            throw new IllegalStateException("Configuration is read only");
        }
    }

    private IJavaProject getJavaProject() {
        return project == null ? null : JavaCore.create(project);
    }

    private List<Replacement> getDefaultFieldPrefixReplacements() {
        final List<Replacement> replacements = new ArrayList<Replacement>();

        final Set<String> prefixes = getDefaultFieldPrefixes();
        for (final String prefix : prefixes) {
            final String shortcut = prefix.trim();
            if (shortcut.length() > 0) {
                replacements.add(new Replacement(shortcut, "", Replacement.SCOPE_FIELD, Replacement.MODE_PREFIX));
            }
        }
        return replacements;
    }

    private Set<String> getDefaultFieldPrefixes() {
        final Set<String> prefixes = new HashSet<String>();
        final IJavaProject javaProject = getJavaProject();

        getDefaultFieldPrefixes(javaProject, prefixes, JavaCore.CODEASSIST_FIELD_PREFIXES);
        getDefaultFieldPrefixes(javaProject, prefixes, JavaCore.CODEASSIST_STATIC_FIELD_PREFIXES);
        // not supported in 3.4
        //getDefaultFieldPrefixes(javaProject, prefixes, JavaCore.CODEASSIST_STATIC_FINAL_FIELD_PREFIXES);
        getDefaultFieldPrefixes(javaProject, prefixes, JavaCore.CODEASSIST_ARGUMENT_PREFIXES);
        return prefixes;
    }

    private void getDefaultFieldPrefixes(final IJavaProject javaProject, final Set<String> prefixes,
            final String optionName) {

        final String sPrefixes = (javaProject == null) ? JavaCore.getOption(optionName)
                : javaProject.getOption(optionName, true);
        if (sPrefixes != null && sPrefixes.length() > 0) {
            prefixes.addAll(Arrays.asList(sPrefixes.split(",")));
        }
    }

    private void initGlobalPreferenceChangeListener() {
        getScopeContext().getNode(JavaCore.PLUGIN_ID).addPreferenceChangeListener(new IPreferenceChangeListener() {

            public void preferenceChange(PreferenceChangeEvent event) {
                if (event.getKey().equals(JavaCore.CODEASSIST_FIELD_PREFIXES)
                        || event.getKey().equals(JavaCore.CODEASSIST_STATIC_FIELD_PREFIXES)
                        // not supported in 3.4
                        //|| event.getKey().equals(JavaCore.CODEASSIST_STATIC_FINAL_FIELD_PREFIXES)
                        || event.getKey().equals(JavaCore.CODEASSIST_ARGUMENT_PREFIXES))
                    initReplacements();
            }
        });
    }

    private IScopeContext getScopeContext() {
        return project == null ? InstanceScope.INSTANCE : new ProjectScope(project);
    }
}
