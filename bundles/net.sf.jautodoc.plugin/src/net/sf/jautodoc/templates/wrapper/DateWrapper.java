/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.wrapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Date wrapper class.
 */
public class DateWrapper {
	public static final DateFormat DATE	= DateFormat.getDateInstance(DateFormat.MEDIUM);
	public static final DateFormat TIME	= DateFormat.getTimeInstance(DateFormat.MEDIUM);
	public static final SimpleDateFormat YEAR	= new SimpleDateFormat("yyyy");
	
	private DateFormat formatter;
	
	/**
	 * Instantiates a new date wrapper.
	 * 
	 * @param format the format
	 */
	public DateWrapper(DateFormat format) {
		this.formatter = format;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return formatter.format(new Date());
	}
}
