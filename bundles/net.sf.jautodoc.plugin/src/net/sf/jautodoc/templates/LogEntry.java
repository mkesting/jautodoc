/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates;


/**
 * Log entry delivered by the template engine logger.
 */
public class LogEntry {
	private int line;
	private int column;
	private int severity;
	private String message;
	private Throwable throwable;
	
	
	/**
	 * Gets the column where the error occured.
	 * 
	 * @return the column
	 */
	public int getColumn() {
		return column;
	}
	
	/**
	 * Sets the column where the error occured.
	 * 
	 * @param column the new column
	 */
	public void setColumn(int column) {
		this.column = column;
	}
	
	/**
	 * Gets the line where the error occured.
	 * 
	 * @return the line
	 */
	public int getLine() {
		return line;
	}
	
	/**
	 * Sets the line where the error occured.
	 * 
	 * @param line the new line
	 */
	public void setLine(int line) {
		this.line = line;
	}
	
	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the message.
	 * 
	 * @param message the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Gets the severity of the message.
	 * 
	 * @return the severity
	 */
	public int getSeverity() {
		return severity;
	}
	
	/**
	 * Sets the severity of the message.
	 * 
	 * @param severity the new severity
	 */
	public void setSeverity(int severity) {
		this.severity = severity;
	}
	
	/**
	 * Gets the throwable.
	 * 
	 * @return the throwable
	 */
	public Throwable getThrowable() {
		return throwable;
	}
	
	/**
	 * Sets the throwable.
	 * 
	 * @param throwable the new throwable
	 */
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
}
