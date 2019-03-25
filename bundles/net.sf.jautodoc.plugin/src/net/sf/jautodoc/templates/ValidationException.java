/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates;


/**
 * Template validation exception.
 */
public class ValidationException extends Exception {
    private static final long serialVersionUID = 1L;
    
    private int line;
	private int column;
	
	/**
	 * Instantiates a new validation exception.
	 * 
	 * @param cause the cause
	 */
	public ValidationException(Throwable cause) {
		this(cause, -1, -1);
	}
	
	/**
	 * Instantiates a new validation exception.
	 * 
	 * @param cause the cause
	 * @param line the line where the error was detected
	 * @param column the column where the error was detected
	 */
	public ValidationException(Throwable cause, int line, int column) {
		super(cause);
		this.line = line;
		this.column = column;
	}

	/**
	 * Gets the line where the error was detected.
	 * 
	 * @return the line where the error was detected
	 */
	public int getLine() {
		return line;
	}
	
	/**
	 * Gets the column where the error was detected.
	 * 
	 * @return the column where the error was detected
	 */
	public int getColumn() {
		return column;
	}
}
