/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.wrapper;


/**
 * Wrapper class for characters.
 */
public class CharacterWrapper {
	char character;
	
	/**
	 * Instantiates a new character wrapper.
	 * 
	 * @param character the character
	 */
	public CharacterWrapper(char character) {
		this.character = character;
	}
	
	/**
	 * Checks if is upper case.
	 * 
	 * @return the boolean
	 */
	public boolean isUpperCase() {
		return Character.isUpperCase(character);
	}
	
	/**
	 * Checks if is lower case.
	 * 
	 * @return the boolean
	 */
	public boolean isLowerCase() {
		return Character.isLowerCase(character);
	}
	
	/**
	 * Checks if is letter.
	 * 
	 * @return the boolean
	 */
	public boolean isLetter() {
		return Character.isLetter(character);
	}
	
	/**
	 * Checks if is digit.
	 * 
	 * @return the boolean
	 */
	public boolean isDigit() {
		return Character.isDigit(character);
	}
	
	/**
	 * Checks if is letter or digit.
	 * 
	 * @return the boolean
	 */
	public boolean isLetterOrDigit() {
		return Character.isLetterOrDigit(character);
	}
	
	// --------------------------------
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.valueOf(character);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return toString().hashCode();
	}
}
