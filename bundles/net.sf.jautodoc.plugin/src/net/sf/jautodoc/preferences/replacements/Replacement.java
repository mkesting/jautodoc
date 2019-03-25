/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences.replacements;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import net.sf.jautodoc.preferences.Constants;
import net.sf.jautodoc.preferences.ITableEntry;


/**
 * Replacement bean.
 */
public class Replacement implements ITableEntry, Comparable<Replacement> {
	
	public static final int SCOPE_METHOD = 1;
	public static final int SCOPE_FIELD  = 2;
	public static final int SCOPE_BOTH	 = 3;
	
	public static final int MODE_PREFIX = 0;
	public static final int MODE_ALL	= 1;
	
	private String shortcut;
	private String replacement;
	
	private int scope;
	private int mode;
	
	
	/**
	 * Instantiates a new replacement.
	 */
	public Replacement() {
		this("", "", SCOPE_METHOD, MODE_PREFIX);
	}
	
	/**
	 * Instantiates a new replacement.
	 * 
	 * @param shortcut the shortcut
	 * @param replacement the replacement
	 */
	public Replacement(String shortcut, String replacement) {
		this(shortcut, replacement, SCOPE_METHOD, MODE_PREFIX);
	}
	
	/**
	 * Instantiates a new replacement.
	 * 
	 * @param shortcut the shortcut
	 * @param replacement the replacement
	 * @param scope the scope
	 */
	public Replacement(String shortcut, String replacement, int scope) {
		this(shortcut, replacement, scope, MODE_PREFIX);
	}
	
	/**
	 * Instantiates a new replacement.
	 * 
	 * @param shortcut the shortcut
	 * @param replacement the replacement
	 * @param scope the scope
	 * @param mode the mode
	 */
	public Replacement(String shortcut, String replacement, int scope, int mode) {
		this.shortcut = shortcut;
		this.replacement = replacement;
		this.scope = scope;
		this.mode = mode;
	}
	
	/**
	 * Gets the shortcut.
	 * 
	 * @return the shortcut
	 */
	@XmlAttribute
	public String getShortcut() {
		return shortcut;
	}
	
	/**
	 * Sets the shortcut.
	 * 
	 * @param shortcut the shortcut
	 */
	public void setShortcut(String prefix) {
		this.shortcut = prefix;
	}
	
	/**
	 * Gets the replacement.
	 * 
	 * @return the replacement
	 */
	@XmlValue
	public String getReplacement() {
		return replacement;
	}
	
	/**
	 * Sets the replacement.
	 * 
	 * @param replacement the replacement
	 */
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	/**
	 * Gets the scope.
	 * 
	 * @return the scope
	 */
	@XmlAttribute
	public int getScope() {
		return scope;
	}

	/**
	 * Sets the scope.
	 * 
	 * @param scope the scope
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}
	
	/**
	 * Gets the mode.
	 * 
	 * @return the mode
	 */
	@XmlAttribute
	public int getMode() {
		return mode;
	}

	/**
	 * Sets the mode.
	 * 
	 * @param mode the new mode
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * Checks if is field replacement.
	 * 
	 * @return true, if is field replacement
	 */
	public boolean isFieldReplacement() {
		return (scope & SCOPE_FIELD) != 0;
	}
	
	/**
	 * Checks if is method replacement.
	 * 
	 * @return true, if is method replacement
	 */
	public boolean isMethodReplacement() {
		return (scope & SCOPE_METHOD) != 0;
	}
	
	/**
	 * Gets the scope label.
	 * 
	 * @return the scope label
	 */
	public String getScopeLabel() {
		if (isFieldReplacement() && isMethodReplacement()) {
			return Constants.COLUMN_SCOPE_BOTH;
		}
		
		return isFieldReplacement() ?
    			Constants.COLUMN_SCOPE_FIELD : Constants.COLUMN_SCOPE_METHOD;
	}
	
	/**
	 * Gets the mode label.
	 * 
	 * @return the mode label
	 */
	public String getModeLabel() {
		return mode == Replacement.MODE_ALL ?
    			Constants.COLUMN_MODE_ALL : Constants.COLUMN_MODE_PREFIX;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.preferences.ITableEntry#getColumnText(int)
	 */
	public String getColumnText(int columnIndex) {
		switch (columnIndex) {
        case 0:
            return getShortcut();
        case 1:
        	return getReplacement();
        case 2:
        	return getScopeLabel();
        case 3:
        	return getModeLabel();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Replacement other) {
		int result = getShortcut().compareTo(other.getShortcut());
		if (result == 0) {
			result = getScope() - other.getScope();
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Replacement)) return false;
		
		Replacement other = (Replacement)o;
		return getScope() == other.getScope()
				&& getShortcut().equalsIgnoreCase(other.getShortcut());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getShortcut().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getShortcut() + " -> " + getReplacement() + " ("
				+ getScopeLabel() + ", " + getModeLabel() + ")";
	}
}
