/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.replacements;


/**
 * Listener for template replacements change events.
 */
public interface ITemplateReplacementsListener {
	
	/**
	 * Template replacements change occured.
	 * 
	 * @param e the event
	 */
	public void templateReplacementsChange(TemplateReplacementsChangeEvent e);
}
