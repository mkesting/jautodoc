/*******************************************************************
 * Copyright (c) 2006 - 2007, Martin Kesting, All rights reserved.
 * 
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.velocity.log;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;


/**
 * Proxy class for the velocity log system which delegates all method
 * calls to a given log chute. It is used to catch velocity log messages
 * from outside of this plugin.
 */
public class VelocityLogChuteProxy implements LogChute {
	private static LogChute delegate;
	
	
	/**
	 * Sets the log chute.
	 * 
	 * @param logChute the log chute
	 */
	public static void setLogChute(LogChute logChute) {
		delegate = logChute;
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.log.LogChute#init(org.apache.velocity.runtime.RuntimeServices)
	 */
	public void init(RuntimeServices rs) throws Exception {
		delegate.init(rs);
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.log.LogChute#isLevelEnabled(int)
	 */
	public boolean isLevelEnabled(int level) {
		return delegate.isLevelEnabled(level);
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.log.LogChute#log(int, java.lang.String)
	 */
	public void log(int level, String message) {
		delegate.log(level, message);

	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.runtime.log.LogChute#log(int, java.lang.String, java.lang.Throwable)
	 */
	public void log(int level, String message, Throwable throwable) {
		delegate.log(level, message, throwable);
	}
}
