/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;


/**
 * A class for table entries.
 */
public interface ITableEntry {
	
	/**
	 * Gets the column text for this entry.
	 * 
	 * @param columnIndex the column index
	 * 
	 * @return the column text
	 */
	public String getColumnText(int columnIndex);
}
