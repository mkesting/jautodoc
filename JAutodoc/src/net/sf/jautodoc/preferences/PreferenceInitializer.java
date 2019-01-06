/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

import java.util.ArrayList;
import java.util.List;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.replacements.Replacement;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;


/**
 * Initialize preference store with default values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer implements Constants {

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences() {
        final PreferenceStore prefStore = (PreferenceStore) JAutodocPlugin.getDefault().getPreferenceStore();

        prefStore.setDefault(VISIBILITY_PUBLIC,    DEFAULT_VISIBILITY_PUBLIC);
        prefStore.setDefault(VISIBILITY_PROTECTED, DEFAULT_VISIBILITY_PROTECTED);
        prefStore.setDefault(VISIBILITY_PACKAGE,   DEFAULT_VISIBILITY_PACKAGE);
        prefStore.setDefault(VISIBILITY_PRIVATE,   DEFAULT_VISIBILITY_PRIVATE);

        prefStore.setDefault(FILTER_TYPES,         DEFAULT_FILTER_TYPES);
        prefStore.setDefault(FILTER_FIELDS,        DEFAULT_FILTER_FIELDS);
        prefStore.setDefault(FILTER_METHODS,       DEFAULT_FILTER_METHODS);
        prefStore.setDefault(FILTER_GETSET,        DEFAULT_FILTER_GETSET);
        prefStore.setDefault(FILTER_EXCLGETSET,    DEFAULT_FILTER_EXCLGETSET);
        prefStore.setDefault(FILTER_EXCLOVERRID,   DEFAULT_FILTER_EXCLOVERRID);

        prefStore.setDefault(MODE,                 DEFAULT_MODE);
        prefStore.setDefault(CREATE_DUMMY_DOC,     DEFAULT_CREATE_DUMMY_DOC);
        prefStore.setDefault(ADD_TODO,             DEFAULT_ADD_TODO);
        prefStore.setDefault(SINGLE_LINE,          DEFAULT_SINGLE_LINE);
        prefStore.setDefault(USE_FORMATTER,        DEFAULT_USE_FORMATTER);
        prefStore.setDefault(GET_SET_FROM_FIELD,   DEFAULT_GET_SET_FROM_FIELD);
        prefStore.setDefault(INCLUDE_SUBPACKAGES,  DEFAULT_INCLUDE_SUBPACKAGES);

        prefStore.setDefault(GET_SET_FROM_FIELD_FIRST,   DEFAULT_GET_SET_FROM_FIELD_FIRST);
        prefStore.setDefault(GET_SET_FROM_FIELD_REPLACE, DEFAULT_GET_SET_FROM_FIELD_REPLACE);

        prefStore.setDefault(GET_SET_FROM_FIELD_REPLACEMENTS,  DEFAULT_GET_SET_FROM_FIELD_REPLACEMENTS);

        prefStore.setDefault(ADD_HEADER,           DEFAULT_ADD_HEADER);
        prefStore.setDefault(REPLACE_HEADER,       DEFAULT_REPLACE_HEADER);
        prefStore.setDefault(MULTI_HEADER,         DEFAULT_MULTI_HEADER);
        prefStore.setDefault(USE_PKG_INFO,         DEFAULT_USE_PKG_INFO);
        prefStore.setDefault(HEADER_TEXT,          DEFAULT_HEADER_TEXT);
        prefStore.setDefault(PKG_DOC_TEXT,         DEFAULT_PKG_DOC_TEXT);
        prefStore.setDefault(PKG_INFO_TEXT,        DEFAULT_PKG_INFO_TEXT);
        prefStore.setDefault(TAG_ORDER,            DEFAULT_TAG_ORDER);

        prefStore.setDefault(PROJECT_SPECIFIC,     DEFAULT_PROJECT_SPECIFIC);

        prefStore.setDefaultReplacements(getDefaultReplacements());
    }

    /**
     * Read default prefix replacements from file.
     *
     * @return the default prefix replacements
     */
    private Replacement[] getDefaultReplacements() {
        int counter = 0;
        final List<Replacement> defaults = new ArrayList<Replacement>();
        while (true) {
            final String prefix = PreferenceMessages.getDefaultValue("preferences.default.prefix." + counter);
            final String replacement = PreferenceMessages.getDefaultValue("preferences.default.preplacement." + counter);
            if (prefix == null || replacement == null) {
                break;
            }

            defaults.add(new Replacement(prefix, replacement));
            ++counter;
        }
        return defaults.toArray(new Replacement[defaults.size()]);
    }
}
