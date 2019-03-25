/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import java.util.ArrayList;
import java.util.List;

import net.sf.jautodoc.preferences.IMemberFilter;

import org.eclipse.jface.dialogs.IDialogSettings;

/**
 * Parameters for the current search.
 */
public class TaskSearchPattern implements IMemberFilter {

    private boolean missingJavadoc;
    private boolean missingParamTag;
    private boolean missingReturnTag;
    private boolean missingThrowsTag;

    private boolean missingPeriods;
    private boolean generatedJavadoc;
    private boolean todoForGenerated;

    private boolean missingHeader;
    private boolean outdatedHeader;

    private boolean includePublic;
    private boolean includeProtected;
    private boolean includePackage;
    private boolean includePrivate;

    private boolean includeTypes;
    private boolean includeFields;
    private boolean includeMethods;
    private boolean getterSetterOnly;
    private boolean excludeGetterSetter;

    private boolean searchMissingTags;
    private String missingTagString;
    private MissingTag[] missingTags;


    public void store(IDialogSettings settings) {
        settings.put("missingJavadoc", missingJavadoc); //$NON-NLS-1$
        settings.put("missingParamTag", missingParamTag); //$NON-NLS-1$
        settings.put("missingReturnTag", missingReturnTag); //$NON-NLS-1$
        settings.put("missingThrowsTag", missingThrowsTag); //$NON-NLS-1$

        settings.put("missingPeriods", missingPeriods); //$NON-NLS-1$
        settings.put("generatedJavadoc", generatedJavadoc); //$NON-NLS-1$
        settings.put("todoForGenerated", todoForGenerated); //$NON-NLS-1$

        settings.put("missingHeader", missingHeader); //$NON-NLS-1$
        settings.put("outdatedHeader", outdatedHeader); //$NON-NLS-1$

        settings.put("includePublic", includePublic); //$NON-NLS-1$
        settings.put("includeProtected", includeProtected); //$NON-NLS-1$
        settings.put("includePackage", includePackage); //$NON-NLS-1$
        settings.put("includePrivate", includePrivate); //$NON-NLS-1$

        settings.put("includeTypes", includeTypes); //$NON-NLS-1$
        settings.put("includeFields", includeFields); //$NON-NLS-1$
        settings.put("includeMethods", includeMethods); //$NON-NLS-1$
        settings.put("getterSetterOnly", getterSetterOnly); //$NON-NLS-1$
        settings.put("excludeGetterSetter", excludeGetterSetter); //$NON-NLS-1$

        settings.put("searchMissingTags", searchMissingTags); //$NON-NLS-1$
        settings.put("missingTagString", missingTagString); //$NON-NLS-1$
    }

    public static TaskSearchPattern create(final IDialogSettings settings) {
        final TaskSearchPattern pattern  = new TaskSearchPattern();

        pattern.missingJavadoc   = getBoolean(settings, "missingJavadoc",   true); //$NON-NLS-1$
        pattern.missingParamTag  = getBoolean(settings, "missingParamTag",  true); //$NON-NLS-1$
        pattern.missingReturnTag = getBoolean(settings, "missingReturnTag", true); //$NON-NLS-1$
        pattern.missingThrowsTag = getBoolean(settings, "missingThrowsTag", true); //$NON-NLS-1$

        pattern.missingPeriods   = getBoolean(settings, "missingPeriods",   false); //$NON-NLS-1$
        pattern.generatedJavadoc = getBoolean(settings, "generatedJavadoc", false); //$NON-NLS-1$
        pattern.todoForGenerated = getBoolean(settings, "todoForGenerated", false); //$NON-NLS-1$

        pattern.missingHeader  = getBoolean(settings, "missingHeader",  false); //$NON-NLS-1$
        pattern.outdatedHeader = getBoolean(settings, "outdatedHeader", false); //$NON-NLS-1$

        pattern.includePublic    = getBoolean(settings, "includePublic",    true); //$NON-NLS-1$
        pattern.includeProtected = getBoolean(settings, "includeProtected", true); //$NON-NLS-1$
        pattern.includePackage   = getBoolean(settings, "includePackage",   false); //$NON-NLS-1$
        pattern.includePrivate   = getBoolean(settings, "includePrivate",   false); //$NON-NLS-1$

        pattern.includeTypes        = getBoolean(settings, "includeTypes",        true); //$NON-NLS-1$
        pattern.includeFields       = getBoolean(settings, "includeFields",       true); //$NON-NLS-1$
        pattern.includeMethods      = getBoolean(settings, "includeMethods",      true); //$NON-NLS-1$
        pattern.getterSetterOnly    = getBoolean(settings, "getterSetterOnly",    false); //$NON-NLS-1$
        pattern.excludeGetterSetter = getBoolean(settings, "excludeGetterSetter", false); //$NON-NLS-1$

        pattern.searchMissingTags = getBoolean(settings, "searchMissingTags", false); //$NON-NLS-1$
        pattern.missingTagString = getString(settings, "missingTagString", "author,deprecated+"); //$NON-NLS-1$

        return pattern;
    }

    public boolean isMissingJavadoc() {
        return missingJavadoc;
    }

    public void setMissingJavadoc(boolean missingJavadoc) {
        this.missingJavadoc = missingJavadoc;
    }

    public boolean isMissingParamTag() {
        return missingParamTag;
    }

    public void setMissingParamTag(boolean missingParamTag) {
        this.missingParamTag = missingParamTag;
    }

    public boolean isMissingReturnTag() {
        return missingReturnTag;
    }

    public void setMissingReturnTag(boolean missingReturnTag) {
        this.missingReturnTag = missingReturnTag;
    }

    public boolean isMissingThrowsTag() {
        return missingThrowsTag;
    }

    public void setMissingThrowsTag(boolean missingThrowsTag) {
        this.missingThrowsTag = missingThrowsTag;
    }

    public boolean isMissingPeriods() {
        return missingPeriods;
    }

    public void setMissingPeriods(boolean missingPeriods) {
        this.missingPeriods = missingPeriods;
    }

    public boolean isGeneratedJavadoc() {
        return generatedJavadoc;
    }

    public void setGeneratedJavadoc(boolean generatedJavadoc) {
        this.generatedJavadoc = generatedJavadoc;
    }

    public boolean isTodoForGenerated() {
        return todoForGenerated;
    }

    public void setTodoForGenerated(boolean todoForGenerated) {
        this.todoForGenerated = todoForGenerated;
    }

    public boolean isMissingHeader() {
        return missingHeader;
    }

    public void setMissingHeader(boolean missingHeader) {
        this.missingHeader = missingHeader;
    }

    public boolean isOutdatedHeader() {
        return outdatedHeader;
    }

    public void setOutdatedHeader(boolean outdatedHeader) {
        this.outdatedHeader = outdatedHeader;
    }

    public boolean isIncludePublic() {
        return includePublic;
    }

    public void setVisibilityPublic(boolean visibilityPublic) {
        this.includePublic = visibilityPublic;
    }

    public boolean isIncludeProtected() {
        return includeProtected;
    }

    public void setVisibilityProtected(boolean visibilityProtected) {
        this.includeProtected = visibilityProtected;
    }

    public boolean isIncludePackage() {
        return includePackage;
    }

    public void setVisibilityPackage(boolean visibilityPackage) {
        this.includePackage = visibilityPackage;
    }

    public boolean isIncludePrivate() {
        return includePrivate;
    }

    public void setVisibilityPrivate(boolean visibilityPrivate) {
        this.includePrivate = visibilityPrivate;
    }

    public boolean isIncludeTypes() {
        return includeTypes;
    }

    public void setFilterTypes(boolean filterTypes) {
        this.includeTypes = filterTypes;
    }

    public boolean isIncludeFields() {
        return includeFields;
    }

    public void setFilterFields(boolean filterFields) {
        this.includeFields = filterFields;
    }

    public boolean isIncludeMethods() {
        return includeMethods;
    }

    public void setFilterMethods(boolean filterMethods) {
        this.includeMethods = filterMethods;
    }

    public boolean isGetterSetterOnly() {
        return getterSetterOnly;
    }

    public void setFilterGetSetOnly(boolean filterGetSetOnly) {
        this.getterSetterOnly = filterGetSetOnly;
    }

    public boolean isExcludeGetterSetter() {
        return excludeGetterSetter;
    }

    public void setFilterExcludeGetSet(boolean filterExcludeGetSet) {
        this.excludeGetterSetter = filterExcludeGetSet;
    }

    public boolean isExcludeOverriding() {
        return true;
    }

    public boolean isSearchMissingTags() {
        return searchMissingTags;
    }

    public void setSearchMissingTags(boolean searchMissingTags) {
        this.searchMissingTags = searchMissingTags;
    }

    public String getMissingTagString() {
        return missingTagString;
    }

    public void setMissingTagString(String missingTagString) {
        this.missingTags = null;
        this.missingTagString = missingTagString;
    }

    public MissingTag[] getMissingTags() {
        if (missingTags == null) {
            final List<MissingTag> missingTagList = new ArrayList<MissingTag>();
            final String[] values = missingTagString.split(",");
            for (String value : values) {
                if (value.trim().length() > 0) {
                    missingTagList.add(MissingTag.fromString(value.trim()));
                }
            }
            missingTags = missingTagList.toArray(new MissingTag[missingTagList.size()]);
        }
        return missingTags;
    }

    public String getDescription() {
        final StringBuffer buffer = new StringBuffer();
        if (isMissingJavadoc()) {
            buffer.append("Missing Javadoc, ");
        }

        if (isMissingParamTag()) {
            buffer.append("@param, ");
        }

        if (isMissingReturnTag()) {
            buffer.append("@return, ");
        }

        if (isMissingThrowsTag()) {
            buffer.append("@throws, ");
        }

        if (isGeneratedJavadoc()) {
            buffer.append("Generated Javadoc, ");
        }

        if (isTodoForGenerated()) {
            buffer.append("ToDo's, ");
        }

        if (isMissingPeriods()) {
            buffer.append("Missing periods, ");
        }

        if (isMissingHeader()) {
            buffer.append("Missing header, ");
        }

        if (isOutdatedHeader()) {
            buffer.append("Outdated header, ");
        }

        if (isSearchMissingTags() && getMissingTagString().trim().length() > 0) {
            buffer.append("Missing @tags (" + getMissingTagString() + "), ");
        }

        return buffer.length() > 0 ? buffer.substring(0, buffer.length() - 2) : "";
    }

    private static String getString(final IDialogSettings settings, final String key, final String defaultValue) {
        final String value = settings.get(key);
        return value == null ? defaultValue : value;
    }

    private static boolean getBoolean(final IDialogSettings settings, final String key, final boolean defaultValue) {
        final String value = settings.get(key);
        return value == null ? defaultValue : Boolean.valueOf(value).booleanValue();
    }

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    public static class MissingTag {

        public enum Option {
            ALL(""),
            ONLY_MISSING("-"),
            ONLY_EMPTY("+");

            private final String sign;

            private Option(final String sign) {
                this.sign = sign;
            }
        }

        private final String name;
        private final Option option;

        private MissingTag(final String name, final Option option) {
            this.name = name;
            this.option = option;
        }

        public static MissingTag fromString(final String string) {
            Option option = Option.ALL;
            String name = string.startsWith("@") ? string.substring(1) : string;

            if (name.endsWith(Option.ONLY_MISSING.sign)) {
                name = name.substring(0, name.length() - 1);
                option = Option.ONLY_MISSING;
            }
            else if (name.endsWith(Option.ONLY_EMPTY.sign)) {
                name = name.substring(0, name.length() - 1);
                option = Option.ONLY_EMPTY;
            }
            return new MissingTag(name, option);
        }

        public String getName() {
            return name;
        }

        public boolean isOnlyMissing() {
            return option == Option.ONLY_MISSING;
        }

        public boolean isOnlyEmpty() {
            return option == Option.ONLY_EMPTY;
        }

        @Override
        public String toString() {
            return name + option.sign;
        }
    }
}
