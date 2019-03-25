/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jautodoc.templates.LogEntry;
import net.sf.jautodoc.templates.TemplateEngineLogger;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.eclipse.core.runtime.IStatus;


/**
 * Class for getting Velocity log messages.
 */
public class VelocityLogger extends TemplateEngineLogger implements LogChute {
	
	/** Pattern for location informations in log messages. */
	private static final Pattern PATTERN = Pattern
			.compile("line\\s+(\\d+).+column\\s+(\\d+)");
	
	private int level;
	private LogEntry logEntry;
	

	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.log.LogChute#init(org.apache.velocity.runtime.RuntimeServices)
	 */
	public void init(RuntimeServices rs) throws Exception {
		level = rs.getInt("runtime.log.logsystem.jautodoc.level", WARN_ID);
		logEntry = new LogEntry();
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.log.LogChute#isLevelEnabled(int)
	 */
	public boolean isLevelEnabled(int level) {
		return this.level <= level;
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.log.LogChute#log(int, java.lang.String)
	 */
	public void log(int level, String message) {
		log(level, message, null);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.log.LogChute#log(int, java.lang.String, java.lang.Throwable)
	 */
	public void log(int level, String message, Throwable throwable) {
		if (isLevelEnabled(level)) {
			parseLocation(message, logEntry);
			logEntry.setSeverity(getSeverity(level));
			logEntry.setMessage(message);
			logEntry.setThrowable(throwable);
			logMessage(logEntry);
		}
	}
	
	/**
	 * Translates the given log level to IStatus severity.
	 * 
	 * @param level the level
	 * 
	 * @return the severity
	 */
	private int getSeverity(int level) {
		return    (level == ERROR_ID ? IStatus.ERROR
				: (level == WARN_ID ? IStatus.WARNING
				: (level == INFO_ID ? IStatus.INFO : 0)));
	}
	
	/**
	 * Parses the log message for location informations and adds
	 * these informations to the given log entry.
	 * 
	 * @param message the message
	 * @param logEntry the log entry
	 */
	private void parseLocation(String message, LogEntry logEntry) {
		Matcher matcher = PATTERN.matcher(message);
		if (matcher.find()) {
			logEntry.setLine  (Integer.parseInt(matcher.group(1)) - 1);
			logEntry.setColumn(Integer.parseInt(matcher.group(2)) - 1);
		}
		else {
			logEntry.setLine(-1);
			logEntry.setColumn(-1);
		}
	}
}
