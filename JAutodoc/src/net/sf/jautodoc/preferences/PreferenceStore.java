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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import net.sf.jautodoc.preferences.replacements.Replacement;
import net.sf.jautodoc.preferences.replacements.ReplacementSerializer;
import net.sf.jautodoc.utils.StringUtils;

import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.preferences.ScopedPreferenceStore;


/**
 * Preference store for this plugin.
 */
public class PreferenceStore extends ScopedPreferenceStore {

    private List<String> tagOrder;
	private Map<String, String> properties;
	private Set<GetSetFromFieldReplacement> getSetFromFieldReplacements;

	/**
	 * Instantiates a new preference store.
	 *
	 * @param context the the scope to store to
	 * @param qualifier the qualifier used to look up the preference node
	 * @param defaultQualifierPath the qualifier used when looking up the defaults
	 */
	public PreferenceStore(IScopeContext context, String qualifier, String defaultQualifierPath) {
		super(context, qualifier, defaultQualifierPath);
	}

	/**
	 * Instantiates a new preference store.
	 *
	 * @param context the scope to store to
	 * @param qualifier the qualifier used to look up the preference node
	 */
	public PreferenceStore(IScopeContext context, String qualifier) {
		super(context, qualifier);
	}

	/**
	 * Get replacements.
	 *
	 * @return the replacements
	 */
	public Replacement[] getReplacements() {
		String name = Constants.REPLACEMENTS;
		String value = super.getString(name);

		if (value != null && value.trim().startsWith("<")) {
			// new xml format
			return ReplacementSerializer.deserialize(value);
		}
		else {
			// old format
			return oldGetReplacements();
		}
	}

	/**
	 * Get replacements (old format).
	 *
	 * @return the replacements
	 */
	private Replacement[] oldGetReplacements() {
		String name = Constants.REPLACEMENTS;
		String keyString = super.getString(name);
		List<String> keyList = getListFromString(keyString);

		Replacement[] result = new Replacement[keyList.size()];
		for (int i = 0; i < keyList.size(); ++i) {
			String key = keyList.get(i);
			String val = super.getString(name + "." + key);
			if (val != null) {
				int scope = key.startsWith(Constants.FIELDS + ".") ?
						Replacement.SCOPE_FIELD : Replacement.SCOPE_METHOD;
				result[i] = new Replacement(StringUtils.getLastElement(key, '.'), val, scope);
			}
		}

		return result;
	}

	/**
	 * Set replacements.
	 *
	 * @param prs the new replacements
	 */
	public void setReplacements(Replacement[] prs) {
		String name = Constants.REPLACEMENTS;
		String value = ReplacementSerializer.serialize(prs);
		super.setValue(name, value);
	}

	/**
	 * Get the default replacements (old format for compatibility).
	 *
	 * @return the prefix replacements
	 */
	public Replacement[] getDefaultReplacements() {
		String name = Constants.REPLACEMENTS;
		String keyString = super.getDefaultString(name);
		List<String> keyList = getListFromString(keyString);

		Replacement[] result = new Replacement[keyList.size()];
		for (int i = 0; i < keyList.size(); ++i) {
			String key = name + "." + keyList.get(i);
			String val = super.getDefaultString(key);
			if (val != null) {
				int scope = key.startsWith(Constants.FIELDS + ".") ?
						Replacement.SCOPE_FIELD : Replacement.SCOPE_METHOD;
				result[i] = new Replacement(StringUtils.getLastElement(key, '.'), val, scope);
			}
		}

		return result;
	}

	/**
	 * Set the default replacements (old format for compatibility).
	 *
	 * @param prs the default replacements
	 */
	public void setDefaultReplacements(Replacement[] prs) {
		String name = Constants.REPLACEMENTS;
		StringBuffer keyString = new StringBuffer();

		for (int i = 0; i < prs.length; ++i) {
			Replacement pr = prs[i];
			if (pr.getScope() == Replacement.SCOPE_FIELD) {
				keyString.append(Constants.FIELDS + ".");
			}
			keyString.append(pr.getShortcut());
			keyString.append(",");
			String key = pr.getScope() == Replacement.SCOPE_FIELD ?
					name + "." + Constants.FIELDS : name;
			super.setDefault(key + "." + pr.getShortcut(), pr.getReplacement());
		}
		super.setDefault(name, keyString.toString());
	}

	public List<String> getTagOrder() {
	    if (tagOrder == null) {
	        tagOrder = new ArrayList<String>(Arrays.asList(super.getString(Constants.TAG_ORDER).split(",")));
	    }
	    return Collections.unmodifiableList(tagOrder);
    }

    public void setTagOrder(final List<String> newTagOrder) {
        tagOrder.clear();
        tagOrder.addAll(newTagOrder);

        final StringBuilder tagOrderString = new StringBuilder();
        for (final String tag : tagOrder) {
            if (tagOrderString.length() > 0) {
                tagOrderString.append(',');
            }
            tagOrderString.append(tag);
        }
        super.setValue(Constants.TAG_ORDER, tagOrderString.toString());
    }

    public Set<GetSetFromFieldReplacement> getDefaultGetSetFromFieldReplacements() {
        return Collections.unmodifiableSet(getGetSetFromFieldReplacements(Constants.DEFAULT_GET_SET_FROM_FIELD_REPLACEMENTS));
    }

    public Set<GetSetFromFieldReplacement> getGetSetFromFieldReplacements() {
        if (getSetFromFieldReplacements == null) {
            getSetFromFieldReplacements = getGetSetFromFieldReplacements(
                    super.getString(Constants.GET_SET_FROM_FIELD_REPLACEMENTS));
        }
        return Collections.unmodifiableSet(getSetFromFieldReplacements);
    }

    public void setGetSetFromFieldReplacements(Set<GetSetFromFieldReplacement> getSetFromFieldReplacements) {
        this.getSetFromFieldReplacements.clear();
        this.getSetFromFieldReplacements.addAll(getSetFromFieldReplacements);

        final StringBuilder replacementsString = new StringBuilder();
        for (final GetSetFromFieldReplacement replacement : getSetFromFieldReplacements) {
            if (replacementsString.length() > 0) {
                replacementsString.append(',');
            }
            replacementsString.append(replacement.toString());
        }
        super.setValue(Constants.GET_SET_FROM_FIELD_REPLACEMENTS, replacementsString.toString());
    }

    private Set<GetSetFromFieldReplacement> getGetSetFromFieldReplacements(final String replacementsString) {
        final Set<GetSetFromFieldReplacement> replacementsSet = new TreeSet<GetSetFromFieldReplacement>();

        for (final String string : replacementsString.split(",")) {
            replacementsSet.add(GetSetFromFieldReplacement.fromString(string));
        }
        return replacementsSet;
    }

    public Map<String, String> getProperties() {
		if (properties != null) {
			return Collections.unmodifiableMap(properties);
		}

		final String baseKey = Constants.PROPERTIES;
		final String keyString = super.getString(baseKey);
		final List<String> keyList = getListFromString(keyString);

		properties = new HashMap<String, String>();
		for (int i = 0; i < keyList.size(); ++i) {
		    final String key = (String)keyList.get(i);
		    final String val = super.getString(baseKey + "." + key);
			if (val != null) {
				properties.put(key, val);
			}
		}
		return Collections.unmodifiableMap(properties);
	}

	public void setProperties(final Map<String, String> newProperties) {
		properties.clear();
		properties.putAll(newProperties);

		final String baseKey = Constants.PROPERTIES;
		final StringBuilder keyString = new StringBuilder();

		final Iterator<String> names = newProperties.keySet().iterator();
		while (names.hasNext()) {
			String name = names.next();
			keyString.append(name);
			keyString.append(",");
			super.setValue(baseKey + "." + name, (String)newProperties.get(name));
		}
		super.setValue(baseKey, keyString.toString());
	}

    private List<String> getListFromString(String stringList) {
        List<String> result = new ArrayList<String>();
        if(stringList == null || stringList.trim().equals("")) {
            return result;
        }

        StringTokenizer tokens = new StringTokenizer(stringList, ",");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim();
            if(!token.equals(""))
                result.add(token);
        }

        return result;
    }
}
