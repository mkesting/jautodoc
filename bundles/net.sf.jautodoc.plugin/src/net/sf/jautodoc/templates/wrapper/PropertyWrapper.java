/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.wrapper;

import java.util.Map;


/**
 * Wrapper class for Properties.
 */
public class PropertyWrapper {
	private String key;
	private Map<String, String> properties;
	
	
	/**
	 * Instantiates a new property wrapper.
	 */
	public PropertyWrapper() {
		this(null, null);
	}
	
	/**
	 * Instantiates a new property wrapper.
	 * 
	 * @param key the key
	 * @param properties the properties to use
	 */
	public PropertyWrapper(String key, Map<String, String> properties) {
		this.key = key;
		this.properties = properties;
	}
	
	/**
	 * Gets the property value.
	 * 
	 * @param key the key
	 * 
	 * @return the value
	 */
	public String get(String key) {
		return key == null ? "${missing_property_key}" :
			get(key, "${missing_property:" + key + "}");
	}
	
	/**
	 * Gets the property value.
	 * 
	 * @param key the key
	 * @param defaultValue the default value
	 * 
	 * @return the value
	 */
	public String get(String key, String defaultValue) {
		String val = properties != null ? (String)properties.get(key) : null;
		if (val != null) {
			return val;
		}
		
		// check system properties
		return System.getProperty(key, defaultValue);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return get(key).toString();
	}
}
