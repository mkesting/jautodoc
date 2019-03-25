/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences.replacements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for shortcut replacements in strings.
 */
public class ReplacementManager {

    private final Map<String,Replacement> fieldReplacements = new HashMap<String,Replacement>();
    private final Map<String,Replacement> methodReplacements = new HashMap<String,Replacement>();

    /**
     * Instantiates a new replacement manager.
     *
     * @param replacements the replacements to manage
     */
    public ReplacementManager(final Replacement[] replacements) {
        init(replacements);
    }

    /**
     * Instantiates a new replacement manager.
     *
     * @param other another replacement manager
     */
    public ReplacementManager(final ReplacementManager other) {
        fieldReplacements.putAll(other.fieldReplacements);
        methodReplacements.putAll(other.methodReplacements);
    }

    /**
     * Does the replacements in the given string array.
     *
     * @param strings the strings to replace
     * @param scope the scope (FIELD| METHOD)
     * @return the string array with the replaced strings
     */
    public String[] doReplacements(final String[] strings, final int scope) {
        final Map<String, Replacement> replacements = getReplacements(scope);

        final List<String> newStrings = new ArrayList<String>(strings.length);
        for (int i = 0; i < strings.length; ++i) {
            final Replacement r = replacements.get(strings[i].toLowerCase());
            if (r != null && (i == 0 || r.getMode() == Replacement.MODE_ALL)) {
                if (r.getReplacement().length() > 0) {
                    newStrings.add(r.getReplacement());
                }
            } else {
                newStrings.add(strings[i]);
            }
        }
        return newStrings.toArray(new String[newStrings.size()]);
    }

    private Map<String,Replacement> getReplacements(final int scope) {
        if (scope == Replacement.SCOPE_FIELD) {
            return fieldReplacements;
        } else {
            return methodReplacements;
        }
    }

    private void init(final Replacement[] replacements) {
        for (int i = 0; i < replacements.length; ++i) {
            Replacement r = replacements[i];
            if (r.isFieldReplacement()) {
                fieldReplacements.put(r.getShortcut().toLowerCase(), r);
            }
            if (r.isMethodReplacement()) {
                methodReplacements.put(r.getShortcut().toLowerCase(), r);
            }
        }
    }
}
