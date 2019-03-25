/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates;


/**
 * The listener interface for receiving log events. The class that is interested
 * in processing a log event implements this interface, and the object created
 * with that class is registered with a component using the component's
 * <code>addLogListener<code> method. When the log event occurs, that object's
 * messageLogged method is invoked.
 */
public interface LogListener {
	
	/**
	 * Called when a message is logged.
	 * 
	 * @param logEntry the log entry
	 */
	public void messageLogged(LogEntry logEntry);
}
