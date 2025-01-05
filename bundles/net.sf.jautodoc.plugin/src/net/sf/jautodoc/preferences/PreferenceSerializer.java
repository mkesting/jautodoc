/*******************************************************************
 * Copyright (c) 2006 - 2025, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.replacements.Replacement;
import net.sf.jautodoc.preferences.replacements.ReplacementBlock;
import net.sf.jautodoc.utils.JAXBSerializer;
import net.sf.jautodoc.utils.StringUtils;

/**
 * Import and export of JAutodoc overall configuration via JAXB.
 */
public final class PreferenceSerializer {

    private PreferenceSerializer() {/* no instantiation */}

    public static void doImport(final OptionsBlock ob, final ReplacementBlock rb,
            final List<PreferenceType> selectedPreferenceTypes, final String fileName) throws Exception {

        final OverallConfiguration oc = JAXBSerializer.doImport(fileName, OverallConfiguration.class);
        fromConfiguration(oc, ob, rb, selectedPreferenceTypes);
    }

    public static void doExport(final OptionsBlock ob, final ReplacementBlock rb,
            final List<PreferenceType> selectedPreferenceTypes, final String fileName) throws Exception {

        final OverallConfiguration oc = toConfiguration(ob, rb, selectedPreferenceTypes);
        JAXBSerializer.doExport(fileName, oc);
    }

    private static OverallConfiguration toConfiguration(final OptionsBlock ob, final ReplacementBlock rb,
            final List<PreferenceType> selectedPreferenceTypes) throws Exception {

        final OverallConfiguration oc = new OverallConfiguration();

        if (selectedPreferenceTypes.contains(PreferenceType.OPTIONS)) {
            final Configuration configuration = new Configuration();

            configuration.setCompleteExistingJavadoc(ob.completeButton.getSelection());
            configuration.setKeepExistingJavadoc(ob.keepButton.getSelection());
            configuration.setReplaceExistingJavadoc(ob.replaceButton.getSelection());

            configuration.setUseMarkdown(ob.useMarkdownButton.getSelection());
            configuration.setSwitchDocStyle(ob.switchDocStyleButton.getSelection());

            configuration.setVisibilityPublic(ob.publicButton.getSelection());
            configuration.setVisibilityProtected(ob.protectedButton.getSelection());
            configuration.setVisibilityPackage(ob.packageButton.getSelection());
            configuration.setVisibilityPrivate(ob.privateButton.getSelection());

            configuration.setCommentTypes(ob.filterTypesButton.getSelection());
            configuration.setCommentFields(ob.filterFieldsButton.getSelection());
            configuration.setCommentMethods(ob.filterMethodsButton.getSelection());
            configuration.setGetterSetterOnly(ob.filterGetterSetterButton.getSelection());
            configuration.setExcludeGetterSetter(ob.filterExcludeGetterSetterButton.getSelection());
            configuration.setExcludeOverriding(ob.filterExcludeOverridingButton.getSelection());

            configuration.setAddTodoForAutodoc(ob.todoButton.getSelection());
            configuration.setCreateDummyComment(ob.dummyDocButton.getSelection());
            configuration.setSingleLineComment(ob.singleLineButton.getSelection());
            configuration.setUseEclipseFormatter(ob.useFormatterButton.getSelection());
            configuration.setGetterSetterFromField(ob.getSetFromFieldButton.getSelection());
            configuration.setIncludeSubPackages(ob.includeSubPackagesButton.getSelection());

            configuration.setGetterSetterFromFieldFirst(ob.getSetFromFieldFirstButton.getSelection());
            configuration.setGetterSetterFromFieldReplace(ob.getSetFromFieldReplaceButton.getSelection());

            configuration.setAddHeader(ob.addHeaderButton.getSelection());
            configuration.setReplaceHeader(ob.replaceHeaderButton.getSelection());
            configuration.setMultiCommentHeader(ob.multiHeaderButton.getSelection());
            configuration.setUsePackageInfo(ob.usePackageInfoButton.getSelection());

            oc.setTagOrder(ob.tagOrder);
            oc.setConfiguration(configuration);
            oc.setGetSetFromFieldReplacements(ob.getSetFromFieldReplacements);
        }

        if (selectedPreferenceTypes.contains(PreferenceType.HEADERTEXT)) {
            oc.setHeaderText(ob.headerText);
        }
        if (selectedPreferenceTypes.contains(PreferenceType.PACKAGEDOCTEXT)) {
            oc.setPackageDocText(ob.packageDocText);
        }
        if (selectedPreferenceTypes.contains(PreferenceType.PACKAGEINFOTEXT)) {
            oc.setPackageInfoText(ob.packageInfoText);
        }
        if (selectedPreferenceTypes.contains(PreferenceType.PROPERTIES)) {
            oc.setProperties(ob.properties);
        }
        if (selectedPreferenceTypes.contains(PreferenceType.REPLACEMENTS)) {
            oc.setReplacements(Arrays.asList(rb.getReplacements()));
        }
        if (selectedPreferenceTypes.contains(PreferenceType.TEMPLATES)) {
            oc.setTemplates(JAutodocPlugin.getContext().getTemplateManager().getTemplates());
        }
        return oc;
    }

    private static void fromConfiguration(final OverallConfiguration oc, final OptionsBlock ob,
            final ReplacementBlock rb, final List<PreferenceType> selectedPreferenceTypes) throws Exception {

        if (selectedPreferenceTypes.contains(PreferenceType.OPTIONS) && oc.getConfiguration() != null) {
            final Configuration configuration = oc.getConfiguration();

            ob.completeButton.setSelection(configuration.isCompleteExistingJavadoc());
            ob.keepButton.setSelection(configuration.isKeepExistingJavadoc());
            ob.replaceButton.setSelection(configuration.isReplaceExistingJavadoc());

            ob.useMarkdownButton.setSelection(configuration.isUseMarkdown());
            ob.switchDocStyleButton.setSelection(configuration.isSwitchDocStyle());

            ob.publicButton.setSelection(configuration.isIncludePublic());
            ob.protectedButton.setSelection(configuration.isIncludeProtected());
            ob.packageButton.setSelection(configuration.isIncludePackage());
            ob.privateButton.setSelection(configuration.isIncludePrivate());

            ob.filterTypesButton.setSelection(configuration.isIncludeTypes());
            ob.filterFieldsButton.setSelection(configuration.isIncludeFields());
            ob.filterMethodsButton.setSelection(configuration.isIncludeMethods());
            ob.filterGetterSetterButton.setSelection(configuration.isGetterSetterOnly());
            ob.filterExcludeGetterSetterButton.setSelection(configuration.isExcludeGetterSetter());
            ob.filterExcludeOverridingButton.setSelection(configuration.isExcludeOverriding());

            ob.todoButton.setSelection(configuration.isAddTodoForAutodoc());
            ob.dummyDocButton.setSelection(configuration.isCreateDummyComment());
            ob.singleLineButton.setSelection(configuration.isSingleLineComment());
            ob.useFormatterButton.setSelection(configuration.isUseEclipseFormatter());
            ob.getSetFromFieldButton.setSelection(configuration.isGetterSetterFromField());
            ob.includeSubPackagesButton.setSelection(configuration.isIncludeSubPackages());

            ob.getSetFromFieldFirstButton.setSelection(configuration.isGetterSetterFromFieldFirst());
            ob.getSetFromFieldReplaceButton.setSelection(configuration.isGetterSetterFromFieldReplace());

            ob.addHeaderButton.setSelection(configuration.isAddHeader());
            ob.replaceHeaderButton.setSelection(configuration.isReplaceHeader());
            ob.multiHeaderButton.setSelection(configuration.isMultiCommentHeader());
            ob.usePackageInfoButton.setSelection(configuration.isUsePackageInfo());
        }

        if (selectedPreferenceTypes.contains(PreferenceType.OPTIONS)
                && oc.getTagOrder() != null && !oc.getTagOrder().isEmpty()) {
            ob.tagOrder = new ArrayList<String>(oc.getTagOrder());
        }

        if (selectedPreferenceTypes.contains(PreferenceType.OPTIONS)
                && oc.getGetSetFromFieldReplacements() != null && !oc.getGetSetFromFieldReplacements().isEmpty()) {
            ob.getSetFromFieldReplacements =  new TreeSet<GetSetFromFieldReplacement>(oc.getGetSetFromFieldReplacements());
        }

        if (selectedPreferenceTypes.contains(PreferenceType.HEADERTEXT)
                && !StringUtils.isEmpty(oc.getHeaderText())) {
            ob.headerText = oc.getHeaderText();
        }
        if (selectedPreferenceTypes.contains(PreferenceType.PACKAGEDOCTEXT)
                && !StringUtils.isEmpty(oc.getPackageDocText())) {
            ob.packageDocText = oc.getPackageDocText();
        }
        if (selectedPreferenceTypes.contains(PreferenceType.PACKAGEINFOTEXT)
                && !StringUtils.isEmpty(oc.getPackageInfoText())) {
            ob.packageInfoText = oc.getPackageInfoText();
        }
        if (selectedPreferenceTypes.contains(PreferenceType.PROPERTIES)
                && oc.getProperties() != null && !oc.getProperties().isEmpty()) {
            ob.properties = new HashMap<String, String>(oc.getProperties());
        }
        if (selectedPreferenceTypes.contains(PreferenceType.REPLACEMENTS)
                && oc.getReplacements() != null && !oc.getReplacements().isEmpty()) {
            rb.setReplacements(oc.getReplacements().toArray(new Replacement[oc.getReplacements().size()]));
        }
        if (selectedPreferenceTypes.contains(PreferenceType.TEMPLATES)
                && oc.getTemplates() != null && !oc.getTemplates().isEmpty()) {
            JAutodocPlugin.getContext().getTemplateManager().setTemplates(oc.getTemplates());
        }
    }
}
