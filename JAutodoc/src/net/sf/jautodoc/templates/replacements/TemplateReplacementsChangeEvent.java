/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.replacements;

import java.util.Collection;


/**
 * Indicates a template replacements change event.
 */
public class TemplateReplacementsChangeEvent {
	private Collection<String> replacements;

	/**
	 * Instantiates a new template replacements change event.
	 * 
	 * @param replacements the replacements
	 */
	public TemplateReplacementsChangeEvent(Collection<String> replacements) {
		this.replacements = replacements;
	}

	/**
	 * Gets a list of replacement strings for references in the template.
	 * 
	 * @return the template replacements
	 */
	public Collection<String> getReplacements() {
		return replacements;
	}
}
