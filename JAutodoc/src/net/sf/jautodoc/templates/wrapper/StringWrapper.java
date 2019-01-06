/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.wrapper;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.Configuration;
import net.sf.jautodoc.preferences.ConfigurationManager;
import net.sf.jautodoc.source.CommentManager;
import net.sf.jautodoc.templates.ITemplateKinds;
import net.sf.jautodoc.utils.StringUtils;


/**
 * Wrapper for native string class.
 */
public class StringWrapper {
	private String string;
	private int templateKind;
	
	/**
	 * Instantiates a new string wrapper.
	 */
	public StringWrapper() {
		this("", ITemplateKinds.UNKNOWN);
	}
	
	/**
	 * Instantiates a new string wrapper.
	 * 
	 * @param string the wrapped string
	 * @param templateKind the template kind
	 */
	public StringWrapper(String string, int templateKind) {
		this.string = string != null ? string : "";
		this.templateKind = templateKind;
	}
	
	/**
	 * Converts first char to lower.
	 * 
	 * @return the modified string
	 */
	public StringWrapper firstToLower() {
		// only, if it does not start with two upper case letters
		if (string.length() > 1 && Character.isUpperCase(string.charAt(1))) {
			return this;
		}
		return new StringWrapper(StringUtils.firstToLower(string), templateKind);
	}
	
	/**
	 * Converts first char to upper.
	 * 
	 * @return the modified string
	 */
	public StringWrapper firstToUpper() {
		return new StringWrapper(StringUtils.firstToUpper(string), templateKind);
	}
	
	/**
	 * Converts string to upper.
	 * 
	 * @return the upper case string
	 */
	public StringWrapper toUpper() {
		return new StringWrapper(string.toUpperCase(), templateKind);
	}
	
	/**
	 * Converts string to lower.
	 * 
	 * @return the lower case string
	 */
	public StringWrapper toLower() {
		return new StringWrapper(string.toLowerCase(), templateKind);
	}
	
	/**
	 * Split string where characters change from lower to upper case.
	 * All characters will be changed to lower case, despite of the
	 * first and sequences with more then one upper case letter.
	 * Example: getIDFromProdukt => get ID from produkt
	 * 
	 * @return the modified string
	 */
	public StringWrapper split() {
		String comment = CommentManager.createComment(getConfig(), string,
				getScope(), true, false);
		return new StringWrapper(comment, templateKind);
	}
	
	/**
	 * Replace keywords.
	 * 
	 * @return the modified string
	 */
	public StringWrapper replace() {
		String comment = CommentManager.createComment(getConfig(), string,
				getScope(), false, true);
		return new StringWrapper(comment, templateKind);
	}
	
	/**
	 * Get char at the given index.
	 * 
	 * @param index the index
	 * 
	 * @return the character wrapper
	 */
	public CharacterWrapper charAt(int index) {
		try {
			return new CharacterWrapper(string.charAt(index));
		} catch (IndexOutOfBoundsException e) {
			JAutodocPlugin.getDefault().handleException(e);
		}
		
		return new CharacterWrapper(' ');
	}
	
	/**
	 * Returns the length of this string.
	 * 
	 * @return the length
	 */
	public int length() {
		return string.length();
	}
	
	private int getScope() {
		if (templateKind == ITemplateKinds.TYPE) {
			return CommentManager.TYPE;
		}
		else if (templateKind == ITemplateKinds.FIELD) {
			return CommentManager.FIELD;
		}
		else if (templateKind == ITemplateKinds.METHOD) {
			return CommentManager.METHOD;
		}
		else if (templateKind == ITemplateKinds.PARAMETER) {
			return CommentManager.PARAMETER;
		}
		else if (templateKind == ITemplateKinds.EXCEPTION) {
			return CommentManager.EXCEPTION;
		}
		else {
			return CommentManager.NONE;
		}
	}
	
	private Configuration getConfig() {
		return ConfigurationManager.getCurrentConfiguration();
	}
	
	// shortcuts
	
	/**
	 * Converts first char to lower.
	 * 
	 * @return the modified string
	 */
	public StringWrapper fl() {
		return firstToLower();
	}
	
	/**
	 * Converts first char to uper.
	 * 
	 * @return the modified string
	 */
	public StringWrapper fu() {
		return firstToUpper();
	}
	
	/**
	 * Converts string to upper.
	 * 
	 * @return the upper case string
	 */
	public StringWrapper u() {
		return toUpper();
	}
	
	/**
	 * Converts string to lower.
	 * 
	 * @return the lower case string
	 */
	public StringWrapper l() {
		return toLower();
	}
	
	/**
	 * Split string, where characters change from lower to upper case.
	 * All characters will be changed to lower case, despite of the
	 * first and sequences with more then one upper case letter.
	 * Example: getIDFromProdukt => get ID from produkt
	 * 
	 * @return the modified string
	 */
	public StringWrapper s() {
		return split();
	}
	
	/**
	 * Split, first to upper.
	 * 
	 * @return the modified string
	 */
	public StringWrapper sfu() {
		String comment = CommentManager.createComment(getConfig(), string,
				getScope(), true, false, CommentManager.FIRST_TO_UPPER);
		return new StringWrapper(comment, templateKind);
	}
	
	/**
	 * Split, first to lower.
	 * 
	 * @return the modified string
	 */
	public StringWrapper sfl() {
		String comment = CommentManager.createComment(getConfig(), string,
				getScope(), true, false, CommentManager.FIRST_TO_LOWER);
		return new StringWrapper(comment, templateKind);
	}
	
	/**
	 * Replace keywords.
	 * 
	 * @return the modified string
	 */
	public StringWrapper r() {
		return replace();
	}
	
	/**
	 * Replace keywords, split string.
	 * 
	 * @return the modified string
	 */
	public StringWrapper rs() {
		String comment = CommentManager.createComment(getConfig(), string,
				getScope(), true, true);
		return new StringWrapper(comment, templateKind);
	}
	
	/**
	 * Replace keywords, split string, first to upper.
	 * 
	 * @return the modified string
	 */
	public StringWrapper rsfu() {
		String comment = CommentManager.createComment(getConfig(), string,
				getScope(), true, true, CommentManager.FIRST_TO_UPPER);
		return new StringWrapper(comment, templateKind);
	}
	
	/**
	 * Replace keywords, split string, first to lower.
	 * 
	 * @return the modified string
	 */
	public StringWrapper rsfl() {
		String comment = CommentManager.createComment(getConfig(), string,
				getScope(), true, true, CommentManager.FIRST_TO_LOWER);
		return new StringWrapper(comment, templateKind);
	}

	// --------------------------------
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return string;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return string.equals(obj.toString());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return string.hashCode();
	}
}
