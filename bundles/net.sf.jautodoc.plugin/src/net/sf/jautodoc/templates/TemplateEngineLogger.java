/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.jautodoc.JAutodocPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


public abstract class TemplateEngineLogger {
	private static Set<LogListener> logListener = new HashSet<LogListener>();
	
	public static void addLogListener(LogListener listener) {
		logListener.add(listener);
	}
	
	public static void removeLogListener(LogListener listener) {
		logListener.remove(listener);
	}
	
	protected void logMessage(LogEntry logEntry) {
		if (logListener.isEmpty()) {
			writeErrorLog(logEntry);
			return;
		}
		
		notifyLogListener(logEntry);
	}
	
	private void notifyLogListener(LogEntry logEntry) {
		if (logEntry.getSeverity() < IStatus.INFO) return;
		
		Iterator<LogListener> iter = logListener.iterator();
		while (iter.hasNext()) {
			LogListener listener = iter.next();
			listener.messageLogged(logEntry);
		}
	}
	
	private void writeErrorLog(LogEntry logEntry) {
		if (logEntry.getSeverity() < IStatus.WARNING) return;
		
		Status status = new Status(
				logEntry.getSeverity(),
				JAutodocPlugin.PLUGIN_ID,
				0,
				"Template: " + logEntry.getMessage(),
				logEntry.getThrowable());

		JAutodocPlugin.getDefault().getLog().log(status);
	}
}
