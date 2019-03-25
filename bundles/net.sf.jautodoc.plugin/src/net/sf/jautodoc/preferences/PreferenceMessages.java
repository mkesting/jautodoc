/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * Class for getting preference messages.
 */
public class PreferenceMessages {
	
	private static final String BUNDLE_NAME =
		"net.sf.jautodoc.preferences.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE =
		ResourceBundle.getBundle(BUNDLE_NAME);
	
	private static final String DEFAULTS_BUNDLE_NAME =
		"net.sf.jautodoc.preferences.defaults"; //$NON-NLS-1$

	private static final ResourceBundle DEFAULTS_RESOURCE_BUNDLE =
		ResourceBundle.getBundle(DEFAULTS_BUNDLE_NAME);

	
	/**
	 * Get string for the given key.
	 * 
	 * @param key the key
	 * @return the string
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	/**
	 * Get the default value for the given key.
	 * 
	 * @param key the key
	 * @return the default value
	 */
	public static String getDefaultValue(String key) {
		try {
			return DEFAULTS_RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return null;
		}
	}
}
